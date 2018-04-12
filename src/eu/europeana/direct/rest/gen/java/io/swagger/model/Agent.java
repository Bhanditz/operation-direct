package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents the Operation Direct entity Agent.
 **/
@JsonInclude(Include.NON_EMPTY)
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class Agent  implements ObjectInformation,Serializable {
  
  private BigDecimal id = null;
  private List<AgentLanguageAware> languageAwareFields = new ArrayList<AgentLanguageAware>();
  private AgentLanguageNonAware languageNonAwareFields = null;

  
  /**
   * Represents the unique Operation Direct ID of the Agent.
   **/
  
  @JsonProperty("id")
  public BigDecimal getId() {
    return id;
  }
  public void setId(BigDecimal id) {
    this.id = id;
  }

  
  /**
   * Language aware (multilingual) fields of the object 
   **/
  
  @JsonProperty("languageAwareFields")
  public List<AgentLanguageAware> getLanguageAwareFields() {
    return languageAwareFields;
  }
  public void setLanguageAwareFields(List<AgentLanguageAware> languageAwareFields) {
    this.languageAwareFields = languageAwareFields;
  }

  
  /**
   * Language non-aware (non-multilingual) fields of the object 
   **/
  
  @JsonProperty("languageNonAwareFields")
  public AgentLanguageNonAware getLanguageNonAwareFields() {
    return languageNonAwareFields;
  }
  public void setLanguageNonAwareFields(AgentLanguageNonAware languageNonAwareFields) {
    this.languageNonAwareFields = languageNonAwareFields;
  }

  

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Agent agent = (Agent) o;
    return Objects.equals(id, agent.id) &&
        Objects.equals(languageAwareFields, agent.languageAwareFields) &&
        Objects.equals(languageNonAwareFields, agent.languageNonAwareFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, languageAwareFields, languageNonAwareFields);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Agent {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    languageAwareFields: ").append(toIndentedString(languageAwareFields)).append("\n");
    sb.append("    languageNonAwareFields: ").append(toIndentedString(languageNonAwareFields)).append("\n");
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
		
		List<String> langNonAwareDateFields = new AgentLanguageNonAware().getDateFields();
		List<String> langAwareDateFields = new AgentLanguageAware().getDateFields();
		
		List<String> agentDateFields = new ArrayList<String>(langAwareDateFields);
		
		for(String field : agentDateFields){
			if(langNonAwareDateFields.contains(field)){
				langNonAwareDateFields.remove(field);
			}
		}
		agentDateFields.addAll(langNonAwareDateFields);
		
		return agentDateFields;	
	}
	
	@Override
	public List<String> getNumericFields() {
		List<String> langNonAwareNumericFields = new AgentLanguageNonAware().getNumericFields();
		List<String> langAwareNumericFields = new AgentLanguageAware().getNumericFields();

		List<String> agentNumericFields = new ArrayList<String>(langAwareNumericFields);
		
		for(String field : agentNumericFields){
			if(langNonAwareNumericFields.contains(field)){
				langNonAwareNumericFields.remove(field);
			}
		}
		agentNumericFields.addAll(langNonAwareNumericFields);
		
		return agentNumericFields;	
	}
	
	@Override
	public List<String> getAllFields() {		
		List<String> agentLangNonAwareFields = new AgentLanguageNonAware().getAllFields();
		List<String> agentLangAwareFields = new AgentLanguageAware().getAllFields();
		List<String> fields = new ArrayList<String>(agentLangNonAwareFields);
		
		for(String field : fields){						
			if(agentLangAwareFields.contains(field)){
				agentLangAwareFields.remove(field);
			}
		}
		
		// change to same name as in entities index field for agent document
		agentLangAwareFields.set(agentLangAwareFields.indexOf("preferredLabel"),"agentPreferredLabel");
		agentLangAwareFields.set(agentLangAwareFields.indexOf("alternativeLabel"),"agentAlternativeLabel");
		
		fields.addAll(agentLangAwareFields);
		return fields;		
	}

}

