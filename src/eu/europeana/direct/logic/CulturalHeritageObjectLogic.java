package eu.europeana.direct.logic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.direct.backend.model.CulturalHeritageObjectLangAware;
import eu.europeana.direct.backend.model.CulturalHeritageObjectLangNonAware;
import eu.europeana.direct.backend.model.DeletedEntity;
import eu.europeana.direct.backend.model.EuropeanaDataObject;
import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.backend.model.EuropeanaDataObjectRoleType;
import eu.europeana.direct.backend.model.Timespan;
import eu.europeana.direct.backend.model.WebResource;
import eu.europeana.direct.backend.repositories.CulturalHeritageObjectRepository;
import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.logic.entities.AgentLogic;
import eu.europeana.direct.logic.entities.ConceptLogic;
import eu.europeana.direct.logic.entities.LocationLogic;
import eu.europeana.direct.logic.entities.TimespanLogic;
import eu.europeana.direct.logic.entities.WebLinkLogic;
import eu.europeana.direct.messaging.MessageProducer;
import eu.europeana.direct.messaging.MessageType;
import eu.europeana.direct.messaging.add.IndexMessage;
import eu.europeana.direct.messaging.delete.DeleteIndexMessage;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.KeyValuePair;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink.TypeEnum;

public class CulturalHeritageObjectLogic {
	
	private final CulturalHeritageObjectRepository repository = new CulturalHeritageObjectRepository();
	private final static Logger logger = Logger.getLogger(CulturalHeritageObjectLogic.class);	
	private String message;
	private List<DeletedEntity> deletedEntityIds = new ArrayList<DeletedEntity>();
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public CulturalHeritageObjectLogic(){
	}
	
