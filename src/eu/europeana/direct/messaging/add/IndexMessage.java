package eu.europeana.direct.messaging.add;

import java.io.Serializable;

public class IndexMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long objectId;
	private boolean onlyRelated;
	private String documentType;
			
	public IndexMessage(){		
	}
	
	public IndexMessage(long objectId, boolean onlyRelated, String documentType) {		
		this.objectId = objectId;
		this.onlyRelated = onlyRelated;
		this.documentType = documentType;
	}
	
	public long getObjectId() {
		return objectId;
	}
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
	public boolean isOnlyRelated() {
		return onlyRelated;
	}
	public void setOnlyRelated(boolean onlyRelated) {
		this.onlyRelated = onlyRelated;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	
	
}
