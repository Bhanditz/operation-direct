package eu.europeana.direct.backend.model;

import java.io.Serializable;

public class DeletedEntity implements Serializable{

	private long id;
	private EuropeanaDataObjectRoleType entityType;
	
	public DeletedEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DeletedEntity(long id, EuropeanaDataObjectRoleType entityType) {
		super();
		this.id = id;
		this.entityType = entityType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public EuropeanaDataObjectRoleType getEntityType() {
		return entityType;
	}

	public void setEntityType(EuropeanaDataObjectRoleType entityType) {
		this.entityType = entityType;
	}
	
	
	
	
	
}
