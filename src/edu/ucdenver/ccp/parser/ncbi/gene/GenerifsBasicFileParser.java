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
import java.util.Iterator;

import edu.ucdenver.ccp.util.file.FileLoaderUtil;
import edu.ucdenver.ccp.util.string.StringConstants;

/**
 * Created: September, 2004<br>
 * Description: This file is used to parse the generifs_basic file downloadable from Entrez Gene at <br>
 * ftp://ftp.ncbi.nlm.nih.gov/gene/GeneRIF/generifs_basic.gz<br>
 * To use this class, simply call the parse method passing in the name of the local copy of the
 * generifs_basic. The returned Iterator will provide GeneRIF objects - one for each line of the
 * file. <br>
 *<br>
 * Changes:<br>
 * March, 2010: Modified to use the FileUtil.getLineIterator() method
 * 
 * @author Philip V. Ogren
 * @author Bill Baumgartner
 * 
 */
public class GenerifsBasicFileParser implements Iterator<GeneRIF> {

	public static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm";
	private static final String COMMENT_INDICATOR = StringConstants.POUND;
	private final Iterator<String> fileLinesIterator;

	/**
	 * The constructor is private to force the user to use the parse method.
	 * 
	 * @param generifsBasicFile
	 * @throws FileNotFoundException
	 */
	private GenerifsBasicFileParser(File generifsBasicFile) throws IOException {
		fileLinesIterator = FileLoaderUtil.getLineIterator(generifsBasicFile, COMMENT_INDICATOR);
	}

	/**
	 * @param fileName
	 *            The local file name of the entrez generif file download:
	 *            ftp://ftp.ncbi.nlm.nih.gov/gene/GeneRIF/generifs_basic.gz
	 * @throws IOException
	 * @return an Iterator of GeneRIF objects - one for each line of the file
	 */

	public static Iterator<GeneRIF> parse(File generifsBasicFile) throws IOException {
		return new GenerifsBasicFileParser(generifsBasicFile);
	}

	@Override
	public boolean hasNext() {
		return fileLinesIterator.hasNext();
	}

	@Override
	public GeneRIF next() {
		String line = fileLinesIterator.next();
		try {
			return extractFromGenerifsBasicFileLine(line);
		} catch (ParseException e) {
			throw new IllegalArgumentException(String.format("Unable to parse date for generif: %s", line));
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("The remove() method is not supported for this Iterator.");
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
