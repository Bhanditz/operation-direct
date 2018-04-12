package eu.europeana.direct.legacy.api;


import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import eu.europeana.direct.rest.gen.java.io.swagger.api.NotFoundException;


public abstract class LegacyApiService {

	
	public abstract Response getFullImage(UriInfo info)
			throws NotFoundException;

	public abstract Response getRecords(UriInfo info)
			throws NotFoundException;

	public abstract Response getRemovedRecords(UriInfo info)
			throws NotFoundException;
	
	public abstract Response getRecord(String id, UriInfo info)
			throws NotFoundException;
	
	public abstract Response reIndex(String fromId, String toId, String apiKey)
			throws NotFoundException;
	
	public abstract Response fullReindex(String apiKey)
			throws NotFoundException;
	
	public abstract Response merge(String apiKey)
			throws NotFoundException;
}
