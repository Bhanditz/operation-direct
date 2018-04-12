package eu.europeana.direct.legacy.model.search;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchViewModel {

	private String apiKey;
	private boolean success; 
	private String action;
	private int itemsCount;
	private int totalResults;
	private String nextCursor;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Item[] items;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Facet[] facets;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Breadcrumb[] breadcrumbs;
	
	public SearchViewModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SearchViewModel(String apiKey, boolean success, String action, int itemsCount, int totalResults,
			String nextCursor, Item[] items, Facet[] facets, Breadcrumb[] breadcrumbs) {
		super();
		this.apiKey = apiKey;
		this.success = success;
		this.action = action;
		this.itemsCount = itemsCount;
		this.totalResults = totalResults;
		this.nextCursor = nextCursor;
		this.items = items;
		this.facets = facets;
		this.breadcrumbs = breadcrumbs;
	}
	
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getItemsCount() {
		return itemsCount;
	}
	public void setItemsCount(int itemsCount) {
		this.itemsCount = itemsCount;
	}
	public int getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}
	public String getNextCursor() {
		return nextCursor;
	}
	public void setNextCursor(String nextCursor) {
		this.nextCursor = nextCursor;
	}
	public Item[] getItems() {
		return items;
	}
	public void setItems(Item[] items) {
		this.items = items;
	}
	public Facet[] getFacets() {
		return facets;
	}
	public void setFacets(Facet[] facets) {
		this.facets = facets;
	}
	public Breadcrumb[] getBreadcrumbs() {
		return breadcrumbs;
	}
	public void setBreadcrumbs(Breadcrumb[] breadcrumbs) {
		this.breadcrumbs = breadcrumbs;
	}	
}
