package edu.ucdenver.ccp.parser.uniprot.goa;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import edu.ucdenver.ccp.parser.LineFileParser;
import edu.ucdenver.ccp.util.file.FileLoaderUtil;
import edu.ucdenver.ccp.util.string.RegExPatterns;
import edu.ucdenver.ccp.util.string.StringConstants;

public class GpAssociationGoaUniprotFileParser extends LineFileParser<GpAssociationGoaUniprotFileData> {

	public static final String DELIMITER_REGEX = RegExPatterns.TAB;
	private static final int FILE_COLUMN_COUNT = 10;
	public static final String COMMENT_INDICATOR = StringConstants.EXCLAMATION_MARK;

	public GpAssociationGoaUniprotFileParser(File inputFile) throws IOException {
		super(inputFile, COMMENT_INDICATOR);
	}

	@Override
	protected void initialize() {
		super.initialize();
		/* */
	}

	@Override
	protected GpAssociationGoaUniprotFileData parseFileDataFromLine(String line) {
		return parseGpAssociationGoaUniprotFileDataFromLine(line);
	}

	public static GpAssociationGoaUniprotFileData parseGpAssociationGoaUniprotFileDataFromLine(String line) {
		String[] columns = FileLoaderUtil.getColumnsFromLine(line, DELIMITER_REGEX);
		if (columns.length < FILE_COLUMN_COUNT) {
			throw new IllegalArgumentException(String.format(
					"Unable to initialize a new GpAssociationGoaUniprotFileData object. Expected %d columns in the input "
							+ "String[] but there were %d columns. Columns= %s LINE=%s", FILE_COLUMN_COUNT,
					columns.length, Arrays.toString(columns), line));
		}
		return initializeNewGpAssociationGoaUniprotFileData(columns);
	}

	private static GpAssociationGoaUniprotFileData initializeNewGpAssociationGoaUniprotFileData(String[] columns) {
		String database = columns[0];
		String databaseObjectID = columns[1];
		String qualifier = columns[2];
		String goID = columns[3];
		String dbReference = columns[4];
		String evidenceCode = columns[5];
		String with = columns[6];
		String extraTaxonID = columns[7];
		String date = columns[8];
		String assignedBy = columns[9];
		String annotationExtension = "";
		if (columns.length > 10)
			annotationExtension = columns[10];
		String geneProductFormID = "";
		if (columns.length > 11)
			geneProductFormID = columns[11];
		return new GpAssociationGoaUniprotFileData(database, databaseObjectID, qualifier, goID, dbReference,
				evidenceCode, with, extraTaxonID, date, assignedBy, annotationExtension, geneProductFormID);
	}

}
