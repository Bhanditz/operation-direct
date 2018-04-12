package eu.europeana.direct.legacy.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.facet.FacetResult;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import eu.europeana.direct.legacy.model.search.Item;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LuceneSearchModel {

	private int totalResults;
	private List<Item> items = new ArrayList<>();
	private List<FacetResult> facets = new ArrayList<>();	
	
	public LuceneSearchModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	public LuceneSearchModel(List<Item> items, List<FacetResult> facets) {
		super();
		this.items = items;
		this.facets = facets;
	}		
	
	public int getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}
	/**
	 * List of found items from index with search query	 
	 */
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	/** 
	 * List of found facets from index
	 */
	public List<FacetResult> getFacets() {
		return facets;
	}
	public void setFacets(List<FacetResult> facets) {
		this.facets = facets;
	}		
}
