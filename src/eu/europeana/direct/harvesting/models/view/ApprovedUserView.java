package eu.europeana.direct.harvesting.models.view;

public class ApprovedUserView {

	private String name;
	private String lastname;
	private String providerName;
	private String username;
	private String mailAddress;		
	
	public ApprovedUserView(String name, String lastname, String providerName, String username, String mailAddress) {
		super();
		this.name = name;
		this.lastname = lastname;
		this.providerName = providerName;
		this.username = username;
		this.mailAddress = mailAddress;
	}
	public ApprovedUserView() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMailAddress() {
		return mailAddress;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	
	
}
