package eu.europeana.direct.legacy.model.search;

public class TimespanItemLanguageAware {
	private String language;
	private String preferredLabel;
	private String[] alternativeLabel;

	
	/**
	 * Two letter ISO language code of the language in which the data are provided	
	 */
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	/**
	 * 
	 * The label to be used for displaying the timespan 
	 */
	public String getPreferredLabel() {
		return preferredLabel;
	}
	public void setPreferredLabel(String preferredLabel) {
		this.preferredLabel = preferredLabel;
	}
	/**
	 * 
	 * The alternative label to be used for displaying the timespan
	 */
	public String[] getAlternativeLabel() {
		return alternativeLabel;
	}
	public void setAlternativeLabel(String[] alternativeLabel) {
		this.alternativeLabel = alternativeLabel;
	}
}
