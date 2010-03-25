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
public class LuceneSearch  {
	private static final Logger logger = Logger.getLogger(LuceneSearch.class);

	private final Directory indexDirectory;

	private TopDocs searchResults;

	// protected String[] fieldValues;
	//
	// protected float[] fieldScores;
	//
	// private int numHits = -1;
	//
	// protected List<String[]> returnedFieldValues;

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

	// /**
	// * Searches a Lucene index returns the hits as an array of Strings containing the given field
	// * values.
	// *
	// * @param searchCriteria
	// * a Lucene search query
	// * @param field
	// * the field whose values are to be returned if search hits are found
	// */
	// public String[] returnField(String searchCriteria, String field) throws IOException {
	// search(searchCriteria, field, false);
	// return fieldValues;
	// }
	//
	// /**
	// * Searches a Lucene index and returns the number of hits obtained for the given search query
	// *
	// * @param searchCriteria
	// * a Lucene search query
	// *
	// */
	// public int returnCount(String searchCriteria) {
	// search(searchCriteria, "", false);
	// System.out.println("Returning the count...");
	// return numHits;
	// }
	//
	// /**
	// * Returns the scores for the hits of the last search
	// *
	// * @return an array of scores
	// */
	// public float[] returnScores() {
	// return fieldScores;
	// }
	//
	// /**
	// * Return the field values of hits for the most recent search. The ArrayList contains String
	// * arrays containing the field values.
	// *
	// * @return an ArryList of field values in the form of String arrays
	// */
	// public List<String[]> returnFieldValues() {
	// return returnedFieldValues;
	// }

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

