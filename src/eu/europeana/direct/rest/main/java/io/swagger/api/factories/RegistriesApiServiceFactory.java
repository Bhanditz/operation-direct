package eu.europeana.direct.rest.main.java.io.swagger.api.factories;

import eu.europeana.direct.rest.gen.java.io.swagger.api.EntitiesApiService;
import eu.europeana.direct.rest.main.java.io.swagger.api.impl.RegistriesApiServiceImpl;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class RegistriesApiServiceFactory {

   private final static EntitiesApiService service = new RegistriesApiServiceImpl();

   public static EntitiesApiService getRegistriesApi()
   {
      return service;
   }
}
