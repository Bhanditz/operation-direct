package eu.europeana.direct.backend.model;

public class PlaceLangAware {
	
	private String[]  preferredLabel;
	private String[]  alternativeLabel;
	private String[]  note;
			
	public PlaceLangAware() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PlaceLangAware(String[] preferredLabel, String[] alternativeLabel, String[] note) {
		super();
		this.preferredLabel = preferredLabel;
		this.alternativeLabel = alternativeLabel;
		this.note = note;
	}
	
	public String[] getPreferredLabel() {
		return preferredLabel;
	}
	public void setPreferredLabel(String[] preferredLabel) {
		this.preferredLabel = preferredLabel;
	}
	public String[] getAlternativeLabel() {
		return alternativeLabel;
	}
	public void setAlternativeLabel(String[] alternativeLabel) {
		this.alternativeLabel = alternativeLabel;
	}
	public String[] getNote() {
		return note;
	}
	public void setNote(String[] note) {
		this.note = note;
	}
	
	
}
