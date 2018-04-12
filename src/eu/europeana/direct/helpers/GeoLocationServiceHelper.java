package eu.europeana.direct.helpers;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class GeoLocationServiceHelper {

	public Map<String,String> getLocationDetails(String currentLocationLink,String language){
		
		Map<String,String> details = new HashMap<String,String>();
		
		IRestClient rs = new RestClient();
		String geonameId = currentLocationLink;
		geonameId = geonameId.replaceAll("\\D+","");		
		String responseDetails;
		try {
			responseDetails = rs.httpGetRequest(String.format("http://api.geonames.org/get?geonameId=%s&username=semantika", geonameId),null);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(responseDetails));
			Document xmlDoc = builder.parse(is);

			NodeList nl = xmlDoc.getElementsByTagName(xmlDoc.getDocumentElement().getNodeName());
			Element rootElement = (Element) nl.item(0);
			details.put("latitude", rootElement.getElementsByTagName("lat").item(0).getTextContent());
			details.put("longitude", rootElement.getElementsByTagName("lng").item(0).getTextContent());

			NodeList alternateNames = rootElement.getElementsByTagName("alternateName");
			for (int i = 0; i < alternateNames.getLength(); i++) {
				if (alternateNames.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element alternateNamesElement = (Element) alternateNames.item(i);

					if (alternateNamesElement.hasAttribute("lang")
							&& !alternateNamesElement.hasAttribute("isPreferredName")) {
						if (alternateNamesElement.getAttribute("lang").equals(language)) {
							details.put("locationName", alternateNamesElement.getTextContent());
						}
					}
				}
			}					
				
		
		
		} catch (UnsupportedOperationException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e) {
		} catch (SAXException e) {
		}	
					
		return details;
	}
	
}
