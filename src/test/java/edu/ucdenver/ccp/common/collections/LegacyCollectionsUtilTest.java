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
		List legacyList = initRawTypeList("string 1", Integer.valueOf(1));
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
		Integer value3 = Integer.valueOf(3);

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
