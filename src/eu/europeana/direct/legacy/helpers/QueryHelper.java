package eu.europeana.direct.legacy.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;

import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.model.search.Parameters;
import eu.europeana.direct.legacy.model.search.ProfileType;


public class QueryHelper {
	final static Logger logger = Logger.getLogger(QueryHelper.class);

	/**
	 * Method adds backlash to iso date for lucene query syntax purposes
	 * @param query Lucene query for searching records
	 * @return String processed query
	 */
	public String processSearchQuery(String query){
		try{
			for(int i=0; i < query.length(); i++){
		
				if(query.charAt(i) == ':' && i != 0){
					String s = ""+query.charAt(i-1);
									
					if(s.matches("[0-9]")){
						query = query.substring(0,i)+ "\\" + query.substring(i,query.length());					
					}
				}			
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return query;
	}
	
	public boolean hasAllSearchFields(String query){
		String[] array = query.split("AND");
		boolean hasAll = false;
		for(String s : array){				
			if(s.contains(":")){
				hasAll = true;
			}else{		
				return false;
			}
		}		
		return hasAll;
	}
	
	public String trimAndSingleWhitespace(String query){		
		query = query.trim().replaceAll(" +", " ");			
		query = query.trim().replaceAll(": ", ":");			
		query = query.trim().replaceAll(":\" ", ":\"");		
		query = query.trim().replaceAll(" \"", "\"");		
		
		return query;
	}
	
	/**
	 * Method changes ISO date characters to upper case for lucene query syntax purposes
	 * Example: 2013-11-01t00:00:0.000z to 2017-12-01t00:00:00.000z -> 2013-11-01T00:00:0.000Z TO 2017-12-01T00:00:00.000Z
	 * @param query Lucene query for searching records
	 * @return String Valid query for lucene search
	 */
	public String processIsoDate(String query){
		StringBuilder q = new StringBuilder(query);

		try{	
			for(int i=0; i < query.length(); i++){	
				if((query.charAt(i) == 't' || query.charAt(i) == 'T') && i != 0 && (i < query.length()-2)){
					String s = ""+query.charAt(i-1);
					String s1 = ""+query.charAt(i+1);
					String s2 = ""+query.charAt(i+2);
					if(s.matches("[0-9]")){
						q.setCharAt(i, 'T');	
					}else if(s.matches("\\s") && (s1.matches("o") || s1.matches("O")) && s2.matches("\\s")){						
						q.setCharAt(i, 'T');
						q.setCharAt(i+1, 'O');
					}				
				}else if(query.charAt(i) == 'z' && i != 0){
					String s = ""+query.charAt(i-1);
					if(s.matches("[0-9]")){
						q.setCharAt(i, 'Z');
					}
				}
			}
		}catch(Exception e){
				logger.error(e.getMessage(),e);
		}			
								
		return q.toString();
	}

	/**
	 * Adds langind page term
	 * @param searchTerm
	 * @param searchParameters
	 * @return
	 */
	public String addLandingPageTerm(String searchTerm, Parameters searchParameters) {
		if(searchParameters.isLandingpage()){
			return searchTerm += " AND hasLandingPage:true";
		}else{
			return searchTerm += " AND hasLandingPage:false";
		}
	}

	/**
	 * Adds thumbnail term
	 * @param searchTerm
	 * @param searchParameters
	 * @return
	 */
	public String addThumbnailTerm(String searchTerm, Parameters searchParameters) {
		if(searchParameters.isThumbnail()){
			return searchTerm += " AND thumbnail:true";
		}else{
			return searchTerm += " AND thumbnail:false";
		}
	}

	public Map<String,Object> setObjectTypeTerm(String object) {
		Map<String,Object> map = new HashMap<String,Object>();

		if(object == null){
			map.put("object_type", "object_type:culturalheritageobject AND ");
			map.put("docType",LuceneDocumentType.CulturalHeritageObject);			
		}else{						
			switch (object.toLowerCase()) {
			case "culturalheritageobject":
				map.put("docType",LuceneDocumentType.CulturalHeritageObject);
				map.put("object_type", "object_type:culturalheritageobject AND ");
				break;
			case "agent":
				map.put("docType",LuceneDocumentType.Agent);
				map.put("object_type", "object_type:agent AND ");
				break;
			case "timespan":
				map.put("docType",LuceneDocumentType.Timespan);
				map.put("object_type", "object_type:timespan AND ");
				break;
			case "place":
				map.put("docType",LuceneDocumentType.Place);
				map.put("object_type", "object_type:place AND ");
				break;
			case "concept":
				map.put("docType",LuceneDocumentType.Concept);
				map.put("object_type", "object_type:concept AND ");
				break;
			case "weblink":
				map.put("docType",LuceneDocumentType.Weblink);
				map.put("object_type", "object_type:weblink AND ");
				break;
			// only for private purpose (Statistics for contextual entities for certain user)
			case "entity":
				//docType can be anything except CHO
				map.put("docType",LuceneDocumentType.Agent);				
				map.put("object_type", "");
				break;
			default:
				map.put("docType",LuceneDocumentType.CulturalHeritageObject);
				map.put("object_type", "object_type:culturalheritageobject AND ");
				break;
			}
		}
		return map;
	}

	/**
	 * Adds additional terms to search term for lucene query (additional terms are specified in requeust parameter qf=)
	 * @param searchTerm
	 * @param searchParameters
	 * @return
	 */
	public String addAdditionalTerms(String searchTerm, Parameters searchParameters) {
		if (!searchParameters.getQf().isEmpty()) {
			for (String qf : searchParameters.getQf()) {
				if(searchTerm.length() == 0){
					searchTerm += qf;	
				}else{
					searchTerm += " AND " + qf;
				}						
			}
		}
		return searchTerm;
	}

	public List<String> parseFacetsQuery(List<String> facetsValues) {
		List<String> facetsList = new ArrayList<String>();
		for (String s : facetsValues) {
			//if multiple facet fields, we split them
			if (s.contains(",")) {
				String[] facets = s.split(",");
				for (int i = 0; i < facets.length; i++) {
					facetsList.add(facets[i]);
				}
			} else if (s.contains(" ")) {
				String[] facets = s.split(" ");
				for (int i = 0; i < facets.length; i++) {
					facetsList.add(facets[i]);
				}
			} else {
				facetsList.add(s);
			}
		}
		return facetsList;
	}

	/**
	 * Retrivies profile based on profile query parameter
	 * @param profile
	 * @return
	 */
	public ProfileType setProfileType(String profile) {		
		ProfileType type = null;
		
		if(profile != null){
			switch(profile.toLowerCase()){
			case "minimal":
				type = ProfileType.Minimal;				
			case "standard":
				type = ProfileType.Standard;				
			case "portal":
				type = ProfileType.Portal;				
			case "rich":
				type = ProfileType.Rich;
			case "oai":
				type = ProfileType.OAI;
			}
			if(type != null){
				return type;	
			}			
		}		

		// for europeana portal purposes
		// europeana portal gives you profile parameter like profile=rich+facets+..
		if(type == null && profile != null){
			if(profile.contains("minimal")){
				type = ProfileType.Minimal;
			}else if(profile.contains("standard")){
				type = ProfileType.Standard;
			}else if(profile.contains("rich")){
				type = ProfileType.Rich;
			}else if(profile.contains("portal")){
				type = ProfileType.Portal;
			}else{				
				return ProfileType.Standard;
			}
		}else{			
			return ProfileType.Standard;
		}		

		return type;
	}	
	
	/**
	 * Retrivies query parameters for facet limits (f.[facet name].facet.limit)
	 * @param queryParameters All search query parameters
	 * @return
	 */
	public Map<String, Integer> getFacetsLimits(MultivaluedMap<String, String> queryParameters) {
		Map<String,Integer> facetsLimit = new HashMap<String,Integer>();
		
		SearchQueryMapper sqm = new SearchQueryMapper();
		
		for(Map.Entry<String,List<String>> map : queryParameters.entrySet()){
			if(map.getKey().startsWith("f.") && map.getKey().endsWith("limit")){
				
				List<String> list = new ArrayList<String>();
				list.add(map.getKey());
				//map facet title to databaseField, se we can compare with facets title (parameter facet=)
				List<String> mapped = sqm.mapToDatabaseField(list);
				if(mapped.size() > 0){
					facetsLimit.put(mapped.get(0), Integer.parseInt(map.getValue().get(0)));	
				}				
			}
		}
		
		return facetsLimit;
	}	
}
