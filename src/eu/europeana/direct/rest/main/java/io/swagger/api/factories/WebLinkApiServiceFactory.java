package eu.europeana.direct.rest.main.java.io.swagger.api.factories;

import eu.europeana.direct.rest.gen.java.io.swagger.api.WebLinkApiService;
import eu.europeana.direct.rest.main.java.io.swagger.api.impl.WebLinkApiServiceImpl;

public class WebLinkApiServiceFactory{

	private final static WebLinkApiService service = new WebLinkApiServiceImpl();

	   public static WebLinkApiService getWebLinkApi()
	   {
	      return service;
	   }
}
