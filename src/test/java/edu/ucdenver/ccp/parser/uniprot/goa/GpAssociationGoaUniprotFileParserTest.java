package edu.ucdenver.ccp.parser.uniprot.goa;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.util.collections.CollectionsUtil;
import edu.ucdenver.ccp.util.file.FileWriterUtil;
import edu.ucdenver.ccp.util.test.DefaultTestCase;
import edu.ucdenver.ccp.util.test.TestUtil;

public class GpAssociationGoaUniprotFileParserTest extends DefaultTestCase {

	private File sampleGpAssociationGoaUniprotFile;

	@Before
	public void setUp() throws Exception {
		initializeSampleGpAssociationGoaUniprotFile();
	}

	private void initializeSampleGpAssociationGoaUniprotFile() throws IOException {
		sampleGpAssociationGoaUniprotFile = folder.newFile("gp_association.goa_uniprot");
		List<String> lines = CollectionsUtil
				.createList("!gpa-version: 1.0", "!Columns:", "!",
						"!   name                  required? cardinality   GAF column #",
						"!   DB                    required  1             1",
						"!   DB_Object_ID          required  1             2",
						"!   Qualifier             optional  0 or greater  4",
						"!   GO ID                 required  1             5",
						"!   DB:Reference(s)       required  1 or greater  6",
						"!   Evidence code         required  1             7",
						"!   With                  optional  0 or greater  8",
						"!   Extra taxon ID        optional  0 or 1        13",
						"!   Date                  required  1             14",
						"!   Assigned_by           required  1             15",
						"!   Annotation Extension  optional  0 or greater  16",
						"!   Spliceform ID         optional  0 or 1        17", "!", "!Generated: 2010-06-01 13:49",
						"!", "IPI\tIPI00197411\t\tGO:0000900\tPMID:12933792\tIDA\t\t\t20061230\tHGNC\t\t",
						"IPI\tIPI00693437\tcolocalizes_with\tGO:0005657\tPMID:10490843\tISS\tUniProtKB:P41182\t\t20070622\tUniProtKB\t\t");
		FileWriterUtil.printLines(lines, sampleGpAssociationGoaUniprotFile);
	}

	@Test
	public void testParser() throws Exception {
		GpAssociationGoaUniprotFileParser parser = new GpAssociationGoaUniprotFileParser(
				sampleGpAssociationGoaUniprotFile);
		assertTrue(String.format("should have the first of two records."), parser.hasNext());
		GpAssociationGoaUniprotFileData dataRecord = parser.next();
		TestUtil.conductBeanComparison(getExpectedRecord1(), dataRecord);
		assertTrue("This data record has a PubMed ID reference.", dataRecord.hasPubmedReference());
		assertEquals("The pubmed id reference is 12933792", 12933792, dataRecord.getPubmedReferenceID());
		assertTrue(String.format("should have the second of two records."), parser.hasNext());
		TestUtil.conductBeanComparison(getExpectedRecord2(), parser.next());
		assertFalse(String.format("should only have 2 records"), parser.hasNext());

	}

	@Test
	public void testname() throws Exception {
		assertArrayEquals(new String[] { "a", "b", "c" }, "a\tb\tc".split("\\t"));
		assertArrayEquals(new String[] { "a", "b", "c" }, "a\tb\tc\t\t\t\t\t".split("\\t"));
	}

	private GpAssociationGoaUniprotFileData getExpectedRecord1() {
		return new GpAssociationGoaUniprotFileData("IPI", "IPI00197411", "", "GO:0000900", "PMID:12933792", "IDA", "",
				"", "20061230", "HGNC", "", "");
	}

	private GpAssociationGoaUniprotFileData getExpectedRecord2() {
		return new GpAssociationGoaUniprotFileData("IPI", "IPI00693437", "colocalizes_with", "GO:0005657",
				"PMID:10490843", "ISS", "UniProtKB:P41182", "", "20070622", "UniProtKB", "", "");
	}

}
