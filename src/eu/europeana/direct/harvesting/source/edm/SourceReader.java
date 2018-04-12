package eu.europeana.direct.harvesting.source.edm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.metadata.ResourceBuilder.ResourceMethodParameterBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import eu.europeana.direct.backend.repositories.CulturalHeritageObjectRepository;
import eu.europeana.direct.harvesting.jobs.EdmHarvestWorker;
import eu.europeana.direct.harvesting.jobs.HarvestThread;
import eu.europeana.direct.harvesting.jobs.IndexThread;
import eu.europeana.direct.harvesting.mapper.EdmMapper;
import eu.europeana.direct.harvesting.mapper.IMapper;
import eu.europeana.direct.harvesting.mapper.OaiDcMapper;
import eu.europeana.direct.harvesting.source.edm.model.EdmAgent;
import eu.europeana.direct.harvesting.source.edm.model.EdmConcept;
import eu.europeana.direct.harvesting.source.edm.model.EdmEvent;
import eu.europeana.direct.harvesting.source.edm.model.EdmOaiSource;
import eu.europeana.direct.harvesting.source.edm.model.EdmPlace;
import eu.europeana.direct.harvesting.source.edm.model.EdmTimespan;
import eu.europeana.direct.harvesting.source.edm.model.EdmWebResource;
import eu.europeana.direct.helpers.IRestClient;
import eu.europeana.direct.helpers.RestClient;
import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import se.kb.oai.pmh.Header;
import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.Record;
import se.kb.oai.pmh.RecordsList;
import se.kb.oai.pmh.ResumptionToken;
import se.kb.oai.pmh.Set;
import se.kb.oai.pmh.SetsList;

/**
 * SourceReader is used for reading metadata from url of harvesting sources
 *
 */
@SuppressWarnings("rawtypes")
public class SourceReader implements ISourceReader<EdmOaiSource> {
	final static Logger logger = Logger.getLogger(SourceReader.class);
	
	// Flag for interrupting job
	private boolean interrupted = false;

	// Method retrives metadata for each record from OAI-PMH source
	@Override
	public List<EdmOaiSource> readSourceDataFromOaiPmh(String url, String metadataFormat, String harvester) throws Exception {
		OaiPmhServer server = new OaiPmhServer(url);
		List<EdmOaiSource> edmOaiSourceList = new ArrayList<EdmOaiSource>();
		IMapper<EdmOaiSource> edmMapper = new EdmMapper();
		IMapper<EdmOaiSource> oaiDcMapper = new OaiDcMapper();
		ISourceReader<EdmOaiSource> sourceReader = new SourceReader();
		// workers for harvesting
		HarvestThread workerThread = new HarvestThread(3);

		try {												
				RecordsList records = null;
				List<Record> recordsList = null;	
				ResumptionToken resumptionToken = null;														
				SetsList sets = server.listSets();					
				List<Set> setList = sets.asList();								
				
				for(Set s : setList) {					
					records = server.listRecords(metadataFormat, null, null, s.getSpec());					
					int j = 0;
					while (j == 0 || resumptionToken != null) {						
						if (j > 0 && resumptionToken != null) {																	
							records = server.listRecords(resumptionToken);							
						}
						j++;						
						recordsList = records.asList();						
						
						if (recordsList != null && recordsList.size() > 0) {
							workerThread.execute(new EdmHarvestWorker(recordsList, metadataFormat, harvester, sourceReader,
								edmMapper, oaiDcMapper));
						}
	
						if (interrupted) {
							File file = new File("/var/direct/wildfly/token.txt");
							// if file doesnt exists, then create it
							if (!file.exists()) {
								file.createNewFile();
							}
	
							FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write("Last Set spec: " + s.getSpec());
							bw.write("Last resumption token id:" + resumptionToken.getId());
							bw.close();
							break;
						}
						// if(j%5 == 0){
						// workerThread.execute(new IndexThread());
						//}					
						resumptionToken = records.getResumptionToken();							
					}						
				}
																
		} catch (Exception e1) {
			e1.printStackTrace();
		}finally {					
			//commit index at the end just to be sure
			LuceneIndexing.getInstance().commitIndex(true);			
		}

		return edmOaiSourceList;
	}
	
	@Override
	public List<EdmOaiSource> readSourceDataFromDDB(String url, String metadataFormat, String key, CulturalHeritageObjectLogic choLogic) throws Exception {
		
		IRestClient restClient = new RestClient();
		List<EdmOaiSource> edmOaiSourceList = new ArrayList<EdmOaiSource>();
		
		// get all api records with their search method
		String jsonRecords = restClient.httpGetRequest(url+"/search", new HashMap<String, String>() {
			{
				put("Authorization", "OAuth oauth_consumer_key=\"" + key + "\"");
	            put("Accept", "application/json");
	        }
	    });
		 
		try {
	       	JSONObject obj = new JSONObject(jsonRecords);
	       	JSONArray results = (JSONArray) obj.get("results");
	       	JSONObject records = (JSONObject) results.get(0);	       		       	
	       	JSONArray docs = records.getJSONArray("docs");
	       	List<String> itemIdentifiers = new ArrayList<String>();
	        	        	
	       	// saving record id's to list so we can retrieve data record by record in our next rest call
        	for(int i=0; i< docs.length(); i++) {
        		JSONObject o = (JSONObject) docs.get(i);
        		itemIdentifiers.add(o.getString("id"));
	       	}
	        
        	IMapper<EdmOaiSource> edmMapper = new EdmMapper();
        	int i = 0;
        	// get record by id
        	for(String id : itemIdentifiers) {        		        		
        		String url2 = url+"/items/"+id+"/edm";        		
	            String metadata = restClient.httpGetRequest(url2, new HashMap<String, String>() {
	            	{
	            		put("Authorization", "OAuth oauth_consumer_key=\"" + key + "\"");
	        	        put("Accept", "application/xml");
	                }
	            });  	 
	            
	            //map record data to edmOai
	            EdmOaiSource edm = readSourceDataEdm(metadata, id);
	            if(edm.getProvidedCHO().getOaiDc().getLanguage().size() < 1){
	            	edm.getProvidedCHO().getOaiDc().getLanguage().add("de");
	            }
	            CulturalHeritageObject cho = edmMapper.mapFromEdm(edm);
	            if(cho != null){
	            	if(cho.getLanguageNonAwareFields() != null){
	            		if(cho.getLanguageNonAwareFields().getDataOwner() != null){
	            			cho.getLanguageNonAwareFields().setDataOwner("DBB");		
	            		}
	            	}	            	
	            }
	            choLogic.mapAndSaveCHO(cho, true, false);
	        }  	        	
		} catch (JSONException e) {
			logger.error(e.getMessage(),e);
		}  
		return edmOaiSourceList;
	}

