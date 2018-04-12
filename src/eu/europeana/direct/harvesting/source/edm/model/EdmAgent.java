package eu.europeana.direct.harvesting.source.edm.model;

import java.util.*;

/**
 * This class is for xml element <edm:Agent> in metadata edm format 
 */
@SuppressWarnings("rawtypes")
public class EdmAgent {
	private Map<String,List<String>> prefLabel = new HashMap();
	private Map<String,List<String>> altLabel = new HashMap();
	private List<String> note = new ArrayList<String>();
	private List<String> date = new ArrayList<String>();
	private List<String> identifier = new ArrayList<String>();
	private List<String> hasPart = new ArrayList<String>();
	private List<String> isPartOf = new ArrayList<String>();
	private String begin;
	private String end;
	private List<String> hasMet = new ArrayList<String>();
	private List<String> isRelatedTo = new ArrayList<String>();
	private List<String> name = new ArrayList<String>();
	private List<String> biographicalInformation = new ArrayList<String>();
	private String dateOfBirth;
	private String dateOfDeath;
	private String dateOfEstablishment;
	private String dateOfTermination;
	private String gender;
	private String placeOfBirth;
	private String placeOfDeath;
	private List<String> professionOrOccupation = new ArrayList<String>();
	private List<String> sameAs = new ArrayList<String>();
	private String about;
	private String role;
	
	
	
	
	public EdmAgent() {
		super();
	}
	public EdmAgent(Map<String, List<String>> prefLabel) {
		super();
		this.prefLabel = prefLabel;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Map<String, List<String>> getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(Map<String, List<String>> prefLabel) {
		this.prefLabel = prefLabel;
	}
	public Map<String, List<String>> getAltLabel() {
		return altLabel;
	}
	public void setAltLabel(Map<String, List<String>> altLabel) {
		this.altLabel = altLabel;
	}
	public List<String> getNote() {
		return note;
	}
	public void setNote(List<String> note) {
		this.note = note;
	}
	public List<String> getDate() {
		return date;
	}
	public void setDate(List<String> date) {
		this.date = date;
	}
	public List<String> getIdentifier() {
		return identifier;
	}
	public void setIdentifier(List<String> identifier) {
		this.identifier = identifier;
	}
	public List<String> getHasPart() {
		return hasPart;
	}
	public void setHasPart(List<String> hasPart) {
		this.hasPart = hasPart;
	}
	public List<String> getIsPartOf() {
		return isPartOf;
	}
	public void setIsPartOf(List<String> isPartOf) {
		this.isPartOf = isPartOf;
	}
	public String getBegin() {
		return begin;
	}
	public void setBegin(String begin) {
		this.begin = begin;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public List<String> getHasMet() {
		return hasMet;
	}
	public void setHasMet(List<String> hasMet) {
		this.hasMet = hasMet;
	}
	public List<String> getIsRelatedTo() {
		return isRelatedTo;
	}
	public void setIsRelatedTo(List<String> isRelatedTo) {
		this.isRelatedTo = isRelatedTo;
	}
	public List<String> getName() {
		return name;
	}
	public void setName(List<String> name) {
		this.name = name;
	}
	public List<String> getBiographicalInformation() {
		return biographicalInformation;
	}
	public void setBiographicalInformation(List<String> biographicalInformation) {
		this.biographicalInformation = biographicalInformation;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getDateOfDeath() {
		return dateOfDeath;
	}
	public void setDateOfDeath(String dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}
	public String getDateOfEstablishment() {
		return dateOfEstablishment;
	}
	public void setDateOfEstablishment(String dateOfEstablishment) {
		this.dateOfEstablishment = dateOfEstablishment;
	}
	public String getDateOfTermination() {
		return dateOfTermination;
	}
	public void setDateOfTermination(String dateOfTermination) {
		this.dateOfTermination = dateOfTermination;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPlaceOfBirth() {
		return placeOfBirth;
	}
	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}
	public String getPlaceOfDeath() {
		return placeOfDeath;
	}
	public void setPlaceOfDeath(String placeOfDeath) {
		this.placeOfDeath = placeOfDeath;
	}
	public List<String> getProfessionOrOccupation() {
		return professionOrOccupation;
	}
	public void setProfessionOrOccupation(List<String> professionOrOccupation) {
		this.professionOrOccupation = professionOrOccupation;
	}
	public List<String> getSameAs() {
		return sameAs;
	}
	public void setSameAs(List<String> sameAs) {
		this.sameAs = sameAs;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	};
	
	
	
}
