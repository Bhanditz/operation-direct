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
 * Database representation of Operation Direct Agent entity  
 */
@SuppressWarnings("serial")
@Entity
@TypeDefs({		
		@TypeDef(name = "AgentLangAware", typeClass = JSONBType.class, parameters = { @Parameter(name = "type", value="MAP_LIST"), @Parameter(name ="value",value = "eu.europeana.direct.backend.model.AgentLangAware")}),
		@TypeDef(name = "AgentLangNonAware", typeClass = JSONBType.class, parameters = {@Parameter(name ="value",value = "eu.europeana.direct.backend.model.AgentLangNonAware")})		
		})		
@Table(name = "agent", schema = "public")
@NamedQueries({
@NamedQuery(name = "Agent.findById", query = "SELECT cho FROM Agent cho WHERE cho.id = :id") })
public class Agent implements java.io.Serializable {

	private long id;
	private EuropeanaDataObject europeanaDataObject;		
	private AgentLangNonAware languageNonAwareFields;
	private Map<String,List<AgentLangAware>> languageAwareFields = new HashMap<String,List<AgentLangAware>>();
	
	public Agent() {
	}

	public Agent(EuropeanaDataObject europeanadataobject) {
		this.europeanaDataObject = europeanadataobject;
	}	

	 public Agent(EuropeanaDataObject europeanaDataObject, AgentLangNonAware languageNonAwareFields,
			Map<String, List<AgentLangAware>> languageAwareFields) {
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

	public void setEuropeanaDataObject(EuropeanaDataObject europeanadataobject) {
		this.europeanaDataObject = europeanadataobject;
	}
		
	@Column(name = "languagenonawarefields")
	@Type(type = "AgentLangNonAware")
	public AgentLangNonAware getLanguageNonAwareFields() {
		return languageNonAwareFields;
	}

	public void setLanguageNonAwareFields(AgentLangNonAware languageNonAwareFields) {
		this.languageNonAwareFields = languageNonAwareFields;
	}

	
	@Column(name = "languageawarefields")
	@Type(type = "AgentLangAware")
	public Map<String,List<AgentLangAware>> getLanguageAwareFields() {
		return languageAwareFields;
	}

	public void setLanguageAwareFields(Map<String,List<AgentLangAware>> languageAwareFields) {
		this.languageAwareFields = languageAwareFields;
	}


}
