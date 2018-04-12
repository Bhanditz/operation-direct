package eu.europeana.direct.legacy.logic.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.RandomSamplingFacetsCollector;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import eu.europeana.direct.legacy.index.CustomQueryParser;
import eu.europeana.direct.legacy.index.IndexManager;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneSearchModel;
import eu.europeana.direct.legacy.logic.helpers.MappingHelper;
import eu.europeana.direct.legacy.model.search.FacetParameter;
import eu.europeana.direct.legacy.model.search.Item;
import eu.europeana.direct.legacy.model.search.ProfileType;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

public class SearchEngine {
	final static Logger logger = Logger.getLogger(SearchEngine.class);
	
	private FacetsConfig config = new FacetsConfig();
	private StandardAnalyzer analyzer = new StandardAnalyzer();			
	private MappingHelper mappingHelper = new MappingHelper();		
	
	public SearchEngine() {
	}	

	/**
	 * Method searches for records in index based on query
	 * 
	 * @param indexPath
	 *            Path where index is located
	 * @param searchField
	 *            List of search field names
	 * @param searchTerm
	 *            Lucene query search term
	 * @param rows
	 *            The number of records to return. Maximum is 100. Default is
	 *            12
	 * @param facetsQuery
	 *            List of facets to search for
	 * @param documentType
	 *            Type of index document to search
	 *            (culturalheritageobject,agent,place,concept,..)
	 * @return LuceneSearchModel
	 * @throws IOException
	 */
	public LuceneSearchModel searchRecordsFromIndex(String queryString, boolean hasSearchField, String searchTerm,
			int rows, List<FacetParameter> facetsQuery, LuceneDocumentType documentType, int start, ProfileType profile, boolean entitySearch)
			throws IOException, ParseException {
	
		LuceneSearchModel luceneSearchModel = new LuceneSearchModel();
		CulturalHeritageObject culturalHeritageObject = new CulturalHeritageObject();		
		IndexReader indexReader = null;
		TaxonomyReader taxoReader = null;
		QueryParser queryParser = null;		
		Query query = null;						
		
		try {
			// get indexreader and taxonomy reader
			if(entitySearch){					
				indexReader = IndexManager.getInstance().getEntitiesIndexReaderInstance();				
			}else{
				indexReader = IndexManager.getInstance().getIndexReaderInstance();							
			}
			
			taxoReader = IndexManager.getInstance().getTaxonomyReaderInstance();			
			IndexSearcher searcher = new IndexSearcher(indexReader);						
			
			if (queryString == null || searchTerm == null || queryString.contains("*:*") || queryString.startsWith("*")){															
				queryParser = new CustomQueryParser("*", analyzer);				
				query = queryParser.parse("*");								
			} else {
				if (hasSearchField) {					
					queryParser = new CustomQueryParser(queryString, analyzer);					
				} else {
					// if no search field is defined for every search value, search for all fields
					
					// all cho fields
					String[] allFields = getAllFields(entitySearch, culturalHeritageObject);
					allFields = concatFields(allFields);
										
					// create query
					queryParser = new MultiFieldQueryParser(allFields, analyzer);														
				}				
				queryParser.setAllowLeadingWildcard(true);					
				query = queryParser.parse(searchTerm);					
			}
					
					
			RandomSamplingFacetsCollector fc = new RandomSamplingFacetsCollector(10000000);
			// search documents in index based on query												
			int numberOfDocs = rows+start;						
			if(start > 0){
				numberOfDocs -= 1;
			}
			if(numberOfDocs <= 0){
				numberOfDocs = 1;
			}			
			
			TopDocs topDocs = FacetsCollector.search(searcher, query, numberOfDocs, fc);					
			ScoreDoc[] hits = topDocs.scoreDocs;																					
			luceneSearchModel.setTotalResults(topDocs.totalHits);

			int startProcessed = start-1;
			if (startProcessed < 0) {
				startProcessed = 0;
			}

			
			for (int i = startProcessed; i < hits.length; i++) {				
				Document doc = searcher.doc(hits[i].doc);							
				luceneSearchModel.getItems().add(mappingHelper.documentToItem(doc, documentType, profile));
			}						
			
			if(!entitySearch){				
				if (!facetsQuery.isEmpty()) {
					// search facets only if at least one record was found in search
					if (luceneSearchModel.getItems().size() > 0) {				
						List<FacetResult> searchedFacets = new ArrayList<>();										
						Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);										
						for (FacetParameter fp : facetsQuery) {							
							searchedFacets.add(facets.getTopChildren(fp.getTopChildrens(), fp.getFacetTitle()));						
						}					
						if (searchedFacets.size() > 0) {				
							luceneSearchModel.setFacets(searchedFacets);
						}
					}
				}
			}
											
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (ParseException pe) {
			logger.error("Lucene query parse exception - " + pe.getMessage(), pe);
			throw new ParseException("Invalid query: " + pe.getMessage() + " Please check query syntax.");
		} finally {			
			if(entitySearch){
				IndexManager.getInstance().releaseEntitiesReaderInstance(indexReader);
			}else{
				IndexManager.getInstance().releaseReaderInstance(indexReader);	
			}			
			IndexManager.getInstance().releaseTaxonomyReaderInstance(taxoReader);		
		}				
		return luceneSearchModel;
	}
	
