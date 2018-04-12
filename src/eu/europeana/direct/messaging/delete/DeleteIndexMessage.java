package eu.europeana.direct.messaging.delete;

import java.io.Serializable;
import java.util.List;

import eu.europeana.direct.backend.model.DeletedEntity;
import eu.europeana.direct.legacy.index.LuceneDocumentType;

public class DeleteIndexMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long objectId;
	private List<DeletedEntity> deletedEntities;
	private LuceneDocumentType docType;
	private boolean relatedEntity;
	
	public DeleteIndexMessage(){}
	
	public DeleteIndexMessage(long choId, List<DeletedEntity> deletedEntities,boolean relatedEntity) {
		this.objectId = choId;
		this.deletedEntities = deletedEntities;
		this.relatedEntity = relatedEntity;
	}
	
	public DeleteIndexMessage(long choId, LuceneDocumentType docType,boolean relatedEntity) {
		super();
		this.objectId = choId;
		this.docType = docType;
		this.relatedEntity = relatedEntity;
	}	
	
	public LuceneDocumentType getDocType() {
		return docType;
	}

	public void setDocType(LuceneDocumentType docType) {
		this.docType = docType;
	}

	public boolean isRelatedEntity() {
		return relatedEntity;
	}

	public void setRelatedEntity(boolean relatedEntity) {
		this.relatedEntity = relatedEntity;
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long choId) {
		this.objectId = choId;
	}

	public List<DeletedEntity> getDeletedEntities() {
		return deletedEntities;
	}

	public void setDeletedEntities(List<DeletedEntity> deletedEntities) {
		this.deletedEntities = deletedEntities;
	}
	
}
