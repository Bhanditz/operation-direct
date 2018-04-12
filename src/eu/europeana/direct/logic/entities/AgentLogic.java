package eu.europeana.direct.logic.entities;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.AgentLangAware;
import eu.europeana.direct.backend.model.AgentLangNonAware;
import eu.europeana.direct.backend.model.EuropeanaDataObject;
import eu.europeana.direct.backend.repositories.AgentRepository;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.messaging.MessageProducer;
import eu.europeana.direct.messaging.MessageType;
import eu.europeana.direct.messaging.add.IndexMessage;
import eu.europeana.direct.messaging.delete.DeleteIndexMessage;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.KeyValuePair;

public class AgentLogic {
	
	private final AgentRepository repository;
	private final static Logger logger = Logger.getLogger(AgentLogic.class);	

	public AgentLogic() {
		repository = new AgentRepository();
	}

	public AgentLogic(EntityManager manager) {
		repository = new AgentRepository(manager);
	}	

	/**
	 * Maps database Agent model to Operation Direct Agent model
	 * @param databaseCho Database Agent model
	 * @return Operation Direct Agent model
	 */
	public Agent mapFromDatabase(
			eu.europeana.direct.backend.model.Agent databaseCho) {		
		
		Agent agentMapped = new Agent();		
		agentMapped.setId(new BigDecimal(databaseCho.getId()));

		//mapping agent lang non aware
		agentMapped.setLanguageNonAwareFields(new AgentLanguageNonAware());
		if(databaseCho.getLanguageNonAwareFields() != null){
			if(databaseCho.getLanguageNonAwareFields().getDateOfBirth() != null){
				agentMapped.getLanguageNonAwareFields().setDateOfBirth(databaseCho.getLanguageNonAwareFields().getDateOfBirth());	
			}
			
			if(databaseCho.getLanguageNonAwareFields().getDateOfDeath() != null){
				agentMapped.getLanguageNonAwareFields().setDateOfDeath(databaseCho.getLanguageNonAwareFields().getDateOfDeath());	
			}
			
			if(databaseCho.getLanguageNonAwareFields().getDateOfEstablishment() != null){
				agentMapped.getLanguageNonAwareFields().setDateOfEstablishment(databaseCho.getLanguageNonAwareFields().getDateOfEstablishment());	
			}
					
			if(databaseCho.getLanguageNonAwareFields().getDateOfTermination() != null){
				agentMapped.getLanguageNonAwareFields().setDateOfTermination(databaseCho.getLanguageNonAwareFields().getDateOfTermination());	
			}				
		}				

		// mapping non aware custom fields
		if(databaseCho.getEuropeanaDataObject().getNonAwareCustomFields() != null){
			if(!databaseCho.getEuropeanaDataObject().getNonAwareCustomFields().isEmpty()){
				for (Map.Entry<String, String> entry : databaseCho.getEuropeanaDataObject().getNonAwareCustomFields().entrySet()) {
					
					agentMapped.getLanguageNonAwareFields().getCustomFields().add(new KeyValuePair(entry.getKey(), entry.getValue()));
				}
			}
		}				

		List<AgentLanguageAware> listLangs = new ArrayList<AgentLanguageAware>();		
		if (databaseCho.getLanguageAwareFields() != null && !databaseCho.getLanguageAwareFields().isEmpty()) {
			// maps agent language aware model to Operation Direct Agent language aware fields
			
			// Language Aware fields			
			for (Map.Entry<String, List<AgentLangAware>> entry : databaseCho.getLanguageAwareFields().entrySet()) {
				
				for(AgentLangAware agentAware : entry.getValue()){
					
					//application agent lang aware model
					AgentLanguageAware langAwareFields = new AgentLanguageAware();
								
					langAwareFields.setLanguage(entry.getKey());

					if (agentAware.getAlternativeLabel() != null) {
						langAwareFields.setAlternativeLabel(Arrays.asList(agentAware.getAlternativeLabel()));
					}

					if (agentAware.getBiographicalInformation() != null
							&& agentAware.getBiographicalInformation().length > 0) {
						langAwareFields.setBiographicalInformation(agentAware.getBiographicalInformation()[0]);
					}

					if (agentAware.getIdentifier() != null) {
						langAwareFields.setIdentifier(Arrays.asList(agentAware.getIdentifier()));
					}

					if (agentAware.getGender() != null) {
						langAwareFields.setGender(agentAware.getGender());
					}

					if (agentAware.getPreferredLabel() != null && agentAware.getPreferredLabel().length > 0) {
						langAwareFields.setPreferredLabel(agentAware.getPreferredLabel()[0]);
					}

					if (agentAware.getPlaceOfBirth() != null && agentAware.getPlaceOfBirth().length > 0) {
						langAwareFields.setPlaceOfBirth(agentAware.getPlaceOfBirth()[0]);
					}

					if (agentAware.getPlaceOfDeath() != null && agentAware.getPlaceOfDeath().length > 0) {
						langAwareFields.setPlaceOfDeath(agentAware.getPlaceOfDeath()[0]);
					}

					if (agentAware.getProfessionOrOccupation() != null
							&& agentAware.getProfessionOrOccupation().length > 0) {
						langAwareFields.setProfessionOrOccupation(agentAware.getProfessionOrOccupation()[0]);
					}

					if (agentAware.getSameAs() != null) {
						langAwareFields.setSameAs(Arrays.asList(agentAware.getSameAs()));
					}

					// mapping lang aware custom fields
					if(databaseCho.getEuropeanaDataObject().getAwareCustomFields() != null){
						if(!databaseCho.getEuropeanaDataObject().getAwareCustomFields().isEmpty()){					
							// check if custom fields exists for this language
							if(databaseCho.getEuropeanaDataObject().getAwareCustomFields().containsKey(entry.getKey())){
								// get map of custom fields for this language
								for (Map.Entry<String,String> customf : databaseCho.getEuropeanaDataObject().getAwareCustomFields().get(entry.getKey()).entrySet()) {
									langAwareFields.getCustomFields().add(new KeyValuePair(customf.getKey(), customf.getValue()));
								}
							}										
						}	
					}					
					listLangs.add(langAwareFields);
				}															
			}			
			agentMapped.setLanguageAwareFields(listLangs);
		}

		return agentMapped;				
	}

