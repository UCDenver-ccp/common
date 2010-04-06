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

	private int id = -1;

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
		return Arrays.copyOf(pmids, pmids.length);
	}

	public Date getTimeStamp() {
		return (Date) timeStamp.clone();
	}

	public String getText() {
		return text;
	}

	public String toString() {
		return "GeneRIF: ID=" + id + "  Entrez ID=" + entrezID + "  Text=" + text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + entrezID;
		result = prime * result + id;
		result = prime * result + Arrays.hashCode(pmids);
		result = prime * result + taxID;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((timeStamp == null) ? 0 : timeStamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneRIF other = (GeneRIF) obj;
		if (entrezID != other.entrezID)
			return false;
		if (id != other.id)
			return false;
		if (!Arrays.equals(pmids, other.pmids))
			return false;
		if (taxID != other.taxID)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (timeStamp == null) {
			if (other.timeStamp != null)
				return false;
		} else if (!timeStamp.equals(other.timeStamp))
			return false;
		return true;
	}

}
