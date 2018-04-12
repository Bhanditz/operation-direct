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
 * Database representation of Operation Direct Timespan entity  
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "timespan", schema = "public")
@TypeDefs({		
	@TypeDef(name = "TimespanLangAware", typeClass = JSONBType.class, parameters = { @Parameter(name = "type", value="MAP_LIST"), @Parameter(name ="value",value = "eu.europeana.direct.backend.model.TimespanLangAware")}),
	@TypeDef(name = "TimespanLangNonAware", typeClass = JSONBType.class, parameters = {@Parameter(name ="value",value = "eu.europeana.direct.backend.model.TimespanLangNonAware")})		
	})		
@NamedQueries({
@NamedQuery(name = "Timespan.findById", query = "SELECT cho FROM Timespan cho WHERE cho.id = :id") })
public class Timespan implements java.io.Serializable {

	private long id;
	private EuropeanaDataObject europeanaDataObject;	
	private TimespanLangNonAware languageNonAwareFields;
	private Map<String,List<TimespanLangAware>> languageAwareFields = new HashMap<String,List<TimespanLangAware>>();
	
	public Timespan() {
	}

	public Timespan(EuropeanaDataObject europeanadataobject) {
		this.europeanaDataObject = europeanadataobject;
	}	

	 public Timespan(EuropeanaDataObject europeanaDataObject, TimespanLangNonAware languageNonAwareFields,
			Map<String, List<TimespanLangAware>> languageAwareFields) {
		super();
		this.europeanaDataObject = europeanaDataObject;
		this.languageNonAwareFields = languageNonAwareFields;
		this.languageAwareFields = languageAwareFields;
	}

	/**
	   * Represents the unique Operation Direct ID of the Agent.
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

	@Column(name = "languagenonawarefields",insertable = false, updatable = false)
	@Type(type = "TimespanLangNonAware")
	public TimespanLangNonAware getLanguageNonAwareFields() {
		return languageNonAwareFields;
	}

	public void setLanguageNonAwareFields(TimespanLangNonAware languageNonAwareFields) {
		this.languageNonAwareFields = languageNonAwareFields;
	}
	
	@Column(name = "languageawarefields")
	@Type(type = "TimespanLangAware")
	public Map<String, List<TimespanLangAware>> getLanguageAwareFields() {
		return languageAwareFields;
	}

	public void setLanguageAwareFields(Map<String, List<TimespanLangAware>> languageAwareFields) {
		this.languageAwareFields = languageAwareFields;
	}
	
}
