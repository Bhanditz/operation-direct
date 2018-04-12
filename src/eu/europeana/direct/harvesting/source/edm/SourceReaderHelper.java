package eu.europeana.direct.harvesting.source.edm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.europeana.direct.backend.repositories.ConceptRepository;
import eu.europeana.direct.harvesting.mapper.EdmMapper;
import eu.europeana.direct.harvesting.mapper.IMapper;
import eu.europeana.direct.harvesting.source.edm.model.EdmOaiSource;
import eu.europeana.direct.helpers.IRestClient;
import eu.europeana.direct.helpers.RestClient;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

public class SourceReaderHelper {

	final static Logger logger = Logger.getLogger(SourceReaderHelper.class);

	IRestClient restClient = new RestClient();
	
	/**
	 * Retrivies all records from APE API and returns list of mapped edm source records
	 * @param apiKey APE API key
	 */
	
	public List<EdmOaiSource> getAPESourceList(String apiKey, CulturalHeritageObjectLogic choLogic){
		
		List<EdmOaiSource> edmOaiSourceList = new ArrayList<EdmOaiSource>();
		IMapper<EdmOaiSource> edmMapper = new EdmMapper();
		ISourceReader<EdmOaiSource> sourceReader = new SourceReader();
		Map<String,String> apiBodyProperties = new HashMap<String,String>();
		apiBodyProperties.put("APIkey", apiKey);	
		apiBodyProperties.put("Content-Type", "application/vnd.ape-v3+json");
		
		int startIndex = 0;
		int count = 50;
				
		/*	Body preview
		 * 	{
			  "count": "50",
			  "startIndex": 0,
			  "query": "Willem van Oranje",
			  "filters": [			      
			     {
			      "facetFieldName": "hasDigitalObject",
			      "facetFieldIds": [
			        "true"
			      ]
			    },
			    {
			     "facetFieldName": "language",
			      "facetFieldIds": [
			        "dut"
			      ]
			    }
			  ]
			} 
		*/
		String body = "{\"count\":"+count+",\"startIndex\":"+startIndex+",\"query\":\"Willem van Oranje\",\"filters\":[{\"facetFieldName\":"
				+ "\"hasDigitalObject\",\"facetFieldIds\":[\"true\"]}]}";
		
		//returns first 50 records from APE API search method
		String jsonRecords = restClient.httpPost("https://api.archivesportaleurope.net/services/search/ead",apiBodyProperties,body);		
		try{
			JSONObject obj = new JSONObject(jsonRecords);
			
			// get ead results from search
	       	JSONArray results = (JSONArray) obj.get("eadSearchResults");
	       		       	
	       
	       	
	       	//loop through records
	       	for(int i=0; i < results.length(); i++){
	       		JSONObject result = (JSONObject) results.get(i);
	       		try{
	       			
	       			if(result.has("id")){
	       				/*
	       				 * if records ID starts with C then its a C-level record,
	       				 * else it's an AED record, so we use APE API content method for retrieving single AED record by ID
	       				 */
	       				if(result.get("id").toString().startsWith("C")){	       						       	
	       					try{
	       						String json = restClient.httpGetRequest("https://api.archivesportaleurope.net/services/content/ead/clevel/"+result.get("id"), apiBodyProperties);	       					
	       						EdmOaiSource edm = sourceReader.readSourceAPEClevel(json,"dut");
	       						CulturalHeritageObject cho = edmMapper.mapFromEdm(edm);
	       						if(cho != null){
	       							cho.getLanguageNonAwareFields().setDataOwner("APE");
	       							choLogic.mapAndSaveCHO(cho, true,false);
	       						}
	       					}catch(Exception e){
	       						continue;
	       					}
	       				}else if(result.get("id").toString().startsWith("F") || result.get("id").toString().startsWith("H") || result.get("id").toString().startsWith("S")){
	       					try{
		       					String json = restClient.httpGetRequest("https://api.archivesportaleurope.net/services/content/ead/archdesc/"+result.get("id"), apiBodyProperties);	       		
		       					// maps AED json response to EdmOaiSource object
		       					EdmOaiSource edm = sourceReader.readSourceApeEAD(json);
	       						CulturalHeritageObject cho = edmMapper.mapFromEdm(edm);
	       						if(cho != null){
	       							cho.getLanguageNonAwareFields().setDataOwner("APE");
	       							choLogic.mapAndSaveCHO(cho, true, false);
	       						}
	       					}catch(Exception e){
	       						continue;
	       					}
	       				}
	       			}
	       			
	       		}catch(Exception e){	       			
	       			logger.error(e.getMessage(),e);
	       		}	       			       
	       	}
			int totalPages = (int) obj.get("totalPages");	    	
			
			// retrieving records from every page not just 1st
	    	for(int i=0; i < totalPages; i++){

	    		//increasing startIndex parameter by 50 with every API call, so we don't get the same records twice
	    		startIndex += count;

	    		body = "{\"count\":"+count+",\"startIndex\":"+startIndex+",\"query\":\"Willem van Oranje\",\"filters\":[{\"facetFieldName\":"
	    				+ "\"hasDigitalObject\",\"facetFieldIds\":[\"true\"]},"
	    				+ "{\"facetFieldName\":\"language\",\"facetFieldIds\":[\"dut\"]}"
	    				+ "]}"; 
	    			    	
	    		
	    		jsonRecords = restClient.httpPost("https://api.archivesportaleurope.net/services/search/ead", apiBodyProperties,body);				
				obj = new JSONObject(jsonRecords);
		       	results = (JSONArray) obj.get("eadSearchResults");
		     	for(int j=0; j < results.length(); j++){
		       		JSONObject result = (JSONObject) results.get(j);
		       		
		       		try{
		       			
		       			if(result.has("id")){
		       				/*
		       				 * if records ID starts with C then its a C-level record,
		       				 * else it's an AED record, so we use APE API content method for retrieving single AED record by ID
		       				 */
		       				if(result.get("id").toString().startsWith("C")){
		       					try{
		       						String json = restClient.httpGetRequest("https://api.archivesportaleurope.net/services/content/ead/clevel/"+result.get("id"), apiBodyProperties);		       					
			       					EdmOaiSource edm = sourceReader.readSourceAPEClevel(json,"dut");
			       					CulturalHeritageObject cho = edmMapper.mapFromEdm(edm);
			       					if(cho != null){
		       							cho.getLanguageNonAwareFields().setDataOwner("APE");
		       							choLogic.mapAndSaveCHO(cho, true, false);
		       						}
		       					}catch(Exception e){
		       						continue;
		       					}		       					
		       				}else if(result.get("id").toString().startsWith("F") || result.get("id").toString().startsWith("H") || result.get("id").toString().startsWith("S")){
		       					try{
		       						String json = restClient.httpGetRequest("https://api.archivesportaleurope.net/services/content/ead/archdesc/"+result.get("id"),apiBodyProperties);
			       					// maps AED json response to EdmOaiSource object
			       					EdmOaiSource edm = sourceReader.readSourceApeEAD(json);
			       					CulturalHeritageObject cho = edmMapper.mapFromEdm(edm);
			       					if(cho != null){
		       							cho.getLanguageNonAwareFields().setDataOwner("APE");
		       							choLogic.mapAndSaveCHO(cho, true, false);
		       						}
		       					}catch(Exception e){
		       						continue;
		       					}		       					
		       				}
		       			}		       				       	
		       		}catch(Exception e){	       			
		       			logger.error(e.getMessage(),e);
		       		}			       				       	
		       	}				     	
			}	    	
	    		    		       										
		}catch(Exception e){
   			logger.error(e.getMessage(),e);
		}
						
		return edmOaiSourceList;
	}
	
	
	
}
