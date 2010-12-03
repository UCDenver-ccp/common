package edu.ucdenver.ccp.common.collections;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class IteratorUtilTest {
	
	@Test
	public void testConsolidateIterator() throws Exception {
		List<Integer> list1 = CollectionsUtil.createList(1, 2, 3, 4);
		List<Integer> list2 = CollectionsUtil.createList(5, 6, 7, 8);
		List<Integer> emptyList = new ArrayList<Integer>();
		List<Integer> list4 = CollectionsUtil.createList(9, 10, 11, 12);

		List<Iterator<Integer>> lists = new ArrayList<Iterator<Integer>>();
		lists.add(list1.iterator());
		lists.add(list2.iterator());
		lists.add(emptyList.iterator());
		lists.add(list4.iterator());

		Iterator<Integer> consolidatedIterator = IteratorUtil.consolidate(lists);
		List<Integer> output = CollectionsUtil.createList(consolidatedIterator);

		List<Integer> expectedOutput = CollectionsUtil.createList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

		assertEquals(String.format("Integer list is as expected."), expectedOutput, output);

	}

}
