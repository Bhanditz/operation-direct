package eu.europeana.direct.harvesting.source.edm;

import java.util.List;

import org.xml.sax.SAXParseException;

import eu.europeana.direct.logic.CulturalHeritageObjectLogic;

public interface ISourceReader<T> {
	
	/**
	 * Method retrives objects from oai-pmh source
	 * @param url Harvesting source 
	 * @param metadataFormat Format in which metadata is presented
	 * @return List of read source objects
	 * @throws Exception
	 */
	List<T> readSourceDataFromOaiPmh(String url,String metadataFormat, String harvester) throws Exception;

	/**
	 * Method retrives all records from DDB api
	 * @param url Source url (link od DDB api)
	 * @param metadataFormat Format in which metadata is presented
	 * @param key API key
	 * @return List of read source objects
	 * @throws Exception
	 */
	List<T> readSourceDataFromDDB(String url,String metadataFormat,String key,CulturalHeritageObjectLogic choLogic) throws Exception;

	/**
	 * Method maps metadata to edm source object
	 * @param xml XML metadata of record
	 * @param headerIdentifier Identifier of record
	 * 
	 */
	T readSourceDataEdm(String xml, String headerIdentifier) throws SAXParseException;

	/**
	 * Method maps APE c-level record metadata to edm source object
	 * @param jsonResponse Metadata of record
	 * 
	 */
	T readSourceAPEClevel(String jsonResponse,String language);

	/**
	 * Method maps APE EAD record metadata to edm source object
	 * @param jsonResponse Metadata of record
	 * 
	 */
	T readSourceApeEAD(String jsonResponse);
		
	/**
	 * 
	 * Method maps metadata to edm source object
	 * @param xml XML metadata of record
	 * @param headerIdentifier Identifier of record	 
	 */
	T readSourceDataOaiDc(String xml, String headerIdentifier);
	
	void setInterrupt(boolean interrupt);
	
}