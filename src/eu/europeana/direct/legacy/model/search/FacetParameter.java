package eu.europeana.direct.legacy.model.search;

public class FacetParameter {

	private String facetTitle;
	private int topChildrens;
	
	public FacetParameter(String facetTitle, int topChildrens) {
		super();
		this.facetTitle = facetTitle;
		this.topChildrens = topChildrens;
	}
	public FacetParameter() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getFacetTitle() {
		return facetTitle;
	}
	public void setFacetTitle(String facetTitle) {
		this.facetTitle = facetTitle;
	}
	public int getTopChildrens() {
		return topChildrens;
	}
	public void setTopChildrens(int topChildrens) {
		this.topChildrens = topChildrens;
	}
	
	
}
