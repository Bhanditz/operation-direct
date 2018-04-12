package eu.europeana.direct.legacy.index;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;

import eu.europeana.direct.configuration.AppPropertyProducer;

public class IndexManager {
	
	final static Logger logger = Logger.getLogger(IndexManager.class);	
	private static IndexManager instance = null;
	
	// index paths
	private static java.nio.file.Path indexDirectoryPath = null;
	private static java.nio.file.Path taxonomyDirectoryPath = null;
	private static java.nio.file.Path entitiesIndexDirectoryPath = null;
	private static java.nio.file.Path removedRecordsIndexDirectoryPath = null;
	
	// index directories
	private static FSDirectory indexDirectory = null;
	private static FSDirectory taxonomyDirectory = null;
	private static FSDirectory entitiesIndexDirectory = null;
	private static FSDirectory removedRecordsIndexDirectory = null;
	
	// index writers	
	private static IndexWriter indexWriter = null;
	private static DirectoryTaxonomyWriter taxonomyWriter = null;
	private static IndexWriter entitiesIndexWriter = null;
	private static IndexWriter removedRecordsIndexWriter = null;

	// index readers
	private static IndexReader indexReader = null;
	private static IndexReader entitiesIndexReader = null;	
	private static TaxonomyReader taxonomyReader = null;
	private static IndexReader removedRecordsIndexReader = null;

	// old readers
	private static Map<IndexReader, Integer> oldReaders = new HashMap<IndexReader, Integer>();
	private static Map<IndexReader, Integer> oldEntityReaders = new HashMap<IndexReader, Integer>();
	private static Map<TaxonomyReader, Integer> oldTaxonomyReaders = new HashMap<TaxonomyReader, Integer>();
	private static Map<IndexReader, Integer> oldRemovedRecordsReaders = new HashMap<IndexReader, Integer>();

	
	// number of current reader users
	private static int currentReaderUsers = 0;
	private static int currentEntityReaderUsers = 0;
	private static int currentTaxonomyReaderUsers = 0;
	private static int currentRemovedRecordsReaderUsers = 0;

	
	// index config
	private FacetsConfig config = new FacetsConfig();
	private boolean indexingRunning = false;	
	private boolean indexLocked = false;
	private static StandardAnalyzer analyzer = null;			
	private static StandardAnalyzer analyzer2 = null;			

	private IndexManager() {
		// read index path from server configuration file
		indexDirectoryPath = Paths.get(AppPropertyProducer.getInstance().getPropertyByName("index_path"));
		taxonomyDirectoryPath = Paths.get(AppPropertyProducer.getInstance().getPropertyByName("taxonomy_path"));
		entitiesIndexDirectoryPath = Paths.get(AppPropertyProducer.getInstance().getPropertyByName("entity_index_path"));
		removedRecordsIndexDirectoryPath = Paths.get(AppPropertyProducer.getInstance().getPropertyByName("deleted_records_index_path"));
	}

	public boolean isIndexLocked() {
		return indexLocked;
	}

	public void setIndexLocked(boolean indexLocked) {
		this.indexLocked = indexLocked;
	}

	public FacetsConfig getConfig() {
		return config;
	}

	public boolean isIndexingRunning() {
		return indexingRunning;
	}

	public void setIndexingRunning(boolean indexingRunning) {
		this.indexingRunning = indexingRunning;
	}

	public void setConfig(FacetsConfig config) {
		this.config = config;
	}

	/**
	 * Method commits index writer, entity-index writer and toxonomy writer if they exists
	 * @throws IOException
	 */
	public void commit(boolean forceMergeDeletes) throws IOException {
		
		if (taxonomyWriter != null) {			
			taxonomyWriter.commit();
		}
		if (indexWriter != null && indexWriter.isOpen()) {			
			indexWriter.commit();			
			if(forceMergeDeletes){				
				indexWriter.forceMergeDeletes();	
			}
			
			indexWriter.flush();
		}
		if (entitiesIndexWriter != null && entitiesIndexWriter.isOpen()) {			
			entitiesIndexWriter.commit();			
			if(forceMergeDeletes){				
				entitiesIndexWriter.forceMergeDeletes();	
			}			
			entitiesIndexWriter.flush();
		}
	}
	
