package eu.europeana.direct.legacy.model.search;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Field {
	
	private String label;
	private int count;
	
	public Field() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Field(String label, int count) {
		super();
		this.label = label;
		this.count = count;
	}
	
	/**
	 * 
	 * Facet label
	 */
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * 
	 * Count of objects
	 */
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
