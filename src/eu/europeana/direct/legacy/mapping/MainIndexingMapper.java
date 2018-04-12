package eu.europeana.direct.legacy.mapping;

import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetsConfig;

import eu.europeana.direct.legacy.model.search.ProfileType;

public interface MainIndexingMapper<T> {
	
	/**
	 * Maps source to Lucene document of the same type like source.
	 * For example: If source is Agent then maps lucene document of agent
	 * @param source One of database sources (Cultural Heritage Object, Agent, Concept,...)
	 */
	Document mapFromSource(T source, Date created);
	
	/**
	 * Maps lucene document to database source object (Cultural Heritage Object, Agent, Concept,...)
	 * @param luceneDocument Lucene document from index 
	 */
	T mapToSource(Document luceneDocument, ProfileType profile);
	
	/**
	 * Configure facets config. Setting multivalued fields,...
	 * @param config Facets config
	 */
	void configureFacets(FacetsConfig config);
}
