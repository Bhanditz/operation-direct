package eu.europeana.direct.backend.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import eu.europeana.direct.backend.userTypes.JSONBType;

/**
 * 
 * Database representation of Operation Direct Concept entity  
 *
 */
@SuppressWarnings("serial")
@Entity
@TypeDefs({
	@TypeDef(name = "ConceptLangAware", typeClass = JSONBType.class, parameters = { @Parameter(name = "type", value="MAP_LIST"), @Parameter(name ="value",value = "eu.europeana.direct.backend.model.ConceptLangAware")}),
	@TypeDef(name = "ConceptLangNonAware", typeClass = JSONBType.class, parameters = {@Parameter(name ="value",value = "eu.europeana.direct.backend.model.ConceptLangNonAware")})
	})
@Table(name = "concept", schema = "public")
@NamedQueries({
@NamedQuery(name = "Concept.findById", query = "SELECT cho FROM Concept cho WHERE cho.id = :id") })
public class Concept implements java.io.Serializable {

	private long id;
	private EuropeanaDataObject europeanaDataObject;	
	private ConceptLangNonAware languageNonAwareFields;
	private Map<String,List<ConceptLangAware>> languageAwareFields = new HashMap<String,List<ConceptLangAware>>();

	public Concept() {
	}

	public Concept(EuropeanaDataObject europeanaDataObject) {
		this.europeanaDataObject = europeanaDataObject;
	}	

	 public Concept(EuropeanaDataObject europeanaDataObject, ConceptLangNonAware languageNonAwareFields,
			Map<String, List<ConceptLangAware>> languageAwareFields) {
		super();
		this.europeanaDataObject = europeanaDataObject;
		this.languageNonAwareFields = languageNonAwareFields;
		this.languageAwareFields = languageAwareFields;
	}

	/**
	   * Represents the unique Operation Direct ID of the Concept.
	   **/
	@Id
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}	
	
	@Column(name = "languagenonawarefields", updatable = false, insertable = false)
	@Type(type = "ConceptLangNonAware")	
	public ConceptLangNonAware getLanguageNonAwareFields() {
		return languageNonAwareFields;
	}

	public void setLanguageNonAwareFields(ConceptLangNonAware languageNonAwareFields) {
		this.languageNonAwareFields = languageNonAwareFields;
	}
	
	@Column(name = "languageawarefields")
	@Type(type = "ConceptLangAware")
	public Map<String, List<ConceptLangAware>> getLanguageAwareFields() {
		return languageAwareFields;
	}

	public void setLanguageAwareFields(Map<String, List<ConceptLangAware>> languageAwareFields) {
		this.languageAwareFields = languageAwareFields;
	}

	@OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	@JoinColumn(name="id")
	@MapsId
	public EuropeanaDataObject getEuropeanaDataObject() {
		return this.europeanaDataObject;
	}

	public void setEuropeanaDataObject(EuropeanaDataObject europeanaDataObject) {
		this.europeanaDataObject = europeanaDataObject;
	}
}