	/**
	 * Maps Operation Direct Agent model to database Agent model
	 * @param directAgent Operation Direct Agent model
	 * @return Database Agent model
	 */
	public eu.europeana.direct.backend.model.Agent mapToDatabase(
			Agent directAgent) throws Exception{
		
		eu.europeana.direct.backend.model.Agent agentMapped = new eu.europeana.direct.backend.model.Agent();
		// get availible languages from DB
		
		boolean update = false;
		if (directAgent.getId() != null && directAgent.getId().longValueExact() > 0) {
			update = true;
			agentMapped = repository.getAgent(directAgent.getId().longValueExact());
			if (agentMapped == null) {
				throw new Exception("Agent " + directAgent.getId() + " does not exists!");
			}
			agentMapped.getEuropeanaDataObject().setModified(new Date());
		} else {
			EuropeanaDataObject edoNew = new EuropeanaDataObject();
			edoNew.setCreated(new Date());
			edoNew.setModified(new Date());			
			agentMapped.setEuropeanaDataObject(edoNew);
		}

		if (directAgent.getLanguageNonAwareFields() != null) {
			
			// domain agent lang non aware
			AgentLangNonAware agentLangNonAware = new AgentLangNonAware();
			
			if (directAgent.getLanguageNonAwareFields().getDateOfBirth() != null && directAgent.getLanguageNonAwareFields().getDateOfBirth().length() > 0) {
				agentLangNonAware.setDateOfBirth(directAgent.getLanguageNonAwareFields().getDateOfBirth());
			} else {
				agentLangNonAware.setDateOfBirth(null);
			}

			if (directAgent.getLanguageNonAwareFields().getDateOfDeath() != null
					&& directAgent.getLanguageNonAwareFields().getDateOfDeath().length() > 0) {
				agentLangNonAware.setDateOfDeath(directAgent.getLanguageNonAwareFields().getDateOfDeath());
			} else {
				agentLangNonAware.setDateOfDeath(null);
			}

			if (directAgent.getLanguageNonAwareFields().getDateOfEstablishment() != null
					&& directAgent.getLanguageNonAwareFields().getDateOfEstablishment().length() > 0) {
				agentLangNonAware.setDateOfEstablishment(directAgent.getLanguageNonAwareFields().getDateOfEstablishment());
			} else {
				agentLangNonAware.setDateOfEstablishment(null);
			}

			if (directAgent.getLanguageNonAwareFields().getDateOfTermination() != null
					&& directAgent.getLanguageNonAwareFields().getDateOfTermination().length() > 0) {
				agentLangNonAware.setDateOfTermination(directAgent.getLanguageNonAwareFields().getDateOfTermination());
			} else {
				agentLangNonAware.setDateOfTermination(null);
			}
			
			// set language non aware property
			agentMapped.setLanguageNonAwareFields(agentLangNonAware);
		}

		// map non aware custom fields
		if (directAgent.getLanguageNonAwareFields() != null) {
			if (directAgent.getLanguageNonAwareFields().getCustomFields() != null && directAgent.getLanguageNonAwareFields().getCustomFields().size() > 0) {												
				for (KeyValuePair kv : directAgent.getLanguageNonAwareFields().getCustomFields()) {
					if(kv.getKey() != null) {
						agentMapped.getEuropeanaDataObject().getNonAwareCustomFields().put(kv.getKey(), kv.getValue());	
					} else {
						throw new Exception("Key of one of the custom field for Agent language non aware is null");
					}											
				}
			}
		}		

		if (directAgent.getLanguageAwareFields() != null && directAgent.getLanguageAwareFields().size() > 0) {

			// if updating, clear previous lang aware fields
			if(update){
				agentMapped.getLanguageAwareFields().clear();
			}
			
			// maps Operation Direct Agent Language Aware fields to database
			// Agent translation model
			for (AgentLanguageAware langAwareFields : directAgent.getLanguageAwareFields()) {
				String lang = null;

				// domain agent lang aware										
				AgentLangAware domainLangAware = new AgentLangAware();
				
				// language is required, if null skip mapping this agent
				if (langAwareFields.getLanguage() != null) {
					// finds Language object based on isocode

					lang = langAwareFields.getLanguage();
				} else {
					lang = "en";
					//throw new Exception("Required field language is missing for Agent language aware entity");
				}

				if (langAwareFields.getAlternativeLabel() != null && langAwareFields.getAlternativeLabel().size() > 0) {					
					domainLangAware.setAlternativeLabel(langAwareFields.getAlternativeLabel().toArray(new String[langAwareFields.getAlternativeLabel().size()]));
				}

				if (langAwareFields.getProfessionOrOccupation() != null && langAwareFields.getProfessionOrOccupation().length() > 0) {
					String[] prof = new String[1];
					prof[0] = langAwareFields.getProfessionOrOccupation();
					domainLangAware.setProfessionOrOccupation(prof);
				}

				if (langAwareFields.getBiographicalInformation() != null && langAwareFields.getBiographicalInformation().length() > 0) {
					String[] bioInf = new String[1];
					bioInf[0] = langAwareFields.getBiographicalInformation();
					domainLangAware.setBiographicalInformation(bioInf);
				}

				if (langAwareFields.getGender() != null && langAwareFields.getGender().length() > 0) {
					domainLangAware.setGender(langAwareFields.getGender());
				}

				if (langAwareFields.getIdentifier() != null && langAwareFields.getIdentifier().size() > 0) {
					domainLangAware.setIdentifier(langAwareFields.getIdentifier().toArray(new String[0]));
				}

				if (langAwareFields.getPlaceOfBirth() != null && langAwareFields.getPlaceOfBirth().length() > 0) {
					String[] placeOfBirth = new String[1];
					placeOfBirth[0] = langAwareFields.getPlaceOfBirth();
					domainLangAware.setPlaceOfBirth(placeOfBirth);
				}

				if (langAwareFields.getPlaceOfDeath() != null && langAwareFields.getPlaceOfDeath().length() > 0) {
					String[] placeOfDeath = new String[1];
					placeOfDeath[0] = langAwareFields.getPlaceOfDeath();
					domainLangAware.setPlaceOfDeath(placeOfDeath);
				}
				// preferred label is required, if null skip mapping this agent
				if (langAwareFields.getPreferredLabel() != null && langAwareFields.getPreferredLabel().trim().length() > 0) {
					String[] prefLabel = new String[1];
					prefLabel[0] = langAwareFields.getPreferredLabel();
					domainLangAware.setPreferredLabel(prefLabel);
				} else {
					throw new Exception("Required field preferred label is missing for Agent language aware entity");
				}

				if (langAwareFields.getSameAs() != null && langAwareFields.getSameAs().size() > 0) {
					domainLangAware.setSameAs(langAwareFields.getSameAs().toArray(new String[langAwareFields.getSameAs().size()]));
				}

				if (langAwareFields.getCustomFields() != null && langAwareFields.getCustomFields().size() > 0) {
					Map<String,String> customfields = new HashMap<String,String>();
					
					for (KeyValuePair kv : langAwareFields.getCustomFields()) {						
						if(kv.getKey() != null) {
							customfields.put(kv.getKey(),kv.getValue());	
						} else {
							throw new Exception("Key of one of the custom field for Agent language aware is null");
						}						
					}
					if(!customfields.isEmpty()){						
						agentMapped.getEuropeanaDataObject().getAwareCustomFields().put(langAwareFields.getLanguage(), customfields);
					}
				}

				// for every language add language aware object
				if(agentMapped.getLanguageAwareFields().containsKey(lang)){
					agentMapped.getLanguageAwareFields().get(lang).add(domainLangAware);
				}else{					
					List<AgentLangAware> list = new ArrayList<AgentLangAware>();
					list.add(domainLangAware);
					agentMapped.getLanguageAwareFields().put(lang,list);	
				}
												
			}
		} else {
			throw new Exception("Required field languageAwareFields is missing from Agent entity.");
		}		
		
		return agentMapped;		
	}
	
