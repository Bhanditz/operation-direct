package eu.europeana.direct.legacy.model.search;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Facet {

	private String name;
	private Field[] fields;
	
	public Facet() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Facet(String name, Field[] fields) {
		super();
		this.name = name;
		this.fields = fields;
	}
	
	/**
	 * Name of the facet	 
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Facet field	
	 */
	public Field[] getFields() {
		return fields;
	}
	public void setFields(Field[] fields) {
		this.fields = fields;
	}
}

