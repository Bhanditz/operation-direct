package eu.europeana.direct.legacy.logic.search;

import java.io.IOException;

import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;

import eu.europeana.direct.legacy.index.IndexManager;

public class FacetSearcher implements Runnable{

	private FacetsConfig config;
	private FacetsCollector fc;
			
	public FacetSearcher(FacetsConfig config, FacetsCollector fc) {
		super();
		this.config = config;
		this.fc = fc;
	}

	//asynchronous facets search
	@Override
	public void run() {
	
		TaxonomyReader taxoReader = null;
		
		try {
			taxoReader = IndexManager.getInstance().getTaxonomyReaderInstance();
			Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);										

		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				IndexManager.getInstance().releaseTaxonomyReaderInstance(taxoReader);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

}