	/**
	 * Returns Operation Direct Agent
	 * @param id Unique ID of agent
	 * @return
	 */
	public Agent getAgent(long id) {
		eu.europeana.direct.backend.model.Agent cho = repository.getAgent(id);
		if (cho == null) {
			return null;
		} else {
			Agent result = mapFromDatabase(cho);
			return result;
		}
	}
	
	/**
	 * Returns database Agent
	 * @param id Unique ID of agent
	 * @return
	 */
	public eu.europeana.direct.backend.model.Agent getAgentDb(long id) {		
		eu.europeana.direct.backend.model.Agent cho = repository.getAgent(id);
		if (cho == null) {
			return null;
		} else {
			return cho;
		}		
	}
	
	
	/**
	 * Maps Operation Direct Agent to database Agent and saves to database
	 * @param cho Operation Direct agent
	 * @return
	 */
	public long mapAndSaveAgent(Agent agent) throws Exception{
		long result = repository.saveAgent(mapToDatabase(agent));
		agent.setId(new BigDecimal(result));				
		return result;
	}
	
	public void sendIndexing(long agentId) throws IOException{		
		try {
			MessageProducer.sendMsg(new IndexMessage(agentId,true,"agent"), MessageType.UPDATE_INDEX);
		} catch (Exception e) {		
			e.printStackTrace();
		}
	}
	
