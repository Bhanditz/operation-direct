package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents the Operation Direct entity WebLink.
 **/
@JsonInclude(Include.NON_EMPTY)
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class WebLink implements ObjectInformation,Serializable {

	private BigDecimal id = null;
	private String owner = null;
	private String link = null;

	public enum TypeEnum {
		DIRECT_MEDIA("DIRECT_MEDIA"), LANDING_PAGE("LANDING_PAGE"), PREVIEW_SOURCE("PREVIEW_SOURCE"), OTHER("OTHER");

		private String value;

		TypeEnum(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return value;
		}
	}

	private TypeEnum type = null;
	private String rights = null;
	private List<KeyValuePair> customFields = new ArrayList<KeyValuePair>();

	/**
	 * Represents the unique Operation Direct ID of the Web Resource.
	 **/

	@JsonProperty("id")
	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	/**
	 * The owner of the link
	 **/

	@JsonProperty("owner")
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * The location of the landing page of the resource
	 **/

	@JsonProperty("link")
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * The type of link provided
	 **/

	@JsonProperty("type")
	public TypeEnum getType() {
		return type;
	}

	public void setType(TypeEnum type) {
		this.type = type;
	}

	/**
	 * The license for the web resource from the controlled vocabulary from
	 * rightstatemnts.org
	 **/

	@JsonProperty("rights")
	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	/**
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
		WebLink webLink = (WebLink) o;
		return Objects.equals(id, webLink.id) && Objects.equals(owner, webLink.owner)
				&& Objects.equals(link, webLink.link) && Objects.equals(type, webLink.type)
				&& Objects.equals(rights, webLink.rights) && Objects.equals(customFields, webLink.customFields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, owner, link, type, rights, customFields);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class WebLink {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
		sb.append("    link: ").append(toIndentedString(link)).append("\n");
		sb.append("    type: ").append(toIndentedString(type)).append("\n");
		sb.append("    rights: ").append(toIndentedString(rights)).append("\n");
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
