package eu.europeana.direct.logic.entities;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.EuropeanaDataObject;
import eu.europeana.direct.backend.model.PlaceLangAware;
import eu.europeana.direct.backend.model.PlaceLangNonAware;
import eu.europeana.direct.backend.repositories.PlaceRepository;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.messaging.MessageProducer;
import eu.europeana.direct.messaging.MessageType;
import eu.europeana.direct.messaging.add.IndexMessage;
import eu.europeana.direct.messaging.delete.DeleteIndexMessage;
import eu.europeana.direct.rest.gen.java.io.swagger.model.KeyValuePair;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.PlaceLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.PlaceLanguageNonAware;

public class LocationLogic {
	
	private final PlaceRepository repository;
	private final static Logger logger = Logger.getLogger(LocationLogic.class);	

	public LocationLogic() {
		repository = new PlaceRepository();
	}

	public LocationLogic(EntityManager manager) {
		repository = new PlaceRepository(manager);
	}
	
	/**
	 * Maps database Place model to Operation Direct Place model
	 * @param databasePlace Database Place model
	 * @return Operation Direct Place model
	 */
	public Place mapFromDatabase(
			eu.europeana.direct.backend.model.Place databasePlace) {

		Place placeMapped = new Place();

		placeMapped.setId(new BigDecimal(databasePlace.getId()));
		
		placeMapped.setLanguageNonAwareFields(new PlaceLanguageNonAware());

		if(databasePlace.getLanguageNonAwareFields() != null){
			if(databasePlace.getLanguageNonAwareFields().getAltitude() != null){
				placeMapped.getLanguageNonAwareFields().setAltitude(databasePlace.getLanguageNonAwareFields().getAltitude());	
			}			
			if(databasePlace.getLanguageNonAwareFields().getLatitude() != null){
				placeMapped.getLanguageNonAwareFields().setLatitude(databasePlace.getLanguageNonAwareFields().getLatitude());	
			}
			if(databasePlace.getLanguageNonAwareFields().getLongitude() != null){
				placeMapped.getLanguageNonAwareFields().setLongitude(databasePlace.getLanguageNonAwareFields().getLongitude());
			}										
		}				

		// mapping non aware custom fields
		if(databasePlace.getEuropeanaDataObject().getNonAwareCustomFields() != null){
			if(!databasePlace.getEuropeanaDataObject().getNonAwareCustomFields().isEmpty()){
				for (Map.Entry<String, String> entry : databasePlace.getEuropeanaDataObject().getNonAwareCustomFields().entrySet()) {
					placeMapped.getLanguageNonAwareFields().getCustomFields().add(new KeyValuePair(entry.getKey(), entry.getValue()));
				}
			}	
		}		

		List<PlaceLanguageAware> listLangs = new ArrayList<PlaceLanguageAware>();
		if (databasePlace.getLanguageAwareFields() != null && !databasePlace.getLanguageAwareFields().isEmpty()) {
			// maps database Place translation model to Operation Direct Place
			// Language Aware fields
			for (Map.Entry<String, List<PlaceLangAware>> entry : databasePlace.getLanguageAwareFields().entrySet()) {
				
				for(PlaceLangAware placeAware : entry.getValue()){
					
					PlaceLanguageAware langAwareFields = new PlaceLanguageAware();

					langAwareFields.setLanguage(entry.getKey());

					if (placeAware.getAlternativeLabel() != null) {
						langAwareFields.setAlternativeLabel(Arrays.asList(placeAware.getAlternativeLabel()));
					}

					if (placeAware.getPreferredLabel() != null && placeAware.getPreferredLabel().length > 0) {
						langAwareFields.setPreferredLabel(placeAware.getPreferredLabel()[0]);
					}

					
					if (placeAware.getNote() != null && placeAware.getNote().length > 0) {
						Set<String> notes = new HashSet<String>(Arrays.asList(placeAware.getNote()));						
						int k = 1;
					    for (Iterator<String> it = notes.iterator(); it.hasNext(); ) {
					    	KeyValuePair keyValuePair = new KeyValuePair();
							keyValuePair.setKey("note " + (k++));																				
							keyValuePair.setValue(it.next());
							langAwareFields.getCustomFields().add(keyValuePair);
					    }						
					}

					// mapping lang aware custom fields
					if(!databasePlace.getEuropeanaDataObject().getAwareCustomFields().isEmpty()){					
						// check if custom fields exists for this language
						if(databasePlace.getEuropeanaDataObject().getAwareCustomFields().containsKey(entry.getKey())){
							// get map of custom fields for this language
							for (Map.Entry<String,String> customf : databasePlace.getEuropeanaDataObject().getAwareCustomFields().get(entry.getKey()).entrySet()) {								
								// for note custom field check if note with same value already exists																	
								if(customf.getKey().contains("note")){
									if(!(langAwareFields.getCustomFields().stream().anyMatch(c -> c.getValue().equals(customf.getValue())))){
										langAwareFields.getCustomFields().add(new KeyValuePair(customf.getKey(), customf.getValue()));	
									}									
								}else{									
									langAwareFields.getCustomFields().add(new KeyValuePair(customf.getKey(), customf.getValue()));
								}														
							}							
						}										
					}

					listLangs.add(langAwareFields);
				}								
			}

			placeMapped.setLanguageAwareFields(listLangs);
		}

		return placeMapped;
	}

