package eu.europeana.direct.harvesting.models.helpers;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import eu.europeana.direct.legacy.model.search.Item;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchModel {

	private List<Item> items = new ArrayList<Item>();
	private int totalResults;
	
	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}
	
	

}