	public LuceneSearchModel searchRemovedRecords(String queryString, int rows, int start) throws IOException, ParseException {
		
		LuceneSearchModel luceneSearchModel = new LuceneSearchModel();			
		IndexReader indexReader = null;		
		QueryParser queryParser = null;		
		Query query = null;				
	
		try {
					
			indexReader = IndexManager.getInstance().getRemovedRecordsIndexReaderInstance();						
			IndexSearcher searcher = new IndexSearcher(indexReader);
									
			if (queryString != null && queryString.length() > 0){					
				queryParser = new CustomQueryParser(queryString, analyzer);					
				queryParser.setAllowLeadingWildcard(true);				
				query = queryParser.parse(queryString);
				
				// search documents in index based on query												
				int numberOfDocs = rows+start;						
				if(start > 0){
					numberOfDocs -= 1;
				}
				if(numberOfDocs <= 0){
					numberOfDocs = 1;
				}			
						
				TopDocs topDocs = searcher.search(query, numberOfDocs);			
				ScoreDoc[] hits = topDocs.scoreDocs;																					
				luceneSearchModel.setTotalResults(topDocs.totalHits);

				int startProcessed = start-1;
				if (startProcessed < 0) {
					startProcessed = 0;
				}
				
				for (int i = startProcessed; i < hits.length; i++) {				
					Document doc = searcher.doc(hits[i].doc);									
					// id, time of deletion
					luceneSearchModel.getItems().add(new Item(doc.get("choidString"),doc.get("deletedString")));
				}	
			}																				
											
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (ParseException pe) {
			logger.error("Lucene query parse exception - " + pe.getMessage(), pe);
			throw new ParseException("Invalid query: " + pe.getMessage() + " Please check query syntax.");
		} finally {						
			IndexManager.getInstance().releaseRemovedRecordsReaderInstance(indexReader);		
		}				
		return luceneSearchModel;
	}
	
	private String[] concatFields(String[] allFields) {
		for (int i = 0; i < allFields.length; i++) {
			allFields[i] = allFields[i] + "_index";
		}
		return allFields;
	}

	private String[] getAllFields(boolean entitySearch, CulturalHeritageObject culturalHeritageObject){
		
		if(entitySearch){
			return culturalHeritageObject.getRelatedEntitiesFields(null)
					.toArray(new String[culturalHeritageObject.getRelatedEntitiesFields(null).size()]);
		}else{
			return culturalHeritageObject.getAllFields()
					.toArray(new String[culturalHeritageObject.getAllFields().size()]);
		}
	}
	
}