	// protected void search(String searchCriteria, String returnField, boolean returnTopHitOnly) {
	// debug("return top hit only: " + returnTopHitOnly);
	// try {
	// // searchCriteria = "pmid:2214149 OR pmid:9430188 OR pmid:[1932583
	// // TO 1932589]";
	// // String searchCriteria = "phosphorylate";
	// if (DEBUG) {
	// System.out.println("Searching for matches in lucene...");
	// System.out.print("Init IndexSearcher...");
	// }
	// IndexSearcher is = new IndexSearcher(indexDirectory);
	// // if (DEBUG) {
	// // System.out.println("done.");
	// // System.out.print("Init Analyzer...");
	// // }
	// // Analyzer analyzer = new StandardAnalyzer();
	// if (DEBUG) {
	// System.out.println("done.");
	// System.out.print("Init QueryParser...");
	// }
	// QueryParser parser = new QueryParser("abstract_text", analyzer);
	// if (DEBUG) {
	// System.out.println("done.");
	// System.out.print("Init Query...");
	// }
	// Query query = parser.parse(searchCriteria);
	// if (DEBUG) {
	// System.out.println("done.");
	// System.out.print("Init Hits...");
	// }
	// Hits searchHits = null;
	// try {
	// debug("Query: " + query.toString());
	// // System.out.println("MAX DOC: " + is.maxDoc());
	// // QueryFilter filter = new QueryFilter(parser.parse("pmid:[0 TO
	// // 10000000]"));
	// searchHits = is.search(query);
	//
	// debug("done.");
	//
	// if (returnTopHitOnly) {
	// fieldValues = new String[1];
	// fieldScores = new float[1];
	// if (searchHits.length() > 0) {
	// Document doc = searchHits.doc(0);
	// fieldScores[0] = searchHits.score(0);
	// fieldValues[0] = doc.getField(returnField).stringValue();
	// } else {
	// fieldValues[0] = "-1";
	// fieldScores[0] = -1.0f;
	// }
	// } else {
	//
	// numHits = searchHits.length();
	//
	// debug("# hits returned: " + numHits);
	// if (!returnField.equals("")) {
	// fieldValues = new String[searchHits.length()];
	// fieldScores = new float[searchHits.length()];
	//
	// for (int i = 0; i < searchHits.length(); i++) {
	// Document doc = searchHits.doc(i);
	// debug("hit: " + doc.toString());
	// try {
	// fieldScores[i] = searchHits.score(i);
	// fieldValues[i] = doc.getField(returnField).stringValue();
	// } catch (NullPointerException npe) {
	// fieldValues[i] = "-1";
	// fieldScores[i] = -1.0f;
	// }
	//
	// }
	// }
	// }
	// } catch (Exception e) {
	// System.out.println("Exception while getting hits -------------------------------");
	// e.printStackTrace();
	// System.exit(0);
	// }
	//
	// is.close();
	// } catch (Exception e) {
	// System.err.println("exception while searching index...");
	// e.printStackTrace();
	// }
	//
	// }
	//
	// protected void search(String searchCriteria, String[] returnFields) {
	// try {
	// IndexSearcher is = new IndexSearcher(indexDirectory);
	// QueryParser parser = new QueryParser("abstract_text", analyzer);
	// Query query = parser.parse(searchCriteria);
	// Hits searchHits = null;
	// try {
	// searchHits = is.search(query);
	// numHits = searchHits.length();
	//
	// returnedFieldValues = new ArrayList<String[]>();
	// fieldScores = new float[searchHits.length()];
	// String[] indivFieldValues = new String[1];
	// for (int i = 0; i < searchHits.length(); i++) {
	// /* For each search hit, grab the corresponding document */
	// Document doc = searchHits.doc(i);
	// /* update the scores array for that document */
	// fieldScores[i] = searchHits.score(i);
	//
	// for (int j = 0; j < returnFields.length; j++) {
	// try {
	// Field[] fields = doc.getFields(returnFields[j]);
	// indivFieldValues = new String[fields.length];
	// for (int k = 0; k < fields.length; k++) {
	// indivFieldValues[k] = fields[k].stringValue();
	// }
	// } catch (NullPointerException npe) {
	// indivFieldValues = new String[0];
	// fieldScores[i] = -1.0f;
	// }
	// returnedFieldValues.add(indivFieldValues);
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// System.out.println("Exception while getting hits -------------------------------");
	// e.printStackTrace();
	// System.exit(0);
	// }
	//
	// is.close();
	// } catch (Exception e) {
	// System.err.println("exception while searching index...");
	// e.printStackTrace();
	// }
	//
	// }
	//
	// /**
	// * Return the Lucene documents resulting from a search
	// *
	// * @param searchCriteria
	// */
	// protected Hits getDocuments(String searchCriteria) {
	// try {
	// IndexSearcher is;
	//
	// is = new IndexSearcher(indexDirectory);
	//
	// QueryParser parser = new QueryParser("abstract_text", analyzer);
	// Query query;
	// query = parser.parse(searchCriteria);
	//
	// Hits searchHits = null;
	// searchHits = is.search(query);
	// is.close();
	// return searchHits;
	// } catch (ParseException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// protected List<Map<String, List<String>>> search(String searchCriteria, String[]
	// returnFields,
	// int numResultsToReturn) {
	// try {
	// List<Map<String, List<String>>> hitOutput = new ArrayList<Map<String, List<String>>>();
	// IndexSearcher is = new IndexSearcher(indexDirectory);
	// QueryParser parser = new QueryParser("abstract_text", analyzer);
	// Query query = parser.parse(searchCriteria);
	// Hits searchHits = null;
	// try {
	// searchHits = is.search(query);
	// numHits = searchHits.length();
	// fieldScores = new float[searchHits.length()];
	// int numToReturn = Math.min(numHits, numResultsToReturn);
	// for (int i = 0; i < numToReturn; i++) {
	// /* For each search hit, grab the corresponding document */
	// Document doc = searchHits.doc(i);
	// /* update the scores array for that document */
	// fieldScores[i] = searchHits.score(i);
	//
	// /* create a new HashMap to hold the output */
	// Map<String, List<String>> hitValuesMap = new HashMap<String, List<String>>();
	// for (String fieldName : returnFields) {
	// /*
	// * for each of the return fields, get the field value(s), add them to a
	// * list, then add the list to the hitValuesMap with the key being the field
	// * name
	// */
	// try {
	// Field[] fields = doc.getFields(fieldName);
	// List<String> valuesList = new ArrayList<String>();
	// for (Field field : fields) {
	// valuesList.add(field.stringValue());
	// }
	// hitValuesMap.put(fieldName, valuesList);
	// } catch (NullPointerException npe) {
	// List<String> valuesList = new ArrayList<String>();
	// hitValuesMap.put(fieldName, valuesList);
	// fieldScores[i] = -1.0f;
	// }
	// }
	// hitOutput.add(hitValuesMap);
	// }
	// } catch (Exception e) {
	// System.out.println("Exception while getting hits -------------------------------");
	// e.printStackTrace();
	// System.exit(0);
	// return null;
	// }
	// is.close();
	// return hitOutput;
	// } catch (Exception e) {
	// System.err.println("exception while searching index...");
	// e.printStackTrace();
	// return null;
	// }
	//
	// }
	//
	// public static void printResults(List<Map<String, List<String>>> outputList, int
	// maxNumToReturn, PrintStream ps) {
	// ps.println("Search results..." + outputList.size());
	// StringBuffer outputStr = new StringBuffer();
	// int numToReturn = Math.min(maxNumToReturn, outputList.size());
	// for (int i = 0; i < numToReturn; i++) {
	// Map<String, List<String>> hitMap = outputList.get(i);
	// Set<String> keys = hitMap.keySet();
	// List<String> keyList = Arrays.asList(keys.toArray(new String[0]));
	// Collections.sort(keyList);
	// Collections.reverse(keyList);
	//
	// for (String key : keyList) {
	// outputStr.append("  " + key + ":");
	// String valueStr = "";
	// List<String> values = hitMap.get(key);
	// for (String value : values) {
	// valueStr += (value + ", ");
	// }
	// valueStr = valueStr.substring(0, valueStr.length() - 2);
	// outputStr.append(valueStr);
	// }
	// outputStr.append("\n");
	//
	// }
	//
	// ps.println(outputStr.toString());
	//
	// }

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
	public String printIndexStatistics() throws CorruptIndexException, IOException {
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
			System.out.println(search.printIndexStatistics());

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
