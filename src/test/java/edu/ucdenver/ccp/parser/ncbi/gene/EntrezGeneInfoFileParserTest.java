/*
 * Copyright (C) 2009 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package edu.ucdenver.ccp.parser.ncbi.gene;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import edu.ucdenver.ccp.parser.DataFileParserTester;
import edu.ucdenver.ccp.util.collections.CollectionsUtil;
import edu.ucdenver.ccp.util.test.DefaultTestCase;

/**
 * 
 * @author Bill Baumgartner
 * 
 */
public class EntrezGeneInfoFileParserTest extends DefaultTestCase implements DataFileParserTester {

	private final String SAMPLE_ENTREZGENEINFO_FILE_NAME = "EntrezGene_gene_info";

	@Test
	public void testParser() {
		try {
			EntrezGeneInfoFileParser parser = new EntrezGeneInfoFileParser(getResourceFromClasspath(this.getClass(),
					SAMPLE_ENTREZGENEINFO_FILE_NAME));

			if (parser.hasNext()) {
				/*
				 * 10090 12780 Abcc2 - AI173996|Abc30|Cmoat|Mrp2|cMRP
				 * MGI:1352447|Ensembl:ENSMUSG00000025194 19 19 C3|19 43.0 cM ATP-binding cassette,
				 * sub-family C (CFTR/MRP), member 2 protein-coding Abcc2 ATP-binding cassette,
				 * sub-family C (CFTR/MRP), member 2 O ATP-binding cassette, sub-family C, member
				 * 2|canalicular multispecific organic anion transporter|multidrug resistance
				 * protein 2 20080827
				 */
				EntrezGeneInfoFileData record = parser.next();
				assertEquals(10090, record.getTaxonID());
				assertEquals(12780, record.getGeneID());
				assertEquals("Abcc2", record.getSymbol());
				assertEquals(null, record.getLocusTag());
				Set<String> expectedSynonyms = new HashSet<String>();
				expectedSynonyms.add("AI173996");
				expectedSynonyms.add("Abc30");
				expectedSynonyms.add("Cmoat");
				expectedSynonyms.add("Mrp2");
				expectedSynonyms.add("cMRP");
				assertEquals(expectedSynonyms, record.getSynonyms());
				Set<String> expectedDBXrefs = new HashSet<String>();
				expectedDBXrefs.add("MGI:1352447");
				expectedDBXrefs.add("Ensembl:ENSMUSG00000025194");
				assertEquals(expectedDBXrefs, record.getDbXrefs());
				assertEquals("19", record.getChromosome());
				assertEquals("19 C3|19 43.0 cM", record.getMapLocation());
				assertEquals("ATP-binding cassette, sub-family C (CFTR/MRP), member 2", record.getDescription());
				assertEquals("protein-coding", record.getTypeOfGene());
				assertEquals("Abcc2", record.getSymbolFromNomenclatureAuthority());
				assertEquals("ATP-binding cassette, sub-family C (CFTR/MRP), member 2", record
						.getFullNameFromNomenclatureAuthority());
				assertEquals("O", record.getNomenclatureStatus());
				Set<String> expectedOtherDesignations = new HashSet<String>();
				expectedOtherDesignations.add("ATP-binding cassette, sub-family C, member 2");
				expectedOtherDesignations.add("canalicular multispecific organic anion transporter");
				expectedOtherDesignations.add("multidrug resistance protein 2");
				assertEquals(expectedOtherDesignations, record.getOtherDesignations());
				assertEquals("20080827", record.getModificationDate());
			} else {
				fail("Parser should have returned a record here.");
			}

			if (parser.hasNext()) {
				/*
				 * 10090 11308 Abi1 - E3B1|MGC6064|NAP1|Ssh3bp1
				 * MGI:104913|Ensembl:ENSMUSG00000058835 2 2 A3|2 15.0 cM abl-interactor 1
				 * protein-coding Abi1 abl-interactor 1 O abelson interactor 1|ablphilin-1|eps8
				 * binding protein|spectrin SH3 domain binding protein 1 20080817
				 */
				EntrezGeneInfoFileData record = parser.next();
				assertEquals(10090, record.getTaxonID());
				assertEquals(11308, record.getGeneID());
				assertEquals("Abi1", record.getSymbol());
				assertEquals(null, record.getLocusTag());
				Set<String> expectedSynonyms = new HashSet<String>();
				expectedSynonyms.add("E3B1");
				expectedSynonyms.add("MGC6064");
				expectedSynonyms.add("NAP1");
				expectedSynonyms.add("Ssh3bp1");
				assertEquals(expectedSynonyms, record.getSynonyms());
				Set<String> expectedDBXrefs = new HashSet<String>();
				expectedDBXrefs.add("MGI:104913");
				expectedDBXrefs.add("Ensembl:ENSMUSG00000058835");
				assertEquals(expectedDBXrefs, record.getDbXrefs());
				assertEquals("2", record.getChromosome());
				assertEquals("2 A3|2 15.0 cM", record.getMapLocation());
				assertEquals("abl-interactor 1", record.getDescription());
				assertEquals("protein-coding", record.getTypeOfGene());
				assertEquals("Abi1", record.getSymbolFromNomenclatureAuthority());
				assertEquals("abl-interactor 1", record.getFullNameFromNomenclatureAuthority());
				assertEquals("O", record.getNomenclatureStatus());
				Set<String> expectedOtherDesignations = new HashSet<String>();
				expectedOtherDesignations.add("abelson interactor 1");
				expectedOtherDesignations.add("ablphilin-1");
				expectedOtherDesignations.add("eps8 binding protein");
				expectedOtherDesignations.add("spectrin SH3 domain binding protein 1");
				assertEquals(expectedOtherDesignations, record.getOtherDesignations());
				assertEquals("20080817", record.getModificationDate());
			} else {
				fail("Parser should have returned a record here.");
			}

			if (parser.hasNext()) {
				/*
				 * 10090 11434 Acr - AI323726|MGC124043 MGI:87884|Ensembl:ENSMUSG00000022622 15 15
				 * E-F|15 48.6 cM acrosin prepropeptide protein-coding Acr acrosin prepropeptide O
				 * acrosin|preproacrosin 20080831
				 */
				EntrezGeneInfoFileData record = parser.next();
				assertEquals(10090, record.getTaxonID());
				assertEquals(11434, record.getGeneID());
				assertEquals("Acr", record.getSymbol());
				assertEquals(null, record.getLocusTag());
				Set<String> expectedSynonyms = new HashSet<String>();
				expectedSynonyms.add("AI323726");
				expectedSynonyms.add("MGC124043");
				assertEquals(expectedSynonyms, record.getSynonyms());
				Set<String> expectedDBXrefs = new HashSet<String>();
				expectedDBXrefs.add("MGI:87884");
				expectedDBXrefs.add("Ensembl:ENSMUSG00000022622");
				assertEquals(expectedDBXrefs, record.getDbXrefs());
				assertEquals("15", record.getChromosome());
				assertEquals("15 E-F|15 48.6 cM", record.getMapLocation());
				assertEquals("acrosin prepropeptide", record.getDescription());
				assertEquals("protein-coding", record.getTypeOfGene());
				assertEquals("Acr", record.getSymbolFromNomenclatureAuthority());
				assertEquals("acrosin prepropeptide", record.getFullNameFromNomenclatureAuthority());
				assertEquals("O", record.getNomenclatureStatus());
				Set<String> expectedOtherDesignations = new HashSet<String>();
				expectedOtherDesignations.add("acrosin");
				expectedOtherDesignations.add("preproacrosin");
				assertEquals(expectedOtherDesignations, record.getOtherDesignations());
				assertEquals("20080831", record.getModificationDate());
			} else {
				fail("Parser should have returned a record here.");
			}

			assertTrue(parser.hasNext());
			parser.next();
			assertTrue(parser.hasNext());
			parser.next();
			assertTrue(parser.hasNext());
			parser.next();
			assertTrue(parser.hasNext());
			parser.next();
			assertTrue(parser.hasNext());
			parser.next();
			assertFalse(parser.hasNext());

		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Parser threw an IOException");
		}

	}

