package eu.europeana.direct.backend.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@SuppressWarnings("serial")
@Entity
@Table(name = "objecttype", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class ObjectType implements java.io.Serializable {

	private int id;
	private ExternalVocabulary externalVocabulary;
	private String name;
	private String vocabularyTerm;

	public ObjectType() {
	}

	public ObjectType(int id, ExternalVocabulary externalvocabulary, String name) {
		this.id = id;
		this.externalVocabulary = externalvocabulary;
		this.name = name;
	}

	public ObjectType(int id, ExternalVocabulary externalvocabulary,
			String name, String vocabularyterm,
			Set<CulturalHeritageObject> culturalheritageobjects) {
		this.id = id;
		this.externalVocabulary = externalvocabulary;
		this.name = name;
		this.vocabularyTerm = vocabularyterm;
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

	public void setVocabularyTerm(String vocabularyterm) {
		this.vocabularyTerm = vocabularyterm;
	}

}
