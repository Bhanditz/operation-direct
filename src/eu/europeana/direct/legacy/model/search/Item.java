package eu.europeana.direct.legacy.model.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Item {
	
	// cultural heritage object search model fields
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double europeanaCompleteness;
	private String[] dataProvider;
	private String[] europeanaCollectionName;
	private String id;
	private String guid;
	private String link;
	private String provider;
	private String[] rights;
	private String type;
	private String[] dcCreator;
	private String edmConceptLabel;
	private String edmPreview;
	private String edmTimespanLabel;
	private String[] language;
	private String[] title;
	private String[] year;
	private String[] edmIsShownAt;
	private String[] edmPlaceLatitude;
	private String[] edmPlaceLongitude;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double score;
	private String[] edmConceptTerm;
	private String[] edmConceptPrefLabel;	
	private String[] edmConceptBroaderTerm;	
	private String[] edmConceptBroaderLabel;	
	private String[] edmTimespanBegin;	
	private String[] edmTimespanEnd;
	private String[] edmTimespanBroaderTerm;
	private String[] edmTimespanBroaderLabel;
	private boolean[] ugc;
	private String[] country;
	private String[] edmPlaceBroaderTerm;
	private String[] edmPlaceAltLabel;
	private String[] dctermsIsPartOf;
	private String[] dctermsSpatial;
	private String[] edmPlace;
	private String[] edmTimespan;
	private String[] edmAgent;
	private String[] edmAgentLabel;
	private String[] dcContributor;
	private String[] isShownBy;
	private String[] dcDescription;
	private String[] edmLandingPage;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double timestamp_created_epoch;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double timestamp_update_epoch;
	private String timestamp_created;
	private String timestamp_update;
	private String deletion_date;
	private String setSpec;
	
	public Item(String id, String deletion_date) {
		super();
		this.id = id;
		this.deletion_date = deletion_date;
	}

	// agent search model fields
	private String dateOfBirth;
	private String dateOfDeath;
	private String dateOfTermination;
	private String dateOfEstablishment;
	private AgentItemLanguageAware[] agentLanguageAwareFields;
	
	
	// concept search model fields
	private ConceptItemLanguageAware[] conceptLanguageAwareFields;

	// concept search model fields
	private TimespanItemLanguageAware[] timespanLanguageAwareFields;

	//place search model fields
	private String[] altitude;
	private PlaceItemLanguageAware[] placeLanguageAwareFields;
	
	//weblink search model fields
	private String owner;	
	
	public Item() {
		super();
		// TODO Auto-generated constructor stub
	}	
	
	public String getDeletion_date() {
		return deletion_date;
	}

	public void setDeletion_date(String deletion_date) {
		this.deletion_date = deletion_date;
	}

	/**
	 * A number from 1 to 10 that is an internal measure of the metadata quality. It is based on the availability of mandatory and optional schema fields.
	 */
	public double getEuropeanaCompleteness() {
		return europeanaCompleteness;
	}
	public void setEuropeanaCompleteness(double europeanaCompleteness) {
		this.europeanaCompleteness = europeanaCompleteness;
	}

	/**
	 * The names of Europeana Data Providers who provided the object.

	 */
	public String[] getDataProvider() {
		return dataProvider;
	}
	public void setDataProvider(String[] dataProvider) {
		this.dataProvider = dataProvider;
	}
	/**
	 * The names of the Europeana collections that contain the item

	 */
	public String[] getEuropeanaCollectionName() {
		return europeanaCollectionName;
	}
	public void setEuropeanaCollectionName(String[] europeanaCollectionName) {
		this.europeanaCollectionName = europeanaCollectionName;
	}
	/**
	 * The Europeana ID of the record.
	 */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * A link to the object page on the Europeana portal to be used by client applications.

	 */
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	/**
	 * A link to the API object call. This link should be used to retrieve the full metadata object.

	 */
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	/**
	 * The name or identifier of the provider of the object.

	 */
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	/**
	 * A collection of URLs referring to the object rights.

	 */
	public String[] getRights() {
		return rights;
	}
	public void setRights(String[] rights) {
		this.rights = rights;
	}
	/**
	 * The type of the provided object (TEXT, VIDEO, SOUND, IMAGE, 3D)

	 */
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * A collection entities primarily responsible for making the resource.

	 */
	public String[] getDcCreator() {
		return dcCreator;
	}
	public void setDcCreator(String[] dcCreator) {
		this.dcCreator = dcCreator;
	}

	/**
	 * The label of the SKOS Concept of the record. Find more in EDM definition

	 */
	public String getEdmConceptLabel() {
		return edmConceptLabel;
	}
	public void setEdmConceptLabel(String edmConceptLabel) {
		this.edmConceptLabel = edmConceptLabel;
	}
	/**
	 * A link to the representation of the object on Europeana.

	 */
	public String getEdmPreview() {
		return edmPreview;
	}
	public void setEdmPreview(String edmPreview) {
		this.edmPreview = edmPreview;
	}
	/**
	 * The label of EDM Time Span object of the record. Find more in EDM Definition

	 */
	public String getEdmTimespanLabel() {
		return edmTimespanLabel;
	}
	public void setEdmTimespanLabel(String edmTimespanLabel) {
		this.edmTimespanLabel = edmTimespanLabel;
	}
	/**
	 * Languages assigned to the resource with reference to the Provider. Usually, this field contains the languages of the metadata of the record.

	 */
	public String[] getLanguage() {
		return language;
	}
	public void setLanguage(String[] language) {
		this.language = language;
	}
	/**
	 * The main and alternative titles of the item.

	 */
	public String[] getTitle() {
		return title;
	}
	public void setTitle(String[] title) {
		this.title = title;
	}
	/**
	 * A point of time associated with an event in the life of the original analog or born digital object. Find more in EDM definition

	 */
	public String[] getYear() {
		return year;
	}
	public void setYear(String[] year) {
		this.year = year;
	}
	/**
	 * The URL of a web view of the object in full information context.

	 */
	public String[] getEdmIsShownAt() {
		return edmIsShownAt;
	}
	public void setEdmIsShownAt(String[] edmIsShownAt) {
		this.edmIsShownAt = edmIsShownAt;
	}
	/**
	 * The latitude of a spatial thing (decimal degrees).

	 */
	public String[] getEdmPlaceLatitude() {
		return edmPlaceLatitude;
	}
	public void setEdmPlaceLatitude(String[] edmPlaceLatitude) {
		this.edmPlaceLatitude = edmPlaceLatitude;
	}
	/**
	 * The longitude of a spatial thing (decimal degrees).

	 */
	public String[] getEdmPlaceLongitude() {
		return edmPlaceLongitude;
	}
	public void setEdmPlaceLongitude(String[] edmPlaceLongitude) {
		this.edmPlaceLongitude = edmPlaceLongitude;
	}
	/**
	 * The relevancy score calculated by the search engine. Depends of the query.

	 */
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	/**
	 * A SKOS Concept.

	 */
	public String[] getEdmConceptTerm() {
		return edmConceptTerm;
	}
	public void setEdmConceptTerm(String[] edmConceptTerm) {
		this.edmConceptTerm = edmConceptTerm;
	}
	/**
	 * The preferred form of the name of the concept.

	 */
	public String[] getEdmConceptPrefLabel() {
		return edmConceptPrefLabel;
	}
	public void setEdmConceptPrefLabel(String[] edmConceptPrefLabel) {
		this.edmConceptPrefLabel = edmConceptPrefLabel;
	}
	/**
	 * The identifier of a broader concept in the same thesaurus or controlled vocabulary

	 */
	public String[] getEdmConceptBroaderTerm() {
		return edmConceptBroaderTerm;
	}
	public void setEdmConceptBroaderTerm(String[] edmConceptBroaderTerm) {
		this.edmConceptBroaderTerm = edmConceptBroaderTerm;
	}
	/**
	 * A human readable name of a broader concept.

	 */
	public String[] getEdmConceptBroaderLabel() {
		return edmConceptBroaderLabel;
	}
	public void setEdmConceptBroaderLabel(String[] edmConceptBroaderLabel) {
		this.edmConceptBroaderLabel = edmConceptBroaderLabel;
	}
	/**
	 * The date the timespan started.

	 */
	public String[] getEdmTimespanBegin() {
		return edmTimespanBegin;
	}
	public void setEdmTimespanBegin(String[] edmTimespanBegin) {
		this.edmTimespanBegin = edmTimespanBegin;
	}
	/**
	 * The date the timespan finished.

	 */
	public String[] getEdmTimespanEnd() {
		return edmTimespanEnd;
	}
	public void setEdmTimespanEnd(String[] edmTimespanEnd) {
		this.edmTimespanEnd = edmTimespanEnd;
	}
	/**
	 * ts_dcterms_isPartOf

	 */
	public String[] getEdmTimespanBroaderTerm() {
		return edmTimespanBroaderTerm;
	}
	public void setEdmTimespanBroaderTerm(String[] edmTimespanBroaderTerm) {
		this.edmTimespanBroaderTerm = edmTimespanBroaderTerm;
	}
	/**
	 * ts_dcterms_isPartOf

	 */
	public String[] getEdmTimespanBroaderLabel() {
		return edmTimespanBroaderLabel;
	}
	public void setEdmTimespanBroaderLabel(String[] edmTimespanBroaderLabel) {
		this.edmTimespanBroaderLabel = edmTimespanBroaderLabel;
	}
	/**
	 * Whether or not the record includeshas user generated contents in the record

	 */
	public boolean[] getUgc() {
		return ugc;
	}
	public void setUgc(boolean[] ugc) {
		this.ugc = ugc;
	}
	/**
	 * The name of the country in which the Provider is based or “Europe” in the case of Europe-wide projects.

	 */
	public String[] getCountry() {
		return country;
	}
	public void setCountry(String[] country) {
		this.country = country;
	}
	/**
	 * pl_dcterms_isPartOf

	 */
	public String[] getEdmPlaceBroaderTerm() {
		return edmPlaceBroaderTerm;
	}
	public void setEdmPlaceBroaderTerm(String[] edmPlaceBroaderTerm) {
		this.edmPlaceBroaderTerm = edmPlaceBroaderTerm;
	}
	/**
	 * Alternative forms of the name of the place.

	 */
	public String[] getEdmPlaceAltLabel() {
		return edmPlaceAltLabel;
	}
	public void setEdmPlaceAltLabel(String[] edmPlaceAltLabel) {
		this.edmPlaceAltLabel = edmPlaceAltLabel;
	}
	/**
	 * A related resource in which the described resource is physically or logically included.

	 */
	public String[] getDctermsIsPartOf() {
		return dctermsIsPartOf;
	}
	public void setDctermsIsPartOf(String[] dctermsIsPartOf) {
		this.dctermsIsPartOf = dctermsIsPartOf;
	}
	/**
	 * Spatial characteristics of the resource.

	 */
	public String[] getDctermsSpatial() {
		return dctermsSpatial;
	}
	public void setDctermsSpatial(String[] dctermsSpatial) {
		this.dctermsSpatial = dctermsSpatial;
	}
	/**
	 * The URI of an EDM Place object.

	 */
	public String[] getEdmPlace() {
		return edmPlace;
	}
	public void setEdmPlace(String[] edmPlace) {
		this.edmPlace = edmPlace;
	}
	/**
	 * The URI of an EDM Timespan object.

	 */
	public String[] getEdmTimespan() {
		return edmTimespan;
	}
	public void setEdmTimespan(String[] edmTimespan) {
		this.edmTimespan = edmTimespan;
	}
	/**
	 * The URI of an EDM Agent object

	 */
	public String[] getEdmAgent() {
		return edmAgent;
	}
	public void setEdmAgent(String[] edmAgent) {
		this.edmAgent = edmAgent;
	}
	/**
	 * Name of the agent.

	 */
	public String[] getEdmAgentLabel() {
		return edmAgentLabel;
	}
	public void setEdmAgentLabel(String[] edmAgentLabel) {
		this.edmAgentLabel = edmAgentLabel;
	}
	/**
	 * An entity responsible for making contributions to the resource.

	 */
	public String[] getDcContributor() {
		return dcContributor;
	}
	public void setDcContributor(String[] dcContributor) {
		this.dcContributor = dcContributor;
	}
	/**
	 * The URL of a web view of the object.

	 */
	public String[] getIsShownBy() {
		return isShownBy;
	}
	public void setIsShownBy(String[] isShownBy) {
		this.isShownBy = isShownBy;
	}
	/**
	 * A description of the resource.

	 */
	public String[] getDcDescription() {
		return dcDescription;
	}
	public void setDcDescription(String[] dcDescription) {
		this.dcDescription = dcDescription;
	}
	/**
	 * This property captures the relation between an aggregation representing a cultural heritage object and the Web resource representing that object on the provider’s web site.

	 */
	public String[] getEdmLandingPage() {
		return edmLandingPage;
	}
	public void setEdmLandingPage(String[] edmLandingPage) {
		this.edmLandingPage = edmLandingPage;
	}
	/**
	 * UNIX timestamp of the date when record were created

	 */
	public double getTimestamp_created_epoch() {
		return timestamp_created_epoch;
	}
	public void setTimestamp_created_epoch(double timestamp_created_epoch) {
		this.timestamp_created_epoch = timestamp_created_epoch;
	}
	/**
	 * UNIX timestamp of the date when record were last updated

	 */
	public double getTimestamp_update_epoch() {
		return timestamp_update_epoch;
	}
	public void setTimestamp_update_epoch(double timestamp_update_epoch) {
		this.timestamp_update_epoch = timestamp_update_epoch;
	}
	/**
	 * ISO 8601 format of the date when record were created
	 */
	public String getTimestamp_created() {
		return timestamp_created;
	}
	public void setTimestamp_created(String timestamp_created) {
		this.timestamp_created = timestamp_created;
	}
	/**
	 * ISO 8601 format of the date when record were last updated
	 */
	public String getTimestamp_update() {
		return timestamp_update;
	}
	public void setTimestamp_update(String timestamp_update) {
		this.timestamp_update = timestamp_update;
	}
	/**
	 * 
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	/**
	 * 
	 */
	public String getDateOfDeath() {
		return dateOfDeath;
	}
	public void setDateOfDeath(String dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}
	/**
	 * 
	 */
	public String getDateOfTermination() {
		return dateOfTermination;
	}
	public void setDateOfTermination(String dateOfTermination) {
		this.dateOfTermination = dateOfTermination;
	}
	/**
	 * 
	 */
	public String getDateOfEstablishment() {
		return dateOfEstablishment;
	}
	public void setDateOfEstablishment(String dateOfEstablishment) {
		this.dateOfEstablishment = dateOfEstablishment;
	}
	/**
	 * 
	 */
	public AgentItemLanguageAware[] getAgentLanguageAwareFields() {
		return agentLanguageAwareFields;
	}
	public void setAgentLanguageAwareFields(AgentItemLanguageAware[] agentLanguageAwareFields) {
		this.agentLanguageAwareFields = agentLanguageAwareFields;
	}
	/**
	 * 
	 */
	public ConceptItemLanguageAware[] getConceptLanguageAwareFields() {
		return conceptLanguageAwareFields;
	}
	public void setConceptLanguageAwareFields(ConceptItemLanguageAware[] conceptLanguageAwareFields) {
		this.conceptLanguageAwareFields = conceptLanguageAwareFields;
	}
	/**
	 * 
	 */
	public TimespanItemLanguageAware[] getTimespanLanguageAwareFields() {
		return timespanLanguageAwareFields;
	}
	public void setTimespanLanguageAwareFields(TimespanItemLanguageAware[] timespanLanguageAwareFields) {
		this.timespanLanguageAwareFields = timespanLanguageAwareFields;
	}
	/**
	 * 
	 */
	public PlaceItemLanguageAware[] getPlaceLanguageAwareFields() {
		return placeLanguageAwareFields;
	}
	public void setPlaceLanguageAwareFields(PlaceItemLanguageAware[] placeLanguageAwareFields) {
		this.placeLanguageAwareFields = placeLanguageAwareFields;
	}
	/**
	 * 
	 */
	public String[] getAltitude() {
		return altitude;
	}
	public void setAltitude(String[] altitude) {
		this.altitude = altitude;
	}
	/**
	 * 
	 */
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getSetSpec() {
		return setSpec;
	}

	public void setSetSpec(String setSpec) {
		this.setSpec = setSpec;
	}	
	
	
}