	/**
	 * Method commits removed-records index writer
	 * @throws IOException
	 */
	public void commitOthers() throws IOException {
		
		if (removedRecordsIndexWriter != null && removedRecordsIndexWriter.isOpen()) {						
			removedRecordsIndexWriter.commit();			
			removedRecordsIndexWriter.forceMergeDeletes();
			removedRecordsIndexWriter.flush();
		}
	}

	/**
	 * 
	 * @return IndexManager instance
	 */
	public static final synchronized IndexManager getInstance() {

		if (instance == null) {
			instance = new IndexManager();
		}
		return instance;
	}

	/**
	 * Method opens index writer, entities writer, and toxonomy writer
	 */
	public synchronized void ensureOpenWriter() {
		
		analyzer = new StandardAnalyzer();
		
		try {
			indexDirectory = MMapDirectory.open(indexDirectoryPath);	
			entitiesIndexDirectory = MMapDirectory.open(entitiesIndexDirectoryPath);
			taxonomyDirectory = MMapDirectory.open(taxonomyDirectoryPath);
			
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			indexWriterConfig.setCommitOnClose(true);			
			indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);

			IndexWriterConfig entitiesIndexWriterConfig = new IndexWriterConfig(analyzer);
			entitiesIndexWriterConfig.setCommitOnClose(true);			
			entitiesIndexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
					
			
			if (indexWriter == null || !indexWriter.isOpen()) {
				indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
			}
			
			if (entitiesIndexWriter == null || !entitiesIndexWriter.isOpen()) {
				entitiesIndexWriter = new IndexWriter(entitiesIndexDirectory, entitiesIndexWriterConfig);
			}						

			if (taxonomyWriter == null) {
				taxonomyWriter = new DirectoryTaxonomyWriter(taxonomyDirectory);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * Method opens index for removed records
	 */
	public synchronized void ensureOpenWriterForOther() {
		analyzer2 = new StandardAnalyzer();

		try{
			removedRecordsIndexDirectory = MMapDirectory.open(removedRecordsIndexDirectoryPath);

			
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer2);
			indexWriterConfig.setCommitOnClose(true);			
			indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			
			if (removedRecordsIndexWriter == null || !removedRecordsIndexWriter.isOpen()) {
				removedRecordsIndexWriter = new IndexWriter(removedRecordsIndexDirectory, indexWriterConfig);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}				
	}

	/**
	 * 
	 * @return IndexReader instance of index reader
	 * @throws IOException
	 */
	public synchronized IndexReader getIndexReaderInstance() throws IOException {

		if (indexReader == null) {
			if (indexWriter == null || !indexWriter.isOpen() || taxonomyWriter == null) {
				ensureOpenWriter();
			}

			indexReader = DirectoryReader.open(indexWriter);

			if (taxonomyReader != null) {
				try {
					taxonomyReader.close();
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
			taxonomyReader = new DirectoryTaxonomyReader(taxonomyWriter);

		} else {
			DirectoryReader newReader;

			try {
				newReader = DirectoryReader.openIfChanged((DirectoryReader) indexReader, indexWriter, true);
			} catch (AlreadyClosedException a) {
				if (indexWriter == null || !indexWriter.isOpen()) {
					ensureOpenWriter();
				}
				newReader = DirectoryReader.open(indexWriter);
			}
			if (newReader != null) {
				if (currentReaderUsers == 0) {
					indexReader.close();
				} else {
					oldReaders.put(indexReader, currentReaderUsers);
				}

				currentReaderUsers = 0;
				indexReader = newReader;
			}

			TaxonomyReader newTaxonomyReader;
			try {
				newTaxonomyReader = DirectoryTaxonomyReader.openIfChanged(taxonomyReader);
			} catch (AlreadyClosedException a) {
				if (taxonomyWriter == null) {
					ensureOpenWriter();
				}
				newTaxonomyReader = new DirectoryTaxonomyReader(taxonomyWriter);
			}

			if (newTaxonomyReader != null) {
				if (currentTaxonomyReaderUsers == 0) {
					taxonomyReader.close();
				} else {
					oldTaxonomyReaders.put(taxonomyReader, currentTaxonomyReaderUsers);
				}
				currentTaxonomyReaderUsers = 0;
				taxonomyReader = newTaxonomyReader;
			}
		}

		currentReaderUsers++;
		return indexReader;
	}		
	
	/**
	 * Method releases reader instance after done working with main index	 
	 * @throws IOException
	 */
	public synchronized void releaseReaderInstance(IndexReader readerToRelease) throws IOException {
		if (readerToRelease == indexReader) {
			currentReaderUsers--;
		} else {
			int readerUsers = 0;
			Integer oldReadersNum = oldReaders.get(readerToRelease);
			if (oldReadersNum != null) {
				readerUsers = oldReadersNum;
				readerUsers--;
				if (readerUsers <= 0) {
					oldReaders.remove(readerToRelease);
					readerToRelease.close();
				} else {
					oldReaders.put(readerToRelease, readerUsers);
				}
			}
		}
	}

	/**
	 * 
	 * @return TaxonomyReader instance of taxonomy reader
	 * @throws IOException
	 */
	public synchronized TaxonomyReader getTaxonomyReaderInstance() throws IOException {

		getIndexReaderInstance();
		currentReaderUsers--;
		currentTaxonomyReaderUsers++;
		return taxonomyReader;
	}
	
	/**
	 * Method releases taxonomy reader instance after done working with it	 
	 * @throws IOException
	 */
	public synchronized void releaseTaxonomyReaderInstance(TaxonomyReader readerToRelease) throws IOException {

		if (readerToRelease == taxonomyReader) {
			currentTaxonomyReaderUsers--;
		} else {

			int readerUsers = 0;
			Integer oldReadersTaxonomyNum = oldTaxonomyReaders.get(readerToRelease);

			if (oldReadersTaxonomyNum != null) {
				readerUsers = oldReadersTaxonomyNum;
				readerUsers--;
				if (readerUsers <= 0) {
					oldTaxonomyReaders.remove(readerToRelease);
					readerToRelease.close();
				} else {
					oldTaxonomyReaders.put(readerToRelease, readerUsers);
				}
			}
		}
	}
	
	/**
	 * 
	 * @return IndexReader instance of entities index reader
	 * @throws IOException
	 */
	public synchronized IndexReader getEntitiesIndexReaderInstance() throws IOException {
		
		if(entitiesIndexReader == null){
						
			//If we have no previous instances and we have no writer available, make sure we create a writer
            if (entitiesIndexWriter == null || !entitiesIndexWriter.isOpen() || taxonomyWriter == null)
            {
                ensureOpenWriter();
            }

            //We create a new reader based either on the new or an existing writer
            entitiesIndexReader = DirectoryReader.open(entitiesIndexWriter);
            
            if (taxonomyReader != null) {
				try {
					taxonomyReader.close();
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
			taxonomyReader = new DirectoryTaxonomyReader(taxonomyWriter);            
            
		} else {			
			//We already have a previous instance of the reader. Make sure it contains all of the recent NRTS changes
            DirectoryReader newReader;
            try
            {            	
                newReader = DirectoryReader.openIfChanged((DirectoryReader)entitiesIndexReader, entitiesIndexWriter, true);
            }
            catch (AlreadyClosedException e)
            {
                if (entitiesIndexWriter == null || !entitiesIndexWriter.isOpen())
                {
                    ensureOpenWriter();
                }
                newReader = DirectoryReader.open(entitiesIndexWriter);
            }

            if (newReader != null) {
				if (currentEntityReaderUsers == 0) {
					entitiesIndexReader.close();
				} else {
					oldEntityReaders.put(entitiesIndexReader, currentEntityReaderUsers);
				}

				currentEntityReaderUsers = 0;
				entitiesIndexReader = newReader;
			}
		}

		currentEntityReaderUsers++;
		return entitiesIndexReader;
	}
	
	public synchronized IndexReader getRemovedRecordsIndexReaderInstance() throws IOException {
		
		if(removedRecordsIndexReader == null){
						
			//If we have no previous instances and we have no writer available, make sure we create a writer
            if (removedRecordsIndexWriter == null || !removedRecordsIndexWriter.isOpen())
            {
            	ensureOpenWriterForOther();
            }

            //We create a new reader based either on the new or an existing writer
            removedRecordsIndexReader = DirectoryReader.open(removedRecordsIndexWriter);
                             
            
		} else {			
			//We already have a previous instance of the reader. Make sure it contains all of the recent NRTS changes
            DirectoryReader newReader;
            try
            {            	            	
                newReader = DirectoryReader.openIfChanged((DirectoryReader)removedRecordsIndexReader, removedRecordsIndexWriter, true);
            }
            catch (AlreadyClosedException e)
            {
                if (removedRecordsIndexWriter == null || !removedRecordsIndexWriter.isOpen())
                {
                	ensureOpenWriterForOther();
                }
                newReader = DirectoryReader.open(removedRecordsIndexWriter);
            }

            if (newReader != null) {
				if (currentRemovedRecordsReaderUsers == 0) {
					removedRecordsIndexReader.close();
				} else {
					oldRemovedRecordsReaders.put(removedRecordsIndexReader, currentRemovedRecordsReaderUsers);
				}

				currentRemovedRecordsReaderUsers = 0;
				removedRecordsIndexReader = newReader;
			}
		}

		currentRemovedRecordsReaderUsers++;
		return removedRecordsIndexReader;
	}
	
	/**
	 * Method releases entities index reader instance after done working with it	 
	 * @throws IOException
	 */
	public synchronized void releaseEntitiesReaderInstance(IndexReader readerToRelease) throws IOException {
		if (readerToRelease == entitiesIndexReader) {
			currentEntityReaderUsers--;
		} else {
			int readerUsers = 0;
			Integer oldReadersNum = oldEntityReaders.get(readerToRelease);
			if (oldReadersNum != null) {
				readerUsers = oldReadersNum;
				readerUsers--;
				if (readerUsers <= 0) {
					oldEntityReaders.remove(readerToRelease);
					readerToRelease.close();
				} else {
					oldEntityReaders.put(readerToRelease, readerUsers);
				}
			}
		}
	}
	
	/**
	 * Method releases removed-records index reader instance after done working with it	 
	 * @throws IOException
	 */
	public synchronized void releaseRemovedRecordsReaderInstance(IndexReader readerToRelease) throws IOException {
		if(readerToRelease == removedRecordsIndexReader) {
			currentRemovedRecordsReaderUsers--;
		} else {
			int readerUsers = 0;
			Integer oldReadersNum = oldRemovedRecordsReaders.get(readerToRelease);
			if(oldReadersNum != null){
				readerUsers = oldReadersNum;
				readerUsers--;
				if(readerUsers <= 0){
					oldRemovedRecordsReaders.remove(readerToRelease);
					readerToRelease.close();
				} else {
					oldRemovedRecordsReaders.put(readerToRelease, readerUsers);
				}
			}
		}
	}

	/**
	 * Adds document to main index
	 * @param document Lucene document for CHO object
	 * @throws IOException
	 */
	public void AddDocument(Document document) throws IOException {
		if (indexWriter == null || !indexWriter.isOpen() || taxonomyWriter == null) {
			ensureOpenWriter();
		}
		indexWriter.addDocument(config.build(taxonomyWriter, document));
	}

	/**
	 * Adds document to entity-index
	 * @param document Lucene document for contextual entity
	 * @throws IOException
	 */
	public void AddEntityDocument(Document document) throws IOException {
		if(entitiesIndexWriter == null || !entitiesIndexWriter.isOpen() || taxonomyWriter == null){
			entitiesIndexWriter.addDocument(config.build(taxonomyWriter, document));	
		}		
	}
	
	/**
	 * Adds document to removed-records index
	 * @param document Lucene document for removed record
	 * @throws IOException
	 */
	public void AddRemovedRecordDocument(Document document) throws IOException {
		if(removedRecordsIndexWriter == null || !removedRecordsIndexWriter.isOpen()){
			ensureOpenWriterForOther();
		}			
		removedRecordsIndexWriter.addDocument(config.build(document));
	}

	/**
	 * Updates document in main index
	 * @param chotId Id of object we want to update in index
	 * @param document Lucene document which will be commited	 * 
	 * @throws IOException
	 */
	public void updateDocument(long choId, Document document) throws IOException {
										
		String objectId = String.valueOf(choId);
		Term term = new Term("choidString", objectId);
		
		if (indexWriter == null || !indexWriter.isOpen() || taxonomyWriter == null) {			
			ensureOpenWriter();
		}
		
		
		if (indexingRunning) {
			IndexReader reader = getIndexReaderInstance();
			Query query = new TermQuery(term);

			try {
				IndexSearcher searcher = new IndexSearcher(reader);
				TopDocs existingDocs = searcher.search(query, 1);
				if (existingDocs.totalHits > 0) {

					indexWriter.updateDocument(term, config.build(taxonomyWriter, document));
				}
			} finally {
				releaseReaderInstance(reader);
			}
		} else {			
			indexWriter.updateDocument(term, config.build(taxonomyWriter, document));
		}
	}

	/**
	 * Updates document in entities index
	 * @param entitiyId Id of object we want to update in index
	 * @param document Lucene document which will be commited	 * 
	 * @throws IOException
	 */
	public void updateEntityDocument(long entityId, Document document, LuceneDocumentType entityType) throws IOException{
		
		String objectId = String.valueOf(entityId);
		Term term = null;
		
		switch(entityType){
			case Agent:
				term = new Term("agentIdString", objectId);
				break;
			case Concept:
				term = new Term("conceptIdString", objectId);
				break;
			case Place:
				term = new Term("placeIdString", objectId);
				break;
			case Timespan:
				term = new Term("timespanIdString", objectId);
				break;
			case Weblink:
				term = new Term("weblinkIdString", objectId);
				break;
		}
		
		if (entitiesIndexWriter == null || !entitiesIndexWriter.isOpen() || taxonomyWriter == null) {			
			ensureOpenWriter();
		}
		
		if (indexingRunning) {
			IndexReader reader = getEntitiesIndexReaderInstance();
			Query query = new TermQuery(term);

			try {
				IndexSearcher searcher = new IndexSearcher(reader);
				TopDocs existingDocs = searcher.search(query, 1);
				if (existingDocs.totalHits > 0) {

					entitiesIndexWriter.updateDocument(term, config.build(taxonomyWriter, document));
				}
			} finally {
				releaseEntitiesReaderInstance(reader);
			}
		} else {			
			entitiesIndexWriter.updateDocument(term, config.build(taxonomyWriter, document));
		}
		
	}
	
	/**
	 * Deletes document in index
	 * @param choId ID of object we want to delete in index
	 * @param documentType Lucene document type
	 * @throws IOException
	 */
	public void deleteDocument(long choId, boolean forceMergeDeletes, boolean commitIndex) throws IOException {
		if(choId > 0){			
			try{		
				
				String objectId = String.valueOf(choId);
				Term term = new Term("choidString", objectId);			
				
				if (indexWriter == null || !indexWriter.isOpen() || taxonomyWriter == null) {
					ensureOpenWriter();
				}				
				
				indexWriter.deleteDocuments(term);			
		
			} catch (Exception e){
				e.printStackTrace();
			} finally {
				if(commitIndex){
					commit(forceMergeDeletes);	
				}				
			}			
		}
	}

	/**
	 * Deletes document in entity index
	 * @param entityId ID of object we want to delete in entity index
	 * @param documentType Lucene document type
	 * @throws IOException
	 */
	public void deleteEntityDocument(long entityId, LuceneDocumentType documentType, boolean forceMergeDeletes, boolean commitIndex) throws IOException {
		if(entityId > 0){			
			try{			
				String objectId = String.valueOf(entityId);
				Term term = null;
				
				switch (documentType) {		
				
					case Agent:
						term = new Term("agentIdString", objectId);
						break;
					case Concept:
						term = new Term("conceptIdString", objectId);
						break;
					case Place:
						term = new Term("placeIdString", objectId);
						break;
					case Timespan:
						term = new Term("timespanIdString", objectId);
						break;
					case Weblink:
						term = new Term("weblinkIdString", objectId);
						break;
				}

				if (entitiesIndexWriter == null || !entitiesIndexWriter.isOpen() || taxonomyWriter == null) {
					ensureOpenWriter();
				}
				
				entitiesIndexWriter.deleteDocuments(term);			
		
			} catch (Exception e){
				e.printStackTrace();
			} finally {
				if(commitIndex) {
					commit(forceMergeDeletes);	
				}				
			}			
		}
	}
	
	public void fullOptimize(){
				
		if (indexWriter != null && indexWriter.isOpen()) {			
			try {
				indexWriter.forceMerge(1);
				indexWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}						
		}
		
		if (entitiesIndexWriter != null && entitiesIndexWriter.isOpen()) {			
			try {
				entitiesIndexWriter.forceMerge(1);
				entitiesIndexWriter.commit();
				entitiesIndexWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}						
		}				
	}
}
