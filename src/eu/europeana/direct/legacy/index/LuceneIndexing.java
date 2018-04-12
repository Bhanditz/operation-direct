package eu.europeana.direct.legacy.index;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.legacy.mapping.AgentMapper;
import eu.europeana.direct.legacy.mapping.ConceptMapper;
import eu.europeana.direct.legacy.mapping.CulturalHeritageObjectSearchMapper;
import eu.europeana.direct.legacy.mapping.PlaceMapper;
import eu.europeana.direct.legacy.mapping.TimespanMapper;
import eu.europeana.direct.legacy.mapping.WebLinkMapper;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.logic.entities.AgentLogic;
import eu.europeana.direct.logic.entities.ConceptLogic;
import eu.europeana.direct.logic.entities.LocationLogic;
import eu.europeana.direct.logic.entities.TimespanLogic;
import eu.europeana.direct.logic.entities.WebLinkLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;

public class LuceneIndexing {

	final static Logger logger = Logger.getLogger(LuceneIndexing.class);

	private IndexManager indexManager = null;
	private CulturalHeritageObjectSearchMapper choSearchMapper = new CulturalHeritageObjectSearchMapper();	
	private AgentMapper agentSearchMapper = new AgentMapper();
	private ConceptMapper conceptSearchMapper = new ConceptMapper();
	private PlaceMapper placeSearchMapper = new PlaceMapper();
	private TimespanMapper timespanSearchMapper = new TimespanMapper();
	private WebLinkMapper weblinkSearchMapper = new WebLinkMapper();
	
	public IndexManager getIndexManager() {
		if (indexManager == null) {
			indexManager = IndexManager.getInstance();
		}
		return indexManager;
	}

	private LuceneIndexing() {
	}

	private static LuceneIndexing instance = null;

	/**
	 * 
	 * @return LuceneIndexing instance of LuceneIndexing
	 */
	public static final synchronized LuceneIndexing getInstance() {

		if (instance == null) {
			instance = new LuceneIndexing();
			new CulturalHeritageObjectSearchMapper().configureFacets(instance.getIndexManager().getConfig());
		}
		return instance;
	}
	
