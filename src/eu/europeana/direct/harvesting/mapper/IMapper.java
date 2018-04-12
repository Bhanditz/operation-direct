package eu.europeana.direct.harvesting.mapper;

import java.util.List;

import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

public interface IMapper<T> {

	/**
	 * Maps EdmOaiSource to CHO api model
	 * @param listSource 
	 * @return List of cultural heritage object
	 */
	CulturalHeritageObject mapFromEdm(T source);
	
	/**
	 * Number of successfully saved harvesting records	 
	 */
	int getSuccesses();
	
	/**
	 * Number of harvesting records that failed saving to database 	 
	 */
	int getErrors();
	
	/**
	 * Number of warnings appeared when trying to save harvesting record to database	 
	 */
	int getWarnings();
	
	
	void setInterrupt(boolean interrupt);

	/**
	 * List of error messages that apperead while saving harvesting records
	 */
	List<String> getErrorMessages();
	
	/**
	 * List of warning messages that apperead while saving harvesting records
	 */
	List<String> getWarningMessages();
}