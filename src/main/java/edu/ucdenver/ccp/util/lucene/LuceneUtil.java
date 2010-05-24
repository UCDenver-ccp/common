package edu.ucdenver.ccp.util.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * This is a utility class for creating, editing, and populating Lucene indexes.
 * 
 * @author Bill Baumgartner
 * 
 */
public class LuceneUtil {
	public static final Version DEFAULT_VERSION = Version.LUCENE_30;

	private static final Logger logger = Logger.getLogger(LuceneUtil.class);

	public static enum Overwrite {
		YES(true), NO(false);

		private final boolean overwrite;

		private Overwrite(boolean overwrite) {
			this.overwrite = overwrite;
		}

		public boolean overwrite() {
			return overwrite;
		}

	}

	public static enum DirectoryType {
		FILESYSTEM(false), RAM(true);

		private final boolean useRamDirectory;

		private DirectoryType(boolean useRamDirectory) {
			this.useRamDirectory = useRamDirectory;
		}

		public boolean useRamDirectory() {
			return useRamDirectory;
		}

	}

	private final FSDirectory indexDirectory;
	private final Analyzer analyzer;
	private final Version version;
	public Version getVersion() {
		return version;
	}

	private RAMDirectory ramDirectory = null;

	private IndexWriter writer;

	// private int mergeFactor = 1000;
	//
	// private int addThreshold = 100000;

	/**
	 * Construct a new IndexMaintainer using the default Analyzer class, the StandardAnalyzer.
	 * 
	 * @param indexDirectory
	 *            the location for the Lucene index
	 * @throws IOException
	 */
	public LuceneUtil(File indexDirectory) throws IOException {
		this.indexDirectory = FSDirectory.open(indexDirectory);
		this.analyzer = new StandardAnalyzer(DEFAULT_VERSION);
		this.version = DEFAULT_VERSION;
	}

	/**
	 * Construct a new IndexMaintainer using the given Analyzer class.
	 * 
	 * @param indexLocation
	 *            the location of the Lucene index
	 * @param analyzer
	 *            a Lucene Analyzer
	 * @throws IOException
	 */
	public LuceneUtil(Version version, File indexDirectory, Analyzer analyzer) throws IOException {
		this.indexDirectory = FSDirectory.open(indexDirectory);
		this.analyzer = analyzer;
		this.version = version;
	}

	/**
	 * populates a new index
	 * 
	 * @param documentFeeder
	 * @param directoryType
	 * @param optimize
	 * @throws IOException
	 */
	public void populateNewIndex(DocumentFeeder documentFeeder, DirectoryType directoryType, boolean optimize)
			throws IOException {
		addToIndex(directoryType, documentFeeder, Overwrite.YES, optimize);
	}

	/**
	 * Adds to an existing index
	 * 
	 * @param documentFeeder
	 * @param directoryType
	 * @param optimize
	 * @throws IOException
	 */
	public void populateExistingIndex(DocumentFeeder documentFeeder, DirectoryType directoryType, boolean optimize)
			throws IOException {
		addToIndex(directoryType, documentFeeder, Overwrite.NO, optimize);
	}

