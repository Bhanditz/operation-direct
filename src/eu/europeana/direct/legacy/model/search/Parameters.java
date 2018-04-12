package eu.europeana.direct.legacy.model.search;
import java.util.*;

public class Parameters {

	private String query;
	private boolean searchField;
	private String searchTerm;
	private ProfileType profile;
	private List<String> qf = new ArrayList<String>();
	private String reusability;
	private boolean media;
	private boolean thumbnail;
	private boolean landingpage;
	private String colourpalette;
	private String sort;
	private int rows;
	private int start;
	private String cursor;
	private String callback;
	private List<String> facet = new ArrayList<String>();
	private List<FacetParameter> facetWithLimits = new ArrayList<FacetParameter>();
	private String facetLimit;
	private String facetOffset;	
	
	public Parameters() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Parameters(String query,boolean searchField, String searchTerm, ProfileType profile, List<String> qf, String reusability,
			boolean media, boolean thumbnail, boolean landingpage, String colourpalette, String sort, int rows,
			int start, String cursor, String callback, List<String> facet, String facetLimit, String facetOffset) {
		super();
		this.query = query;
		this.searchField = searchField;
		this.searchTerm = searchTerm;
		this.profile = profile;
		this.qf = qf;
		this.reusability = reusability;
		this.media = media;
		this.thumbnail = thumbnail;
		this.landingpage = landingpage;
		this.colourpalette = colourpalette;
		this.sort = sort;
		this.rows = rows;
		this.start = start;
		this.cursor = cursor;
		this.callback = callback;
		this.facet = facet;
		this.facetLimit = facetLimit;
		this.facetOffset = facetOffset;
	}

	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public boolean hasSearchField() {
		return searchField;
	}

	public void setSearchField(boolean searchField) {
		this.searchField = searchField;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	public ProfileType getProfile() {
		return profile;
	}
	public void setProfile(ProfileType profile) {
		this.profile = profile;
	}
	public List<String> getQf() {
		return qf;
	}
	public void setQf(List<String> qf) {
		this.qf = qf;
	}
	public String getReusability() {
		return reusability;
	}
	public void setReusability(String reusability) {
		this.reusability = reusability;
	}
	public boolean isMedia() {
		return media;
	}
	public void setMedia(boolean media) {
		this.media = media;
	}
	public boolean isThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(boolean thumbnail) {
		this.thumbnail = thumbnail;
	}
	public boolean isLandingpage() {
		return landingpage;
	}
	public void setLandingpage(boolean landingpage) {
		this.landingpage = landingpage;
	}
	public String getColourpalette() {
		return colourpalette;
	}
	public void setColourpalette(String colourpalette) {
		this.colourpalette = colourpalette;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public String getCursor() {
		return cursor;
	}
	public void setCursor(String cursor) {
		this.cursor = cursor;
	}
	public String getCallback() {
		return callback;
	}
	public void setCallback(String callback) {
		this.callback = callback;
	}
	public List<String> getFacet() {
		return facet;
	}
	public void setFacet(List<String> facet) {
		this.facet = facet;
	}
	
	
	public List<FacetParameter> getFacetWithLimits() {
		return facetWithLimits;
	}

	public void setFacetWithLimits(List<FacetParameter> facetWithLimits) {
		this.facetWithLimits = facetWithLimits;
	}

	public String getFacetLimit() {
		return facetLimit;
	}
	public void setFacetLimit(String facetLimit) {
		this.facetLimit = facetLimit;
	}
	public String getFacetOffset() {
		return facetOffset;
	}
	public void setFacetOffset(String facetOffset) {
		this.facetOffset = facetOffset;
	}
}
