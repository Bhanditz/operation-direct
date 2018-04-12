package eu.europeana.direct.harvesting.source.edm.model;

import java.util.*;
@SuppressWarnings("rawtypes")
public class EdmProvidedCHO {

	private OaiDcSource oaiDc = new OaiDcSource();
	private Map<String,List<String>> alternativeTitle = new HashMap<>();
	private Map<String,String> issued = new HashMap();
	private Map<String,List<String>> extent = new HashMap();
	private Map<String,List<String>> medium = new HashMap();
	private Map<String,List<String>> provenance = new HashMap();
	private List<String> conformsTo = new ArrayList<String>();
	private List<String> hasFormat = new ArrayList<String>();
	private List<String> hasPart = new ArrayList<String>();
	private List<String> hasVersion = new ArrayList<String>();
	private List<String> isFormatOf = new ArrayList<String>();
	private List<String> isPartOf = new ArrayList<String>();
	private List<String> isReferencedBy = new ArrayList<String>();
	private List<String> isReplacedBy = new ArrayList<String>();
	private List<String> isRequiredBy = new ArrayList<String>();
	private List<String> isVersionOf = new ArrayList<String>();
	private List<String> references = new ArrayList<String>();
	private List<String> replaces = new ArrayList<String>();
	private List<String> requires = new ArrayList<String>();
	private List<String> spatial = new ArrayList<String>();
	private List<String> tableOfContents = new ArrayList<String>();
	private List<String> temporal = new ArrayList<String>();
	private String currentLocation;
	private List<String> hasMet = new ArrayList<String>();
	private List<String> hasType = new ArrayList<String>();
	private List<String> incorporates = new ArrayList<String>();
	private List<String> isDerivativeOf = new ArrayList<String>();
	private List<String> isNextInSequence = new ArrayList<String>();
	private List<String> isRelatedTo = new ArrayList<String>();
	private String isRepresentationOf;
	private List<String> isSimilarTo = new ArrayList<String>();
	private List<String> isSuccessorOf = new ArrayList<String>();
	private List<String> realizes = new ArrayList<String>();
	private List<String> sameAs = new ArrayList<String>();
	private String about;
	
