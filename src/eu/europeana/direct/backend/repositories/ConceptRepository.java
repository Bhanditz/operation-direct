package eu.europeana.direct.backend.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.Concept;
import eu.europeana.direct.backend.model.EuropeanaDataObject;
import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;

/**
 * Data access class for Operation Direct Concept entity
 *
 */
public class ConceptRepository extends BaseRepository {
	final static Logger logger = Logger.getLogger(ConceptRepository.class);

	public ConceptRepository() {
		super();
	}

	public ConceptRepository(EntityManager manager) {
		super(manager);
	}	

	public Concept persist(Concept concept){
		manager.persist(concept);
		return concept;
	}
	
	/**
	 * Saves concept to database, if concept has id then updates concept.
	 * @param concept Concept database model
	 * @return long ID of saved Concept
	 */
	public long saveConcept(Concept concept) {
		boolean update = false;
		boolean localTrans = false;
		try {
			if (!manager.getTransaction().isActive()) {												
				manager.getTransaction().begin();		
				localTrans = true;
			}
			if (concept.getId() > 0) {
				concept = manager.merge(concept);
				manager.merge(concept);
				update = true;
			} else {
				manager.persist(concept);
			}
			if(localTrans){
				manager.getTransaction().commit();				
			}						
			return (long) concept.getId();
		} catch (Exception ex) {
			try {
				manager.getTransaction().rollback();
			} catch (Exception ex2) {
				// We are out of options - ignore
			}
			throw ex;
		}
	}
	
	
	/**
	 * 
	 * @param id Unique id for Concept
	 * @return Concept object
	 */
	public Concept getConcept(long id) {
		try {
			Concept c = manager.find(Concept.class, id);		
			return c;
		} catch (NoResultException nex) {
			logger.error(nex.getMessage(),nex);
			return null;
		}
	}
	
	
	/**
	 * 
	 * @param id Unique id for Concept
	 * @return True if deleted, false if not
	 */
	public boolean deleteConcept(long id){
		boolean localTrans = false;
		try{		
			
			// deletes EuropeanaDataObjectEuropeanaDataObject with concept id
			final Query deleteQuery = manager.createNamedQuery("EuropeanaDataObjectEuropeanaDataObject.findByLinkedObjectId");
			deleteQuery.setParameter("objectId",id);
			List<EuropeanaDataObjectEuropeanaDataObject> edo_edoList = (List<EuropeanaDataObjectEuropeanaDataObject>) deleteQuery.getResultList();
			for(EuropeanaDataObjectEuropeanaDataObject edo_edo : edo_edoList){
				manager.remove(edo_edo);
			}			
			if(!manager.getTransaction().isActive()){
				manager.getTransaction().begin();
				localTrans = true;
			}
			
			
			Concept concept = manager.find(Concept.class, id);
			if(concept != null){							
				manager.remove(concept);									
				try{
					LuceneIndexing.getInstance().deleteFromIndex(concept.getId(),LuceneDocumentType.Concept,true,true);
				}catch(Exception e){
					logger.error(e.getMessage(),e);		
				}
			}

			// deletes EuropeanaDataObject with concept id
			EuropeanaDataObject edo = manager.find(EuropeanaDataObject.class, id);
			if(edo != null){
				manager.remove(edo);
			}
			
			if(localTrans){
				manager.getTransaction().commit();	
			}			
						
			return true;
		}catch(Exception e){		
			logger.error("Error deleting Concept: "+e.getMessage(),e);
			manager.getTransaction().rollback();			
			return false;
		}
	}
	
}
