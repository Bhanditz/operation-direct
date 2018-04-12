package eu.europeana.direct.legacy.helpers;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.direct.legacy.index.LuceneSearchModel;
import eu.europeana.direct.rest.gen.java.io.swagger.api.ApiResponseMessage;

public class SearchResponseParser {

	private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	
	public static LuceneSearchModel parseLegacySearchResponse(String response) throws Exception{
		LuceneSearchModel searchModel = null;				
		try{
			searchModel = OBJECT_MAPPER.readValue(response, LuceneSearchModel.class);	
		} catch (Exception e){
			throw new Exception("Could not parse search response");			
		}
		return searchModel;
	}
}
