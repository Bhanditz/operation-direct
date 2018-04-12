package eu.europeana.direct.backend.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import eu.europeana.direct.backend.userTypes.JSONBType;

@SuppressWarnings("serial")
@Entity
@Table(name = "europeanadataobject", schema = "public")
@TypeDefs({		
	@TypeDef(name = "AwareCustomFields", typeClass = JSONBType.class, parameters = { @Parameter(name = "type", value="DOUBLE_MAP")}),
	@TypeDef(name = "NonAwareCustomFields", typeClass = JSONBType.class, parameters = { @Parameter(name = "type", value="MAP"), @Parameter(name ="value",value = "java.lang.String")})		
	})
@NamedQueries({
@NamedQuery(name = "EuropeanaDataObject.findById", query = "SELECT e FROM EuropeanaDataObject e WHERE e.id = :id") })
public class EuropeanaDataObject implements java.io.Serializable {

	private long id;
	private EuropeanaDataObject parent;
	private ExternalVocabulary externalVocabulary;
	private License license;
	private Date created;
	private Date modified;
	private Date lastIndexed;
	private String edmCompatibilitySerialization;
	private String vocabularyTerm;
	private Concept concept;
	private Set<EuropeanaDataObject> europeanaDataObjects = new HashSet<EuropeanaDataObject>(
			0);
	private Set<WebResource> webResources = new HashSet<WebResource>(0);
	private Set<EuropeanaDataObjectEuropeanaDataObject> europeanaDataObjectForLinkedObject = new HashSet<EuropeanaDataObjectEuropeanaDataObject>(
			0);
	private Place place;
	private Agent agent;
	private CulturalHeritageObject culturalHeritageObject;
	private Set<EuropeanaDataObjectEuropeanaDataObject> europeanaDataObjectForLinkingObject = new HashSet<EuropeanaDataObjectEuropeanaDataObject>(
			0);
	private Timespan timespan;	
	
	private Map<String,String> nonAwareCustomFields = new HashMap<String,String>();
	private Map<String,Map<String,String>> awareCustomFields = new HashMap<String,Map<String,String>>();
	private Date deletionDate;
	
	public EuropeanaDataObject() {
	}

	public EuropeanaDataObject(int id, License license, Date created,
			Date modified) {
		this.id = id;
		this.license = license;
		this.created = created;
		this.modified = modified;
	}

	public EuropeanaDataObject(
			int id,
			EuropeanaDataObject europeanaDataObject,
			ExternalVocabulary externalVocabulary,
			License license,
			Date created,
			Date modified,
			Date lastIndexed,
			String edmCompatibilitySerialization,
			String vocabularyTerm,
			Concept concept,
			Set<EuropeanaDataObject> europeanaDataObjects,
			Set<WebResource> webResources,
			Set<EuropeanaDataObjectEuropeanaDataObject> europeanaDataObjectForLinkedObject,
			Place place,
			Agent agent,
			CulturalHeritageObject culturalHeritageObject,
			Set<EuropeanaDataObjectEuropeanaDataObject> europeanadDataObjectForLinkingObject,
			Timespan timespan){
		this.id = id;
		this.parent = europeanaDataObject;
		this.externalVocabulary = externalVocabulary;
		this.license = license;
		this.created = created;
		this.modified = modified;
		this.lastIndexed = lastIndexed;
		this.edmCompatibilitySerialization = edmCompatibilitySerialization;
		this.vocabularyTerm = vocabularyTerm;
		this.concept = concept;
		this.europeanaDataObjects = europeanaDataObjects;
		this.webResources = webResources;
		this.europeanaDataObjectForLinkedObject = europeanaDataObjectForLinkedObject;
		this.place = place;
		this.agent = agent;
		this.culturalHeritageObject = culturalHeritageObject;
		this.europeanaDataObjectForLinkingObject = europeanadDataObjectForLinkingObject;
		this.timespan = timespan;
	}

	@Id
	@Generated(GenerationTime.INSERT)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentid")
	public EuropeanaDataObject getParent() {
		return this.parent;
	}

	public void setParent(EuropeanaDataObject parent) {
		this.parent = parent;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "externalvocabularyid")
	public ExternalVocabulary getExternalVocabulary() {
		return this.externalVocabulary;
	}

