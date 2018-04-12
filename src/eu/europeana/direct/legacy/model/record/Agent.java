package eu.europeana.direct.legacy.model.record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Agent {

	private String about;
	private Map<String,List<String>> prefLabel = new HashMap<String,List<String>>();
	private Map<String,List<String>> altLabel = new HashMap<String,List<String>>();
	private Map<String,List<String>> hiddenLabel = new HashMap<String,List<String>>();
	private Map<String,List<String>> note = new HashMap<String, List<String>>();
	private Map<String,List<String>> begin = new HashMap<String, List<String>>();
	private Map<String,List<String>> end = new HashMap<String, List<String>>();
	private String[] edmWasPresentAt;
	private Map<String,List<String>> edmHasMet = new HashMap<String, List<String>>();
	private Map<String,List<String>> edmIsRelatedTo = new HashMap<String, List<String>>();
	private String[] owlSameAs;
	private Map<String,List<String>> foafName = new HashMap<String, List<String>>();
	private Map<String,List<String>> dcDate = new HashMap<String, List<String>>();
	private Map<String,List<String>> dcIdentifier = new HashMap<String, List<String>>();
	private Map<String,List<String>> rdaGr2DateOfBirth = new HashMap<String, List<String>>();
	private Map<String,List<String>> rdaGr2DateOfDeath = new HashMap<String, List<String>>();
	private Map<String,List<String>> rdaGr2DateOfEstablishment = new HashMap<String, List<String>>();
	private Map<String,List<String>> rdaGr2DateOfTermination = new HashMap<String, List<String>>();
	private Map<String,List<String>> rdaGr2Gender = new HashMap<String, List<String>>();
	private Map<String,List<String>> rdaGr2ProfessionOrOccupation = new HashMap<String, List<String>>();
	private Map<String,List<String>> rdaGr2BiographicalInformation = new HashMap<String, List<String>>();
	
	public Agent(){}
	
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	/**
	 * The label to be used for displaying the agent.
	 */
	public Map<String, List<String>> getPrefLabel() {
		return prefLabel;
	}	
	public void setPrefLabel(Map<String, List<String>> prefLabel) {
		this.prefLabel = prefLabel;
	}
	
	/**
	 * The alternative label to be used for displaying the agent	 
	 */
	public Map<String, List<String>> getAltLabel() {
		return altLabel;
	}
	public void setAltLabel(Map<String, List<String>> altLabel) {
		this.altLabel = altLabel;
	}
	public Map<String, List<String>> getHiddenLabel() {
		return hiddenLabel;
	}
	public void setHiddenLabel(Map<String, List<String>> hiddenLabel) {
		this.hiddenLabel = hiddenLabel;
	}
		
	public Map<String, List<String>> getNote() {
		return note;
	}
	public void setNote(Map<String, List<String>> note) {
		this.note = note;
	}
	
	/**
	 * Date of birth of the agent
	 */
	public Map<String, List<String>> getBegin() {
		return begin;
	}
	public void setBegin(Map<String, List<String>> begin) {
		this.begin = begin;
	}
	
	/**
	 * Date of death of the agent	 
	 */
	public Map<String, List<String>> getEnd() {
		return end;
	}
	public void setEnd(Map<String, List<String>> end) {
		this.end = end;
	}
	public String[] getEdmWasPresentAt() {
		return edmWasPresentAt;
	}
	public void setEdmWasPresentAt(String[] edmWasPresentAt) {
		this.edmWasPresentAt = edmWasPresentAt;
	}
	public Map<String, List<String>> getEdmHasMet() {
		return edmHasMet;
	}
	public void setEdmHasMet(Map<String, List<String>> edmHasMet) {
		this.edmHasMet = edmHasMet;
	}
	public Map<String, List<String>> getEdmIsRelatedTo() {
		return edmIsRelatedTo;
	}
	public void setEdmIsRelatedTo(Map<String, List<String>> edmIsRelatedTo) {
		this.edmIsRelatedTo = edmIsRelatedTo;
	}
	public String[] getOwlSameAs() {
		return owlSameAs;
	}
	public void setOwlSameAs(String[] owlSameAs) {
		this.owlSameAs = owlSameAs;
	}
	public Map<String, List<String>> getFoafName() {
		return foafName;
	}
	public void setFoafName(Map<String, List<String>> foafName) {
		this.foafName = foafName;
	}
	public Map<String, List<String>> getDcDate() {
		return dcDate;
	}
	public void setDcDate(Map<String, List<String>> dcDate) {
		this.dcDate = dcDate;
	}
	public Map<String, List<String>> getDcIdentifier() {
		return dcIdentifier;
	}
	public void setDcIdentifier(Map<String, List<String>> dcIdentifier) {
		this.dcIdentifier = dcIdentifier;
	}
	
	/**
	 * Date of birth of the agent	 
	 */
	public Map<String, List<String>> getRdaGr2DateOfBirth() {
		return rdaGr2DateOfBirth;
	}
	public void setRdaGr2DateOfBirth(Map<String, List<String>> rdaGr2DateOfBirth) {
		this.rdaGr2DateOfBirth = rdaGr2DateOfBirth;
	}
	
	/**
	 * Date of death of the agent	 
	 */
	public Map<String, List<String>> getRdaGr2DateOfDeath() {
		return rdaGr2DateOfDeath;
	}
	public void setRdaGr2DateOfDeath(Map<String, List<String>> rdaGr2DateOfDeath) {
		this.rdaGr2DateOfDeath = rdaGr2DateOfDeath;
	}	
	public Map<String, List<String>> getRdaGr2DateOfEstablishment() {
		return rdaGr2DateOfEstablishment;
	}
	public void setRdaGr2DateOfEstablishment(Map<String, List<String>> rdaGr2DateOfEstablishment) {
		this.rdaGr2DateOfEstablishment = rdaGr2DateOfEstablishment;
	}
	public Map<String, List<String>> getRdaGr2DateOfTermination() {
		return rdaGr2DateOfTermination;
	}
	public void setRdaGr2DateOfTermination(Map<String, List<String>> rdaGr2DateOfTermination) {
		this.rdaGr2DateOfTermination = rdaGr2DateOfTermination;
	}
	
	/**
	 * Gender of the agent
	 */
	public Map<String, List<String>> getRdaGr2Gender() {
		return rdaGr2Gender;
	}
	public void setRdaGr2Gender(Map<String, List<String>> rdaGr2Gender) {
		this.rdaGr2Gender = rdaGr2Gender;
	}
	
	/**
	 * The profession or occupation in which the agent works or has worked.	 
	 */
	public Map<String, List<String>> getRdaGr2ProfessionOrOccupation() {
		return rdaGr2ProfessionOrOccupation;
	}
	public void setRdaGr2ProfessionOrOccupation(Map<String, List<String>> rdaGr2ProfessionOrOccupation) {
		this.rdaGr2ProfessionOrOccupation = rdaGr2ProfessionOrOccupation;
	}
	public Map<String, List<String>> getRdaGr2BiographicalInformation() {
		return rdaGr2BiographicalInformation;
	}
	public void setRdaGr2BiographicalInformation(Map<String, List<String>> rdaGr2BiographicalInformation) {
		this.rdaGr2BiographicalInformation = rdaGr2BiographicalInformation;
	}
}
