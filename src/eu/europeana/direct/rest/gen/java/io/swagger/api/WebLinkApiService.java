package eu.europeana.direct.rest.gen.java.io.swagger.api;

import java.math.BigDecimal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;

public abstract class WebLinkApiService {

	public abstract Response registriesWebLinkGet(BigDecimal id, String apiKey,SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response registriesWebLinkPost(WebLink agent, String apiKey, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response registriesWebLinkDelete(BigDecimal id, String apiKey, SecurityContext securityContext)
			throws NotFoundException;
}
