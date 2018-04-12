package eu.europeana.direct.legacy.model.record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Concept {

	private String about;
	private Map<String,List<String>> prefLabel = new HashMap<String,List<String>>();
	private Map<String,List<String>> altLabel = new HashMap<String,List<String>>();
	private Map<String,List<String>> hiddenLabel = new HashMap<String,List<String>>();
	private Map<String,List<String>> note = new HashMap<String, List<String>>();
	private String[] broader;
	private String[] narrower;
	private String[] related;
	private String[] broadMatch;
	private String[] narrowMatch;
	private String[] exactMatch;
	private String[] relatedMatch;
	private String[] closeMatch;	
	private Map<String,List<String>> notation = new HashMap<String, List<String>>();
	private String[] inScheme;
	
	public Concept(){}
	
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
	 * The label to be used for displaying the concept.	 
	 */
	public Map<String, List<String>> getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(Map<String, List<String>> prefLabel) {
		this.prefLabel = prefLabel;
	}
	
	/**
	 * The alternative label to be used for displaying the concept.
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
	 * Information relating to the concept.	 
	 */
	public Map<String, List<String>> getNote() {
		return note;
	}
	public void setNote(Map<String, List<String>> note) {
		this.note = note;
	}
	
	/**
	 * The identifier of a broader concept in the same thesaurus or controlled vocabulary	 
	 */
	public String[] getBroader() {
		return broader;
	}
	public void setBroader(String[] broader) {
		this.broader = broader;
	}
	
	/**
	 * The identifier of a narrower concept.
	 */
	public String[] getNarrower() {
		return narrower;
	}
	public void setNarrower(String[] narrower) {
		this.narrower = narrower;
	}
	
	/**
	 * The identifier of a related concept.	 
	 */
	public String[] getRelated() {
		return related;
	}
	public void setRelated(String[] related) {
		this.related = related;
	}
	
	/**
	 * The identifier of a broader matching concepts from other concept schemes.	 
	 */
	public String[] getBroadMatch() {
		return broadMatch;
	}
	public void setBroadMatch(String[] broadMatch) {
		this.broadMatch = broadMatch;
	}
	
	/**
	 * 	The identifier of a narrower matching concepts from other concept schemes.	 
	 */
	public String[] getNarrowMatch() {
		return narrowMatch;
	}
	public void setNarrowMatch(String[] narrowMatch) {
		this.narrowMatch = narrowMatch;
	}
	
	/**
	 * The identifier of exactly matching concepts from other concept schemes.	 
	 */
	public String[] getExactMatch() {
		return exactMatch;
	}
	public void setExactMatch(String[] exactMatch) {
		this.exactMatch = exactMatch;
	}
	
	/**
	 * The identifier of a related matching concepts from other concept schemes.	 
	 */
	public String[] getRelatedMatch() {
		return relatedMatch;
	}
	public void setRelatedMatch(String[] relatedMatch) {
		this.relatedMatch = relatedMatch;
	}
	
	/**
	 * The identifier of close matching concepts from other concept schemes.	 
	 */
	public String[] getCloseMatch() {
		return closeMatch;
	}
	public void setCloseMatch(String[] closeMatch) {
		this.closeMatch = closeMatch;
	}
	
	/**
	 * The notation in which the concept is represented. This may not be words in natural language for some knowledge organisation systems e.g. algebra.	 
	 */
	public Map<String, List<String>> getNotation() {
		return notation;
	}
	public void setNotation(Map<String, List<String>> notation) {
		this.notation = notation;
	}
	
	/**
	 * The URI of a concept scheme.	 
	 */
	public String[] getInScheme() {
		return inScheme;
	}
	public void setInScheme(String[] inScheme) {
		this.inScheme = inScheme;
	}	
}
