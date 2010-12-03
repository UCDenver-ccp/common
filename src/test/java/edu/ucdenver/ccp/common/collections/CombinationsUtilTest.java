package edu.ucdenver.ccp.common.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CombinationsUtil.SelfPairing;


public class CombinationsUtilTest {

	
	@Test
	public void testComputeCombinations() throws Exception {
		Collection<String> list0 = CollectionsUtil.createList("A", "B");
		Collection<String> list1 = CollectionsUtil.createList("1", "2", "3");
		Collection<String> list2 = CollectionsUtil.createList("X");
		Collection<String> list3 = CollectionsUtil.createList("7", "8");

		List<List<String>> expectedTuples = new ArrayList<List<String>>();
		expectedTuples.add(CollectionsUtil.createList("A", "1", "X", "7"));
		expectedTuples.add(CollectionsUtil.createList("A", "1", "X", "8"));
		expectedTuples.add(CollectionsUtil.createList("A", "2", "X", "7"));
		expectedTuples.add(CollectionsUtil.createList("A", "2", "X", "8"));
		expectedTuples.add(CollectionsUtil.createList("A", "3", "X", "7"));
		expectedTuples.add(CollectionsUtil.createList("A", "3", "X", "8"));
		expectedTuples.add(CollectionsUtil.createList("B", "1", "X", "7"));
		expectedTuples.add(CollectionsUtil.createList("B", "1", "X", "8"));
		expectedTuples.add(CollectionsUtil.createList("B", "2", "X", "7"));
		expectedTuples.add(CollectionsUtil.createList("B", "2", "X", "8"));
		expectedTuples.add(CollectionsUtil.createList("B", "3", "X", "7"));
		expectedTuples.add(CollectionsUtil.createList("B", "3", "X", "8"));

		@SuppressWarnings("unchecked")
		List<Collection<String>> collectionOfListsToCombine = CollectionsUtil.createList(list0, list1, list2, list3);
		List<List<String>> tuples = CollectionsUtil.createList(CombinationsUtil
				.computeCombinations(collectionOfListsToCombine));

		assertEquals(String.format("Tuples should be as expected."), expectedTuples, tuples);
	}

	@Test
	public void testComputePairwiseCombinations_SinglePair() throws Exception {
		Set<String> pairMembers = CollectionsUtil.createSet("A", "B");
		List<CombinationsUtil.Pair<String>> pairs = CollectionsUtil.createList(CombinationsUtil.computePairwiseCombinations(pairMembers));
		List<CombinationsUtil.Pair<String>> expectedPairs = new ArrayList<CombinationsUtil.Pair<String>>();
		expectedPairs.add(new CombinationsUtil.Pair<String>("A", "B"));

		assertEquals(String.format("Expected a single pair (A,B)"), expectedPairs, pairs);

	}

	@Test
	public void testPairEquals() throws Exception {
		assertTrue(String.format("Identical pairs should match."),
				new CombinationsUtil.Pair<String>("A", "B").equals(new CombinationsUtil.Pair<String>("A", "B")));
		assertFalse(String.format("Mismatched pairs should not match."),
				new CombinationsUtil.Pair<String>("A", "B").equals(new CombinationsUtil.Pair<String>("C", "B")));
		assertTrue(String.format("Opposite pairs should match."),
				new CombinationsUtil.Pair<String>("B", "A").equals(new CombinationsUtil.Pair<String>("A", "B")));
		assertEquals("hashcodes should equals", new CombinationsUtil.Pair<String>("A", "B").hashCode(),
				new CombinationsUtil.Pair<String>("B", "A").hashCode());
	}

