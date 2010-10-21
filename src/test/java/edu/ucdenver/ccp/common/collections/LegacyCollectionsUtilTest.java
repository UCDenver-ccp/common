package edu.ucdenver.ccp.common.collections;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LegacyCollectionsUtilTest {

	/*
	 * The warnings being suppressed in this method are due to the creation of a legacy raw-type
	 * List, which is necessary in order to test the LegacyCollectionsUtil.checkList() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCheckList_HomogeneousListInput() throws Exception {
		List<String> expectedList = CollectionsUtil.createList("string 1", "string 2");

		List legacyList = new ArrayList();
		legacyList.add("string 1");
		legacyList.add("string 2");

		List<String> list = LegacyCollectionsUtil.checkList(legacyList, String.class);
		assertEquals(String.format("Generic list should convert without error and match the expected."), expectedList,
				list);
	}

	/*
	 * The warnings being suppressed in this method are due to the creation of a legacy raw-type
	 * List, which is necessary in order to test the LegacyCollectionsUtil.checkList() method.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = ClassCastException.class)
	public void testCheckList_HeterogeneousListInput() throws Exception {
		List legacyList = new ArrayList();
		legacyList.add("string 1");
		legacyList.add(new Integer(1));

		LegacyCollectionsUtil.checkList(legacyList, String.class);
	}

}
