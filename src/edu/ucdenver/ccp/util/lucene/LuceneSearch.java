/*
 *
 * The Original Code is UCHSC Utilities.
 *
 * The Initial Developer of the Original Code is University of Colorado.  
 * Copyright (C) 2005.  All Rights Reserved.
 *
 * UCHSC Utilities was developed by the Center for Computational Pharmacology
 * (http://compbio.uchsc.edu) at the University of Colorado Health 
 *  Sciences Center School of Medicine with support from the National 
 *  Library of Medicine.  
 *
 *
 * Contributor(s):
 *   William A. Baumgartner <william.baumgartner@uchsc.edu> (Original Author)
 *   
 * Created: May 2005
 * Dependencies:
 * ToDo: 
 * Changes:
 */
package edu.ucdenver.ccp.util.lucene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.ucdenver.ccp.util.file.FileUtil;
import edu.ucdenver.ccp.util.string.StringBufferUtil;

/**
 * This utility class constitutes a Lucene index search interface.
 * 
 * @author Bill Baumgartner
 * 
 */
public class LuceneSearch {
	private static final Logger logger = Logger.getLogger(LuceneSearch.class);

	private final Directory indexDirectory;

	private TopDocs searchResults;

	protected final Searcher searcher;

	private Analyzer analyzer = new StandardAnalyzer(LuceneUtil.DEFAULT_VERSION);

	private final QueryParser queryParser;

	/**
	 * Construct a new LuceneSearch using the given Lucene Analyzer class.
	 * 
	 * @param indexLocation
	 *            the location of the Lucene index
	 * @param analyzer
	 *            a Lucene Analyzer
	 * @throws IOException
	 */
	public LuceneSearch(File indexDirectory, Analyzer analyzer, String defaultSearchField) throws IOException {
		checkIndexDirectory(indexDirectory);
		this.indexDirectory = FSDirectory.open(indexDirectory);
		this.analyzer = analyzer;
		this.searcher = new IndexSearcher(this.indexDirectory);
		this.queryParser = new QueryParser(LuceneUtil.DEFAULT_VERSION, defaultSearchField, this.analyzer);
	}

	/**
	 * Construct a new LuceneSearch using the default Lucene Analyzer class, the StandardAnalyzer.
	 * 
	 * @param indexLocation
	 *            the location of the Lucene index
	 * @throws IOException
	 */
	public LuceneSearch(File indexDirectory, String defaultSearchField) throws IOException {
		checkIndexDirectory(indexDirectory);
		this.indexDirectory = FSDirectory.open(indexDirectory);
		this.searcher = new IndexSearcher(this.indexDirectory);
		this.queryParser = new QueryParser(LuceneUtil.DEFAULT_VERSION, defaultSearchField, this.analyzer);
	}

	private void checkIndexDirectory(File indexDirectory) throws FileNotFoundException {
		String errorMessage = FileUtil.isDirectoryValid(indexDirectory);
		if (errorMessage != null) {
			throw new FileNotFoundException(errorMessage);
		}
	}

	private int getHitCount() {
		return searchResults.totalHits;
	}

	private void search(String queryString, int topN) throws ParseException, IOException {
		Query query = queryParser.parse(queryString);
		searchResults = searcher.search(query, topN);
	}

	public List<String> search(String queryString, String returnField, int topN) throws ParseException, IOException {
		search(queryString, topN);
		List<String> returnedFields = new ArrayList<String>();
		for (ScoreDoc scoreDoc : searchResults.scoreDocs) {
			Document document = searcher.doc(scoreDoc.doc);
			returnedFields.add(document.get(returnField));
		}
		return returnedFields;
	}

	public void close() throws IOException {
		searcher.close();
	}

	/**
	 * Prints the statistics of the Lucene index, # of documents it contains, as well as the top 45
	 * terms found in the index.
	 * 
	 * @throws IOException
	 * @throws CorruptIndexException
	 * 
	 */
	public String getIndexStatistics() throws CorruptIndexException, IOException {
		StringBuffer sb = new StringBuffer();
		StringBufferUtil.appendLine(sb, "---------------- INDEX STATISTICS ---------------");
		StringBufferUtil.appendLine(sb, String.format("Location: %s", indexDirectory.toString()));
		StringBufferUtil.appendLine(sb, String.format("# Documents: %d", searcher.maxDoc()));
		StringBufferUtil.appendLine(sb, String.format("Index Fields: "));
		IndexReader ir = IndexReader.open(indexDirectory);
		List<String> fieldNames = Collections.list(Collections.enumeration(ir.getFieldNames(FieldOption.INDEXED)));
		Collections.sort(fieldNames);
		for (String fieldName : fieldNames) {
			StringBufferUtil.appendLine(sb, String.format("   %s", fieldName));
		}
		return sb.toString();
	}

	/**
	 * This method used for debugging purposes only.
	 * 
	 * @param args
	 *            args[0]: database directory<br>
	 *            args[1]: search criteria, see Lucene page for format<br>
	 *            args[2]: default search field args[3]: field values to return args[4]: top N to
	 *            return
	 */
	public static void main(String[] args) {
		try {
			File indexDirectory = new File(args[0]);
			String queryString = args[1];
			String defaultSearchField = args[2];
			String fieldToReturn = args[3];
			int topN = Integer.parseInt(args[4]);

			LuceneSearch search = new LuceneSearch(indexDirectory, defaultSearchField);
			System.out.println(search.getIndexStatistics());

			List<String> values = search.search(queryString, fieldToReturn, topN);
			System.out.println(String.format("Returning %d search hits...", search.getHitCount()));
			for (String value : values) {
				System.out.println(value);
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
