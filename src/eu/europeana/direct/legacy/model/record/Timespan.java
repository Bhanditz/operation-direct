package eu.europeana.direct.legacy.model.record;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Timespan {

	private String about;
	private Map<String,List<String>> prefLabel = new HashMap<String,List<String>>();
	private Map<String,List<String>> altLabel = new HashMap<String,List<String>>();
	private Map<String,List<String>> hiddenLabel = new HashMap<String,List<String>>();
	private Map<String,List<String>> note = new HashMap<String,List<String>>();
	private Map<String,List<String>> begin = new HashMap<String,List<String>>();
	private Map<String,List<String>> end = new HashMap<String,List<String>>();
	private Map<String,List<String>> isPartOf = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcTermsHasPart = new HashMap<String,List<String>>();
	private String[] owlSameAs;

	public Timespan(){}
	
	/**
	 * Defines the resource being described.
	 */
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	/**
	 * The preferred form of the name of the timespan or period.

	 */
	public Map<String, List<String>> getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(Map<String, List<String>> prefLabel) {
		this.prefLabel = prefLabel;
	}

	/**
	 * Alternative forms of the name of the timespan or period.
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
	 * Information relating to the timespan or period.
	 */
	public Map<String, List<String>> getNote() {
		return note;
	}
	public void setNote(Map<String, List<String>> note) {
		this.note = note;
	}
	/**
	 * The date the timespan started.
	 */
	public Map<String, List<String>> getBegin() {
		return begin;
	}
	public void setBegin(Map<String, List<String>> begin) {
		this.begin = begin;
	}
	/**
	 * The date the timespan finished.
	 */
	public Map<String, List<String>> getEnd() {
		return end;
	}
	public void setEnd(Map<String, List<String>> end) {
		this.end = end;
	}
	/**
	 * Reference to a timespan of which the described timespan is a part.
	 */
	public Map<String, List<String>> getIsPartOf() {
		return isPartOf;
	}
	public void setIsPartOf(Map<String, List<String>> isPartOf) {
		this.isPartOf = isPartOf;
	}
	/**
	 * Reference to a timespan which is part of the described timespan.

	 */
	public Map<String, List<String>> getDcTermsHasPart() {
		return dcTermsHasPart;
	}
	public void setDcTermsHasPart(Map<String, List<String>> dcTermsHasPart) {
		this.dcTermsHasPart = dcTermsHasPart;
	}
	/**
	 * The URI of a timespan.

	 */
	public String[] getOwlSameAs() {
		return owlSameAs;
	}
	public void setOwlSameAs(String[] owlSameAs) {
		this.owlSameAs = owlSameAs;
	}
	
}
