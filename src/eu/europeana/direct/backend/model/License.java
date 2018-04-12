package eu.europeana.direct.backend.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "license", schema = "public")
public class License implements java.io.Serializable {

	private int id;
	private ExternalVocabulary externalVocabulary;
	private String name;
	private String vocabularyTerm;
	private Set<WebResource> webResources = new HashSet<WebResource>(0);

	public License() {
	}

	public License(int id, ExternalVocabulary externalvocabulary) {
		this.id = id;
		this.externalVocabulary = externalvocabulary;
	}

	public License(int id, ExternalVocabulary externalvocabulary, String name,
			String vocabularyterm, Set<WebResource> webresources,
			Set<EuropeanaDataObject> europeanadataobjects) {
		this.id = id;
		this.externalVocabulary = externalvocabulary;
		this.name = name;
		this.vocabularyTerm = vocabularyterm;
		this.webResources = webresources;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "externalvocabularyid", nullable = false)
	public ExternalVocabulary getExternalVocabulary() {
		return this.externalVocabulary;
	}

	public void setExternalVocabulary(ExternalVocabulary externalvocabulary) {
		this.externalVocabulary = externalvocabulary;
	}

	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "vocabularyterm")
	public String getVocabularyTerm() {
		return this.vocabularyTerm;
	}

	public void setVocabularyTerm(String vocabularyTerm) {
		this.vocabularyTerm = vocabularyTerm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "license")
	public Set<WebResource> getWebResources() {
		return this.webResources;
	}

	public void setWebResources(Set<WebResource> webResources) {
		this.webResources = webResources;
	}

}
