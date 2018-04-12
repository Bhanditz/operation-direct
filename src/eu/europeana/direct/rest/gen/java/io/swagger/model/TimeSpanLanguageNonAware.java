package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Language non-aware (non-multilingual) object for TimeSpan
 **/
@JsonInclude(JsonInclude.Include.NON_EMPTY) //
public class TimeSpanLanguageNonAware implements ObjectInformation,Serializable {

	private String role = null;
	private String begin = null;
	private String end = null;
	private List<KeyValuePair> customFields = new ArrayList<KeyValuePair>();

	/**
	 * The role the contextual object has for the cultural heritage object in
	 * which it is included. This field is used only for linking contextual
	 * objects to their Cultural Heritage Object
	 **/

	@JsonProperty("role")
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	/**
	 **/

	@JsonProperty("begin")
	public String getBegin() {
		return begin;
	}

	public void setBegin(String begin) {
		this.begin = begin;
	}

	/**
	 **/

	@JsonProperty("end")
	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	/**
	 * List of other fields describing the object. Can contain, but is not
	 * limited to EDM fields, not documented explicitly.
	 **/
	@JsonProperty("customFields")
	public List<KeyValuePair> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<KeyValuePair> customFields) {
		this.customFields = customFields;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TimeSpanLanguageNonAware timeSpanLanguageNonAware = (TimeSpanLanguageNonAware) o;
		return Objects.equals(role, timeSpanLanguageNonAware.role)
				&& Objects.equals(begin, timeSpanLanguageNonAware.begin)
				&& Objects.equals(end, timeSpanLanguageNonAware.end)
				&& Objects.equals(customFields, timeSpanLanguageNonAware.customFields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(role, begin, end, customFields);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class TimeSpanLanguageNonAware {\n");

		sb.append("    role: ").append(toIndentedString(role)).append("\n");
		sb.append("    begin: ").append(toIndentedString(begin)).append("\n");
		sb.append("    end: ").append(toIndentedString(end)).append("\n");
		sb.append("    customFields: ").append(toIndentedString(customFields)).append("\n");
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
		List<String> dateFields = new ArrayList<String>();
		return dateFields;
	}

	@Override
	public List<String> getNumericFields() {
		List<String> numericFields = new ArrayList<String>();
		return numericFields;
	}

	@Override
	public List<String> getAllFields() {
		List<String> fields = new ArrayList<String>();
		for (Field field : this.getClass().getDeclaredFields()) {
			fields.add(field.getName());
		}
		return fields;
	}
}
