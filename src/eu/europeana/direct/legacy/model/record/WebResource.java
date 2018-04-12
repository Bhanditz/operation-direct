package eu.europeana.direct.legacy.model.record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WebResource {

	private String about;
	private Map<String, List<String>> webResourceDcRights = new HashMap<String, List<String>>();
	private Map<String, List<String>> webResourceEdmRights = new HashMap<String, List<String>>();
	private Map<String, List<String>> dcDescription = new HashMap<String, List<String>>();
	private Map<String, List<String>> dcFormat = new HashMap<String, List<String>>();
	private Map<String, List<String>> dcSource = new HashMap<String, List<String>>();
	private Map<String, List<String>> dctermsExtent = new HashMap<String, List<String>>();
	private Map<String, List<String>> dctermsIssued = new HashMap<String, List<String>>();
	private Map<String, List<String>> dctermsConformsTo = new HashMap<String, List<String>>();
	private Map<String, List<String>> dctermsCreated = new HashMap<String, List<String>>();
	private Map<String, List<String>> dctermsIsFormatOf = new HashMap<String, List<String>>();
	private Map<String, List<String>> dctermsHasPart = new HashMap<String, List<String>>();
	private String isNextInSequence;
	private String edmCodecName;
	private String ebucoreHasMimeType;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private int ebucoreFileByteSize;
	private String duration;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private int ebucoreWidth;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private int ebucoreHeight;
	private String edmSpatialResolution;
	private String ebucoreSampleSize;
	private String ebucoreSampleRate;
	private String ebucoreBitRate;
	private String ebucoreFrameRate;
	private String edmHasColorSpace;
	private String ebucoreOrientation;
	private String ebucoreAudioChannelNumber;
	private String dctermsIsReferencedBy;
	private String svcsHasService;
	
	/**
	 * URI of the webResource
	 * 
	 */
	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	/**
	 * Free text information about the rights in this object.
	 * 
	 */
	public Map<String, List<String>> getWebResourceDcRights() {
		return webResourceDcRights;
	}

	public void setWebResourceDcRights(Map<String, List<String>> webResourceDcRights) {
		this.webResourceDcRights = webResourceDcRights;
	}

	/**
	 * The value in this element will indicate the usage and access rights that
	 * apply to this digital representation. The rights statement specified at
	 * the level of the web resource will ‘override’ the statement specified at
	 * the level of the aggregation. The value in this element is a URI taken
	 * from the set of those defined for use in Europeana. A list of these can
	 * be found at http://pro.europeana.eu/web/available-rights-statements.
	 * 
	 */
	public Map<String, List<String>> getWebResourceEdmRights() {
		return webResourceEdmRights;
	}

	public void setWebResourceEdmRights(Map<String, List<String>> webResourceEdmRights) {
		this.webResourceEdmRights = webResourceEdmRights;
	}

	/**
	 * An account or description of this digital representation.
	 * 
	 */
	public Map<String, List<String>> getDcDescription() {
		return dcDescription;
	}

	public void setDcDescription(Map<String, List<String>> dcDescription) {
		this.dcDescription = dcDescription;
	}

	/**
	 * The file format, physical medium or dimensions of the resource.
	 * 
	 */
	public Map<String, List<String>> getDcFormat() {
		return dcFormat;
	}

	public void setDcFormat(Map<String, List<String>> dcFormat) {
		this.dcFormat = dcFormat;
	}

	/**
	 * A related resource from which the web resource is derived in whole or in
	 * part.
	 * 
	 */
	public Map<String, List<String>> getDcSource() {
		return dcSource;
	}

	public void setDcSource(Map<String, List<String>> dcSource) {
		this.dcSource = dcSource;
	}

	/**
	 * The size or duration of the digital resource.
	 * 
	 */
	public Map<String, List<String>> getDctermsExtent() {
		return dctermsExtent;
	}

	public void setDctermsExtent(Map<String, List<String>> dctermsExtent) {
		this.dctermsExtent = dctermsExtent;
	}

	/**
	 * Date of formal issuance (e.g., publication) of the resource.
	 * 
	 */
	public Map<String, List<String>> getDctermsIssued() {
		return dctermsIssued;
	}

	public void setDctermsIssued(Map<String, List<String>> dctermsIssued) {
		this.dctermsIssued = dctermsIssued;
	}

	/**
	 * An established standard to which the web resource conforms.
	 * 
	 */
	public Map<String, List<String>> getDctermsConformsTo() {
		return dctermsConformsTo;
	}

	public void setDctermsConformsTo(Map<String, List<String>> dctermsConformsTo) {
		this.dctermsConformsTo = dctermsConformsTo;
	}

	/**
	 * Date of creation of the web resource.
	 * 
	 */
	public Map<String, List<String>> getDctermsCreated() {
		return dctermsCreated;
	}

	public void setDctermsCreated(Map<String, List<String>> dctermsCreated) {
		this.dctermsCreated = dctermsCreated;
	}

	/**
	 * A related resource that is substantially the same as the described
	 * resource, but in another format.
	 * 
	 */
	public Map<String, List<String>> getDctermsIsFormatOf() {
		return dctermsIsFormatOf;
	}

	public void setDctermsIsFormatOf(Map<String, List<String>> dctermsIsFormatOf) {
		this.dctermsIsFormatOf = dctermsIsFormatOf;
	}

	/**
	 * A related resource that is included either physically or logically in the
	 * web resource.
	 * 
	 */
	public Map<String, List<String>> getDctermsHasPart() {
		return dctermsHasPart;
	}

	public void setDctermsHasPart(Map<String, List<String>> dctermsHasPart) {
		this.dctermsHasPart = dctermsHasPart;
	}

	/**
	 * Where one CHO has several web resources, shown by multiple instances of
	 * the edm:hasView property on the ore:Aggregation this property can be used
	 * to show the sequence of the objects. Each web resource (apart from the
	 * first in the sequence) should use this property to give the URI of the
	 * preceding resource in the sequence.
	 * 
	 */
	public String getIsNextInSequence() {
		return isNextInSequence;
	}

	public void setIsNextInSequence(String isNextInSequence) {
		this.isNextInSequence = isNextInSequence;
	}

	/**
	 * The name of a device or computer program capable of encoding or decoding
	 * a digital data stream or signal, e.g. h264
	 * 
	 */
	public String getEdmCodecName() {
		return edmCodecName;
	}

	public void setEdmCodecName(String edmCodecName) {
		this.edmCodecName = edmCodecName;
	}

	/**
	 * The main MIME type as defined by IANA: e.g. image/jpeg
	 * 
	 */
	public String getEbucoreHasMimeType() {
		return ebucoreHasMimeType;
	}

	public void setEbucoreHasMimeType(String ebucoreHasMimeType) {
		this.ebucoreHasMimeType = ebucoreHasMimeType;
	}

	/**
	 * The size of a media file in bytes
	 * 
	 */
	public int getEbucoreFileByteSize() {
		return ebucoreFileByteSize;
	}

	public void setEbucoreFileByteSize(int ebucoreFileByteSize) {
		this.ebucoreFileByteSize = ebucoreFileByteSize;
	}

	/**
	 * The duration of a media file in ms
	 * 
	 */
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * The width of a media file in pixels
	 * 
	 */
	public int getEbucoreWidth() {
		return ebucoreWidth;
	}

	public void setEbucoreWidth(int ebucoreWidth) {
		this.ebucoreWidth = ebucoreWidth;
	}

	/**
	 * The height of a media file in pixels
	 * 
	 */
	public int getEbucoreHeight() {
		return ebucoreHeight;
	}

	public void setEbucoreHeight(int ebucoreHeight) {
		this.ebucoreHeight = ebucoreHeight;
	}

	/**
	 * The spatial resolution of a media resource
	 * 
	 */
	public String getEdmSpatialResolution() {
		return edmSpatialResolution;
	}

	public void setEdmSpatialResolution(String edmSpatialResolution) {
		this.edmSpatialResolution = edmSpatialResolution;
	}

	/**
	 * The size of an audio sample in bits. Also called bit depth
	 * 
	 */
	public String getEbucoreSampleSize() {
		return ebucoreSampleSize;
	}

	public void setEbucoreSampleSize(String ebucoreSampleSize) {
		this.ebucoreSampleSize = ebucoreSampleSize;
	}

	/**
	 * The frequency at which audio is sampled per second. Also called sampling
	 * rate
	 * 
	 */
	public String getEbucoreSampleRate() {
		return ebucoreSampleRate;
	}

	public void setEbucoreSampleRate(String ebucoreSampleRate) {
		this.ebucoreSampleRate = ebucoreSampleRate;
	}

	/**
	 * To provide the bitrate at which the MediaResource can be played in
	 * kilobits/second
	 * 
	 */
	public String getEbucoreBitRate() {
		return ebucoreBitRate;
	}

	public void setEbucoreBitRate(String ebucoreBitRate) {
		this.ebucoreBitRate = ebucoreBitRate;
	}

	/**
	 * The frame rate of the video signal in frame per second
	 * 
	 */
	public String getEbucoreFrameRate() {
		return ebucoreFrameRate;
	}

	public void setEbucoreFrameRate(String ebucoreFrameRate) {
		this.ebucoreFrameRate = ebucoreFrameRate;
	}

	/**
	 * Whether an image is a coloured image
	 * 
	 */
	public String getEdmHasColorSpace() {
		return edmHasColorSpace;
	}

	public void setEdmHasColorSpace(String edmHasColorSpace) {
		this.edmHasColorSpace = edmHasColorSpace;
	}

	/**
	 * The orientation of a Document or an Image i.e. landscape or portrait
	 * 
	 */
	public String getEbucoreOrientation() {
		return ebucoreOrientation;
	}

	public void setEbucoreOrientation(String ebucoreOrientation) {
		this.ebucoreOrientation = ebucoreOrientation;
	}

	/**
	 * The total number of audio channels contained in the MediaResource
	 */
	public String getEbucoreAudioChannelNumber() {
		return ebucoreAudioChannelNumber;
	}

	public void setEbucoreAudioChannelNumber(String ebucoreAudioChannelNumber) {
		this.ebucoreAudioChannelNumber = ebucoreAudioChannelNumber;
	}

	/**
	 * 
	 * A related resource that references, cites, or otherwise points to the described resource. In IIIF, dcterms:isReferencedBy can be used to connect a edm:WebResource to a IIIF manifest URI.
	 */
	public String getDctermsIsReferencedBy() {
		return dctermsIsReferencedBy;
	}

	public void setDctermsIsReferencedBy(String dctermsIsReferencedBy) {
		this.dctermsIsReferencedBy = dctermsIsReferencedBy;
	}

	/**	
	 * The identifier of the Service require to consume the WebResource.
	 */
	public String getSvcsHasService() {
		return svcsHasService;
	}

	public void setSvcsHasService(String svcsHasService) {
		this.svcsHasService = svcsHasService;
	}

	
	
}