	public void close(){
		repository.close();
	}
	
	/**
	 * Deletes agent from database
	 * @param id Unique id of agent
	 */
	public int deleteAgent(long id){		
		if (id > 1) {

			eu.europeana.direct.backend.model.Agent agent = repository.getAgent(id);
			if (agent != null) {
				if (agent.getEuropeanaDataObject().getEuropeanaDataObjectForLinkedObject().size() < 1) {
					repository.deleteAgent(id);
					return 1;
				} else {
					return 2;
				}
			}
		}
		return 0;
	}
	
	public Map<String,String> getNonAwareCustomFields(Agent directAgent){
		Map<String,String> nonAwareCustomFields = null;
		
		if (directAgent.getLanguageNonAwareFields() != null) {
			if (directAgent.getLanguageNonAwareFields().getCustomFields() != null) {								
				nonAwareCustomFields = new HashMap<String,String>();				
				for (KeyValuePair kv : directAgent.getLanguageNonAwareFields().getCustomFields()) {
					nonAwareCustomFields.put(kv.getKey(),kv.getValue());
				}
			}
		}
		return nonAwareCustomFields;
	}
	
	public Map<String,Map<String,String>> getAwareCustomFields(Agent directAgent){
		Map<String,String> customFields = null;
		Map<String,Map<String,String>> awareCustomFields = new HashMap<String,Map<String,String>>();
		
		for(AgentLanguageAware langAware : directAgent.getLanguageAwareFields()){
			customFields = new HashMap<String,String>();			
			
			if (langAware.getCustomFields() != null) {
				for (KeyValuePair kv : langAware.getCustomFields()) {
					customFields.put(kv.getKey(),kv.getValue());
				}
				// add custom fields with lang value to map<String,Map<STring,String>>
				awareCustomFields.put(langAware.getLanguage(),customFields);
			}						
		}
				
		return awareCustomFields;
	}

	public void deleteIndex(long id) {
		try {
			MessageProducer.sendMsg(new DeleteIndexMessage(id,LuceneDocumentType.Agent,true), MessageType.DELETE_FROM_INDEX);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void startTransaction() {
		repository.getManager().getTransaction().begin();				
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

	public void rollbackTransaction() {
		try {
			if(repository.getManager().getTransaction().isActive()){
				repository.getManager().getTransaction().rollback();				
			}			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}	
	}
	
}
