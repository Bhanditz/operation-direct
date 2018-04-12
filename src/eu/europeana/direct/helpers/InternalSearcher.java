package eu.europeana.direct.helpers;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europeana.direct.harvesting.models.helpers.DashboardRecord;
import eu.europeana.direct.harvesting.models.view.DashboardView;
import eu.europeana.direct.legacy.helpers.SearchHelper;


public class InternalSearcher {

	private IRestClient restClient = new RestClient();
    
	private SearchHelper searchHelper = new SearchHelper();
	
	public DashboardView getRecordsByProvider(String user, int rows, int startFrom, Map<String,String> map){				
		
		Map<String,String> props = new HashMap<String,String>();
		props.put("Content-Type", "application/json");
		String searchResponse;
		try {
			String querySyntax = URLEncoder.encode("dataOwner:\""+user+"\"", "UTF-8");
			searchResponse = restClient.httpGetRequest(map.get("odDomain")+"/api/v2/search.json?query="+querySyntax+"&wskey=g31u2hg4qcnoo2hupe06o21re2&rows="+rows+"&start="+startFrom, null);
			if(searchResponse != null){
				return searchHelper.mapSearchToDashboardView(searchResponse,map);	
			}
		} catch (UnsupportedOperationException | IOException e) {		
			e.printStackTrace();
		}	
		return null;		
	}
	
}
