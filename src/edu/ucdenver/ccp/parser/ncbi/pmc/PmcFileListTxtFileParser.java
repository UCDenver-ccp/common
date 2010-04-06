package edu.ucdenver.ccp.parser.ncbi.pmc;

import java.io.File;
import java.io.IOException;

import edu.ucdenver.ccp.parser.LineFileParser;
import edu.ucdenver.ccp.util.file.FileLoaderUtil;
import edu.ucdenver.ccp.util.string.RegExPatterns;
import edu.ucdenver.ccp.util.string.StringConstants;
import edu.ucdenver.ccp.util.string.StringUtil;

/**
 * This class implements a parser for the PubMed Central file_list.txt file available at the PMC FTP
 * site: ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/file_list.txt<br>
 * <br>
 * Format: <br>
 * 
 * <pre>
 * PMC_FTP_LOCATION [tab] CITATION [tab] PMC_ACCESSION
 * </pre>
 * 
 * Sample entries:<br>
 * 
 * <pre>
 * d1/c3/Nucleic_Acids_Res-10-12-320743.tar.gz     Nucleic Acids Res. 1982 Jun 25; 10(12):3681-3691        PMC320743
 * 15/9d/Bioinorg_Chem_Appl-1-2-2267055.tar.gz     Bioinorg Chem Appl. 2003; 1(2):123-139  PMC2267055
 * d9/90/Reprod_Biol_Endocrinol-7-_-2702309.tar.gz Reprod Biol Endocrinol. 2009 Jun 16; 7:63       PMC2702309
 * </pre>
 * 
 * @author Bill Baumgartner
 * 
 */
public class PmcFileListTxtFileParser extends LineFileParser<PmcFileListTxtFileData> {
	public static final String DELIMITER = RegExPatterns.TAB;
	private static final int FILE_COLUMN_COUNT = 3;

	public PmcFileListTxtFileParser(File inputFile) throws IOException {
		super(inputFile);
	}

	@Override
	protected void initialize() {
		super.initialize();
		/* skips the first line, which is a time stamp, e.g. 2010-04-01 21:40:15 */
		String firstLine = lineIterator.next();
		if (!StringUtil.startsWithRegex(firstLine, RegExPatterns.getNDigitsPattern(4))) {
			throw new IllegalStateException(String.format("File format change detected. Expected line starting "
					+ "with \"Journal Title\" as first line, but observed: \"%s\"", firstLine));
		}
	}

	@Override
	protected PmcFileListTxtFileData parseFileDataFromLine(String line) {
		return parsePmcFileListTxtFileDataFromLine(line);
	}

	/**
	 * Parses the input line and returns a new PmcFileListTxtFileData object.
	 * 
	 * @param line
	 * @return
	 */
	public static PmcFileListTxtFileData parsePmcFileListTxtFileDataFromLine(String line) {
		String[] columns = FileLoaderUtil.getColumnsFromLine(line, DELIMITER);
		if (columns.length != FILE_COLUMN_COUNT) {
			throw new IllegalArgumentException(String.format(
					"Unable to initialize a new PmcFileListTxtFileData object. Expected %d columns in the input "
							+ "String[] but there were %d columns.", FILE_COLUMN_COUNT, columns.length));
		}
		return initializeNewPmcFileListTxtFileData(columns);
	}

	/**
	 * Initializes a new PmcFileListTxtFileData object using the input String[]
	 * 
	 * @param columns
	 * @return
	 */
	private static PmcFileListTxtFileData initializeNewPmcFileListTxtFileData(String[] columns) {
		String pmcFtpPath = columns[0];
		String citation = columns[1];
		String pmcid = columns[2];
		return new PmcFileListTxtFileData(pmcFtpPath, citation, pmcid);
	}

}
