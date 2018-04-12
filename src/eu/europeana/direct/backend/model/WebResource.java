package eu.europeana.direct.backend.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import eu.europeana.direct.backend.userTypes.JSONBType;

/**
 * Database representation of Operation Direct WebLink entity  
 */
@SuppressWarnings("serial")
@Entity
@TypeDefs({		
	@TypeDef(name = "CustomFields", typeClass = JSONBType.class, parameters = { @Parameter(name = "type", value="MAP"), @Parameter(name ="value",value = "java.lang.String")})		
	})
@Table(name = "webresource", schema = "public")
public class WebResource implements java.io.Serializable {

	private long id;
	private EuropeanaDataObject europeanaDataObject;
	private License license;
	private String link;
	private String owner;
	private String rights;	
	private WebResourceType resourceType;
	private Map<String,String> customFields = new HashMap<String,String>();


	public WebResource() {
	}

	public WebResource(int id, EuropeanaDataObject europeanadataobject) {
		this.id = id;
		this.europeanaDataObject = europeanadataobject;
	}

	public WebResource(int id, EuropeanaDataObject europeanadataobject,
			License license) {
		this.id = id;
		this.europeanaDataObject = europeanadataobject;
		this.license = license;
	}

	 /**
	   * Represents the unique Operation Direct ID of the WebLink.
	   **/
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn (name="europeanadataobjectid", updatable = false, insertable = false, nullable = false)
	public EuropeanaDataObject getEuropeanaDataObject() {
		return this.europeanaDataObject;
	}

	public void setEuropeanaDataObject(EuropeanaDataObject europeanaDataObject) {
		this.europeanaDataObject = europeanaDataObject;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade= CascadeType.ALL)
	@JoinColumn(name = "licenseid")
	public License getLicense() {
		return this.license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	/**
	 * The location of the landing page of the resource
	 **/
	@Column(name="link")
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * The owner of the link
	 **/
	@Column(name="owner")
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * The license for the web resource from the controlled vocabulary from
	 * rightstatemnts.org
	 **/
	@Column(name="rights")
	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	/**
	 * The type of link provided
	 **/
	@Column(name="type")
	public WebResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(WebResourceType resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * Webresource aware custom fields (key->value)
	 * @return
	 */
	@Column(name = "customfields")
	@Type(type = "CustomFields")
	public Map<String, String> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, String> nonAwareCustomFields) {
		this.customFields = nonAwareCustomFields;
	}
	
	

}
