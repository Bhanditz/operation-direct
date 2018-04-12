package inescid.opaf.europeanadirect;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.plaf.synth.SynthSpinnerUI;

import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;

public class Example {
	public static void main(String[] args) {
		try {
			//Harvesting the IIIF endpoint of the National Library of Wales
			IiifEndpointHarvester iiifHarvester=new IiifEndpointHarvester(
					"National Library of Wales", new File("target/iiif_harvester_workdir"));
			iiifHarvester.setCollectionStartUrl(new URL("http://dams.llgc.org.uk/iiif/newspapers/3100020.json"));
//			iiifHarvester.setCrawlingSitemapUrl(new URL("http://dams.llgc.org.uk/iiif/newspapers/sitemap.xml"));

			
			//Harvesting the IIIF endpoint of University College Dublin
//			IiifEndpointHarvester iiifHarvester=new IiifEndpointHarvester(
//					"University College Dublin", new File("target/iiif_harvester_workdir"));
//			iiifHarvester.setCrawlingSitemapUrl(new URL("https://data.ucd.ie/sitemap_europeana1.xml"));

			//Harvesting the IIIF endpoint of Bodleian Digital
			//TODO
			
			iiifHarvester.runHarvest(new RecordHandler() {			
				
				@Override
				public boolean handleRecord(String jsonRecord) {
					System.out.print(jsonRecord);
					return true;
				}
			});		
				
			
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
