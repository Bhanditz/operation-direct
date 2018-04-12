package eu.europeana.direct.legacy.index;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

public class LuceneQuery {
	
	/**
	 * Returns CHO ID of the last document in index	 
	 * @throws IOException
	 */
	public long getLastDocumentChoId() throws IOException{
		
		IndexReader indexReader = null;
		TaxonomyReader taxoReader = null;
		
		try {
			indexReader = IndexManager.getInstance().getIndexReaderInstance();
            taxoReader = IndexManager.getInstance().getTaxonomyReaderInstance();
            IndexSearcher searcher = new IndexSearcher(indexReader);
            
            // search only in cultural heritage object documents
            Term term = new Term("object_type","culturalheritageobject");
            Query query = new TermQuery(term);

            // search for document with highest choid
            TopDocs topDocs = searcher.search(query,1,new Sort(new SortField("choid", SortField.Type.STRING,true)));

			ScoreDoc[] hits = topDocs.scoreDocs;
			
			for(int i=0; i < hits.length; i++){
				Document doc = searcher.doc(hits[i].doc);
				// return CHO ID of found document
				return Long.parseLong(doc.get("choidString"));
			}				
		
		}finally{			
			IndexManager.getInstance().releaseReaderInstance(indexReader);
			IndexManager.getInstance().releaseTaxonomyReaderInstance(taxoReader);						
		}		
		
		return 0L;
	}
	
}
