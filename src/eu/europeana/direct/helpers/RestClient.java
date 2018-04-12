package eu.europeana.direct.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

public class RestClient implements IRestClient {
	
	@Override
	public String httpGetRequest(String url, Map<String, String> properties) throws UnsupportedOperationException, IOException{
		
		CloseableHttpClient httpclient = HttpClients.createDefault();		
		HttpGet httpGet = new HttpGet(url);		
		
		
		if (properties != null && properties.size() > 0) {
			for (Map.Entry<String, String> entry : properties.entrySet()) {
				httpGet.addHeader(entry.getKey(), entry.getValue());
			}
		}		 
		
		CloseableHttpResponse response = httpclient.execute(httpGet);

		try {
			HttpEntity entity = response.getEntity();
			if(response.getStatusLine().getStatusCode() == 200){				
				return "200";
				//return getBody(entity.getContent());	
			}else{				
				return response.getStatusLine().toString();
			}						    
		} finally {
		    response.close();
		}		
	}
	
	
	@Override
	public String httpPost(String url, Map<String, String> headerValues, String jsonBody){
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {

		    HttpPost request = new HttpPost(url);
		    
		    if(headerValues != null && headerValues.size() > 0){
		    	for(Map.Entry<String,String> entry : headerValues.entrySet()){
		    		request.addHeader(entry.getKey(), entry.getValue());
		    	}		    	
		    }
		    
		    if(jsonBody != null){
		    	StringEntity params =new StringEntity(jsonBody);
		    	request.setEntity(params);
		    }		    		    
		    
		    response = httpClient.execute(request);	

		    HttpEntity entity = response.getEntity();			    
		    return getBody(entity.getContent()); 

		}catch (Exception ex) {				
			ex.printStackTrace();
		} finally {
			try {
				if(response != null){
					response.close();	
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		return "";
	}
	
	private String getBody(InputStream body) throws IOException{
		
		InputStreamReader isr = new InputStreamReader(body, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		isr.close();
		return sb.toString();
	}


	@Override
	public String httpDelete(String url, Map<String, String> properties) throws ClientProtocolException, IOException {
		
		CloseableHttpClient httpclient = HttpClients.createDefault();		
		HttpDelete httpGet = new HttpDelete(url);		
		
		
		if (properties != null && properties.size() > 0) {
			for (Map.Entry<String, String> entry : properties.entrySet()) {
				httpGet.addHeader(entry.getKey(), entry.getValue());
			}
		}		 
		
		CloseableHttpResponse response = httpclient.execute(httpGet);

		try {
			HttpEntity entity = response.getEntity();
			if(response.getStatusLine().getStatusCode() == 200){				
				return getBody(entity.getContent());	
			}else{
				return response.getStatusLine().toString();
			}						    
		} finally {
		    response.close();
		}		
	}
}
