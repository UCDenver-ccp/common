/*
 * Copyright (C) 2009 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package edu.ucdenver.ccp.parser.ncbi.gene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.parser.LineFileParser;
import edu.ucdenver.ccp.util.string.StringConstants;

/**
 * This class is used to parse the EntrezGene gene_info file.
 * 
 * @author Bill Baumgartner
 * 
 */
public class EntrezGeneInfoFileParser extends LineFileParser<EntrezGeneInfoFileData> {

	private final static Logger logger = Logger.getLogger(EntrezGeneInfoFileParser.class);
	private static final String COMMENT_INDICATOR = StringConstants.POUND_SIGN;

	public EntrezGeneInfoFileParser(File entrezGeneInfoFile) throws IOException {
		super(entrezGeneInfoFile, COMMENT_INDICATOR);
	}

	public EntrezGeneInfoFileParser(InputStream entrezGeneInfoFileStream) throws IOException {
		super(entrezGeneInfoFileStream, COMMENT_INDICATOR);
	}

	// /**
	// * During the initialization we want to skip the header, determine the character offsets for
	// the column and load the
	// * first record.
	// */
	// @Override
	// protected void initialize() throws IOException {
	// /* skip the first line, it is a comment. */
	// String line = readLine();
	// if (!line.startsWith("#")) {
	// String errorMessage = "Expected comment line, but observed: " + line;
	// logger.error(errorMessage);
	// throw new IOException(errorMessage);
	// }
	// }

	@Override
	protected EntrezGeneInfoFileData parseFileDataFromLine(String line) {
		return parseGeneInfoFileLine(line);
	}

	/*
	 * #Format: tax_id GeneID Symbol LocusTag Synonyms dbXrefs chromosome map_location description
	 * type_of_gene Symbol_from_nomenclature_authority Full_name_from_nomenclature_authority
	 * Nomenclature_status Other_designations Modification_date (tab is used as a separator, pound
	 * sign - start of a comment)
	 */
	/**
	 * Parse a line from the EntrezGene gene_info file
	 * 
	 * @param line
	 * @return
	 */
	public static EntrezGeneInfoFileData parseGeneInfoFileLine(String line) {
		if (!line.startsWith(COMMENT_INDICATOR)) {
			String[] toks = line.split("\\t");
			if (toks.length != 15) {
				logger.error("Unexpected number of tokens (" + toks.length + ") on line:"
						+ line.replaceAll("\\t", " [TAB] "));
			}

			int taxonID = Integer.parseInt(toks[0]);
			int geneID = Integer.parseInt(toks[1]);
			String symbol = toks[2];
			if (symbol.equals("-")) {
				symbol = null;
			}
			String locusTag = toks[3];
			if (locusTag.equals("-")) {
				locusTag = null;
			}
			Set<String> synonyms;
			if (toks[4].equals("-")) {
				synonyms = new HashSet<String>();
			} else {
				synonyms = new HashSet<String>(Arrays.asList(toks[4].split("\\|")));
			}
			Set<String> dbXrefs;
			if (toks[5].equals("-")) {
				dbXrefs = new HashSet<String>();
			} else {
				dbXrefs = new HashSet<String>(Arrays.asList(toks[5].split("\\|")));
			}
			String chromosome = toks[6];
			if (chromosome.equals("-")) {
				chromosome = null;
			}
			String mapLocation = toks[7];
			if (mapLocation.equals("-")) {
				mapLocation = null;
			}
			String description = toks[8];
			if (description.equals("-")) {
				description = null;
			}
			String typeOfGene = toks[9];
			if (typeOfGene.equals("-")) {
				typeOfGene = null;
			}
			String symbolFromNomenclatureAuthority = toks[10];
			if (symbolFromNomenclatureAuthority.equals("-")) {
				symbolFromNomenclatureAuthority = null;
			}
			String fullNameFromNomenclatureAuthority = toks[11];
			if (fullNameFromNomenclatureAuthority.equals("-")) {
				fullNameFromNomenclatureAuthority = null;
			}
			String nomenclatureStatus = toks[12];
			if (nomenclatureStatus.equals("-")) {
				nomenclatureStatus = null;
			}
			Set<String> otherDesignations;
			if (toks[13].equals("-")) {
				otherDesignations = new HashSet<String>();
			} else {
				otherDesignations = new HashSet<String>(Arrays.asList(toks[13].split("\\|")));
			}
			String modificationDate = toks[14];
			if (modificationDate.equals("-")) {
				modificationDate = null;
			}
			return new EntrezGeneInfoFileData(taxonID, geneID, symbol, locusTag, synonyms, dbXrefs, chromosome,
					mapLocation, description, typeOfGene, symbolFromNomenclatureAuthority,
					fullNameFromNomenclatureAuthority, nomenclatureStatus, otherDesignations, modificationDate);

		} else {
			logger.warn("No relevant data to parse on line: " + line);
			return null;
		}
	}

