package eu.europeana.direct.backend.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import eu.europeana.direct.backend.userTypes.JSONBType;


/**
 * 
 * Database representation of Operation Direct cultural heritage object  
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "culturalheritageobject", schema = "public")
@TypeDefs({		
	@TypeDef(name = "CHOaware", typeClass = JSONBType.class, parameters = { @Parameter(name = "type", value="MAP"), @Parameter(name ="value",value = "eu.europeana.direct.backend.model.CulturalHeritageObjectLangAware")}),
	@TypeDef(name = "CHOnonAware", typeClass = JSONBType.class, parameters = {@Parameter(name ="value",value = "eu.europeana.direct.backend.model.CulturalHeritageObjectLangNonAware")})		
	})		
@NamedQueries({
@NamedQuery(name = "CulturalHeritageObject.findById", query = "SELECT cho FROM CulturalHeritageObject cho WHERE cho.id = :id"),
@NamedQuery(name = "CulturalHeritageObject.loadAll", query = "SELECT cho FROM CulturalHeritageObject cho"),
@NamedQuery(name = "CulturalHeritageObject.sortedFindAll", query = "SELECT cho FROM CulturalHeritageObject cho WHERE cho.id >= :id AND cho.id <= :idd")
})
public class CulturalHeritageObject implements java.io.Serializable {

	private long id;
	private EuropeanaDataObject europeanaDataObject;
	private ObjectType objectType;		
	private CulturalHeritageObjectLangNonAware languageNonAwareFields;
	private Map<String,CulturalHeritageObjectLangAware> languageAwareFields = new HashMap<String,CulturalHeritageObjectLangAware>();
	
	public CulturalHeritageObject() {
	}

	public CulturalHeritageObject(EuropeanaDataObject europeanaDataObject,
			ObjectType objectType) {
		this.europeanaDataObject = europeanaDataObject;
		this.objectType = objectType;
	}	
	
	public CulturalHeritageObject(EuropeanaDataObject europeanaDataObject, ObjectType objectType,
			CulturalHeritageObjectLangNonAware languageNonAwareFields,
			Map<String, CulturalHeritageObjectLangAware> languageAwareFields) {
		super();
		this.europeanaDataObject = europeanaDataObject;
		this.objectType = objectType;
		this.languageNonAwareFields = languageNonAwareFields;
		this.languageAwareFields = languageAwareFields;
	}

	/**
	 * Represents the unique Operation Direct ID of the cultural heritage object.
	 **/
	@Id
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
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

	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
	@JoinColumn(name = "objecttypeid", nullable = false)
	public ObjectType getObjectType() {
		return this.objectType;
	}

	public void setObjectType(ObjectType objectType) {
		this.objectType = objectType;
	}
		
	@Column(name = "languagenonawarefields")
	@Type(type = "CHOnonAware")
	public CulturalHeritageObjectLangNonAware getLanguageNonAwareFields() {
		return languageNonAwareFields;
	}

	public void setLanguageNonAwareFields(CulturalHeritageObjectLangNonAware languageNonAwareFields) {
		this.languageNonAwareFields = languageNonAwareFields;
	}
	
	@Column(name = "languageawarefields")
	@Type(type = "CHOaware")
	public Map<String, CulturalHeritageObjectLangAware> getLanguageAwareFields() {
		return languageAwareFields;
	}

	public void setLanguageAwareFields(Map<String, CulturalHeritageObjectLangAware> languageAwareFields) {
		this.languageAwareFields = languageAwareFields;
	}
}
