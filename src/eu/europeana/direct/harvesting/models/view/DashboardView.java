package eu.europeana.direct.harvesting.models.view;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.direct.harvesting.models.helpers.DashboardRecord;

public class DashboardView {

	private List<DashboardRecord> records = new ArrayList<DashboardRecord>();
	private int numOfPages;
	
	public List<DashboardRecord> getRecords() {
		return records;
	}
	public void setRecords(List<DashboardRecord> records) {
		this.records = records;
	}
	public int getNumOfPages() {
		return numOfPages;
	}
	public void setNumOfPages(int numOfPages) {
		this.numOfPages = numOfPages;
	}
	
	
}
