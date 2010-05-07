/*
 *  Authors:  Philip V. Ogren
 *  Created: September, 2004
 *  Description: This file is used to parse the generifs_basic file downloadable 
 *     from Entrez Gene at 
 *  ftp://ftp.ncbi.nlm.nih.gov/gene/GeneRIF/generifs_basic.gz
 *  To use this class, simply call the parse method passing in the name of the
 *  local copy of the generifs_basic.  The returned Iterator will provide GeneRIF
 *  objects - one for each line of the file.  
 *
 *  Todo:  
 *  Changes:
 *
 */

package edu.ucdenver.ccp.parser.ncbi.gene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.ucdenver.ccp.parser.LineFileParser;
import edu.ucdenver.ccp.util.string.StringConstants;

/**
 * A parser for processing the generifs_basic file available via FTP from the Entrez Gene FTP site:
 * ftp://ftp.ncbi.nlm.nih.gov/gene/GeneRIF/generifs_basic.gz
 * 
 * @author Bill Baumgartner
 * 
 */
public class GenerifsBasicFileParser extends LineFileParser<GeneRIF> {

	public static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm";
	private static final String COMMENT_INDICATOR = StringConstants.POUND_SIGN;

	/**
	 * The constructor is private to force the user to use the parse method.
	 * 
	 * @param generifsBasicFile
	 * @throws FileNotFoundException
	 */
	public GenerifsBasicFileParser(File generifsBasicFile) throws IOException {
		super(generifsBasicFile, COMMENT_INDICATOR);
	}

	@Override
	protected GeneRIF parseFileDataFromLine(String line) {
		try {
			return extractFromGenerifsBasicFileLine(line);
		} catch (ParseException e) {
			throw new RuntimeException(String.format("Error occurred while parsing GeneRIF from line %s", line), e);
		}
	}

	/**
	 * Returns a DateFormat for use with the generifs_basic file. Note: There is no public static
	 * DateFormat field in this class because DateFormat is not thread safe.
	 * 
	 * @return
	 */
	public static DateFormat getGeneRifDateFormat() {
		return new SimpleDateFormat(DATE_FORMAT_STRING);
	}

	/**
	 * Utility method for creating a GeneRIF object from the generifs_basic storage line format.
	 * 
	 * @param line
	 * @return
	 * @throws ParseException
	 */
	public static GeneRIF extractFromGenerifsBasicFileLine(String line) throws ParseException {
		String[] tokens = line.split("\\t");
		int taxID = Integer.parseInt(tokens[0]);
		int geneID = Integer.parseInt(tokens[1]);
		String[] pmidStrings = tokens[2].split(",");
		int[] pmids = new int[pmidStrings.length];
		for (int i = 0; i < pmids.length; i++) {
			pmids[i] = Integer.parseInt(pmidStrings[i]);
		}

		Date timeStamp = getGeneRifDateFormat().parse(tokens[3]);
		String text = tokens[4];

		return new GeneRIF(taxID, geneID, pmids, timeStamp, text);
	}

}
