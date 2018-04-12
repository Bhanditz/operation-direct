package eu.europeana.direct.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.jena.atlas.web.WebLib;
import org.xml.sax.SAXParseException;

import eu.europeana.direct.harvesting.jobs.EdmHarvestWorker;
import eu.europeana.direct.harvesting.jobs.HarvestThread;
import eu.europeana.direct.harvesting.jobs.IndexThread;
import eu.europeana.direct.harvesting.mapper.EdmMapper;
import eu.europeana.direct.harvesting.mapper.IMapper;
import eu.europeana.direct.harvesting.mapper.OaiDcMapper;
import eu.europeana.direct.harvesting.source.edm.ISourceReader;
import eu.europeana.direct.harvesting.source.edm.SourceReader;
import eu.europeana.direct.harvesting.source.edm.model.EdmAgent;
import eu.europeana.direct.harvesting.source.edm.model.EdmConcept;
import eu.europeana.direct.harvesting.source.edm.model.EdmOaiSource;
import eu.europeana.direct.harvesting.source.edm.model.EdmPlace;
import eu.europeana.direct.harvesting.source.edm.model.EdmTimespan;
import eu.europeana.direct.helpers.APIKeyGenerator;
import eu.europeana.direct.helpers.GeoLocationServiceHelper;
import eu.europeana.direct.helpers.IRestClient;
import eu.europeana.direct.helpers.RestClient;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.PlaceLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpanLangaugeAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;
import se.kb.oai.ore.AggregatedResource;
import se.kb.oai.ore.Aggregation;
import se.kb.oai.ore.Metadata;
import se.kb.oai.ore.ResourceMap;
import se.kb.oai.ore.Type;
import se.kb.oai.ore.Metadata.Namespace;
import se.kb.oai.ore.impl.AtomFactory;
import se.kb.oai.ore.impl.AtomSerializer;
import se.kb.oai.pmh.Header;
import se.kb.oai.pmh.IdentifiersList;
import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.Record;
import se.kb.oai.pmh.RecordsList;
import se.kb.oai.pmh.ResumptionToken;
import se.kb.oai.pmh.Set;

