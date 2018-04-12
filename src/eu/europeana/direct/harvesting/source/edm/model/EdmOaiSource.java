package eu.europeana.direct.harvesting.source.edm.model;

import java.util.ArrayList;
import java.util.List;


public class EdmOaiSource {

	private String headerIdentifier;
	private String edmType;
	private OreAggregation oreAggregation = new OreAggregation();
	private List<EdmAgent> edmAgent = new ArrayList<EdmAgent>();
	private List<EdmTimespan> edmTimespan = new ArrayList<EdmTimespan>();
	private List<EdmConcept> edmConcept = new ArrayList<EdmConcept>();
	private List<EdmWebResource> edmWebResource = new ArrayList<EdmWebResource>();
	private List<EdmPlace> edmPlace = new ArrayList<EdmPlace>();
	private EdmProvidedCHO providedCHO = new EdmProvidedCHO();
	private List<EdmEvent> edmEvent = new ArrayList<EdmEvent>();

	public String getHeaderIdentifier() {
		return headerIdentifier;
	}
	
	public void setHeaderIdentifier(String headerIdentifier) {
		this.headerIdentifier = headerIdentifier;
	}

	public String getEdmType() {
		return edmType;
	}

	public void setEdmType(String edmType) {
		this.edmType = edmType;
	}

	public OreAggregation getOreAggregation() {
		return oreAggregation;
	}

	public void setOreAggregation(OreAggregation oreAggregation) {
		this.oreAggregation = oreAggregation;
	}

	public List<EdmAgent> getEdmAgent() {
		return edmAgent;
	}

	public void setEdmAgent(List<EdmAgent> edmAgent) {
		this.edmAgent = edmAgent;
	}

	public List<EdmTimespan> getEdmTimespan() {
		return edmTimespan;
	}

	public void setEdmTimespan(List<EdmTimespan> edmTimespan) {
		this.edmTimespan = edmTimespan;
	}

	public List<EdmConcept> getEdmConcept() {
		return edmConcept;
	}

	public void setEdmConcept(List<EdmConcept> edmConcept) {
		this.edmConcept = edmConcept;
	}

	public List<EdmWebResource> getEdmWebResource() {
		return edmWebResource;
	}

	public void setEdmWebResource(List<EdmWebResource> edmWebResource) {
		this.edmWebResource = edmWebResource;
	}

	public List<EdmPlace> getEdmPlace() {
		return edmPlace;
	}

	public void setEdmPlace(List<EdmPlace> edmPlace) {
		this.edmPlace = edmPlace;
	}

	public EdmProvidedCHO getProvidedCHO() {
		return providedCHO;
	}

	public void setProvidedCHO(EdmProvidedCHO providedCHO) {
		this.providedCHO = providedCHO;
	}

	public List<EdmEvent> getEdmEvent() {
		return edmEvent;
	}

	public void setEdmEvent(List<EdmEvent> edmEvent) {
		this.edmEvent = edmEvent;
	}			
	
	
}