	public void setExternalVocabulary(ExternalVocabulary externalVocabulary) {
		this.externalVocabulary = externalVocabulary;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "licenseid", nullable = false)
	public License getLicense() {
		return this.license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "created", nullable = false, length = 13)
	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "modified", nullable = false, length = 13)
	public Date getModified() {
		return this.modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastindexed", length = 13)
	public Date getLastIndexed() {
		return this.lastIndexed;
	}

	public void setLastIndexed(Date lastIndexed) {
		this.lastIndexed = lastIndexed;
	}

	@Column(name = "edmcompatibilityserialization")
	public String getEdmCompatibilitySerialization() {
		return this.edmCompatibilitySerialization;
	}

	public void setEdmCompatibilitySerialization(
			String edmCompatibilitySerialization) {
		this.edmCompatibilitySerialization = edmCompatibilitySerialization;
	}

	@Column(name = "vocabularyterm")
	public String getVocabularyTerm() {
		return this.vocabularyTerm;
	}

	public void setVocabularyTerm(String vocabularyterm) {
		this.vocabularyTerm = vocabularyterm;
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "europeanaDataObject",  cascade = {CascadeType.ALL})
	public Concept getConcept() {
		return this.concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	public Set<EuropeanaDataObject> getChildObjects() {
		return this.europeanaDataObjects;
	}

	public void setChildObjects(
			Set<EuropeanaDataObject> childObjects) {
		this.europeanaDataObjects = childObjects;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)	
    @JoinColumn(name = "europeanadataobjectid", nullable = false)
	public Set<WebResource> getWebResources() {
		return this.webResources;
	}

	public void setWebResources(Set<WebResource> webResources) {
		this.webResources = webResources;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "europeanaDataObjectByLinkedObjectId", cascade={CascadeType.REFRESH}) 		    
	public Set<EuropeanaDataObjectEuropeanaDataObject> getEuropeanaDataObjectForLinkedObject() {
		return this.europeanaDataObjectForLinkedObject;
	}

	public void setEuropeanaDataObjectForLinkedObject(
			Set<EuropeanaDataObjectEuropeanaDataObject> europeanaDataObjectForLinkedObject) {
		this.europeanaDataObjectForLinkedObject = europeanaDataObjectForLinkedObject;
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "europeanaDataObject", cascade = {CascadeType.ALL})
	public Place getPlace() {
		return this.place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "europeanaDataObject", cascade = { CascadeType.ALL})
	public Agent getAgent() {
		return this.agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "europeanaDataObject", cascade = { CascadeType.ALL})
	public CulturalHeritageObject getCulturalHeritageObject() {
		return this.culturalHeritageObject;
	}

	public void setCulturalHeritageObject(
			CulturalHeritageObject culturalHeritageObject) {
		this.culturalHeritageObject = culturalHeritageObject;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})	
    @JoinColumn(name = "linkingobjectid", nullable = false)
	public Set<EuropeanaDataObjectEuropeanaDataObject> getEuropeanaDataObjectForLinkingObject() {
		return this.europeanaDataObjectForLinkingObject;
	}

	public void setEuropeanaDataObjectForLinkingObject(
			Set<EuropeanaDataObjectEuropeanaDataObject> europeanaDataObjectForLinkingObject) {
		this.europeanaDataObjectForLinkingObject = europeanaDataObjectForLinkingObject;
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "europeanaDataObject", cascade = { CascadeType.ALL})
	public Timespan getTimespan() {
		return this.timespan;
	}

	public void setTimespan(Timespan timespan) {
		this.timespan = timespan;
	}
	
	/**
	 * Language non aware custom fields (key->value)
	 * @return
	 */
	@Column(name = "nonawarecustomfields")
	@Type(type = "NonAwareCustomFields")
	public Map<String, String> getNonAwareCustomFields() {
		return nonAwareCustomFields;
	}

	public void setNonAwareCustomFields(Map<String, String> nonAwareCustomFields) {
		this.nonAwareCustomFields = nonAwareCustomFields;
	}

	/**
	 * Language aware custom fields (key(language)->value(map => key->value))
	 * @return
	 */
	@Column(name = "awarecustomfields")
	@Type(type = "AwareCustomFields")
	public Map<String, Map<String, String>> getAwareCustomFields() {
		return awareCustomFields;
	}

	public void setAwareCustomFields(Map<String, Map<String, String>> awareCustomFields) {
		this.awareCustomFields = awareCustomFields;
	}	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deletion_date", length = 13)
	public Date getDeletionDate() {
		return this.deletionDate;
	}

	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}

}
