package eu.europeana.direct.legacy.model.record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Proxy {

	private String about;
	private Map<String,List<String>> dcContributor = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcCoverage = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcCreator = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcDate = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcDescription = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcFormat = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcIdentifier = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcLanguage = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcPublisher = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcRelation = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcRights = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcSource = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcSubject = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcTitle = new HashMap<String,List<String>>();
	private Map<String,List<String>> dcType = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsAlternative = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsConformsTo = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsCreated = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsExtent = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsHasFormat = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsHasPart = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsHasVersion = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsIsFormatOf = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsIsPartOf = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsIsReferencedBy = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsIsReplacedBy = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsIsRequiredBy = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsIssued = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsIsVersionOf = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsMedium = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsProvenance = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsReferences = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsReplaces = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsRequires = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsSpatial = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsTOC = new HashMap<String,List<String>>();
	private Map<String,List<String>> dctermsTemporal = new HashMap<String,List<String>>();
	private String edmCurrentLocation;
	private Map<String,List<String>> edmHasMet = new HashMap<String,List<String>>();
	private Map<String,List<String>> edmHasType = new HashMap<String,List<String>>();
	private String[] edmIncorporates;
	private String[] edmIsDerivativeOf;
	private String edmIsNextInSequence;
	private Map<String,List<String>> edmIsRelatedTo = new HashMap<String,List<String>>();
	private String edmIsRepresentationOf;
	private String[] edmIsSimilarTo;
	private String[] edmIsSuccessorOf;
	private String[] edmRealizes;
	private String edmType;
	private Map<String,List<String>> edmRights = new HashMap<String,List<String>>();
	private String[] edmWasPresentAt;
	private boolean europeanaProxy;
	private String proxyFor;
	private String[] proxyIn;
	
	public Proxy(){}
	
	/**
	 * Defines the resource being described
	 */
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	
	/**
	 * An entity responsible for making contributions to the resource.
	 */
	public Map<String, List<String>> getDcContributor() {
		return dcContributor;
	}
	public void setDcContributor(Map<String, List<String>> dcContributor) {
		this.dcContributor = dcContributor;
	}
	/**
	 * The spatial or temporal topic of the resource, the spatial applicability of the resource, or the jurisdiction under which the resource is relevant.
	 */
	public Map<String, List<String>> getDcCoverage() {
		return dcCoverage;
	}
	public void setDcCoverage(Map<String, List<String>> dcCoverage) {
		this.dcCoverage = dcCoverage;
	}
	/**
	 * 	An entity primarily responsible for making the resource. This may be a person, organisation or a service.
	 */
	public Map<String, List<String>> getDcCreator() {
		return dcCreator;
	}
	public void setDcCreator(Map<String, List<String>> dcCreator) {
		this.dcCreator = dcCreator;
	}
	/**
	 * A point or period of time associated with an event in the lifecycle of the resource.
	 */
	public Map<String, List<String>> getDcDate() {
		return dcDate;
	}
	public void setDcDate(Map<String, List<String>> dcDate) {
		this.dcDate = dcDate;
	}
	/**
	 * A description of the resource.

	 */
	public Map<String, List<String>> getDcDescription() {
		return dcDescription;
	}
	public void setDcDescription(Map<String, List<String>> dcDescription) {
		this.dcDescription = dcDescription;
	}
	/**
	 * The file format, physical medium or dimensions of the resource.
	 */
	public Map<String, List<String>> getDcFormat() {
		return dcFormat;
	}
	public void setDcFormat(Map<String, List<String>> dcFormat) {
		this.dcFormat = dcFormat;
	}
	/**
	 * An unambiguous reference to the resource within a given context.

	 */
	public Map<String, List<String>> getDcIdentifier() {
		return dcIdentifier;
	}
	public void setDcIdentifier(Map<String, List<String>> dcIdentifier) {
		this.dcIdentifier = dcIdentifier;
	}
	/**
	 * A language of the resource

	 */
	public Map<String, List<String>> getDcLanguage() {
		return dcLanguage;
	}
	public void setDcLanguage(Map<String, List<String>> dcLanguage) {
		this.dcLanguage = dcLanguage;
	}
	/**
	 * An entity responsible for making the resource available. Examples of a publisher include a person, an organisation and a service.

	 */
	public Map<String, List<String>> getDcPublisher() {
		return dcPublisher;
	}
	public void setDcPublisher(Map<String, List<String>> dcPublisher) {
		this.dcPublisher = dcPublisher;
	}
	/**
	 * The name or identifier of a related resource, generally used for other related CHOs. The recommended best practice is to identify the resource using a formal identification scheme.

	 */
	public Map<String, List<String>> getDcRelation() {
		return dcRelation;
	}
	public void setDcRelation(Map<String, List<String>> dcRelation) {
		this.dcRelation = dcRelation;
	}
	/**
	 * Name of the rights holder of the CHO or more general rights information. (Note that the controlled edm:rights property relates to the digital objects and applies to the edm:WebResource and/or edm:Aggregation).

	 */
	public Map<String, List<String>> getDcRights() {
		return dcRights;
	}
	public void setDcRights(Map<String, List<String>> dcRights) {
		this.dcRights = dcRights;
	}
	/**
	 * A related resource from which the described resource is derived in whole or in part, i.e. the source of the original CHO.

	 */
	public Map<String, List<String>> getDcSource() {
		return dcSource;
	}
	public void setDcSource(Map<String, List<String>> dcSource) {
		this.dcSource = dcSource;
	}
	/**
	 * The topic of the resource.

	 */
	public Map<String, List<String>> getDcSubject() {
		return dcSubject;
	}
	public void setDcSubject(Map<String, List<String>> dcSubject) {
		this.dcSubject = dcSubject;
	}
	/**
	 * A name given to the resource. Typically, a Title will be a name by which the resource is formally known.

	 */
	public Map<String, List<String>> getDcTitle() {
		return dcTitle;
	}
	public void setDcTitle(Map<String, List<String>> dcTitle) {
		this.dcTitle = dcTitle;
	}
	/**
	 * The nature or genre of the resource. Type includes terms describing general categories, functions, genres, or aggregation levels for content.

	 */
	public Map<String, List<String>> getDcType() {
		return dcType;
	}
	public void setDcType(Map<String, List<String>> dcType) {
		this.dcType = dcType;
	}
	/**
	 * An alternative name for the resource. This can be any form of the title that is used as a substitute or an alternative to the formal title of the resource including abbreviations or translations of the title.

	 */
	public Map<String, List<String>> getDctermsAlternative() {
		return dctermsAlternative;
	}
	public void setDctermsAlternative(Map<String, List<String>> dctermsAlternative) {
		this.dctermsAlternative = dctermsAlternative;
	}
	/**
	 * An established standard to which the described resource conforms.

	 */
	public Map<String, List<String>> getDctermsConformsTo() {
		return dctermsConformsTo;
	}
	public void setDctermsConformsTo(Map<String, List<String>> dctermsConformsTo) {
		this.dctermsConformsTo = dctermsConformsTo;
	}
	/**
	 * Date of creation of the resource.

	 */
	public Map<String, List<String>> getDctermsCreated() {
		return dctermsCreated;
	}
	public void setDctermsCreated(Map<String, List<String>> dctermsCreated) {
		this.dctermsCreated = dctermsCreated;
	}
	/**
	 * The size or duration of the resource.

	 */
	public Map<String, List<String>> getDctermsExtent() {
		return dctermsExtent;
	}
	public void setDctermsExtent(Map<String, List<String>> dctermsExtent) {
		this.dctermsExtent = dctermsExtent;
	}
	/**
	 * A related resource that is substantially the same as the pre-existing described resource, but in another format.

	 */
	public Map<String, List<String>> getDctermsHasFormat() {
		return dctermsHasFormat;
	}
	public void setDctermsHasFormat(Map<String, List<String>> dctermsHasFormat) {
		this.dctermsHasFormat = dctermsHasFormat;
	}
	/**
	 * A related resource that is included either physically or logically in the described resource.

	 */
	public Map<String, List<String>> getDctermsHasPart() {
		return dctermsHasPart;
	}
	public void setDctermsHasPart(Map<String, List<String>> dctermsHasPart) {
		this.dctermsHasPart = dctermsHasPart;
	}
	/**
	 * A related resource that is a version, edition, or adaptation of the described resource. Changes in version imply substantive changes in content rather than differences in format.

	 */
	public Map<String, List<String>> getDctermsHasVersion() {
		return dctermsHasVersion;
	}
	public void setDctermsHasVersion(Map<String, List<String>> dctermsHasVersion) {
		this.dctermsHasVersion = dctermsHasVersion;
	}
	/**
	 * A related resource that is substantially the same as the described resource, but in another format.

	 */
	public Map<String, List<String>> getDctermsIsFormatOf() {
		return dctermsIsFormatOf;
	}
	public void setDctermsIsFormatOf(Map<String, List<String>> dctermsIsFormatOf) {
		this.dctermsIsFormatOf = dctermsIsFormatOf;
	}
	/**
	 * A related resource in which the described resource is physically or logically included.

	 */
	public Map<String, List<String>> getDctermsIsPartOf() {
		return dctermsIsPartOf;
	}
	public void setDctermsIsPartOf(Map<String, List<String>> dctermsIsPartOf) {
		this.dctermsIsPartOf = dctermsIsPartOf;
	}
	/**
	 * A related resource that references, cites, or otherwise points to the described resource.

	 */
	public Map<String, List<String>> getDctermsIsReferencedBy() {
		return dctermsIsReferencedBy;
	}
	public void setDctermsIsReferencedBy(Map<String, List<String>> dctermsIsReferencedBy) {
		this.dctermsIsReferencedBy = dctermsIsReferencedBy;
	}
	/**
	 * A related resource that supplants, displaces, or supersedes the described resource.

	 */
	public Map<String, List<String>> getDctermsIsReplacedBy() {
		return dctermsIsReplacedBy;
	}
	public void setDctermsIsReplacedBy(Map<String, List<String>> dctermsIsReplacedBy) {
		this.dctermsIsReplacedBy = dctermsIsReplacedBy;
	}
	/**
	 * A related resource that requires the described resource to support its function, delivery or coherence.

	 */
	public Map<String, List<String>> getDctermsIsRequiredBy() {
		return dctermsIsRequiredBy;
	}
	public void setDctermsIsRequiredBy(Map<String, List<String>> dctermsIsRequiredBy) {
		this.dctermsIsRequiredBy = dctermsIsRequiredBy;
	}
	/**
	 * 	Date of formal issuance (e.g., publication) of the resource.
	 */
	public Map<String, List<String>> getDctermsIssued() {
		return dctermsIssued;
	}
	public void setDctermsIssued(Map<String, List<String>> dctermsIssued) {
		this.dctermsIssued = dctermsIssued;
	}
	/**
	 * A related resource of which the described resource is a version, edition, or adaptation. Changes in version imply substantive changes in content rather than differences in format.
	 */
	public Map<String, List<String>> getDctermsIsVersionOf() {
		return dctermsIsVersionOf;
	}
	public void setDctermsIsVersionOf(Map<String, List<String>> dctermsIsVersionOf) {
		this.dctermsIsVersionOf = dctermsIsVersionOf;
	}
	/**
	 * The material or physical carrier of the resource.

	 */
	public Map<String, List<String>> getDctermsMedium() {
		return dctermsMedium;
	}	
	public void setDctermsMedium(Map<String, List<String>> dctermsMedium) {
		this.dctermsMedium = dctermsMedium;
	}

	/**
	 * A statement of any changes in ownership and custody of the resource since its creation that are significant for its authenticity, integrity and interpretation. This may include a description of any changes successive custodians made to the resource.

	 */
	public Map<String, List<String>> getDctermsProvenance() {
		return dctermsProvenance;
	}
	public void setDctermsProvenance(Map<String, List<String>> dctermsProvenance) {
		this.dctermsProvenance = dctermsProvenance;
	}

	/**
	 * A related resource that is referenced, cited, or otherwise pointed to by the described resource.

	 */
	public Map<String, List<String>> getDctermsReferences() {
		return dctermsReferences;
	}
	public void setDctermsReferences(Map<String, List<String>> dctermsReferences) {
		this.dctermsReferences = dctermsReferences;
	}
	/**
	 * A related resource that is supplanted, displaced, or superseded by the described resource.

	 */
	public Map<String, List<String>> getDctermsReplaces() {
		return dctermsReplaces;
	}	
	public void setDctermsReplaces(Map<String, List<String>> dctermsReplaces) {
		this.dctermsReplaces = dctermsReplaces;
	}
	/**
	 * A related resource that is required by the described resource to support its function, delivery or coherence.

	 */
	public Map<String, List<String>> getDctermsRequires() {
		return dctermsRequires;
	}	
	public void setDctermsRequires(Map<String, List<String>> dctermsRequires) {
		this.dctermsRequires = dctermsRequires;
	}
	/**
	 * Spatial characteristics of the resource.

	 */
	public Map<String, List<String>> getDctermsSpatial() {
		return dctermsSpatial;
	}
	public void setDctermsSpatial(Map<String, List<String>> dctermsSpatial) {
		this.dctermsSpatial = dctermsSpatial;
	}
	/**
	 * Table Of Contents. A list of subunits of the resource.

	 */
	public Map<String, List<String>> getDctermsTOC() {
		return dctermsTOC;
	}
	public void setDctermsTOC(Map<String, List<String>> dctermsTOC) {
		this.dctermsTOC = dctermsTOC;
	}
	/**
	 * Temporal characteristics of the resource.

	 */
	public Map<String, List<String>> getDctermsTemporal() {
		return dctermsTemporal;
	}
	public void setDctermsTemporal(Map<String, List<String>> dctermsTemporal) {
		this.dctermsTemporal = dctermsTemporal;
	}
	/**
	 * The geographic location and/or name of the repository, building, site, or other entity whose boundaries presently include the resource.

	 */
	public String getEdmCurrentLocation() {
		return edmCurrentLocation;
	}
	public void setEdmCurrentLocation(String edmCurrentLocation) {
		this.edmCurrentLocation = edmCurrentLocation;
	}
	/**
	 * edm:hasMet relates a resource with the objects or phenomena that have happened to or have happened together with the resource under consideration. We can abstractly think of history and the present as a series of “meetings” between people and other things in space-time. Therefore we name this relationship as the things the object “has met” in the course of its existence. These meetings are events in the proper sense, in which other people and things participate in any role.

	 */
	public Map<String, List<String>> getEdmHasMet() {
		return edmHasMet;
	}
	public void setEdmHasMet(Map<String, List<String>> edmHasMet) {
		this.edmHasMet = edmHasMet;
	}
	/**
	 * This property relates a resource with the concepts it belongs to in a suitable type system such as MIME or any thesaurus that captures categories of objects in a given field (e.g., the “Objects” facet in Getty’s Art and Architecture Thesaurus). It does not capture aboutness.

	 */
	public Map<String, List<String>> getEdmHasType() {
		return edmHasType;
	}
	public void setEdmHasType(Map<String, List<String>> edmHasType) {
		this.edmHasType = edmHasType;
	}
	/**
	 * This property captures the use of some resource to add value to another resource. Such resources may be nested, such as performing a theater play text, and then recording the performance, or creating an artful edition of a collection of poems or just aggregating various poems in an anthology.

	 */
	public String[] getEdmIncorporates() {
		return edmIncorporates;
	}
	public void setEdmIncorporates(String[] edmIncorporates) {
		this.edmIncorporates = edmIncorporates;
	}
	/**
	 * This property captures a narrower notion of derivation than edm:isSimilarTo, in the sense that it relates a resource to another one, obtained by reworking, reducing, expanding, parts or the whole contents of the former, and possibly adding some minor parts.

	 */
	public String[] getEdmIsDerivativeOf() {
		return edmIsDerivativeOf;
	}
	public void setEdmIsDerivativeOf(String[] edmIsDerivativeOf) {
		this.edmIsDerivativeOf = edmIsDerivativeOf;
	}
	/**
	 * edm:isNextInSequence relates two resources S and R that are ordered parts of the same resource A, and such that S comes immediately after R in the order created by their being parts of A.

	 */
	public String getEdmIsNextInSequence() {
		return edmIsNextInSequence;
	}
	public void setEdmIsNextInSequence(String edmIsNextInSequence) {
		this.edmIsNextInSequence = edmIsNextInSequence;
	}
	/**
	 * edm:isRelatedTo is the most general contextual property in EDM. Contextual properties have typically to do either with the things that have happened to or together with the object under consideration, or what the object refers to by its shape, form or features in a figural or encoded form.

	 */
	public Map<String, List<String>> getEdmIsRelatedTo() {
		return edmIsRelatedTo;
	}
	public void setEdmIsRelatedTo(Map<String, List<String>> edmIsRelatedTo) {
		this.edmIsRelatedTo = edmIsRelatedTo;
	}
	/**
	 * This property associates a resource to another resource that it represents.

	 */
	public String getEdmIsRepresentationOf() {
		return edmIsRepresentationOf;
	}
	public void setEdmIsRepresentationOf(String edmIsRepresentationOf) {
		this.edmIsRepresentationOf = edmIsRepresentationOf;
	}
	/**
	 * The most generic derivation property, covering also the case of questionable derivation. Is Similar To asserts that parts of the contents of one resource exhibit common features with respect to ideas, shapes, structures, colors, words, plots, topics with the contents of the related resource.

	 */
	public String[] getEdmIsSimilarTo() {
		return edmIsSimilarTo;
	}
	public void setEdmIsSimilarTo(String[] edmIsSimilarTo) {
		this.edmIsSimilarTo = edmIsSimilarTo;
	}
	/**
	 * This property captures the relation between the continuation of a resource and that resource. This applies to a story, a serial, a journal etc. No content of the successor resource is identical or has a similar form with that of the precursor. The similarity is only in the context, subjects and figures of a plot. Successors typically form part of a common whole – such as a trilogy, a journal, etc.

	 */
	public String[] getEdmIsSuccessorOf() {
		return edmIsSuccessorOf;
	}
	public void setEdmIsSuccessorOf(String[] edmIsSuccessorOf) {
		this.edmIsSuccessorOf = edmIsSuccessorOf;
	}
	/**
	 * This property describes a relation between a physical thing and the information resource that is contained in it, visible at it or otherwise carried by it, if applicable.

	 */
	public String[] getEdmRealizes() {
		return edmRealizes;
	}
	public void setEdmRealizes(String[] edmRealizes) {
		this.edmRealizes = edmRealizes;
	}
	/**
	 * The Europeana material type of the resource. All digital objects in Europeana have to be classified as one of the five Europeana material types using upper case letters: TEXT, IMAGE, SOUND, VIDEO or 3D.

	 */
	public String getEdmType() {
		return edmType;
	}
	public void setEdmType(String edmType) {
		this.edmType = edmType;
	}
	/**
	 * Information about copyright of the digital object as specified by isShownBy and isShownAt.

	 */
	public Map<String, List<String>> getEdmRights() {
		return edmRights;
	}
	public void setEdmRights(Map<String, List<String>> edmRights) {
		this.edmRights = edmRights;
	}
	/**
	 * This property associates the people, things or information resources with an event at which they were present.

	 */
	public String[] getEdmWasPresentAt() {
		return edmWasPresentAt;
	}
	public void setEdmWasPresentAt(String[] edmWasPresentAt) {
		this.edmWasPresentAt = edmWasPresentAt;
	}
	/**
	 *Flag whether the proxy is an Europeana proxy. See chapter 6.2 Europeana proxies and data enrichment in the EDM primer
 
	 */
	public boolean isEuropeanaProxy() {
		return europeanaProxy;
	}
	public void setEuropeanaProxy(boolean europeanaProxy) {
		this.europeanaProxy = europeanaProxy;
	}
	/**
	 * Proxy objects are used to represent a resource as it is aggregated in a particular aggregation. The ore:proxyFor relationship is used to link the proxy to the aggregated resource it is a proxy for. The subject of the relationship is a proxy object, and the object of the relationship is the aggregated resource.

	 */
	public String getProxyFor() {
		return proxyFor;
	}
	public void setProxyFor(String proxyFor) {
		this.proxyFor = proxyFor;
	}
	/**
	 * Proxy objects must also link to the aggregation in which the resource being proxied is aggregated. The ore:proxyIn relationship is used for this purpose. The subject of the relationship is a proxy object, and the object of the relationship is the aggregation.
	 */
	public String[] getProxyIn() {
		return proxyIn;
	}
	public void setProxyIn(String[] proxyIn) {
		this.proxyIn = proxyIn;
	}	
}
