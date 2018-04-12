package eu.europeana.direct.legacy.model.search;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AgentItemLanguageAware {
	
	private String language;
	private String preferredLabel;
	private String[] alternativeLabel;
	private String[] identifier;
	private String biographicalInformation;
	private String gender;
	private String professionOrOccupation;
	private String placeOfDeath;
	private String placeOfBirth;
	private String[] sameAs;
	
	public String[] getSameAs() {
		return sameAs;
	}
	public void setSameAs(String[] sameAs) {
		this.sameAs = sameAs;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getPreferredLabel() {
		return preferredLabel;
	}
	public void setPreferredLabel(String preferredLabel) {
		this.preferredLabel = preferredLabel;
	}
	public String[] getAlternativeLabel() {
		return alternativeLabel;
	}
	public void setAlternativeLabel(String[] alternativeLabel) {
		this.alternativeLabel = alternativeLabel;
	}
	public String[] getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String[] identifier) {
		this.identifier = identifier;
	}
	public String getBiographicalInformation() {
		return biographicalInformation;
	}
	public void setBiographicalInformation(String biographicalInformation) {
		this.biographicalInformation = biographicalInformation;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getProfessionOrOccupation() {
		return professionOrOccupation;
	}
	public void setProfessionOrOccupation(String professionOrOccupation) {
		this.professionOrOccupation = professionOrOccupation;
	}
	public String getPlaceOfDeath() {
		return placeOfDeath;
	}
	public void setPlaceOfDeath(String placeOfDeath) {
		this.placeOfDeath = placeOfDeath;
	}
	public String getPlaceOfBirth() {
		return placeOfBirth;
	}
	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}				
}
