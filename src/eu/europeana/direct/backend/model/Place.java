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
 * Database representation of Operation Direct Place entity  
 *
 */
@SuppressWarnings("serial")
@Entity
@TypeDefs({		
	@TypeDef(name = "PlaceLangAware", typeClass = JSONBType.class, parameters = { @Parameter(name = "type", value="MAP_LIST"), @Parameter(name ="value",value = "eu.europeana.direct.backend.model.PlaceLangAware")}),
	@TypeDef(name = "PlaceLangNonAware", typeClass = JSONBType.class, parameters = {@Parameter(name ="value",value = "eu.europeana.direct.backend.model.PlaceLangNonAware")})		
	})	
@Table(name = "place", schema = "public")
@NamedQueries({
@NamedQuery(name = "Place.findById", query = "SELECT cho FROM Place cho WHERE cho.id = :id") })
public class Place implements java.io.Serializable {

	private long id;
	private EuropeanaDataObject europeanaDataObject;		
	private PlaceLangNonAware languageNonAwareFields;
	private Map<String,List<PlaceLangAware>> languageAwareFields = new HashMap<String,List<PlaceLangAware>>();
	
	public Place() {
	}

	public Place(EuropeanaDataObject europeanaDataObject) {
		this.europeanaDataObject = europeanaDataObject;
	}	

	public Place(EuropeanaDataObject europeanaDataObject, PlaceLangNonAware languageNonAwareFields,
			Map<String, List<PlaceLangAware>> languageAwareFields) {
		super();
		this.europeanaDataObject = europeanaDataObject;
		this.languageNonAwareFields = languageNonAwareFields;
		this.languageAwareFields = languageAwareFields;
	}

	/**
	   * Represents the unique Operation Direct ID of the Place.
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
	
	@Column(name = "languagenonawarefields")
	@Type(type = "PlaceLangNonAware")
	public PlaceLangNonAware getLanguageNonAwareFields() {
		return languageNonAwareFields;
	}

	public void setLanguageNonAwareFields(PlaceLangNonAware languageNonAwareFields) {
		this.languageNonAwareFields = languageNonAwareFields;
	}
	
	@Column(name = "languageawarefields")
	@Type(type = "PlaceLangAware")
	public Map<String, List<PlaceLangAware>> getLanguageAwareFields() {
		return languageAwareFields;
	}

	public void setLanguageAwareFields(Map<String, List<PlaceLangAware>> languageAwareFields) {
		this.languageAwareFields = languageAwareFields;
	}
}
