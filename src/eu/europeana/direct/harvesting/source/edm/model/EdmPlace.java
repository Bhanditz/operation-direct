package eu.europeana.direct.harvesting.source.edm.model;

import java.util.*;

@SuppressWarnings("rawtypes")
public class EdmPlace {
	
	private Float latitude;
	private Float longitude;
	private Float altitude;
	private Map<String,List<String>> prefLabel = new HashMap();
	private Map<String,List<String>> altLabel = new HashMap();
	private Map<String,List<String>> note = new HashMap();
	private List<String> hasPart = new ArrayList<String>();
	private List<String> isPartOf = new ArrayList<String>();
	private List<String> isNextInSequence = new ArrayList<String>();
	private List<String> sameAs = new ArrayList<String>();
	private String about;
	
	public Float getLatitude() {
		return latitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	public Float getLongitude() {
		return longitude;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	public Float getAltitude() {
		return altitude;
	}
	public void setAltitude(Float altitude) {
		this.altitude = altitude;
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
	public Map<String, List<String>> getNote() {
		return note;
	}
	public void setNote(Map<String, List<String>> note) {
		this.note = note;
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
	public List<String> getIsNextInSequence() {
		return isNextInSequence;
	}
	public void setIsNextInSequence(List<String> isNextInSequence) {
		this.isNextInSequence = isNextInSequence;
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
	}

	
	
}
