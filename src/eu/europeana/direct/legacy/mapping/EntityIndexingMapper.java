package eu.europeana.direct.legacy.mapping;

import java.util.Date;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetsConfig;

import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.legacy.model.search.ProfileType;

public interface EntityIndexingMapper<T> {
	
	/**
	 * Maps source to Lucene document of the same type like source.
	 * For example: If source is Agent then maps lucene document of agent
	 * @param source One of database sources (Agent, Concept,...)
	 */
	Document mapFromSource(T source, Date created, Set<EuropeanaDataObjectEuropeanaDataObject> linkedObjects);
	
	/**
	 * Maps lucene document to database source object (Agent, Concept,...)
	 * @param luceneDocument Lucene document from index 
	 */
	T mapToSource(Document luceneDocument, ProfileType profile);
	
	/**
	 * Configure facets config. Setting multivalued fields,...
	 * @param config Facets config
	 */
	void configureFacets(FacetsConfig config);
}