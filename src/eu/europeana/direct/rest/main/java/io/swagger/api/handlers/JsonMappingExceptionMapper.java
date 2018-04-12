package eu.europeana.direct.rest.main.java.io.swagger.api.handlers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonMappingException;

import eu.europeana.direct.rest.gen.java.io.swagger.api.ApiResponseMessage;

@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
    @Override
    public Response toResponse(JsonMappingException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Failed to parse JSON request. Please check your JSON request.")).build();
    }
}