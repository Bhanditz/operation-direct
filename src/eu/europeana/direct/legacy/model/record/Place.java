package eu.europeana.direct.legacy.model.record;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Place {

	private String about;
	private Map<String,List<String>> prefLabel = new HashMap<String,List<String>>();
	private Map<String,List<String>> altLabel = new HashMap<String,List<String>>();
	private Map<String,List<String>> hiddenLabel = new HashMap<String,List<String>>();
	private Map<String,List<String>> note = new HashMap<String, List<String>>();
	private Map<String, List<String>> isPartOf = new HashMap<String,List<String>>();
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double latitude;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double longitude;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double altitude;
	private Position position;
	private Map<String, List<String>> dcTermsHasPart = new HashMap<String,List<String>>();
	private String[] owlSameAs;
	
	public Place(){}
	
	/**
	 * Defines the resource being described
	 */
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	
	/**
	 * The preferred form of the name of the place.	 
	 */
	public Map<String, List<String>> getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(Map<String, List<String>> prefLabel) {
		this.prefLabel = prefLabel;
	}
	
	/**
	 * Alternative forms of the name of the place.	 
	 */
	public Map<String, List<String>> getAltLabel() {
		return altLabel;
	}
	public void setAltLabel(Map<String, List<String>> altLabel) {
		this.altLabel = altLabel;
	}
	
	/**
	 * A hidden lexical label, represented by means of the skos:hiddenLabel property, is a lexical label for a resource, where a KOS designer would like that character string to be accessible to applications performing text-based indexing and search operations, but would not like that label to be visible otherwise.	 
	 */
	public Map<String, List<String>> getHiddenLabel() {
		return hiddenLabel;
	}
	public void setHiddenLabel(Map<String, List<String>> hiddenLabel) {
		this.hiddenLabel = hiddenLabel;
	}
	
	/**
	 * Information relating to the place.
	 */
	public Map<String, List<String>> getNote() {
		return note;
	}
	public void setNote(Map<String, List<String>> note) {
		this.note = note;
	}
	
	/**
	 * Reference to a place that the described place is part of.	 
	 */
	public Map<String, List<String>> getIsPartOf() {
		return isPartOf;
	}
	public void setIsPartOf(Map<String, List<String>> isPartOf) {
		this.isPartOf = isPartOf;
	}
	
	/**
	 * The latitude of a spatial thing (decimal degrees).
	 */
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * The longitude of a spatial thing (decimal degrees).
	 */
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * The altitude of a spatial thing (decimal metres above the reference).	
	 */
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	/**
	 * A comma-separated representation of a latitude, longitude coordinate. 
	 */
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	
	/**
	 * Reference to a place that is part of the place being described.
	 */
	public Map<String, List<String>> getDcTermsHasPart() {
		return dcTermsHasPart;
	}
	public void setDcTermsHasPart(Map<String, List<String>> dcTermsHasPart) {
		this.dcTermsHasPart = dcTermsHasPart;
	}
	
	/**
	 * URI of a Place.	 
	 */
	public String[] getOwlSameAs() {
		return owlSameAs;
	}
	public void setOwlSameAs(String[] owlSameAs) {
		this.owlSameAs = owlSameAs;
	}
}

