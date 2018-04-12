package eu.europeana.direct.logic.entities;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.EuropeanaDataObject;
import eu.europeana.direct.backend.model.TimespanLangAware;
import eu.europeana.direct.backend.repositories.TimespanRepository;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.messaging.MessageProducer;
import eu.europeana.direct.messaging.MessageType;
import eu.europeana.direct.messaging.add.IndexMessage;
import eu.europeana.direct.messaging.delete.DeleteIndexMessage;
import eu.europeana.direct.rest.gen.java.io.swagger.model.KeyValuePair;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpanLangaugeAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpanLanguageNonAware;

public class TimespanLogic {
	
	private final TimespanRepository repository;	
	private final static Logger logger = Logger.getLogger(TimespanLogic.class);	

	public TimespanLogic() {
		repository = new TimespanRepository();						
	}

	public TimespanLogic(EntityManager manager) {
		repository = new TimespanRepository(manager);
	}


	/**
	 * Maps database Timespan model to Operation Direct Timespan model
	 * @param databaseTimespan Database Timespan model
	 * @return Operation Direct Timespan model
	 */
	public TimeSpan mapFromDatabase(
			eu.europeana.direct.backend.model.Timespan databaseTimespan) {
		
		TimeSpan timespanMapped = new TimeSpan();
		timespanMapped.setId(new BigDecimal(databaseTimespan.getId()));
		timespanMapped.setLanguageNonAwareFields(new TimeSpanLanguageNonAware());

		// mapping non aware custom fields
		if(databaseTimespan.getEuropeanaDataObject().getNonAwareCustomFields() != null){
			if (!databaseTimespan.getEuropeanaDataObject().getNonAwareCustomFields().isEmpty()) {
				for (Map.Entry<String, String> entry : databaseTimespan.getEuropeanaDataObject().getNonAwareCustomFields()
						.entrySet()) {
					timespanMapped.getLanguageNonAwareFields().getCustomFields().add(new KeyValuePair(entry.getKey(), entry.getValue()));
				}
			}	
		}		

		List<TimeSpanLangaugeAware> listLangs = new ArrayList<TimeSpanLangaugeAware>();
		if (databaseTimespan.getLanguageAwareFields() != null && !databaseTimespan.getLanguageAwareFields().isEmpty()) {
			// maps database Timespan translation model to Operation Direct
			// Timespan Language Aware fields
			for (Map.Entry<String, List<TimespanLangAware>> entry : databaseTimespan.getLanguageAwareFields().entrySet()) {
				
				for(TimespanLangAware timespanLangAware : entry.getValue()){
					
					TimeSpanLangaugeAware langAwareFields = new TimeSpanLangaugeAware();

					langAwareFields.setLanguage(entry.getKey());

					if (timespanLangAware.getAlternativelabel() != null) {
						langAwareFields.setAlternativeLabel(Arrays.asList(timespanLangAware.getAlternativelabel()));
					}

					if (timespanLangAware.getPreferredlabel() != null && timespanLangAware.getPreferredlabel().length > 0) {
						langAwareFields.setPreferredLabel(timespanLangAware.getPreferredlabel()[0]);
					}

					// mapping lang aware custom fields
					if(databaseTimespan.getEuropeanaDataObject().getAwareCustomFields() != null && !databaseTimespan.getEuropeanaDataObject().getAwareCustomFields().isEmpty()){					
						// check if custom fields exists for this language
						if(databaseTimespan.getEuropeanaDataObject().getAwareCustomFields().containsKey(entry.getKey())){
							// get map of custom fields for this language
							for (Map.Entry<String,String> customf : databaseTimespan.getEuropeanaDataObject().getAwareCustomFields().get(entry.getKey()).entrySet()) {
								langAwareFields.getCustomFields().add(new KeyValuePair(customf.getKey(), customf.getValue()));
							}
						}										
					}
					
					listLangs.add(langAwareFields);
				}			
			}
			timespanMapped.setLanguageAwareFields(listLangs);
		}
		return timespanMapped;				
	}