	// For sources with edm model response
	@Override
	public EdmOaiSource readSourceDataEdm(String xml, String headerIdentifier) throws SAXParseException{
		
		boolean providedChoWasRead = false;
		boolean oreAggregationWasRead = false;
		boolean agentWasRead = false;
		boolean conceptWasRead = false;
		boolean timespanWasRead = false;
		boolean eventWasRead = false;
		boolean placeWasRead = false;
		boolean proxyWasRead = false;
		
		// name of elements that were already read
		java.util.Set<String> readElements = new HashSet<String>();

		EdmOaiSource edmOaiSource = new EdmOaiSource();
		edmOaiSource.setHeaderIdentifier(headerIdentifier);
		String rdfElementNamespace = null;
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
			Document xmlDoc = builder.parse(is);

			String root = xmlDoc.getDocumentElement().getNodeName();
			NodeList nList = null;

			// We need our root element to be <RDF> to start reading metadata.
			// If root element is <edm>, get child element <RDF>
			if (root.contains("edm")) {
				for (int i = 0; i < xmlDoc.getDocumentElement().getChildNodes().getLength(); i++) {
					if (xmlDoc.getDocumentElement().getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
						String rdfElement = xmlDoc.getDocumentElement().getChildNodes().item(i).getNodeName();
						nList = xmlDoc.getElementsByTagName(rdfElement);
						break;
					}
				}
			// <RDF> element is root
			} else {
				nList = xmlDoc.getElementsByTagName(root);
			}

			Node node = (Element) nList.item(0);
			String rdfTag = node.getNodeName();
			
			// Get namespace of RDF element
			rdfElementNamespace = rdfTag.split(":")[0];
			
			String elementName = "";
			
			// Root <RDF>			
			mainloop: for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				if (node.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
					if (interrupted) {
						break mainloop;
					}					
			
					elementName = node.getChildNodes().item(i).getNodeName();					
					
					
					NodeList rdfElements = xmlDoc.getElementsByTagName(elementName);

					// if we already read rdfElements with this element name, then skip
					if(readElements.contains(elementName)){
						continue mainloop;
					}
					
					readElements.add(elementName);
					// Loop through <RDF> children elements
					for (int p = 0; p < rdfElements.getLength(); p++) {
						if (rdfElements.item(p).getNodeType() == Node.ELEMENT_NODE) {
							if (interrupted) {
								break mainloop;
							}

							String tag = rdfElements.item(p).getNodeName();							
							
							if (tag.toLowerCase().matches("edm:providedcho") || tag.toLowerCase().matches("ore:proxy")) {
								
								// If we already read source data for this type
								// of element, skip to reading another element
								if (providedChoWasRead) {
									if(tag.toLowerCase().matches("ore:proxy")){										
										if(proxyWasRead){
											continue mainloop;
										}
									}else{
										continue mainloop;	
									}									
								}

								// set true when 1st time reading data for this	element
								providedChoWasRead = true;
								if(tag.toLowerCase().matches("ore:proxy")){
									proxyWasRead = true;
								}
										
								Element providedChoEl = (Element) rdfElements.item(p);

								if (providedChoEl.hasAttribute(rdfElementNamespace + ":about")) {
									edmOaiSource.getProvidedCHO().setAbout(providedChoEl.getAttribute(rdfElementNamespace + ":about"));
								}
								NodeList providedChoNodes = rdfElements.item(p).getChildNodes();
								// loop through <edm:ProvidedCHO> children elements
								for (int k = 0; k < providedChoNodes.getLength(); k++) {
									if (interrupted) {
										break mainloop;
									}

									if (providedChoNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {

										// dublin core element
										Element element = (Element) providedChoNodes.item(k);

										// names of <edm:providedCHO>
										// children
										// elements
										String providedCHOtag = providedChoNodes.item(k).getNodeName();
										try {
											Node n = providedChoNodes.item(k).getFirstChild();
											String elementValue = "";

											if (n != null) {
												elementValue = n.getTextContent();
											}

											switch (providedCHOtag) {

											
											case "dc:contributor":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getOaiDc().getContributor().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getOaiDc().getContributor().add(elementValue);
													}
												}
												break;
											case "dc:coverage":

												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getOaiDc().getCoverage().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getOaiDc().getCoverage().add(elementValue);
													}
												}
												break;
											case "dc:creator":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													if(!element.getAttribute(rdfElementNamespace + ":resource").contains("http")){
														if(element.hasAttribute("xml:lang")){
															if(edmOaiSource.getProvidedCHO().getOaiDc().getCreator().containsKey(element.getAttribute("xml:lang"))){
																edmOaiSource.getProvidedCHO().getOaiDc().getCreator().get(element.getAttribute("xml:lang")).add(element.getAttribute(rdfElementNamespace + ":resource"));
															}else{
																List<String> list = new ArrayList<>();
																list.add(element.getAttribute(rdfElementNamespace + ":resource"));
																edmOaiSource.getProvidedCHO().getOaiDc().getCreator().put(element.getAttribute("xml:lang"),list);
															}
														}else{
															List<String> list = new ArrayList<>();
															list.add(element.getAttribute(rdfElementNamespace + ":resource"));
															edmOaiSource.getProvidedCHO().getOaiDc().getCreator().put("key "+k,list);
														}														
													}
													
												} else {
													if (elementValue.length() > 0) {														
														if(element.hasAttribute("xml:lang")){
															if(edmOaiSource.getProvidedCHO().getOaiDc().getCreator().containsKey(element.getAttribute("xml:lang"))){
																edmOaiSource.getProvidedCHO().getOaiDc().getCreator().get(element.getAttribute("xml:lang")).add(elementValue);
															}else{
																List<String> list = new ArrayList<>();
																list.add(elementValue);
																edmOaiSource.getProvidedCHO().getOaiDc().getCreator().put(element.getAttribute("xml:lang"),list);
															}
														}else{
															List<String> list = new ArrayList<>();
															list.add(elementValue);
															edmOaiSource.getProvidedCHO().getOaiDc().getCreator().put("key "+k,list);
														}	
													}
												}
												break;
											case "dc:date":
												List<String> dateList = new ArrayList<>();
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													List<String> list = new ArrayList<>();
													list.add(elementValue);
													edmOaiSource.getProvidedCHO().getOaiDc().getDate().put("key " + k,list);
													list = null;
												} else if (element.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getOaiDc().getDate().containsKey(element.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getOaiDc().getDate().get(element.getAttribute("xml:lang")).add(elementValue);
													}else{
														dateList.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getDate()
																.put(element.getAttribute("xml:lang"), dateList);	
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getDate().put("key " + k, list);
														list = null;
													}
												}
												dateList = null;
												break;
											case "dc:description":	
												List<String> descriptionList = new ArrayList<>();

												if (element.hasAttribute("xml:lang")) {													
													if(edmOaiSource.getProvidedCHO().getOaiDc().getDescription().containsKey(element.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getOaiDc().getDescription().get(element.getAttribute("xml:lang")).add(elementValue);
													}else{
														descriptionList.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getDescription()
																.put(element.getAttribute("xml:lang"), descriptionList);
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getDescription().put("key " + k,list);
														list = null;
													}
												}
												descriptionList = null;
												break;
											case "dc:format":	
												List<String> formatList = new ArrayList<>();

												if (element.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getOaiDc().getFormat().containsKey(element.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getOaiDc().getFormat().get(element.getAttribute("xml:lang")).add(elementValue);
													}else{
														formatList.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getFormat()
																.put(element.getAttribute("xml:lang"), formatList);	
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getFormat().put("key " + k,list);
														list = null;
													}
												}
												formatList = null;
												break;
											case "dc:identifier":
												if (elementValue.length() > 0) {
													edmOaiSource.getProvidedCHO().getOaiDc().getIdentifier().add(elementValue);
												}
												break;
											case "dc:language":
												if (elementValue.length() > 0) {
													edmOaiSource.getProvidedCHO().getOaiDc().getLanguage().add(elementValue);
												}
												break;
											case "dc:publisher":
												List<String> publisher = new ArrayList<>();

												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													List<String> list = new ArrayList<>();
													list.add(elementValue);
													edmOaiSource.getProvidedCHO().getOaiDc().getPublisher().put("key " + k,list);
													list = null;
												} else if (element.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getOaiDc().getPublisher().containsKey(element.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getOaiDc().getPublisher().get(element.getAttribute("xml:lang")).add(elementValue);
													}else{
														publisher.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getPublisher()
																.put(element.getAttribute("xml:lang"), publisher);	
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getPublisher().put("key " + k,list);
														list = null;
													}
												}
												publisher = null;
												break;
											case "dc:relation":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getOaiDc().getRelation().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getOaiDc().getRelation().add(elementValue);
													}
												}
												break;
											case "dc:rights":
												List<String> rightsList = new ArrayList<>();

												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													List<String> list = new ArrayList();
													list.add(elementValue);
													edmOaiSource.getProvidedCHO().getOaiDc().getRights().put("key " + k,list);
													list = null;
												} else if (element.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getOaiDc().getRights().containsKey(element.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getOaiDc().getRights().get(element.getAttribute("xml:lang")).add(elementValue);
													}else{
														rightsList.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getRights()
																.put(element.getAttribute("xml:lang"), rightsList);	
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getRights().put("key " + k,list);
														list = null;
													}
												}
												rightsList = null;
												break;
											case "dc:source":
												List<String> sourceList = new ArrayList<>();

												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													List<String> list = new ArrayList<>();
													list.add(elementValue);
													edmOaiSource.getProvidedCHO().getOaiDc().getSource().put("key " + k,list);
													list = null;
												} else if (element.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getOaiDc().getSource().containsKey(element.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getOaiDc().getSource().get(element.getAttribute("xml:lang")).add(elementValue);
													}else{
														sourceList.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getSource()
																.put(element.getAttribute("xml:lang"), sourceList);	
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getSource().put("key " + k,list);
														list = null;
													}
												}
												sourceList = null;
												break;
											case "dc:subject":													
												List<String> subjectList = new ArrayList<String>();														
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													if(!element.getAttribute(rdfElementNamespace + ":resource").contains("http")){
														
														subjectList.add(element.getAttribute(rdfElementNamespace + ":resource"));
														if(element.hasAttribute("xml:lang")){
															
															if(edmOaiSource.getProvidedCHO().getOaiDc().getSubject().containsKey(element.getAttribute("xml:lang"))){
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject().get(element.getAttribute("xml:lang")).add(elementValue);
															}else{				
																subjectList.add(elementValue);
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject().put(element.getAttribute("xml:lang"),subjectList);	
															}
														}else{
															if(edmOaiSource.getProvidedCHO().getOaiDc().getSubject().containsKey("xxx")){																
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject().get("xxx").add(elementValue);	
															}else{																											
																subjectList.add(elementValue);
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject().put("xxx",subjectList);
															}
														}	
													}													
												} else {
													if (elementValue.length() > 0) {
														if (element.hasAttribute("xml:lang")) {
															if (edmOaiSource.getProvidedCHO().getOaiDc().getSubject()
																	.containsKey(element.getAttribute("xml:lang"))) {
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject()
																		.get(element.getAttribute("xml:lang"))
																		.add(elementValue);
															} else {
																subjectList.add(elementValue);
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject()
																		.put(element.getAttribute("xml:lang"),
																				subjectList);
															}
														} else {
															if (edmOaiSource.getProvidedCHO().getOaiDc().getSubject()
																	.containsKey("xxx")) {
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject()
																		.get("xxx").add(elementValue);
															} else {
																subjectList.add(elementValue);
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject()
																		.put("xxx", subjectList);
															}
														}
													}													
												}
												subjectList = null;
												break;
											case "dcterms:subject":												
												List<String> subjectList2 = new ArrayList<String>();											
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													if(!element.getAttribute(rdfElementNamespace + ":resource").contains("http")){														
														subjectList2.add(element.getAttribute(rdfElementNamespace + ":resource"));
														if(element.hasAttribute("xml:lang")){
															
															if(edmOaiSource.getProvidedCHO().getOaiDc().getSubject().containsKey(element.getAttribute("xml:lang"))){
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject().get(element.getAttribute("xml:lang")).add(elementValue);
															}else{											
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject().put(element.getAttribute("xml:lang"),subjectList2);	
															}
														}else{
															if(edmOaiSource.getProvidedCHO().getOaiDc().getSubject().containsKey("en")){
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject().get("en").add(elementValue);	
															}else{											
																subjectList2.add(elementValue);
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject().put("en",subjectList2);
															}
														}	
													}													
												} else {
													if (elementValue.length() > 0) {
														subjectList2.add(elementValue);
														if (element.hasAttribute("xml:lang")) {

															if (edmOaiSource.getProvidedCHO().getOaiDc().getSubject()
																	.containsKey(element.getAttribute("xml:lang"))) {
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject()
																		.get(element.getAttribute("xml:lang"))
																		.add(elementValue);
															} else {
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject()
																		.put(element.getAttribute("xml:lang"),
																				subjectList2);
															}
														} else {
															if (edmOaiSource.getProvidedCHO().getOaiDc().getSubject()
																	.containsKey("xxx")) {
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject()
																		.get("xxx").add(elementValue);
															} else {
																subjectList2.add(elementValue);
																edmOaiSource.getProvidedCHO().getOaiDc().getSubject()
																		.put("xxx", subjectList2);
															}
														}
													}
												}
												subjectList2 = null;
												break;
											case "dc:title":												
												if (element.hasAttribute("xml:lang")) {
													edmOaiSource.getProvidedCHO().getOaiDc().getTitle()
															.put(element.getAttribute("xml:lang"), elementValue);													
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getOaiDc().getTitle().put("key " + k, elementValue);
													}
												}
												break;
											case "dc:type":		
												List<String> typeList = new ArrayList<>();

												if (element.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getOaiDc().getType().containsKey(element.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getOaiDc().getType().get(element.getAttribute("xml:lang")).add(elementValue);
													}else{
														typeList.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getType().put(element.getAttribute("xml:lang"), typeList);	
													}													
												} else if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													if(!element.getAttribute(rdfElementNamespace + ":resource").contains("http")){
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getType().put("key " + k,list);	
														list = null;
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getType().put("key " + k, list);
														list = null;
													}
												}
												typeList = null;
												break;
											case "dcterms:alternative":	
												List<String> alternativeList = new ArrayList<>();

												if (element.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getAlternativeTitle().containsKey(element.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getAlternativeTitle().get(element.getAttribute("xml:lang")).add(elementValue);
													}else{
														alternativeList.add(elementValue);
														edmOaiSource.getProvidedCHO().getAlternativeTitle()
																.put(element.getAttribute("xml:lang"), alternativeList);	
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getAlternativeTitle().put("key " + k, list);
														list = null;
													}
												}
												alternativeList = null;
												break;
											case "dcterms:conformsTo":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getConformsTo().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getConformsTo().add(elementValue);
													}
												}
												break;
											case "dcterms:created":
												List<String> createdList = new ArrayList<>();

												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													List<String> list = new ArrayList<>();
													list.add(elementValue);
													edmOaiSource.getProvidedCHO().getOaiDc().getDate().put("key " + k,list);
													list = null;
												} else if (element.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getOaiDc().getDate().containsKey(element.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getOaiDc().getDate().get(element.getAttribute("xml:lang")).add(elementValue);
													}else{
														createdList.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getDate()
																.put(element.getAttribute("xml:lang"), createdList);	
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getDate().put("key " + k, createdList);
														list = null;
													}
												}
												createdList = null;
												break;
											case "dcterms:extent":
												List<String> extentList = new ArrayList<>();

												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													List<String> list = new ArrayList<>();
													list.add(elementValue);
													edmOaiSource.getProvidedCHO().getExtent().put("key " + k,list);
													list = null;
												} else if (element.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getExtent().containsKey(element.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getExtent().get(element.getAttribute("xml:lang")).add(elementValue);
													}else{
														extentList.add(elementValue);
														edmOaiSource.getProvidedCHO().getExtent().put(element.getAttribute("xml:lang"),
																extentList);	
													}													
												} else {
													if (elementValue.length() > 0) {														
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getExtent().put("key " + k, list);
														list = null;
													}
												}
												extentList = null;
												break;
											case "dcterms:hasFormat":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getHasFormat().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getHasFormat().add(elementValue);
													}
												}
												break;
											case "dcterms:hasPart":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getHasPart().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getHasPart().add(elementValue);
													}
												}
												break;
											case "dcterms:hasVersion":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getHasVersion().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getHasVersion().add(elementValue);
													}
												}
												break;
											case "dcterms:isFormatOf":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIsFormatOf().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getIsFormatOf().add(elementValue);
													}
												}
												break;
											case "dcterms:isPartOf":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIsPartOf().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getIsPartOf().add(elementValue);
													}
												}
												break;
											case "dcterms:isReferencedBy":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIsReferencedBy().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getIsReferencedBy().add(elementValue);
													}
												}
												break;
											case "dcterms:isReplacedBy":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIsReplacedBy().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getIsReplacedBy().add(elementValue);
													}
												}
												break;
											case "dcterms:isRequiredBy":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIsRequiredBy().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getIsRequiredBy().add(elementValue);
													}
												}
												break;
											case "dcterms:issued":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIssued().put("key " + k,
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else if (element.hasAttribute("xml:lang")) {
													
													edmOaiSource.getProvidedCHO().getIssued().put(element.getAttribute("xml:lang"),
															elementValue);
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getIssued().put("key " + k, elementValue);
													}
												}
												break;
											case "dcterms:isVersionOf":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIsVersionOf().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getIsVersionOf().add(elementValue);
													}
												}
												break;
											case "dcterms:medium":
												List<String> mediumList = new ArrayList<>();
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													List<String> list = new ArrayList<>();
													list.add(elementValue);
													edmOaiSource.getProvidedCHO().getMedium().put("key " + k,list);
													list = null;
												} else if (element.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getMedium().containsKey(element.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getMedium().get(element.getAttribute("xml:lang")).add(elementValue);
													}else{
														mediumList.add(elementValue);
														edmOaiSource.getProvidedCHO().getMedium().put(element.getAttribute("xml:lang"),
																mediumList);	
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getMedium().put("key " + k, list);
														list = null;
													}
												}
												mediumList = null;
												break;
											case "dcterms:provenance":
												List<String> provenanceList = new ArrayList<>();

												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													List<String> list = new ArrayList<>();
													list.add(elementValue);												
													edmOaiSource.getProvidedCHO().getProvenance().put("key " + k,list);
													list = null;
												} else if (element.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getProvenance().containsKey(element.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getProvenance().get(element.getAttribute("xml:lang")).add(elementValue);
													}else{
														provenanceList.add(elementValue);
														edmOaiSource.getProvidedCHO().getProvenance().put(element.getAttribute("xml:lang"),
																provenanceList);	
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getProvenance().put("key " + k, list);
														list = null;
													}
												}
												provenanceList = null;
												break;
											case "dcterms:references":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getReferences().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getReferences().add(elementValue);
													}
												}
												break;
											case "dcterms:replaces":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getReplaces().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getReplaces().add(elementValue);
													}
												}
												break;
											case "dcterms:requires":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getRequires().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getRequires().add(elementValue);
													}
												}
												break;
											case "dcterms:spatial":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													if(!element.getAttribute(rdfElementNamespace + ":resource").contains("http")){
														if(!edmOaiSource.getProvidedCHO().getSpatial().contains(element.getAttribute(rdfElementNamespace + ":resource"))){
															edmOaiSource.getProvidedCHO().getSpatial().add(
																	element.getAttribute(rdfElementNamespace + ":resource"));	
														}														
													}
													
												} else {
													if (elementValue.length() > 0) {
														if(!edmOaiSource.getProvidedCHO().getSpatial().contains(elementValue)){
															edmOaiSource.getProvidedCHO().getSpatial().add(elementValue);	
														}														
													}
												}
												break;
											case "dcterms:tableOfContents":
												if (elementValue.length() > 0) {
													edmOaiSource.getProvidedCHO().getSpatial().add(elementValue);
												}
												break;
											case "dcterms:temporal":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getTemporal().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getTemporal().add(elementValue);
													}
												}
												break;
											case "edm:currentLocation":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().setCurrentLocation(
															element.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "edm:hasMet":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getHasMet().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "edm:hasType":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getHasType().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getHasType().add(elementValue);
													}
												}
												break;
											case "edm:incorporates":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIncorporates().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "edm:isDerivativeOf":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIsDerivativeOf().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "edm:isNextInSequence":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIsNextInSequence().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "edm:isRelatedTo":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIsRelatedTo().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														edmOaiSource.getProvidedCHO().getIsRelatedTo().add(elementValue);
													}
												}
												break;
											case "edm:isRepresentationOf":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().setIsRepresentationOf(
															element.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "edm:isSimilarTo":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIsSimilarTo().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "edm:isSuccessorOf":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getIsSuccessorOf().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "edm:realizes":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getRealizes().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "edm:type":
												if (elementValue.length() > 0) {
													edmOaiSource.setEdmType(elementValue);
												}
												break;
											case "owl:sameAs":
												if (element.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getProvidedCHO().getSameAs().add(
															element.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											}
										} catch (Exception ex) {
											logger.error(ex.getMessage(),ex);
										}
									}
								}
							}

							if (tag.matches("skos:Concept")) {								
								// if we already read source data for this type
								// of element, skip to reading another element
								if (conceptWasRead) {
									//continue mainloop;
								}
								// set true when 1st time reading data for this
								// element
								conceptWasRead = true;
								
								EdmConcept concept = new EdmConcept();																
								
								
								Element skosConceptElement = (Element) rdfElements.item(p);
								if(skosConceptElement.hasAttribute(rdfElementNamespace+":about")){									
									if(!skosConceptElement.getAttribute(rdfElementNamespace+":about").contains("http")){										
										concept.setAbout(skosConceptElement.getAttribute(rdfElementNamespace+":about"));
									}
								}
								
								NodeList conceptNodes = rdfElements.item(p).getChildNodes();
								
								for (int k = 0; k < conceptNodes.getLength(); k++) {
									if (interrupted) {
										break mainloop;
									}

									if (conceptNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
										Element aggEl = (Element) conceptNodes.item(k);										
										String conceptTag = conceptNodes.item(k).getNodeName();

										try {

											// <skosConcept> element value
											String elementValue = "";
											Node n = conceptNodes.item(k).getFirstChild();

											// check if node has text value
											if (n != null) {
												elementValue = n.getTextContent();
											}

											switch (conceptTag) {

											case "skos:prefLabel":
												List<String> conceptPrefLabel = new ArrayList<String>();
												
												if (aggEl.hasAttribute("xml:lang")) {
													if(concept.getPrefLabel().containsKey(aggEl.getAttribute("xml:lang"))){														
														concept.getPrefLabel().get(aggEl.getAttribute("xml:lang")).add(elementValue);
													}else{														
														conceptPrefLabel.add(elementValue);
														concept.getPrefLabel().put(aggEl.getAttribute("xml:lang"),
																conceptPrefLabel);	
													}
													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<String>();
														list.add(elementValue);
														concept.getPrefLabel().put("key " + k, list);
														list = null;
													}
												}
												
												conceptPrefLabel = null;
												break;
											case "skos:altLabel":																		
												List<String> conceptAltList = new ArrayList<String>();
												
												if (aggEl.hasAttribute("xml:lang")) {
													if(concept.getAltLabel().containsKey(aggEl.getAttribute("xml:lang"))){
														concept.getAltLabel().get(aggEl.getAttribute("xml:lang")).add(elementValue);
													}else{
														conceptAltList.add(elementValue);
														concept.getAltLabel().put(aggEl.getAttribute("xml:lang"),
																conceptAltList);	
													}
													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<String>();
														list.add(elementValue);
														concept.getAltLabel().put("key " + k, list);
														list = null;
													}
												}
												conceptAltList = null;
												break;
											case "skos:broader":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													concept.getBroader()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "skos:narrower":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													concept.getNarrower()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "skos:related":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													concept.getRelated()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "skos:broadMatch":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													concept.getBroadMatch()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "skos:narrowMatch":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													concept.getNarrowMatch()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "skos:relatedMatch":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													concept.getRelatedMatch()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "skos:exactMatch":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													concept.getExactMatch()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "skos:closeMatch":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													concept.getCloseMatch()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "skos:note":
												if (elementValue.length() > 0) {
													concept.getNote().add(elementValue);
												}
												break;
											case "skos:notation":
												if (aggEl.hasAttribute(rdfElementNamespace + ":datatype")) {
													concept.getNotation().put(
															aggEl.getAttribute(rdfElementNamespace + ":datatype"),
															elementValue);
												} else {
													if (elementValue.length() > 0) {
														concept.getNotation().put("key " + k, elementValue);
													}
												}
												break;
											case "skos:inScheme":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													concept.getInScheme()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											}

										} catch (Exception ex) {
											logger.error(ex.getMessage(),ex);
										}
										aggEl = null;
									}
								}
								if(concept.getPrefLabel().size() > 0){
									edmOaiSource.getEdmConcept().add(concept);	
									concept = null;
									conceptNodes = null;
									skosConceptElement = null;
								}								
							}

							if (tag.matches("edm:Event")) {

								// if we already read source data for this type
								// of element, skip to reading another element
								if (eventWasRead) {
									continue mainloop;
								}

								// set true when 1st time reading data for this
								// element
								eventWasRead = true;
								EdmEvent event = new EdmEvent();

								Element eventElement = (Element) rdfElements.item(p);
								if(eventElement.hasAttribute(rdfElementNamespace+":about")){
									event.setAbout(eventElement.getAttribute(rdfElementNamespace+":about"));
								}
								
								NodeList eventNodes = rdfElements.item(p).getChildNodes();

								for (int k = 0; k < eventNodes.getLength(); k++) {
									if (interrupted) {
										break mainloop;
									}
									
									if (eventNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
										Element aggEl = (Element) eventNodes.item(k);
										String eventTag = eventNodes.item(k).getNodeName();
										try {

											// <edmEvent> element value
											String elementValue = "";
											Node n = eventNodes.item(k).getFirstChild();

											// check if node has text value
											if (n != null) {
												elementValue = n.getTextContent();
											}

											switch (eventTag) {

											case "edm:hasType":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													event.getHasType()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														event.getHasType().add(elementValue);
													}
												}
												break;
											case "edm:happenedAt":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {													
													event.getHappenedAt()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														event.getHappenedAt().add(elementValue);
													}
												}
												break;
											case "edm:occuredAt":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													event.getOccuredAt()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														event.getOccuredAt().add(elementValue);
													}
												}
												break;
											case "crm:P11_had_participant":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													event.getP11_had_participant()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														event.getP11_had_participant().add(elementValue);
													}
												}
												break;
											}

										} catch (Exception ex) {
											logger.error(ex.getMessage(),ex);
										}
									}
								}
								edmOaiSource.getEdmEvent().add(event);
								event = null;
								eventElement = null;
								eventNodes = null;
							}

							if (tag.matches("edm:Place")) {
								// if we already read source data for this type
								// of element, skip to reading another element
								if (placeWasRead) {
									//continue mainloop;
								}

								// set true when 1st time reading data for this
								// element
								placeWasRead = true;
								
								NodeList placeNodes = rdfElements.item(p).getChildNodes();
								EdmPlace place = new EdmPlace();								
								
								Element placeElement = (Element) rdfElements.item(p);
								if(placeElement.hasAttribute(rdfElementNamespace+":about")){
									if(placeElement.getAttribute(rdfElementNamespace+":about").contains("http")){
										place.setAbout(placeElement.getAttribute(rdfElementNamespace+":about"));	
									}									
								}
								
								for (int k = 0; k < placeNodes.getLength(); k++) {
									if (interrupted) {
										break mainloop;
									}

									if (placeNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
										Element aggEl = (Element) placeNodes.item(k);
										String placeTag = placeNodes.item(k).getNodeName();

										try {

											// <edmPlace> element value
											String elementValue = "";
											Node n = placeNodes.item(k).getFirstChild();

											// check if node has text value
											if (n != null) {
												elementValue = n.getTextContent();
											}

											switch (placeTag) {

											case "wgs84_pos:lat":
												if (elementValue.length() > 0) {
													place.setLatitude(Float.parseFloat(elementValue));
												}
												break;
											case "wgs84_pos:long":
												if (elementValue.length() > 0) {
													place.setLongitude(Float.parseFloat(elementValue));
												}
												break;
											case "wgs84_pos:alt":
												if (elementValue.length() > 0) {
													place.setAltitude(Float.parseFloat(elementValue));
												}
												break;
											case "skos:prefLabel":
												List<String> placePrefLabel = new ArrayList<>();
												
												if (aggEl.hasAttribute("xml:lang")) {
													if(place.getPrefLabel().containsKey(aggEl.getAttribute("xml:lang"))){
														place.getPrefLabel().get(aggEl.getAttribute("xml:lang")).add(elementValue);
													}else{
														placePrefLabel.add(elementValue);
														place.getPrefLabel().put(aggEl.getAttribute("xml:lang"),
																placePrefLabel);	
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														place.getPrefLabel().put("key " + k, list);
														list = null;
													}
												}
												placePrefLabel = null;
												break;
											case "skos:altLabel":
												List<String> placeAltList = new ArrayList<String>();
												
												if (aggEl.hasAttribute("xml:lang")) {
													if(place.getAltLabel().containsKey(aggEl.getAttribute("xml:lang"))){
														place.getAltLabel().get(aggEl.getAttribute("xml:lang")).add(elementValue);
													}else{
														placeAltList.add(elementValue);
														place.getAltLabel().put(aggEl.getAttribute("xml:lang"),
																placeAltList);
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														place.getAltLabel().put("key " + k, list);
														list = null;
													}
												}
												placeAltList = null;
												break;
											case "skos:note":	
												List<String> noteList = new ArrayList<>();

												if (aggEl.hasAttribute("xml:lang")) {
													if(place.getNote().containsKey(aggEl.getAttribute("xml:lang"))){
														place.getNote().get(aggEl.getAttribute("xml:lang")).add(elementValue);
													}else{
														noteList.add(elementValue);
														place.getNote().put(aggEl.getAttribute("xml:lang"), noteList);															
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														place.getNote().put("key " + k, list);
														list = null;
													}
												}
												noteList = null;
												break;
											case "dcterms:hasPart":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													place.getHasPart()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "dcterms:isPartOf":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													place.getSameAs()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "edm:isNextInSequence":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													place.getSameAs()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "owl:sameAs":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													place.getSameAs()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											}

										} catch (Exception ex) {
											logger.error(ex.getMessage(),ex);
										}
										aggEl = null;
									}
								}
								if(place.getPrefLabel().size() > 0){
									edmOaiSource.getEdmPlace().add(place);	
									place = null;
									placeNodes = null;
									placeElement = null;
								}								
							}

							if (tag.matches("edm:WebResource")) {
								
								// if we already read source data for this type
								// of element, skip to reading another element
								/*if (webResourceWasRead) {
									continue mainloop;
								}*/

								// set true when 1st time reading data for this
								// element

								EdmWebResource webResource = new EdmWebResource();
								Element webResourceElement = (Element) rdfElements.item(p);								
								
								if (webResourceElement.hasAttribute(rdfElementNamespace + ":about")) {									
									if(webResourceElement.getAttribute(rdfElementNamespace + ":about").contains("resolve") || webResourceElement.getAttribute(rdfElementNamespace + ":about").contains("provided_image") || webResourceElement.getAttribute(rdfElementNamespace + ":about").toLowerCase().contains("jpg")){
										webResource.getType().add("direct");
										webResource.setLink(webResourceElement.getAttribute(rdfElementNamespace + ":about"));	
									}else{
										webResource.setLink(webResourceElement.getAttribute(rdfElementNamespace + ":about"));
										webResource.getType().add("webpage");
									}									
									webResource.setEdmRights("http://rightsstatements.org/vocab/CNE/1.0/");
								}
								NodeList webResourceNodes = rdfElements.item(p).getChildNodes();

								for (int k = 0; k < webResourceNodes.getLength(); k++) {
									if (interrupted) {
										break mainloop;
									}

									if (webResourceNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
										Element aggEl = (Element) webResourceNodes.item(k);
										String webResourceTag = webResourceNodes.item(k).getNodeName();
										try {
											// <edmWebresource> element value
											String elementValue = "";

											Node n = webResourceNodes.item(k).getFirstChild();

											// check if node has text value
											if (n != null) {
												elementValue = n.getTextContent();
											}

											switch (webResourceTag) {

											case "dc:creator":
												if (aggEl.hasAttribute("xml:lang")) {													
													webResource.getCreator().put(aggEl.getAttribute("xml:lang"),
															elementValue);
												} else if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													if(!aggEl.getAttribute(rdfElementNamespace + ":resource").contains("http")){
														webResource.getCreator().put("key " + k,
																aggEl.getAttribute(rdfElementNamespace + ":resource"));	
													}
													
												}
												break;
											case "dc:description":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getDescription().put(
															aggEl.getAttribute(rdfElementNamespace + ":resource"),
															elementValue);
												} else {
													if (elementValue.length() > 0) {
														webResource.getDescription().put("key " + k, elementValue);
													}
												}
												break;
											case "dc:format":
												if (aggEl.hasAttribute("xml:lang")) {
													webResource.getFormat().put(aggEl.getAttribute("xml:lang"),
															elementValue);
												} else if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getFormat().put("key " + k,
															aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														webResource.getFormat().put("key " + k, elementValue);
													}
												}
												break;
											case "dc:rights":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getRights()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														webResource.getRights().add(elementValue);
													}
												}
												break;
												
											case "dcterms:rights":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getRights()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														webResource.getRights().add(elementValue);
													}
												}
												break;
											case "dc:source":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getSource()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														webResource.getSource().add(elementValue);
													}
												}
												break;
											case "dcterms:conformsTo":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getConformsTo()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														webResource.getConformsTo().add(elementValue);
													}
												}
												break;
											case "dcterms:created":
												if (elementValue.length() > 0) {
													webResource.getCreated().add(elementValue);
												}
												break;
											case "dc:date":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getCreated()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "dcterms:extent":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getExtent()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														webResource.getExtent().add(elementValue);
													}
												}
												break;
											case "dcterms:hasPart":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getHasPart()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "dcterms:isFormatOf":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getIsFormatOf()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														webResource.getIsFormatOf().add(elementValue);
													}
												}
												break;
											case "dcterms:isPartOf":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getIsPartOf()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "dcterms:isReferencedBy":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getIsReferencedBy()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														webResource.getIsReferencedBy().add(elementValue);
													}
												}
												break;
											case "dcterms:issued":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getIssued()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														webResource.getIssued().add(elementValue);
													}
												}
												break;
											case "edm:isNextInSequence":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getIsNextInSequence()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "edm:rights":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.setEdmRights(
															aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "owl:sameAs":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getSameAs()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "dc:type":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													webResource.getType()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											}
										} catch (Exception ex) {
											logger.error(ex.getMessage(),ex);
										}
										aggEl = null;
									}
								}			
								if(webResource.getEdmRights() == null && webResource.getRights().size() < 1){
									webResource.setEdmRights("http://rightsstatements.org/vocab/CNE/1.0/");
								}
								
								if(webResource.getLink().toLowerCase().contains("jpg")){
									webResource.getType().clear();
									webResource.getType().add("direct");
								}
								edmOaiSource.getEdmWebResource().add(webResource);
								webResource = null;
								webResourceNodes = null;
								webResourceElement = null;								
							}

							if (tag.matches("edm:EuropeanaAggregation")) {
								NodeList nodes = rdfElements.item(p).getChildNodes();
								for (int k = 0; k < nodes.getLength(); k++) {
									if (interrupted) {
										break mainloop;
									}
									if (nodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
										Element aggEl = (Element) nodes.item(k);
										String nodeTag = nodes.item(k).getNodeName();
										
										switch(nodeTag){
										
										case "edm:language":																																
											edmOaiSource.getProvidedCHO().getOaiDc().getLanguage().add(aggEl.getTextContent());
											break;
										}
									}									
								}
							}
							
							if (tag.matches("edm:TimeSpan")) {
								// if we already read source data for this type
								// of element, skip to reading another element
								if (timespanWasRead) {
									//continue mainloop;
								}
								// set true when 1st time reading data for this
								// element
								timespanWasRead = true;
								NodeList timespanNodes = rdfElements.item(p).getChildNodes();
								EdmTimespan timespan = new EdmTimespan();

								Element timespanElement = (Element) rdfElements.item(p);
								if(timespanElement.hasAttribute(rdfElementNamespace+":about")){
									timespan.setAbout(timespanElement.getAttribute(rdfElementNamespace+":about"));
								}
								
								
								for (int k = 0; k < timespanNodes.getLength(); k++) {
									if (interrupted) {
										break mainloop;
									}

									if (timespanNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
										Element aggEl = (Element) timespanNodes.item(k);
										String timespanTag = timespanNodes.item(k).getNodeName();
										try {
											// <edmTimespan> element value
											String elementValue = "";
											Node n = timespanNodes.item(k).getFirstChild();

											// check if node has text value
											if (n != null) {
												elementValue = n.getTextContent();
											}

											switch (timespanTag) {

											case "skos:prefLabel":
												List<String> timespanPrefLabel = new ArrayList<String>();
												
												if (aggEl.hasAttribute("xml:lang")) {
													if(timespan.getPrefLabel().containsKey(aggEl.getAttribute("xml:lang"))){
														timespan.getPrefLabel().get(aggEl.getAttribute("xml:lang")).add(elementValue);
													}else{
														timespanPrefLabel.add(elementValue);
														timespan.getPrefLabel().put(aggEl.getAttribute("xml:lang"),timespanPrefLabel);
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<String>();
														list.add(elementValue);
														timespan.getPrefLabel().put("key "+k,list);														
													}
												}												
												break;
											case "skos:altLabel":
												List<String> timespanAltList = new ArrayList<String>();
												
												timespanAltList.add(elementValue);
												if (aggEl.hasAttribute("xml:lang")) {
													if(timespan.getAltLabel().containsKey(aggEl.getAttribute("xml:lang"))){
														timespan.getAltLabel().get(aggEl.getAttribute("xml:lang")).add(elementValue);
													}else{
														timespan.getAltLabel().put(aggEl.getAttribute("xml:lang"),timespanAltList);
													}													
												} else {													
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<String>();
														list.add(elementValue);
														timespan.getAltLabel().put("key "+k,list);														
													}
												}
												break;
											case "skos:note":
												if (elementValue.length() > 0) {
													timespan.getNote().add(elementValue);
												}
												break;
											case "skos:notation":
												if (elementValue.length() > 0) {
													timespan.getNote().add(elementValue);
												}
												break;
											case "dcterms:hasPart":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													timespan.getHasPart()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														timespan.getHasPart().add(elementValue);
													}
												}
												break;
											case "dcterms:isPartOf":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													timespan.getIsPartOf()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														timespan.getIsPartOf().add(elementValue);
													}
												}
												break;
											case "edm:begin":
												if (elementValue.length() > 0) {
													timespan.setBegin(elementValue);
												}
												break;
											case "edm:end":
												if (elementValue.length() > 0) {
													timespan.setEnd(elementValue);
												}
												break;
											case "edm:isNextInSequence":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													timespan.getIsNextInSequence()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											case "owl:sameAs":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													timespan.getSameAs()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											}
										} catch (Exception ex) {
											logger.error(ex.getMessage(),ex);
										}
									}
								}
								if(timespan.getPrefLabel().size() > 0){
									edmOaiSource.getEdmTimespan().add(timespan);	
								}								
							}

							if (tag.matches("edm:Agent")) {
								// if we already read source data for this type
								// of element, skip to reading another element
								if (agentWasRead) {
									//continue mainloop;
								}
								// set true when 1st time reading data for this
								// element
								agentWasRead = true;
								NodeList agentNodes = rdfElements.item(p).getChildNodes();
								EdmAgent agent = new EdmAgent();

								Element agentElement = (Element) rdfElements.item(p);
								if(agentElement.hasAttribute(rdfElementNamespace+":about")){
									if(!agentElement.getAttribute(rdfElementNamespace+":about").contains("http")){
										agent.setAbout(agentElement.getAttribute(rdfElementNamespace+":about"));	
									}									
								}
								
								// loop through <edm:Agent> children elements
								for (int k = 0; k < agentNodes.getLength(); k++) {
									if (interrupted) {
										break mainloop;
									}

									if (agentNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
										// <edm:Agent> children element
										Element aggEl = (Element) agentNodes.item(k);
										String agentTag = agentNodes.item(k).getNodeName();
										try {
											// <edm:Agent> element value
											String elementValue = "";
											Node n = agentNodes.item(k).getFirstChild();

											// check if node has text value
											if (n != null) {
												elementValue = n.getTextContent();
											}

											switch (agentTag) {

											case "skos:prefLabel":												
												List<String> agentPrefList = new ArrayList<String>();												
												agentPrefList.add(elementValue);
												if (aggEl.hasAttribute("xml:lang")) {
													if(agent.getPrefLabel().containsKey(aggEl.getAttribute("xml:lang"))){
														agent.getPrefLabel().put(aggEl.getAttribute("xml:lang"),
																agentPrefList);
													}else{
														agent.getPrefLabel().put(aggEl.getAttribute("xml:lang"),
														agentPrefList);
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														agent.getPrefLabel().put("key " + k, list);
														list = null;
													}
												}
												agentPrefList = null;
												break;
											case "skos:altLabel":
												List<String> agentAltList = new ArrayList<String>();
												
												agentAltList.add(elementValue);
												if (aggEl.hasAttribute("xml:lang")) {
													if(agent.getAltLabel().containsKey(aggEl.getAttribute("xml:lang"))){
														agent.getAltLabel().get(aggEl.getAttribute("xml:lang")).add(elementValue);
													}else{
														agent.getAltLabel().put(aggEl.getAttribute("xml:lang"),
																agentAltList);
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														agent.getAltLabel().put("key " + k, list);
														list = null;
													}
												}
												agentAltList = null;
												break;
											case "skos:note":
												if (elementValue.length() > 0) {
													agent.getNote().add(elementValue);
												}
												break;
											case "dc:date":
												if (elementValue.length() > 0) {
													agent.getDate().add(elementValue);
												}
												break;
											case "dc:creator":
												if (elementValue.length() > 0) {																										
													if(aggEl.hasAttribute("xml:lang")){
														if(edmOaiSource.getProvidedCHO().getOaiDc().getCreator().containsKey(aggEl.getAttribute("xml:lang"))){
															edmOaiSource.getProvidedCHO().getOaiDc().getCreator().get(aggEl.getAttribute("xml:lang")).add(elementValue);
														}else{
															List<String> list = new ArrayList<>();
															list.add(elementValue);
															edmOaiSource.getProvidedCHO().getOaiDc().getCreator().put(aggEl.getAttribute("xml:lang"),list);
															list = null;
														}
													}else{
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getCreator().put("key "+k,list);
														list = null;
													}	
												}
												break;
											case "dcterms:hasPart":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													agent.getHasPart()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														agent.getHasPart().add(elementValue);
													}
												}
												break;
											case "dcterms:isPartOf":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													agent.getIsPartOf()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														agent.getIsPartOf().add(elementValue);
													}
												}
												break;
											case "edm:begin":
												if (elementValue.length() > 0) {
													agent.setBegin(elementValue);
												}
												break;
											case "edm:end":
												if (elementValue.length() > 0) {
													agent.setEnd(elementValue);
												}
												break;
											case "edm:hasMet":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													agent.getHasMet()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														agent.getHasMet().add(elementValue);
													}
												}
												break;
											case "edm:isRelatedTo":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													agent.getIsRelatedTo()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0) {
														agent.getIsRelatedTo().add(elementValue);
													}
												}
												break;
											case "foaf:name":
												if (elementValue.length() > 0) {
													agent.getName().add(elementValue);
												}
												break;
											case "rdaGr2:biographicalInformation":
												if (elementValue.length() > 0) {
													agent.getBiographicalInformation().add(elementValue);
												}
												break;
											case "rdaGr2:dateOfBirth":
												if (elementValue.length() > 0) {
													agent.setDateOfBirth(elementValue);
												}
												break;
											case "rdaGr2:dateOfDeath":
												if (elementValue.length() > 0) {
													agent.setDateOfDeath(elementValue);
												}
												break;
											case "rdaGr2:dateOfEstablishment":
												if (elementValue.length() > 0) {
													agent.setDateOfEstablishment(elementValue);
												}
												break;
											case "rdaGr2:dateOfTermination":
												if (elementValue.length() > 0) {
													agent.setDateOfTermination(elementValue);
												}
												break;
											case "rdaGr2:gender":
												if (elementValue.length() > 0) {
													agent.setGender(elementValue);
												}
												break;
											case "rdaGr2:placeOfBirth":
												if (elementValue.length() > 0) {
													agent.setPlaceOfBirth(elementValue);
												}
												break;
											case "rdaGr2:placeOfDeath":
												if (elementValue.length() > 0) {
													agent.setPlaceOfDeath(elementValue);
												}
												break;
											case "rdaGr2:professionOrOccupation":
												if (elementValue.length() > 0) {
													agent.getProfessionOrOccupation().add(elementValue);
												}
												break;
											case "owl:sameAs":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													agent.getSameAs()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												}
												break;
											}

										} catch (Exception ex) {
											logger.error(ex.getMessage(),ex);
										}
										aggEl = null;
									}
								}
								if(agent.getPrefLabel().size() > 0){
									edmOaiSource.getEdmAgent().add(agent);
									agent = null;
									agentElement = null;
									agentNodes = null;
								}								
							}

							if (tag.matches("ore:Aggregation")) {
								// if we already read source data for this type
								// of element, skip to reading another element
								if (oreAggregationWasRead) {
									continue mainloop;
								}

								// set true when 1st time reading data for this
								// element
								oreAggregationWasRead = true;
								Element oreAggregationElement = (Element) rdfElements.item(p);

								if (oreAggregationElement.hasAttribute(rdfElementNamespace + ":about")) {
									edmOaiSource.getOreAggregation().setAbout(
											oreAggregationElement.getAttribute(rdfElementNamespace + ":about"));
								}
								
								NodeList aggregationNodes = rdfElements.item(p).getChildNodes();								
								// loop through <ore:Aggregation> children
								// elements
								for (int k = 0; k < aggregationNodes.getLength(); k++) {
									if (interrupted) {
										break mainloop;
									}

									if (aggregationNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
										// <ore:Aggregation> children element
										Element aggEl = (Element) aggregationNodes.item(k);

										// names of <ore:Aggregation> children
										// elements
										String aggreagationTag = aggregationNodes.item(k).getNodeName();
										try {

											// <ore:Aggregation> element value
											String elementValue = "";
											Node n = aggregationNodes.item(k).getFirstChild();

											// check if node has text value
											if (n != null) {
												elementValue = n.getTextContent();
											}

											switch (aggreagationTag) {

											case "edm:aggregatedCHO":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getOreAggregation().setAggregatedCHO(
															aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0)
														edmOaiSource.getOreAggregation().setAggregatedCHO(elementValue);
												}
												break;
											case "edm:rights":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getOreAggregation().setEdmRights(
															aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0)
														edmOaiSource.getOreAggregation().setEdmRights(elementValue);
												}
												break;
											case "dcterms:rights":
												List<String> rightsList = new ArrayList<>();

												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													List<String> list = new ArrayList<>();
													list.add(elementValue);
													edmOaiSource.getProvidedCHO().getOaiDc().getRights().put("key " + k,list);
													list = null;
												} else if (aggEl.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getOaiDc().getRights().containsKey(aggEl.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getOaiDc().getRights().get(aggEl.getAttribute("xml:lang")).add(elementValue);
													}else{
														rightsList.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getRights()
																.put(aggEl.getAttribute("xml:lang"), rightsList);	
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getRights()
																.put("key " + k, list);
														list = null;
													}
												}
												rightsList = null;
												break;
											case "edm:provider":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getOreAggregation().setOwner(
															aggEl.getAttribute(rdfElementNamespace + ":resource"));												
												} else {
													if (elementValue.length() > 0)
														edmOaiSource.getOreAggregation().setOwner(elementValue);
												}
												break;
											case "edm:dataProvider":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {													
													edmOaiSource.getOreAggregation().setDataOwner(
															aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0)														
													edmOaiSource.getOreAggregation().setDataOwner(elementValue);
												}
												break;
											case "edm:object":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getOreAggregation().setObject(
															aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0)
														edmOaiSource.getOreAggregation().setObject(elementValue);
												}
												break;
											case "edm:isShownBy":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getOreAggregation().setIsShownBy(
															aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0)
														edmOaiSource.getOreAggregation().setIsShownBy(elementValue);
												}
												break;
											case "edm:isShownAt":												
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {													
													edmOaiSource.getOreAggregation().setIsShownAt(
															aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0)
														edmOaiSource.getOreAggregation().setIsShownAt(elementValue);
												}
												break;
											case "edm:hasView":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getOreAggregation().getHasView()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0)
														edmOaiSource.getOreAggregation().getHasView().add(elementValue);
												}
												break;
											case "dc:rights":
												List<String> rightsList2 = new ArrayList<>();

												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													List<String> list = new ArrayList<>();
													list.add(elementValue);
													edmOaiSource.getProvidedCHO().getOaiDc().getRights().put("key " + k,list);
													list = null;
												} else if (aggEl.hasAttribute("xml:lang")) {
													if(edmOaiSource.getProvidedCHO().getOaiDc().getRights().containsKey(aggEl.getAttribute("xml:lang"))){
														edmOaiSource.getProvidedCHO().getOaiDc().getRights().get(aggEl.getAttribute("xml:lang")).add(elementValue);
													}else{
														rightsList2.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getRights()
																.put(aggEl.getAttribute("xml:lang"), rightsList2);	
													}													
												} else {
													if (elementValue.length() > 0) {
														List<String> list = new ArrayList<>();
														list.add(elementValue);
														edmOaiSource.getProvidedCHO().getOaiDc().getRights()
																.put("key " + k, list);
														list = null;
													}
												}
												rightsList2 = null;
												break;
											case "edm:ugc":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getOreAggregation().setUgc(
															aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0)
														edmOaiSource.getOreAggregation().setUgc(elementValue);
												}
												break;
											case "edm:intermediateProvider":
												if (aggEl.hasAttribute(rdfElementNamespace + ":resource")) {
													edmOaiSource.getOreAggregation().getIntermediateProvider()
															.add(aggEl.getAttribute(rdfElementNamespace + ":resource"));
												} else {
													if (elementValue.length() > 0)
														edmOaiSource.getOreAggregation().getIntermediateProvider()
																.add(elementValue);
												}
												break;
											}
										} catch (Exception ex) {
											logger.error(ex.getMessage(),ex);
										}
									}
								}
							}																					
						}
					}
				}						
			}

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}			
			
		// check if type and concepts have same preferredLabel values for same language								
		for (Map.Entry<String, List<String>> entry : edmOaiSource.getProvidedCHO().getOaiDc().getType().entrySet()) {			
			//dc:type language
			String typeLang = entry.getKey();			
			for (EdmConcept c : edmOaiSource.getEdmConcept()) {			
				if(c.getPrefLabel().containsKey(typeLang)){
					// loop through concepts preferredLabels with language same as typeLang
					for(int i=0; i < c.getPrefLabel().get(typeLang).size(); i++){
						// if prefLabel is the same, set value to null for concept prefLabel, so we don't have concept with duplicate prefLabel values 
						if(entry.getValue().contains(c.getPrefLabel().get(typeLang).get(i))){
							c.getPrefLabel().get(typeLang).set(i,null);
						}
					}	
				}							
			}			
		}
						
		// check if subject and concepts have same preferredLabel values for same language								
		for (Map.Entry<String, List<String>> entry : edmOaiSource.getProvidedCHO().getOaiDc().getSubject().entrySet()) {
			// dc:subject language
			String subjectLang = entry.getKey();
			for (EdmConcept c : edmOaiSource.getEdmConcept()) {
				if (c.getPrefLabel().containsKey(subjectLang)) {
					// loop through concepts preferredLabels with language same
					// as subjectLang
					for (int i = 0; i < c.getPrefLabel().get(subjectLang).size(); i++) {
						// if prefLabel is the same, set value to null for
						// concept prefLabel, so we don't have concept with
						// duplicate prefLabel values
						if (entry.getValue().contains(c.getPrefLabel().get(subjectLang).get(i))) {
							c.getPrefLabel().get(subjectLang).set(i, null);
						}
					}
				}
			}
		}					
		
		// check if creator and agents have same preferredLabel values for same language								
		for (Map.Entry<String, List<String>> entry : edmOaiSource.getProvidedCHO().getOaiDc().getCreator().entrySet()) {
			// dc:creator language
			String creatorLang = entry.getKey();
			for (EdmAgent edmAgent : edmOaiSource.getEdmAgent()) {
				if (edmAgent.getPrefLabel().containsKey(creatorLang)) {
					// loop through concepts preferredLabels with language same
					// as creatorLang
					for (int i = 0; i < edmAgent.getPrefLabel().get(creatorLang).size(); i++) {
						// if prefLabel is the same, set value to null for
						// agent prefLabel, so we don't have concept with
						// duplicate prefLabel values
						if (entry.getValue().contains(edmAgent.getPrefLabel().get(creatorLang).get(i))) {
							edmAgent.getPrefLabel().get(creatorLang).set(i, null);
						}
					}
				}
			}
		}
				
		// delete spatial that are already in edmOaiSource.Place
		for (Iterator<String> iter = edmOaiSource.getProvidedCHO().getSpatial().iterator(); iter.hasNext(); ) {
			String spatial = iter.next();
			// loop through edmSource.Places and check if same place title is in spatial
			edmloop:for (EdmPlace a : edmOaiSource.getEdmPlace()) {
				for(List<String> values : a.getPrefLabel().values()){			
					// if list of prefLabels contains same value as spatial
					if(values.contains(spatial)){
						iter.remove();
						break edmloop;
					}
				}				
			}			
		}
		
		// check if temporal has same prefLabel value as edmOaiSource.Date
		// loop through temporal values
		for (Iterator<String> iter = edmOaiSource.getProvidedCHO().getTemporal().iterator(); iter.hasNext(); ) {
			String temporal = iter.next();
			
			// loop through edmOaiSource.Date values
			for (Map.Entry<String, List<String>> dateEntry : edmOaiSource.getProvidedCHO().getOaiDc().getDate().entrySet()) {				
				// if edmOaiSource.Date contains temporal value, delete temporal value (becouse its already in edmOaiSource.Date)
				if(dateEntry.getValue().contains(temporal)){
					iter.remove();
					break;
				}
			}			
		}
		
		return edmOaiSource;
	}

	@Override
	public void setInterrupt(boolean interrupt) {
		interrupted = interrupt;
	}

	
	
	// for OAI-PMH sources with oai_dc metadata prefix
	@Override
	public EdmOaiSource readSourceDataOaiDc(String xml, String headerIdentifier) {
		EdmOaiSource edmOaiSource = new EdmOaiSource();
		edmOaiSource.setHeaderIdentifier(headerIdentifier);

		// name of elements that were already read
		java.util.Set<String> readElements = new HashSet<String>();
		
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
			Document xmlDoc = builder.parse(is);

			String root = xmlDoc.getDocumentElement().getNodeName();				
			Node node = null;
			NodeList nList = null;						
			
			if(xmlDoc.getElementsByTagName("dc:dc").getLength() > 0){					
				nList = xmlDoc.getElementsByTagName("dc:dc");
				node = (Element) nList.item(0);
			}else{								
				nList = xmlDoc.getElementsByTagName(root);
				node = (Element) nList.item(0);
			}											
			

			String language = null;
			mainloop: for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				if (interrupted) {
					break mainloop;
				}

				if (node.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {										
					
					// dublin core tag names
					String tag = node.getChildNodes().item(i).getNodeName();
					// dublin core element
					Element element = (Element) node.getChildNodes().item(i);
					try {
						Node n = node.getChildNodes().item(i).getFirstChild();
						// check if node has text value
						if (n != null) {
							// dublin core element values
							String elementValue = n.getTextContent();

							switch (tag) {

							case "dc:type":
								List<String> typeList = new ArrayList<String>();

								if (element.hasAttribute("xml:lang")) {
									if(edmOaiSource.getProvidedCHO().getOaiDc().getType().containsKey(element.getAttribute("xml:lang"))){
										edmOaiSource.getProvidedCHO().getOaiDc().getType().get(element.getAttribute("xml:lang")).add(elementValue);
									}else{
										typeList.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getType()
												.put(element.getAttribute("xml:lang"), typeList);	
									}
									
								} else {									
									if (elementValue.length() > 0) {
										List<String> list = new ArrayList<>();
										list.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getType().put("key " + i,list);
									}
								}
								break;
							case "dc:date":	
								List<String> dateList = new ArrayList<String>();

								if (element.hasAttribute("xml:lang")) {
									if(edmOaiSource.getProvidedCHO().getOaiDc().getDate().containsKey(element.getAttribute("xml:lang"))){
										edmOaiSource.getProvidedCHO().getOaiDc().getDate().get(element.getAttribute("xml:lang")).add(elementValue);
									}else{
										dateList.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getDate()
												.put(element.getAttribute("xml:lang"), dateList);	
									}									
								} else {
									if (elementValue.length() > 0) {
										List<String> list = new ArrayList<>();
										list.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getDate().put("key " + i,list);
									}
								}
								break;
							case "dc:creator":
								if (elementValue.length() > 0) {
									if(element.hasAttribute("xml:lang")){
										if(edmOaiSource.getProvidedCHO().getOaiDc().getCreator().containsKey(element.getAttribute("xml:lang"))){
											edmOaiSource.getProvidedCHO().getOaiDc().getCreator().get(element.getAttribute("xml:lang")).add(elementValue);
										}else{
											List<String> list = new ArrayList<>();
											list.add(elementValue);
											edmOaiSource.getProvidedCHO().getOaiDc().getCreator().put(element.getAttribute("xml:lang"),list);
										}
									}else{
										List<String> list = new ArrayList<>();
										list.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getCreator().put("key "+i,list);
									}
								}
								break;
							case "dc:rights":	
								List<String> rightsList = new ArrayList<String>();

								if (element.hasAttribute("xml:lang")) {
									if(edmOaiSource.getProvidedCHO().getOaiDc().getRights().containsKey(element.getAttribute("xml:lang"))){
										edmOaiSource.getProvidedCHO().getOaiDc().getRights().get(element.getAttribute("xml:lang")).add(elementValue);
									}else{
										rightsList.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getRights().put(element.getAttribute("xml:lang"), rightsList);										
									}									
								} else {
									if (elementValue.length() > 0) {
										List<String> list = new ArrayList<>();
										list.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getRights().put("key " + i,list);
									}
								}
								break;
							case "dc:format":	
								List<String> formatList = new ArrayList<String>();

								if (element.hasAttribute("xml:lang")) {
									if(edmOaiSource.getProvidedCHO().getOaiDc().getFormat().containsKey(element.getAttribute("xml:lang"))){
										edmOaiSource.getProvidedCHO().getOaiDc().getFormat().get(element.getAttribute("xml:lang")).add(elementValue);
									}else{
										formatList.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getFormat().put(element.getAttribute("xml:lang"), formatList);	
									}									
								} else {
									if (elementValue.length() > 0) {
										List<String> list = new ArrayList<>();
										list.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getFormat().put("key " + i,list);
									}
								}
								break;
							case "dc:relation":
								if (elementValue.length() > 0) {
									edmOaiSource.getProvidedCHO().getOaiDc().getRelation().add(elementValue);
								}
								break;
							case "dc:identifier":
								if (elementValue.length() > 0) {
									edmOaiSource.getProvidedCHO().getOaiDc().getIdentifier().add(elementValue);
								}
								break;
							case "dc:language":
								if (elementValue.length() > 0) {
									edmOaiSource.getProvidedCHO().getOaiDc().getLanguage().add(elementValue);
								}
								break;
							case "dc:publisher":
								List<String> publisher = new ArrayList<String>();

								if (element.hasAttribute("xml:lang")) {
									if(edmOaiSource.getProvidedCHO().getOaiDc().getPublisher().containsKey(element.getAttribute("xml:lang"))){
										edmOaiSource.getProvidedCHO().getOaiDc().getPublisher().get(element.getAttribute("xml:lang")).add(elementValue);
									}else{
										publisher.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getPublisher().put(element.getAttribute("xml:lang"), publisher);	
									}									
								} else {
									if (elementValue.length() > 0) {
										List<String> list = new ArrayList<>();
										list.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getPublisher().put("key " + i,list);
									}
								}
								break;
							case "dc:source":
								List<String> sourceList = new ArrayList<String>();

								if (element.hasAttribute("xml:lang")) {
									if(edmOaiSource.getProvidedCHO().getOaiDc().getSource().containsKey(element.getAttribute("xml:lang"))){
										edmOaiSource.getProvidedCHO().getOaiDc().getSource().get(element.getAttribute("xml:lang")).add(elementValue);
									}else{
										sourceList.add(elementValue);									
										edmOaiSource.getProvidedCHO().getOaiDc().getSource().put(element.getAttribute("xml:lang"), sourceList);	
									}									
								} else {
									if (elementValue.length() > 0) {
										List<String> list = new ArrayList<>();
										list.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getSource().put("key " + i,list);
									}
								}
								break;
							case "dc:title":
								if (element.hasAttribute("xml:lang")) {
									language = element.getAttribute("xml:lang");
									edmOaiSource.getProvidedCHO().getOaiDc().getTitle()
											.put(element.getAttribute("xml:lang"), elementValue);
								} else {
									if (elementValue.length() > 0) {
										edmOaiSource.getProvidedCHO().getOaiDc().getTitle().put("key " + i,
												elementValue);
									}
								}
								break;
							case "dc:subject":
								List<String> subjectList = new ArrayList<String>();
								if (elementValue.length() > 0) {									
									if(element.hasAttribute("xml:lang")){
										if(edmOaiSource.getProvidedCHO().getOaiDc().getSubject().containsKey(element.getAttribute("xml:lang"))){
											edmOaiSource.getProvidedCHO().getOaiDc().getSubject().get(element.getAttribute("xml:lang")).add(elementValue);
										}else{											
											edmOaiSource.getProvidedCHO().getOaiDc().getSubject().put(element.getAttribute("xml:lang"),subjectList);	
										}										
									}else{
										if(edmOaiSource.getProvidedCHO().getOaiDc().getSubject().containsKey("en")){
											edmOaiSource.getProvidedCHO().getOaiDc().getSubject().get("en").add(elementValue);	
										}else{											
											subjectList.add(elementValue);
											edmOaiSource.getProvidedCHO().getOaiDc().getSubject().put("en",subjectList);
										}
										
									}									
								}
								break;
							case "dc:description":	
								List<String> descriptionList = new ArrayList<String>();

								if (element.hasAttribute("xml:lang")) {
									if(edmOaiSource.getProvidedCHO().getOaiDc().getDescription().containsKey(element.getAttribute("xml:lang"))){
										edmOaiSource.getProvidedCHO().getOaiDc().getDescription().get(element.getAttribute("xml:lang")).add(elementValue);
									}else{
										descriptionList.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getDescription()
												.put(element.getAttribute("xml:lang"), descriptionList);	
									}									
								} else {
									if (elementValue.length() > 0) {
										List<String> list = new ArrayList<>();
										list.add(elementValue);
										edmOaiSource.getProvidedCHO().getOaiDc().getDescription().put("key " + i,list);
									}
								}
								break;
							case "dc:contributor":
								if (elementValue.length() > 0) {
									edmOaiSource.getProvidedCHO().getOaiDc().getContributor().add(elementValue);
								}
								break;
							case "dc:coverage":
								if (elementValue.length() > 0) {
									edmOaiSource.getProvidedCHO().getOaiDc().getCoverage().add(elementValue);
								}
								break;
							}
						}
					} catch (Exception ex) {
						logger.error(ex.getMessage(),ex);
					}
				}
			}
			
			if(language != null){
				edmOaiSource.getProvidedCHO().getOaiDc().getLanguage().add(language);
			}
			
			// polish OAI-phm - for weblinks
			if (xmlDoc.getElementsByTagName("fileGrp").getLength() > 0) {
				NodeList fileGrpList = xmlDoc.getElementsByTagName("fileGrp");

				weblinkLoop: for (int i = 0; i < fileGrpList.getLength(); i++) {
					Node fileGrpNode = (Element) fileGrpList.item(i);

					if (interrupted) {
						break weblinkLoop;
					}

					for (int j = 0; j < fileGrpNode.getChildNodes().getLength(); j++) {
						if (fileGrpNode.getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element fileElement = (Element) fileGrpNode.getChildNodes().item(j);

							if (interrupted) {
								break weblinkLoop;
							}

							if (fileElement.hasAttribute("MIMETYPE")
									&& fileElement.getAttribute("MIMETYPE").equals("image/jpeg")) {
								for (int k = 0; k < fileElement.getChildNodes().getLength(); k++) {

									if (interrupted) {
										break weblinkLoop;
									}

									if (fileElement.getChildNodes().item(k).getNodeType() == Node.ELEMENT_NODE) {
										Element fLocatElement = (Element) fileElement.getChildNodes().item(k);

										if (fLocatElement.hasAttribute("xlink:href")) {
											EdmWebResource edmWebResource = new EdmWebResource();
											edmWebResource.setLink(fLocatElement.getAttribute("xlink:href"));
											edmWebResource.getType().add("direct");
											edmWebResource
													.setEdmRights("http://rightsstatements.org/page/CNE/1.0/");
											edmOaiSource.getEdmWebResource().add(edmWebResource);
										}
									}
								}
							}
						}
					}
				}
			}		
			
			
			// <dlibra> elements for polish OAI
			if(xmlDoc.getElementsByTagName("dlibra_avs:metadataDescription").getLength() > 0){					
				nList = xmlDoc.getElementsByTagName("dlibra_avs:metadataDescription");
				node = (Element) nList.item(0);
				

				EdmConcept edmConcept = new EdmConcept();
				EdmTimespan edmTimespan = new EdmTimespan();
				List<String> medium = new ArrayList<String>();
				List<String> extent = new ArrayList<String>();
				String mediumlanguage = null;
				String extentlang = null;
				List<String> timespanPrefLabel = new ArrayList<String>();

				dlibraloop: for (int i = 0; i < node.getChildNodes().getLength(); i++) {
					if (interrupted) {
						break dlibraloop;
					}					
					if (node.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {

						// dlibra_avs tag names
						String tag = node.getChildNodes().item(i).getNodeName();
						// dlibra_avs element
						Element dlibraElement = (Element) node.getChildNodes().item(i);											
						
						try {
							Node n = node.getChildNodes().item(i).getFirstChild();
							// check if node has text value
							if (n != null) {
								// dlibra_avs element values
								String elementValue = n.getTextContent();
								switch (tag) {
									
								case "dlibra_avs:Type":									
									if(dlibraElement.getNodeType() == Node.ELEMENT_NODE){
										if(dlibraElement.hasAttribute("xml:lang")){	
											if(edmConcept.getPrefLabel().containsKey(dlibraElement.getAttribute("xml:lang"))){
												edmConcept.getPrefLabel().get(dlibraElement.getAttribute("xml:lang")).add(elementValue);
											}else{
												List<String> list = new ArrayList<>();
												list.add(elementValue);
												edmConcept.getPrefLabel().put(dlibraElement.getAttribute("xml:lang"), list);	
											}											
										}
									}																									
									break;								
								case "dlibra_avs:Dating":									
									if(dlibraElement.getNodeType() == Node.ELEMENT_NODE){
										if(dlibraElement.hasAttribute("xml:lang")){		
											timespanPrefLabel.add(elementValue);
											edmTimespan.getPrefLabel().put(dlibraElement.getAttribute("xml:lang"), timespanPrefLabel);
											edmTimespan.setBegin(elementValue);
											edmTimespan.setEnd(elementValue);
										}
									}									
									break;
								case "dlibra_avs:Author":
									break;								
								case "dlibra_avs:Technique":									
									if(dlibraElement.getNodeType() == Node.ELEMENT_NODE){
										if(dlibraElement.hasAttribute("xml:lang")){	
											mediumlanguage = dlibraElement.getAttribute("xml:lang");
											medium.add(elementValue);
										}
									}										
									break;
								case "dlibra_avs:Material":																	
									if(dlibraElement.getNodeType() == Node.ELEMENT_NODE){
										if(dlibraElement.hasAttribute("xml:lang")){											
											mediumlanguage = dlibraElement.getAttribute("xml:lang");
											medium.add(elementValue);
										}
									}	
									break;
								case "dlibra_avs:Dimensions":
									if(dlibraElement.getNodeType() == Node.ELEMENT_NODE){
										if(dlibraElement.hasAttribute("xml:lang")){											
											extentlang = dlibraElement.getAttribute("xml:lang");
											extent.add(elementValue);
										}
									}
									break;								
								case "dlibra_avs:Owner":
									if(dlibraElement.getNodeType() == Node.ELEMENT_NODE){																					
										edmOaiSource.getOreAggregation().setDataOwner(elementValue);										
									}
									break;								
								case "dlibra_avs:Identifier":
																		
									for (int k = 0; k < dlibraElement.getChildNodes().getLength(); k++) {
										if (dlibraElement.getChildNodes().item(k).getNodeType() == Node.ELEMENT_NODE) {	
											if(!edmOaiSource.getProvidedCHO().getOaiDc().getIdentifier().contains(dlibraElement.getChildNodes().item(k).getTextContent())){
												edmOaiSource.getProvidedCHO().getOaiDc().getIdentifier().add(dlibraElement.getChildNodes().item(k).getTextContent());
											}
										}
									}
									
									break;								
								case "dlibra_avs:Description":										
									for (int k = 0; k < dlibraElement.getChildNodes().getLength(); k++) {
										if (dlibraElement.getChildNodes().item(k).getNodeType() == Node.ELEMENT_NODE) {											
											Element valueElement = (Element) dlibraElement.getChildNodes().item(k);
											if(valueElement.hasAttribute("xml:lang")){
												if(edmOaiSource.getProvidedCHO().getOaiDc().getDescription().containsKey(valueElement.getAttribute("xml:lang"))){
													edmOaiSource.getProvidedCHO().getOaiDc().getDescription().get(valueElement.getAttribute("xml:lang")).add(valueElement.getTextContent());
												}else{
													List<String> descriptionList = new ArrayList<String>();
													descriptionList.add(valueElement.getTextContent());											
													edmOaiSource.getProvidedCHO().getOaiDc().getDescription().put(valueElement.getAttribute("xml:lang"), descriptionList);	
												}
													
											}											
										}
									}
									break;																
								}
							}
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
						}
					}
				}

				
				if(edmConcept.getPrefLabel().size() > 0){
					edmOaiSource.getEdmConcept().add(edmConcept);	
				}				
				
				if(edmTimespan.getPrefLabel().size() > 0){					
					edmOaiSource.getEdmTimespan().add(edmTimespan);
				}
				
				if(medium.size() > 0){
					edmOaiSource.getProvidedCHO().getMedium().put(mediumlanguage, medium);
				}
				
				if(extent.size() > 0){
					edmOaiSource.getProvidedCHO().getExtent().put(extentlang, extent);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return edmOaiSource;
	}
	
	@Override
	public EdmOaiSource readSourceAPEClevel(String responseData,String language){
		EdmOaiSource edm = new EdmOaiSource();
		
		edm.getProvidedCHO().getOaiDc().getLanguage().add(language);
		List<String> timespanPrefLabel = new ArrayList<>();

		String rights = "";
		try{
			JSONObject object = new JSONObject(responseData);
			
			mainloop:for(int i=0; i < object.getNames(object).length; i++){

				switch(object.getNames(object)[i]){
			
				case "id":
					edm.setHeaderIdentifier(object.getString("id"));
					break;
				case "unitId":
					edm.getProvidedCHO().getOaiDc().getIdentifier().add(object.getString("unitId"));
					break;
				case "unitTitle":
					List<String> description = new ArrayList<String>();
					description.add(object.getString("unitTitle"));
					edm.getProvidedCHO().getOaiDc().getDescription().put(language, description);
					break;
				case "fondsUnitTitle":
					edm.getProvidedCHO().getOaiDc().getTitle().put(language, object.getString("fondsUnitTitle"));
					break;
				case "repository":
					edm.getOreAggregation().setDataOwner(object.getString("repository"));
					break;
				case "content":
					
					JSONObject mainContent = object.getJSONObject("content");
					
					for(int j=0; j < mainContent.getNames(mainContent).length; j++){
						
						if (interrupted) {
							break mainloop;
						}
						switch(mainContent.getNames(mainContent)[j]){
						
						case "did":
							
							JSONObject did = mainContent.getJSONObject("did");
							
							if(did.has("mdid")){
								JSONArray mdid = (JSONArray) did.getJSONArray("mdid");
								
								for(int z=0; z < mdid.length(); z++){								
									if (interrupted) {
										break mainloop;
									}
									JSONObject mdidObject = mdid.getJSONObject(z);
									
									if(mdidObject.has("content")){
										JSONArray mdidObjectContent = (JSONArray) mdidObject.getJSONArray("content");

										for(int k=0; k < mdidObjectContent.length(); k++){
											if (interrupted) {
												break mainloop;
											}	
																																												
											if(mdidObjectContent.get(k) instanceof JSONObject){
											
												JSONObject obj = mdidObjectContent.getJSONObject(k);																								
												
												if(obj.has("value")){
													JSONObject valueObj = obj.getJSONObject("value");
													
													if(valueObj.has("href") && valueObj.has("type")){
														if(valueObj.getString("type").equals("simple")){
															EdmWebResource wr = new EdmWebResource();
															wr.setLink(valueObj.getString("href"));
															wr.getType().add("webpage");
															edm.getEdmWebResource().add(wr);
														}
													}
													
												}																							
											}											
										}
										
									}
									if(mdidObject.has("normal") && mdidObject.has("calendar")){										
										EdmTimespan edmTimespan = new EdmTimespan();
										timespanPrefLabel.add(mdidObject.getString("normal"));
										edmTimespan.getPrefLabel().put(language, timespanPrefLabel);
										edmTimespan.setBegin(mdidObject.getString("normal"));
										edmTimespan.setEnd(mdidObject.getString("normal"));
										edm.getEdmTimespan().add(edmTimespan);
									}	
									
									if(mdidObject.has("title") && mdidObject.has("href")){
										if(mdidObject.getString("href").contains(".jpg")){
											EdmWebResource wr = new EdmWebResource();
											wr.setLink(mdidObject.getString("href"));
											wr.getType().add("direct");
											edm.getEdmWebResource().add(wr);										
										}
									}
								}								
							}
							
							break;
						case "mdescFull":
							
							JSONArray mdescFull = (JSONArray) mainContent.getJSONArray("mdescFull");
							
							for(int k = 0; k < mdescFull.length(); k++){
								if (interrupted) {
									break mainloop;
								}
								JSONObject mdescObject = mdescFull.getJSONObject(k);
								
								if(mdescObject.has("encodinganalog") && mdescObject.has("addressOrChronlistOrList")){
									
									if(mdescObject.getString("encodinganalog").equals("rts:rightscategory")){
										if(mdescObject.getString("encodinganalog").equals("rts:rightscategory")){
											
											JSONArray addressOrChronlistOrList = mdescObject.getJSONArray("addressOrChronlistOrList");
											
											for(int y=0 ; y < addressOrChronlistOrList.length(); y++){
												if (interrupted) {
													break mainloop;
												}
												JSONObject addressObj = addressOrChronlistOrList.getJSONObject(y);
												
												if(addressObj.has("content")){
													
													JSONArray addressObjContent = addressObj.getJSONArray("content");
													
													for(int l=0; l < addressObjContent.length(); l++){
														
														if(addressObjContent.get(l) instanceof JSONObject){
															JSONObject obj = addressObjContent.getJSONObject(l);
															
															if(obj.has("value")){
																JSONObject value = obj.getJSONObject("value");
																rights = value.getString("href");
															}
														}
														
														
													}													
												}												
											}
												
										}
									}																			
								}
								
							}							
							break;						
						}						
					}
					
					break;
				
				}				
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		
		for(int i=0; i < edm.getEdmWebResource().size(); i++){
			if (interrupted) {
				break;
			}
			if(edm.getEdmWebResource().get(i).getEdmRights() == null && rights.length() > 0){
				edm.getEdmWebResource().get(i).setEdmRights(rights);
			}
		}
		
		List<String> dcTypeList = new ArrayList<String>();
		dcTypeList.add("Archival material");
		edm.getProvidedCHO().getOaiDc().getType().put("nl", dcTypeList);		
		edm.setEdmType("TEXT");
		return edm;
	}

	@SuppressWarnings("static-access")
	@Override
	public EdmOaiSource readSourceApeEAD(String jsonResponse) {
		EdmOaiSource edm = new EdmOaiSource();
		EdmPlace place = new EdmPlace();
		EdmAgent agent = new EdmAgent();
		try{
			JSONObject responseObject = new JSONObject(jsonResponse);
			
			String title = "";
			String dataOwner = "";
			String identifier = "";
			List<String> alternative = new ArrayList<String>();
			List<String> publisher = new ArrayList<String>();
			List<String> timespanLabel = new ArrayList<String>();
			List<String> agentLabel = new ArrayList<String>();
			List<String> description = new ArrayList<String>();
			String address = "";
			String language = "";
			String end = "";
			String begin = "";
			String extent = "";
			
			mainloop:for(int i=0; i < responseObject.getNames(responseObject).length; i++){							
				
				if(interrupted){
					break;
				}
				
				switch(responseObject.getNames(responseObject)[i]){
				
				case "id":
					edm.setHeaderIdentifier(responseObject.getString("id"));
					break mainloop;					
				case "unitId":
					//URL of a digital representation of the object.										
					break;
				case "unitTitle":					
					title = responseObject.getString("unitTitle");
					break;
				case "repository":
					dataOwner = responseObject.getString("repository");
					break;
				case "content":
					
					JSONObject mainContent = responseObject.getJSONObject("content");
					
					for(int j=0; j < mainContent.getNames(mainContent).length; j++){
					
						if(interrupted){
							break mainloop;
						}
						
						switch(mainContent.getNames(mainContent)[j]){
							
						case "eadheader":
								
							JSONObject eadHeader = mainContent.getJSONObject("eadheader");
							
							for(int k=0; k < eadHeader.getNames(eadHeader).length; k++){
								
								if(interrupted){
									break mainloop;
								}
								
								switch(eadHeader.getNames(eadHeader)[k]){
									
								case "eadid":
									
									JSONObject eadid = eadHeader.getJSONObject("eadid");
									
									if(eadid.has("identifier")){
										identifier = eadid.getString("identifier");
									}
									
									break;
								case "filedesc":
									
									JSONObject filedesc = eadHeader.getJSONObject("filedesc");
									
									for(int z=0; z < filedesc.getNames(filedesc).length; z++){
										
										if(interrupted){
											break mainloop;
										}
										
										switch(filedesc.getNames(filedesc)[z]){
										
										case "titlestmt":	
											
											JSONObject titlestmt = filedesc.getJSONObject("titlestmt");

											if(titlestmt.has("subtitle")){
												
												JSONArray subtitle = (JSONArray) titlestmt.getJSONArray("subtitle");
												
												for(int x=0; x < subtitle.length(); x++){
													
													if(interrupted){
														break mainloop;
													}
													
													JSONObject object = subtitle.getJSONObject(x);
													
													if(object.has("content")){
														
														JSONArray subtitleContent = (JSONArray) object.getJSONArray("content");														
														for(int y=0; y < subtitleContent.length(); y++){															
															alternative.add(subtitleContent.get(y).toString());															
														}														
													}													
												}												
											}
											
											break;
										case "editionstmt":
											break;
										case "publicationstmt":
											
											JSONObject publicationstmt = filedesc.getJSONObject("publicationstmt");
																																
											if(publicationstmt.has("publisherOrDateOrAddress")){
											
												JSONArray publisherOrDateOrAddress = publicationstmt.getJSONArray("publisherOrDateOrAddress");
												
												for(int x=0; x < publisherOrDateOrAddress.length(); x++){
													
													if(interrupted){
														break mainloop;
													}
													
													JSONObject object = publisherOrDateOrAddress.getJSONObject(x);
													
													if(object.has("content")){
														JSONArray publisherJson = object.getJSONArray("content");
														
														for(int n=0; n < publisherJson.length(); n++){
															publisher.add(publisherJson.getString(n));
														}
													}
													
													if(object.has("addressline")){
														JSONArray addressline = object.getJSONArray("addressline");
														
														for(int n=0; n < addressline.length(); n++){
															if(interrupted){
																break mainloop;
															}
															if(addressline.getJSONObject(n).has("content")){
																JSONArray addressContent = addressline.getJSONObject(n).getJSONArray("content");
																
																for(int m=0; m < addressContent.length(); m++){																	
																	if(m == 0){
																		address += addressContent.getString(m);
																	}else{
																		address += ", "+addressContent.getString(m);
																	}
																}													
															}																														
														}
													}													
												}													
											}											
											break;
										case "seriesstmt":
											break;
										case "notestmt":
											break;										
										}										
									}
									
									break;
								case "profiledesc":
									
									JSONObject profiledesc = eadHeader.getJSONObject("profiledesc");
									
									for(int z=0; z<profiledesc.getNames(profiledesc).length; z++){
										if(interrupted){
											break mainloop;
										}
										switch(profiledesc.getNames(profiledesc)[z]){
																				
										case "langusage":
											JSONObject langusage = profiledesc.getJSONObject("langusage");
											
											if(langusage.has("content")){
												
												JSONArray langcontentArray = langusage.getJSONArray("content");
												
												for(int x=0; x < langcontentArray.length(); x++){
													if(interrupted){
														break mainloop;
													}
													if(langcontentArray.get(x) instanceof JSONObject){
													
														JSONObject object = langcontentArray.getJSONObject(x);
														
														if(object.has("value")){
															JSONObject langValue = object.getJSONObject("value");
															
															if(langValue.has("langcode")){
																if(language.length() == 0){
																	language = langValue.getString("langcode");	
																}																
															}
														}
													}
												}
												
											}
											
											break;										
										}										
									}																											
									
									break;																								
								}
								
							}							
							break;
						case "archdesc":
							
							JSONObject archdesc = mainContent.getJSONObject("archdesc");
							
							for(int k=0; k < archdesc.getNames(archdesc).length; k++){
								if(interrupted){
									break mainloop;
								}
								switch(archdesc.getNames(archdesc)[k]){
								
								case "did":
									
									JSONObject did = archdesc.getJSONObject("did");
									
									if(did.has("mdid")){
										JSONArray mdidArray = did.getJSONArray("mdid");
										
										for(int z=0; z < mdidArray.length(); z++){
											if(interrupted){
												break mainloop;
											}
											
											JSONObject mdidObject = mdidArray.getJSONObject(z);
											
											if(mdidObject.has("calendar") && mdidObject.has("era") && mdidObject.has("content")){
												JSONArray mdidContent = mdidObject.getJSONArray("content");
												
												for(int l=0; l < mdidContent.length(); l++){
													
													String lab = mdidContent.getString(l);
													timespanLabel.add(lab);													
													begin = lab;
													end = lab;													
												}
												
											}
											
											if(mdidObject.has("content")){
												
												JSONArray content = mdidObject.getJSONArray("content");
												for(int l=0; l < content.length(); l++){
													if(interrupted){
														break mainloop;
													}
													if(content.get(l) instanceof JSONObject){
														JSONObject conObject = content.getJSONObject(l);																											

														if(conObject.has("name") && conObject.has("value")){
															
															if(conObject.getString("name").contains("persname")){
																																										
																JSONObject value = conObject.getJSONObject("value");
																if(value.has("content")){														
																	JSONArray valueArray = value.getJSONArray("content");														
																	for(int h=0; h < valueArray.length(); h++){				
																		agentLabel.add(valueArray.getString(h).trim());
																	}														
																}													
															}			
															
															if(conObject.getString("name").contains("extent")){
																JSONObject value = conObject.getJSONObject("value");
																if(value.has("content")){														
																	JSONArray valueArray = value.getJSONArray("content");														
																	for(int h=0; h < valueArray.length(); h++){
																		extent = valueArray.getString(h);
																	}														
																}
															}
														}	
														
													}													
												}
												
											}																																																	
										}										
									}
									
									break;
								case "mdescFull":
									
									JSONArray mdescFull = archdesc.getJSONArray("mdescFull");
									
									for(int n=0; n < mdescFull.length(); n++){
										if(interrupted){
											break mainloop;
										}
										JSONObject mdescObject = mdescFull.getJSONObject(n);
																				
										if (mdescObject.has("encodinganalog")) {

											if (mdescObject.getString("encodinganalog").trim().toLowerCase().equals("summary")) {

												if (mdescObject.has("addressOrChronlistOrList")) {

													JSONArray mdescData = mdescObject.getJSONArray("addressOrChronlistOrList");

													for (int x = 0; x < mdescData.length(); x++) {
														if(interrupted){
															break mainloop;
														}
														JSONObject obj = mdescData.getJSONObject(x);

														if (obj.has("content")) {

															JSONArray objContent = obj.getJSONArray("content");

															for (int y = 0; y < objContent.length(); y++) {																
																description.add(objContent.getString(y).trim());																
															}
														}
													}
												}
											}											
										}			
										
										if(mdescObject.has("type")){
											edm.setEdmType(mdescObject.getString("type"));
										}
									}																		
									break;								
								}								
							}							
							break;						
						}						
					}					
					break;				
				}				
			}
						
			
			if(language.length() > 0){
				edm.getProvidedCHO().getOaiDc().getLanguage().add(language);
			}
			
			
			if(title.length() > 0){
				edm.getProvidedCHO().getOaiDc().getTitle().put(language, title);
			}
			
			if(!description.isEmpty()){				
				edm.getProvidedCHO().getOaiDc().getDescription().put(language, description);												
			}
			
			if(dataOwner.length() > 0){
				edm.getOreAggregation().setDataOwner(dataOwner);				
			}
			
			if(!publisher.isEmpty()){							
				edm.getProvidedCHO().getOaiDc().getPublisher().put(language, publisher);										
			}
			if(!alternative.isEmpty()){								
				edm.getProvidedCHO().getAlternativeTitle().put(language, alternative);				
			}
			
			if(identifier.length() > 0){
				edm.getProvidedCHO().getOaiDc().getIdentifier().add(identifier);
			}
			
			if(address.length() > 0){
				List<String> list = new ArrayList<>();
				list.add(address);
				place.getPrefLabel().put(language, list);
				edm.getEdmPlace().add(place);
			}
			
			if(extent.length() > 0){
				List<String> extentList = new ArrayList<String>();
				edm.getProvidedCHO().getExtent().put(language, extentList);
			}
			
			if(!agentLabel.isEmpty()){
				for(String a : agentLabel){
					List<String> list = new ArrayList<>();
					list.add(a);
					agent.getPrefLabel().put(language, list);
					agent.setRole("dc:Creator");
				}
				edm.getEdmAgent().add(agent);
			}
			
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		
		return edm;
	}
}
