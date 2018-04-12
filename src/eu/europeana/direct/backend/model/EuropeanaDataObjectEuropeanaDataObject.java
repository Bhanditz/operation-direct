package eu.europeana.direct.backend.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "europeanadataobject_europeanadataobject", schema = "public")
@NamedQueries({
@NamedQuery(name = "EuropeanaDataObjectEuropeanaDataObject.linkedObjects", query = "SELECT cho FROM EuropeanaDataObjectEuropeanaDataObject cho WHERE cho.europeanaDataObjectByLinkingObjectId.id = :choId")	,
@NamedQuery(name = "EuropeanaDataObjectEuropeanaDataObject.findByLinkedObjectId", query = "SELECT cho FROM EuropeanaDataObjectEuropeanaDataObject cho WHERE cho.europeanaDataObjectByLinkedObjectId.id = :objectId"),
@NamedQuery(name = "EuropeanaDataObjectEuropeanaDataObject.isRelated", query = "SELECT cho FROM EuropeanaDataObjectEuropeanaDataObject cho WHERE cho.europeanaDataObjectByLinkedObjectId.id = :linkedObject AND cho.europeanaDataObjectByLinkingObjectId.id <> :choId")
})
public class EuropeanaDataObjectEuropeanaDataObject implements
		java.io.Serializable {

	private int id;
	private EuropeanaDataObject europeanaDataObjectByLinkedObjectId;
	private EuropeanaDataObject europeanaDataObjectByLinkingObjectId;
	private String role;
	private EuropeanaDataObjectRoleType roleType;

	public EuropeanaDataObjectEuropeanaDataObject() {
	}

	public EuropeanaDataObjectEuropeanaDataObject(int id,
			EuropeanaDataObject europeanaDataObjectByLinkedObjectId,
			EuropeanaDataObject europeanaDataObjectByLinkingObjectId) {
		this.id = id;
		this.europeanaDataObjectByLinkedObjectId = europeanaDataObjectByLinkedObjectId;
		this.europeanaDataObjectByLinkingObjectId = europeanaDataObjectByLinkingObjectId;
	}

	public EuropeanaDataObjectEuropeanaDataObject(int id,
			EuropeanaDataObject europeanaDataObjectByLinkedObjectId,
			EuropeanaDataObject europeanaDataObjectByLinkingObjectId,
			String role) {
		this.id = id;
		this.europeanaDataObjectByLinkedObjectId = europeanaDataObjectByLinkedObjectId;
		this.europeanaDataObjectByLinkingObjectId = europeanaDataObjectByLinkingObjectId;
		this.role = role;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	//owning
	@ManyToOne(fetch = FetchType.EAGER, cascade={ CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "linkedobjectid", nullable = false)	
	public EuropeanaDataObject getEuropeanaDataObjectByLinkedObjectId() {
		return this.europeanaDataObjectByLinkedObjectId;
	}

	public void setEuropeanaDataObjectByLinkedObjectId(
			EuropeanaDataObject europeanaDataObjectByLinkedObjectId) {
		this.europeanaDataObjectByLinkedObjectId = europeanaDataObjectByLinkedObjectId;
	}
	
	//non owning
	@ManyToOne(fetch = FetchType.EAGER, cascade={ CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn (name="linkingobjectid", updatable = false, insertable = false, nullable = false)
	public EuropeanaDataObject getEuropeanaDataObjectByLinkingObjectId() {
		return this.europeanaDataObjectByLinkingObjectId;
	}

	public void setEuropeanaDataObjectByLinkingObjectId(
			EuropeanaDataObject europeanaDataObjectByLinkingObjectId) {
		this.europeanaDataObjectByLinkingObjectId = europeanaDataObjectByLinkingObjectId;
	}

	@Column(name = "role")
	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Column(name = "roleType")
	@Enumerated(EnumType.ORDINAL)
	public EuropeanaDataObjectRoleType getRoleType() {
		return roleType;
	}

	public void setRoleType(EuropeanaDataObjectRoleType roleType) {
		this.roleType = roleType;
	}

}