	/**
	 * Maps Operation Direct Place model to database Place model
	 * @param directPlace Operation Direct Place Model
	 * @return Database Place model
	 */
	public eu.europeana.direct.backend.model.Place mapToDatabase(
			Place directPlace) throws Exception{
				
		eu.europeana.direct.backend.model.Place placeMapped = new eu.europeana.direct.backend.model.Place();
		boolean update = false;
		if (directPlace.getId() != null && directPlace.getId().longValueExact() > 0) {
			update = true;
			placeMapped = repository.getPlace(directPlace.getId().longValueExact());
			if (placeMapped == null) {
				throw new Exception("Place " + directPlace.getId() + " does not exists!");
			}
			placeMapped.getEuropeanaDataObject().setModified(new Date());
		} else {
			EuropeanaDataObject edoNew = new EuropeanaDataObject();
			edoNew.setCreated(new Date());
			edoNew.setModified(new Date());
			placeMapped.setEuropeanaDataObject(edoNew);
		}

		// set language non aware field for domain model
		if (directPlace.getLanguageNonAwareFields() != null) {
			PlaceLangNonAware domainNonAware = new PlaceLangNonAware();
			
			if(directPlace.getLanguageNonAwareFields().getAltitude() != null){
				domainNonAware.setAltitude(directPlace.getLanguageNonAwareFields().getAltitude());	
			}
			
			if(directPlace.getLanguageNonAwareFields().getLatitude() != null){
				domainNonAware.setLatitude(directPlace.getLanguageNonAwareFields().getLatitude());	
			}

			if(directPlace.getLanguageNonAwareFields().getLongitude() != null){
				domainNonAware.setLongitude(directPlace.getLanguageNonAwareFields().getLongitude());	
			}			
			
			placeMapped.setLanguageNonAwareFields(domainNonAware);
		}

		if (directPlace.getLanguageNonAwareFields() != null) {
			if (directPlace.getLanguageNonAwareFields().getCustomFields() != null && directPlace.getLanguageNonAwareFields().getCustomFields().size() > 0) {
				for (KeyValuePair kv : directPlace.getLanguageNonAwareFields().getCustomFields()) {
					if(kv.getKey() != null){
						placeMapped.getEuropeanaDataObject().getNonAwareCustomFields().put(kv.getKey(), kv.getValue());	
					} else {
						throw new Exception("Key of one of the custom field for Place language non aware is null");
					}					
				}
			}
		}
		
		if (directPlace.getLanguageAwareFields() != null && directPlace.getLanguageAwareFields().size() > 0) {
			// maps Operation Direct Place Language Aware fields to database
			// Place translation model
			
			// if update clear previous
			if(update){
				placeMapped.getLanguageAwareFields().clear();
			}
			
			for (PlaceLanguageAware langAwareFields : directPlace.getLanguageAwareFields()) {
				String lang = null;

				// domain place lang aware										
				PlaceLangAware domainLangAware = new PlaceLangAware();	
				
				if (langAwareFields.getLanguage() != null && langAwareFields.getLanguage().length() > 0) {

					lang = langAwareFields.getLanguage();
				} else {
					lang = "en";
					//throw new Exception("Required field language is missing for Place language aware entity");
				}

				if (langAwareFields.getAlternativeLabel() != null && langAwareFields.getAlternativeLabel().size() > 0) {
					domainLangAware.setAlternativeLabel(langAwareFields.getAlternativeLabel().toArray(new String[0]));
				}

				// preferred label is required, if null skip mapping this place
				if (langAwareFields.getPreferredLabel() != null && langAwareFields.getPreferredLabel().trim().length() > 0) {
					String[] prefLabel = new String[1];
					prefLabel[0] = langAwareFields.getPreferredLabel();
					domainLangAware.setPreferredLabel(prefLabel);
				} else {
					throw new Exception("Required field preferred label is missing for Place language aware entity");
				}

				// mapping lang aware custom fields
				List<String> notes = new ArrayList<String>();
				if (langAwareFields.getCustomFields() != null && langAwareFields.getCustomFields().size() > 0) {
					Map<String,String> customfields = new HashMap<String,String>();

					for (KeyValuePair kv : langAwareFields.getCustomFields()) {						
						if(kv.getKey() != null){
							if (kv.getKey().toLowerCase().contains("note")) {
								notes.add(kv.getValue());
							} else {							
								customfields.put(kv.getKey(), kv.getValue());
							}	
						} else {
							throw new Exception("Key of one of the custom field for Place language aware is null");
						}						
					}
					
					if(!customfields.isEmpty()){
						placeMapped.getEuropeanaDataObject().getAwareCustomFields().put(langAwareFields.getLanguage(), customfields);
					}
					domainLangAware.setNote(notes.toArray(new String[notes.size()]));
				}

				// for every language add language aware object
				if(placeMapped.getLanguageAwareFields().containsKey(lang)){
					placeMapped.getLanguageAwareFields().get(lang).add(domainLangAware);
				}else{
					List<PlaceLangAware> list = new ArrayList<PlaceLangAware>();
					list.add(domainLangAware);
					placeMapped.getLanguageAwareFields().put(lang, list);	
				}				
			}
		} else {
			throw new Exception("Required field languageAwareFields is missing from Place entity.");
		}		

		return placeMapped;		
	}
	
