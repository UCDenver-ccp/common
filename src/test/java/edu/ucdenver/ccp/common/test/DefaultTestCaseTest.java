/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.common.test;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;


public class DefaultTestCaseTest extends DefaultTestCase {
	
	File dir=null;
	File file=null;
	
	@Before
	public void setup() throws IOException {
		//folder.create();
		file = folder.newFile("test1.tree");
		dir = folder.getRoot();
		assertNotNull(dir);
		assertNotNull(file);
		
		System.out.println("file:" + file.getAbsolutePath());
		System.out.println("file:" + dir.getAbsolutePath());
	}
	
	/** 
	 * There's a test in nlp that fails with similar code.
	 * That code runs when the create() method is not commented out.
	 * ...and will need more investigation.
	 */
	@Test
	public void test() {
		assertNotNull(dir);
		assertNotNull(file);
		
		System.out.println("file:" + file.getAbsolutePath());
		System.out.println("file:" + dir.getAbsolutePath());
	}
	
	/**
	 * Test JUnit temp folder creation API. There was a problem with a particular junit version hence the test.
	 */
	@Test
	public void test_folder_creation() {
		File f = folder.newFolder("tempfolder");
		assertTrue(f.exists());
		assertTrue(f.isDirectory());
	}
}
