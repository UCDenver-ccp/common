package edu.ucdenver.ccp.common.collections;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CombinationsUtil.Pair;
import edu.ucdenver.ccp.common.collections.CombinationsUtil.SelfPairing;

public class CombinationsUtilTest {

	@Test
	public void testComputeCombinations() {
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

		List<Collection<String>> collectionOfListsToCombine = CollectionsUtil.createList(list0, list1, list2, list3);
		List<List<String>> tuples = CollectionsUtil.createList(CombinationsUtil
				.computeCombinations(collectionOfListsToCombine));

		assertEquals(String.format("Tuples should be as expected."), expectedTuples, tuples);
	}

	@Test
	public void testComputePairwiseCombinations_SinglePair() {
		Set<String> pairMembers = CollectionsUtil.createSet("A", "B");
		List<CombinationsUtil.Pair<String>> pairs = CollectionsUtil.createList(CombinationsUtil
				.computePairwiseCombinations(pairMembers));
		List<CombinationsUtil.Pair<String>> expectedPairs = new ArrayList<CombinationsUtil.Pair<String>>();
		expectedPairs.add(new CombinationsUtil.Pair<String>("A", "B"));

		assertEquals(String.format("Expected a single pair (A,B)"), expectedPairs, pairs);

	}

	@Test
	public void testPairEquals() {
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
	public void testComputePairwiseCombinations_MultiplePairs() {
		Set<String> pairMembers = CollectionsUtil.createSet("A", "B", "C", "D");
		Set<CombinationsUtil.Pair<String>> pairs = new HashSet<CombinationsUtil.Pair<String>>(
				CollectionsUtil.createList(CombinationsUtil.computePairwiseCombinations(pairMembers)));
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
	public void testComputePairwiseCombinations_OneEntry() {
		Set<String> pairMembers = CollectionsUtil.createSet("A");
		List<CombinationsUtil.Pair<String>> pairs = CollectionsUtil.createList(CombinationsUtil
				.computePairwiseCombinations(pairMembers));

		assertTrue(String.format("Cannot make a pair with a single entry, so no pairs should be returned"),
				pairs.isEmpty());

	}

	@Test
	public void testComputePairwiseCombinations_ZeroEntries() {
		Set<String> pairMembers = new HashSet<String>();
		List<CombinationsUtil.Pair<String>> pairs = CollectionsUtil.createList(CombinationsUtil
				.computePairwiseCombinations(pairMembers));

		assertTrue(String.format("Cannot make a pair with empty input, so no pairs should be returned"),
				pairs.isEmpty());

	}

	@Test
	public void testComputePairwiseCombinations_2CollectionInput() {
		Collection<String> input1 = CollectionsUtil.createList("A", "B", "C");
		Collection<String> input2 = CollectionsUtil.createList("A", "X", "Y");
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
		HashSet<CombinationsUtil.Pair<String>> pairs = new HashSet<CombinationsUtil.Pair<String>>(
				CollectionsUtil.createList(CombinationsUtil.computePairwiseCombinations(input1, input2,
						SelfPairing.ALLOW)));
		assertEquals(String.format("Pairs not as expected"), expectedPairs, pairs);
	}

	@Test
	public void testComputePairwiseCombinations_2CollectionInput_ProhibitSelfPairs() {
		Collection<String> input1 = CollectionsUtil.createList("A", "B", "C");
		Collection<String> input2 = CollectionsUtil.createList("A", "X", "Y");
		Set<CombinationsUtil.Pair<String>> expectedPairs = new HashSet<CombinationsUtil.Pair<String>>();
		expectedPairs.add(new CombinationsUtil.Pair<String>("A", "X"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("A", "Y"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("B", "A"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("B", "X"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("B", "Y"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("C", "A"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("C", "X"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("C", "Y"));
		HashSet<CombinationsUtil.Pair<String>> pairs = new HashSet<CombinationsUtil.Pair<String>>(
				CollectionsUtil.createList(CombinationsUtil.computePairwiseCombinations(input1, input2,
						SelfPairing.PROHIBIT)));
		assertEquals(String.format("Pairs not as expected"), expectedPairs, pairs);
	}

	@Test
	public void testComputePairwiseCombinations_2CollectionInput_emptyInput() {
		Collection<String> input1 = Collections.emptyList();
		Collection<String> input2 = CollectionsUtil.createList("A", "X", "Y");
		Set<CombinationsUtil.Pair<String>> expectedPairs = Collections.emptySet();
		HashSet<CombinationsUtil.Pair<String>> pairs = new HashSet<CombinationsUtil.Pair<String>>(
				CollectionsUtil.createList(CombinationsUtil.computePairwiseCombinations(input1, input2,
						SelfPairing.ALLOW)));
		assertEquals(String.format("Pairs not as expected"), expectedPairs, pairs);
	}

	/**
	 * make sure that the order of returned pairs is the same no matter the
	 * order of the input
	 */
	@Test
	public void testComputePairwiseCombinations_reproducibleOrder() {
		List<String> input = CollectionsUtil.createList("a", "b", "c", "d");

		Set<CombinationsUtil.Pair<String>> expectedPairs = new HashSet<CombinationsUtil.Pair<String>>();
		expectedPairs.add(new CombinationsUtil.Pair<String>("a", "b"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("a", "c"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("a", "d"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("b", "c"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("b", "d"));
		expectedPairs.add(new CombinationsUtil.Pair<String>("c", "d"));
		HashSet<CombinationsUtil.Pair<String>> pairs = new HashSet<CombinationsUtil.Pair<String>>(
				CollectionsUtil.createList(CombinationsUtil.computePairwiseCombinations(new HashSet<String>(input))));
		assertEquals(String.format("Pairs not as expected"), expectedPairs, pairs);

		input = CollectionsUtil.createList("d", "b", "a", "c");
		pairs = new HashSet<CombinationsUtil.Pair<String>>(CollectionsUtil.createList(CombinationsUtil
				.computePairwiseCombinations(new HashSet<String>(input))));
		assertEquals(String.format("Pairs not as expected"), expectedPairs, pairs);
	}

}
