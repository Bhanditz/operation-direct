package eu.europeana.direct.backend.model;

import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@SuppressWarnings("serial")
@Entity
@Table(name = "provider", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@NamedQueries({
@NamedQuery(name = "Provider.findByName", query = "SELECT p FROM Provider p WHERE p.name = :name")
})
public class Provider implements java.io.Serializable {

	private int id;
	private ExternalVocabulary externalVocabulary;
	private String name;
	private String vocabularyTerm;
	private Integer reputation;
	private Set<ProviderDataSchema> providerDataSchemas = new HashSet<ProviderDataSchema>(
			0);

	public Provider() {
	}

	public Provider(int id, ExternalVocabulary externalVocabulary, String name) {
		this.id = id;
		this.externalVocabulary = externalVocabulary;
		this.name = name;
	}
	
	

	public Provider(String name) {
		super();
		this.name = name;
	}

	public Provider(int id, ExternalVocabulary externalvocabulary, String name,
			String vocabularyterm, Integer reputation,
			Set<ProviderDataSchema> providerdataschemas) {
		this.id = id;
		this.externalVocabulary = externalvocabulary;
		this.name = name;
		this.vocabularyTerm = vocabularyterm;
		this.reputation = reputation;
		this.providerDataSchemas = providerdataschemas;
	}

	@Id
	@SequenceGenerator(name="pk_sequence",sequenceName="provider_id_seq", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="pk_sequence")
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "externalvocabularyid")
	public ExternalVocabulary getExternalVocabulary() {
		return this.externalVocabulary;
	}

	public void setExternalVocabulary(ExternalVocabulary externalVocabulary) {
		this.externalVocabulary = externalVocabulary;
	}

	@Column(name = "name", unique = true, nullable = false)
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

	@Column(name = "reputation")
	public Integer getReputation() {
		return this.reputation;
	}

	public void setReputation(Integer reputation) {
		this.reputation = reputation;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "provider")
	public Set<ProviderDataSchema> getProviderDataSchemas() {
		return this.providerDataSchemas;
	}

	public void setProviderDataSchemas(
			Set<ProviderDataSchema> providerDataSchemas) {
		this.providerDataSchemas = providerDataSchemas;
	}

}
