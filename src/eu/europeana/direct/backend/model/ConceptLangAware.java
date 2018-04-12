package eu.europeana.direct.backend.model;

public class ConceptLangAware {
	
	private String[] preferredLabel;
	private String[] alternativeLabel;
			
	public ConceptLangAware() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ConceptLangAware(String[] preferredLabel, String[] alternativeLabel) {
		super();
		this.preferredLabel = preferredLabel;
		this.alternativeLabel = alternativeLabel;
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
}
