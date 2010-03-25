/**
 *  Authors:  Philip V. Ogren
 *  Created: September, 2004
 *  Description:  This class represents a single generif.  It is based on code 
 *   previously called MedlineGrif and then later on a version that was in the
 *   GeneRIF Annotator source code.  
 *  Todo: having a public constructor seems weird.  For public consumers, these
 *  GeneRIFs should be 1) retrieved from a database and 2) immutable.
 *  Changes:
 *
 */

package edu.ucdenver.ccp.parser.ncbi.gene;

/**
 * Represents a GeneRIF object. A single GeneRIF object corresponds to a single line in the generifs_basic file downloadable from Entrez gene at:
 * ftp://ftp.ncbi.nlm.nih.gov/gene/GeneRIF/generifs_basic.gz
 * 
 * 
 */

import java.util.Date;

public class GeneRIF {

	private int id = -1;

	private final int taxID;

	private final int entrezID;

	private final int[] pmids;

	private final Date timeStamp;

	private final String text;

	public GeneRIF(int taxID, int entrezID, int[] pmids, Date timeStamp, String text) {
		this.taxID = taxID;
		this.entrezID = entrezID;
		this.pmids = pmids;
		this.timeStamp = timeStamp;
		this.text = text;
	}

	void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return this.id;
	}

	public int getTaxID() {
		return this.taxID;
	}

	public int getEntrezID() {
		return entrezID;
	}

	public int[] getPMIDs() {
		return pmids;
	}

	public Date getTimeStamp() {
		return this.timeStamp;
	}

	public String getText() {
		return text;
	}

	public String toString() {
		return "GeneRIF: ID=" + id + "  Entrez ID=" + entrezID + "  Text=" + text;
	}

}