	/**
	 * Maps Operation Direct TimeSpan model to database Timespan model
	 * @param directTimespan Operation Direct TimeSpan model
	 * @return Database Timespan model
	 */
	public eu.europeana.direct.backend.model.Timespan mapToDatabase(
			TimeSpan directTimespan) throws Exception{
		
		eu.europeana.direct.backend.model.Timespan timespanMapped = new eu.europeana.direct.backend.model.Timespan();
		boolean update = false;
		if (directTimespan.getId() != null && directTimespan.getId().longValueExact() > 0) {
			update = true;
			timespanMapped = repository.getTimespan(directTimespan.getId().longValueExact());
			if (timespanMapped == null) {
				throw new Exception("Timespan " + directTimespan.getId() + " does not exists!");
			}
			timespanMapped.getEuropeanaDataObject().setModified(new Date());
		} else {
			EuropeanaDataObject edoNew = new EuropeanaDataObject();
			edoNew.setCreated(new Date());
			edoNew.setModified(new Date());
			timespanMapped.setEuropeanaDataObject(edoNew);
		}

		if (directTimespan.getLanguageNonAwareFields() != null) {
			if (directTimespan.getLanguageNonAwareFields().getCustomFields() != null && directTimespan.getLanguageNonAwareFields().getCustomFields().size() > 0) {
				for (KeyValuePair kv : directTimespan.getLanguageNonAwareFields().getCustomFields()) {
					if(kv.getKey() != null){
						timespanMapped.getEuropeanaDataObject().getNonAwareCustomFields().put(kv.getKey(), kv.getValue());	
					} else {
						throw new Exception("Key of one of the custom field for Timespan language non aware is null");
					}				
				}
			}
		}

				
		if (directTimespan.getLanguageAwareFields() != null && directTimespan.getLanguageAwareFields().size() > 0) {
			// maps Operation Direct Timespan Language Aware fields to database
			// Timespan translation model
			
			if(update){
				timespanMapped.getLanguageAwareFields().clear();
			}
			
			for (TimeSpanLangaugeAware langAwareFields : directTimespan.getLanguageAwareFields()) {
				String lang = null;

				// domain timespan lang aware										
				TimespanLangAware domainLangAware = new TimespanLangAware();

				if (langAwareFields.getLanguage() != null && langAwareFields.getLanguage().trim().length() > 0) {

					lang = langAwareFields.getLanguage();
				} else {
					throw new Exception("Required field language is missing for Timespan language aware entity");
				}

				if (langAwareFields.getAlternativeLabel() != null && langAwareFields.getAlternativeLabel().size() > 0) {
					domainLangAware.setAlternativelabel(langAwareFields.getAlternativeLabel().toArray(new String[0]));
				}

				// preferred label is required, if null skip mapping this
				// timespan
				if (langAwareFields.getPreferredLabel() != null && langAwareFields.getPreferredLabel().trim().length() > 0) {
					String[] prefLabel = new String[1];
					prefLabel[0] = langAwareFields.getPreferredLabel();
					domainLangAware.setPreferredlabel(prefLabel);
				} else {
					throw new Exception("Required field prefered label is missing for Timespan language aware entity");
				}

				// mapping lang aware custom fields
				if (langAwareFields.getCustomFields() != null && langAwareFields.getCustomFields().size() > 0) {
					Map<String,String> customfields = new HashMap<String,String>();
					
					for (KeyValuePair kv : langAwareFields.getCustomFields()) {						
						if(kv.getKey() != null){
							customfields.put(kv.getKey(),kv.getValue());							
						} else {
							throw new Exception("Key of one of the custom field for Timespan language aware is null");
						}						
					}
					if(!customfields.isEmpty()){
						timespanMapped.getEuropeanaDataObject().getAwareCustomFields().put(langAwareFields.getLanguage(), customfields);
					}
				}	


				// for every language add language aware object		
				if(timespanMapped.getLanguageAwareFields().containsKey(lang)){
					timespanMapped.getLanguageAwareFields().get(lang).add(domainLangAware);
				}else{
					List<TimespanLangAware> list = new ArrayList<TimespanLangAware>();
					list.add(domainLangAware);
					timespanMapped.getLanguageAwareFields().put(lang, list);
				}
														
			}
		} else {
			throw new Exception("Required field languageAwareFields is missing from Timespan entity.");
		}

		return timespanMapped;				
	}
	
