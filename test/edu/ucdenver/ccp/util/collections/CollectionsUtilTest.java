package edu.ucdenver.ccp.util.collections;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

}