	/**
	 * Returns a map from the gene symbol (3rd column in gene_info file) to the entrez gene id.
	 * 
	 * @param entrezGeneInfoFile
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Set<Integer>> getGeneSymbol2EntrezGeneIDMap(File entrezGeneInfoFile, int taxonID,
			boolean toLowerCase) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(entrezGeneInfoFile);
			return getGeneSymbol2EntrezGeneIDMap(fis, taxonID, toLowerCase);
		} finally {
			fis.close();
		}
	}

	/**
	 * Returns a map from the gene symbol (3rd column in gene_info file) to the entrez gene id.
	 * 
	 * @param entrezGeneInfoFile
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Set<Integer>> getGeneSymbol2EntrezGeneIDMap(InputStream entrezGeneInfoFileStream,
			int taxonID, boolean toLowerCase) throws IOException {
		Map<String, Set<Integer>> geneSymbol2EntrezGeneIDMap = new HashMap<String, Set<Integer>>();
		EntrezGeneInfoFileParser parser = new EntrezGeneInfoFileParser(entrezGeneInfoFileStream);

		while (parser.hasNext()) {
			EntrezGeneInfoFileData dataRecord = parser.next();
			if (dataRecord.getTaxonID() == taxonID) {
				String geneSymbol = dataRecord.getSymbol();
				if (toLowerCase) {
					geneSymbol = geneSymbol.toLowerCase();
				}
				Integer entrezGeneID = dataRecord.getGeneID();
				if (geneSymbol2EntrezGeneIDMap.containsKey(geneSymbol)) {
					geneSymbol2EntrezGeneIDMap.get(geneSymbol).add(entrezGeneID);
				} else {
					Set<Integer> entrezGeneIDs = new HashSet<Integer>();
					entrezGeneIDs.add(entrezGeneID);
					geneSymbol2EntrezGeneIDMap.put(geneSymbol.trim(), entrezGeneIDs);
				}
			}
		}

		return geneSymbol2EntrezGeneIDMap;
	}

	/**
	 * Returns a map from the gene symbol (3rd column in gene_info file) to the entrez gene id.
	 * 
	 * @param entrezGeneInfoFile
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Set<String>> getGeneSymbol2EntrezGeneIDAsStringMap(File entrezGeneInfoFile, int taxonID,
			boolean toLowerCase) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(entrezGeneInfoFile);
			return getGeneSymbol2EntrezGeneIDAsStringMap(fis, taxonID, toLowerCase);
		} finally {
			fis.close();
		}
	}

	/**
	 * Returns a map from the gene symbol (3rd column in gene_info file) to the entrez gene id.
	 * 
	 * @param entrezGeneInfoFile
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Set<String>> getGeneSymbol2EntrezGeneIDAsStringMap(InputStream entrezGeneInfoFileStream,
			int taxonID, boolean toLowerCase) throws IOException {
		Map<String, Set<String>> geneSymbol2EntrezGeneIDMap = new HashMap<String, Set<String>>();
		EntrezGeneInfoFileParser parser = new EntrezGeneInfoFileParser(entrezGeneInfoFileStream);

		while (parser.hasNext()) {
			EntrezGeneInfoFileData dataRecord = parser.next();
			if (dataRecord.getTaxonID() == taxonID) {
				String geneSymbol = dataRecord.getSymbol();
				if (toLowerCase) {
					geneSymbol = geneSymbol.toLowerCase();
				}
				Integer entrezGeneID = dataRecord.getGeneID();
				if (geneSymbol2EntrezGeneIDMap.containsKey(geneSymbol)) {
					geneSymbol2EntrezGeneIDMap.get(geneSymbol).add(entrezGeneID.toString());
				} else {
					Set<String> entrezGeneIDs = new HashSet<String>();
					entrezGeneIDs.add(entrezGeneID.toString());
					geneSymbol2EntrezGeneIDMap.put(geneSymbol.trim(), entrezGeneIDs);
				}
			}
		}

		return geneSymbol2EntrezGeneIDMap;
	}

	/**
	 * Returns a map from the gene symbol including synonyms and nomenclature symbol to the entrez
	 * gene id.
	 * 
	 * @param entrezGeneInfoFileStream
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Set<Integer>> getGeneSymbol2EntrezGeneIDMap_withSynonyms(File entrezGeneInfoFile,
			int taxonID, boolean toLowerCase) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(entrezGeneInfoFile);
			return getGeneSymbol2EntrezGeneIDMap_withSynonyms(fis, taxonID, toLowerCase);
		} finally {
			fis.close();
		}
	}

	/**
	 * Returns a map from the gene symbol including synonyms and nomenclature symbol to the entrez
	 * gene id.
	 * 
	 * @param entrezGeneInfoFileStream
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Set<Integer>> getGeneSymbol2EntrezGeneIDMap_withSynonyms(
			InputStream entrezGeneInfoFileStream, int taxonID, boolean toLowerCase) throws IOException {
		Map<String, Set<Integer>> geneSymbol2EntrezGeneIDMap = new HashMap<String, Set<Integer>>();
		EntrezGeneInfoFileParser parser = new EntrezGeneInfoFileParser(entrezGeneInfoFileStream);

		while (parser.hasNext()) {
			EntrezGeneInfoFileData dataRecord = parser.next();
			if (dataRecord.getTaxonID() == taxonID) {
				Integer entrezGeneID = dataRecord.getGeneID();
				Set<String> geneSymbols = new HashSet<String>();
				geneSymbols.add(dataRecord.getSymbol());
				if (dataRecord.getSymbolFromNomenclatureAuthority() != null) {
					geneSymbols.add(dataRecord.getSymbolFromNomenclatureAuthority());
				}
				geneSymbols.addAll(dataRecord.getSynonyms());

				for (String geneSymbol : geneSymbols) {
					if (geneSymbol == null) {
						System.err.println("Null geneSymbol for ID: " + entrezGeneID);
					}
					if (toLowerCase) {
						geneSymbol = geneSymbol.toLowerCase();
					}
					if (geneSymbol2EntrezGeneIDMap.containsKey(geneSymbol)) {
						geneSymbol2EntrezGeneIDMap.get(geneSymbol).add(entrezGeneID);
					} else {
						Set<Integer> entrezGeneIDs = new HashSet<Integer>();
						entrezGeneIDs.add(entrezGeneID);
						geneSymbol2EntrezGeneIDMap.put(geneSymbol.trim(), entrezGeneIDs);
					}
				}
			}
		}

		return geneSymbol2EntrezGeneIDMap;
	}

	/**
	 * Returns a map from the gene symbol including synonyms and nomenclature symbol to the entrez
	 * gene id.
	 * 
	 * @param entrezGeneInfoFileStream
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Set<String>> getGeneSymbol2EntrezGeneIDAsStringMap_withSynonyms(File entrezGeneInfoFile,
			int taxonID, boolean toLowerCase) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(entrezGeneInfoFile);
			return getGeneSymbol2EntrezGeneIDAsStringMap_withSynonyms(fis, taxonID, toLowerCase);
		} finally {
			fis.close();
		}
	}

	/**
	 * Returns a map from the gene symbol including synonyms and nomenclature symbol to the entrez
	 * gene id.
	 * 
	 * @param entrezGeneInfoFileStream
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Set<String>> getGeneSymbol2EntrezGeneIDAsStringMap_withSynonyms(
			InputStream entrezGeneInfoFileStream, int taxonID, boolean toLowerCase) throws IOException {
		Map<String, Set<String>> geneSymbol2EntrezGeneIDMap = new HashMap<String, Set<String>>();
		EntrezGeneInfoFileParser parser = new EntrezGeneInfoFileParser(entrezGeneInfoFileStream);

		while (parser.hasNext()) {
			EntrezGeneInfoFileData dataRecord = parser.next();
			if (dataRecord.getTaxonID() == taxonID) {
				Integer entrezGeneID = dataRecord.getGeneID();
				Set<String> geneSymbols = new HashSet<String>();
				geneSymbols.add(dataRecord.getSymbol());
				if (dataRecord.getSymbolFromNomenclatureAuthority() != null) {
					geneSymbols.add(dataRecord.getSymbolFromNomenclatureAuthority());
				}
				geneSymbols.addAll(dataRecord.getSynonyms());

				for (String geneSymbol : geneSymbols) {
					if (geneSymbol == null) {
						System.err.println("Null geneSymbol for ID: " + entrezGeneID);
					}
					if (toLowerCase) {
						geneSymbol = geneSymbol.toLowerCase();
					}
					if (geneSymbol2EntrezGeneIDMap.containsKey(geneSymbol)) {
						geneSymbol2EntrezGeneIDMap.get(geneSymbol).add(entrezGeneID.toString());
					} else {
						Set<String> entrezGeneIDs = new HashSet<String>();
						entrezGeneIDs.add(entrezGeneID.toString());
						geneSymbol2EntrezGeneIDMap.put(geneSymbol.trim(), entrezGeneIDs);
					}
				}
			}
		}

		return geneSymbol2EntrezGeneIDMap;
	}

	/**
	 * Returns a map from the EntrezGene ID to the gene symbol
	 * 
	 * @param entrezGeneInfoFileStream
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, String> getEntrezGeneID2GeneSymbolMap(File entrezGeneInfoFile, int taxonID)
			throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(entrezGeneInfoFile);
			return getEntrezGeneID2GeneSymbolMap(fis, taxonID);
		} finally {
			fis.close();
		}
	}

	/**
	 * Returns a map from the EntrezGene ID to the gene symbol
	 * 
	 * @param entrezGeneInfoFileStream
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, String> getEntrezGeneID2GeneSymbolMap(InputStream entrezGeneInfoFileStream, int taxonID)
			throws IOException {
		Map<Integer, String> entrezGeneID2GeneSymbolMap = new HashMap<Integer, String>();
		EntrezGeneInfoFileParser parser = new EntrezGeneInfoFileParser(entrezGeneInfoFileStream);

		while (parser.hasNext()) {
			EntrezGeneInfoFileData dataRecord = parser.next();
			if (dataRecord.getTaxonID() == taxonID) {
				String geneSymbol = dataRecord.getSymbol();
				Integer entrezGeneID = dataRecord.getGeneID();
				if (entrezGeneID2GeneSymbolMap.containsKey(entrezGeneID)) {
					logger.error("Symbol for Entrez Gene ID has already been extracted!!!");
				} else {
					entrezGeneID2GeneSymbolMap.put(entrezGeneID, geneSymbol.trim());
				}
			}
		}

		return entrezGeneID2GeneSymbolMap;
	}

	/**
	 * Returns a map from the EntrezGene ID to the gene symbol where the Entrez Gene ID is
	 * represented as a String
	 * 
	 * @param entrezGeneInfoFileStream
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> getEntrezGeneIDAsString2GeneSymbolMap(File entrezGeneInfoFile, int taxonID)
			throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(entrezGeneInfoFile);
			return getEntrezGeneIDAsString2GeneSymbolMap(fis, taxonID);
		} finally {
			fis.close();
		}
	}

	/**
	 * Returns a map from the EntrezGene ID to the gene symbol where the Entrez Gene ID is
	 * represented as a String
	 * 
	 * @param entrezGeneInfoFileStream
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> getEntrezGeneIDAsString2GeneSymbolMap(InputStream entrezGeneInfoFileStream,
			int taxonID) throws IOException {
		Map<String, String> entrezGeneID2GeneSymbolMap = new HashMap<String, String>();
		EntrezGeneInfoFileParser parser = new EntrezGeneInfoFileParser(entrezGeneInfoFileStream);

		while (parser.hasNext()) {
			EntrezGeneInfoFileData dataRecord = parser.next();
			if (dataRecord.getTaxonID() == taxonID) {
				String geneSymbol = dataRecord.getSymbol();
				Integer entrezGeneID = dataRecord.getGeneID();
				if (entrezGeneID2GeneSymbolMap.containsKey(entrezGeneID.toString())) {
					logger.error("Symbol for Entrez Gene ID has already been extracted!!!");
				} else {
					entrezGeneID2GeneSymbolMap.put(entrezGeneID.toString().trim(), geneSymbol.trim());
				}
			}
		}

		return entrezGeneID2GeneSymbolMap;
	}

	/**
	 * Returns a map from the EntrezGene ID to the gene symbol where the Entrez Gene ID is
	 * represented as a String
	 * 
	 * @param entrezGeneInfoFile
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> getEntrezGeneIDAsString2GeneNameMap(File entrezGeneInfoFile, int taxonID)
			throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(entrezGeneInfoFile);
			return getEntrezGeneIDAsString2GeneNameMap(fis, taxonID);
		} finally {
			fis.close();
		}
	}

	/**
	 * Returns a map from the EntrezGene ID to the gene symbol where the Entrez Gene ID is
	 * represented as a String
	 * 
	 * @param entrezGeneInfoFile
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> getEntrezGeneIDAsString2GeneNameMap(InputStream entrezGeneInfoFile, int taxonID)
			throws IOException {
		Map<String, String> entrezGeneID2GeneSymbolMap = new HashMap<String, String>();
		EntrezGeneInfoFileParser parser = new EntrezGeneInfoFileParser(entrezGeneInfoFile);

		while (parser.hasNext()) {
			EntrezGeneInfoFileData dataRecord = parser.next();
			if (dataRecord.getTaxonID() == taxonID) {
				String geneName = getGeneName(dataRecord);

				Integer entrezGeneID = dataRecord.getGeneID();
				if (entrezGeneID2GeneSymbolMap.containsKey(entrezGeneID.toString())) {
					logger.error("Name for Entrez Gene ID has already been extracted!!!");
				} else {
					if (geneName != null) {
						entrezGeneID2GeneSymbolMap.put(entrezGeneID.toString().trim(), geneName.trim());
					}
				}
			}
		}

		return entrezGeneID2GeneSymbolMap;
	}

	/**
	 * Returns a gene name from an EntrezGeneInfoFileData record. In order of priority, this method
	 * returns the first available (non-null) of the following:<br>
	 * 1. Full name from nomenclature authority <br>
	 * 2. Description <br>
	 * 3. gene symbol <br>
	 * 4. null
	 * 
	 * @param dataRecord
	 */
	private static String getGeneName(EntrezGeneInfoFileData dataRecord) {
		String geneName = dataRecord.getFullNameFromNomenclatureAuthority();
		if (geneName == null) {
			geneName = dataRecord.getDescription();
			if (geneName == null) {
				geneName = dataRecord.getSymbol();
			}
		}
		return geneName;
	}

	public static Set<Integer> getEntrezGeneIDsForTaxonomyID(File entrezGeneInfoFile, int taxonID) throws IOException {
		return getEntrezGeneID2GeneSymbolMap(entrezGeneInfoFile, taxonID).keySet();
	}

	public static Set<Integer> getEntrezGeneIDsForTaxonomyID(InputStream entrezGeneInfoFileStream, int taxonID)
			throws IOException {
		return getEntrezGeneID2GeneSymbolMap(entrezGeneInfoFileStream, taxonID).keySet();
	}

}