	/**
	 * Maps database CHO model to Operation Direct CHO model
	 * 
	 * @param databaseCho
	 *            Database CHO model
	 */
 	public CulturalHeritageObject mapFromDatabase(eu.europeana.direct.backend.model.CulturalHeritageObject databaseCho)
			throws Exception {			
		
			AgentLogic agentLogic = new AgentLogic(repository.getManager());
			ConceptLogic conceptLogic = new ConceptLogic(repository.getManager());
			LocationLogic locationLogic = new LocationLogic(repository.getManager());
			TimespanLogic timespanLogic = new TimespanLogic(repository.getManager());

			CulturalHeritageObject choMapped = new CulturalHeritageObject();
			choMapped.setId(new BigDecimal(databaseCho.getId()));
			
			// list of agents linked to CHO
			List<Agent> linkedAgents = new ArrayList<Agent>();
			// list of concept linked to CHO
			List<Concept> linkedConcepts = new ArrayList<Concept>();
			// list of places linked to CHO
			List<Place> linkedPlaces = new ArrayList<Place>();
			// list of timespans linked to CHO
			List<TimeSpan> linkedTimespans = new ArrayList<TimeSpan>();

			CulturalHeritageObjectLanguageNonAware nonAwareFields = new CulturalHeritageObjectLanguageNonAware();
			choMapped.setLanguageNonAwareFields(nonAwareFields);

			
			// mapping non aware custom fields				
			if (databaseCho.getEuropeanaDataObject().getNonAwareCustomFields() != null && !databaseCho.getEuropeanaDataObject().getNonAwareCustomFields().isEmpty()) {
				for (Map.Entry<String, String> entry : databaseCho.getEuropeanaDataObject().getNonAwareCustomFields()
						.entrySet()) {
					if (!entry.getValue().toLowerCase().equals("service")
							&& !entry.getValue().toLowerCase().equals("isreferencedby")) {
						choMapped.getLanguageNonAwareFields().getCustomFields().add(new KeyValuePair(entry.getKey(), entry.getValue()));						
					}
				}
			}											
			
			if(databaseCho.getLanguageNonAwareFields() != null){								
				
				if(databaseCho.getLanguageNonAwareFields().getLanguageObject() != null && databaseCho.getLanguageNonAwareFields().getLanguageObject().length > 0) {
					nonAwareFields.setLanguageObject(databaseCho.getLanguageNonAwareFields().getLanguageObject());
				}
				
				if(databaseCho.getLanguageNonAwareFields().getObjectLanguage() != null) {					
					
					if(nonAwareFields.getLanguageObject() != null && nonAwareFields.getLanguageObject().length > 0) {
						List<String> list = Arrays.asList(nonAwareFields.getLanguageObject());
						list.add(databaseCho.getLanguageNonAwareFields().getObjectLanguage());
						String[] array = new String[list.size()];
						array = list.toArray(array);
						nonAwareFields.setLanguageObject(list.toArray(array));	
					} else {
						String[] arr = {databaseCho.getLanguageNonAwareFields().getObjectLanguage()};
						nonAwareFields.setLanguageObject(arr);
					}
					
				}
				
				if(databaseCho.getLanguageNonAwareFields().getRelation() != null){
					nonAwareFields.setRelation(Arrays.asList(databaseCho.getLanguageNonAwareFields().getRelation()));
				}
				
				if (databaseCho.getLanguageNonAwareFields().getIdentifier() != null) {
					nonAwareFields.setIdentifier(Arrays.asList(databaseCho.getLanguageNonAwareFields().getIdentifier()));
				}

				if (databaseCho.getLanguageNonAwareFields().getDataowner() != null) {
					nonAwareFields.setDataOwner(databaseCho.getLanguageNonAwareFields().getDataowner());
				}

				if (databaseCho.getLanguageNonAwareFields().getMediaType() != null) {
					switch (databaseCho.getLanguageNonAwareFields().getMediaType()) {

					case "IMAGE":
						choMapped.getLanguageNonAwareFields().setType(
								eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageNonAware.TypeEnum.IMAGE);
						break;
					case "VIDEO":
						choMapped.getLanguageNonAwareFields().setType(
								eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageNonAware.TypeEnum.VIDEO);
						break;
					case "AUDIO":
						choMapped.getLanguageNonAwareFields().setType(
								eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageNonAware.TypeEnum.AUDIO);
						break;
					case "_3D":
						choMapped.getLanguageNonAwareFields().setType(
								eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageNonAware.TypeEnum._3D);
						break;
					case "TEXT":
						choMapped.getLanguageNonAwareFields().setType(
								eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageNonAware.TypeEnum.TEXT);
						break;
					}
				}	
			}			

			// list of CHO LanguageAware objects
			List<CulturalHeritageObjectLanguageAware> listLangs = new ArrayList<CulturalHeritageObjectLanguageAware>();

			// mapping backend CHO translation object to Operation Direct CHO
			// language aware object
			if (databaseCho.getLanguageAwareFields() != null && !databaseCho.getLanguageAwareFields().isEmpty()) {
				for (Map.Entry<String, CulturalHeritageObjectLangAware> entry : databaseCho.getLanguageAwareFields().entrySet()) {

					CulturalHeritageObjectLanguageAware langAwareFields = new CulturalHeritageObjectLanguageAware();

					langAwareFields.setLanguage(entry.getKey());
					langAwareFields.setTitle(entry.getValue().getTitle());
					langAwareFields.setDescription(entry.getValue().getDescription());
					
					if (entry.getValue().getAlternative() != null) {
						langAwareFields.setAlternative(Arrays.asList(entry.getValue().getAlternative()));
					}

					if (entry.getValue().getCreated() != null && entry.getValue().getCreated().length > 0) {
						langAwareFields.setCreated(entry.getValue().getCreated()[0]);
					}

					if (entry.getValue().getExtent() != null) {
						langAwareFields.setExtent(Arrays.asList(entry.getValue().getExtent()));
					}

					if (entry.getValue().getFormat() != null) {
						langAwareFields.setFormat(Arrays.asList(entry.getValue().getFormat()));
					}

					if (entry.getValue().getIssued() != null && entry.getValue().getIssued().length > 0) {
						langAwareFields.setIssued(entry.getValue().getIssued()[0]);
					}					

					if (entry.getValue().getMedium() != null) {
						langAwareFields.setMedium(Arrays.asList(entry.getValue().getMedium()));
					}

					if (entry.getValue().getProvenance() != null) {
						langAwareFields.setProvenance(Arrays.asList(entry.getValue().getProvenance()));
					}

					if (entry.getValue().getPublisher() != null) {
						langAwareFields.setPublisher(Arrays.asList(entry.getValue().getPublisher()));
					}

					if (entry.getValue().getSource() != null) {
						langAwareFields.setSource(Arrays.asList(entry.getValue().getSource()));
					}

					// mapping lang aware custom fields
					if(!databaseCho.getEuropeanaDataObject().getAwareCustomFields().isEmpty()){					
						// check if custom fields exists for this language
						if(databaseCho.getEuropeanaDataObject().getAwareCustomFields().containsKey(entry.getKey())){
							// get map of custom fields for this language
							for (Map.Entry<String,String> customf : databaseCho.getEuropeanaDataObject().getAwareCustomFields().get(entry.getKey()).entrySet()) {
								langAwareFields.getCustomFields().add(new KeyValuePair(customf.getKey(), customf.getValue()));
							}
						}										
					}						

					listLangs.add(langAwareFields);
				}
			}
			choMapped.setLanguageAwareFields(listLangs);

			if (databaseCho.getEuropeanaDataObject() != null && databaseCho.getEuropeanaDataObject().getEuropeanaDataObjectForLinkingObject() != null) {

				// get objects that are related to CHO
				Set<EuropeanaDataObjectEuropeanaDataObject> edoLinkList = databaseCho.getEuropeanaDataObject().getEuropeanaDataObjectForLinkingObject();

				for (EuropeanaDataObjectEuropeanaDataObject edoLink : edoLinkList) {

					// maps object that is related to CHO from backend to Operation
					// Direct model and adds to list
					switch (edoLink.getRoleType()) {
					case Agent:
						eu.europeana.direct.backend.model.Agent dbAgent = edoLink.getEuropeanaDataObjectByLinkedObjectId()
								.getAgent();										
						
						if (dbAgent != null) {
							// maps backend Agent model to Operation Direct Agent
							// model and adds to list of related agents to CHO
							Agent a = agentLogic.mapFromDatabase(dbAgent);							
							if (edoLink.getRole() != null && edoLink.getRole().length() > 0) {
								a.getLanguageNonAwareFields().setRole(edoLink.getRole());
							}

							linkedAgents.add(a);
						}
						break;
					case Concept:
						eu.europeana.direct.backend.model.Concept dbConcept = edoLink
								.getEuropeanaDataObjectByLinkedObjectId().getConcept();
												
						if (dbConcept != null) {
							// maps backend Concept model to Operation Direct
							// Concept model and adds to list of related concepts to
							// CHO
							Concept c = conceptLogic.mapFromDatabase(dbConcept);
							if (edoLink.getRole() != null && edoLink.getRole().length() > 0) {
								c.getLanguageNonAwareFields().setRole(edoLink.getRole());
							}
							linkedConcepts.add(c);
						}
						break;
					case Location:
						eu.europeana.direct.backend.model.Place dbPlace = edoLink.getEuropeanaDataObjectByLinkedObjectId()
								.getPlace();												
						
						if (dbPlace != null) {
							// maps backend Place model to Operation Direct Place
							// model and adds to list of related places to CHO
							Place p = locationLogic.mapFromDatabase(dbPlace);
							if (edoLink.getRole() != null && edoLink.getRole().length() > 0) {
								p.getLanguageNonAwareFields().setRole(edoLink.getRole());
							}
							linkedPlaces.add(p);
						}
						break;
					case Timespan:						

						Timespan dbTimespan = edoLink.getEuropeanaDataObjectByLinkedObjectId().getTimespan();
						if (dbTimespan != null) {
							// maps backend Timespan model to Operation Direct
							// Timespan model and adds to list of related timespans
							// to CHO
							TimeSpan ts = timespanLogic.mapFromDatabase(dbTimespan);
							if (edoLink.getRole() != null && edoLink.getRole().length() > 0) {
								ts.getLanguageNonAwareFields().setRole(edoLink.getRole());
							}
							linkedTimespans.add(ts);
						}
						break;
					}
				}
			}

			// set agents that are linked to CHO
			choMapped.setAgents(linkedAgents);
			// set concepts that are linked to CHO
			choMapped.setConcepts(linkedConcepts);
			// set places that are linked to CHO
			choMapped.setSpatial(linkedPlaces);
			// set timespans that are linked to CHO
			choMapped.setTemporal(linkedTimespans);

			// weblinks that are related to CHO
			List<WebLink> links = new ArrayList<WebLink>();
			if (databaseCho.getEuropeanaDataObject().getWebResources() != null) {
				// maps list of related WebResources to Operation Direct WebLink
				for (WebResource resource : databaseCho.getEuropeanaDataObject().getWebResources()) {
					WebLink link = new WebLink();

					link.setId(new BigDecimal(resource.getId()));

					if (resource.getLink() != null) {
						link.setLink(resource.getLink());
					}
					if (resource.getOwner() != null) {
						link.setOwner(resource.getOwner());
					}
					if (resource.getRights() != null) {
						link.setRights(resource.getRights());
					}
					if (resource.getResourceType() != null) {
						link.setType(TypeEnum.values()[resource.getResourceType().ordinal()]);
					}
					
					if(resource.getCustomFields() != null && !resource.getCustomFields().isEmpty()) {						
						for (Map.Entry<String, String> entry : resource.getCustomFields().entrySet()) {																			
							link.getCustomFields().add(new KeyValuePair(entry.getKey(),entry.getValue()));
						}						
					}
					
					links.add(link);
				}
			}
			// set weblinks that are linked to CHO
			choMapped.setWebLinks(links);

			return choMapped;							
	}

