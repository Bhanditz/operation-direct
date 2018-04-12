package eu.europeana.direct.harvesting.source.edm.model;

import java.util.ArrayList;
import java.util.List;

// <ore:Aggregation> element
public class OreAggregation {

	private String isShownBy;
	private String isShownAt;
	private String object;
	//<edm:provider> or <edm:dataProvider>
	private String owner;
	private String dataOwner;
	private String aggregatedCHO;
	private List<String> hasView = new ArrayList<String>();
	private List<String> intermediateProvider = new ArrayList<String>();
	private String ugc;
	private String edmRights;
	private String about;
	
	
	public String getDataOwner() {
		return dataOwner;
	}
	public void setDataOwner(String dataOwner) {
		this.dataOwner = dataOwner;
	}
	public String getIsShownBy() {
		return isShownBy;
	}
	public void setIsShownBy(String isShownBy) {
		this.isShownBy = isShownBy;
	}
	public String getIsShownAt() {
		return isShownAt;
	}
	public void setIsShownAt(String isShownAt) {
		this.isShownAt = isShownAt;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getAggregatedCHO() {
		return aggregatedCHO;
	}
	public void setAggregatedCHO(String aggregatedCHO) {
		this.aggregatedCHO = aggregatedCHO;
	}			
	public List<String> getHasView() {
		return hasView;
	}
	public void setHasView(List<String> hasView) {
		this.hasView = hasView;
	}
	public List<String> getIntermediateProvider() {
		return intermediateProvider;
	}
	public void setIntermediateProvider(List<String> intermediateProvider) {
		this.intermediateProvider = intermediateProvider;
	}
	public String getUgc() {
		return ugc;
	}
	public void setUgc(String ugc) {
		this.ugc = ugc;
	}
	public String getEdmRights() {
		return edmRights;
	}
	public void setEdmRights(String edmRights) {
		this.edmRights = edmRights;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}			
	
}
