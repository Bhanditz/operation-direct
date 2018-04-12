package eu.europeana.direct.backend.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@SuppressWarnings("serial")
@Entity
@Table(name = "country", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "isocode"))
public class Country implements java.io.Serializable {

	private int id;
	private String isoCode;
	
	public Country() {
	}

	public Country(int id, String isoCode) {
		this.id = id;
		this.isoCode = isoCode;
	}

	public Country( String isoCode) {		
		this.isoCode = isoCode;
	}
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "isocode", unique = true, nullable = false, length = 2)
	public String getIsoCode() {
		return this.isoCode;
	}

	public void setIsoCode(String isocode) {
		this.isoCode = isocode;
	}

}
