/* Copyright (C) 2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP common library.
 * The CCP common library is free software: you can redistribute it and/or
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
 */

package edu.ucdenver.ccp.common.collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class CollectionsUtilTest {

	@Test
	public void testCreateSetWithValidInput() {
		Set<String> expectedSet = new HashSet<String>();
		expectedSet.add("1");
		expectedSet.add("2");
		expectedSet.add("3");

		assertEquals(String.format("Sets should be equal."), expectedSet, CollectionsUtil.createSet("1", "2", "3"));
		assertEquals(String.format("Sets should be equal."), expectedSet, CollectionsUtil.createSet("1", "2", "3", "1",
				"2", "3", "3"));
	}

	@Test
	public void testCreateSetWithEmptyInput() {
		assertEquals(String.format("Should return empty set for no input."), new HashSet<String>(), CollectionsUtil
				.createSet());
		assertNull(String.format("Should return null for null input."), CollectionsUtil.createSet((Object[]) null));
	}

	@Test
	public void testCreateListWithValidInput() {
		List<String> expectedList = new ArrayList<String>();
		expectedList.add("2");
		expectedList.add("2");
		expectedList.add("3");

		assertEquals(String.format("Lists should be equal."), expectedList, CollectionsUtil.createList("2", "2", "3"));
	}

	@Test
	public void testCreateListWithEmptyInput() {
		assertEquals(String.format("Should return empty List for no input."), new ArrayList<String>(), CollectionsUtil
				.createList());
	}

	@Test
	public void testCreateSortedList() {
		assertEquals(String.format("List should be sorted."), CollectionsUtil.createList(1, 2, 3, 4, 5),
				CollectionsUtil.createSortedList(CollectionsUtil.createList(5, 3, 2, 1, 4)));
	}

	@Test
	public void testParseInts() {
		List<String> toParseList = CollectionsUtil.createList("5", "6", "789", "1", "-4", "0");
		List<Integer> expectedIntList = CollectionsUtil.createList(5, 6, 789, 1, -4, 0);
		assertEquals(String.format("The list of Strings should have been converted into a list of Integers."),
				expectedIntList, CollectionsUtil.parseInts(toParseList));
	}

	@Test
	public void testArray2Set() {
		Set<String> expectedSet = new HashSet<String>();
		expectedSet.add("1");
		expectedSet.add("2");
		expectedSet.add("3");

		assertEquals(String.format("Simple conversion from array to set."), expectedSet, CollectionsUtil
				.array2Set(new String[] { "1", "2", "3" }));
		assertEquals(String.format("Lossy conversion from array to set."), expectedSet, CollectionsUtil
				.array2Set(new String[] { "1", "2", "3", "3", "3", "2" }));
	}

	@Test
	public void testCreateZeroBasedSequence() {
		int[] expected = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
		assertArrayEquals("Should have 8 sequential members.", expected, CollectionsUtil.createZeroBasedSequence(8));
	}

	@Test
	public void testCreateMap() {
		Map<String, String> map = CollectionsUtil.createMap("key1", "value1", "key2", "value2", "key3", "value3");
		Map<String, String> expectedMap = new HashMap<String, String>();
		expectedMap.put("key1", "value1");
		expectedMap.put("key2", "value2");
		expectedMap.put("key3", "value3");
		assertEquals(String.format("Maps should be identical."), expectedMap, map);
	}

	@Test
	public void testCreateMap_2keyValuePairs() {
		Map<Integer, String> map = CollectionsUtil.createMap(1, "value1", 2, "value2");
		Map<Integer, String> expectedMap = new HashMap<Integer, String>();
		expectedMap.put(1, "value1");
		expectedMap.put(2, "value2");
		assertEquals(String.format("Maps should be identical."), expectedMap, map);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateMap_2keyValuePairs_duplicateKeys() {
		CollectionsUtil.createMap(1, "value1", 1, "value2");
	}

	@Test
	public void testAddToOne2ManyMap() {
		Map<Integer, Set<String>> expectedMap = new HashMap<Integer, Set<String>>();
		expectedMap.put(1, CollectionsUtil.createSet("a", "b", "c"));
		expectedMap.put(2, CollectionsUtil.createSet("d"));

		Map<Integer, Set<String>> map = new HashMap<Integer, Set<String>>();
		CollectionsUtil.addToOne2ManyUniqueMap(1, "a", map);
		CollectionsUtil.addToOne2ManyUniqueMap(2, "d", map);
		CollectionsUtil.addToOne2ManyUniqueMap(1, "b", map);
		CollectionsUtil.addToOne2ManyUniqueMap(1, "c", map);

		assertEquals(String.format("Maps should be equal."), expectedMap, map);
	}

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
		List<List<String>> tuples = CollectionsUtil.createList(CollectionsUtil
				.computeCombinations(collectionOfListsToCombine));

		assertEquals(String.format("Tuples should be as expected."), expectedTuples, tuples);
	}

	@Test
	public void testCombineMaps() {
		Map<String, String> inputMap1 = CollectionsUtil.createMap("1", "blue", "2", "red", "3", "green");
		Map<String, String> inputMap2 = CollectionsUtil.createMap("4", "purple", "5", "yellow", "1", "green");

		@SuppressWarnings("unchecked")
		Map<String, String> combinedMap = CollectionsUtil.combineMaps(inputMap1, inputMap2);

		Map<String, String> expectedMap = CollectionsUtil.createMap("1", "green", "2", "red", "3", "green", "4",
				"purple", "5", "yellow");
		assertEquals(String.format("Map not as expected."), expectedMap, combinedMap);
	}

	@Test
	public void testCombineUniqueMaps() {
		Map<String, Set<String>> inputMap1 = new HashMap<String, Set<String>>();
		inputMap1.put("colors", CollectionsUtil.createSet("blue", "red", "yellow"));
		inputMap1.put("numbers", CollectionsUtil.createSet("one", "two", "three"));

		Map<String, Set<String>> inputMap2 = new HashMap<String, Set<String>>();
		inputMap2.put("colors", CollectionsUtil.createSet("green", "red", "purple"));

		Map<String, Set<String>> expectedMap = new HashMap<String, Set<String>>();
		expectedMap.put("colors", CollectionsUtil.createSet("blue", "red", "yellow", "green", "purple"));
		expectedMap.put("numbers", CollectionsUtil.createSet("one", "two", "three"));

		@SuppressWarnings("unchecked")
		Map<String, Set<String>> combinedMap = CollectionsUtil.combineUniqueMaps(inputMap1, inputMap2);
		assertEquals(String.format("Expected map not the same as combined."), expectedMap, combinedMap);
	}

	@Test
	public void testInitHashMap() {
		String key = "1";
		Integer value = 1;
		Map<String, Integer> map = CollectionsUtil.initHashMap();
		map.put(key, value);
		assertEquals(value, map.get(key));
	}

	@Test
	public void testCreateDelimitedString() {
		List<String> list = CollectionsUtil.createList("1", "2", "3");
		String delimitedStr = CollectionsUtil.createDelimitedString(list, "|");
		String expectedStr = "1|2|3";
		assertEquals(String.format("output or createDelimitedString not as expected."), expectedStr, delimitedStr);

		List<Integer> numberList = CollectionsUtil.createList(1, 2, 3);
		delimitedStr = CollectionsUtil.createDelimitedString(numberList, "|");
		assertEquals(String.format("output or createDelimitedString not as expected."), expectedStr, delimitedStr);
	}

	@Test
	public void testToString() {
		Set<Integer> integerSet = CollectionsUtil.createSet(1, 2, 3, 4, 5);
		Set<String> strings = new HashSet<String>(CollectionsUtil.toString(integerSet));
		assertEquals(String.format("Set should now contain strings."), CollectionsUtil.createSet("1", "2", "3", "4",
				"5"), strings);
	}

	@Test
	public void testConsolidateSets() {
		Collection<Set<String>> sets = new ArrayList<Set<String>>();
		sets.add(CollectionsUtil.createSet("1", "2"));
		sets.add(CollectionsUtil.createSet("2", "3"));

		assertEquals(String.format("Sets should be consolidated"), CollectionsUtil.createSet("1", "2", "3"),
				CollectionsUtil.consolidateSets(sets));

	}

}
