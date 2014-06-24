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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class IteratorUtilTest {
	
	@Test
	public void testConsolidateIterator() {
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
