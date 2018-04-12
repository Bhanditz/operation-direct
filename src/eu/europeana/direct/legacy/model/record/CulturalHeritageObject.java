package eu.europeana.direct.legacy.model.record;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CulturalHeritageObject {

	private String about;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Agent[] agents;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Aggregation[] aggregations;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Concept[] concepts;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String[] country;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private EuropeanaAggregation[] europeanaAggregation;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String[] europeanaCollectionName;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double europeanaCompleteness;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String[] language;
	private boolean optOut;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Place[] places;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String[] provider;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private ProvidedCHO[] providedCHOs;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Proxy[] proxies;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Timespan[] timespans;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private long timestamp_created_epoch;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private long timestamp_update_epoch;
	private String timestamp_created;
	private String timestamp_update;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String[] title;
	private String type;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String[] year;
	
	public CulturalHeritageObject(){}

	/**
	 * Defines the resource being described
	 */
	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	/**
	 * 
	 * Agent related to Cultural Heritage Object
	 */
	public Agent[] getAgents() {
		return agents;
	}

	public void setAgents(Agent[] agents) {
		this.agents = agents;
	}

	public Aggregation[] getAggregations() {
		return aggregations;
	}

	public void setAggregations(Aggregation[] aggregations) {
		this.aggregations = aggregations;
	}

	public Concept[] getConcepts() {
		return concepts;
	}

	public void setConcepts(Concept[] concepts) {
		this.concepts = concepts;
	}

	public String[] getCountry() {
		return country;
	}

	public void setCountry(String[] country) {
		this.country = country;
	}

	public EuropeanaAggregation[] getEuropeanaAggregation() {
		return europeanaAggregation;
	}

	public void setEuropeanaAggregation(EuropeanaAggregation[] europeanaAggregation) {
		this.europeanaAggregation = europeanaAggregation;
	}

	public String[] getEuropeanaCollectionName() {
		return europeanaCollectionName;
	}

	public void setEuropeanaCollectionName(String[] europeanaCollectionName) {
		this.europeanaCollectionName = europeanaCollectionName;
	}

	public double getEuropeanaCompleteness() {
		return europeanaCompleteness;
	}

	public void setEuropeanaCompleteness(double europeanaCompleteness) {
		this.europeanaCompleteness = europeanaCompleteness;
	}

	public String[] getLanguage() {
		return language;
	}

	public void setLanguage(String[] language) {
		this.language = language;
	}

	public boolean isOptOut() {
		return optOut;
	}

	public void setOptOut(boolean optOut) {
		this.optOut = optOut;
	}

	public Place[] getPlaces() {
		return places;
	}

	public void setPlaces(Place[] places) {
		this.places = places;
	}

	public String[] getProvider() {
		return provider;
	}

	public void setProvider(String[] provider) {
		this.provider = provider;
	}

	public ProvidedCHO[] getProvidedCHOs() {
		return providedCHOs;
	}

	public void setProvidedCHOs(ProvidedCHO[] providedCHOs) {
		this.providedCHOs = providedCHOs;
	}

	public Proxy[] getProxies() {
		return proxies;
	}

	public void setProxies(Proxy[] proxies) {
		this.proxies = proxies;
	}

	public Timespan[] getTimespans() {
		return timespans;
	}

	public void setTimespans(Timespan[] timespans) {
		this.timespans = timespans;
	}

	public long getTimestamp_created_epoch() {
		return timestamp_created_epoch;
	}

	public void setTimestamp_created_epoch(long timestamp_created_epoch) {
		this.timestamp_created_epoch = timestamp_created_epoch;
	}

	public long getTimestamp_update_epoch() {
		return timestamp_update_epoch;
	}

	public void setTimestamp_update_epoch(long timestamp_update_epoch) {
		this.timestamp_update_epoch = timestamp_update_epoch;
	}

	public String getTimestamp_created() {
		return timestamp_created;
	}

	public void setTimestamp_created(String timestamp_created) {
		this.timestamp_created = timestamp_created;
	}

	public String getTimestamp_update() {
		return timestamp_update;
	}

	public void setTimestamp_update(String timestamp_update) {
		this.timestamp_update = timestamp_update;
	}

	public String[] getTitle() {
		return title;
	}

	public void setTitle(String[] title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getYear() {
		return year;
	}

	public void setYear(String[] year) {
		this.year = year;
	}
}
