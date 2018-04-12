package eu.europeana.direct.legacy.model.record;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Aggregation {

	private String about;
	private Map<String,List<String>> edmDataProvider = new HashMap<String,List<String>>();
	private String edmIsShownBy;
	private String edmIsShownAt;
	private String edmObject;
	private Map<String,List<String>> edmProvider = new HashMap<String,List<String>>();
	private Map<String,List<String>> edmRights = new HashMap<String,List<String>>();
	private String edmUgc;
	private Map<String,List<String>> dcRights = new HashMap<String,List<String>>();
	private String[] hasView;
	private String aggregatedCHO;
	private String[] aggregates;
	private String[] edmUnstored;
	private WebResource[] webResources;
	
	public Aggregation(){}
	
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	
	/**
	 * 
	 * Data provider of the CHO
	 */
	public Map<String, List<String>> getEdmDataProvider() {
		return edmDataProvider;
	}
	public void setEdmDataProvider(Map<String, List<String>> edmDataProvider) {
		this.edmDataProvider = edmDataProvider;
	}
	
	/**
	 * The URL of a web view of the object.
	 */
	public String getEdmIsShownBy() {
		return edmIsShownBy;
	}
	public void setEdmIsShownBy(String edmIsShownBy) {
		this.edmIsShownBy = edmIsShownBy;
	}
	
	/**
	 * The URL of a web view of the object in full information context. 
	 */
	public String getEdmIsShownAt() {
		return edmIsShownAt;
	}
	public void setEdmIsShownAt(String edmIsShownAt) {
		this.edmIsShownAt = edmIsShownAt;
	}
	
	/**
	 * The URL of a representation of the CHO which will be used for generating previews for use in the Europeana portal. This may be the same URL as edm:isShownBy
	 */
	public String getEdmObject() {
		return edmObject;
	}
	public void setEdmObject(String edmObject) {
		this.edmObject = edmObject;
	}
	
	/**
	 * The name or identifier of the provider of the object (i.e. the organisation providing data directly to Europeana).	
	 */
	public Map<String, List<String>> getEdmProvider() {
		return edmProvider;
	}
	public void setEdmProvider(Map<String, List<String>> edmProvider) {
		this.edmProvider = edmProvider;
	}
	
	/**
	 * The rights statement that applies to the digital representation as given (for example) in edm:object or edm:isShownAt/By, when these resources are not provided with their own edm:rights.	 
	 */
	public Map<String, List<String>> getEdmRights() {
		return edmRights;
	}
	public void setEdmRights(Map<String, List<String>> edmRights) {
		this.edmRights = edmRights;
	}
	
	/**
	 * This is a mandatory property for objects that are user generated or user created that have been collected by crowdsourcing or project activity. The property is used to identify such content and can only take the value “true” (lower case).	 
	 */
	public String getEdmUgc() {
		return edmUgc;
	}
	public void setEdmUgc(String edmUgc) {
		this.edmUgc = edmUgc;
	}
	
	/**
	 * Information about rights held in and over the resource.	 
	 */
	public Map<String, List<String>> getDcRights() {
		return dcRights;
	}
	public void setDcRights(Map<String, List<String>> dcRights) {
		this.dcRights = dcRights;
	}
	
	/**
	 * The URL of a web resource which is a digital representation of the CHO. This may be the source object itself in the case of a born digital cultural heritage object	 
	 */
	public String[] getHasView() {
		return hasView;
	}
	public void setHasView(String[] hasView) {
		this.hasView = hasView;
	}
	
	/**
	 * The identifier of the source object e.g. the Mona Lisa itself. This could be a full linked open data URI or an internal identifier 
	 */
	public String getAggregatedCHO() {
		return aggregatedCHO;
	}
	public void setAggregatedCHO(String aggregatedCHO) {
		this.aggregatedCHO = aggregatedCHO;
	}
	
	/**
	 * This is a container element which includes all relevant information that otherwise cannot be mapped to another element in the ESE	 
	 */
	public String[] getAggregates() {
		return aggregates;
	}
	public void setAggregates(String[] aggregates) {
		this.aggregates = aggregates;
	}
	
	/**
	 * 	This property should not be used and is only included here for backward compatibility with ESE.	 
	 */
	public String[] getEdmUnstored() {
		return edmUnstored;
	}
	public void setEdmUnstored(String[] edmUnstored) {
		this.edmUnstored = edmUnstored;
	}
	
	/**
	 * Information Resources that have at least one Web Representation and at least a URI.	 
	 */
	public WebResource[] getWebResources() {
		return webResources;
	}
	public void setWebResources(WebResource[] webResources) {
		this.webResources = webResources;
	}
}
