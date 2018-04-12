package eu.europeana.direct.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@SuppressWarnings("serial")
@Entity
@Table(name = "directuser", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {"username","password","mail","salt"}))
@NamedQueries({
@NamedQuery(name = "DirectUser.findByAPIKey", query = "SELECT u FROM DirectUser u WHERE u.apiKey = :apikey"),
@NamedQuery(name = "DirectUser.findByUsername", query = "SELECT u FROM DirectUser u WHERE u.username = :username"),
@NamedQuery(name = "DirectUser.findByMail", query = "SELECT u FROM DirectUser u WHERE u.mailAddress = :mail"),
@NamedQuery(name = "DirectUser.findUnapproved", query = "SELECT u FROM DirectUser u WHERE u.approved = FALSE"),
@NamedQuery(name = "DirectUser.findByApikey", query = "SELECT u FROM DirectUser u WHERE u.apiKey = :apikey"),

})
public class DirectUser {

	
	private int id;
	private String name;
	private String lastname;
	private String username;
	private String password;
	private String salt;
	private String mailAddress;
	private String apiKey;
	private Provider institution;
	private boolean approved;
	private boolean admin;
	
	public DirectUser(){}

	@Id
	@SequenceGenerator(name="pk_sequence2",sequenceName="directuser_id_seq", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="pk_sequence2")
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "lastname", nullable = false)
	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Column(name = "username", nullable = false)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	@Column(name = "password", nullable = false)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "mail", nullable = false)
	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "providerid")
	public Provider getInstitution() {
		return institution;
	}

	public void setInstitution(Provider institution) {
		this.institution = institution;
	}
	
	@Column(name = "salt", nullable = false)
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	@Column(name = "apikey", nullable = false)
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Column(name = "approved")
	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	@Column(name = "admin")
	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}		
	
	
	
	
}
