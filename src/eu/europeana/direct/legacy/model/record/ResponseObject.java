package eu.europeana.direct.legacy.model.record;

public class ResponseObject {

	// operation success
	private boolean success;
	private CulturalHeritageObject object;
	
	public ResponseObject() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ResponseObject(boolean success, CulturalHeritageObject object) {
		super();
		this.success = success;
		this.object = object;
	}

	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public CulturalHeritageObject getObject() {
		return object;
	}

	public void setObject(CulturalHeritageObject object) {
		this.object = object;
	}
}
