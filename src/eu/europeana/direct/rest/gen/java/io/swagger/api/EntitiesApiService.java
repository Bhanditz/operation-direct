package eu.europeana.direct.rest.gen.java.io.swagger.api;

import java.math.BigDecimal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public abstract class EntitiesApiService {
  
      public abstract Response registriesAgentGet(BigDecimal id, String apiKey, SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response registriesAgentPost(Agent agent, String apiKey, SecurityContext securityContext)
      throws NotFoundException;
      
      public abstract Response registriesAgentDelete(BigDecimal id, String apiKey, SecurityContext securityContext)
    	      throws NotFoundException;     
      
      public abstract Response registriesConceptGet(BigDecimal id, String apiKey, SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response registriesConceptPost(Concept concept, String apiKey, SecurityContext securityContext)
      throws NotFoundException;
      
      public abstract Response registriesConceptDelete(BigDecimal id, String apiKey, SecurityContext securityContext)
    	      throws NotFoundException;
  
      public abstract Response registriesPlaceGet(BigDecimal id, String apiKey, SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response registriesPlacePost(Place place,String apiKey, SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response registriesPlaceDelete(BigDecimal id, String apiKey, SecurityContext securityContext)
    	      throws NotFoundException;
      
      public abstract Response registriesTimespanGet(BigDecimal id, String apiKey, SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response registriesTimespanPost(TimeSpan timeSpan, String apiKey, SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response registriesTimespanDelete(BigDecimal id, String apiKey, SecurityContext securityContext)
    	      throws NotFoundException;
}
