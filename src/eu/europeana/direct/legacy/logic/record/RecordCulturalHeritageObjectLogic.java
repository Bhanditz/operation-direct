package eu.europeana.direct.legacy.logic.record;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;

import eu.europeana.direct.backend.model.AgentLangAware;
import eu.europeana.direct.backend.model.ConceptLangAware;
import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.backend.model.EuropeanaDataObjectRoleType;
import eu.europeana.direct.backend.repositories.AgentRepository;
import eu.europeana.direct.backend.repositories.ConceptRepository;
import eu.europeana.direct.backend.repositories.CulturalHeritageObjectRepository;
import eu.europeana.direct.legacy.model.record.Agent;
import eu.europeana.direct.legacy.model.record.Aggregation;
import eu.europeana.direct.legacy.model.record.Concept;
import eu.europeana.direct.legacy.model.record.CulturalHeritageObject;
import eu.europeana.direct.legacy.model.record.EuropeanaAggregation;
import eu.europeana.direct.legacy.model.record.Place;
import eu.europeana.direct.legacy.model.record.Position;
import eu.europeana.direct.legacy.model.record.Proxy;
import eu.europeana.direct.legacy.model.record.Timespan;
import eu.europeana.direct.legacy.model.record.WebResource;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.logic.entities.AgentLogic;
import eu.europeana.direct.logic.entities.ConceptLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.KeyValuePair;
import eu.europeana.direct.rest.gen.java.io.swagger.model.PlaceLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpanLangaugeAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;

/**
 * RecordCulturalHeritageObjectLogic is used for mapping database CHO model to record api CHO model
 */
public class RecordCulturalHeritageObjectLogic {
	
	private CulturalHeritageObjectLogic choLogic;
	private AgentLogic agentLogic;
	private ConceptLogic conceptLogic;
	
	
	
	public RecordCulturalHeritageObjectLogic(CulturalHeritageObjectLogic choLogic) {
		super();
		this.choLogic = choLogic;
		agentLogic = new AgentLogic();
		conceptLogic = new ConceptLogic();
	}



