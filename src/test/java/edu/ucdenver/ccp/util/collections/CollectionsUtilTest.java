package edu.ucdenver.ccp.util.collections;

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
	public void testCreateSetWithValidInput() throws Exception {
		Set<String> expectedSet = new HashSet<String>();
		expectedSet.add("1");
		expectedSet.add("2");
		expectedSet.add("3");

		assertEquals(String.format("Sets should be equal."), expectedSet, CollectionsUtil.createSet("1", "2", "3"));
		assertEquals(String.format("Sets should be equal."), expectedSet, CollectionsUtil.createSet("1", "2", "3", "1",
				"2", "3", "3"));
	}

	@Test
	public void testCreateSetWithEmptyInput() throws Exception {
		assertEquals(String.format("Should return empty set for no input."), new HashSet<String>(), CollectionsUtil
				.createSet());
		assertNull(String.format("Should return null for null input."), CollectionsUtil.createSet((Object[]) null));
	}

	@Test
	public void testCreateListWithValidInput() throws Exception {
		List<String> expectedList = new ArrayList<String>();
		expectedList.add("2");
		expectedList.add("2");
		expectedList.add("3");

		assertEquals(String.format("Lists should be equal."), expectedList, CollectionsUtil.createList("2", "2", "3"));
	}

	@Test
	public void testCreateListWithEmptyInput() throws Exception {
		assertEquals(String.format("Should return empty List for no input."), new ArrayList<String>(), CollectionsUtil
				.createList());
		assertNull(String.format("Should return null for null input."), CollectionsUtil.createList((Object[]) null));
	}

	@Test
	public void testParseInts() throws Exception {
		List<String> toParseList = CollectionsUtil.createList("5", "6", "789", "1", "-4", "0");
		List<Integer> expectedIntList = CollectionsUtil.createList(5, 6, 789, 1, -4, 0);
		assertEquals(String.format("The list of Strings should have been converted into a list of Integers."),
				expectedIntList, CollectionsUtil.parseInts(toParseList));
	}

	@Test
	public void testArray2Set() throws Exception {
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
	public void testCreateZeroBasedSequence() throws Exception {
		int[] expected = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
		assertArrayEquals("Should have 8 sequential members.", expected, CollectionsUtil.createZeroBasedSequence(8));
	}

	@Test
	public void testCreateMap() throws Exception {
		Map<String, String> map = CollectionsUtil.createMap("key1", "value1", "key2", "value2", "key3", "value3");
		Map<String, String> expectedMap = new HashMap<String, String>();
		expectedMap.put("key1", "value1");
		expectedMap.put("key2", "value2");
		expectedMap.put("key3", "value3");
		assertEquals(String.format("Maps should be identical."), expectedMap, map);
	}

	@Test
	public void testAddToOne2ManyMap() throws Exception {
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

		List<List<String>> tuples = CollectionsUtil.createList(CollectionsUtil.computeCombinations(CollectionsUtil.createList(list0, list1, list2,
				list3)));

		assertEquals(String.format("Tuples should be as expected."), expectedTuples, tuples);

	}

}
