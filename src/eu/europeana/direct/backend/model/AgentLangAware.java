package eu.europeana.direct.backend.model;

public class AgentLangAware {

	private String[] preferredLabel;
	private String[] alternativeLabel;
	private String[] identifier;
	private String[] biographicalInformation;
	private String gender;
	private String[] professionOrOccupation;
	private String[] placeOfBirth;
	private String[] placeOfDeath;
	private String[] sameAs;
	
	
	
	public AgentLangAware(String[] preferredLabel, String[] alternativeLabel, String[] identifier,
			String[] biographicalInformation, String gender, String[] professionOrOccupation, String[] placeOfBirth,
			String[] placeOfDeath, String[] sameAs) {
		super();
		this.preferredLabel = preferredLabel;
		this.alternativeLabel = alternativeLabel;
		this.identifier = identifier;
		this.biographicalInformation = biographicalInformation;
		this.gender = gender;
		this.professionOrOccupation = professionOrOccupation;
		this.placeOfBirth = placeOfBirth;
		this.placeOfDeath = placeOfDeath;
		this.sameAs = sameAs;
	}
	public AgentLangAware() {
		// TODO Auto-generated constructor stub
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
	public String[] getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String[] identifier) {
		this.identifier = identifier;
	}
	public String[] getBiographicalInformation() {
		return biographicalInformation;
	}
	public void setBiographicalInformation(String[] biographicalInformation) {
		this.biographicalInformation = biographicalInformation;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String[] getProfessionOrOccupation() {
		return professionOrOccupation;
	}
	public void setProfessionOrOccupation(String[] professionOrOccupation) {
		this.professionOrOccupation = professionOrOccupation;
	}
	public String[] getPlaceOfBirth() {
		return placeOfBirth;
	}
	public void setPlaceOfBirth(String[] placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}
	public String[] getPlaceOfDeath() {
		return placeOfDeath;
	}
	public void setPlaceOfDeath(String[] placeOfDeath) {
		this.placeOfDeath = placeOfDeath;
	}
	public String[] getSameAs() {
		return sameAs;
	}
	public void setSameAs(String[] sameAs) {
		this.sameAs = sameAs;
	}
	
	
}