	@Test
	public void testGetGeneSymbol2EntrezGeneIDMap() throws Exception {
		boolean toLowerCase = false;
		Map<String, Set<Integer>> geneSymbol2EntrezGeneIDMap = EntrezGeneInfoFileParser.getGeneSymbol2EntrezGeneIDMap(
				getResourceFromClasspath(this.getClass(), SAMPLE_ENTREZGENEINFO_FILE_NAME), 10090, toLowerCase);

		Set<Integer> abcc2Set = new HashSet<Integer>();
		abcc2Set.add(12780);
		Set<Integer> abi1Set = new HashSet<Integer>();
		abi1Set.add(11308);
		Set<Integer> acrSet = new HashSet<Integer>();
		acrSet.add(11434);
		Set<Integer> rdxSet = new HashSet<Integer>();
		rdxSet.add(19684);
		Set<Integer> ezrSet = new HashSet<Integer>();
		ezrSet.add(22350);
		Set<Integer> enahSet = new HashSet<Integer>();
		enahSet.add(13800);
		Set<Integer> zp2Set = new HashSet<Integer>();
		zp2Set.add(22787);
		Set<Integer> zp3Set = new HashSet<Integer>();
		zp3Set.add(22788);

		Map<String, Set<Integer>> expectedGeneSymbol2EntrezGeneIDMap = new HashMap<String, Set<Integer>>();
		expectedGeneSymbol2EntrezGeneIDMap.put("Abcc2", abcc2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("Abi1", abi1Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("Acr", acrSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("Rdx", rdxSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("Ezr", ezrSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("Enah", enahSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("Zp2", zp2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("Zp3", zp3Set);

		/* Maps should be identical */
		assertEquals(expectedGeneSymbol2EntrezGeneIDMap, geneSymbol2EntrezGeneIDMap);

		/* Now test when toLowerCase = true */
		toLowerCase = true;
		geneSymbol2EntrezGeneIDMap = EntrezGeneInfoFileParser.getGeneSymbol2EntrezGeneIDMap(getResourceFromClasspath(
				this.getClass(), SAMPLE_ENTREZGENEINFO_FILE_NAME), 10090, toLowerCase);
		expectedGeneSymbol2EntrezGeneIDMap = new HashMap<String, Set<Integer>>();
		expectedGeneSymbol2EntrezGeneIDMap.put("abcc2", abcc2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("abi1", abi1Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("acr", acrSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("rdx", rdxSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("ezr", ezrSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("enah", enahSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("zp2", zp2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("zp3", zp3Set);

		/* Maps should be identical with lowercase keys */
		assertEquals(expectedGeneSymbol2EntrezGeneIDMap, geneSymbol2EntrezGeneIDMap);

		/* Test taxon id filter */
		geneSymbol2EntrezGeneIDMap = EntrezGeneInfoFileParser.getGeneSymbol2EntrezGeneIDMap(getResourceFromClasspath(
				this.getClass(), SAMPLE_ENTREZGENEINFO_FILE_NAME), 12345, toLowerCase);
		assertEquals(0, geneSymbol2EntrezGeneIDMap.size());

	}

	@Test
	public void testGetGeneSymbol2EntrezGeneIDMap_withSynonyms() throws Exception {
		boolean toLowerCase = false;
		Map<String, Set<Integer>> geneSymbol2EntrezGeneIDMap = EntrezGeneInfoFileParser
				.getGeneSymbol2EntrezGeneIDMap_withSynonyms(getResourceFromClasspath(this.getClass(),
						SAMPLE_ENTREZGENEINFO_FILE_NAME), 10090, toLowerCase);

		Set<Integer> abcc2Set = new HashSet<Integer>();
		abcc2Set.add(12780);
		Set<Integer> aI173996Set = new HashSet<Integer>();
		aI173996Set.add(12780);
		Set<Integer> abc30Set = new HashSet<Integer>();
		abc30Set.add(12780);
		Set<Integer> cmoatSet = new HashSet<Integer>();
		cmoatSet.add(12780);
		Set<Integer> mrp2Set = new HashSet<Integer>();
		mrp2Set.add(12780);
		Set<Integer> cMRPSet = new HashSet<Integer>();
		cMRPSet.add(12780);

		Set<Integer> abi1Set = new HashSet<Integer>();
		abi1Set.add(11308);
		Set<Integer> e3B1Set = new HashSet<Integer>();
		e3B1Set.add(11308);
		Set<Integer> mGC6064Set = new HashSet<Integer>();
		mGC6064Set.add(11308);
		Set<Integer> nAP1Set = new HashSet<Integer>();
		nAP1Set.add(11308);
		Set<Integer> ssh3bp1Set = new HashSet<Integer>();
		ssh3bp1Set.add(11308);

		Set<Integer> acrSet = new HashSet<Integer>();
		acrSet.add(11434);
		Set<Integer> aI323726Set = new HashSet<Integer>();
		aI323726Set.add(11434);
		Set<Integer> mGC124043Set = new HashSet<Integer>();
		mGC124043Set.add(11434);

		Set<Integer> rdxSet = new HashSet<Integer>();
		rdxSet.add(19684);

		Set<Integer> aA516625Set = new HashSet<Integer>();
		aA516625Set.add(19684);

		Set<Integer> ezrSet = new HashSet<Integer>();
		ezrSet.add(22350);
		Set<Integer> aW146364Set = new HashSet<Integer>();
		aW146364Set.add(22350);
		Set<Integer> mGC107499Set = new HashSet<Integer>();
		mGC107499Set.add(22350);
		Set<Integer> r75297Set = new HashSet<Integer>();
		r75297Set.add(22350);
		Set<Integer> vil2Set = new HashSet<Integer>();
		vil2Set.add(22350);
		Set<Integer> p81Set = new HashSet<Integer>();
		p81Set.add(22350);

		Set<Integer> enahSet = new HashSet<Integer>();
		enahSet.add(13800);
		Set<Integer> aI464316Set = new HashSet<Integer>();
		aI464316Set.add(13800);
		Set<Integer> aW045240Set = new HashSet<Integer>();
		aW045240Set.add(13800);
		Set<Integer> menaSet = new HashSet<Integer>();
		menaSet.add(13800);
		Set<Integer> ndpp1Set = new HashSet<Integer>();
		ndpp1Set.add(13800);
		Set<Integer> wBP8Set = new HashSet<Integer>();
		wBP8Set.add(13800);

		Set<Integer> zp2Set = new HashSet<Integer>();
		zp2Set.add(22787);
		Set<Integer> ZpDash2Set = new HashSet<Integer>();
		ZpDash2Set.add(22787);

		Set<Integer> zp3Set = new HashSet<Integer>();
		zp3Set.add(22788);
		Set<Integer> ZpDash3Set = new HashSet<Integer>();
		ZpDash3Set.add(22788);

		Map<String, Set<Integer>> expectedGeneSymbol2EntrezGeneIDMap = new HashMap<String, Set<Integer>>();
		expectedGeneSymbol2EntrezGeneIDMap.put("Abcc2", abcc2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("AI173996", aI173996Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("Abc30", abc30Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("Cmoat", cmoatSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("Mrp2", mrp2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("cMRP", cMRPSet);

		expectedGeneSymbol2EntrezGeneIDMap.put("Abi1", abi1Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("E3B1", e3B1Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("MGC6064", mGC6064Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("NAP1", nAP1Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("Ssh3bp1", ssh3bp1Set);

		expectedGeneSymbol2EntrezGeneIDMap.put("Acr", acrSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("AI323726", aI323726Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("MGC124043", mGC124043Set);

		expectedGeneSymbol2EntrezGeneIDMap.put("Rdx", rdxSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("AA516625", aA516625Set);

		expectedGeneSymbol2EntrezGeneIDMap.put("Ezr", ezrSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("AW146364", aW146364Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("MGC107499", mGC107499Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("R75297", r75297Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("Vil2", vil2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("p81", p81Set);

		expectedGeneSymbol2EntrezGeneIDMap.put("Enah", enahSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("AI464316", aI464316Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("AW045240", aW045240Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("Mena", menaSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("Ndpp1", ndpp1Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("WBP8", wBP8Set);

		expectedGeneSymbol2EntrezGeneIDMap.put("Zp2", zp2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("Zp-2", ZpDash2Set);

		expectedGeneSymbol2EntrezGeneIDMap.put("Zp3", zp3Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("Zp-3", ZpDash3Set);

		/* Maps should be identical */
		assertEquals(expectedGeneSymbol2EntrezGeneIDMap, geneSymbol2EntrezGeneIDMap);

		/* Now test with toLowerCase = true */
		toLowerCase = true;
		geneSymbol2EntrezGeneIDMap = EntrezGeneInfoFileParser.getGeneSymbol2EntrezGeneIDMap_withSynonyms(
				getResourceFromClasspath(this.getClass(), SAMPLE_ENTREZGENEINFO_FILE_NAME), 10090, toLowerCase);
		expectedGeneSymbol2EntrezGeneIDMap = new HashMap<String, Set<Integer>>();
		expectedGeneSymbol2EntrezGeneIDMap.put("abcc2", abcc2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("ai173996", aI173996Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("abc30", abc30Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("cmoat", cmoatSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("mrp2", mrp2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("cmrp", cMRPSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("abi1", abi1Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("e3b1", e3B1Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("mgc6064", mGC6064Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("nap1", nAP1Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("ssh3bp1", ssh3bp1Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("acr", acrSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("ai323726", aI323726Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("mgc124043", mGC124043Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("rdx", rdxSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("aa516625", aA516625Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("ezr", ezrSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("aw146364", aW146364Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("mgc107499", mGC107499Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("r75297", r75297Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("vil2", vil2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("p81", p81Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("enah", enahSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("ai464316", aI464316Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("aw045240", aW045240Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("mena", menaSet);
		expectedGeneSymbol2EntrezGeneIDMap.put("ndpp1", ndpp1Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("wbp8", wBP8Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("zp2", zp2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("zp-2", ZpDash2Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("zp3", zp3Set);
		expectedGeneSymbol2EntrezGeneIDMap.put("zp-3", ZpDash3Set);

		/* Maps should be identical with lowercase keys */
		assertEquals(expectedGeneSymbol2EntrezGeneIDMap, geneSymbol2EntrezGeneIDMap);

	}

	@Test
	public void testIteratorPattern() throws Exception {
		EntrezGeneInfoFileParser parser = new EntrezGeneInfoFileParser(getResourceFromClasspath(this.getClass(),
				SAMPLE_ENTREZGENEINFO_FILE_NAME));
		assertTrue(parser.hasNext());
		assertTrue(parser.hasNext());
		assertTrue(parser.hasNext());
		assertTrue(parser.hasNext());
		assertTrue(parser.hasNext());
		assertTrue(parser.hasNext());
		assertTrue(parser.hasNext());
		assertTrue(parser.hasNext());
		assertTrue("Repeated calls to parser.hasNext() should not advance the iterator.", parser.hasNext());
	}

	@Test
	public void testGetEntrezGeneIDAsString2GeneSymbolMap() throws Exception {
		Map<String, String> expectedGeneID2GeneNameMap = new HashMap<String, String>();
		expectedGeneID2GeneNameMap.put("12780", "Abcc2");
		expectedGeneID2GeneNameMap.put("11308", "Abi1");
		expectedGeneID2GeneNameMap.put("11434", "Acr");
		expectedGeneID2GeneNameMap.put("19684", "Rdx");
		expectedGeneID2GeneNameMap.put("22350", "Ezr");
		expectedGeneID2GeneNameMap.put("13800", "Enah");
		expectedGeneID2GeneNameMap.put("22787", "Zp2");
		expectedGeneID2GeneNameMap.put("22788", "Zp3");

		assertEquals("Maps should match exactly", expectedGeneID2GeneNameMap, EntrezGeneInfoFileParser
				.getEntrezGeneIDAsString2GeneSymbolMap(getResourceFromClasspath(this.getClass(),
						SAMPLE_ENTREZGENEINFO_FILE_NAME), 10090));
	}

	@Test
	public void testGetEntrezGeneIDAsString2GeneNameMap() throws Exception {
		Map<String, String> expectedGeneID2GeneNameMap = new HashMap<String, String>();
		expectedGeneID2GeneNameMap.put("12780", "ATP-binding cassette, sub-family C (CFTR/MRP), member 2");
		expectedGeneID2GeneNameMap.put("11308", "abl-interactor 1");
		expectedGeneID2GeneNameMap.put("11434", "acrosin prepropeptide");
		expectedGeneID2GeneNameMap.put("19684", "radixin");
		expectedGeneID2GeneNameMap.put("22350", "ezrin");
		expectedGeneID2GeneNameMap.put("13800", "enabled homolog (Drosophila)");
		expectedGeneID2GeneNameMap.put("22787", "zona pellucida glycoprotein 2");
		expectedGeneID2GeneNameMap.put("22788", "zona pellucida glycoprotein 3");

		assertEquals("Maps should match exactly", expectedGeneID2GeneNameMap, EntrezGeneInfoFileParser
				.getEntrezGeneIDAsString2GeneNameMap(getResourceFromClasspath(this.getClass(),
						SAMPLE_ENTREZGENEINFO_FILE_NAME), 10090));
	}

	@Test
	public void testGetEntrezGeneID2TaxonomyIDMap() throws Exception {
		Set<Integer> geneIDs2Include = CollectionsUtil.createSet(11308, 22350, 22787);
		Map<Integer, Integer> expectedEntrezGeneID2TaxonomyIDMap = CollectionsUtil.createMap(11308, 10090, 22350,
				10090, 22787, 10090);
		Map<Integer, Integer> entrezGeneID2TaxonomyIDMap = EntrezGeneInfoFileParser.getEntrezGeneID2TaxonomyIDMap(
				getResourceFromClasspath(this.getClass(), SAMPLE_ENTREZGENEINFO_FILE_NAME), geneIDs2Include);
		assertEquals(String.format("Map should have 3 entries."), expectedEntrezGeneID2TaxonomyIDMap,
				entrezGeneID2TaxonomyIDMap);
	}
}
