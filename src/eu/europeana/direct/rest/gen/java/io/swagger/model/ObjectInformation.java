package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface ObjectInformation {
	
	/**
	 * Method returns list of field names which are of type date 
	 */
	@JsonIgnore
	public List<String> getDateFields();
	
	/**
	 * 
	 * Method returns list of field names which are numeric type (longitude,latitude,..) 
	 */
	@JsonIgnore
	public List<String> getNumericFields();

	/**
	 * 
	 * Method returns list of class field names 
	 */
	@JsonIgnore
	public List<String> getAllFields();
	
}
