package eu.europeana.direct.harvesting.source.edm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@SuppressWarnings("rawtypes")
public class EdmTimespan {

	private Map<String,List<String>> prefLabel = new HashMap();
	private Map<String,List<String>> altLabel = new HashMap();
	private List<String> note = new ArrayList<String>();
	private List<String> hasPart = new ArrayList<String>();
	private List<String> isPartOf = new ArrayList<String>();
	private String begin;
	private String end;
	private List<String> isNextInSequence = new ArrayList<String>();
	private List<String> sameAs = new ArrayList<String>();
	private String about;
	
	
	
	public EdmTimespan() {
		super();
		// TODO Auto-generated constructor stub
	}
	public EdmTimespan(Map<String, List<String>> prefLabel) {
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
	public List<String> getNote() {
		return note;
	}
	public void setNote(List<String> note) {
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
