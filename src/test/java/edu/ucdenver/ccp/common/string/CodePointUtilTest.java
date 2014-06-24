package edu.ucdenver.ccp.common.string;

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

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.file.SampleUtf8File;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;


public class CodePointUtilTest extends DefaultTestCase {

	private String utf8String;
	
	@Before
	public void setUp() throws IOException {
		utf8String = ClassPathUtil.getContentsFromClasspathResource(SampleUtf8File.class, SampleUtf8File.FILE_NAME, SampleUtf8File.ENCODING);
	}
	
	
	@Test
	public void testConvertCharacterOffsetToCodePointOffset() throws IOException {
		String expectedString = "ABCD";
		assertEquals("Should be ABCD", expectedString, utf8String.substring(19,23));
		
		assertEquals("Expect code point for A to be 16, cha offset = 19",16, CodePointUtil.convertCharacterOffsetToCodePointOffset(utf8String, 19));
	}
	
	@Test
	public void testConvertCodePointToCharOffset() {
		assertEquals("code point 12 should equal char offset 15", 15, CodePointUtil.convertCodePointOffsetToCharOffset(utf8String, 12));
	}
	
	
	
	
}