	/**
	 * Maps Operation Direct CHO model to database CHO model
	 * 
	 * @param directChoModel
	 *            Operation Direct CHO model
	 */
	public eu.europeana.direct.backend.model.CulturalHeritageObject mapToDatabase(CulturalHeritageObject directChoModel)
			throws Exception {		
		
			AgentLogic agentLogic = new AgentLogic(repository.getManager());
			ConceptLogic conceptLogic = new ConceptLogic(repository.getManager());
			TimespanLogic timespanLogic = new TimespanLogic(repository.getManager());
			LocationLogic locationLogic = new LocationLogic(repository.getManager());
			WebLinkLogic weblinkLogic = new WebLinkLogic(repository.getManager());
			
			// domain cho lang non aware model
			CulturalHeritageObjectLangNonAware domainNonAware = new CulturalHeritageObjectLangNonAware();
			
			eu.europeana.direct.backend.model.CulturalHeritageObject choMapped;
			boolean update = false;
			
			if (directChoModel.getId() != null && directChoModel.getId().longValue() > 0) {				
				choMapped = repository.getCHO(directChoModel.getId().longValue());
				update = true;
				if (choMapped == null) {
					throw new Exception("CHO" +directChoModel.getId()+" does not exist!");
				}
				choMapped.getEuropeanaDataObject().setModified(new Date());
			} else {
				choMapped = new eu.europeana.direct.backend.model.CulturalHeritageObject();
				EuropeanaDataObject edoNew = new EuropeanaDataObject();
				edoNew.setCreated(new Date());
				edoNew.setModified(new Date());
				choMapped.setEuropeanaDataObject(edoNew);
			}

			CulturalHeritageObjectLanguageNonAware nonAwareFields = null;						
			if (directChoModel.getLanguageNonAwareFields() != null) {				
				nonAwareFields = directChoModel.getLanguageNonAwareFields();
			} else {
				throw new Exception("Cultural Heritage Object languageNonAwareFields is required");
			}

			List<CulturalHeritageObjectLanguageAware> listLangs = null;
			if (directChoModel.getLanguageAwareFields() != null) {
				if(directChoModel.getLanguageAwareFields().isEmpty()){
					throw new Exception("Cultural Heritage Object languageAwareFields is empty");
				}else{
					listLangs = directChoModel.getLanguageAwareFields();					
				}				
			} else {
				throw new Exception("Cultural Heritage Object languageAwareFields is required");
			}

			if (nonAwareFields.getIdentifier() != null) {
				domainNonAware.setIdentifier(
						nonAwareFields.getIdentifier().toArray(new String[nonAwareFields.getIdentifier().size()]));
			}

			if (nonAwareFields.getDataOwner() != null) {
				domainNonAware.setDataowner(nonAwareFields.getDataOwner());
			}

			if(nonAwareFields.getLanguageObject() != null && nonAwareFields.getLanguageObject().length > 0) {
				domainNonAware.setLanguageObject(nonAwareFields.getLanguageObject());				
			}
			
			// if CHO has no tpye which is required, cancel mapping
			if (nonAwareFields.getType() == null) {
				throw new Exception("Required field Type is missing for Cultural Heritage Object");
			} else {
				switch (nonAwareFields.getType()) {

				case IMAGE:
					domainNonAware.setMediaType("IMAGE");
					break;
				case VIDEO:
					domainNonAware.setMediaType("VIDEO");
					break;
				case AUDIO:
					domainNonAware.setMediaType("AUDIO");
					break;
				case _3D:
					domainNonAware.setMediaType("_3D");
					break;
				case TEXT:
					domainNonAware.setMediaType("TEXT");
					break;
				}
			}

			// set domain cho lang non aware object
			choMapped.setLanguageNonAwareFields(domainNonAware);
									
			if(directChoModel.getLanguageNonAwareFields().getCustomFields() != null){
				for(KeyValuePair kv : directChoModel.getLanguageNonAwareFields().getCustomFields()) {
					if(kv.getKey() != null) {
						choMapped.getEuropeanaDataObject().getNonAwareCustomFields().put(kv.getKey(), kv.getValue());	
					} else {
						throw new Exception("Key of one of the custom field for Cultural heritage object language non aware is null");	
					}					
				}
			}
			
			for (CulturalHeritageObjectLanguageAware langAwareFields : listLangs) {
				
				//domain cho lang aware model
				CulturalHeritageObjectLangAware domainLangAware = new CulturalHeritageObjectLangAware();
				
				String lang = null;

				if(langAwareFields.getLanguage() != null){
					lang = langAwareFields.getLanguage();
				}else{
					lang = "en";
				}
				// if CHO language aware object has no title, which is required,
				// cancel mapping current LanguageAware object
				if (langAwareFields.getTitle() == null) {
					throw new Exception("Required field title is missing for Cultural Heritage Object language aware!");
				} else {
					if(langAwareFields.getTitle().trim().length() > 0){
						domainLangAware.setTitle(langAwareFields.getTitle());	
					}else{
						throw new Exception("Cultural Heritage Object language aware title must have a value!");
					}					
				}

				if (langAwareFields.getDescription() != null && langAwareFields.getDescription().trim().length() > 0) {
					domainLangAware.setDescription(langAwareFields.getDescription());					
				}

				if (langAwareFields.getAlternative() != null) {
					domainLangAware.setAlternative((langAwareFields.getAlternative().toArray(new String[0])));
				}

				if (langAwareFields.getCreated() != null) {
					String[] prefLabel = new String[1];
					prefLabel[0] = langAwareFields.getCreated();
					domainLangAware.setCreated(prefLabel);
				}

				if (langAwareFields.getExtent() != null) {
					domainLangAware.setExtent((langAwareFields.getExtent().toArray(new String[0])));
				}

				if (langAwareFields.getFormat() != null) {
					domainLangAware.setFormat((langAwareFields.getFormat().toArray(new String[0])));
				}

				if (langAwareFields.getIssued() != null) {
					String[] prefLabel = new String[1];
					prefLabel[0] = langAwareFields.getIssued();
					domainLangAware.setIssued(prefLabel);
				}

				if (langAwareFields.getMedium() != null) {
					domainLangAware.setMedium((langAwareFields.getMedium().toArray(new String[0])));
				}

				if (langAwareFields.getProvenance() != null) {
					domainLangAware.setProvenance((langAwareFields.getProvenance().toArray(new String[0])));
				}

				if (langAwareFields.getPublisher() != null) {
					domainLangAware.setPublisher((langAwareFields.getPublisher().toArray(new String[0])));
				}

				if (langAwareFields.getSource() != null) {
					domainLangAware.setSource((langAwareFields.getSource().toArray(new String[0])));
				}

				// map lang aware custom fields
				if(langAwareFields.getCustomFields() != null && !langAwareFields.getCustomFields().isEmpty()){					
					Map<String,String> customfields = new HashMap<String,String>();
					
					for(KeyValuePair kv : langAwareFields.getCustomFields()){
						if(kv.getKey() != null){
							customfields.put(kv.getKey(), kv.getValue());	
						} else {
							throw new Exception("Key of one of the custom field for Cultural heritage object language aware is null");
						}						
					}
					if(!customfields.isEmpty()){
						choMapped.getEuropeanaDataObject().getAwareCustomFields().put(langAwareFields.getLanguage(), customfields);	
					}					
				}																
				
				//for every language add lang aware object
				choMapped.getLanguageAwareFields().put(lang, domainLangAware);								
			}			

			if(!update){					
				try{
					choMapped = repository.persistObject(choMapped);	
				} catch (Exception e) {
					throw e;
				}								
			}							
			
			if (update) {

				// list of ID's of related entities that won't be deleted because
				// we're just updating them
				List<Long> listIds = new ArrayList<Long>();

				// if CHO's API model agent contains ID means, we just update agent
				// and don't delete this agent
				if (directChoModel.getAgents() != null && directChoModel.getAgents().size() > 0) {
					for (Agent a : directChoModel.getAgents()) {
						if (a.getId() != null && a.getId().longValueExact() > 0) {
							listIds.add(a.getId().longValueExact());
						}
					}
				}

				// if CHO's API model concept contains ID means, we just update
				// concept
				// and don't delete this concept
				if (directChoModel.getConcepts() != null && directChoModel.getConcepts().size() > 0) {
					for (Concept c : directChoModel.getConcepts()) {
						if (c.getId() != null && c.getId().longValueExact() > 0) {
							listIds.add(c.getId().longValueExact());
						}
					}
				}

				// if CHO's API model place contains ID means, we just update place
				// and don't delete this place
				if (directChoModel.getSpatial() != null && directChoModel.getSpatial().size() > 0) {
					for (Place p : directChoModel.getSpatial()) {
						if (p.getId() != null && p.getId().longValueExact() > 0) {
							listIds.add(p.getId().longValueExact());
						}
					}
				}

				// if CHO's API model timespan contains ID means, we just update
				// timespan
				// and don't delete this timespan
				if (directChoModel.getTemporal() != null && directChoModel.getTemporal().size() > 0) {
					for (TimeSpan ts : directChoModel.getTemporal()) {
						if (ts.getId() != null && ts.getId().longValueExact() > 0) {
							listIds.add(ts.getId().longValueExact());
						}
					}
				}

				// deletes only related entities of CHO object, doesnt delete
				// entities with ids in listIds
				repository.deleteCHO(directChoModel.getId().longValueExact(), true, false, listIds);				
			}

			// list of weblinks that have ID in POST payload
			List<Long> weblinkIds = new ArrayList<Long>();

			if (directChoModel.getWebLinks() != null && directChoModel.getWebLinks().size() > 0) {
				for (WebLink w : directChoModel.getWebLinks()) {
					if (w.getId() != null && w.getId().longValueExact() > 0) {
						weblinkIds.add(w.getId().longValueExact());
					}
				}
			}
			
			// mapping Operation Direct CHO related agents to database CHO model
			if (directChoModel.getAgents() != null) {
	
				for (Agent a : directChoModel.getAgents()) {
					EuropeanaDataObjectEuropeanaDataObject newLink = new EuropeanaDataObjectEuropeanaDataObject();
					// maps Operation Direct Agent model to backend Agent model
					eu.europeana.direct.backend.model.Agent newAgent = agentLogic.mapToDatabase(a);
	
					if (newAgent != null) {
	
						newLink.setEuropeanaDataObjectByLinkedObjectId(newAgent.getEuropeanaDataObject());
						// set non aware custom field to Agent europeanaDataObject
						newLink.getEuropeanaDataObjectByLinkedObjectId()
								.setNonAwareCustomFields(agentLogic.getNonAwareCustomFields(a));
						// set lang aware custom field to Agent europeanaDataObject
						newLink.getEuropeanaDataObjectByLinkedObjectId()
								.setAwareCustomFields(agentLogic.getAwareCustomFields(a));
	
						newLink.setEuropeanaDataObjectByLinkingObjectId(choMapped.getEuropeanaDataObject());
						newAgent.getEuropeanaDataObject().setAgent(newAgent);
						newLink.setRoleType(EuropeanaDataObjectRoleType.Agent);
						if (a.getLanguageNonAwareFields() != null) {
							if (a.getLanguageNonAwareFields().getRole() != null
									&& a.getLanguageNonAwareFields().getRole().length() > 0) {
								newLink.setRole(a.getLanguageNonAwareFields().getRole());
							}
						}
	
						if (update && newAgent.getId() > 0) {
						} else {
							choMapped.getEuropeanaDataObject().getEuropeanaDataObjectForLinkingObject().add(newLink);
						}
					}
				}
			}

			// mapping Operation Direct CHO related concepts to database CHO model
			if (directChoModel.getConcepts() != null) {				
				for (Concept c : directChoModel.getConcepts()) {

					EuropeanaDataObjectEuropeanaDataObject newLink = new EuropeanaDataObjectEuropeanaDataObject();
					// maps Operation Direct Concept model to backend Concept model
					eu.europeana.direct.backend.model.Concept newConcept = conceptLogic.mapToDatabase(c);

					if (newConcept != null) {
						
						newLink.setEuropeanaDataObjectByLinkedObjectId(newConcept.getEuropeanaDataObject());
						// set non aware custom field to Concept europeanaDataObject
						newLink.getEuropeanaDataObjectByLinkedObjectId().setNonAwareCustomFields(conceptLogic.getNonAwareCustomFields(c));
						// set lang aware custom field to Concept europeanaDataObject
						newLink.getEuropeanaDataObjectByLinkedObjectId().setAwareCustomFields(conceptLogic.getAwareCustomFields(c));
						
						newLink.setEuropeanaDataObjectByLinkingObjectId(choMapped.getEuropeanaDataObject());
						newConcept.getEuropeanaDataObject().setConcept(newConcept);
						newLink.setRoleType(EuropeanaDataObjectRoleType.Concept);

						if (c.getLanguageNonAwareFields() != null) {
							if (c.getLanguageNonAwareFields().getRole() != null
									&& c.getLanguageNonAwareFields().getRole().length() > 0) {
								newLink.setRole(c.getLanguageNonAwareFields().getRole());
							}
						}

						if(update && newConcept.getId() > 0){								
						}else{							
							choMapped.getEuropeanaDataObject().getEuropeanaDataObjectForLinkingObject().add(newLink);
						}					
					}
				}
			}

			// mapping Operation Direct CHO related places to database CHO model
			if (directChoModel.getSpatial() != null) {				

				for (Place p : directChoModel.getSpatial()) {
					EuropeanaDataObjectEuropeanaDataObject newLink = new EuropeanaDataObjectEuropeanaDataObject();
					// maps Operation Direct Place model to backend Place model
					eu.europeana.direct.backend.model.Place newPlace = locationLogic.mapToDatabase(p);
					
					if (newPlace != null) {						
						
						newLink.setEuropeanaDataObjectByLinkedObjectId(newPlace.getEuropeanaDataObject());
						// set non aware custom field to Place europeanaDataObject
						newLink.getEuropeanaDataObjectByLinkedObjectId().setNonAwareCustomFields(locationLogic.getNonAwareCustomFields(p));
						// set lang aware custom field to Place europeanaDataObject
						newLink.getEuropeanaDataObjectByLinkedObjectId().setAwareCustomFields(locationLogic.getAwareCustomFields(p));						
						
						newLink.setEuropeanaDataObjectByLinkingObjectId(choMapped.getEuropeanaDataObject());
						newPlace.getEuropeanaDataObject().setPlace(newPlace);
						newLink.setRoleType(EuropeanaDataObjectRoleType.Location);
						if (p.getLanguageNonAwareFields() != null) {
							if (p.getLanguageNonAwareFields().getRole() != null
									&& p.getLanguageNonAwareFields().getRole().length() > 0) {
								newLink.setRole(p.getLanguageNonAwareFields().getRole());
							}
						}					
						if(update && newPlace.getId() > 0){								
						}else{							
							choMapped.getEuropeanaDataObject().getEuropeanaDataObjectForLinkingObject().add(newLink);
						}						
					}
				}
			}

			// mapping Operation Direct CHO related timespans to database CHO model
			if (directChoModel.getTemporal() != null) {				

				for (TimeSpan t : directChoModel.getTemporal()) {
					EuropeanaDataObjectEuropeanaDataObject newLink = new EuropeanaDataObjectEuropeanaDataObject();

					// maps Operation Direct Timespan model to backend Timespan
					// model
					eu.europeana.direct.backend.model.Timespan newTimespan = timespanLogic.mapToDatabase(t);
					if (newTimespan != null) {

						newLink.setEuropeanaDataObjectByLinkedObjectId(newTimespan.getEuropeanaDataObject());
						// set non aware custom field to TimeSpan europeanaDataObject
						newLink.getEuropeanaDataObjectByLinkedObjectId().setNonAwareCustomFields(timespanLogic.getNonAwareCustomFields(t));
						// set lang aware custom field to TimeSpan europeanaDataObject
						newLink.getEuropeanaDataObjectByLinkedObjectId().setAwareCustomFields(timespanLogic.getAwareCustomFields(t));	
						
						newLink.setEuropeanaDataObjectByLinkingObjectId(choMapped.getEuropeanaDataObject());
						newTimespan.getEuropeanaDataObject().setTimespan(newTimespan);
						newLink.setRoleType(EuropeanaDataObjectRoleType.Timespan);
						if (t.getLanguageNonAwareFields() != null) {
							if (t.getLanguageNonAwareFields().getRole() != null
									&& t.getLanguageNonAwareFields().getRole().length() > 0) {
								newLink.setRole(t.getLanguageNonAwareFields().getRole());
							}
						}
						if(update && newTimespan.getId() > 0){								
						}else{							
							choMapped.getEuropeanaDataObject().getEuropeanaDataObjectForLinkingObject().add(newLink);
						}															
					}
				}
			}

			// removes weblinks that are related to CHO, but are not specified in new POST request
			if (choMapped.getEuropeanaDataObject().getWebResources() != null) {
				if(choMapped.getEuropeanaDataObject().getWebResources().size() > 0){
					Iterator<WebResource> iterator = choMapped.getEuropeanaDataObject().getWebResources().iterator();
					while (iterator.hasNext()) {
						WebResource wr = iterator.next();
						if (!weblinkIds.contains(wr.getId())) {
							iterator.remove();
						}
					}	
				}				
			}

			// mapping Operation Direct CHO related weblinks to database CHO model
			if (directChoModel.getWebLinks() != null && !directChoModel.getWebLinks().isEmpty()) {
				for (WebLink weblink : directChoModel.getWebLinks()) {
					// maps Operation Direct WebLink model to backend WebResource
					// model
					eu.europeana.direct.backend.model.WebResource newResource = null;
					// use try catch for europeana import
//					try{
						newResource = weblinkLogic.mapToDatabase(weblink);	
//					} catch (Exception ex){
//						ex.printStackTrace();
//						continue;
//					}					

					if (newResource != null) {
						choMapped.getEuropeanaDataObject().getWebResources().add(newResource);						
					}										
					
				}
			}						
			return choMapped;				
	}

