package edu.ucdenver.ccp.parser.ncbi.pmc;

import edu.ucdenver.ccp.parser.LineFileData;

/**
 * This data structure is designed to store data parsed from the PubMed Central file_list.txt file
 * available at the PMC FTP site: ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/file_list.txt<br>
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
public class PmcFileListTxtFileData implements LineFileData {
	private final String ftpFilePath;
	private final String citation;
	private final String pmcAccession;

	public PmcFileListTxtFileData(String ftpFilePath, String citation, String pmcAccession) {
		super();
		this.ftpFilePath = ftpFilePath;
		this.citation = citation;
		this.pmcAccession = pmcAccession;
	}

	public String getFtpFilePath() {
		return ftpFilePath;
	}

	public String getCitation() {
		return citation;
	}

	public String getPmcAccession() {
		return pmcAccession;
	}

	/**
	 * Returns the directory path to the file represented by this data record.
	 * 
	 * @return
	 */
	public String getFtpDirectory() {
		return ftpFilePath.substring(0, ftpFilePath.lastIndexOf('/'));
	}

	/**
	 * Returns the file name for the file represented by this data record.
	 * 
	 * @return
	 */
	public String getFtpFileName() {
		return ftpFilePath.substring(ftpFilePath.lastIndexOf('/') + 1);
	}

}