	/**
	 * Retrieves Timespan direct model from database
	 * @param id Timespan id
	 * @return
	 */
	public TimeSpan getTimeSpan(long id) {		
		eu.europeana.direct.backend.model.Timespan timespan = repository.getTimespan(id);
		if (timespan == null) {
			return null;
		} else {
			TimeSpan result = mapFromDatabase(timespan);
			return result;
		}
	}
	
	/**
	 * Returns database Timespan
	 * @param id Unique ID of timespan
	 * @return
	 */
	public eu.europeana.direct.backend.model.Timespan getTimespanDb(long id) {
		eu.europeana.direct.backend.model.Timespan cho = repository.getTimespan(id);
		if (cho == null) {
			return null;
		} else {
			return cho;
		}
	}
	
	/**
	 * Maps direct Timespan model to database model and saves
	 * @param timespan Direct TimeSpan model
	 * @return ID of saved object
	 * @throws Exception
	 */
	public long mapAndSaveTimespan(TimeSpan timespan) throws Exception{
		long result = repository.saveTimespan(mapToDatabase(timespan));
		timespan.setId(new BigDecimal(result));
		return result;		
	}
	
	/**
	 * Performs indexing of timespan object
	 * @param timespanId Id of timespan object
	 * @throws IOException
	 */
	public void sendIndexing(long timespanId) throws IOException{		
		try {
			MessageProducer.sendMsg(new IndexMessage(timespanId,true,"timespan"), MessageType.UPDATE_INDEX);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes timespan from database
	 * @param id Timespan id
	 * @return
	 */
	public int deleteTimespan(long id){
		if (id < 1) {
			return 0;
		} else {
			eu.europeana.direct.backend.model.Timespan timespan = repository.getTimespan(id);
			if (timespan != null) {
				if (timespan.getEuropeanaDataObject().getEuropeanaDataObjectForLinkedObject().size() < 1) {
					repository.deleteTimespan(id);
					return 1;
				} else {
					return 2;
				}
			} else {
				return 0;
			}
		}				
	}
	
	public void close(){
		repository.close();
	}
	
	/**
	 * 
	 * @param directTimespan Timespan model
	 * @return Custom language non aware fields for timespan
	 */
	public Map<String,String> getNonAwareCustomFields(TimeSpan directTimespan){
		Map<String,String> nonAwareCustomFields = null;
		
		if (directTimespan.getLanguageNonAwareFields() != null) {
			if (directTimespan.getLanguageNonAwareFields().getCustomFields() != null) {								
				nonAwareCustomFields = new HashMap<String,String>();				
				for (KeyValuePair kv : directTimespan.getLanguageNonAwareFields().getCustomFields()) {
					nonAwareCustomFields.put(kv.getKey(),kv.getValue());
				}
			}
		}
		return nonAwareCustomFields;
	}
	
	/**
	 * 
	 * @param directTimespan Timespan model
	 * @return Custom language aware fields for timespan
	 */
	public Map<String,Map<String,String>> getAwareCustomFields(TimeSpan directTimespan){
		Map<String,String> customFields = null;
		Map<String,Map<String,String>> awareCustomFields = new HashMap<String,Map<String,String>>();
		
		for(TimeSpanLangaugeAware langAware : directTimespan.getLanguageAwareFields()){
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
			MessageProducer.sendMsg(new DeleteIndexMessage(id,LuceneDocumentType.Timespan,true), MessageType.DELETE_FROM_INDEX);
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
