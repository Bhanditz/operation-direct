package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents the Operation Direct Cultural Heritage Object.
 **/
@JsonInclude(JsonInclude.Include.NON_EMPTY) //
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class CulturalHeritageObject implements ObjectInformation,Serializable{
  
  private BigDecimal id = null;
  private List<CulturalHeritageObjectLanguageAware> languageAwareFields = new ArrayList<CulturalHeritageObjectLanguageAware>();
  private CulturalHeritageObjectLanguageNonAware languageNonAwareFields = null;
  private List<Agent> agents = new ArrayList<Agent>();
  private List<Concept> concepts = new ArrayList<Concept>();
  private List<Place> spatial = new ArrayList<Place>();
  private List<TimeSpan> temporal = new ArrayList<TimeSpan>();
  private List<WebLink> webLinks = new ArrayList<WebLink>();
  
  
  
  @JsonProperty("webLinks")
  public List<WebLink> getWebLinks() {
    return webLinks;
  }
  public void setWebLinks(List<WebLink> webLinks) {
    this.webLinks = webLinks;
  }
  
  
  /**
   * Represents the unique Operation Direct ID of the Cultural Heritage Object.
   **/
  
  @JsonProperty("id")
  public BigDecimal getId() {
    return id;
  }
  public void setId(BigDecimal id) {
    this.id = id;
  }

  
  /**
   * Language aware (multilingual) fields of the object, such as title or description 
   **/
  
  @JsonProperty("languageAwareFields")
  public List<CulturalHeritageObjectLanguageAware> getLanguageAwareFields() {
    return languageAwareFields;
  }
  public void setLanguageAwareFields(List<CulturalHeritageObjectLanguageAware> languageAwareFields) {
    this.languageAwareFields = languageAwareFields;
  }

  
  /**
   * Language non-aware (non-multilingual) fields of the object, such as identifier or extent 
   **/
  
  @JsonProperty("languageNonAwareFields")
  public CulturalHeritageObjectLanguageNonAware getLanguageNonAwareFields() {
    return languageNonAwareFields;
  }
  public void setLanguageNonAwareFields(CulturalHeritageObjectLanguageNonAware languageNonAwareFields) {
    this.languageNonAwareFields = languageNonAwareFields;
  }

  
  /**
   * Contextual entities of type agent related to the cultural heritage object 
   **/
  
  @JsonProperty("agents")
  public List<Agent> getAgents() {
    return agents;
  }
  public void setAgents(List<Agent> agents) {
    this.agents = agents;
  }

  
  /**
   * Contextual entities of type concept related to the cultural heritage object 
   **/
  
  @JsonProperty("concepts")
  public List<Concept> getConcepts() {
    return concepts;
  }
  public void setConcepts(List<Concept> concepts) {
    this.concepts = concepts;
  }

  
  /**
   * Contextual entities of type place related to the cultural heritage object 
   **/
  
  @JsonProperty("spatial")
  public List<Place> getSpatial() {
    return spatial;
  }
  public void setSpatial(List<Place> spatial) {
    this.spatial = spatial;
  }

  
  /**
   * Contextual entities of type timespan related to the cultural heritage object
   **/
  
  @JsonProperty("temporal")
  public List<TimeSpan> getTemporal() {
    return temporal;
  }
  public void setTemporal(List<TimeSpan> temporal) {
    this.temporal = temporal;
  }

  

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CulturalHeritageObject culturalHeritageObject = (CulturalHeritageObject) o;
    return Objects.equals(id, culturalHeritageObject.id) &&
        Objects.equals(languageAwareFields, culturalHeritageObject.languageAwareFields) &&
        Objects.equals(languageNonAwareFields, culturalHeritageObject.languageNonAwareFields) &&
        Objects.equals(agents, culturalHeritageObject.agents) &&
        Objects.equals(concepts, culturalHeritageObject.concepts) &&
        Objects.equals(spatial, culturalHeritageObject.spatial) &&
        Objects.equals(temporal, culturalHeritageObject.temporal) &&
        Objects.equals(webLinks, culturalHeritageObject.webLinks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, languageAwareFields, languageNonAwareFields, agents, concepts, spatial, temporal, webLinks);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CulturalHeritageObject {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    languageAwareFields: ").append(toIndentedString(languageAwareFields)).append("\n");
    sb.append("    languageNonAwareFields: ").append(toIndentedString(languageNonAwareFields)).append("\n");
    sb.append("    agents: ").append(toIndentedString(agents)).append("\n");
    sb.append("    concepts: ").append(toIndentedString(concepts)).append("\n");
    sb.append("    spatial: ").append(toIndentedString(spatial)).append("\n");
    sb.append("    temporal: ").append(toIndentedString(temporal)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  	
  	
	@Override
	public List<String> getDateFields() {
		
		List<String> choLangAwareDateFields = new CulturalHeritageObjectLanguageAware().getDateFields();
		List<String> choLangNonAwareDateFields = new CulturalHeritageObjectLanguageNonAware().getDateFields();
		List<String> agentDateFields = new Agent().getDateFields();
		List<String> placeDateFields = new Place().getDateFields();
		List<String> conceptDateFields = new Concept().getDateFields();
		List<String> timespanDateFields = new TimeSpan().getDateFields();
		List<String> weblinkDateFields = new WebLink().getDateFields();
		
		List<String> choDateFields = new ArrayList<String>(choLangAwareDateFields);
		
		for(String field : choDateFields){
			if(choLangNonAwareDateFields.contains(field)){
				choLangNonAwareDateFields.remove(field);
			}
		}
		choDateFields.addAll(choLangNonAwareDateFields);
		
		for(String field : choDateFields){
			if(agentDateFields.contains(field)){
				agentDateFields.remove(field);
			}
		}
		choDateFields.addAll(agentDateFields);

		for(String field : choDateFields){
			if(placeDateFields.contains(field)){
				placeDateFields.remove(field);
			}
		}
		choDateFields.addAll(placeDateFields);

		for(String field : choDateFields){
			if(conceptDateFields.contains(field)){
				conceptDateFields.remove(field);
			}
		}
		choDateFields.addAll(conceptDateFields);

		for(String field : choDateFields){
			if(timespanDateFields.contains(field)){
				timespanDateFields.remove(field);
			}
		}
		choDateFields.addAll(timespanDateFields);

		for(String field : choDateFields){

			if(weblinkDateFields.contains(field)){
				weblinkDateFields.remove(field);				
			}
		}
		choDateFields.addAll(weblinkDateFields);
		choDateFields.add("modified");
		choDateFields.add("deleted");
		return choDateFields;
	}
  	
  	
	@Override
	public List<String> getAllFields() {
		List<String> choLangAwareAllFields = new CulturalHeritageObjectLanguageAware().getAllFields();
		List<String> choLangNonAwareAllFields = new CulturalHeritageObjectLanguageNonAware().getAllFields();		
		
		List<String> choFields = new ArrayList<String>(choLangAwareAllFields);
		
		for(String field : choFields){
			if(choLangNonAwareAllFields.contains(field)){
				choLangNonAwareAllFields.remove(field);
			}
		}
		choFields.addAll(choLangNonAwareAllFields);
		
		choFields = getRelatedEntitiesFields(choFields);
		
		return choFields;
	}
  	
	@Override
	public List<String> getNumericFields() {
		List<String> choLangAwareNumericFields = new CulturalHeritageObjectLanguageAware().getNumericFields();
		List<String> choLangNonAwareNumericFields = new CulturalHeritageObjectLanguageNonAware().getNumericFields();
		List<String> agentNumericFields = new Agent().getNumericFields();
		List<String> placeNumericFields = new Place().getNumericFields();
		List<String> conceptNumericFields = new Concept().getNumericFields();
		List<String> timespanNumericFields = new TimeSpan().getNumericFields();
		List<String> weblinkNumericFields = new WebLink().getNumericFields();
		
		List<String> choNumericFields = new ArrayList<String>(choLangAwareNumericFields);
		
		for(String field : choNumericFields){
			if(choLangNonAwareNumericFields.contains(field)){
				choLangNonAwareNumericFields.remove(field);
			}
		}
		choNumericFields.addAll(choLangNonAwareNumericFields);
		
		for(String field : choNumericFields){

			if(agentNumericFields.contains(field)){
				agentNumericFields.remove(field);
			}
		}
		choNumericFields.addAll(agentNumericFields);

		for(String field : choNumericFields){
			if(placeNumericFields.contains(field)){
				placeNumericFields.remove(field);
			}
		}
		choNumericFields.addAll(placeNumericFields);

		for(String field : choNumericFields){
			if(conceptNumericFields.contains(field)){
				conceptNumericFields.remove(field);
			}
		}
		choNumericFields.addAll(conceptNumericFields);

		for(String field : choNumericFields){
			if(timespanNumericFields.contains(field)){
				timespanNumericFields.remove(field);
			}
		}
		choNumericFields.addAll(timespanNumericFields);

		for(String field : choNumericFields){
			if(weblinkNumericFields.contains(field)){
				weblinkNumericFields.remove(field);
			}
		}		
		choNumericFields.addAll(weblinkNumericFields);
		
		return choNumericFields;
	}
	
	public List<String> getRelatedEntitiesFields(List<String> choFields) {
	
		if(choFields == null){
			choFields = new ArrayList<String>();
		}	
		
		List<String> agentAllFields = new Agent().getAllFields();
		List<String> placeAllFields = new Place().getAllFields();
		List<String> conceptAllFields = new Concept().getAllFields();
		List<String> timespanAllFields = new TimeSpan().getAllFields();
		List<String> weblinkAllFields = new WebLink().getAllFields();							
		
		for(String field : choFields){

			if(agentAllFields.contains(field)){
				agentAllFields.remove(field);
			}
		}
		choFields.addAll(agentAllFields);

		for(String field : choFields){
			if(placeAllFields.contains(field)){
				placeAllFields.remove(field);
			}
		}
		choFields.addAll(placeAllFields);

		for(String field : choFields){
			if(conceptAllFields.contains(field)){
				conceptAllFields.remove(field);
			}
		}
		choFields.addAll(conceptAllFields);

		for(String field : choFields){
			if(timespanAllFields.contains(field)){
				timespanAllFields.remove(field);
			}
		}
		choFields.addAll(timespanAllFields);

		for(String field : choFields){
			if(weblinkAllFields.contains(field)){
				weblinkAllFields.remove(field);
			}
		}		
		choFields.addAll(weblinkAllFields);
		return choFields;
	}
}

