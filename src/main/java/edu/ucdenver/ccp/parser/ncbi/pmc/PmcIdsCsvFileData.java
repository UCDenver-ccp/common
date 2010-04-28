package edu.ucdenver.ccp.parser.ncbi.pmc;

import edu.ucdenver.ccp.parser.LineFileData;

/**
 * This data structure is designed to store data parsed from the PubMed Central PMC-ids.csv file
 * available at the PMC FTP site: ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/PMC-ids.csv.gz<br>
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
public class PmcIdsCsvFileData implements LineFileData {
	private final String journalTitle;
	private final String issn;
	private final String electronicIssn;
	private final int publicationYear;
	private final String volume;
	private final String issue;
	private final String page;
	private final String doi;
	private final String pmcAccession;
	private final String pubMedID;
	private final String manuscriptID;
	private final String releaseDate;

	public PmcIdsCsvFileData(String journalTitle, String issn, String electronicIssn, int publicationYear,
			String volume, String issue, String page, String doi, String pmcAccession, String pubMedID,
			String manuscriptID, String releaseDate) {
		super();
		this.journalTitle = journalTitle;
		this.issn = issn;
		this.electronicIssn = electronicIssn;
		this.publicationYear = publicationYear;
		this.volume = volume;
		this.issue = issue;
		this.page = page;
		this.doi = doi;
		this.pmcAccession = pmcAccession;
		this.pubMedID = pubMedID;
		this.manuscriptID = manuscriptID;
		this.releaseDate = releaseDate;
	}

	public String getJournalTitle() {
		return journalTitle;
	}

	public String getIssn() {
		return issn;
	}

	public String getElectronicIssn() {
		return electronicIssn;
	}

	public int getPublicationYear() {
		return publicationYear;
	}

	public String getVolume() {
		return volume;
	}

	public String getIssue() {
		return issue;
	}

	public String getPage() {
		return page;
	}

	public String getDoi() {
		return doi;
	}

	public String getPmcAccession() {
		return pmcAccession;
	}

	public String getPubMedID() {
		return pubMedID;
	}

	public String getManuscriptID() {
		return manuscriptID;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

}
