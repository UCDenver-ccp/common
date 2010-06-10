package edu.ucdenver.ccp.parser.ncbi.pmc;

import java.io.File;
import java.io.IOException;

import edu.ucdenver.ccp.parser.LineFileParser;
import edu.ucdenver.ccp.util.file.FileLoaderUtil;
import edu.ucdenver.ccp.util.string.StringConstants;

/**
 * This class implements a parser for the PubMed Central PMC-ids.csv file available at the PMC FTP
 * site: ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/PMC-ids.csv.gz<br>
 * <br>
 * Format: <br>
 * 
 * <pre>
 * Journal Title,ISSN,eISSN,Year,Volume,Issue,Page,DOI,PMC accession number,PMID,Manuscript Id,Release Date
 * </pre>
 * 
 * Sample entries:<br>
 * 
 * <pre>
 * Mol Biol Cell,1059-1524,1939-4586,2000,11,6,2019, ,PMC14900,10848626, ,live
 * J Neurosci,0270-6474,1529-2401,2005,25,24,5740,10.1523/JNEUROSCI.0913-05.2005,PMC1201448,15958740,NIHMS3372,live
 * Cancer Res,0008-5472,1538-7445,2007,67,17,8022,10.1158/0008-5472.CAN-06-3749,PMC1986634,17804713,NIHMS25090,Sep 1 2008
 * Proc Natl Acad Sci U S A,0027-8424,1091-6490,2007,104,43,17075,10.1073/pnas.0707060104,PMC2040460,17940018, ,live
 * Cell Host Microbe,1931-3128,1934-6069,2007,2,6,404,10.1016/j.chom.2007.09.014,PMC2184509,18078692,NIHMS36164,live
 * Proc Natl Acad Sci U S A,0027-8424,1091-6490,2008,105,21,7382,10.1073/pnas.0711174105,PMC2396716,18495922, ,Nov 27 2008
 * PLoS Med,1549-1277,1549-1676,2008, ,Immediate Access,e168,10.1371/journal.pmed.0050168,PMC2494565,18684010, ,live
 * </pre>
 * 
 * @author Bill Baumgartner
 * 
 */
public class PmcIdsCsvFileParser extends LineFileParser<PmcIdsCsvFileData> {
	public static final String DELIMITER_REGEX = StringConstants.COMMA;
	public static final char FIELD_ENCLOSURE = StringConstants.QUOTATION_MARK;
	private static final int FILE_COLUMN_COUNT = 12;
	private static final String FIELD_ENCLOSING_REGEX = Character.toString(StringConstants.QUOTATION_MARK);

	public PmcIdsCsvFileParser(File inputFile) throws IOException {
		super(inputFile);
	}

	@Override
	protected void initialize() {
		super.initialize();
		/* skips the first line, which is a file header */
		String firstLine = lineIterator.next();
		if (!firstLine.startsWith("Journal Title")) {
			throw new IllegalStateException(String.format("File format change detected. Expected line starting "
					+ "with \"Journal Title\" as first line, but observed: \"%s\"", firstLine));
		}
	}

	@Override
	protected PmcIdsCsvFileData parseFileDataFromLine(String line) {
		return parsePmcIdsCsvFileDataFromLine(line);
	}

	/**
	 * Returns a new PmcIdsCsvFileData object built using data parsed from the input line.
	 * 
	 * @param line
	 * @return
	 */
	public static PmcIdsCsvFileData parsePmcIdsCsvFileDataFromLine(String line) {
		String[] columns = FileLoaderUtil.getColumnsFromLine(line, DELIMITER_REGEX, FIELD_ENCLOSING_REGEX);
		if (columns.length != FILE_COLUMN_COUNT) {
			throw new IllegalArgumentException(String.format(
					"Unable to initialize a new PmcIdsCsvFileData object. Expected %d columns in the input "
							+ "String[] but there were %d columns. Line = %s", FILE_COLUMN_COUNT, columns.length, line));
		}
		return initializeNewPmcIdsCsvFileData(columns);
	}

	/**
	 * Returns a new PmcIdsCsvFileData object initialized using the input String[] array
	 * 
	 * @param columns
	 * @return
	 */
	private static PmcIdsCsvFileData initializeNewPmcIdsCsvFileData(String[] columns) {
		String journalTitle = columns[0];
		String issn = columns[1];
		String electronicIssn = columns[2];
		int publicationYear = Integer.parseInt(columns[3]);
		String volume = columns[4];
		String issue = columns[5];
		String page = columns[6];
		String doi = columns[7];
		String pmcAccession = columns[8];
		String pubMedID = columns[9];
		String manuscriptID = columns[10];
		String releaseDate = columns[11];
		return new PmcIdsCsvFileData(journalTitle, issn, electronicIssn, publicationYear, volume, issue, page, doi,
				pmcAccession, pubMedID, manuscriptID, releaseDate);
	}
}
