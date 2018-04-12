package inescid.opaf.europeanadirect;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import inescid.opaf.manager.DataSourceManager;
import inescid.opaf.manager.iiif.DataSourceIiif;

/**
 * Executes a single crawl of a IIIF endpoint
 * 
 * @author Nuno
 *
 */
public class IiifEndpointHarvester {
	URL crawlingSitemapUrl;
	URL collectionStartUrl;
	
	Properties iiifHarvesterProperties;
	private List<CulturalHeritageObject> choList = new ArrayList<>();
	
	/**
	 */
	public IiifEndpointHarvester(String dataSourceName, File workingDirectory) {
		iiifHarvesterProperties=new Properties();
		iiifHarvesterProperties.setProperty("opaf.workingdir", workingDirectory.getAbsolutePath());
		iiifHarvesterProperties.setProperty("opaf.datasource.iiif.class", "inescid.opaf.manager.iiif.DataSourceIiif ");
		iiifHarvesterProperties.setProperty("opaf.datasource.iiif.properties.ManifestCrawlHandler.class",
				"inescid.opaf.europeanadirect.CrawlingHandlerForEuropeanaDirect");	 
		iiifHarvesterProperties.setProperty("opaf.datasource.iiif.properties.name", dataSourceName);		 
	}
	
	/**
	 * Executes the harvesting process. The harvested records are passed on to a RecordHandler, 
	 * 
	 * @param handler 
	 * @throws Exception 
	 */
	public void runHarvest(RecordHandler handler) throws Exception {
		try {
				DataSourceManager manager=new DataSourceManager();
				manager.init(iiifHarvesterProperties);				
				DataSourceIiif dataSourceIiif = (DataSourceIiif)manager.getDataSources().iterator().next();
				((CrawlingHandlerForEuropeanaDirect)dataSourceIiif.getHandler()).setRecordHandler(handler);				
				manager.syncAll();						
				choList = ((CrawlingHandlerForEuropeanaDirect)((DataSourceIiif)manager.getDataSources().iterator().next()).getHandler()).getChoList();
				manager.close();				
		}finally {
			close();
		}
	}
	
	
	private void close() {		
		//TODO: for Nuno
	}

	public URL getCrawlingSitemapUrl() {
		return crawlingSitemapUrl;
	}

	public void setCrawlingSitemapUrl(URL crawlingSitemapUrl) {
		this.crawlingSitemapUrl = crawlingSitemapUrl;
		iiifHarvesterProperties.setProperty("opaf.datasource.iiif.properties.sitemap", crawlingSitemapUrl.toString());		 
	}

	public URL getCollectionStartUrl() {
		return collectionStartUrl;
	}

	public void setCollectionStartUrl(URL collectionStartUrl) {
		this.collectionStartUrl = collectionStartUrl;
		iiifHarvesterProperties.setProperty("opaf.datasource.iiif.properties.collection", collectionStartUrl.toString());	
	}

	public List<CulturalHeritageObject> getChoList() {
		return choList;
	}

	public void setChoList(List<CulturalHeritageObject> choList) {
		this.choList = choList;
	}		
}