	@Test
	public void testComputePairwiseCombinations_MultiplePairs() throws Exception {
		Set<String> pairMembers = CollectionsUtil.createSet("A", "B", "C", "D");
		Set<CombinationsUtil.Pair<String>> pairs = new HashSet<CombinationsUtil.Pair<String>>(CollectionsUtil.createList(CombinationsUtil
				.computePairwiseCombinations(pairMembers)));
		Set<CombinationsUtil.Pair<String>> expectedPairs = new HashSet<CombinationsUtil.Pair<String>>();
		expectedPairs.add(new CombinationsUtil.Pair<String>("A", "B"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("A", "C"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("A", "D"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("B", "C"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("B", "D"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("C", "D"));

		assertEquals(String.format("Expected 6 pairs."), expectedPairs, pairs);

	}
	
	@Test
	public void testComputePairwiseCombinations_OneEntry() throws Exception {
		Set<String> pairMembers = CollectionsUtil.createSet("A");
		List<CombinationsUtil.Pair<String>> pairs = CollectionsUtil.createList(CombinationsUtil.computePairwiseCombinations(pairMembers));

		assertTrue(String.format("Cannot make a pair with a single entry, so no pairs should be returned"), pairs.isEmpty());

	}
	@Test
	public void testComputePairwiseCombinations_ZeroEntries() throws Exception {
		Set<String> pairMembers = new HashSet<String>();
		List<CombinationsUtil.Pair<String>> pairs = CollectionsUtil.createList(CombinationsUtil.computePairwiseCombinations(pairMembers));
		
		assertTrue(String.format("Cannot make a pair with empty input, so no pairs should be returned"), pairs.isEmpty());
		
	}
	
	@Test
	public void testComputePairwiseCombinations_2CollectionInput() throws Exception {
		Collection<String> input1 = CollectionsUtil.createList("A","B","C");
		Collection<String> input2 = CollectionsUtil.createList("A","X","Y");
		Set<CombinationsUtil.Pair<String>> expectedPairs = new HashSet<CombinationsUtil.Pair<String>>();
		expectedPairs.add(new CombinationsUtil.Pair<String>("A", "A"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("A", "X"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("A", "Y"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("B", "A"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("B", "X"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("B", "Y"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("C", "A"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("C", "X"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("C", "Y"));
		HashSet<CombinationsUtil.Pair<String>> pairs = new HashSet<CombinationsUtil.Pair<String>>(CollectionsUtil.createList(CombinationsUtil.computePairwiseCombinations(input1, input2, SelfPairing.ALLOW)));
		assertEquals(String.format("Pairs not as expected"), expectedPairs, pairs);
	}
	
	@Test
	public void testComputePairwiseCombinations_2CollectionInput_ProhibitSelfPairs() throws Exception {
		Collection<String> input1 = CollectionsUtil.createList("A","B","C");
		Collection<String> input2 = CollectionsUtil.createList("A","X","Y");
		Set<CombinationsUtil.Pair<String>> expectedPairs = new HashSet<CombinationsUtil.Pair<String>>();
		expectedPairs.add(new CombinationsUtil.Pair<String>("A", "X"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("A", "Y"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("B", "A"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("B", "X"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("B", "Y"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("C", "A"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("C", "X"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("C", "Y"));
		HashSet<CombinationsUtil.Pair<String>> pairs = new HashSet<CombinationsUtil.Pair<String>>(CollectionsUtil.createList(CombinationsUtil.computePairwiseCombinations(input1, input2, SelfPairing.PROHIBIT)));
		assertEquals(String.format("Pairs not as expected"), expectedPairs, pairs);
	}
	
	@Test
	public void testComputePairwiseCombinations_2CollectionInput_emptyInput() throws Exception {
		Collection<String> input1 = Collections.emptyList();
		Collection<String> input2 = CollectionsUtil.createList("A","X","Y");
		Set<CombinationsUtil.Pair<String>> expectedPairs = Collections.emptySet();
		HashSet<CombinationsUtil.Pair<String>> pairs = new HashSet<CombinationsUtil.Pair<String>>(CollectionsUtil.createList(CombinationsUtil.computePairwiseCombinations(input1, input2, SelfPairing.ALLOW)));
		assertEquals(String.format("Pairs not as expected"), expectedPairs, pairs);
	}
	
}
