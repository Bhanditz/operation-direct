package eu.europeana.direct.legacy.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import eu.europeana.direct.harvesting.jobs.HarvestThread;
import eu.europeana.direct.harvesting.jobs.IndexChoWorker;
import eu.europeana.direct.legacy.api.factories.LegacyApiServiceFactory;
import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.api.NotFoundException;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

@Path("/v2")
public class LegacyApi {

	private final LegacyApiService delegate = LegacyApiServiceFactory.getLegacyApi();
	
	@GET
	@Path("/thumbnail-by-url.json")
	@Produces("image/png")
	public Response getFullImage(@Context UriInfo info) throws NotFoundException{
		return delegate.getFullImage(info);
	}	
	
	@GET
	@Path("/search.json")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecords(@Context UriInfo info) throws NotFoundException{	
		return delegate.getRecords(info);
	}
	
	@GET
	@Path("/removed")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRemovedRecords(@Context UriInfo info) throws NotFoundException{	
		return delegate.getRemovedRecords(info);
	}
	
	@GET
	@Path("/record/direct/{id}")	
	@Produces({"application/json"})
	public Response getRecord(@PathParam("id") String id, @Context UriInfo info) throws NotFoundException{		
		return delegate.getRecord(id, info);
	}	
	
	@GET
	@Path("/direct/generateIndex")
	public Response generateIndex( @QueryParam("apikey") String apiKey) throws NotFoundException{
		return delegate.fullReindex(apiKey);
	}		
	
	@GET
	@Path("/merge")
	public Response mergeIndex(@QueryParam("apikey") String apiKey) throws NotFoundException{
		return delegate.merge(apiKey);		
	}
	
	@GET
	@Path("/reindex/{id}/{id2}")
	public Response reindex(@PathParam("id") String id, @PathParam("id2") String id2, @QueryParam("apikey") String apiKey) throws NotFoundException{
		return delegate.reIndex(id, id2, apiKey);
	}	
	
}
