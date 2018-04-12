package eu.europeana.direct.rest.main.java.io.swagger.api.factories;

import eu.europeana.direct.rest.gen.java.io.swagger.api.UserApiService;
import eu.europeana.direct.rest.main.java.io.swagger.api.impl.UserApiServiceImpl;

public class UserApiServiceFactory {
	private final static UserApiService service = new UserApiServiceImpl();

	   public static UserApiService getUserApi()
	   {
	      return service;
	   }
}
