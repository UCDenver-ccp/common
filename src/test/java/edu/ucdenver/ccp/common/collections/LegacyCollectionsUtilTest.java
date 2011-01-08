package edu.ucdenver.ccp.common.collections;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import edu.ucdenver.ccp.common.test.TestUtil;

public class LegacyCollectionsUtilTest {

	/*
	 * The warnings being suppressed in this method are due to the creation of a legacy raw-type
	 * List, which is necessary in order to test the LegacyCollectionsUtil.checkList() method.
	 */
	@Test
	public void testCheckList_HomogeneousListInput() {
		List<String> expectedList = CollectionsUtil.createList("string 1", "string 2");

		@SuppressWarnings("rawtypes")
		List legacyList = initRawTypeList("string 1", "string 2");

		List<String> list = LegacyCollectionsUtil.checkList(legacyList, String.class);
		assertEquals(String.format("Generic list should convert without error and match the expected."), expectedList,
				list);
	}

	/*
	 * The warnings being suppressed in this method are due to the creation of a legacy raw-type
	 * List, which is necessary in order to test the LegacyCollectionsUtil.checkList() method.
	 */
	@Test(expected = ClassCastException.class)
	public void testCheckList_HeterogeneousListInput() {
		@SuppressWarnings("rawtypes")
		List legacyList = initRawTypeList("string 1", new Integer(1));
		LegacyCollectionsUtil.checkList(legacyList, String.class);
	}

	/*
	 * The warnings being suppressed in this method are due to the creation of a legacy raw-type
	 * Iterator, which is necessary in order to test the LegacyCollectionsUtil.checkIterator()
	 * method.
	 */
	@Test
	public void testCheckIterator_HomogeneousInput() {
		String value1 = "1";
		String value2 = "2";
		String value3 = "3";

		@SuppressWarnings("rawtypes")
		Iterator legacyIter = initRawTypeList(value1, value2, value3).iterator();

		Iterator<String> generifiedIter = LegacyCollectionsUtil.checkIterator(legacyIter, String.class);
		TestUtil.checkNext(generifiedIter, value1);
		TestUtil.checkNext(generifiedIter, value2);
		TestUtil.checkNext(generifiedIter, value3);
	}

	/*
	 * The warnings being suppressed in this method are due to the creation of a legacy raw-type
	 * Iterator, which is necessary in order to test the LegacyCollectionsUtil.checkIterator()
	 * method.
	 */
	@Test(expected = ClassCastException.class)
	public void testCheckIterator_HeterogeneousInput() {
		String value1 = "1";
		String value2 = "2";
		Integer value3 = new Integer(3);

		@SuppressWarnings("rawtypes")
		Iterator legacyIter = initRawTypeList(value1, value2, value3).iterator();

		Iterator<String> generifiedIter = LegacyCollectionsUtil.checkIterator(legacyIter, String.class);
		TestUtil.checkNext(generifiedIter, value1);
		TestUtil.checkNext(generifiedIter, value2);
		generifiedIter.next();
	}

	/**
	 * Initialize a raw-type list using the input list members
	 * 
	 * @param listMembers
	 * @return
	 */
	/*
	 * The warnings being suppressed in this method are due to the intentional creation of a legacy raw-type
	 * List
	 */
	private @SuppressWarnings("rawtypes") List initRawTypeList(Object... listMembers) {
		return Arrays.asList(listMembers);
	}

}