	public OaiDcSource getOaiDc() {
		return oaiDc;
	}
	public void setOaiDc(OaiDcSource oaiDc) {
		this.oaiDc = oaiDc;
	}
	public Map<String, List<String>> getAlternativeTitle() {
		return alternativeTitle;
	}
	public void setAlternativeTitle(Map<String, List<String>> alternativeTitle) {
		this.alternativeTitle = alternativeTitle;
	}
	public List<String> getConformsTo() {
		return conformsTo;
	}
	public void setConformsTo(List<String> conformsTo) {
		this.conformsTo = conformsTo;
	}
	public Map<String, List<String>> getExtent() {
		return extent;
	}
	public void setExtent(Map<String, List<String>> extent) {
		this.extent = extent;
	}
	public List<String> getHasFormat() {
		return hasFormat;
	}
	public void setHasFormat(List<String> hasFormat) {
		this.hasFormat = hasFormat;
	}
	public List<String> getHasPart() {
		return hasPart;
	}
	public void setHasPart(List<String> hasPart) {
		this.hasPart = hasPart;
	}
	public List<String> getHasVersion() {
		return hasVersion;
	}
	public void setHasVersion(List<String> hasVersion) {
		this.hasVersion = hasVersion;
	}
	public List<String> getIsFormatOf() {
		return isFormatOf;
	}
	public void setIsFormatOf(List<String> isFormatOf) {
		this.isFormatOf = isFormatOf;
	}
	public List<String> getIsPartOf() {
		return isPartOf;
	}
	public void setIsPartOf(List<String> isPartOf) {
		this.isPartOf = isPartOf;
	}
	public List<String> getIsReferencedBy() {
		return isReferencedBy;
	}
	public void setIsReferencedBy(List<String> isReferencedBy) {
		this.isReferencedBy = isReferencedBy;
	}
	public List<String> getIsRequiredBy() {
		return isRequiredBy;
	}
	public void setIsRequiredBy(List<String> isRequiredBy) {
		this.isRequiredBy = isRequiredBy;
	}
	public Map<String, String> getIssued() {
		return issued;
	}
	public void setIssued(Map<String, String> issued) {
		this.issued = issued;
	}
	public List<String> getIsVersionOf() {
		return isVersionOf;
	}
	public void setIsVersionOf(List<String> isVersionOf) {
		this.isVersionOf = isVersionOf;
	}
	public Map<String, List<String>> getMedium() {
		return medium;
	}
	public void setMedium(Map<String, List<String>> medium) {
		this.medium = medium;
	}
	public Map<String, List<String>> getProvenance() {
		return provenance;
	}
	public void setProvenance(Map<String, List<String>> provenance) {
		this.provenance = provenance;
	}
	public List<String> getReferences() {
		return references;
	}
	public void setReferences(List<String> references) {
		this.references = references;
	}
	public List<String> getReplaces() {
		return replaces;
	}
	public void setReplaces(List<String> replaces) {
		this.replaces = replaces;
	}
	public List<String> getRequires() {
		return requires;
	}
	public void setRequires(List<String> requires) {
		this.requires = requires;
	}
	public List<String> getSpatial() {
		return spatial;
	}
	public void setSpatial(List<String> spatial) {
		this.spatial = spatial;
	}
	public List<String> getTableOfContents() {
		return tableOfContents;
	}
	public void setTableOfContents(List<String> tableOfContents) {
		this.tableOfContents = tableOfContents;
	}
	public List<String> getTemporal() {
		return temporal;
	}
	public void setTemporal(List<String> temporal) {
		this.temporal = temporal;
	}
	public String getCurrentLocation() {
		return currentLocation;
	}
	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}
	public List<String> getHasMet() {
		return hasMet;
	}
	public void setHasMet(List<String> hasMet) {
		this.hasMet = hasMet;
	}
	public List<String> getHasType() {
		return hasType;
	}
	public void setHasType(List<String> hasType) {
		this.hasType = hasType;
	}
	public List<String> getIncorporates() {
		return incorporates;
	}
	public void setIncorporates(List<String> incorporates) {
		this.incorporates = incorporates;
	}
	public List<String> getIsDerivativeOf() {
		return isDerivativeOf;
	}
	public void setIsDerivativeOf(List<String> isDerivativeOf) {
		this.isDerivativeOf = isDerivativeOf;
	}
	public List<String> getIsNextInSequence() {
		return isNextInSequence;
	}
	public void setIsNextInSequence(List<String> isNextInSequence) {
		this.isNextInSequence = isNextInSequence;
	}
	public List<String> getIsRelatedTo() {
		return isRelatedTo;
	}
	public void setIsRelatedTo(List<String> isRelatedTo) {
		this.isRelatedTo = isRelatedTo;
	}
	public String getIsRepresentationOf() {
		return isRepresentationOf;
	}
	public void setIsRepresentationOf(String isRepresentationOf) {
		this.isRepresentationOf = isRepresentationOf;
	}
	public List<String> getIsSimilarTo() {
		return isSimilarTo;
	}
	public void setIsSimilarTo(List<String> isSimilarTo) {
		this.isSimilarTo = isSimilarTo;
	}
	public List<String> getIsSuccessorOf() {
		return isSuccessorOf;
	}
	public void setIsSuccessorOf(List<String> isSuccessorOf) {
		this.isSuccessorOf = isSuccessorOf;
	}
	public List<String> getRealizes() {
		return realizes;
	}
	public void setRealizes(List<String> realizes) {
		this.realizes = realizes;
	}
	public List<String> getSameAs() {
		return sameAs;
	}
	public void setSameAs(List<String> sameAs) {
		this.sameAs = sameAs;
	}
	public List<String> getIsReplacedBy() {
		return isReplacedBy;
	}
	public void setIsReplacedBy(List<String> isReplacedBy) {
		this.isReplacedBy = isReplacedBy;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}		
}
