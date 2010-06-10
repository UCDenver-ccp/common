package edu.ucdenver.ccp.parser.ncbi.pmc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.util.collections.CollectionsUtil;
import edu.ucdenver.ccp.util.test.DefaultTestCase;
import edu.ucdenver.ccp.util.test.TestUtil;

public class PmcIdsCsvFileParserTest extends DefaultTestCase {

	private File pmcIdsCsvFile;
	private File pmcIdsCsvFile_WithInvalidHeader;
	private File pmcIdsCsvFile_WithInvalidRow;
	private File pmcIdsCsvFile_WithCommaInsideField;

	@Before
	public void setUp() throws Exception {
		populateSamplePmcIdsCsvFile();
		populateSamplePmcIdsCsvFile_WithInvalidHeader();
		populateSamplePmcIdsCsvFile_WithInvalidRow();
		populateSamplePmcIdsCsvFile_WithRowWithCommaInsideField();
	}

	private void populateSamplePmcIdsCsvFile() throws IOException {
		List<String> lines = CollectionsUtil.createList(
				"Journal Title,ISSN,eISSN,Year,Volume,Issue,Page,DOI,PMCID,PMID,Manuscript Id,Release Date",
				"Breast Cancer Res,1465-5411,1465-542X,2000,3,1,55,,PMC13900,11250746,,live",
				"Breast Cancer Res,1465-5411,1465-542X,2000,3,1,61,,PMC13901,11250747,,live");
		pmcIdsCsvFile = TestUtil.populateTestFile(folder, "PMC-ids.csv", lines);
	}
	
	private void populateSamplePmcIdsCsvFile_WithRowWithCommaInsideField() throws IOException {
		List<String> lines = CollectionsUtil.createList(
				"Journal Title,ISSN,eISSN,Year,Volume,Issue,Page,DOI,PMCID,PMID,Manuscript Id,Release Date",
				"J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, vol.1-17\",1,10.1172/JCI101100,PMC548872,0,,live");
		pmcIdsCsvFile_WithCommaInsideField = TestUtil.populateTestFile(folder, "PMC-ids.csv.commaInside", lines);
	}

	/**
	 * The header is not as expected
	 * 
	 * @throws IOException
	 */
	private void populateSamplePmcIdsCsvFile_WithInvalidHeader() throws IOException {
		List<String> lines = CollectionsUtil
				.createList("#Journal Title,ISSN,eISSN,Year,Volume,Issue,Page,DOI,PMCID,PMID,Manuscript Id,Release Date");
		pmcIdsCsvFile_WithInvalidHeader = TestUtil.populateTestFile(folder, "PMC-ids.csv.invalid_header", lines);
	}

	/**
	 * Record 1 is missing a column
	 * 
	 * @throws IOException
	 */
	private void populateSamplePmcIdsCsvFile_WithInvalidRow() throws IOException {
		List<String> lines = CollectionsUtil.createList(
				"Journal Title,ISSN,eISSN,Year,Volume,Issue,Page,DOI,PMCID,PMID,Manuscript Id,Release Date",
				"Breast Cancer Res,1465-5411,1465-542X,2000,3,1,55,,PMC13900,11250746,live");
		pmcIdsCsvFile_WithInvalidRow = TestUtil.populateTestFile(folder, "PMC-ids.csv.invalid_row", lines);
	}

	private PmcIdsCsvFileData getExpectedRecord1() {
		return new PmcIdsCsvFileData("Breast Cancer Res", "1465-5411", "1465-542X", 2000, "3", "1", "55", "",
				"PMC13900", "11250746", "", "live");
	}

	private PmcIdsCsvFileData getExpectedRecord2() {
		return new PmcIdsCsvFileData("Breast Cancer Res", "1465-5411", "1465-542X", 2000, "3", "1", "61", "",
				"PMC13901", "11250747", "", "live");
	}

	@Test
	public void testParse() throws Exception {
		Iterator<PmcIdsCsvFileData> pmdIdsIter = new PmcIdsCsvFileParser(pmcIdsCsvFile);
		assertTrue(String.format("Should be 1st of two pmc id records."), pmdIdsIter.hasNext());
		checkPmcIdsCsvFileData(getExpectedRecord1(), pmdIdsIter.next());
		assertTrue(String.format("Should be 2nd of two pmc id records."), pmdIdsIter.hasNext());
		checkPmcIdsCsvFileData(getExpectedRecord2(), pmdIdsIter.next());
		assertFalse(pmdIdsIter.hasNext());
	}

	private void checkPmcIdsCsvFileData(PmcIdsCsvFileData expected, PmcIdsCsvFileData observed) throws Exception {
		TestUtil.conductBeanComparison(expected, observed);
	}

	@Test(expected = IllegalStateException.class)
	public void testParse_WithInvalidHeader() throws Exception {
		new PmcIdsCsvFileParser(pmcIdsCsvFile_WithInvalidHeader);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParse_WithInvalidRow() throws Exception {
		Iterator<PmcIdsCsvFileData> pmdIdsIter = new PmcIdsCsvFileParser(pmcIdsCsvFile_WithInvalidRow);
		pmdIdsIter.hasNext();
	}
	
	@Test
	public void testParseLineWithCommaInsideField() throws Exception {
		Iterator<PmcIdsCsvFileData> pmdIdsIter = new PmcIdsCsvFileParser(pmcIdsCsvFile_WithCommaInsideField);
		assertTrue(pmdIdsIter.hasNext());
		assertNotNull(pmdIdsIter.next());
	}

}
