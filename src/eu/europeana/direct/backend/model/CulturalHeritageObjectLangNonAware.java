package eu.europeana.direct.backend.model;

public class CulturalHeritageObjectLangNonAware {
	
	private String mediaType;
	private String[] identifier;
	private String[] relation;
	private String dataowner;		
	private String language;
	private String objectLanguage;
	private String[] languageObject;
	
	public CulturalHeritageObjectLangNonAware() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CulturalHeritageObjectLangNonAware(String mediaType, String[] identifier, String[] relation,
			String dataowner, String[] languageObject) {
		super();
		this.mediaType = mediaType;
		this.identifier = identifier;
		this.relation = relation;
		this.dataowner = dataowner;
		this.languageObject = languageObject;
	}
	
	public CulturalHeritageObjectLangNonAware(String mediaType, String[] identifier, String[] relation,
			String dataowner, String objectLanguage) {
		super();
		this.mediaType = mediaType;
		this.identifier = identifier;
		this.relation = relation;
		this.dataowner = dataowner;
		this.objectLanguage = objectLanguage;
	}
	
	public CulturalHeritageObjectLangNonAware(String mediaType,String language, String[] identifier, String[] relation,
			String dataowner) {
		super();
		this.mediaType = mediaType;
		this.language = language;
		this.identifier = identifier;
		this.relation = relation;
		this.dataowner = dataowner;				
	}
	
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String[] getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String[] identifier) {
		this.identifier = identifier;
	}
	public String[] getRelation() {
		return relation;
	}
	public void setRelation(String[] relation) {
		this.relation = relation;
	}
	public String getDataowner() {
		return dataowner;
	}
	public void setDataowner(String dataowner) {
		this.dataowner = dataowner;
	}

	public String getObjectLanguage() {
		return objectLanguage;
	}

	public void setObjectLanguage(String objectLanguage) {
		this.objectLanguage = objectLanguage;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String[] getLanguageObject() {
		return languageObject;
	}

	public void setLanguageObject(String[] languageObject) {
		this.languageObject = languageObject;
	}
	
	
}
