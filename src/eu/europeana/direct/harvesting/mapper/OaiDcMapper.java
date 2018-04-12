package eu.europeana.direct.harvesting.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import eu.europeana.direct.harvesting.source.edm.model.EdmOaiSource;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageNonAware.TypeEnum;
import eu.europeana.direct.rest.gen.java.io.swagger.model.KeyValuePair;

public class OaiDcMapper implements IMapper<EdmOaiSource> {

	//number of errors appeared while saving CHO in database
	private int errorCount = 0;
	//number of warnings appeared while saving CHO in database
	private int warningCount = 0;
	//flag for interrupting job
	private boolean interrupted = false;
	private List<CulturalHeritageObject> culturalHeritageObjects;
	private List<String> errorMessages;
	private List<String> warningMessages;
	final static Logger logger = Logger.getLogger(OaiDcMapper.class);

	@Override
	public CulturalHeritageObject mapFromEdm(EdmOaiSource edmSource) {								
		
		culturalHeritageObjects = new ArrayList<CulturalHeritageObject>();
		errorMessages = new ArrayList<String>();
		warningMessages = new ArrayList<String>();
		
		CulturalHeritageObject culturalHeritageObject = new CulturalHeritageObject();
		List<CulturalHeritageObjectLanguageAware> culturalHeritageObjectLanguageAwareList = new ArrayList<CulturalHeritageObjectLanguageAware>();
		CulturalHeritageObjectLanguageAware culturalHeritageObjectLanguageAware = new CulturalHeritageObjectLanguageAware();
		CulturalHeritageObjectLanguageNonAware culturalHeritageObjectLanguageNonAware = new CulturalHeritageObjectLanguageNonAware();
				
			try {													
				String language = null;											
				
				//CHO LANGUAGE AWARE MAPPING
				if(edmSource.getProvidedCHO().getOaiDc().getTitle().size() > 0){			
					if(!edmSource.getProvidedCHO().getOaiDc().getLanguage().isEmpty()){
						language = edmSource.getProvidedCHO().getOaiDc().getLanguage().get(0);
					}					
					
					for (Map.Entry<String, String> choLanguageAwareTitleList : edmSource.getProvidedCHO().getOaiDc().getTitle()
							.entrySet()) {																			
																	
						culturalHeritageObjectLanguageAware.setTitle(choLanguageAwareTitleList.getValue());												
						boolean hasLangAttribute = false;
						
						/* check if title element has xml:lang attribute, then key should be 2 letter iso code.
						 * Else key value is: "key {number}"
						 */	
						if(choLanguageAwareTitleList.getKey().length() == 2){
							hasLangAttribute = true;			
							culturalHeritageObjectLanguageAware.setLanguage(choLanguageAwareTitleList.getKey());							
						} else {
							//if not check for element language
							if(language == null){
								errorCount++;
								errorMessages.add("ERROR: Cannot map Cultural heritage object, required field Language is missing");
							}else{
								culturalHeritageObjectLanguageAware.setLanguage(language);															
							}
						}
						
						if (edmSource.getProvidedCHO().getOaiDc().getDescription().size() > 0) {									
							for (Map.Entry<String, List<String>> descriptionMap : edmSource.getProvidedCHO().getOaiDc().getDescription()
									.entrySet()) {																
															
								// if title doesn't have xml:lang attribute neither does description
								if(!hasLangAttribute){
									for(String d : descriptionMap.getValue()){
										culturalHeritageObjectLanguageAware.setDescription(d);	
									}																		
								}else{
									// find description with same xml:lang attribute as title
									if (descriptionMap.getKey() == choLanguageAwareTitleList.getKey()) {
										for(String d : descriptionMap.getValue()){
											culturalHeritageObjectLanguageAware.setDescription(d);	
										}														
									}
								}														
							}								
						} else {
							// if there is no description element in metadata, title value is set for description of CHO
							culturalHeritageObjectLanguageAware.setDescription(choLanguageAwareTitleList.getValue());
						}
						
						if (edmSource.getProvidedCHO().getOaiDc().getFormat().size() > 0) {									
							for (Map.Entry<String, List<String>> formatMap : edmSource.getProvidedCHO().getOaiDc().getFormat()
									.entrySet()) {															
								
								// if title doesn't have xml:lang attribute neither does description
								if(!hasLangAttribute){
									for(String f : formatMap.getValue()){
										culturalHeritageObjectLanguageAware.getFormat().add(f);	
									}																		
								} else {
									// find format with same xml:lang attribute as title
									if (formatMap.getKey() == choLanguageAwareTitleList.getKey()) {
										for(String f : formatMap.getValue()){
											culturalHeritageObjectLanguageAware.getFormat().add(f);	
										}												
									}
								}														
							}								
						}
						
						if (edmSource.getProvidedCHO().getOaiDc().getPublisher().size() > 0) {									
							for (Map.Entry<String, List<String>> publisherMap : edmSource.getProvidedCHO().getOaiDc().getPublisher()
									.entrySet()) {																															
								
								// if title doesn't have xml:lang attribute neither does publisher
								if(!hasLangAttribute){
									for(String pub : publisherMap.getValue()){
										culturalHeritageObjectLanguageAware.getPublisher().add(pub);	
									}																		
								} else {
									// find format with same xml:lang attribute as title
									if (publisherMap.getKey() == choLanguageAwareTitleList.getKey()) {
										for(String pub : publisherMap.getValue()){
											culturalHeritageObjectLanguageAware.getPublisher().add(pub);	
										}
									}
								}														
							}								
						}
						
						if (edmSource.getProvidedCHO().getOaiDc().getSource().size() > 0) {									
							for (Map.Entry<String, List<String>> sourceMap : edmSource.getProvidedCHO().getOaiDc().getSource()
									.entrySet()) {																					
								
								// if title doesn't have xml:lang attribute neither does source
								if(!hasLangAttribute){
									for(String s : sourceMap.getValue()){
										culturalHeritageObjectLanguageAware.getSource().add(s);	
									}																		
								} else {
									// find source with same xml:lang attribute as title
									if (sourceMap.getKey() == choLanguageAwareTitleList.getKey()) {
										for(String s : sourceMap.getValue()){
											culturalHeritageObjectLanguageAware.getSource().add(s);	
										}														
									}
								}														
							}								
						}
										
						
						if (edmSource.getProvidedCHO().getOaiDc().getDate().size() > 0) {									
							for (Map.Entry<String, List<String>> createdMap : edmSource.getProvidedCHO().getOaiDc().getDate()
									.entrySet()) {								
								
								// if title doesn't have xml:lang attribute neither does date
								if(!hasLangAttribute){
									for(String c : createdMap.getValue()){
										culturalHeritageObjectLanguageAware.setCreated(c);	
									}																		
								} else {
									// find source with same xml:lang attribute as title
									if (createdMap.getKey() == choLanguageAwareTitleList.getKey()) {
										for(String c : createdMap.getValue()){
											culturalHeritageObjectLanguageAware.setCreated(c);	
										}	
									}
								}														
							}								
						}																																						
						
						/*if (edmSource.getProvidedCHO().getOaiDc().getRights().size() > 0) {				
							int counter = 0;
							for (Map.Entry<String, List<String>> rightsMap : edmSource.getProvidedCHO().getOaiDc().getRights()
									.entrySet()) {															
								if (interrupted) {
									break mainloop;
								}
								
								// if title doesn't have xml:lang attribute neither does rights
								if(!hasLangAttribute){
									keyValuePair = new KeyValuePair();
									keyValuePair.setKey("Rights "+(++counter));
									keyValuePair.setValue(rightsMap.getValue());
									culturalHeritageObjectLanguageAware.getCustomFields().add(keyValuePair);										
								} else {
									// find source with same xml:lang attribute as title
									if (rightsMap.getKey() == choLanguageAwareTitleList.getKey()) {
										keyValuePair = new KeyValuePair();
										keyValuePair.setKey("Rights "+(++counter));
										keyValuePair.setValue(rightsMap.getValue());
										culturalHeritageObjectLanguageAware.getCustomFields().add(keyValuePair);
									}
								}														
							}								
						}*/
						
						culturalHeritageObjectLanguageAwareList.add(culturalHeritageObjectLanguageAware);
					}
										
				}else{
					errorCount++;
					errorMessages.add("ERROR: Cannot map Cultural heritage object, required field (Title, Language) is missing");
				}
				
				//CHO LANGUAGE NON AWARE MAPPING								
				if (!edmSource.getProvidedCHO().getOaiDc().getIdentifier().isEmpty())
					culturalHeritageObjectLanguageNonAware.setIdentifier(edmSource.getProvidedCHO().getOaiDc().getIdentifier());
				
				if (!edmSource.getProvidedCHO().getOaiDc().getRelation().isEmpty())
					culturalHeritageObjectLanguageNonAware.setRelation(edmSource.getProvidedCHO().getOaiDc().getRelation());								
								
				culturalHeritageObjectLanguageNonAware.setType(TypeEnum.IMAGE);

				if (edmSource.getProvidedCHO().getOaiDc().getType().size() > 0) {				
					for (Map.Entry<String, List<String>> typeMap : edmSource.getProvidedCHO().getOaiDc().getType()
							.entrySet()) {										
												
						for(String type : typeMap.getValue()){
							switch (type.toUpperCase()) {

							case "IMAGE":
								culturalHeritageObjectLanguageNonAware.setType(TypeEnum.IMAGE);
								break;
							case "AUDIO":
								culturalHeritageObjectLanguageNonAware.setType(TypeEnum.AUDIO);
								break;
							case "VIDEO":
								culturalHeritageObjectLanguageNonAware.setType(TypeEnum.VIDEO);
								break;
							case "3D":
								culturalHeritageObjectLanguageNonAware.setType(TypeEnum._3D);
								break;
							case "TEXT":
								culturalHeritageObjectLanguageNonAware.setType(TypeEnum.IMAGE);
								break;
							default:
								culturalHeritageObjectLanguageNonAware.setType(TypeEnum.IMAGE);
								break;
							}
						}	
					}							
				}
				
				/*if(!edmSource.getProvidedCHO().getOaiDc().getAudience().isEmpty()){
					int counter = 0;
					for(int index = 0; index < edmSource.getProvidedCHO().getOaiDc().getAudience().size(); index++){									
						if (interrupted) {
							break mainloop;
						}						
						keyValuePair = new KeyValuePair();
						keyValuePair.setKey("Audience "+(++counter));
						keyValuePair.setValue(edmSource.getProvidedCHO().getOaiDc().getAudience().get(index));
						culturalHeritageObjectLanguageNonAware.getCustomFields().add(keyValuePair);
					}
				}*/
				
				List<AgentLanguageAware> listAgentLanguageAware;
				Agent agent;
				AgentLanguageAware agentLanguageAware;
				AgentLanguageNonAware agentLanguageNonAware;
				List<Agent> agentsList = new ArrayList<>();
				
				if(!edmSource.getProvidedCHO().getOaiDc().getCreator().isEmpty()){					
					for (Entry<String, List<String>> entry : edmSource.getProvidedCHO().getOaiDc().getCreator()
							.entrySet()) {						

						for (String prefLabel : entry.getValue()) {
							listAgentLanguageAware = new ArrayList<AgentLanguageAware>();
							agent = new Agent();
							agentLanguageNonAware = new AgentLanguageNonAware();
							agentLanguageAware = new AgentLanguageAware();

							agentLanguageAware.setPreferredLabel(prefLabel);
							if(entry.getKey().length() == 2){
								agentLanguageAware.setLanguage(entry.getKey());
							}else{
								if(language != null){
									agentLanguageAware.setLanguage(language);
								}else{
									agentLanguageAware.setLanguage("en");		
								}
							}							
							agentLanguageNonAware.setRole("dc:Creator");
							agent.setLanguageNonAwareFields(agentLanguageNonAware);
							listAgentLanguageAware.add(agentLanguageAware);
							agent.setLanguageAwareFields(listAgentLanguageAware);
							agentsList.add(agent);
						}											
					}																												
				}
				
				List<ConceptLanguageAware> listConceptLanguageAware;
				Concept concept;
				ConceptLanguageNonAware conceptLanguageNonAware;
				ConceptLanguageAware conceptLanguageAware;
				List<Concept> conceptsList = new ArrayList<>();
				if(!edmSource.getProvidedCHO().getOaiDc().getSubject().isEmpty()){					
					for (Entry<String, List<String>> entry : edmSource.getProvidedCHO().getOaiDc().getSubject()
							.entrySet()) {					

						for (String prefLabel : entry.getValue()) {
							listConceptLanguageAware = new ArrayList<ConceptLanguageAware>();
							concept = new Concept();
							conceptLanguageNonAware = new ConceptLanguageNonAware();
							conceptLanguageAware = new ConceptLanguageAware();

							conceptLanguageAware.setPreferredLabel(prefLabel);
							if(entry.getKey().length() == 2){
								conceptLanguageAware.setLanguage(entry.getKey());
							}else{
								if(language != null){
									conceptLanguageAware.setLanguage(language);
								}else{
									conceptLanguageAware.setLanguage("en");		
								}
							}							
							conceptLanguageNonAware.setRole("dc:Subject");
							concept.setLanguageNonAwareFields(conceptLanguageNonAware);
							listConceptLanguageAware.add(conceptLanguageAware);
							concept.setLanguageAwareFields(listConceptLanguageAware);
							conceptsList.add(concept);
						}											
					}																												
				}
				
				
				if (edmSource.getProvidedCHO().getOaiDc().getType().size() > 0) {				
					typeloop:for (Map.Entry<String, List<String>> typeMap : edmSource.getProvidedCHO().getOaiDc().getType()
							.entrySet()) {
															
						String typeLang = "";
						if(typeMap.getKey().length() == 3){
							typeLang = typeMap.getKey();
						}
						// if title doesn't have xml:lang attribute neither does type
						if(typeMap.getKey().length() == 2){
							
												
							for(String prefL : typeMap.getValue()){
							
								conceptLanguageAware = new ConceptLanguageAware();
								listConceptLanguageAware = new ArrayList<ConceptLanguageAware>();						
								concept = new Concept();
								conceptLanguageNonAware = new ConceptLanguageNonAware();
								conceptLanguageAware = new ConceptLanguageAware();

								conceptLanguageNonAware.setRole("dc:Type");
								
								conceptLanguageAware.setLanguage(typeMap.getKey());	
								
								conceptLanguageAware.setPreferredLabel(prefL);
								concept.setLanguageNonAwareFields(conceptLanguageNonAware);
								listConceptLanguageAware.add(conceptLanguageAware);
								concept.setLanguageAwareFields(listConceptLanguageAware);
								conceptsList.add(concept);
							}																																										
						} else {															
							for(String prefL : typeMap.getValue()){	
								conceptLanguageAware = new ConceptLanguageAware();
								listConceptLanguageAware = new ArrayList<ConceptLanguageAware>();						
								concept = new Concept();
								conceptLanguageNonAware = new ConceptLanguageNonAware();
								conceptLanguageAware = new ConceptLanguageAware();
								
								if(typeLang.length() > 1){
									conceptLanguageAware.setLanguage(typeLang);
								}else if(language != null){
									conceptLanguageAware.setLanguage(language);															
								}else{
									conceptLanguageAware.setLanguage("en");
								}											
								
								conceptLanguageNonAware.setRole("dc:Type");								
								conceptLanguageAware.setPreferredLabel(prefL);
								concept.setLanguageNonAwareFields(conceptLanguageNonAware);
								listConceptLanguageAware.add(conceptLanguageAware);
								concept.setLanguageAwareFields(listConceptLanguageAware);									
								conceptsList.add(concept);
							}						
								
						}							
					}								
				}
				
				
				/*if(!edmSource.getProvidedCHO().getOaiDc().getContributor().isEmpty()){
					int counter = 0;
					for(int index = 0; index < edmSource.getProvidedCHO().getOaiDc().getContributor().size(); index++){											
						if (interrupted) {
							break mainloop;
						}
						keyValuePair = new KeyValuePair();
						keyValuePair.setKey("Contributor "+(++counter));
						keyValuePair.setValue(edmSource.getProvidedCHO().getOaiDc().getContributor().get(index));
						culturalHeritageObjectLanguageNonAware.getCustomFields().add(keyValuePair);
					}
				}*/
				/*if(!edmSource.getProvidedCHO().getOaiDc().getCoverage().isEmpty()){
					int counter = 0;
					for(int index = 0; index < edmSource.getProvidedCHO().getOaiDc().getCoverage().size(); index++){								
						if (interrupted) {
							break mainloop;
						}
						keyValuePair = new KeyValuePair();
						keyValuePair.setKey("Coverage "+(++counter));
						keyValuePair.setValue(edmSource.getProvidedCHO().getOaiDc().getCoverage().get(index));
						culturalHeritageObjectLanguageNonAware.getCustomFields().add(keyValuePair);
					}
				}*/
						

			culturalHeritageObject.setLanguageAwareFields(culturalHeritageObjectLanguageAwareList);
			culturalHeritageObject.setLanguageNonAwareFields(culturalHeritageObjectLanguageNonAware);
			if (agentsList.size() > 0) {
				culturalHeritageObject.setAgents(agentsList);
			}

			if (conceptsList.size() > 0) {
				culturalHeritageObject.setConcepts(conceptsList);
			}
			
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		
		return culturalHeritageObject;
	}
	
	@Override
	public int getErrors() {
		return errorCount;
	}

	@Override
	public void setInterrupt(boolean interrupt) {
		interrupted = interrupt;
	}

	@Override
	public int getSuccesses() {
		// TODO Auto-generated method stub
		return culturalHeritageObjects.size();
	}

	@Override
	public int getWarnings() {
		// TODO Auto-generated method stub
		return warningCount;
	}

	@Override
	public List<String> getErrorMessages() {
		return errorMessages;
	}

	@Override
	public List<String> getWarningMessages() {
		return warningMessages;
	}
}
