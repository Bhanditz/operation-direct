package eu.europeana.direct.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "providerdataschema", schema = "public")
public class ProviderDataSchema implements java.io.Serializable {

	private int id;	
	private Provider provider;
	private String key;
	private String displayname;
	private String description;
	private Integer order;
	private String group;

	public ProviderDataSchema() {
	}

	public ProviderDataSchema(int id, Provider provider, String key) {
		this.id = id;
		this.provider = provider;
		this.key = key;
	}

	public ProviderDataSchema(int id, Provider provider,
			String key, String displayname, String description, Integer order,
			String group) {
		this.id = id;		
		this.provider = provider;
		this.key = key;
		this.displayname = displayname;
		this.description = description;
		this.order = order;
		this.group = group;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "providerid", nullable = false)
	public Provider getProvider() {
		return this.provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	@Column(name = "Key", nullable = false)
	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Column(name = "displayname")
	public String getDisplayname() {
		return this.displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "Order")
	public Integer getOrder() {
		return this.order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Column(name = "Group")
	public String getGroup() {
		return this.group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}
