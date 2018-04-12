package eu.europeana.direct.legacy.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

public class SearchQueryMapper {
	
	private List<String> warnings = new ArrayList<String>();
	private List<String> langAwareFields = new ArrayList<String>();
		
	public SearchQueryMapper() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public SearchQueryMapper(List<String> warnings) {
		super();
		this.warnings = warnings;
	}
	
	public List<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}		
		
	/**
	 * Method changes api search parameter value to that equivalent to the parameter name in index
	 * if parameter value name doesn't exist in database then adds a warning
	 * @param nameInSearch List of searching field names	 
	 */
	public List<String> mapToDatabaseField(List<String> nameInSearch){
		
		langAwareFields.add("title");
		langAwareFields.add("description");
		langAwareFields.add("publisher");
		langAwareFields.add("source");
		langAwareFields.add("createdCho");
		langAwareFields.add("issued");
		langAwareFields.add("format");
		langAwareFields.add("extent");
		langAwareFields.add("medium");
		langAwareFields.add("timespanAlternativeLabel");
		langAwareFields.add("timespanPreferredLabel");
		langAwareFields.add("placeAlternativeLabel");
		langAwareFields.add("placePreferredLabel");
		langAwareFields.add("conceptAlternativeLabel");
		langAwareFields.add("conceptPreferredLabel");
		langAwareFields.add("agentAlternativeLabel");
		langAwareFields.add("agentPreferredLabel");
		
		List<String> databaseFieldNames = new ArrayList<String>();
		
		for(String name : nameInSearch){	
			if(name.contains("timestamp_created")){
				name = name.replace("timestamp_created", "created");
			}
			if(name.contains("timestamp_update")){
				name = name.replace("timestamp_update", "modified");
			}		
			if(name.contains("europeana_id")){
				name = name.replace("europeana_id", "choidString");
				if(name.contains("/direct/")){
					name = name.replace("/direct/","");
				}
			}
			if(name.contains("who")){
				name = name.replace("who", "agentPreferredLabel_index");
			}
			if(name.contains("what")){
				name = name.replace("what", "conceptPreferredLabel_index");
			}
			if(name.contains("edm_europeana_proxy")){
				warnings.add("No such field in database (edm_europeana_proxy)");
				continue;
			}
			if(name.contains("proxy_dc_contributor")){
				warnings.add("No such field in database (proxy_dc_contributor)");
				continue;
			}
			if(name.contains("proxy_dc_coverage")){
				warnings.add("No such field in database (proxy_dc_coverage)");
				continue;
			}
			if(name.contains("proxy_dc_creator")){
				name = name.replace("proxy_dc_creator", "dataOwner");				
			}
			if(name.contains("proxy_dc_date")){
				name = name.replace("proxy_dc_date", "created");				
			}
			if(name.contains("proxy_dc_description")){
				name = name.replace("proxy_dc_description", "description_index");				
			}
			if(name.contains("proxy_dc_format")){
				name = name.replace("proxy_dc_format", "format_index");				
			}
			if(name.contains("proxy_dc_identifier")){
				name = name.replace("proxy_dc_identifier", "identifier");				
			}
			if(name.contains("proxy_dc_language")){
				name = name.replace("proxy_dc_language", "language");				
			}
			if(name.contains("proxy_dc_publisher")){
				name = name.replace("proxy_dc_publisher", "publisher_index");				
			}
			if(name.contains("proxy_dc_relation")){
				name = name.replace("proxy_dc_relation", "relation");
			}
			if(name.contains("proxy_dc_rights")){
				name = name.replace("proxy_dc_rights", "rights");
			}
			if(name.contains("proxy_dc_source")){
				name = name.replace("proxy_dc_source", "source_index");
			}
			if(name.contains("proxy_dc_subject")){
				warnings.add("No such field in database (proxy_dc_subject)");
				continue;
			}
			if(name.contains("proxy_dc_title")){
				name = name.replace("proxy_dc_title", "title_index");
			}
			if(name.contains("proxy_dc_type")){
				name = name.replace("proxy_dc_type", "type");
			}
			if(name.contains("proxy_dcterms_alternative")){
				name = name.replace("proxy_dcterms_alternative", "alternative");
			}
			if(name.contains("proxy_dcterms_conformsTo")){
				warnings.add("No such field in database (proxy_dcterms_conformsTo)");
				continue;
			}
			if(name.contains("proxy_dcterms_created")){
				name = name.replace("proxy_dcterms_created", "createdCho_index");
			}
			if(name.contains("proxy_dcterms_extent")){
				name = name.replace("proxy_dcterms_extent", "extent_index");
			}
			if(name.contains("proxy_dcterms_hasFormat")){
				warnings.add("No such field in database (proxy_dcterms_hasFormat)");
				continue;
			}
			if(name.contains("proxy_dcterms_hasPart")){
				warnings.add("No such field in database (proxy_dcterms_hasPart)");
				continue;
			}
			if(name.contains("proxy_dcterms_hasVersion")){
				warnings.add("No such field in database (proxy_dcterms_hasVersion)");
				continue;
			}
			if(name.contains("proxy_dcterms_isFormatOf")){
				name = name.replace("proxy_dcterms_isFormatOf", "format_index");
			}
			if(name.contains("proxy_dcterms_isPartOf")){
				warnings.add("No such field in database (proxy_dcterms_isPartOf)");
				continue;
			}
			if(name.contains("proxy_dcterms_isReferencedBy")){
				warnings.add("No such field in database (proxy_dcterms_isReferencedBy)");
				continue;
			}
			if(name.contains("proxy_dcterms_isReplacedBy")){
				warnings.add("No such field in database (proxy_dcterms_isReplacedBy)");
				continue;
			}
			if(name.contains("proxy_dcterms_isRequiredBy")){
				warnings.add("No such field in database (proxy_dcterms_isRequiredBy)");
				continue;
			}
			if(name.contains("proxy_dcterms_issued")){
				name = name.replace("proxy_dcterms_issued", "issued_index");
			}
			if(name.contains("proxy_dcterms_isVersionOf")){
				warnings.add("No such field in database (proxy_dcterms_isVersionOf)");
				continue;
			}
			if(name.contains("proxy_dcterms_medium")){
				name = name.replace("proxy_dcterms_medium", "conceptPreferredLabel_index");
			}
			if(name.contains("proxy_dcterms_provenance")){
				name = name.replace("proxy_dcterms_provenance", "provenance_index");
			}
			if(name.contains("proxy_dcterms_references")){
				warnings.add("No such field in database (proxy_dcterms_references)");
				continue;
			}
			if(name.contains("proxy_dcterms_replaces")){
				warnings.add("No such field in database (proxy_dcterms_replaces)");
				continue;
			}
			if(name.contains("proxy_dcterms_requires")){
				warnings.add("No such field in database (proxy_dcterms_requires)");
				continue;
			}
			if(name.contains("proxy_dcterms_spatial")){
				warnings.add("No such field in database (proxy_dcterms_spatial)");
				continue;
			}
			if(name.contains("proxy_dcterms_tableOfContents")){
				warnings.add("No such field in database (proxy_dcterms_tableOfContents)");
				continue;
			}
			if(name.contains("proxy_dcterms_temporal")){
				warnings.add("No such field in database (proxy_dcterms_temporal)");
				continue;
			}
			if(name.contains("proxy_edm_currentLocation")){
				warnings.add("No such field in database (proxy_edm_currentLocation)");
				continue;
			}
			if(name.contains("proxy_edm_currentLocation_lat")){
				name = name.replace("proxy_edm_currentLocation_lat", "latitude");
			}
			if(name.contains("proxy_edm_currentLocation_lon")){
				name = name.replace("proxy_edm_currentLocation_lon", "longitude");
			}
			if(name.contains("proxy_edm_hasMet")){
				warnings.add("No such field in database (proxy_edm_hasMet)");
				continue;
			}
			if(name.contains("proxy_edm_hasType")){
				warnings.add("No such field in database (proxy_edm_hasType)");
				continue;
			}
			if(name.contains("proxy_edm_incorporates")){
				warnings.add("No such field in database (proxy_edm_incorporates)");
				continue;
			}
			if(name.contains("proxy_edm_isDerivativeOf")){
				warnings.add("No such field in database (proxy_edm_isDerivativeOf)");
				continue;
			}
			if(name.contains("proxy_edm_isNextInSequence")){
				warnings.add("No such field in database (proxy_edm_isNextInSequence)");
				continue;
			}
			if(name.contains("proxy_edm_isRelatedTo")){
				warnings.add("No such field in database (proxy_edm_isRelatedTo)");
				continue;
			}
			if(name.contains("proxy_edm_isRepresentationOf")){
				warnings.add("No such field in database (proxy_edm_isRepresentationOf)");
				continue;
			}
			if(name.contains("proxy_edm_isSimilarTo")){
				warnings.add("No such field in database (proxy_edm_isSimilarTo)");
				continue;
			}
			if(name.contains("proxy_edm_isSuccessorOf")){
				warnings.add("No such field in database (proxy_edm_isSimilarTo)");
				continue;
			}
			if(name.contains("proxy_edm_realizes")){
				warnings.add("No such field in database (proxy_edm_isSimilarTo)");
				continue;
			}
			if(name.contains("proxy_edm_type")){
				name = name.replace("proxy_edm_type", "type");
			}
			if(name.contains("proxy_edm_unstored")){
				warnings.add("No such field in database (proxy_edm_unstored)");
				continue;
			}
			if(name.contains("proxy_edm_wasPresentAt")){
				warnings.add("No such field in database (proxy_edm_wasPresentAt)");
				continue;
			}
			if(name.contains("proxy_owl_sameAs")){
				name = name.replace("proxy_edm_type", "sameAs");
			}
			if(name.contains("proxy_edm_rights")){
				name = name.replace("proxy_edm_rights", "rights");
			}
			if(name.contains("proxy_edm_userTags")){
				warnings.add("No such field in database (proxy_edm_userTags)");
				continue;
			}
			if(name.contains("proxy_edm_year")){
				warnings.add("No such field in database (proxy_edm_year)");
				continue;
			}
			if(name.contains("proxy_ore_proxy")){
				warnings.add("No such field in database (proxy_ore_proxy)");
				continue;
			}
			if(name.contains("proxy_ore_proxyFor")){
				warnings.add("No such field in database (proxy_ore_proxyFor)");
				continue;
			}
			if(name.contains("proxy_ore_proxyIn")){
				warnings.add("No such field in database (proxy_ore_proxyIn)");
				continue;
			}
			if(name.contains("proxy_edm_currentLocation")){
				warnings.add("No such field in database (proxy_edm_currentLocation)");
				continue;
			}
			if(name.contains("provider_aggregation_ore_aggregation")){
				warnings.add("No such field in database (provider_aggregation_ore_aggregation)");
				continue;
			}
			if(name.contains("provider_aggregation_ore_aggregates")){
				warnings.add("No such field in database (provider_aggregation_ore_aggregates)");
				continue;
			}
			if(name.contains("provider_aggregation_edm_aggregatedCHO")){
				warnings.add("No such field in database (provider_aggregation_edm_aggregatedCHO)");
				continue;
			}
			if(name.contains("provider_aggregation_edm_dataProvider")){
				name = name.replace("provider_aggregation_edm_dataProvider", "dataOwner");
			}
			if(name.contains("provider_aggregation_edm_hasView")){
				warnings.add("No such field in database (provider_aggregation_edm_hasView)");
				continue;
			}
			if(name.contains("provider_aggregation_edm_isShownAt")){
				warnings.add("No such field in database (provider_aggregation_edm_isShownAt)");
				continue;
			}
			if(name.contains("provider_aggregation_edm_isShownBy")){
				warnings.add("No such field in database (provider_aggregation_edm_isShownBy)");
				continue;
			}
			if(name.contains("provider_aggregation_edm_object")){
				warnings.add("No such field in database (provider_aggregation_edm_object)");
				continue;
			}
			if(name.contains("provider_aggregation_edm_provider")){
				warnings.add("No such field in database (provider_aggregation_edm_provider)");
				continue;
			}
			if(name.contains("provider_aggregation_dc_rights")){
				name = name.replace("provider_aggregation_dc_rights", "rights");
			}			
			if(name.contains("edm_ugc")){
				warnings.add("No such field in database (edm_UGC)");
				continue;
			}
			if(name.contains("provider_aggregation_edm_unstored")){
				warnings.add("No such field in database (provider_aggregation_edm_unstored)");
				continue;
			}
			if(name.contains("edm_previewNoDistribute")){
				warnings.add("No such field in database (edm_previewNoDistribute)");
				continue;
			}
			if(name.contains("edm_europeana_aggregation")){
				warnings.add("No such field in database (edm_europeana_aggregation)");
				continue;
			}
			if(name.contains("europeana_aggregation_dc_creator")){
				warnings.add("No such field in database (europeana_aggregation_dc_creator)");
				continue;
			}
			if(name.contains("europeana_aggregation_datasetName")){
				warnings.add("No such field in database (europeana_aggregation_datasetName)");
				continue;
			}
			if(name.contains("europeana_aggregation_edm_country")){
				name = name.replace("europeana_aggregation_edm_country", "country");
			}
			if(name.contains("europeana_aggregation_edm_hasView")){
				warnings.add("No such field in database (europeana_aggregation_edm_hasView)");
				continue;
			}
			if(name.contains("europeana_aggregation_edm_isShownBy")){
				warnings.add("No such field in database (europeana_aggregation_edm_isShownBy)");
				continue;
			}
			if(name.contains("europeana_aggregation_edm_landingPage")){
				warnings.add("No such field in database (europeana_aggregation_edm_landingPage)");
				continue;
			}
			if(name.contains("europeana_aggregation_edm_language")){
				name = name.replace("europeana_aggregation_edm_language", "language");
			}
			if(name.contains("europeana_aggregation_edm_rights")){
				name = name.replace("europeana_aggregation_edm_rights", "rights");
			}
			if(name.contains("europeana_aggregation_ore_aggregatedCHO")){
				warnings.add("No such field in database (europeana_aggregation_ore_aggregatedCHO)");
				continue;
			}
			if(name.contains("europeana_aggregation_ore_aggregates")){
				warnings.add("No such field in database (europeana_aggregation_ore_aggregates)");
				continue;
			}
			if(name.contains("edm_webResource")){
				name = name.replace("edm_webResource", "id");
			}
			if(name.contains("wr_dc_description")){
				name = name.replace("wr_dcterms_extent", "description");
			}
			if(name.contains("wr_dc_format")){
				name = name.replace("wr_dcterms_extent", "format");
			}
			if(name.contains("wr_dc_rights")){
				name = name.replace("wr_dc_rights", "rights");
			}
			if(name.contains("wr_dc_source")){
				warnings.add("No such field in database (wr_dc_source)");
				continue;
			}
			if(name.contains("wr_dcterms_conformsTo")){
				warnings.add("No such field in database (wr_dcterms_conformsTo)");
				continue;
			}
			if(name.contains("wr_dcterms_created")){
				name = name.replace("wr_dcterms_extent", "created");
			}
			if(name.contains("wr_dcterms_extent")){
				name = name.replace("wr_dcterms_extent", "extent");
			}
			if(name.contains("wr_dcterms_hasPart")){
				warnings.add("No such field in database (wr_dcterms_hasPart)");
				continue;
			}
			if(name.contains("wr_dcterms_isFormatOf")){
				warnings.add("No such field in database (wr_dcterms_isFormatOf)");
				continue;
			}
			if(name.contains("wr_dcterms_issued")){
				name = name.replace("wr_dcterms_issued", "issued");
			}
			if(name.contains("wr_edm_isNextInSequence")){
				warnings.add("No such field in database (wr_edm_isNextInSequence)");
				continue;
			}
			if(name.contains("wr_edm_rights")){
				name = name.replace("wr_edm_rights", "rights");
			}
			if(name.contains("wr_edm_codecName")){
				warnings.add("No such field in database (wr_edm_codecName)");
				continue;
			}
			if(name.contains("wr_ebucore_hasMimeType")){
				warnings.add("No such field in database (wr_ebucore_hasMimeType)");
				continue;
			}
			if(name.contains("wr_ebucore_fileByteSize")){
				warnings.add("No such field in database (wr_ebucore_fileByteSize)");
				continue;
			}
			if(name.contains("wr_ebucore_duration")){
				warnings.add("No such field in database (wr_ebucore_duration)");
				continue;
			}
			if(name.contains("wr_ebucore_width")){
				warnings.add("No such field in database (wr_ebucore_width)");
				continue;
			}			
			if(name.contains("wr_ebucore_height")){
				warnings.add("No such field in database (wr_ebucore_height)");
				continue;
			}
			if(name.contains("wr_edm_spatialResolution")){
				warnings.add("No such field in database (wr_edm_spatialResolution)");
				continue;
			}
			if(name.contains("wr_ebucore_sampleSize")){
				warnings.add("No such field in database (wr_ebucore_sampleSize)");
				continue;
			}
			if(name.contains("wr_ebucore_sampleRate")){
				warnings.add("No such field in database (wr_ebucore_sampleRate)");
				continue;
			}
			if(name.contains("wr_ebucore_bitRate")){
				warnings.add("No such field in database (wr_ebucore_bitRate)");
				continue;
			}
			if(name.contains("wr_ebucore_frameRate")){
				warnings.add("No such field in database (wr_ebucore_frameRate)");
				continue;
			}
			if(name.contains("wr_edm_hasColorSpace")){
				warnings.add("No such field in database (wr_edm_hasColorSpace)");
				continue;
			}
			if(name.contains("wr_edm_componentColor")){
				warnings.add("No such field in database (wr_edm_componentColor)");
				continue;
			}
			if(name.contains("wr_ebucore_orientation")){
				warnings.add("No such field in database (wr_ebucore_orientation)");
				continue;
			}
			if(name.contains("wr_ebucore_audioChannelNumber")){
				warnings.add("No such field in database (wr_ebucore_audioChannelNumber)");
				continue;
			}
			if(name.contains("rdf_type")){
				name = name.replace("rdf_type", "type");
			}
			if(name.contains("edm_agent")){
				name = name.toLowerCase().replace("edm_agent", "agentid");
			}
			if(name.contains("ag_skos_prefLabel")){
				name = name.replace("ag_skos_prefLabel", "agentPreferredLabel_index");
			}
			if(name.contains("ag_skos_altLabel")){
				name = name.replace("ag_skos_altLabel", "agentAlternativeLabel_index");
			}
			if(name.contains("ag_skos_hiddenLabel")){
				warnings.add("No such field in database (ag_skos_hiddenLabel)");
				continue;
			}
			if(name.contains("ag_skos_note")){
				warnings.add("No such field in database (ag_skos_note)");
				continue;
			}
			if(name.contains("ag_dc_date")){
				warnings.add("No such field in database (ag_dc_date)");
				continue;
			}
			if(name.contains("ag_dc_identifier")){
				name = name.replace("ag_dc_identifier", "identifier");
			}
			if(name.contains("ag_edm_begin")){
				name = name.replace("ag_edm_begin", "dateOfBirth");
			}
			if(name.contains("ag_edm_end")){
				name = name.replace("ag_edm_end", "dateOfDeath");
			}
			if(name.contains("ag_edm_hasMet")){
				warnings.add("No such field in database (ag_edm_hasMet)");
				continue;
			}
			if(name.contains("ag_edm_isRelatedTo")){
				warnings.add("No such field in database (ag_edm_isRelatedTo)");
				continue;
			}
			if(name.contains("ag_edm_wasPresentAt")){
				warnings.add("No such field in database (ag_edm_wasPresentAt)");
				continue;
			}
			if(name.contains("ag_foaf_name")){
				warnings.add("No such field in database (ag_foaf_name)");
				continue;
			}
			if(name.contains("ag_rdagr2_biographicalInformation")){
				name = name.replace("ag_rdagr2_biographicalInformation", "biographicalInformation");
			}
			if(name.contains("ag_rdagr2_dateOfBirth")){
				name = name.replace("ag_rdagr2_dateOfBirth", "dateOfBirth");
			}
			if(name.contains("ag_rdagr2_dateOfDeath")){
				name = name.replace("ag_rdagr2_dateOfDeath", "dateOfDeath");
			}
			if(name.contains("ag_rdagr2_dateOfEstablishment")){
				name = name.replace("ag_rdagr2_dateOfEstablishment", "dateOfEstablishment");
			}
			if(name.contains("ag_rdagr2_dateOfTermination")){
				name = name.replace("ag_rdagr2_dateOfTermination", "dateOfTermination");
			}
			if(name.contains("ag_rdagr2_gender")){
				name = name.replace("ag_rdagr2_gender", "gender");
			}
			if(name.contains("ag_rdagr2_professionOrOccupation")){
				name = name.replace("ag_rdagr2_professionOrOccupation", "professionOrOccupation");
			}
			if(name.contains("ag_owl_sameAs")){
				name = name.replace("proxy_edm_type", "sameAs");
			}
			if(name.contains("skos_concept")){
				name = name.replace("skos_concept", "conceptid");
			}
			if(name.contains("cc_skos_prefLabel")){
				name = name.replace("cc_skos_prefLabel", "conceptPreferredLabel_index");
			}
			if(name.contains("cc_skos_altLabel")){
				name = name.replace("cc_skos_altLabel", "conceptAlternativeLabel_index");
			}
			if(name.contains("cc_skos_hiddenLabel")){
				warnings.add("No such field in database (cc_skos_hiddenLabel)");
				continue;
			}
			if(name.contains("cc_skos_broader")){
				warnings.add("No such field in database (cc_skos_broader)");
				continue;
			}
			if(name.contains("cc_skos_broaderLabel")){
				warnings.add("No such field in database (cc_skos_broaderLabel)");
				continue;
			}
			if(name.contains("cc_skos_narrower")){
				warnings.add("No such field in database (cc_skos_narrower)");
				continue;
			}
			if(name.contains("cc_skos_related")){
				warnings.add("No such field in database (cc_skos_related)");
				continue;
			}
			if(name.contains("cc_skos_broadMatch")){
				warnings.add("No such field in database (cc_skos_broadMatch)");
				continue;
			}
			if(name.contains("cc_skos_narrowMatch")){
				warnings.add("No such field in database (cc_skos_narrowMatch)");
				continue;
			}
			if(name.contains("cc_skos_relatedMatch")){
				warnings.add("No such field in database (cc_skos_relatedMatch)");
				continue;
			}
			if(name.contains("cc_skos_exactMatch")){
				warnings.add("No such field in database (cc_skos_exactMatch)");
				continue;
			}
			if(name.contains("cc_skos_closeMatch")){
				warnings.add("No such field in database (cc_skos_closeMatch)");
				continue;
			}
			if(name.contains("cc_skos_note")){
				name = name.replace("cc_skos_note", "note");
			}
			if(name.contains("cc_skos_notation")){
				warnings.add("No such field in database (cc_skos_notation)");
				continue;
			}
			if(name.contains("cc_skos_inScheme")){
				warnings.add("No such field in database (cc_skos_inScheme)");
				continue;
			}
			if(name.contains("edm_place")){
				name = name.replace("edm_place", "placeIdString");
			}
			if(name.contains("pl_wgs84_pos_lat")){
				name = name.replace("pl_wgs84_pos_lat", "latitude");
			}
			if(name.contains("pl_wgs84_pos_long")){
				name = name.replace("pl_wgs84_pos_long", "longitude");
			}
			if(name.contains("pl_wgs84_pos_alt")){
				name = name.replace("pl_wgs84_pos_alt", "altitude");
			}
			if(name.contains("pl_wgs84_pos_lat_long")){
				warnings.add("No such field in database (pl_wgs84_pos_lat_long)");
				continue;
			}
			if(name.contains("pl_skos_prefLabel")){
				name = name.replace("pl_skos_prefLabel", "placePreferredLabel_index");
			}
			if(name.contains("pl_skos_altLabel")){
				name = name.replace("pl_skos_altLabel", "placeAlternativeLabel_index");
			}
			if(name.contains("pl_skos_hiddenLabel")){
				warnings.add("No such field in database (pl_skos_hiddenLabel)");
				continue;
			}
			if(name.contains("pl_skos_note")){
				name = name.replace("pl_skos_note", "note");
			}
			if(name.contains("pl_dcterms_hasPart")){
				warnings.add("No such field in database (pl_dcterms_hasPart)");
				continue;
			}
			if(name.contains("pl_dcterms_isPartOf")){
				warnings.add("No such field in database (pl_dcterms_isPartOf)");
				continue;
			}
			if(name.contains("pl_owl_sameAs")){
				name = name.replace("pl_owl_sameAs", "sameAs");
			}
			if(name.contains("pl_dcterms_isPartOf_label")){
				warnings.add("No such field in database (pl_dcterms_isPartOf_label)");
				continue;
			}
			if(name.contains("edm_timespan")){
				name = name.replace("edm_timespan", "timespanIdString");
			}
			if(name.contains("ts_skos_prefLabel")){
				name = name.replace("ts_skos_prefLabel", "timespanPreferredLabel_index");
			}
			if(name.contains("ts_skos_altLabel")){
				name = name.replace("ts_skos_altLabel", "timespanAlternativeLabel_index");
			}
			if(name.contains("ts_skos_hiddenLabel")){
				warnings.add("No such field in database (ts_skos_hiddenLabel)");
				continue;
			}
			if(name.contains("ts_skos_note")){
				name = name.replace("ts_skos_note", "note");
			}
			if(name.contains("ts_dcterms_hasPart")){
				warnings.add("No such field in database (ts_dcterms_hasPart)");
				continue;
			}
			if(name.contains("ts_dcterms_isPartOf")){
				warnings.add("No such field in database (ts_dcterms_isPartOf)");
				continue;
			}
			if(name.contains("ts_edm_begin")){
				warnings.add("No such field in database (ts_edm_begin)");
				continue;
			}
			if(name.contains("ts_edm_end")){
				warnings.add("No such field in database (ts_edm_end)");
				continue;
			}
			if(name.contains("ts_owl_sameAs")){
				name = name.replace("ts_owl_sameAs", "sameAs");
			}
			if(name.contains("ts_dcterms_isPartOf_label ")){
				warnings.add("No such field in database (ts_dcterms_isPartOf_label)");
				continue;
			}
			if (name.contains("COMPLETENESS")) {
				warnings.add("No such field in database (COMPLETENESS)");
				continue;
			}				
			if (name.contains("CONTRIBUTOR")) {
				warnings.add("No such field in database (CONTRIBUTOR)");
				continue;
			}
			if (name.contains("COUNTRY")) {
				name = name.replace("COUNTRY", "country");
			}
			if (name.contains("DATA_PROVIDER")) {
				name = name.replace("DATA_PROVIDER", "dataOwner");
			}
			if (name.contains("LANGUAGE")) {
				name = name.replace("LANGUAGE", "language");
			}
			if (name.contains("language")) {
				name = name.replace("language", "language");
			}
			if (name.contains("LOCATION")) {
				name = name.replace("LOCATION", "placePreferredLabel");
			}
			if (name.contains("PROVIDER")) {
				warnings.add("No such field in database (CONTRIBUTOR)");
				continue;
			}
			if (name.contains("RIGHTS")) {
				name = name.replace("RIGHTS", "rights");
			}
			if (name.contains("SUBJECT")) {
				warnings.add("No such field in database (SUBJECT)");
				continue;
			}
			if (name.contains("TYPE")) {
				name = name.replace("TYPE", "type");
			}
			if (name.contains("UGC")) {
				warnings.add("No such field in database (UGC)");
				continue;
			}
			if (name.contains("YEAR")) {
				warnings.add("No such field in database (YEAR)");
				continue;
			}
			if (name.contains("europeanaCompleteness")) {
				warnings.add("No such field in database (europeanaCompleteness)");
				continue;
			}
			if (name.contains("completeness")) {
				warnings.add("No such field in database (completeness)");
				continue;
			}
			if (name.contains("MEDIA")) {
				warnings.add("No such field in database (MEDIA)");
				continue;
			}
			if (name.toUpperCase().contains("MIME_TYPE")) {
				warnings.add("No such field in database (MIME_TYPE)");
				continue;
			}
			if (name.contains("IMAGE_SIZE")) {
				warnings.add("No such field in database (IMAGE_SIZE)");
				continue;
			}
			if (name.contains("IMAGE_COLOUR")) {
				warnings.add("No such field in database (IMAGE_COLOUR)");
				continue;
			}
			if (name.contains("IMAGE_GREYSCALE")) {
				warnings.add("No such field in database (IMAGE_GREYSCALE)");
				continue;
			}
			if (name.contains("COLOURPALETTE")) {
				warnings.add("No such field in database (COLOURPALETTE)");
				continue;
			}
			if (name.contains("IMAGE_ASPECTRATIO")) {
				warnings.add("No such field in database (IMAGE_ASPECTRATIO)");
				continue;
			}
			if (name.contains("VIDEO_HD")) {
				warnings.add("No such field in database (VIDEO_HD)");
				continue;
			}
			if (name.contains("VIDEO_DURATION")) {
				warnings.add("No such field in database (VIDEO_DURATION)");
				continue;
			}
			if (name.contains("SOUND_HQ")) {
				warnings.add("No such field in database (SOUND_HQ)");
				continue;
			}
			if (name.contains("SOUND_DURATION")) {
				warnings.add("No such field in database (SOUND_DURATION)");
				continue;
			}
			if (name.contains("TEXT_FULLTEXT")) {
				warnings.add("No such field in database (TEXT_FULLTEXT)");
				continue;
			}
			if (name.contains("REUSABILITY")) {
				warnings.add("No such field in database (REUSABILITY)");
				continue;
			}
			
			if(!databaseFieldNames.contains(name)){						
				databaseFieldNames.add(name);	
			}			

			for(int i=0; i<langAwareFields.size();i++){
				for(int j=0; j<databaseFieldNames.size();j++){					
					if(databaseFieldNames.get(j) != null && databaseFieldNames.get(j).contains(langAwareFields.get(i))){						
						String facetLabel = databaseFieldNames.get(j);
						if(facetLabel.contains(langAwareFields.get(i)+"_index")){
							facetLabel = facetLabel.replace(langAwareFields.get(i)+"_index", langAwareFields.get(i)+"_index");
						}else{
							facetLabel = facetLabel.replace(langAwareFields.get(i), langAwareFields.get(i)+"_index");	
						}
												
						databaseFieldNames.set(j,facetLabel);
					}
				}
			}			
		}		
		return databaseFieldNames;
	}	
}
