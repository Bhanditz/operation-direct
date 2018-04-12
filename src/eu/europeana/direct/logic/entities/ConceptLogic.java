package eu.europeana.direct.logic.entities;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.Agent;
import eu.europeana.direct.backend.model.ConceptLangAware;
import eu.europeana.direct.backend.model.EuropeanaDataObject;
import eu.europeana.direct.backend.repositories.ConceptRepository;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.messaging.MessageProducer;
import eu.europeana.direct.messaging.MessageType;
import eu.europeana.direct.messaging.add.IndexMessage;
import eu.europeana.direct.messaging.delete.DeleteIndexMessage;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.KeyValuePair;
import jdk.nashorn.internal.runtime.ECMAException;

public class ConceptLogic {
	
	private final ConceptRepository repository;
	private final static Logger logger = Logger.getLogger(ConceptLogic.class);	

	
	public ConceptLogic() {
		repository = new ConceptRepository();
	}

	public ConceptLogic(EntityManager manager) {
		repository = new ConceptRepository(manager);
	}
	
	/**
	 * Maps database Concept model to Operation Direct Concept model
	 * @param databaseConcept Database Concept model
	 * @return Operation Direct Concept model
	 */
	public Concept mapFromDatabase(
			eu.europeana.direct.backend.model.Concept databaseConcept) {

		Concept conceptMapped = new Concept();

		conceptMapped.setId(new BigDecimal(databaseConcept.getId()));
		conceptMapped.setLanguageNonAwareFields(new ConceptLanguageNonAware());

		// mapping non aware custom fields
		if(databaseConcept.getEuropeanaDataObject().getNonAwareCustomFields() != null){
			if(!databaseConcept.getEuropeanaDataObject().getNonAwareCustomFields().isEmpty()){
				for (Map.Entry<String, String> entry : databaseConcept.getEuropeanaDataObject().getNonAwareCustomFields().entrySet()) {
					conceptMapped.getLanguageNonAwareFields().getCustomFields().add(new KeyValuePair(entry.getKey(), entry.getValue()));
				}
			}	
		}		

		List<ConceptLanguageAware> listLangs = new ArrayList<ConceptLanguageAware>();
		if (databaseConcept.getLanguageAwareFields() != null && !databaseConcept.getLanguageAwareFields().isEmpty()) {
			// maps database Concept translation model to Operation Direct
			// Concept Language Aware fields
			for (Map.Entry<String, List<ConceptLangAware>> entry : databaseConcept.getLanguageAwareFields().entrySet()) {				
				for(ConceptLangAware conceptAware : entry.getValue()){
					//application concept lang aware model
					ConceptLanguageAware langAwareFields = new ConceptLanguageAware();
										
					langAwareFields.setLanguage(entry.getKey());

					if (conceptAware.getAlternativeLabel() != null) {
						langAwareFields.setAlternativeLabel(Arrays.asList(conceptAware.getAlternativeLabel()));
					}

					if (conceptAware.getPreferredLabel() != null && conceptAware.getPreferredLabel().length > 0) {
						langAwareFields.setPreferredLabel(conceptAware.getPreferredLabel()[0]);
					}

					// mapping lang aware custom fields
					if(databaseConcept.getEuropeanaDataObject().getAwareCustomFields() != null && !databaseConcept.getEuropeanaDataObject().getAwareCustomFields().isEmpty()){					
						// check if custom fields exists for this language
						if(databaseConcept.getEuropeanaDataObject().getAwareCustomFields().containsKey(entry.getKey())){
							// get map of custom fields for this language
							for (Map.Entry<String,String> customf : databaseConcept.getEuropeanaDataObject().getAwareCustomFields().get(entry.getKey()).entrySet()) {
								langAwareFields.getCustomFields().add(new KeyValuePair(customf.getKey(), customf.getValue()));
							}
						}										
					}

					listLangs.add(langAwareFields);	
				}								
			}
			conceptMapped.setLanguageAwareFields(listLangs);
		}
		return conceptMapped;					
	}