	/**
	 * Returns the number of documents stored in the index
	 * 
	 * @return
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	public int getIndexedDocumentCount() throws CorruptIndexException, LockObtainFailedException, IOException {
		IndexReader indexReader = IndexReader.open(indexDirectory);
		int indexedDocumentCount = indexReader.numDocs();
		indexReader.close();
		return indexedDocumentCount;
	}

	/**
	 * Deletes records from the index based on the input ID field
	 * 
	 * @param idFieldName
	 * @param idList
	 * @throws IOException
	 * @throws ParseException
	 */
	public void deleteFromIndex(String idFieldName, List<String> idList) throws IOException, ParseException {
		IndexWriter indexWriter = new IndexWriter(indexDirectory, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
		int index = 0;
		Term[] termsToDelete = new Term[idList.size()];
		for (String id : idList) {
			termsToDelete[index++] = new Term(idFieldName, id);
		}
		indexWriter.deleteDocuments(termsToDelete);
		indexWriter.close();
	}

//	/**
//	 * Adds a single document to the index
//	 * 
//	 * @param documentToAdd
//	 * @throws IOException
//	 * @throws IllegalStateException
//	 *             - if the writer has not been initialized.
//	 */
//	private void addSingleDocumentToIndex(Document documentToAdd) throws IOException, IllegalStateException {
//		if (writer != null) {
//			writer.addDocument(documentToAdd);
//		} else {
//			throw new IllegalStateException(
//					"Cannot add documents to index before the index has been opened. Try openIndex()");
//		}
//	}

	/**
	 * Adds the documents in a DocumentFeeder to the index
	 * 
	 * @param df
	 * @throws IllegalStateException
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void addDocumentsToIndex(DocumentFeeder df) throws IllegalStateException, CorruptIndexException, IOException {
		if (writer != null) {
			while (df.hasNext()) {
				writer.addDocument(df.next());
			}
		} else {
			throw new IllegalStateException(
					"Cannot add documents to index before the index has been opened. Try openIndex()");
		}
	}

	/**
	 * Adds documents to the index
	 * 
	 * @param directoryType
	 * @param documentFeeder
	 * @param overwrite
	 * @param optimize
	 * @throws IOException
	 */
	public void addToIndex(LuceneUtil.DirectoryType directoryType, DocumentFeeder documentFeeder,
			LuceneUtil.Overwrite overwrite, boolean optimize) throws IOException {
		logger.debug(String.format("Adding to index. Type is RAM: %b", directoryType.useRamDirectory));
		openIndex(directoryType, overwrite);
		addDocumentsToIndex(documentFeeder);
		closeIndex(directoryType, optimize);
	}

	/**
	 * Opens an index for adding to it
	 * 
	 * @param directoryType
	 * @param overwrite
	 * @throws IOException
	 */
	private void openIndex(LuceneUtil.DirectoryType directoryType, LuceneUtil.Overwrite overwrite) throws IOException {
		logger.debug("Index is new: " + overwrite);
		Directory directory = indexDirectory;
		if (directoryType.useRamDirectory()) {
			if (overwrite.overwrite()) {
				ramDirectory = new RAMDirectory();
			} else {
				ramDirectory = new RAMDirectory(indexDirectory);
			}
			directory = ramDirectory;
		}
		writer = new IndexWriter(directory, analyzer, overwrite.overwrite(), IndexWriter.MaxFieldLength.LIMITED);
		writer.setMergeFactor(1000);
	}

	/**
	 * closes an index
	 * 
	 * @param directoryType
	 * @param optimize
	 * @throws IOException
	 */
	private void closeIndex(LuceneUtil.DirectoryType directoryType, boolean optimize) throws IOException {
		logger.info("Closing index.");
		if (optimize) {
			logger.info("Optimizing...");
			writer.optimize();
		}
		if (directoryType.useRamDirectory()) {
			logger.debug(String.format("Writing RAM directory to disk... Num Docs: %d", writer.numDocs()));
			IndexWriter fsWriter = new IndexWriter(indexDirectory, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
			fsWriter.addIndexes(writer.getReader());
			writer.close();
			fsWriter.close();
		} else {
			writer.close();
		}
	}

	/**
	 * Merges the input indexes into a newly created directory using the input Analyzer
	 * 
	 * @param mergedIndexDirectory
	 * @param analyzer
	 * @param directoriesOfIndexesToMerge
	 * @throws IOException
	 */
	public static void mergeIndexes(File mergedIndexDirectory, Analyzer analyzer, File... directoriesOfIndexesToMerge)
			throws IOException {
		IndexWriter indexWriter = null;
		IndexReader[] indexReaders = null;
		try {
			FSDirectory mergedDir = FSDirectory.open(mergedIndexDirectory);
			indexWriter = new IndexWriter(mergedDir, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
			indexReaders = initializeIndexReaders(directoriesOfIndexesToMerge);
			indexWriter.addIndexes(indexReaders);
			indexWriter.optimize();
		} finally {
			cleanUp(indexWriter, indexReaders);
		}
	}

	/**
	 * Closes the input IndexWriter and IndexReaders
	 * 
	 * @param indexWriter
	 * @param indexReaders
	 * @throws IOException
	 */
	private static void cleanUp(IndexWriter indexWriter, IndexReader[] indexReaders) throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
		if (indexReaders != null) {
			for (IndexReader indexReader : indexReaders) {
				if (indexReader != null) {
					indexReader.close();
				}
			}
		}
	}

	/**
	 * Returns an array of IndexReader objects initialized to the input index directories parameter
	 * 
	 * @param indexDirectories
	 * @return
	 * @throws IOException
	 */
	private static IndexReader[] initializeIndexReaders(File... indexDirectories) throws IOException {
		List<IndexReader> indexReaders = new ArrayList<IndexReader>();
		for (File indexDirectory : indexDirectories) {
			FSDirectory fsDirectory = FSDirectory.open(indexDirectory);
			indexReaders.add(IndexReader.open(fsDirectory));
		}
		return indexReaders.toArray(new IndexReader[indexReaders.size()]);
	}

}