	/**
	 * Maps Operation Direct CHO model to database CHO model and saves into
	 * database
	 * 
	 * @param cho
	 *            Operation Direct CHO model
	 */
	public long mapAndSaveCHO(CulturalHeritageObject cho, boolean commitIndex, boolean useAsyncIndexing) throws Exception {
		eu.europeana.direct.backend.model.CulturalHeritageObject dbCho = null;
		long result = 0;
		try {
			
			dbCho = mapToDatabase(cho);
			result = repository.saveCHO(dbCho, commitIndex);
			
			if (result > 0) {
				cho.setId(new BigDecimal(result));															
				//for harvesting jobs only, otherwise we perform indexing for CHO asynchronously
				if(!useAsyncIndexing){
					LuceneIndexing.getInstance().updateLuceneIndex(cho, false, commitIndex, dbCho.getEuropeanaDataObject().getCreated(),true);
				}				
			}					
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);			
			throw e;
		}		
		return result;
	}

	/**
	 * Retrieves database CHO model and maps retrieved cho to Operation Direct
	 * CHO model and returns it.
	 * 
	 * @param id
	 *         Unique ID of cultural heritage object
	 */
	public CulturalHeritageObject getCHO(long id) throws Exception {
		eu.europeana.direct.backend.model.CulturalHeritageObject cho = getCHODomain(id);		
		if (cho == null) {			
			return null;
		} else {			
			CulturalHeritageObject result = mapFromDatabase(cho);
			return result;
		}
	}		
	
	public eu.europeana.direct.backend.model.CulturalHeritageObject getCHODomain(long id){
		try{
			return repository.getCHO(id);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * Deletes cultural heritage object from database
	 * 
	 * @param id
	 *            Unique ID of cultural heritage object
	 * @return
	 */
	public boolean deleteCHO(long id, boolean onlyCHO) {

		boolean deleted = false;
		if (id < 1) {
			return deleted;
		} else {
			if (repository.deleteCHO(id, false, onlyCHO, new ArrayList<>()) > 0) {
				// build message for user (which entities we're not deleted because of relation to other CHO's)				
				message = parseNotRelatedEntities(repository.getNotDeletedEntities());
				deletedEntityIds = repository.getDeletedEntities();
				deleted = true;
			}
			return deleted;
		}			
	}
	
	/**
	 * Parse ID's of not deleted entities to message for user
	 * @param idsNotDeleted
	 * @return
	 */
	private String parseNotRelatedEntities(List<String> idsNotDeleted) {
		StringBuilder sb = new StringBuilder();
		if(idsNotDeleted.size() > 0){
			sb.append("Contextual entites not deleted: ");
			for(String id : idsNotDeleted){
				sb.append(id);				
				sb.append(",");
			}
			// remove last char: ','			
			return sb.toString().substring(0,sb.toString().length()-1);			
		}
		return sb.toString();
	}

 	public List<CulturalHeritageObject> getByLimit(int startFromRow, int recordsNum){		
		List<eu.europeana.direct.backend.model.CulturalHeritageObject> dbChoList = null;		
		List<eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject> mappedChoList = new ArrayList<>();
		// get number of records (records) from row (startFromRow)
		dbChoList = repository.loadByLimit(startFromRow,recordsNum);		
		
		if(dbChoList != null){
			for (eu.europeana.direct.backend.model.CulturalHeritageObject cho : dbChoList) {		
				try {
					// map to application model (direct)
					mappedChoList.add(mapFromDatabase(cho));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// clear cache
			repository.clear();
		} else {
			return null;
		}			
		
		if(mappedChoList.size() == 0){
			return null;
		}
		return mappedChoList;
	}
	
	public void close() {		
		repository.close();
	}

	public void startTransaction() {		
		repository.getManager().getTransaction().begin();		
	}

	public void rollbackTransaction() {
		try {
			if(repository.getManager().getTransaction().isActive()){				
				repository.getManager().getTransaction().rollback();				
			}			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}				
		
	}
	
	public void commitTransaction() {
		try {
			if(repository.getManager().getTransaction().isActive()){
				repository.getManager().getTransaction().commit();	
			}			
		} catch (Exception e) {
			repository.getManager().getTransaction().rollback();
			logger.error(e.getMessage(), e);
		}
	}
	
	public String getMessage() {
		return message;
	}
			
	public List<DeletedEntity> getDeletedEntityIds() {
		return deletedEntityIds;
	}

	public List<eu.europeana.direct.backend.model.CulturalHeritageObject> getByDataOwner(String dataOwner) {					
		return repository.loadByDataOwner(dataOwner);
	}

	public List<eu.europeana.direct.backend.model.CulturalHeritageObject> getFromTo(long id, long id2) {					
		return repository.fromTo(id,id2);
	}

	/**
	 * Performs indexing of CHO object
	 * @param id ID of CHO object
	 */
	public void sendIndexing(long id) {		
		try{
			// send JMS message thorugh channel, JMS consumer will receive msg and perform indexing of CHO								
			MessageProducer.sendMsg(new IndexMessage(id, false, ""), MessageType.UPDATE_INDEX);					
		}catch (Exception e){
			e.printStackTrace();
		}		
	}

	/**
	 * Deletes cho object from index and contextual entitiy objects of this CHO from entities index
	 * @param id ID of CHO object
	 * @param deletedEntities Contextual entities that we're deleted with CHO (because of relation)
	 */
	public void deleteFromIndex(long id, List<DeletedEntity> deletedEntities) {		
		try {			
			// send JMS message through channel, JMS consumer will receive msg and perform deletion of CHO document and related entities documents			
			MessageProducer.sendMsg(new DeleteIndexMessage(id,deletedEntities,false), MessageType.DELETE_FROM_INDEX);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
}
