package eu.europeana.direct.legacy.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.LabelAndValue;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.direct.harvesting.models.helpers.DashboardRecord;
import eu.europeana.direct.harvesting.models.helpers.SearchModel;
import eu.europeana.direct.harvesting.models.view.DashboardView;
import eu.europeana.direct.legacy.model.search.Facet;
import eu.europeana.direct.legacy.model.search.FacetParameter;
import eu.europeana.direct.legacy.model.search.Field;
import eu.europeana.direct.legacy.model.search.Item;

public class SearchHelper {	
	
	private ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
 	public Facet[] mapFacetsToSearchModel(List<FacetResult> facets){
	
		Facet[] facetArray = null;
		//set facets to search response model
		if (!facets.isEmpty()) {
			List<Facet> facetsList = new ArrayList<Facet>();
			for (FacetResult result : facets) {
				if (result != null) {
					Facet f = new Facet();
					// facet name
					f.setName(result.dim);
					List<Field> fieldsList = new ArrayList<Field>();
					// facet values
					for (LabelAndValue lav : result.labelValues) {
						fieldsList.add(new Field(lav.label, (int) lav.value));
					}
					Field[] fieldArray = fieldsList.toArray(new Field[fieldsList.size()]);
					// set fields as array
					f.setFields(fieldArray);
					// add facet to list of facets
					facetsList.add(f);
				}
			}
			// list of facets to array
			facetArray = facetsList.toArray(new Facet[facetsList.size()]);
		}
		return facetArray;
	}
	
	public Item[] mapSearchItemsToSearchModel(List<Item> searchItems){
		Item[] itemArray = null;
		
		// set searched items to search response model
		if (!searchItems.isEmpty()) {			
			itemArray = searchItems.toArray(new Item[searchItems.size()]);
		}			
		return itemArray;
	}

	public DashboardView mapSearchToDashboardView(String searchResponse, Map<String,String> map) {

		SearchModel searchModel;
		DashboardView viewModel = new DashboardView();
		List<DashboardRecord> list = new ArrayList<DashboardRecord>();
		try {
			searchModel = OBJECT_MAPPER.readValue(searchResponse, SearchModel.class);		
			viewModel.setNumOfPages(searchModel.getTotalResults());
			
			if(searchModel.getItems() != null && searchModel.getItems().size() > 0){			
				for(Item item : searchModel.getItems()){
					String id = item.getId().replace("/direct/", "");
					String weblink = null;
					
					if(item.getEdmIsShownAt() != null && item.getEdmIsShownAt().length > 0){
						weblink = item.getEdmIsShownAt()[0];
					} else if(item.getEdmPreview() != null && item.getEdmPreview().length() > 0){						
						weblink = item.getEdmPreview().substring(item.getEdmPreview().indexOf("="),item.getEdmPreview().indexOf("&"));		
						weblink = weblink.replace("=", "");
					}
					
					list.add(new DashboardRecord(Long.valueOf(id), item.getTimestamp_update(),weblink,
							map.get("edmDomain")+"verb=GetRecord&identifier="+id+"&metadataPrefix=edm",
							map.get("odDomain")+"/api/object?id="+id+"&apikey=g31u2hg4qcnoo2hupe06o21re2"));
				}
			}
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		viewModel.setRecords(list);
		return viewModel;
	}
	
	public boolean setMediaValue(String mediaString) {
		//if parameter media exists
		if (mediaString != null) {				
			if (mediaString.equals("true")) {
				return true;
			} else if (mediaString.equals("false")) {
				return false;
			}
		}
		return false;
	}

	public boolean setThumbnailValue(String thumbnailString) {
		//if parameter thumbnail exists
		if (thumbnailString != null) {
			if (thumbnailString.toLowerCase().equals("true")) {
				return true;
			} else if (thumbnailString.toLowerCase().equals("false")) {
				return false;
			}
		}
		return false;
	}

	public boolean setLandingPageValue(String landingPageString) {
		if (landingPageString != null) {
			if (landingPageString.toLowerCase().equals("true")) {
				return true;
			} else if (landingPageString.toLowerCase().equals("false")) {
				return false;
			}
		}
		return false;
	}

	public List<FacetParameter> providerFacetsWithLimits(List<String> queryFacets, Map<String, Integer> facetsLimit) {
		
		List<FacetParameter> facetsWithLimits = new ArrayList<FacetParameter>();
		
		if(facetsLimit.size() > 0){
			for(String f : queryFacets){
				boolean contains = false;
				int value = 20;
				// loop through those facets that had defined limit for topChilderns in GET request 
				for(Map.Entry<String, Integer> map : facetsLimit.entrySet()){
					// if facets also exists in map, we retrieve value for topChildrens
					if(map.getKey().contains(f)){						
						contains = true;
						value = map.getValue();					
					}
				}
				// if contains add with value(topChildrens) that was defined in GET request, otherwise default is 20 topChildrens for facets
				if(contains){
					facetsWithLimits.add(new FacetParameter(f,value));
				}else{
					facetsWithLimits.add(new FacetParameter(f,value));
				}
			}	
		}else{
			// if map is empty add all with 20 top childrens max value
			for(String f : queryFacets){
				facetsWithLimits.add(new FacetParameter(f,20));
			}
		}						
		return facetsWithLimits;
	}			
}