public class Test {
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get("C:/Users/nino.berke/Desktop/direct-stuff/"+path));
		return new String(encoded, encoding);
	}				
	
	public static void main(String[] args) {						
						
		
		//				
		IRestClient cl = new RestClient();
		
		for(int i=0; i < 1; i++){
//			try {
//				String response = i + ": " +cl.httpGetRequest("http://data.jewisheritage.net/oai/OAIHandler?verb=ListRecords&resumptionToken=HAEXE_2", null);
//				System.out.println(response);
//			} catch (UnsupportedOperationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
						
//				new Thread()
//				{
//					public void run() {				    	
//				        for(int j = 0; j < 10; j++){	
//				        	try {
//								String response = cl.httpGetRequest("http://localhost:8080/oai/OAIHandler?verb=ListRecords&resumptionToken=NTSWS_0", null);
//								
//								if(response != "200"){
//				    				System.out.println("1 req: " + response);	
//				    			}
//							} catch (UnsupportedOperationException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//				        }				        				       
//				    }
//				}.start();
				
				new Thread()
				{
				    public void run() {				    	
				    	for(int k = 0; k < 370; k++){						        	
				    		try {
				    			String response = cl.httpGetRequest("http://data.jewisheritage.net/oai/OAIHandler?verb=ListRecords&resumptionToken=PBJCX_0", null);
				    			System.out.println("2 req: " + response);
	    											
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
				        }
				    }
				}.start();							
		}		
						
//		try {
//			final String body1 = readFile("test1.txt",StandardCharsets.UTF_8);
//			final String body2 = readFile("test2.txt",StandardCharsets.UTF_8);
//			
			for(int i=0; i < 1; i++){
//				new Thread()
//				{
//				    public void run() {				    	
//				        for(int j = 0; j < 100000; j++){	
//							cl.httpPost("http://localhost:8080/EuropeanaDirect/api/object", props, body1);							
//				        }				        				       
//				    }
//				}.start();
				
//				new Thread()
//				{
//				    public void run() {				    	
//				    	for(int k = 0; k < 30; k++){						        	
//							System.out.println(cl.httpPost("http://localhost:8080/rest/api/object?apikey=g31u2hg4qcnoo2hupe06o21re2", props, body2));
//							System.out.println(k);
//				        }
//				    }
//				}.start();			
			}			
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		
	
		
//		try{
//			
//			OaiPmhServer server = new OaiPmhServer("http://localhost:8080/OAI-PMH/OAIHandler");
//			
//			List<EdmOaiSource> edmOaiSourceList = new ArrayList<EdmOaiSource>();
//			ISourceReader<EdmOaiSource> sourceReader = new SourceReader();
//			IMapper<EdmOaiSource> edmMapper = new EdmMapper();
//			IMapper<EdmOaiSource> oaiDcMapper = new OaiDcMapper();
//			
//			RecordsList records = null;
//			List<Record> recordsList = null;	
//			ResumptionToken resumptionToken = null;					
//
//			records = server.listRecords("edm");
//			int j = 0;
//			
//			while (j == 0 || resumptionToken != null) {							
//				if(j > 0 && resumptionToken != null){											
//					records = server.listRecords(resumptionToken);						
//				}				
//				j++;
//				recordsList = records.asList();					
//																											
//				resumptionToken = records.getResumptionToken();								
//			}	
//			System.out.println("harvesting finished");
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		
		

		
		//GeoLocationServiceHelper geoLocationServiceHelper = new GeoLocationServiceHelper();
		//Map<String,String> locationDetails = geoLocationServiceHelper.getLocationDetails("http://www.geonames.org/792680/","sr");
//		try
//		{		
//			ISourceReader<EdmOaiSource> sourceReader = new SourceReader();									
//			String content = readFile("test.txt", StandardCharsets.UTF_8);
//			EdmOaiSource edmSource = sourceReader.readSourceDataEdm(content,"23232323");				
//			IMapper<EdmOaiSource> edmMapper = new EdmMapper();						
//			CulturalHeritageObject cho =  edmMapper.mapFromEdm(edmSource);										
//		}		
//		catch(Exception e){
//			e.printStackTrace();
//		}		
		
		try{
			//EdmMapper edmMapper = new EdmMapper();
			//IMapper<EdmOaiSource> oaiDcMapper = new OaiDcMapper();
			//ISourceReader<EdmOaiSource> sourceReader = new SourceReader();
			//http://cyfrowe.mnw.art.pl/oai-pmh-repository.xml
			//"http://oai.europeana.eu/oaicat/OAIHandler"
			//System.out.println("http://www.arkivverket.no/URN:NBN:no-a1450-ft10061202294760.jpg".toLowerCase().contains(".jpg"));

			//List<EdmOaiSource> list = readSourceDataFromOaiPmh("http://oai.europeana.eu/oaicat/OAIHandler","edm");					
			
			//List<CulturalHeritageObject> choList = edmMapper.getCulturalHeritageObjects(list,"");
			//System.out.println(choList.size());
			/*int k = 0;
			
			System.out.println(choList.size());
			for(CulturalHeritageObject cho : choList){
				k++;
				for(Agent a : cho.getAgents()){
					for(AgentLanguageAware al : a.getLanguageAwareFields()){
						if(al.getLanguage() == null || al.getPreferredLabel() == null){
							System.out.println("null "+k);	
						}						
					}										
				}				
			}*/
			
			/*String content = "";
			List<EdmOaiSource> list2 = new ArrayList();
			
			for(int i = 2; i <= 8465; i++){								
				try{
					content = readFile("C:/Users/nino.berke/Desktop/result/Edm"+i+".xml");													
					list2.add(sourceReader.readSourceDataEdm(content, "Edm"+i));					
				}catch(Exception e){
					e.printStackTrace();
				}						
			}
						
			List<CulturalHeritageObject> choList2 = edmMapper.getCulturalHeritageObjects(list2);
			System.out.println(choList2.size());
			System.out.println("errors "+edmMapper.getErrors());
			/*for(CulturalHeritageObject cho : choList2){			
				System.out.println(cho);				
			}*/
			
		}catch (Exception e){
			e.printStackTrace();
		}	
	}	
	
	public static List<EdmOaiSource> readSourceDataFromOaiPmh(String url, String metadataFormat) throws Exception {

		OaiPmhServer server = new OaiPmhServer(url);
		List<EdmOaiSource> edmOaiSourceList = new ArrayList<EdmOaiSource>();
		ISourceReader<EdmOaiSource> sourceReader = new SourceReader();

		try {
			
			main:for(Set s : server.listSets().asList()){
				RecordsList records = null;
				List<Record> recordsList = null;
				
				IdentifiersList list = server.listIdentifiers(metadataFormat, null,null,s.getSpec());
				// Retrieve all records from source
				ResumptionToken resumptionToken = list.getResumptionToken();							
				
				int j = 0;
				while (j == 0 || resumptionToken != null) {				
					j++;
					if(resumptionToken != null){					
						records = server.listRecords(resumptionToken);
					}else{
						records = server.listRecords(metadataFormat);
					}				
					recordsList = records.asList();				
					for (int i = 0; i < recordsList.size(); i++) {
						System.out.println(i);
						try {
							// Record metadata
							String recordXmlMetadata = recordsList.get(i).getMetadataAsString();
							Header header = recordsList.get(i).getHeader();
							String identifier = header.getIdentifier();

							// map metadata to edmOai model
							if (metadataFormat.equals("oai_dc") || metadataFormat.equals("mets")) {
								edmOaiSourceList.add(sourceReader.readSourceDataOaiDc(recordXmlMetadata, identifier));
							} else {
								edmOaiSourceList.add(sourceReader.readSourceDataEdm(recordXmlMetadata, identifier));
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					resumptionToken = records.getResumptionToken();	
										
				}		
			}			
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return edmOaiSourceList;
	}
	
}
