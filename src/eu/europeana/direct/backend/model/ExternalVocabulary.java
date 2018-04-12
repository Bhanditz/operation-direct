package eu.europeana.direct.backend.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@SuppressWarnings("serial")
@Entity
@Table(name = "externalvocabulary", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class ExternalVocabulary implements java.io.Serializable {

	private int id;
	private String name;
	private String baseUrl;	
	private Set<ObjectType> objectTypes = new HashSet<ObjectType>(0);
	private Set<Provider> providers = new HashSet<Provider>(0);
	private Set<EuropeanaDataObject> europeanaDataObjects = new HashSet<EuropeanaDataObject>(
			0);
	private Set<License> licenses = new HashSet<License>(0);

	public ExternalVocabulary() {
	}

	public ExternalVocabulary(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public ExternalVocabulary(int id, String name, String baseUrl,
			Set<ObjectType> objectTypes, Set<Provider> providers,
			Set<EuropeanaDataObject> europeanaDataObjects, Set<License> licenses) {
		this.id = id;
		this.name = name;
		this.baseUrl = baseUrl;
		this.objectTypes = objectTypes;
		this.providers = providers;
		this.europeanaDataObjects = europeanaDataObjects;
		this.licenses = licenses;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name", unique = true, nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "baseurl")
	public String getBaseUrl() {
		return this.baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "externalVocabulary")
	public Set<ObjectType> getObjectTypes() {
		return this.objectTypes;
	}

	public void setObjectTypes(Set<ObjectType> objectTypes) {
		this.objectTypes = objectTypes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "externalVocabulary")
	public Set<Provider> getProviders() {
		return this.providers;
	}

	public void setProviders(Set<Provider> providers) {
		this.providers = providers;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "externalVocabulary")
	public Set<EuropeanaDataObject> getEuropeanaDataObjects() {
		return this.europeanaDataObjects;
	}

	public void setEuropeanaDataObjects(
			Set<EuropeanaDataObject> europeanaDataObjects) {
		this.europeanaDataObjects = europeanaDataObjects;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "externalVocabulary")
	public Set<License> getLicenses() {
		return this.licenses;
	}

	public void setLicenses(Set<License> licenses) {
		this.licenses = licenses;
	}

}
