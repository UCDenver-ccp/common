package edu.ucdenver.ccp.parser.ncbi.gene;

import java.util.Arrays;
import java.util.Date;

import edu.ucdenver.ccp.parser.LineFileData;

/**
 * Represents a GeneRIF object. A single GeneRIF object corresponds to a single line in the
 * generifs_basic file downloadable from Entrez gene at:
 * ftp://ftp.ncbi.nlm.nih.gov/gene/GeneRIF/generifs_basic.gz
 * 
 * @author Philip V. Ogren
 * @author Bill Baumgartner
 */
public class GeneRIF implements LineFileData {

	private final int taxID;

	private final int entrezID;

	private final int[] pmids;

	private final Date timeStamp;

	private final String text;

	public GeneRIF(int taxID, int entrezID, int[] pmids, Date timeStamp, String text) {
		this.taxID = taxID;
		this.entrezID = entrezID;
		this.pmids = Arrays.copyOf(pmids, pmids.length);
		this.timeStamp = (Date) timeStamp.clone();
		this.text = text;
	}

	public int getTaxID() {
		return this.taxID;
	}

	public int getEntrezID() {
		return entrezID;
	}

	public int[] getPMIDs() {
		return Arrays.copyOf(pmids, pmids.length);
	}

	public Date getTimeStamp() {
		return (Date) timeStamp.clone();
	}

	public String getText() {
		return text;
	}

}