	/**
	 * Retrieves direct Place object
	 * @param id Id of place object
	 * @return
	 */
	public Place getPlace(long id) {
		eu.europeana.direct.backend.model.Place cho = repository.getPlace(id);
		if (cho == null) {
			return null;
		} else {
			Place result = mapFromDatabase(cho);
			return result;
		}
	}
	
	/**
	 * Returns database Place
	 * @param id Unique ID of place
	 * @return
	 */
	public eu.europeana.direct.backend.model.Place getPlaceDb(long id) {
		eu.europeana.direct.backend.model.Place cho = repository.getPlace(id);
		if (cho == null) {
			return null;
		} else {
			return cho;
		}
	}
	
	/**
	 * Maps direct place to domain place and saves to database
	 * @param place Direct place object
	 * @return Id of saved object
	 * @throws Exception
	 */
	public long mapAndSavePlace(Place place) throws Exception{
		long result = repository.savePlace(mapToDatabase(place));
		place.setId(new BigDecimal(result));
		return result;
	}
	
	/**
	 * Performs indexing for place object
	 * @param placeId Id of place object
	 * @throws IOException
	 */
	public void sendIndexing(long placeId) throws IOException{		
		try {
			MessageProducer.sendMsg(new IndexMessage(placeId,true,"place"), MessageType.UPDATE_INDEX);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes place from database
	 * @param id Id of place object
	 * @return
	 */
	public int deletePlace(long id){
		if (id < 1) {
			return 0;
		} else {
			eu.europeana.direct.backend.model.Place place = repository.getPlace(id);
			if (place != null) {
				if (place.getEuropeanaDataObject().getEuropeanaDataObjectForLinkedObject().size() < 1) {
					repository.deletePlace(id);
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
	 * @param directPlace
	 * @return Language non aware custom fields of place object
	 */
	public Map<String,String> getNonAwareCustomFields(Place directPlace){
		Map<String,String> nonAwareCustomFields = null;
		
		if (directPlace.getLanguageNonAwareFields() != null) {
			if (directPlace.getLanguageNonAwareFields().getCustomFields() != null) {								
				nonAwareCustomFields = new HashMap<String,String>();				
				for (KeyValuePair kv : directPlace.getLanguageNonAwareFields().getCustomFields()) {
					nonAwareCustomFields.put(kv.getKey(),kv.getValue());
				}
			}
		}
		return nonAwareCustomFields;
	}
	
	/**
	 * 
	 * @param directPlace
	 * @return Language aware custom fields of place object
	 */
	public Map<String,Map<String,String>> getAwareCustomFields(Place directPlace){
		Map<String,String> customFields = null;
		Map<String,Map<String,String>> awareCustomFields = new HashMap<String,Map<String,String>>();
		
		for(PlaceLanguageAware langAware : directPlace.getLanguageAwareFields()){
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
			MessageProducer.sendMsg(new DeleteIndexMessage(id,LuceneDocumentType.Place,true),MessageType.DELETE_FROM_INDEX);
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
