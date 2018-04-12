package eu.europeana.direct.legacy.model.record;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EuropeanaAggregation {

	private String about;
	private WebResource[] webResources;
	private String aggregatedCHO;
	private String[] aggregates;
	private Map<String, List<String>> dcCreator = new HashMap<String, List<String>>();
	private String edmLandingPage;
	private String edmIsShownBy;
	private String[] edmHasView;
	private Map<String, List<String>> edmCountry = new HashMap<String, List<String>>();
	private Map<String, List<String>> edmLanguage = new HashMap<String, List<String>>();
	private Map<String, List<String>> edmRights = new HashMap<String, List<String>>();
	private String edmPreview;

	public EuropeanaAggregation() {
	}

	/**
	 * URI of the europeanaAggregation
	 * 
	 */
	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	/**
	 * A collection of webResource objects
	 * 
	 */
	public WebResource[] getWebResources() {
		return webResources;
	}

	public void setWebResources(WebResource[] webResources) {
		this.webResources = webResources;
	}

	/**
	 * The ID of the record corresponding to the CHO of this aggregation
	 * 
	 */
	public String getAggregatedCHO() {
		return aggregatedCHO;
	}

	public void setAggregatedCHO(String aggregatedCHO) {
		this.aggregatedCHO = aggregatedCHO;
	}

	/**
	 * Aggregations, by definition, aggregate resources. The ore:aggregates
	 * relationship expresses that the object resource is a member of the set of
	 * aggregated resources of the subject (the Aggregation). This relationship
	 * between the Aggregation and its Aggregated Resources is thus more
	 * specific than a simple part/whole relationship, as expressed by
	 * dcterms:hasPart for example.
	 * 
	 */
	public String[] getAggregates() {
		return aggregates;
	}

	public void setAggregates(String[] aggregates) {
		this.aggregates = aggregates;
	}

	/**
	 * A creator definitions. This field has always the value Europeana
	 * 
	 */
	public Map<String, List<String>> getDcCreator() {
		return dcCreator;
	}

	public void setDcCreator(Map<String, List<String>> dcCreator) {
		this.dcCreator = dcCreator;
	}

	/**
	 * This property captures the relation between an aggregation representing a
	 * cultural heritage object and the Web resource representing that object on
	 * the provider’s web site.
	 * 
	 */
	public String getEdmLandingPage() {
		return edmLandingPage;
	}

	public void setEdmLandingPage(String edmLandingPage) {
		this.edmLandingPage = edmLandingPage;
	}

	/**
	 * An unambiguous URL reference to the digital object on the provider’s web
	 * site in the best available resolution/quality.
	 * 
	 */
	public String getEdmIsShownBy() {
		return edmIsShownBy;
	}

	public void setEdmIsShownBy(String edmIsShownBy) {
		this.edmIsShownBy = edmIsShownBy;
	}

	/**
	 * This property relates a ORE aggregation about a CHO with a web resource
	 * providing a view of that CHO. Examples of view are: a thumbnail, a
	 * textual abstract and a table of contents.
	 * 
	 */
	public String[] getEdmHasView() {
		return edmHasView;
	}

	public void setEdmHasView(String[] edmHasView) {
		this.edmHasView = edmHasView;
	}

	/**
	 * This is the name of the country in which the Provider is based or
	 * “Europe” in the case of Europe-wide projects.
	 * 
	 */
	public Map<String, List<String>> getEdmCountry() {
		return edmCountry;
	}

	public void setEdmCountry(Map<String, List<String>> edmCountry) {
		this.edmCountry = edmCountry;
	}

	/**
	 * A language assigned to the resource with reference to the Provider.
	 * 
	 */
	public Map<String, List<String>> getEdmLanguage() {
		return edmLanguage;
	}

	public void setEdmLanguage(Map<String, List<String>> edmLanguage) {
		this.edmLanguage = edmLanguage;
	}

	/**
	 * Information about copyright of the digital object as specified by
	 * isShownBy and isShownAt.
	 * 
	 */
	public Map<String, List<String>> getEdmRights() {
		return edmRights;
	}

	public void setEdmRights(Map<String, List<String>> edmRights) {
		this.edmRights = edmRights;
	}

	/**
	 * 
	 */
	public String getEdmPreview() {
		return edmPreview;
	}

	public void setEdmPreview(String edmPreview) {
		this.edmPreview = edmPreview;
	}

}