	/**
	 * commits main, entity and taxonomy index
	 */
	public void commitIndex(boolean forceMergeDeletes){
		try {			
			getIndexManager().commit(forceMergeDeletes);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
	

	/**
	 * commits other indexes (removed-records index)
	 */
	public void commitOthers(){
		try {			
			getIndexManager().commitOthers();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}	

	/**
	 * Deletes document from index
	 * 
	 * @param objectId Object database ID we want to delete from index
	 * @param documentType Lucene document type
	 * @throws IOException
	 */
	public void deleteFromIndex(long objectId, LuceneDocumentType documentType,boolean forceMergeDeletes, boolean commitIndex) throws IOException {
		if(LuceneDocumentType.CulturalHeritageObject.equals(documentType)){
			getIndexManager().deleteDocument(objectId,forceMergeDeletes,commitIndex);	
		}else{
			getIndexManager().deleteEntityDocument(objectId, documentType,forceMergeDeletes,commitIndex);
		}			
	}

	/**
	 * Adds document to index
	 * 
	 * @param id Object database ID we want to add to index
	 * @param documentType
	 */
	public void addLuceneDocument(CulturalHeritageObject cho, boolean commitIndex, boolean forceMergeDeletes) throws IOException{
		
		try {			

			// maps CHO Operation Direct API model to lucene document of
			// type culturalheritageobject (object_type)
			Document doc = choSearchMapper.mapFromSource(cho, new Date());
			getIndexManager().AddDocument(doc);	
			
			if(cho != null){
				if(cho.getAgents() != null && cho.getAgents().size() > 0){
					for (Agent a : cho.getAgents()) {
						// we're not commiting index here, it will commit at the end of the method (finally block)
						updateEntityDocument(a, false, null, LuceneDocumentType.Agent, cho.getId().longValueExact(), cho.getLanguageNonAwareFields().getDataOwner(),forceMergeDeletes);																
					}	
				}					
				
				if(cho.getConcepts() != null && cho.getConcepts().size() > 0){
					for(Concept c : cho.getConcepts()){
						// we're not commiting index here, it will commit at the end of the method (finally block)
						updateEntityDocument(c, false, null, LuceneDocumentType.Concept, cho.getId().longValueExact(), cho.getLanguageNonAwareFields().getDataOwner(),forceMergeDeletes);
					}	
				}					
				
				if(cho.getSpatial() != null && cho.getSpatial().size() > 0){
					for(Place p : cho.getSpatial()){
						// we're not commiting index here, it will commit at the end of the method (finally block)
						updateEntityDocument(p, false, null, LuceneDocumentType.Place, cho.getId().longValueExact(), cho.getLanguageNonAwareFields().getDataOwner(),forceMergeDeletes);
					}	
				}					
				
				if(cho.getTemporal() != null && cho.getTemporal().size() > 0){
					for(TimeSpan t : cho.getTemporal()){
						// we're not commiting index here, it will commit at the end of the method (finally block)
						updateEntityDocument(t, false, null, LuceneDocumentType.Timespan, cho.getId().longValueExact(), cho.getLanguageNonAwareFields().getDataOwner(),forceMergeDeletes);
					}
				}
				
				if(cho.getWebLinks() != null && cho.getWebLinks().size() > 0){
					for(WebLink wl : cho.getWebLinks()){
						// we're not commiting index here, it will commit at the end of the method (finally block)
						updateEntityDocument(wl, false, null, LuceneDocumentType.Weblink, cho.getId().longValueExact(), cho.getLanguageNonAwareFields().getDataOwner(),forceMergeDeletes);
					}
				}						
			}			
							
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if(commitIndex){
				getIndexManager().commit(forceMergeDeletes);	
			}		
		}
	}

	public void addRemovedRecordDocument(long choId) throws IOException{
		
		try{
			// created Lucene document for removed-records index
			Document doc = choSearchMapper.mapForRemovedRecords(choId);
			getIndexManager().AddRemovedRecordDocument(doc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {			
			getIndexManager().commitOthers();			
		}		
	}
	
	/**
	 * Updates document in index
	 * 
	 * @param id
	 *            Object database ID we want to update in index
	 * @param documentType
	 *            Lucene document type
	 * @param updatingRelated
	 *            Updating related only once (break recursion)
	 */						
	public void updateLuceneIndex(Object obj, boolean updatingRelated, boolean commitIndex, Date created, boolean forceMergeDeletes) throws IOException{
		try {

			CulturalHeritageObject cho = null;

			if (obj instanceof CulturalHeritageObject) {
				cho = (CulturalHeritageObject) obj;
			} else {
				CulturalHeritageObjectLogic choLogic = new CulturalHeritageObjectLogic();

				try {					
					long id = (long) obj;					
					eu.europeana.direct.backend.model.CulturalHeritageObject domainCho = choLogic.getCHODomain(id);
					created = domainCho.getEuropeanaDataObject().getCreated();
					cho = choLogic.mapFromDatabase(domainCho);
				} finally {
					choLogic.close();
				}
			}

			if (cho != null) {
				Document doc = choSearchMapper.mapFromSource(cho, created);
				long choId = 0;
				if (cho.getId() != null) {
					choId = cho.getId().longValueExact();
				}

				getIndexManager().updateDocument(choId, doc);
				
				// updating related entities that are indexed in entities index
				if (updatingRelated) {

					if(cho.getAgents() != null && cho.getAgents().size() > 0){
						for (Agent a : cho.getAgents()) {
							// we're not commiting index here, it will commit at the end of the method (finally block)
							updateEntityDocument(a, false, null, LuceneDocumentType.Agent, choId, cho.getLanguageNonAwareFields().getDataOwner(),forceMergeDeletes);																
						}	
					}					
					
					if(cho.getConcepts() != null && cho.getConcepts().size() > 0){
						for(Concept c : cho.getConcepts()){
							// we're not commiting index here, it will commit at the end of the method (finally block)
							updateEntityDocument(c, false, null, LuceneDocumentType.Concept, choId, cho.getLanguageNonAwareFields().getDataOwner(),forceMergeDeletes);
						}	
					}					
					
					if(cho.getSpatial() != null && cho.getSpatial().size() > 0){
						for(Place p : cho.getSpatial()){
							// we're not commiting index here, it will commit at the end of the method (finally block)
							updateEntityDocument(p, false, null, LuceneDocumentType.Place, choId, cho.getLanguageNonAwareFields().getDataOwner(),forceMergeDeletes);
						}	
					}					
					
					if(cho.getTemporal() != null && cho.getTemporal().size() > 0){
						for(TimeSpan t : cho.getTemporal()){
							// we're not commiting index here, it will commit at the end of the method (finally block)
							updateEntityDocument(t, false, null, LuceneDocumentType.Timespan, choId, cho.getLanguageNonAwareFields().getDataOwner(),forceMergeDeletes);
						}
					}
					
					if(cho.getWebLinks() != null && cho.getWebLinks().size() > 0){
						for(WebLink wl : cho.getWebLinks()){
							// we're not commiting index here, it will commit at the end of the method (finally block)
							updateEntityDocument(wl, false, null, LuceneDocumentType.Weblink, choId, cho.getLanguageNonAwareFields().getDataOwner(),forceMergeDeletes);
						}
					}					
				}								
			}					
						
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
			if(commitIndex){
				getIndexManager().commit(forceMergeDeletes);	
			}										
		}	
	}

	public void updateEntityDocument(Object entityObject, boolean commitIndex, Date created, LuceneDocumentType entityType, long choId, String dataOwner,boolean forceMergeDeletes) throws IOException{
		
		try{	
			if(entityType != null){
				switch (entityType) {
				case Agent:

					AgentLogic agentLogic = new AgentLogic();

					try {
						long id = 0;
						Agent directAgent = null;

						if (entityObject instanceof Agent) {
							directAgent = (Agent) entityObject;
							id = directAgent.getId().longValueExact();
						} else {
							id = (long) entityObject;
							directAgent = agentLogic.getAgent(id);
						}

						eu.europeana.direct.backend.model.Agent domainAgent = agentLogic.getAgentDb(id);

						Document agentDoc = agentSearchMapper.mapFromSource(directAgent,
								domainAgent.getEuropeanaDataObject().getCreated(),
								domainAgent.getEuropeanaDataObject().getEuropeanaDataObjectForLinkedObject());
						getIndexManager().updateEntityDocument(directAgent.getId().longValueExact(), agentDoc,
								entityType);

						// We must also update all CHO's index documents
						// that are related to this agent
						updateRelatedCHO(domainAgent.getEuropeanaDataObject().getEuropeanaDataObjectForLinkedObject(),
								choId, commitIndex,forceMergeDeletes);

					} finally {
						agentLogic.close();
					}

					break;
				case Concept:

					ConceptLogic conceptLogic = new ConceptLogic();

					try {

						Concept directConcept = (Concept) entityObject;
						eu.europeana.direct.backend.model.Concept domainConcept = conceptLogic
								.getConceptDb(directConcept.getId().longValueExact());

						Document conceptDoc = conceptSearchMapper.mapFromSource(directConcept,
								domainConcept.getEuropeanaDataObject().getCreated(),
								domainConcept.getEuropeanaDataObject().getEuropeanaDataObjectForLinkedObject());
						getIndexManager().updateEntityDocument(directConcept.getId().longValueExact(), conceptDoc,
								entityType);

						// We must also update all CHO's index documents
						// that are related to this concept
						updateRelatedCHO(domainConcept.getEuropeanaDataObject().getEuropeanaDataObjectForLinkedObject(),
								choId, commitIndex,forceMergeDeletes);

					} finally {
						conceptLogic.close();
					}

					break;
				case Timespan:

					TimespanLogic timespanLogic = new TimespanLogic();

					try {

						TimeSpan directTimespan = (TimeSpan) entityObject;
						eu.europeana.direct.backend.model.Timespan domainConcept = timespanLogic
								.getTimespanDb(directTimespan.getId().longValueExact());

						Document timespanDoc = timespanSearchMapper.mapFromSource(directTimespan,
								domainConcept.getEuropeanaDataObject().getCreated(),
								domainConcept.getEuropeanaDataObject().getEuropeanaDataObjectForLinkedObject());
						getIndexManager().updateEntityDocument(directTimespan.getId().longValueExact(), timespanDoc,
								entityType);

						// We must also update all CHO's index documents
						// that are related to this timespan
						updateRelatedCHO(domainConcept.getEuropeanaDataObject().getEuropeanaDataObjectForLinkedObject(),
								choId, commitIndex,forceMergeDeletes);

					} finally {
						timespanLogic.close();
					}

					break;
				case Place:

					LocationLogic locationLogic = new LocationLogic();

					try {

						Place directPlace = (Place) entityObject;
						eu.europeana.direct.backend.model.Place domainPlace = locationLogic
								.getPlaceDb(directPlace.getId().longValueExact());

						Document placedoc = placeSearchMapper.mapFromSource(directPlace,
								domainPlace.getEuropeanaDataObject().getCreated(),
								domainPlace.getEuropeanaDataObject().getEuropeanaDataObjectForLinkedObject());
						getIndexManager().updateEntityDocument(directPlace.getId().longValueExact(), placedoc,
								entityType);

						// We must also update all CHO's index documents
						// that are related to this timespan
						updateRelatedCHO(domainPlace.getEuropeanaDataObject().getEuropeanaDataObjectForLinkedObject(),
								choId, commitIndex,forceMergeDeletes);

					} finally {
						locationLogic.close();
					}

					break;
				case Weblink:

					WebLinkLogic weblinkLogic = new WebLinkLogic();

					try {

						WebLink wl = (WebLink) entityObject;
						eu.europeana.direct.backend.model.WebResource domainWeblink = weblinkLogic
								.getWeblinkDb(wl.getId().longValueExact());												
						
						Document weblinkDoc = weblinkSearchMapper.mapFromSource(wl, domainWeblink.getEuropeanaDataObject().getCreated(), String.valueOf(domainWeblink.getEuropeanaDataObject().getId()), domainWeblink.getEuropeanaDataObject().getCulturalHeritageObject().getLanguageNonAwareFields().getDataowner());

						getIndexManager().updateEntityDocument(wl.getId().longValueExact(), weblinkDoc, entityType);

					} finally {
						weblinkLogic.close();
					}

					break;
				}
			}				
		} finally {
			if(commitIndex){
				getIndexManager().commit(forceMergeDeletes);
			}
		}				
	}
	
	/**
	 * Fully optimizes index. Forces index merge on main and entities index
	 */
	public void fullOptimize() {		
		getIndexManager().fullOptimize();					
	}
	
	/**
	 * Updates CHO's that are related to contextual entity that we indexed
	 * @param relatedObjects Related CHO's
	 * @param currentChoId Current CHO id
	 * @param commitIndex 
	 * @throws IOException
	 */
	private void updateRelatedCHO(Set<EuropeanaDataObjectEuropeanaDataObject> relatedObjects, long currentChoId, boolean commitIndex, boolean forceMergeDeletes) throws IOException{
		// loop through CHO's that contain this contextual entity (agent,concept,...)
		for (EuropeanaDataObjectEuropeanaDataObject edoedo : relatedObjects) {
			// update all other CHO except the one with choId
			if (edoedo.getEuropeanaDataObjectByLinkingObjectId().getId() != currentChoId) {
				// update only cho, but not related entities
				updateLuceneIndex(edoedo.getEuropeanaDataObjectByLinkingObjectId().getId(), false,
						commitIndex, null,forceMergeDeletes);
			}
		}
	}
		
	
}