	/**
	 * Method maps database CHO model to record api CHO model
	 * @param databaseCho Database CHO model
	 */
	public CulturalHeritageObject mapFromDatabase(
			eu.europeana.direct.backend.model.CulturalHeritageObject databaseCho) throws Exception{		
		
		try{
		List<String> creator = new ArrayList<String>();
		Proxy proxy = new Proxy();
		
		// linked objects that have connection to CHO
		Set<EuropeanaDataObjectEuropeanaDataObject> listEdoEdo = databaseCho.getEuropeanaDataObject().getEuropeanaDataObjectForLinkingObject();
	
		Map<String,List<String>> proxyMedium = new HashMap<String,List<String>>();
		Map<String,List<String>> proxyType = new HashMap<String,List<String>>();
		Map<String,List<String>> proxySubject = new HashMap<String,List<String>>();
		Map<String,List<String>> proxyCreator = new HashMap<String,List<String>>();

		// find dcCreators and map them as agents
		try{			
			for(EuropeanaDataObjectEuropeanaDataObject edoedo : listEdoEdo){
				if(edoedo.getRole() != null && edoedo.getRole().length() > 0){	
					if(edoedo.getRole().toLowerCase().contains("dc:creator")){
						creator.add("/direct/"+edoedo.getEuropeanaDataObjectByLinkedObjectId().getId());
						//check if edoedo is agent type
						if(edoedo.getRoleType().equals(EuropeanaDataObjectRoleType.Agent)){							
							//get agent
							eu.europeana.direct.backend.model.Agent a = agentLogic.getAgentDb(edoedo.getEuropeanaDataObjectByLinkedObjectId().getId());
							// map agent lang aware fields to proxyCreator
							for (Map.Entry<String, List<AgentLangAware>> entry : a.getLanguageAwareFields().entrySet()) {
								for(AgentLangAware agentAware : entry.getValue()){
									for(int i = 0; i < agentAware.getPreferredLabel().length; i++){
										// if already contains key, just add to arraylist new preffered label
										if(proxyCreator.containsKey(entry.getKey())){
											proxyCreator.get(entry.getKey()).add(agentAware.getPreferredLabel()[i]);
										}else{
											// first create list of new preffered label for new language
											List<String> list = new ArrayList<String>();
											list.add(agentAware.getPreferredLabel()[i]);									
											proxyCreator.put(entry.getKey(), list);
										}
									}		
								}											
							}
						}
					} else if(edoedo.getRole().toLowerCase().contains("dc:type")){
						//mapping dc:type if in concept role
						if(edoedo.getRoleType().equals(EuropeanaDataObjectRoleType.Concept)){							
							eu.europeana.direct.backend.model.Concept c = conceptLogic.getConceptDb(edoedo.getEuropeanaDataObjectByLinkedObjectId().getId());						

							for (Map.Entry<String, List<ConceptLangAware>> entry : c.getLanguageAwareFields().entrySet()) {
								for(ConceptLangAware conceptAware : entry.getValue()){
									for(int i = 0; i < conceptAware.getPreferredLabel().length; i++){
										// if already contains key, just add to arraylist new preffered label
										if(proxyType.containsKey(entry.getKey())){
											proxyType.get(entry.getKey()).add(conceptAware.getPreferredLabel()[i]);
										}else{
											// first create list of new preffered label for new language
											List<String> list = new ArrayList<String>();
											list.add(conceptAware.getPreferredLabel()[i]);									
											proxyType.put(entry.getKey(), list);
										}
									}	
								}																				
							}
						}
					//mapping dc:medium if in concept role
					}else if(edoedo.getRole().toLowerCase().contains("dc:medium")){
						if(edoedo.getRoleType().equals(EuropeanaDataObjectRoleType.Concept)){							
							eu.europeana.direct.backend.model.Concept c = conceptLogic.getConceptDb(edoedo.getEuropeanaDataObjectByLinkedObjectId().getId());
													
							for (Map.Entry<String, List<ConceptLangAware>> entry : c.getLanguageAwareFields().entrySet()) {
								for(ConceptLangAware conceptAware : entry.getValue()){
									for(int i = 0; i < conceptAware.getPreferredLabel().length; i++){								
										// if already contains key, just add to arraylist new preffered label
										if(proxyMedium.containsKey(entry.getKey())){
											proxyMedium.get(entry.getKey()).add(conceptAware.getPreferredLabel()[i]);
										}else{
											// first create list of new preffered label for new language
											List<String> list = new ArrayList<String>();
											list.add(conceptAware.getPreferredLabel()[i]);									
											proxyMedium.put(entry.getKey(), list);
										}
									}	
								}							
							}
						}
					//mapping dc:subject if in concept role
					}else if(edoedo.getRole().toLowerCase().contains("dc:subject")){
						if(edoedo.getRoleType().equals(EuropeanaDataObjectRoleType.Concept)){							
							eu.europeana.direct.backend.model.Concept c = conceptLogic.getConceptDb(edoedo.getEuropeanaDataObjectByLinkedObjectId().getId());
							
							for (Map.Entry<String, List<ConceptLangAware>> entry : c.getLanguageAwareFields().entrySet()) {
								for(ConceptLangAware conceptAware : entry.getValue()){
									for(int i = 0; i < conceptAware.getPreferredLabel().length; i++){
										// if already contains key, just add to arraylist new preffered label
										if(proxySubject.containsKey(entry.getKey())){
											proxySubject.get(entry.getKey()).add(conceptAware.getPreferredLabel()[i]);
										}else{
											// first create list of new preffered label for new language
											List<String> list = new ArrayList<String>();
											list.add(conceptAware.getPreferredLabel()[i]);									
											proxySubject.put(entry.getKey(), list);
										}
									}	
								}							
							}
						}
					}
				}
			}			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			agentLogic.close();
			conceptLogic.close();
		}					
		
		// dc:creator - map english language (if contains) to default language
		if(proxyCreator.containsKey("en")){			
			proxy.getDcCreator().put("en", proxyCreator.get("en"));
			proxy.getDcCreator().put("def", proxyCreator.get("en"));
		}
		
		for(Map.Entry<String, List<String>> entry : proxyCreator.entrySet()) {
			// skip english language, we mapped it already if it exists
			if(entry.getKey().equals("en")){
				continue;
			}
			proxy.getDcCreator().put(entry.getKey(), entry.getValue());
			// if no english language, map first language to default
			if(!proxy.getDcCreator().containsKey("def")){
				proxy.getDcCreator().put("def", entry.getValue());
			}									 
		}
		
		// dc:medium - map english language (if contains) to default language
		if(proxyMedium.containsKey("en")){			
			proxy.getDctermsMedium().put("en", proxyMedium.get("en"));
			proxy.getDctermsMedium().put("def", proxyMedium.get("en"));
		}
		
		for(Map.Entry<String, List<String>> entry : proxyMedium.entrySet()) {
			// skip english language, we mapped it already if it exists
			if(entry.getKey().equals("en")){
				continue;
			}
			proxy.getDctermsMedium().put(entry.getKey(), entry.getValue());
			// if no english language, map first language to default
			if(!proxy.getDctermsMedium().containsKey("def")){
				proxy.getDctermsMedium().put("def", entry.getValue());
			}									 
		}
		
		// dc:type - map english language (if contains) to default language
		if(proxyType.containsKey("en")){			
			proxy.getDcType().put("en", proxyType.get("en"));
			proxy.getDcType().put("def", proxyType.get("en"));
		}
		
		for(Map.Entry<String, List<String>> entry : proxyType.entrySet()) {
			// skip english language, we mapped it already if it exists
			if(entry.getKey().equals("en")){
				continue;
			}
			proxy.getDcType().put(entry.getKey(), entry.getValue());
			// if no english language, map first language to default
			if(!proxy.getDcType().containsKey("def")){
				proxy.getDcType().put("def", entry.getValue());
			}									 
		}
		
		// dc:subject - map english language (if contains) to default language
		if (proxySubject.containsKey("en")) {
			proxy.getDcSubject().put("en", proxySubject.get("en"));
			proxy.getDcSubject().put("def", proxySubject.get("en"));
		}

		for (Map.Entry<String, List<String>> entry : proxySubject.entrySet()) {
			// skip english language, we mapped it already if it exists
			if (entry.getKey().equals("en")) {
				continue;
			}
			proxy.getDcSubject().put(entry.getKey(), entry.getValue());
			// if no english language, map first language to default
			if (!proxy.getDcSubject().containsKey("def")) {
				proxy.getDcSubject().put("def", entry.getValue());
			}
		}
		
		
		// map CHO from backend model to rest model
		eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject culturalHeritageObject = choLogic.mapFromDatabase(databaseCho);
			
		
		String objectId = "/direct/"+databaseCho.getId();
		
		// EDM model from Europeana API
		eu.europeana.direct.legacy.model.record.CulturalHeritageObject legacyCulturalHeritageObject = new CulturalHeritageObject();
		List<String> languages = new ArrayList<String>();
		List<String> titlesCho = new ArrayList<String>();
		List<Proxy> proxies = new ArrayList<Proxy>();
		List<EuropeanaAggregation> europeanaAggregations = new ArrayList<EuropeanaAggregation>();
		List<Aggregation> aggregations = new ArrayList<Aggregation>();
		List<String> langs = new ArrayList<String>();
		String defaultLanguage = "def";
		Map<String,List<String>> proxyPublisher = new HashMap<String,List<String>>();
		Map<String,List<String>> proxySource = new HashMap<String,List<String>>();
		Map<String,List<String>> proxyAlternative = new HashMap<String,List<String>>();
		Map<String,List<String>> proxyCreated = new HashMap<String,List<String>>();
		Map<String,List<String>> proxyIssued = new HashMap<String,List<String>>();
		Map<String,List<String>> proxyFormat = new HashMap<String,List<String>>();
		Map<String,List<String>> proxyExtent = new HashMap<String,List<String>>();
		Map<String,List<String>> proxyProvenance = new HashMap<String,List<String>>();

		
		Aggregation aggregation = new Aggregation();
		EuropeanaAggregation euAggregation = new EuropeanaAggregation();		
		
		// check if CHO has english metadata values for language aware object		
		boolean hasEnglish = culturalHeritageObject.getLanguageAwareFields().stream().anyMatch(x -> x.getLanguage().equals("en"));
		boolean englishLang = false;
		
		if(creator != null && creator.size() > 0){
			euAggregation.getDcCreator().put(defaultLanguage, creator);	
		}	
		
		// mapping CHO language aware fields to API Search Record model
		for(CulturalHeritageObjectLanguageAware choLangAware : culturalHeritageObject.getLanguageAwareFields()){			
			

			List<String> listDataOwner = new ArrayList<String>();
			listDataOwner.add(culturalHeritageObject.getLanguageNonAwareFields().getDataOwner());
			
			if(hasEnglish){
				englishLang = choLangAware.getLanguage().equals("en");				
			}			
			
			proxy.setAbout("/proxy"+objectId);				
			proxy.setEdmType(culturalHeritageObject.getLanguageNonAwareFields().getType().toString());
			euAggregation.setAbout("/europeanaAggregation"+objectId);			
			aggregation.setAbout("/aggregation"+objectId);
			
			if(culturalHeritageObject.getLanguageNonAwareFields().getDataOwner() != null){
				if(aggregation.getEdmDataProvider().containsKey(choLangAware.getLanguage())){
					aggregation.getEdmDataProvider().get(choLangAware.getLanguage()).add(culturalHeritageObject.getLanguageNonAwareFields().getDataOwner());
				}else{
					aggregation.getEdmDataProvider().put(choLangAware.getLanguage(),listDataOwner);
				}
			}

			//adding default language values - if english exists then default language is english, otherwise first in list
			if(hasEnglish){
				if(englishLang){
					if(!aggregation.getEdmDataProvider().containsKey(defaultLanguage)){
						aggregation.getEdmDataProvider().put(defaultLanguage,listDataOwner);
					}
				}
			}else{				
				if(!aggregation.getEdmDataProvider().containsKey(defaultLanguage)){					
					aggregation.getEdmDataProvider().put(defaultLanguage,listDataOwner);
				}
			}			
						
			
			langs.add(choLangAware.getLanguage());			
			titlesCho.add(choLangAware.getTitle());			
												
			if(!culturalHeritageObject.getLanguageNonAwareFields().getIdentifier().isEmpty()){				
				List<String> identifiers = new ArrayList<String>();
				for(String i : culturalHeritageObject.getLanguageNonAwareFields().getIdentifier()){				
					identifiers.add(i);
				}
				
				if(proxy.getDcIdentifier().containsKey(choLangAware.getLanguage())){
					for(String i : culturalHeritageObject.getLanguageNonAwareFields().getIdentifier()){				
						proxy.getDcIdentifier().get(choLangAware.getLanguage()).add(i);
					}					
				}else{
					proxy.getDcIdentifier().put(choLangAware.getLanguage(),identifiers);
				}
							
				//adding default language values - if english exists then default language is english, otherwise first in list
				if(hasEnglish){
					if(englishLang){
						if(!proxy.getDcIdentifier().containsKey(defaultLanguage)){
							proxy.getDcIdentifier().put(defaultLanguage, identifiers);
						}
					}
				}else{
					if(!proxy.getDcIdentifier().containsKey(defaultLanguage)){
						proxy.getDcIdentifier().put(defaultLanguage, identifiers);
					}
				}
			}
			
			if(!culturalHeritageObject.getLanguageNonAwareFields().getIdentifier().isEmpty()){				
			}
			
			if(!culturalHeritageObject.getLanguageNonAwareFields().getRelation().isEmpty()){								
				List<String> relations = new ArrayList<String>();					
				for(String r : culturalHeritageObject.getLanguageNonAwareFields().getRelation()){									
					relations.add(r);
				}
				
				// add only if it doesnt contain list with this lang
				if(proxy.getDcRelation().containsKey(choLangAware.getLanguage())){
					for(String r : culturalHeritageObject.getLanguageNonAwareFields().getRelation()){									
						proxy.getDcRelation().get(choLangAware.getLanguage()).add(r);
					}	
				}else{					
					proxy.getDcRelation().put(choLangAware.getLanguage(),relations);
				}
				//adding default language values - if english exists then default language is english, otherwise first in list
				if(hasEnglish){
					if(englishLang){
						if(!proxy.getDcRelation().containsKey(defaultLanguage)){
							proxy.getDcRelation().put(defaultLanguage, relations);
						}
					}
				}else{
					if(!proxy.getDcRelation().containsKey(defaultLanguage)){
						proxy.getDcRelation().put(defaultLanguage, relations);
					}
				}
			}
			
			if(culturalHeritageObject.getLanguageNonAwareFields().getOwner() != null){				
				List<String> owners = new ArrayList<String>();
				owners.add(culturalHeritageObject.getLanguageNonAwareFields().getOwner());							
			}
			
			if(choLangAware.getTitle() != null){
				List<String> titles = new ArrayList<String>();
				titles.add(choLangAware.getTitle());

				if(proxy.getDcTitle().containsKey(choLangAware.getLanguage())){
					proxy.getDcTitle().get(choLangAware.getLanguage()).add(choLangAware.getTitle());
				}else{
					proxy.getDcTitle().put(choLangAware.getLanguage(),titles);
				}
				
				//adding default language values - if english exists then default language is english, otherwise first in list
				if(hasEnglish){
					if(englishLang){
						if(!proxy.getDcTitle().containsKey(defaultLanguage)){
							proxy.getDcTitle().put(defaultLanguage, titles);
						}
					}
				}else{
					if(!proxy.getDcTitle().containsKey(defaultLanguage)){
						proxy.getDcTitle().put(defaultLanguage, titles);
					}
				}
			}
			
			if(choLangAware.getDescription() != null){
				List<String> descriptions = new ArrayList<String>();
				descriptions.add(choLangAware.getDescription());
								
				if(proxy.getDcDescription().containsKey(choLangAware.getLanguage())){
					proxy.getDcDescription().get(choLangAware.getLanguage()).add(choLangAware.getDescription());
				}else{
					proxy.getDcDescription().put(choLangAware.getLanguage(), descriptions);
				}
				
				//adding default language values - if english exists then default language is english, otherwise first in list
				if(hasEnglish){
					if(englishLang){
						if(!proxy.getDcDescription().containsKey(defaultLanguage)){
							proxy.getDcDescription().put(defaultLanguage, descriptions);
						}						
					}
				}else{
					if(!proxy.getDcDescription().containsKey(defaultLanguage)){
						proxy.getDcDescription().put(defaultLanguage, descriptions);
					}
				}
			}
			
			if(!choLangAware.getPublisher().isEmpty()){				
				for(String p : choLangAware.getPublisher()){									
					// if already contains key, just add to arraylist new preffered label
					if(proxyPublisher.containsKey(choLangAware.getLanguage())){
						proxyPublisher.get(choLangAware.getLanguage()).add(p);
					}else{
						// first create list of new preffered label for new language
						List<String> list = new ArrayList<String>();
						list.add(p);									
						proxyPublisher.put(choLangAware.getLanguage(), list);
					}					
				}					
			}
			
			if(!choLangAware.getSource().isEmpty()){				
				for(String s : choLangAware.getSource()){
					if(proxySource.containsKey(choLangAware.getLanguage())){
						proxySource.get(choLangAware.getLanguage()).add(s);
					}else{
						// first create list of new preffered label for new language
						List<String> list = new ArrayList<String>();
						list.add(s);									
						proxySource.put(choLangAware.getLanguage(), list);
					}	
				}							
			}
			if(!choLangAware.getAlternative().isEmpty()){				
				for(String a : choLangAware.getAlternative()){
					if(proxyAlternative.containsKey(choLangAware.getLanguage())){
						proxyAlternative.get(choLangAware.getLanguage()).add(a);
					}else{
						// first create list of new preffered label for new language
						List<String> list = new ArrayList<String>();
						list.add(a);									
						proxyAlternative.put(choLangAware.getLanguage(), list);
					}
				}			
			}
			
			if(choLangAware.getCreated() != null){				
				if(proxyCreated.containsKey(choLangAware.getLanguage())){
					proxyCreated.get(choLangAware.getLanguage()).add(choLangAware.getCreated());
				}else{
					// first create list of new preffered label for new language													
					List<String> created = new ArrayList<String>();
					created.add(choLangAware.getCreated());
					proxyCreated.put(choLangAware.getLanguage(), created);
				}						
			}
			
			if(choLangAware.getIssued() != null){				
				if(proxyIssued.containsKey(choLangAware.getLanguage())){
					proxyIssued.get(choLangAware.getLanguage()).add(choLangAware.getIssued());
				}else{
					// first create list of new preffered label for new language													
					List<String> issued = new ArrayList<String>();
					issued.add(choLangAware.getIssued());
					proxyIssued.put(choLangAware.getLanguage(), issued);
				}	
			}
			
			if(!choLangAware.getFormat().isEmpty()){
				for(String f : choLangAware.getFormat()){
					// if already contains key, just add to arraylist new preffered label
					if(proxyFormat.containsKey(choLangAware.getLanguage())){
						proxyFormat.get(choLangAware.getLanguage()).add(f);
					}else{
						// first create list of new preffered label for new language
						List<String> list = new ArrayList<String>();
						list.add(f);									
						proxyFormat.put(choLangAware.getLanguage(), list);
					}
				}				
			}
			
			if(!choLangAware.getExtent().isEmpty()){
				for(String e : choLangAware.getExtent()){
					// if already contains key, just add to arraylist new preffered label
					if(proxyExtent.containsKey(choLangAware.getLanguage())){
						proxyExtent.get(choLangAware.getLanguage()).add(e);
					}else{
						// first create list of new preffered label for new language
						List<String> list = new ArrayList<String>();
						list.add(e);									
						proxyExtent.put(choLangAware.getLanguage(), list);
					}
				}				
			}
			
			if(!choLangAware.getMedium().isEmpty()){				
				for(String m : choLangAware.getMedium()){					
					// if already contains key, just add to arraylist new
					// preffered label
					if (proxyMedium.containsKey(choLangAware.getLanguage())) {
						proxyMedium.get(choLangAware.getLanguage()).add(m);
					} else {
						// first create list of new preffered label for new
						// language
						List<String> list = new ArrayList<String>();
						list.add(m);
						proxyMedium.put(choLangAware.getLanguage(), list);
					}
				}				
			}
			
			if(!choLangAware.getProvenance().isEmpty()){
				for(String p : choLangAware.getProvenance()){
					// if already contains key, just add to arraylist new
					// preffered label
					if (proxyProvenance.containsKey(choLangAware.getLanguage())) {
						proxyProvenance.get(choLangAware.getLanguage()).add(p);
					} else {
						// first create list of new preffered label for new
						// language
						List<String> list = new ArrayList<String>();
						list.add(p);
						proxyProvenance.put(choLangAware.getLanguage(), list);
					}
				}			
			}
			
			Map<Integer,String> previewImages = new HashMap<Integer,String>();															
			// sorts weblinks by id ascending
			List<WebLink> sortedWeblinks = culturalHeritageObject.getWebLinks().parallelStream()
				    .sorted(Comparator.comparingLong(w -> w.getId().longValueExact()))
				    .collect(Collectors.toList());														

			// mapping current location from custom fields
			if(databaseCho.getEuropeanaDataObject().getNonAwareCustomFields().containsKey("Current location")){
				proxy.setEdmCurrentLocation(databaseCho.getEuropeanaDataObject().getNonAwareCustomFields().get("Current location"));
			}			
			
			// mapping weblink fields to API Search Record model
			if(!culturalHeritageObject.getWebLinks().isEmpty()){	
				
				// get fields svcHasService and isReferencedBy. important only for IIIF records								
				List<WebResource> legacyWebResources = new ArrayList<WebResource>();				
				List<String> rights = new ArrayList<String>();
				for(eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink w : sortedWeblinks){				
					WebResource legacyWebResource = new WebResource();
					if(w.getRights() != null){											
						rights.add(w.getRights());	
						
						if(!legacyWebResource.getWebResourceEdmRights().containsKey(defaultLanguage)){
							legacyWebResource.getWebResourceEdmRights().put(defaultLanguage,rights);
						}
						
						// add only if it doesn't contain list with this lang						
						if(!proxy.getEdmRights().containsKey(defaultLanguage)){
							proxy.getEdmRights().put(defaultLanguage, rights);
						}
								
						// add only if it doesn't contain list with this lang
						if(!euAggregation.getEdmRights().containsKey(defaultLanguage)){
							euAggregation.getEdmRights().put(defaultLanguage, rights);
						}
						
						// add only if it doesn't contain list with this lang
						if(!aggregation.getEdmRights().containsKey(defaultLanguage)){
							aggregation.getEdmRights().put(defaultLanguage, rights);
						}
					}															
					
					if(w.getType() != null && w.getLink() != null && w.getLink().length() > 0){
						
						
						switch (w.getType()) {					
						case DIRECT_MEDIA:
							if(!previewImages.containsKey(0)){
								previewImages.put(0, w.getLink());
							}
							legacyWebResource.setAbout(w.getLink());	
							break;
						case LANDING_PAGE:
							if(!previewImages.containsKey(2)){
								previewImages.put(2, w.getLink());	
							}							
							legacyWebResource.setAbout(w.getLink());						
							break;
						case PREVIEW_SOURCE:
							if(!previewImages.containsKey(1)){
								previewImages.put(1, w.getLink());	
							}							
							legacyWebResource.setAbout(w.getLink());	
							break;
						case OTHER:
							if(!previewImages.containsKey(3)){
								previewImages.put(3, w.getLink());
							}
							legacyWebResource.setAbout(w.getLink());	
							break;										
						}												
					}
					
					// map custom field service if exists
					if(databaseCho.getEuropeanaDataObject().getNonAwareCustomFields().containsKey("Service")){
						legacyWebResource.setSvcsHasService(databaseCho.getEuropeanaDataObject().getNonAwareCustomFields().get("Service"));
					}

					// map custom field isReferencedBy if exists
					if(databaseCho.getEuropeanaDataObject().getNonAwareCustomFields().containsKey("isReferencedBy")){
						legacyWebResource.setDctermsIsReferencedBy(databaseCho.getEuropeanaDataObject().getNonAwareCustomFields().get("isReferencedBy"));
					}					
					
					legacyWebResources.add(legacyWebResource);

				}
				String imageUri = "";
				if(previewImages.containsKey(0)){
					imageUri = previewImages.get(0);
				}else if(previewImages.containsKey(1)){
					imageUri = previewImages.get(1);
				}
				
				if(previewImages.containsKey(2)){					
					aggregation.setEdmIsShownAt(previewImages.get(2));					
					euAggregation.setEdmLandingPage(previewImages.get(2));					
				}
				
				//displaying the picture on europeana portal
				if(imageUri.length() > 0){
					aggregation.setEdmObject(imageUri);				
					//String edmPreview = "https://europeana-direct.semantika.eu/EuropeanaDirect/api/v2/thumbnail-by-url.json?uri=" + imageUri + "&size=LARGE&type=IMAGE";
					//byte[] encodedImageUri = Base64.encodeBase64(imageUri.getBytes());						
					//String edmPreview = "http://data.jewisheritage.net/rest/api/v2/thumbnail-by-url.json?uri=" + new String(encodedImageUri) + "&size=LARGE&type=IMAGE";
					euAggregation.setEdmPreview(imageUri);
					aggregation.setEdmIsShownBy(imageUri);	
					euAggregation.setEdmIsShownBy(imageUri);
				}
										
				WebResource[] array = new WebResource[legacyWebResources.size()];				
				//euAggregation.setWebResources(legacyWebResources.toArray(array));
				aggregation.setWebResources(legacyWebResources.toArray(array));
			}
			
			languages.add(choLangAware.getLanguage());
		}
		
		// dc:publisher - map english language (if contains) to default language
		if (proxyPublisher.containsKey("en")) {
			proxy.getDcPublisher().put("en", proxyPublisher.get("en"));
			proxy.getDcPublisher().put("def", proxyPublisher.get("en"));
		}

		for (Map.Entry<String, List<String>> entry : proxyPublisher.entrySet()) {
			// skip english language, we mapped it already if it exists
			if (entry.getKey().equals("en")) {
				continue;
			}
			proxy.getDcPublisher().put(entry.getKey(), entry.getValue());
			// if no english language, map first language to default
			if (!proxy.getDcPublisher().containsKey("def")) {
				proxy.getDcPublisher().put("def", entry.getValue());
			}
		}
		
		// dc:source - map english language (if contains) to default language
		if (proxySource.containsKey("en")) {
			proxy.getDcSource().put("en", proxySource.get("en"));
			proxy.getDcSource().put("def", proxySource.get("en"));
		}

		for (Map.Entry<String, List<String>> entry : proxySource.entrySet()) {
			// skip english language, we mapped it already if it exists
			if (entry.getKey().equals("en")) {
				continue;
			}
			proxy.getDcSource().put(entry.getKey(), entry.getValue());
			// if no english language, map first language to default
			if (!proxy.getDcSource().containsKey("def")) {
				proxy.getDcSource().put("def", entry.getValue());
			}
		}
		
		// dc:alternative - map english language (if contains) to default language
		if (proxyAlternative.containsKey("en")) {
			proxy.getDctermsAlternative().put("en", proxyAlternative.get("en"));
			proxy.getDctermsAlternative().put("def", proxyAlternative.get("en"));
		}

		for (Map.Entry<String, List<String>> entry : proxyAlternative.entrySet()) {
			// skip english language, we mapped it already if it exists
			if (entry.getKey().equals("en")) {
				continue;
			}
			proxy.getDctermsAlternative().put(entry.getKey(), entry.getValue());
			// if no english language, map first language to default
			if (!proxy.getDctermsAlternative().containsKey("def")) {
				proxy.getDctermsAlternative().put("def", entry.getValue());
			}
		}
		
		// dc:created - map english language (if contains) to default language
		if (proxyCreated.containsKey("en")) {
			proxy.getDctermsCreated().put("en", proxyCreated.get("en"));
			proxy.getDctermsCreated().put("def", proxyCreated.get("en"));
		}

		for (Map.Entry<String, List<String>> entry : proxyCreated.entrySet()) {
			// skip english language, we mapped it already if it exists
			if (entry.getKey().equals("en")) {
				continue;
			}
			proxy.getDctermsCreated().put(entry.getKey(), entry.getValue());
			// if no english language, map first language to default
			if (!proxy.getDctermsCreated().containsKey("def")) {
				proxy.getDctermsCreated().put("def", entry.getValue());
			}
		}
		
		// dc:issued - map english language (if contains) to default language
		if (proxyIssued.containsKey("en")) {
			proxy.getDctermsIssued().put("en", proxyIssued.get("en"));
			proxy.getDctermsIssued().put("def", proxyIssued.get("en"));
		}

		for (Map.Entry<String, List<String>> entry : proxyIssued.entrySet()) {
			// skip english language, we mapped it already if it exists
			if (entry.getKey().equals("en")) {
				continue;
			}
			proxy.getDctermsIssued().put(entry.getKey(), entry.getValue());
			// if no english language, map first language to default
			if (!proxy.getDctermsIssued().containsKey("def")) {
				proxy.getDctermsIssued().put("def", entry.getValue());
			}
		}
		
		// dc:format - map english language (if contains) to default language
		if (proxyFormat.containsKey("en")) {
			proxy.getDcFormat().put("en", proxyFormat.get("en"));
			proxy.getDcFormat().put("def", proxyFormat.get("en"));
		}

		for (Map.Entry<String, List<String>> entry : proxyFormat.entrySet()) {
			// skip english language, we mapped it already if it exists
			if (entry.getKey().equals("en")) {
				continue;
			}
			proxy.getDcFormat().put(entry.getKey(), entry.getValue());
			// if no english language, map first language to default
			if (!proxy.getDcFormat().containsKey("def")) {
				proxy.getDcFormat().put("def", entry.getValue());
			}
		}
		
		// dc:extent - map english language (if contains) to default language
		if (proxyExtent.containsKey("en")) {
			proxy.getDctermsExtent().put("en", proxyExtent.get("en"));
			proxy.getDctermsExtent().put("def", proxyExtent.get("en"));
		}

		for (Map.Entry<String, List<String>> entry : proxyExtent.entrySet()) {
			// skip english language, we mapped it already if it exists
			if (entry.getKey().equals("en")) {
				continue;
			}
			proxy.getDctermsExtent().put(entry.getKey(), entry.getValue());
			// if no english language, map first language to default
			if (!proxy.getDctermsExtent().containsKey("def")) {
				proxy.getDctermsExtent().put("def", entry.getValue());
			}
		}
		
		// dc:provenance - map english language (if contains) to default language
		if (proxyProvenance.containsKey("en")) {
			proxy.getDctermsProvenance().put("en", proxyProvenance.get("en"));
			proxy.getDctermsProvenance().put("def", proxyProvenance.get("en"));
		}

		for (Map.Entry<String, List<String>> entry : proxyProvenance.entrySet()) {
			// skip english language, we mapped it already if it exists
			if (entry.getKey().equals("en")) {
				continue;
			}
			proxy.getDctermsProvenance().put(entry.getKey(), entry.getValue());
			// if no english language, map first language to default
			if (!proxy.getDctermsProvenance().containsKey("def")) {
				proxy.getDctermsProvenance().put("def", entry.getValue());
			}
		}

		europeanaAggregations.add(euAggregation);		
		proxies.add(proxy);
		euAggregation.getEdmLanguage().put(defaultLanguage, langs);

		
		if(aggregation.getWebResources() != null && aggregation.getEdmRights() != null){
			if(aggregation.getWebResources().length > 0 && aggregation.getEdmRights().size() > 0){
				aggregations.add(aggregation);	
			}				
		}
								
		
		if(!proxies.isEmpty()){
			Proxy[] array1 = new Proxy[proxies.size()];		 
			legacyCulturalHeritageObject.setProxies(proxies.toArray(array1));
		}
		
		if(!europeanaAggregations.isEmpty()){					
			EuropeanaAggregation[] array2 = new EuropeanaAggregation[europeanaAggregations.size()];		 
			legacyCulturalHeritageObject.setEuropeanaAggregation(europeanaAggregations.toArray(array2));
		}
		
		if(!aggregations.isEmpty()){
			Aggregation[] array3 = new Aggregation[aggregations.size()];		 
			legacyCulturalHeritageObject.setAggregations(aggregations.toArray(array3));			
		}
		
		if(!languages.isEmpty()){
			String[] array5 = new String[languages.size()];
			legacyCulturalHeritageObject.setLanguage(languages.toArray(array5));			
		}
		if(!titlesCho.isEmpty()){
			String[] array6 = new String[titlesCho.size()];
			legacyCulturalHeritageObject.setTitle(titlesCho.toArray(array6));					
		}
		
		// mapping TimeSpan fields to API Search Record model
		if(!culturalHeritageObject.getTemporal().isEmpty()){			
			List<Timespan> legacyTimespans = new ArrayList<Timespan>();
			List<String> prefLabels = new ArrayList<String>();
			List<String> altLabels = new ArrayList<String>();
			String lang = "";
																
			for(eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan t : culturalHeritageObject.getTemporal()){				
				Timespan legacyTimespan = new Timespan();
										
				boolean timespanHasEnglish = t.getLanguageAwareFields().stream().anyMatch(x -> x.getLanguage().equals("en"));				
				for(TimeSpanLangaugeAware timespanLangAware : t.getLanguageAwareFields()){					
					lang = timespanLangAware.getLanguage();
					prefLabels.add(timespanLangAware.getPreferredLabel());																				
															
					if(legacyTimespan.getPrefLabel().containsKey(lang)){					
						legacyTimespan.getPrefLabel().get(lang).add(timespanLangAware.getPreferredLabel());						
					}else{
						legacyTimespan.getPrefLabel().put(lang, prefLabels);
					}
					
					//adding default language values - if english exists then default language is english, otherwise first in list
					if(timespanHasEnglish){
						if(lang.equals("en")){							
							if(!legacyTimespan.getPrefLabel().containsKey(defaultLanguage)){
								legacyTimespan.getPrefLabel().put(defaultLanguage, prefLabels);
							}							
						}
					}else{						
						if(!legacyTimespan.getPrefLabel().containsKey(defaultLanguage)){
							legacyTimespan.getPrefLabel().put(defaultLanguage, prefLabels);
						}
					}
					
					if(altLabels.size() > 0){
						legacyTimespan.getAltLabel().put(lang, altLabels);
						//adding default language values - if english exists then default language is english, otherwise first in list
						if(timespanHasEnglish){
							if(lang.equals("en")){
								if(!legacyTimespan.getAltLabel().containsKey(defaultLanguage)){
									for(String timespanAltLabel : timespanLangAware.getAlternativeLabel()){
										altLabels.add(timespanAltLabel);
									}
									legacyTimespan.getAltLabel().put(defaultLanguage, altLabels);									
								}									
							}
						}else{
							if(!legacyTimespan.getAltLabel().containsKey(defaultLanguage)){
								for(String timespanAltLabel : timespanLangAware.getAlternativeLabel()){
									altLabels.add(timespanAltLabel);
								}
								legacyTimespan.getAltLabel().put(defaultLanguage, altLabels);								
							}												
						}
					}					
					altLabels = new ArrayList<String>();
					prefLabels = new ArrayList<String>();
					
					if(timespanLangAware.getCustomFields() != null && timespanLangAware.getCustomFields().size() > 0){
						for(KeyValuePair kv : timespanLangAware.getCustomFields()){
							if(kv.getKey().toLowerCase().equals("about")){
								legacyTimespan.setAbout(kv.getValue());
							}
						}
					}
					
				}
				legacyTimespans.add(legacyTimespan);
			}
			Timespan[] array = new Timespan[legacyTimespans.size()];		 
			legacyCulturalHeritageObject.setTimespans(legacyTimespans.toArray(array));
		}
		
		// mapping Place fields to API Search Record model
		if(!culturalHeritageObject.getSpatial().isEmpty()){			
			List<Place> legacyPlaces = new ArrayList<Place>();
			List<String> prefLabels = new ArrayList<String>();
			List<String> altLabels = new ArrayList<String>();
			List<String> notes = new ArrayList<String>();
			String lang = "";
			
			for(eu.europeana.direct.rest.gen.java.io.swagger.model.Place p : culturalHeritageObject.getSpatial()){	
				
				Place legacyPlace = new Place();				
				boolean placeHasEnglish = p.getLanguageAwareFields().stream().anyMatch(x -> x.getLanguage().equals("en"));
				
				for(PlaceLanguageAware placeLangAware : p.getLanguageAwareFields()){
			
					lang = placeLangAware.getLanguage();
					prefLabels.add(placeLangAware.getPreferredLabel());		
					//check if preflabel with same language already exists
					if(legacyPlace.getPrefLabel().containsKey(lang)){
						legacyPlace.getPrefLabel().get(lang).add(placeLangAware.getPreferredLabel());
					}else{
						legacyPlace.getPrefLabel().put(lang, prefLabels);
					}
															
					//adding default language values - if english exists then default language is english, otherwise first in list
					if(placeHasEnglish){
						if(lang.equals("en")){
							// check if map with defaultLanguage already exists
							if(!legacyPlace.getPrefLabel().containsKey(defaultLanguage)){
								legacyPlace.getPrefLabel().put(defaultLanguage, prefLabels);
							}														
						}
					}else{
						if(!legacyPlace.getPrefLabel().containsKey(defaultLanguage)){
							legacyPlace.getPrefLabel().put(defaultLanguage, prefLabels);
						}
					}
					
		
					if(placeLangAware.getAlternativeLabel() != null && placeLangAware.getAlternativeLabel().size() > 0){
						for(String placeAltLabel : placeLangAware.getAlternativeLabel()){
							altLabels.add(placeAltLabel);																			
							if(altLabels.size() > 0){							
								//check if altlabel with same language already exists
								if(legacyPlace.getAltLabel().containsKey(lang)){
									legacyPlace.getAltLabel().get(lang).add(placeAltLabel);									
								}else{
									legacyPlace.getAltLabel().put(lang, altLabels);
								}
								
								//adding default language values - if english exists then default language is english, otherwise first in list
								if(placeHasEnglish){
									if(lang.equals("en")){
										// check if map with defaultLanguage already exists
										if(!legacyPlace.getAltLabel().containsKey(defaultLanguage)){
											legacyPlace.getAltLabel().put(defaultLanguage, altLabels);
										}
									}
								}else{
									if(!legacyPlace.getAltLabel().containsKey(defaultLanguage)){
										legacyPlace.getAltLabel().put(defaultLanguage,altLabels);
									}															
								}
							}	
						}			
					}
					
					for(KeyValuePair keyValuePair : placeLangAware.getCustomFields()){
						if(keyValuePair.getKey().contains("Note")){
							notes.add(keyValuePair.getValue());
						}
					}																			
					if(notes.size() > 0){
						legacyPlace.getNote().put(lang, notes);	
						//adding default language values - if english exists then default language is english, otherwise first in list
						if(placeHasEnglish){
							if(lang.equals("en")){
								if(!legacyPlace.getNote().containsKey(defaultLanguage)){
									legacyPlace.getNote().put(defaultLanguage, notes);									
								}								
							}
						}else{
							if(!legacyPlace.getNote().containsKey(defaultLanguage)){
								legacyPlace.getNote().put(defaultLanguage, notes);
							}
						}
					}										
					notes = new ArrayList<String>();
					prefLabels = new ArrayList<String>();
					altLabels = new ArrayList<String>();
					
					if(placeLangAware.getCustomFields() != null && placeLangAware.getCustomFields().size() > 0){
						for(KeyValuePair kv : placeLangAware.getCustomFields()){
							if(kv.getKey().toLowerCase().equals("about")){
								legacyPlace.setAbout(kv.getValue());
							}
						}
					}									
				}
				
				
				if(p.getLanguageNonAwareFields().getAltitude() != null){
					legacyPlace.setAltitude(p.getLanguageNonAwareFields().getAltitude());	
				}				
				if(p.getLanguageNonAwareFields().getLatitude() != null){
					legacyPlace.setLatitude(p.getLanguageNonAwareFields().getLatitude());	
				} 
				if(p.getLanguageNonAwareFields().getLongitude() != null){
					legacyPlace.setLongitude(p.getLanguageNonAwareFields().getLongitude());	
				}
				if(p.getLanguageNonAwareFields().getLongitude() != null && p.getLanguageNonAwareFields().getLatitude() != null){
					legacyPlace.setPosition(new Position(p.getLanguageNonAwareFields().getLatitude()+","+p.getLanguageNonAwareFields().getLongitude()));	
				}
				legacyPlaces.add(legacyPlace);

			}
			Place[] array = new Place[legacyPlaces.size()];		 
			legacyCulturalHeritageObject.setPlaces(legacyPlaces.toArray(array));
		}
		

		// mapping Concept fields to API Search Record model
		if(!culturalHeritageObject.getConcepts().isEmpty()){			
			List<Concept> legacyConcepts = new ArrayList<Concept>();
			List<String> prefLabels = new ArrayList<String>();
			List<String> altLabels = new ArrayList<String>();
			String lang = "";
			
			for(eu.europeana.direct.rest.gen.java.io.swagger.model.Concept c : culturalHeritageObject.getConcepts()){				
				Concept legacyConcept = new Concept();

				boolean conceptHasEnglish = c.getLanguageAwareFields().stream().anyMatch(x -> x.getLanguage().equals("en"));				
				
				for(ConceptLanguageAware conceptLangAware : c.getLanguageAwareFields()){
			
					lang = conceptLangAware.getLanguage();
					prefLabels.add(conceptLangAware.getPreferredLabel());		
					
					if(legacyConcept.getPrefLabel().containsKey(lang)){
						legacyConcept.getPrefLabel().get(lang).add(conceptLangAware.getPreferredLabel());
					}else{
						legacyConcept.getPrefLabel().put(lang, prefLabels);
					}
										
					//adding default language values - if english exists then default language is english, otherwise first in list
					if(conceptHasEnglish){
						if(lang.equals("en")){
							if(!legacyConcept.getPrefLabel().containsKey(defaultLanguage)){
								legacyConcept.getPrefLabel().put(defaultLanguage, prefLabels);
							}							
						}
					}else{
						if(!legacyConcept.getPrefLabel().containsKey(defaultLanguage)){
							legacyConcept.getPrefLabel().put(defaultLanguage, prefLabels);
						}
					}
					
					
					if(conceptLangAware.getAlternativeLabel() != null && conceptLangAware.getAlternativeLabel().size() > 0){
						for(String conceptAltLabel : conceptLangAware.getAlternativeLabel()){
							altLabels.add(conceptAltLabel);												
						}
						
						if(altLabels.size() > 0){
							if(!legacyConcept.getAltLabel().containsKey(lang)){
								legacyConcept.getAltLabel().put(lang, altLabels);
							}
														
							//adding default language values - if english exists then default language is english, otherwise first in list
							if(conceptHasEnglish){
								if(lang.equals("en")){
									if(!legacyConcept.getAltLabel().containsKey(defaultLanguage)){
										legacyConcept.getAltLabel().put(defaultLanguage, altLabels);
									}																
								}
							}else{
								if(!legacyConcept.getAltLabel().containsKey(defaultLanguage)){
									legacyConcept.getAltLabel().put(defaultLanguage, altLabels);
								}
							}
						}	
					}
										
					altLabels = new ArrayList<String>();
					prefLabels = new ArrayList<String>();
					
					
					if(conceptLangAware.getCustomFields() != null && conceptLangAware.getCustomFields().size() > 0){
						for(KeyValuePair kv : conceptLangAware.getCustomFields()){
							if(kv.getKey().toLowerCase().equals("about")){
								legacyConcept.setAbout(kv.getValue());
							}
						}
					}
					
				}
							
				legacyConcepts.add(legacyConcept);

			}
			Concept[] array = new Concept[legacyConcepts.size()];		 
			legacyCulturalHeritageObject.setConcepts(legacyConcepts.toArray(array));
		}
		
		// mapping Agent fields to API Search Record model
		if(!culturalHeritageObject.getAgents().isEmpty()){
			List<Agent> legacyAgents = new ArrayList<Agent>();
			List<String> prefLabels = new ArrayList<String>();
			List<String> altLabels = new ArrayList<String>();
			List<String> listIdentifiers = new ArrayList<String>();
			List<String> listSameAs = new ArrayList<String>();

			String lang = "";
			for(eu.europeana.direct.rest.gen.java.io.swagger.model.Agent a : culturalHeritageObject.getAgents()){				
				Agent legacyAgent = new Agent();

				boolean agentHasEnglish = a.getLanguageAwareFields().stream().anyMatch(x -> x.getLanguage().equals("en"));
				
				for(AgentLanguageAware agentLangAware : a.getLanguageAwareFields()){
			
					lang = agentLangAware.getLanguage();
					prefLabels.add(agentLangAware.getPreferredLabel());
					
					if(legacyAgent.getPrefLabel().containsKey(lang)){
						legacyAgent.getPrefLabel().get(lang).add(agentLangAware.getPreferredLabel());
					}else{
						legacyAgent.getPrefLabel().put(lang, prefLabels);
					}
										
					//adding default language values - if english exists then default language is english, otherwise first in list
					if(agentHasEnglish){
						if(lang.equals("en")){
							if(!legacyAgent.getPrefLabel().containsKey(defaultLanguage)){
								legacyAgent.getPrefLabel().put(defaultLanguage, prefLabels);
							}						
						}
					}else{
						if(!legacyAgent.getPrefLabel().containsKey(defaultLanguage)){
							legacyAgent.getPrefLabel().put(defaultLanguage, prefLabels);
						}
					}
					
					
					if(agentLangAware.getAlternativeLabel() != null && agentLangAware.getAlternativeLabel().size() > 0){
						for(String agentAltLabel : agentLangAware.getAlternativeLabel()){
							altLabels.add(agentAltLabel);												
						}
						if(altLabels.size() > 0){
							
							if(!legacyAgent.getAltLabel().containsKey(lang)){
								legacyAgent.getAltLabel().put(lang, altLabels);
							}
							
							//adding default language values - if english exists then default language is english, otherwise first in list
							if(agentHasEnglish){
								if(lang.equals("en")){
									if(!legacyAgent.getAltLabel().containsKey(defaultLanguage)){
										legacyAgent.getAltLabel().put(defaultLanguage, altLabels);
									}
								}
							}else{
								if(!legacyAgent.getAltLabel().containsKey(defaultLanguage)){
									legacyAgent.getAltLabel().put(defaultLanguage, altLabels);
								}
							}
						}		
					}
													
					if(agentLangAware.getBiographicalInformation() != null && agentLangAware.getBiographicalInformation().length() > 0){						
						List<String> listBio = new ArrayList<String>();
						listBio.add(agentLangAware.getBiographicalInformation());
						
						if(legacyAgent.getRdaGr2BiographicalInformation().containsKey(lang)){
							legacyAgent.getRdaGr2BiographicalInformation().get(lang).add(agentLangAware.getBiographicalInformation());
						}else{
							legacyAgent.getRdaGr2BiographicalInformation().put(lang,listBio);
						}					
																	
						//adding default language values - if english exists then default language is english, otherwise first in list
						if(agentHasEnglish){
							if(lang.equals("en")){
								if(!legacyAgent.getRdaGr2BiographicalInformation().containsKey(defaultLanguage)){
									legacyAgent.getRdaGr2BiographicalInformation().put(defaultLanguage, listBio);
								}
							}
						}else{
							if(!legacyAgent.getRdaGr2BiographicalInformation().containsKey(defaultLanguage)){
								legacyAgent.getRdaGr2BiographicalInformation().put(defaultLanguage, listBio);
							}							
						}
					}
					
					if(agentLangAware.getGender() != null && agentLangAware.getGender().length() > 0){
						List<String> listGender = new ArrayList<String>();
						listGender.add(agentLangAware.getGender());
						
						if(legacyAgent.getRdaGr2Gender().containsKey(lang)){
							legacyAgent.getRdaGr2Gender().get(lang).add(agentLangAware.getGender());
						}else{
							legacyAgent.getRdaGr2Gender().put(lang,listGender);
						}
						
						//adding default language values - if english exists then default language is english, otherwise first in list
						if(agentHasEnglish){
							if(lang.equals("en")){
								if(!legacyAgent.getRdaGr2Gender().containsKey(defaultLanguage)){
									legacyAgent.getRdaGr2Gender().put(defaultLanguage, listGender);
								}	
							}
						}else{
							if(!legacyAgent.getRdaGr2Gender().containsKey(defaultLanguage)){
								legacyAgent.getRdaGr2Gender().put(defaultLanguage, listGender);
							}	
						}
					}
					
					
					
					if(agentLangAware.getIdentifier() != null && !agentLangAware.getIdentifier().isEmpty()){
						for(String identifier : agentLangAware.getIdentifier()){
							listIdentifiers.add(identifier);												
						}					
					}
					if(listIdentifiers.size() > 0){
						
						if(!legacyAgent.getDcIdentifier().containsKey(lang)){
							legacyAgent.getDcIdentifier().put(lang,listIdentifiers);																	
						}
												
						//adding default language values - if english exists then default language is english, otherwise first in list
						if(agentHasEnglish){
							if(lang.equals("en")){
								if(!legacyAgent.getDcIdentifier().containsKey(defaultLanguage)){
									legacyAgent.getDcIdentifier().put(defaultLanguage, listIdentifiers);									
								}									
							}
						}else{
							if(!legacyAgent.getDcIdentifier().containsKey(defaultLanguage)){
								legacyAgent.getDcIdentifier().put(defaultLanguage, listIdentifiers);
							}
						}
					}
									
					if(agentLangAware.getProfessionOrOccupation() != null && agentLangAware.getProfessionOrOccupation().length() > 0){
						List<String> proOccu = new ArrayList<String>();
						proOccu.add(agentLangAware.getProfessionOrOccupation());
						
						if(legacyAgent.getRdaGr2ProfessionOrOccupation().containsKey(lang)){
							legacyAgent.getRdaGr2ProfessionOrOccupation().get(lang).add(agentLangAware.getProfessionOrOccupation());
						}else{
							legacyAgent.getRdaGr2ProfessionOrOccupation().put(lang, proOccu);
						}
												
						//adding default language values - if english exists then default language is english, otherwise first in list
						if(agentHasEnglish){
							if(lang.equals("en")){
								if(!legacyAgent.getRdaGr2ProfessionOrOccupation().containsKey(defaultLanguage)){
									legacyAgent.getRdaGr2ProfessionOrOccupation().put(defaultLanguage, proOccu);
								}
							}
						}else{
							if(!legacyAgent.getRdaGr2ProfessionOrOccupation().containsKey(defaultLanguage)){
								legacyAgent.getRdaGr2ProfessionOrOccupation().put(defaultLanguage, proOccu);
							}
						}
					}
					
					if(agentLangAware.getSameAs() != null && !agentLangAware.getSameAs().isEmpty()){
						for(String sameAs : agentLangAware.getSameAs()){
							listSameAs.add(sameAs);
						}						
					}									
					if(listSameAs.size() > 0){
						String[] array = new String[listSameAs.size()];		 
						legacyAgent.setOwlSameAs(listSameAs.toArray(array));
					}									
					
					if(a.getLanguageNonAwareFields().getDateOfBirth() != null && a.getLanguageNonAwareFields().getDateOfBirth().length() > 0){
						List<String> listBirth = new ArrayList<String>();
						listBirth.add(a.getLanguageNonAwareFields().getDateOfBirth());
						
						if(legacyAgent.getRdaGr2DateOfBirth().containsKey(lang)){
							legacyAgent.getRdaGr2DateOfBirth().get(lang).add(a.getLanguageNonAwareFields().getDateOfBirth());
						}else{
							legacyAgent.getRdaGr2DateOfBirth().put(lang, listBirth);
						}
						
						if(legacyAgent.getBegin().containsKey(lang)){							
							legacyAgent.getBegin().get(lang).add(a.getLanguageNonAwareFields().getDateOfBirth());							
						}else{
							legacyAgent.getBegin().put(lang, listBirth);
						}
																
						
						//adding default language values - if english exists then default language is english, otherwise first in list
						if(agentHasEnglish){
							if(lang.equals("en")){
								if(!legacyAgent.getRdaGr2DateOfBirth().containsKey(defaultLanguage)){
									legacyAgent.getRdaGr2DateOfBirth().put(defaultLanguage, listBirth);
								}
								if(!legacyAgent.getBegin().containsKey(defaultLanguage)){
									legacyAgent.getBegin().put(defaultLanguage, listBirth);
								}
							}
						}else{
							if(!legacyAgent.getRdaGr2DateOfBirth().containsKey(defaultLanguage)){
								legacyAgent.getRdaGr2DateOfBirth().put(defaultLanguage, listBirth);
							}	
							if(!legacyAgent.getBegin().containsKey(defaultLanguage)){
								legacyAgent.getBegin().put(defaultLanguage, listBirth);
							}							
						}
					}
					
					if(a.getLanguageNonAwareFields().getDateOfDeath() != null && a.getLanguageNonAwareFields().getDateOfDeath().length() > 0){
						List<String> listDeath = new ArrayList<String>();
						listDeath.add(a.getLanguageNonAwareFields().getDateOfDeath());
						
						
						if(legacyAgent.getRdaGr2DateOfDeath().containsKey(lang)){							
							legacyAgent.getRdaGr2DateOfDeath().get(lang).add(a.getLanguageNonAwareFields().getDateOfDeath());

						}else{
							legacyAgent.getRdaGr2DateOfDeath().put(lang, listDeath);
						}
						
						if(legacyAgent.getEnd().containsKey(lang)){
							legacyAgent.getEnd().get(lang).add(a.getLanguageNonAwareFields().getDateOfDeath());																									
						}else{
							legacyAgent.getEnd().put(lang, listDeath);
						}
						
						//adding default language values - if english exists then default language is english, otherwise first in list
						if(agentHasEnglish){
							if(lang.equals("en")){
								if(!legacyAgent.getRdaGr2DateOfDeath().containsKey(defaultLanguage)){
									legacyAgent.getRdaGr2DateOfDeath().put(defaultLanguage, listDeath);	
								}
								
								if(!legacyAgent.getEnd().containsKey(defaultLanguage)){
									legacyAgent.getEnd().put(defaultLanguage, listDeath);	
								}
							}
						}else{
							if(!legacyAgent.getRdaGr2DateOfDeath().containsKey(defaultLanguage)){
								legacyAgent.getRdaGr2DateOfDeath().put(defaultLanguage, listDeath);	
							}
							if(!legacyAgent.getEnd().containsKey(defaultLanguage)){
								legacyAgent.getEnd().put(defaultLanguage, listDeath);	
							}						
						}
					}
					
					if(a.getLanguageNonAwareFields().getDateOfEstablishment() != null && a.getLanguageNonAwareFields().getDateOfEstablishment().length() > 0){
						List<String> listEst = new ArrayList<String>();
						listEst.add(a.getLanguageNonAwareFields().getDateOfEstablishment());
						
						if(legacyAgent.getRdaGr2DateOfEstablishment().containsKey(lang)){
							legacyAgent.getRdaGr2DateOfEstablishment().get(lang).add(a.getLanguageNonAwareFields().getDateOfEstablishment());	
						}else{
							legacyAgent.getRdaGr2DateOfEstablishment().put(lang, listEst);
						}
						
						//adding default language values - if english exists then default language is english, otherwise first in list
						if(agentHasEnglish){
							if(lang.equals("en")){
								if(!legacyAgent.getRdaGr2DateOfEstablishment().containsKey(defaultLanguage)){
									legacyAgent.getRdaGr2DateOfEstablishment().put(defaultLanguage, listEst);	
								}
							}
						}else{
							if(!legacyAgent.getRdaGr2DateOfEstablishment().containsKey(defaultLanguage)){
								legacyAgent.getRdaGr2DateOfEstablishment().put(defaultLanguage, listEst);	
							}
						}
					}
					
					if(a.getLanguageNonAwareFields().getDateOfTermination() != null && a.getLanguageNonAwareFields().getDateOfTermination().length() > 0){
						List<String> listTermination = new ArrayList<String>();
						listTermination.add(a.getLanguageNonAwareFields().getDateOfTermination());
						
						if(legacyAgent.getRdaGr2DateOfTermination().containsKey(lang)){
							legacyAgent.getRdaGr2DateOfTermination().get(lang).add(a.getLanguageNonAwareFields().getDateOfTermination());	
						}else{
							legacyAgent.getRdaGr2DateOfTermination().put(lang, listTermination);
						}
						
						//adding default language values - if english exists then default language is english, otherwise first in list
						if(agentHasEnglish){
							if(lang.equals("en")){
								if(!legacyAgent.getRdaGr2DateOfTermination().containsKey(defaultLanguage)){
									legacyAgent.getRdaGr2DateOfTermination().put(defaultLanguage, listTermination);	
								}
							}
						}else{
							if(!legacyAgent.getRdaGr2DateOfTermination().containsKey(defaultLanguage)){
								legacyAgent.getRdaGr2DateOfTermination().put(defaultLanguage, listTermination);
							}
						}
					}					
					listIdentifiers = new ArrayList<String>();
					altLabels = new ArrayList<String>();
					prefLabels = new ArrayList<String>();
					listSameAs = new ArrayList<String>();
					
					if(agentLangAware.getCustomFields() != null && agentLangAware.getCustomFields().size() > 0){
						for(KeyValuePair kv : agentLangAware.getCustomFields()){
							if(kv.getKey().toLowerCase().equals("about")){
								legacyAgent.setAbout(kv.getValue());
							}
						}
					}
					
				}
							
				legacyAgents.add(legacyAgent);

			}
			Agent[] array = new Agent[legacyAgents.size()];		 
			legacyCulturalHeritageObject.setAgents(legacyAgents.toArray(array));	
		}
		
		
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); 
		if(databaseCho.getEuropeanaDataObject() != null && databaseCho.getEuropeanaDataObject().getCreated() != null){
			String isoDateCreated = df.format(databaseCho.getEuropeanaDataObject().getCreated());
			legacyCulturalHeritageObject.setTimestamp_created(isoDateCreated);
			legacyCulturalHeritageObject.setTimestamp_created_epoch(databaseCho.getEuropeanaDataObject().getCreated().getTime());

		}
		
		
		if(databaseCho.getEuropeanaDataObject() != null && databaseCho.getEuropeanaDataObject().getModified() != null){
			String isoDateUpdate = df.format(databaseCho.getEuropeanaDataObject().getModified());
			legacyCulturalHeritageObject.setTimestamp_update(isoDateUpdate);
			legacyCulturalHeritageObject.setTimestamp_update_epoch(databaseCho.getEuropeanaDataObject().getModified().getTime());
		}

		legacyCulturalHeritageObject.setAbout(objectId);
		if(culturalHeritageObject.getLanguageNonAwareFields().getType() != null)
		{
			legacyCulturalHeritageObject.setType(culturalHeritageObject.getLanguageNonAwareFields().getType().toString());			
		}
		else
		{
			legacyCulturalHeritageObject.setType("IMAGE");
		}
		
		return legacyCulturalHeritageObject;

		}finally{
			choLogic.close();	
		}
		
	}
}
