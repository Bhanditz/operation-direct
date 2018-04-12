package eu.europeana.direct.harvesting.models.helpers;

import java.util.Date;

public class DashboardRecord {

	private long id;	
	private String lastUpdated;
	private String weblink;
	private String edmPresentation;
	private String odJsonPresentation;
	
	public DashboardRecord(){}
	
	public DashboardRecord(long id, String lastUpdated, String weblink, String edmPresentation,
			String odJsonPresentation) {
		super();
		this.id = id;
		this.lastUpdated = lastUpdated;
		this.weblink = weblink;
		this.edmPresentation = edmPresentation;
		this.odJsonPresentation = odJsonPresentation;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getWeblink() {
		return weblink;
	}

	public void setWeblink(String weblink) {
		this.weblink = weblink;
	}

	public String getEdmPresentation() {
		return edmPresentation;
	}

	public void setEdmPresentation(String edmPresentation) {
		this.edmPresentation = edmPresentation;
	}

	public String getOdJsonPresentation() {
		return odJsonPresentation;
	}

	public void setOdJsonPresentation(String odJsonPresentation) {
		this.odJsonPresentation = odJsonPresentation;
	}	
}
