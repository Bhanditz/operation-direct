package eu.europeana.direct.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

public interface IRestClient {
	
	String httpGetRequest(String urlStr, Map<String, String> properties) throws UnsupportedOperationException, IOException;	
	String httpPost(String urlStr, Map<String, String> properties, String jsonBody);	
	String httpDelete(String urlStr, Map<String, String> properties) throws ClientProtocolException, IOException;	

}
