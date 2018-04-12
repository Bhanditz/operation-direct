package eu.europeana.direct.rest.main.java.io.swagger.api.factories;

import eu.europeana.direct.rest.gen.java.io.swagger.api.CulturalHeritageObjectApiService;
import eu.europeana.direct.rest.main.java.io.swagger.api.impl.CulturalHeritageObjectApiServiceImpl;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class CulturalHeritageObjectApiServiceFactory {

   private final static CulturalHeritageObjectApiService service = new CulturalHeritageObjectApiServiceImpl();

   public static CulturalHeritageObjectApiService getCulturalHeritageObjectApi()
   {
      return service;
   }
}
