package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * Represents the Operation Direct entity TimeSpan.
 **/
@JsonInclude(Include.NON_EMPTY)
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class TimeSpan implements ObjectInformation,Serializable{
  
  private BigDecimal id = null;
  private List<TimeSpanLangaugeAware> languageAwareFields = new ArrayList<TimeSpanLangaugeAware>();
  private TimeSpanLanguageNonAware languageNonAwareFields = null;

  
  /**
   * Represents the unique Operation Direct ID of the timespan.
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
  public List<TimeSpanLangaugeAware> getLanguageAwareFields() {
    return languageAwareFields;
  }
  public void setLanguageAwareFields(List<TimeSpanLangaugeAware> languageAwareFields) {
    this.languageAwareFields = languageAwareFields;
  }

  
  /**
   * Language non-aware (non-multilingual) fields of the object 
   **/
  @JsonProperty("languageNonAwareFields")
  public TimeSpanLanguageNonAware getLanguageNonAwareFields() {
    return languageNonAwareFields;
  }
  public void setLanguageNonAwareFields(TimeSpanLanguageNonAware languageNonAwareFields) {
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
    TimeSpan timeSpan = (TimeSpan) o;
    return Objects.equals(id, timeSpan.id) &&
        Objects.equals(languageAwareFields, timeSpan.languageAwareFields) &&
        Objects.equals(languageNonAwareFields, timeSpan.languageNonAwareFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, languageAwareFields, languageNonAwareFields);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TimeSpan {\n");
    
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
		List<String> langNonAwareDateFields = new PlaceLanguageNonAware().getDateFields();
		List<String> langAwareDateFields = new PlaceLanguageAware().getDateFields();
		
		List<String> timespanDateFields = new ArrayList<String>(langAwareDateFields);
		
		for(String field : timespanDateFields){
			if(langNonAwareDateFields.contains(field)){
				langNonAwareDateFields.remove(field);
			}
		}
		timespanDateFields.addAll(langNonAwareDateFields);
		
		return timespanDateFields;
	}
	@Override
	public List<String> getNumericFields() {
		List<String> langNonAwareNumericFields = new PlaceLanguageNonAware().getNumericFields();
		List<String> langAwareNumericFields = new PlaceLanguageAware().getNumericFields();
		
		List<String> timespanNumericFields = new ArrayList<String>(langAwareNumericFields);
		
		for(String field : timespanNumericFields){
			if(langNonAwareNumericFields.contains(field)){
				langNonAwareNumericFields.remove(field);
			}
		}
		timespanNumericFields.addAll(langNonAwareNumericFields);
		
		return timespanNumericFields;
	}
	@Override
	public List<String> getAllFields() {
		List<String> timespanLangNonAwareFields = new AgentLanguageNonAware().getAllFields();
		List<String> timespanLangAwareFields = new AgentLanguageAware().getAllFields();
		List<String> fields = new ArrayList<String>(timespanLangNonAwareFields);
		
		for(String field : fields){
			if(timespanLangAwareFields.contains(field)){
				timespanLangAwareFields.remove(field);
			}
		}
		
		fields.addAll(timespanLangAwareFields);
		return fields;	
	}
}