	/**
	 * Maps Operation Direct Concept model to database Concept model
	 * @param directConcept Operation Direct Concept model
	 * @return Database Concept model
	 */
	public eu.europeana.direct.backend.model.Concept mapToDatabase(
			Concept directConcept) throws Exception{
		
		eu.europeana.direct.backend.model.Concept conceptMapped = new eu.europeana.direct.backend.model.Concept();
		boolean update = false;
		if (directConcept.getId() != null && directConcept.getId().longValueExact() > 0) {
			update = true;
			conceptMapped = repository.getConcept(directConcept.getId().longValueExact());
			if (conceptMapped == null) {
				throw new Exception("Concept " + directConcept.getId() + " does not exists!");
			}
			conceptMapped.getEuropeanaDataObject().setModified(new Date());
		} else {
			EuropeanaDataObject edoNew = new EuropeanaDataObject();
			edoNew.setCreated(new Date());
			edoNew.setModified(new Date());			
			conceptMapped.setEuropeanaDataObject(edoNew);
		}

		// map non aware custom fields
		if (directConcept.getLanguageNonAwareFields() != null) {
			if (directConcept.getLanguageNonAwareFields().getCustomFields() != null && directConcept.getLanguageNonAwareFields().getCustomFields().size() > 0) {
				for (KeyValuePair kv : directConcept.getLanguageNonAwareFields().getCustomFields()) {
					if(kv.getKey() != null) {
						conceptMapped.getEuropeanaDataObject().getNonAwareCustomFields().put(kv.getKey(), kv.getValue());	
					} else {
						throw new Exception("Key of one of the custom field for Concept language non aware is null");
					}
				}
			}
		}
		
		if (directConcept.getLanguageAwareFields() != null && directConcept.getLanguageAwareFields().size() > 0) {
			// maps Operation Direct Concept Language Aware fields to database
			// Concept translation model
		
			// if updating, clear previous lang aware fields
			if (update) {
				conceptMapped.getLanguageAwareFields().clear();
			}
			
			for (ConceptLanguageAware langAwareFields : directConcept.getLanguageAwareFields()) {
				String lang = null;

				// domain concept lang aware										
				ConceptLangAware domainLangAware = new ConceptLangAware();				
								
				if (langAwareFields.getLanguage() != null) {

					lang = langAwareFields.getLanguage();
				} else {
					//throw new Exception("Required field language is missing for Concept language aware entity");
					lang = "en";
				}

				if (langAwareFields.getAlternativeLabel() != null && langAwareFields.getAlternativeLabel().size() > 0) {
					domainLangAware.setAlternativeLabel(langAwareFields.getAlternativeLabel().toArray(new String[0]));
				}

				// preferred label is required, if null skip mapping this
				// concept
				if (langAwareFields.getPreferredLabel() != null && langAwareFields.getPreferredLabel().trim().length() > 0) {
					String[] prefLabel = new String[1];
					prefLabel[0] = langAwareFields.getPreferredLabel();
					domainLangAware.setPreferredLabel(prefLabel);
				} else {
					throw new Exception("Required field preferred label is missing for Concept language aware entity");

				}

				// mapping lang aware custom fields
				if (langAwareFields.getCustomFields() != null && langAwareFields.getCustomFields().size() > 0) {
					Map<String,String> customfields = new HashMap<String,String>();
					
					for (KeyValuePair kv : langAwareFields.getCustomFields()) {						
						if(kv.getKey() != null) {
							customfields.put(kv.getKey(),kv.getValue());	
						} else {
							throw new Exception("Key of one of the custom field for Concept language aware is null");
						}											
					}
					if(!customfields.isEmpty()){
						conceptMapped.getEuropeanaDataObject().getAwareCustomFields().put(langAwareFields.getLanguage(), customfields);
					}
				}

				// for every language add language aware object
				if(conceptMapped.getLanguageAwareFields().containsKey(lang)){
					conceptMapped.getLanguageAwareFields().get(lang).add(domainLangAware);
				}else{
					List<ConceptLangAware> list = new ArrayList<ConceptLangAware>();
					list.add(domainLangAware);
					conceptMapped.getLanguageAwareFields().put(lang, list);	
				}			
			}
		} else {
			throw new Exception("Required field languageAwareFields is missing from Concept entity.");
		}		
		
		return conceptMapped;		
	}
	
	public Concept getConcept(long id) {
		eu.europeana.direct.backend.model.Concept cho = repository.getConcept(id);
		if (cho == null) {
			return null;
		} else {
			Concept result = mapFromDatabase(cho);
			return result;
		}				
	}
	
	/**
	 * Returns database Concept
	 * @param id Unique ID of concept
	 * @return
	 */
	public eu.europeana.direct.backend.model.Concept getConceptDb(long id) {
		eu.europeana.direct.backend.model.Concept cho = repository.getConcept(id);
		if (cho == null) {
			return null;
		} else {
			return cho;
		}		
	}
	
	public long mapAndSaveConcept(Concept concept) throws Exception{
		long result = repository.saveConcept(mapToDatabase(concept));
		concept.setId(new BigDecimal(result));		
		return result;			
	}
	
	public void sendIndexing(long conceptId) throws IOException{		
		try {
			MessageProducer.sendMsg(new IndexMessage(conceptId,true,"concept"), MessageType.UPDATE_INDEX);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		repository.close();
	}
	
	public int deleteConcept(long id){
		if (id < 1) {
			return 0;
		} else {
			eu.europeana.direct.backend.model.Concept concept = repository.getConcept(id);
			if (concept != null) {
				if (concept.getEuropeanaDataObject().getEuropeanaDataObjectForLinkedObject().size() < 1) {
					repository.deleteConcept(id);
					return 1;
				} else {
					return 2;
				}
			} else {
				return 0;
			}
		}
	}
	
	public Map<String,String> getNonAwareCustomFields(Concept directConcept){
		Map<String,String> nonAwareCustomFields = null;
		
		if (directConcept.getLanguageNonAwareFields() != null) {
			if (directConcept.getLanguageNonAwareFields().getCustomFields() != null) {								
				nonAwareCustomFields = new HashMap<String,String>();				
				for (KeyValuePair kv : directConcept.getLanguageNonAwareFields().getCustomFields()) {
					nonAwareCustomFields.put(kv.getKey(),kv.getValue());
				}
			}
		}
		return nonAwareCustomFields;
	}
	
	public Map<String,Map<String,String>> getAwareCustomFields(Concept directConcept){
		Map<String,String> customFields = null;
		Map<String,Map<String,String>> awareCustomFields = new HashMap<String,Map<String,String>>();
		
		for(ConceptLanguageAware langAware : directConcept.getLanguageAwareFields()){
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
			MessageProducer.sendMsg(new DeleteIndexMessage(id,LuceneDocumentType.Concept,true), MessageType.DELETE_FROM_INDEX);
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
