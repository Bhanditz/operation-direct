package eu.europeana.direct.harvesting.source.edm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is for xml element <edm:Concept> in metadata edm format 
 */
@SuppressWarnings("rawtypes")
public class EdmConcept {

	private Map<String,List<String>> prefLabel = new HashMap();
	private Map<String,List<String>> altLabel = new HashMap();
	private List<String> broader = new ArrayList<String>();
	private List<String> narrower = new ArrayList<String>();
	private List<String> related = new ArrayList<String>();
	private List<String> broadMatch = new ArrayList<String>();
	private List<String> relatedMatch = new ArrayList<String>();
	private List<String> narrowMatch = new ArrayList<String>();
	private List<String> exactMatch = new ArrayList<String>();
	private List<String> closeMatch = new ArrayList<String>();
	private List<String> note = new ArrayList<String>();
	private Map<String,String> notation = new HashMap();
	private List<String> inScheme = new ArrayList<String>();
	private String about;
	
	
	public EdmConcept(){}
	public EdmConcept(Map<String, List<String>> prefLabel) {
		super();
		this.prefLabel = prefLabel;
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
	public List<String> getBroader() {
		return broader;
	}
	public void setBroader(List<String> broader) {
		this.broader = broader;
	}
	public List<String> getNarrower() {
		return narrower;
	}
	public void setNarrower(List<String> narrower) {
		this.narrower = narrower;
	}
	public List<String> getRelated() {
		return related;
	}
	public void setRelated(List<String> related) {
		this.related = related;
	}
	public List<String> getBroadMatch() {
		return broadMatch;
	}
	public void setBroadMatch(List<String> broadMatch) {
		this.broadMatch = broadMatch;
	}
	public List<String> getRelatedMatch() {
		return relatedMatch;
	}
	public void setRelatedMatch(List<String> relatedMatch) {
		this.relatedMatch = relatedMatch;
	}
	public List<String> getNarrowMatch() {
		return narrowMatch;
	}
	public void setNarrowMatch(List<String> narrowMatch) {
		this.narrowMatch = narrowMatch;
	}
	public List<String> getExactMatch() {
		return exactMatch;
	}
	public void setExactMatch(List<String> exactMatch) {
		this.exactMatch = exactMatch;
	}
	public List<String> getCloseMatch() {
		return closeMatch;
	}
	public void setCloseMatch(List<String> closeMatch) {
		this.closeMatch = closeMatch;
	}
	public List<String> getNote() {
		return note;
	}
	public void setNote(List<String> note) {
		this.note = note;
	}
	public Map<String, String> getNotation() {
		return notation;
	}
	public void setNotation(Map<String, String> notation) {
		this.notation = notation;
	}
	public List<String> getInScheme() {
		return inScheme;
	}
	public void setInScheme(List<String> inScheme) {
		this.inScheme = inScheme;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
}
