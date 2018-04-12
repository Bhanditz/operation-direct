package eu.europeana.direct.harvesting.source.edm.model;

import java.util.*;

public class EdmEvent {
	
	private List<String> hasType = new ArrayList<String>();
	private List<String> happenedAt = new ArrayList<String>();
	private List<String> occuredAt = new ArrayList<String>();
	private List<String> P11_had_participant = new ArrayList<String>();
	private String about;
	
	public List<String> getHasType() {
		return hasType;
	}
	public void setHasType(List<String> hasType) {
		this.hasType = hasType;
	}
	public List<String> getHappenedAt() {
		return happenedAt;
	}
	public void setHappenedAt(List<String> happenedAt) {
		this.happenedAt = happenedAt;
	}
	public List<String> getOccuredAt() {
		return occuredAt;
	}
	public void setOccuredAt(List<String> occuredAt) {
		this.occuredAt = occuredAt;
	}
	public List<String> getP11_had_participant() {
		return P11_had_participant;
	}
	public void setP11_had_participant(List<String> p11_had_participant) {
		P11_had_participant = p11_had_participant;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	
}
