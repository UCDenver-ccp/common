package edu.ucdenver.ccp.parser.ncbi.pmc;

import static org.junit.Assert.assertFalse;
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

public class PmcFileListTxtFileParserTest extends DefaultTestCase {

	private File pmcFileListTxtFile;

	@Before
	public void setUp() throws Exception {
		populateSamplePmcFileListTxtFile();
	}

	private void populateSamplePmcFileListTxtFile() throws IOException {
		List<String> lines = CollectionsUtil
				.createList(
						"2010-04-01 21:40:15",
						"d1/c3/Nucleic_Acids_Res-10-12-320743.tar.gz\tNucleic Acids Res. 1982 Jun 25; 10(12):3681-3691\tPMC320743",
						"15/9d/Bioinorg_Chem_Appl-1-2-2267055.tar.gz\tBioinorg Chem Appl. 2003; 1(2):123-139\tPMC2267055");
		pmcFileListTxtFile = TestUtil.populateTestFile(folder, "file_list.txt", lines);
	}

	private PmcFileListTxtFileData getExpectedRecord1() {
		return new PmcFileListTxtFileData("d1/c3/Nucleic_Acids_Res-10-12-320743.tar.gz",
				"Nucleic Acids Res. 1982 Jun 25; 10(12):3681-3691", "PMC320743");
	}

	private PmcFileListTxtFileData getExpectedRecord2() {
		return new PmcFileListTxtFileData("15/9d/Bioinorg_Chem_Appl-1-2-2267055.tar.gz",
				"Bioinorg Chem Appl. 2003; 1(2):123-139", "PMC2267055");
	}

	@Test
	public void testParse() throws Exception {
		Iterator<PmcFileListTxtFileData> pmcFileListTxtIter = new PmcFileListTxtFileParser(pmcFileListTxtFile);
		assertTrue(String.format("Should be 1st of two pmc id records."), pmcFileListTxtIter.hasNext());
		checkPmcFileListTxtFileData(getExpectedRecord1(), pmcFileListTxtIter.next());
		assertTrue(String.format("Should be 2nd of two pmc id records."), pmcFileListTxtIter.hasNext());
		checkPmcFileListTxtFileData(getExpectedRecord2(), pmcFileListTxtIter.next());
		assertFalse(pmcFileListTxtIter.hasNext());
	}

	private void checkPmcFileListTxtFileData(PmcFileListTxtFileData expected, PmcFileListTxtFileData observed)
			throws Exception {
		TestUtil.conductBeanComparison(expected, observed);
	}

}
