package eu.europeana.direct.rest.main.java.io.swagger.api.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.direct.authentication.ApiAuthenticator;
import eu.europeana.direct.helpers.IRestClient;
import eu.europeana.direct.helpers.RestClient;
import eu.europeana.direct.legacy.helpers.SearchResponseParser;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.legacy.index.LuceneSearchModel;
import eu.europeana.direct.legacy.model.search.Item;
import eu.europeana.direct.logic.CHOBatchLogic;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.messaging.MessageType;
import eu.europeana.direct.rest.gen.java.io.swagger.api.ApiResponseMessage;
import eu.europeana.direct.rest.gen.java.io.swagger.api.CulturalHeritageObjectApiService;
import eu.europeana.direct.rest.gen.java.io.swagger.api.NotFoundException;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageAware;
import eu.europeana.direct.rest.helpers.entities.AgentHelper;
import eu.europeana.direct.rest.helpers.entities.ConceptHelper;
import eu.europeana.direct.rest.helpers.entities.LocationHelper;
import eu.europeana.direct.rest.helpers.entities.TimespanHelper;

public class CulturalHeritageObjectApiServiceImpl extends
		CulturalHeritageObjectApiService {

	final static Logger logger = Logger.getLogger(CulturalHeritageObjectApiServiceImpl.class);
	private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
			.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Unauthorized")).build();
	/**
	 * Finds CHO database model based on id, maps it into Operation Direct CHO model and returns it
	 */
	@Override
	public Response culturalHeritageObjectGet(BigDecimal id, String language, String apiKey,
			SecurityContext securityContext) throws NotFoundException {
		
		if(ApiAuthenticator.validApiKey(apiKey)) {
			CulturalHeritageObjectLogic logic = new CulturalHeritageObjectLogic();			
			try {						
				// get api CHO model
				CulturalHeritageObject cho = logic.getCHO(id.longValueExact());
				if (cho != null) {
					// if language parameter is not specified, return all language aware fields of CHO
					if(language == null || language.length() == 0){
						return Response.ok().entity(cho).build();
					}else{																							
						
						//check if retrieved cho contains language aware fields with given language in request										
						if(cho.getLanguageAwareFields().stream().anyMatch(t -> t.getLanguage().equals(language))){
							// sorts CHO language aware objects that have metadata in the same language as language specified in request language parameter
							List<CulturalHeritageObjectLanguageAware> langAwareList = new ArrayList<CulturalHeritageObjectLanguageAware>();
							
							for(int i=0; i < cho.getLanguageAwareFields().size(); i++){						
								if(cho.getLanguageAwareFields().get(i).getLanguage().equals(language)){
									langAwareList.add(cho.getLanguageAwareFields().get(i));
								}
							}						
							// clears all CHO language aware objects
							cho.getLanguageAwareFields().clear();
							// set CHO language aware objects that are in the same language as language specified in request language parameter
							cho.setLanguageAwareFields(langAwareList);
																									
							if(!cho.getConcepts().isEmpty()){
								ConceptHelper conceptHelper = new ConceptHelper();
								// go through concepts and check if any of them has the same language value as specified in request parameter							
								conceptHelper.sortByLanguage(cho,cho.getConcepts(),language);																											
							}
							
							if(!cho.getAgents().isEmpty()){
								AgentHelper agentHelper = new AgentHelper();
								// go through agents and check if any of them has the same language value as specified in request parameter							
								agentHelper.sortByLanguage(cho,cho.getAgents(),language);																											
							}
							
							if(!cho.getSpatial().isEmpty()){
								LocationHelper locationHelper = new LocationHelper();
								// go through places and check if any of them has the same language value as specified in request parameter							
								locationHelper.sortByLanguage(cho,cho.getSpatial(),language);																											
							}
							
							if(!cho.getTemporal().isEmpty()){
								TimespanHelper timespanHelper = new TimespanHelper();
								// go through timespans and check if any of them has the same language value as specified in request parameter							
								timespanHelper.sortByLanguage(cho,cho.getTemporal(),language);																											
							}									
							
							return Response.ok().entity(cho).build();
							
						}else{
							// CHO doesn't contain metadata with language specified in url parameter
							logger.info("CHO : "+id+ "doesnt contain "+language);
							return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.WARNING,"Cultural Heritage Object "+id+" doesn't contain metadata in language: "+language)).build();
						}					
					}			
				} else {					
					logger.error("CHO ID "+id.longValueExact()+" not found");
					return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Cultural Heritage Object with ID: "+id+" not found")).build();
				}
			} catch (NullPointerException ex) {				
				logger.error("ID parameter for retrieving Cultural Heritage Object not found.",ex);
				return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Make sure that Object ID parameter is specified in the correct way")).build();
			} catch (Exception e) {								
				logger.error(e.getMessage(),e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,e.getMessage())).build();			
			} finally {							
				logic.close();
			}	
		} else {
			return ACCESS_DENIED;
		}				
	}


	/**
	 * Maps Operation Direct API CHO model into database model and saves
	 */
	@Override
	public Response culturalHeritageObjectPost(CulturalHeritageObject culturalHeritageObject,String apiKey, SecurityContext securityContext) throws NotFoundException {
		
		if(ApiAuthenticator.validApiKey(apiKey)) {
			CulturalHeritageObjectLogic logic = new CulturalHeritageObjectLogic();
			try {											
				logic.startTransaction();				
				long id = logic.mapAndSaveCHO(culturalHeritageObject, true, true);
				logic.commitTransaction();						
				logic.sendIndexing(id);			
				return Response.ok().entity(id).build();
			} catch (Exception ex) {				
				logic.rollbackTransaction();
				logger.error("Object received & parsed, save failed: " + ex.getMessage(), ex);
				return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.WARNING,
						"Object received & parsed, save failed: " + ex.getMessage())).build();
			} finally {			
				logic.close();
			}
		} else {
			return ACCESS_DENIED;
		}										
	}


	/**
	 * Deletes CHO model from database
	 */
	@Override
	public Response culturalHeritageObjectDelete(BigDecimal id,Boolean choOnly,String apiKey, SecurityContext securityContext)
			throws NotFoundException {					
		if(ApiAuthenticator.validApiKey(apiKey)) {
			
			if(choOnly == null){
				choOnly = false;
			}
			CulturalHeritageObjectLogic logic = new CulturalHeritageObjectLogic();			

			try {					
				logic.startTransaction();				
				boolean deleted = logic.deleteCHO(id.longValueExact(),choOnly);					
				logic.commitTransaction();				
				if (deleted) {					
					
					logic.deleteFromIndex(id.longValueExact(),logic.getDeletedEntityIds());																		
					return Response.status(Response.Status.OK).entity(new ApiResponseMessage(ApiResponseMessage.OK,"Cultural Heritage Object with ID: "+id+" successfully deleted. "+logic.getMessage())).build();						
				} else {
					logger.info("ID "+id.longValueExact()+" not found");
					return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.ERROR," Cultural Heritage Object with ID "+id+ " not found")).build();				
				}
			} catch (Exception ex) {
				logic.rollbackTransaction();
				logger.error(ex.getMessage(),ex);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,ex.getMessage())).build();
			}finally {
				logic.close();
			}
		} else {
			return ACCESS_DENIED;
		}
	}


	@Override
	public Response culturalHeritageObjectDeleteByDataOwner(String dataOwner, String apiKey,
			SecurityContext securityContext) throws NotFoundException {
		
		IRestClient restclient = new RestClient();
		CHOBatchLogic batchLogic = new CHOBatchLogic();
		
		Map<String,String> props = new HashMap<String,String>(){{
			put("Content-Type", "application/json");
		}};
		try {
			dataOwner = dataOwner.trim();
			if(!dataOwner.contains("\"")){
				dataOwner = "\""+dataOwner+"\"";				
    		}
			dataOwner = URLEncoder.encode(dataOwner, "UTF-8");			
			String searchResponse = restclient.httpGetRequest("http://data.jewisheritage.net/rest/api/v2/search.json?query=dataOwner:"+dataOwner+"&wskey=g31u2hg4qcnoo2hupe06o21re2&rows=100", props);			
			//String searchResponse = restclient.httpGetRequest("http://localhost:8080/rest/api/v2/search.json?query=dataOwner:"+dataOwner+"&wskey=g31u2hg4qcnoo2hupe06o21re2&rows=100", props);
			
			if(searchResponse != null) {				
				try{
					// get all items for dataOwner
					LuceneSearchModel searchModel = SearchResponseParser.parseLegacySearchResponse(searchResponse);
					// delete items from index
					if(searchModel.getItems().size() > 0){
						
						batchLogic.deleteRecordsFromIndex(searchModel.getItems());
						while(true){										
							searchResponse = restclient.httpGetRequest("http://data.jewisheritage.net/rest/api/v2/search.json?query=dataOwner:"+dataOwner+"&wskey=g31u2hg4qcnoo2hupe06o21re2&rows=100", props);
							//searchResponse = restclient.httpGetRequest("http://localhost:8080/rest/api/v2/search.json?query=dataOwner:"+dataOwner+"&wskey=g31u2hg4qcnoo2hupe06o21re2&rows=100", props);
							if(searchResponse != null){
								searchModel = SearchResponseParser.parseLegacySearchResponse(searchResponse);
								if(searchModel.getItems().size() > 0){
									batchLogic.deleteRecordsFromIndex(searchModel.getItems());	
								} else {
									break;
								}																							
							}									
						}
					}																				
					
				}catch (Exception e){
					return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,e.getMessage())).build();
				}											
			}
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			return Response.serverError().build();			
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError().build();			
		} catch (Exception e){
			e.printStackTrace();
			return Response.serverError().build();			
		} finally {							
			LuceneIndexing.getInstance().commitIndex(true);
		}
		return Response.ok().build();
	}

}
