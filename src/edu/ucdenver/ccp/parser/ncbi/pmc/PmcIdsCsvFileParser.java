package edu.ucdenver.ccp.parser.ncbi.pmc;

import java.io.File;
import java.io.IOException;

import edu.ucdenver.ccp.parser.LineFileParser;
import edu.ucdenver.ccp.util.file.FileLoaderUtil;
import edu.ucdenver.ccp.util.string.StringConstants;

public class PmcIdsCsvFileParser extends LineFileParser<PmcIdsCsvFileData> {
	public static final String DELIMITER = StringConstants.COMMA;
	private static final int FILE_COLUMN_COUNT = 12;

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
		String[] columns = FileLoaderUtil.getColumnsFromLine(line, DELIMITER);
		if (columns.length != FILE_COLUMN_COUNT) {
			throw new IllegalArgumentException(String.format(
					"Unable to initialize a new PmcIdsCsvFileData object. Expected %d columns in the input "
							+ "String[] but there were %d columns.", FILE_COLUMN_COUNT, columns.length));
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
