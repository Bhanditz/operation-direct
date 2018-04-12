package eu.europeana.direct.rest.gen.java.io.swagger.api;

import java.math.BigDecimal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public abstract class CulturalHeritageObjectApiService {
  
      public abstract Response culturalHeritageObjectGet(BigDecimal id, String language, String apiKey, SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response culturalHeritageObjectPost(CulturalHeritageObject culturalHeritageObject, String apiKey, SecurityContext securityContext)
      throws NotFoundException;
      
      public abstract Response culturalHeritageObjectDelete(BigDecimal id, Boolean choOnly, String apiKey, SecurityContext securityContext)
    	      throws NotFoundException;
      
      public abstract Response culturalHeritageObjectDeleteByDataOwner(String dataOwner, String apiKey, SecurityContext securityContext)
    	      throws NotFoundException;
}
