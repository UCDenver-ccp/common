/**
 * 
 */
package edu.ucdenver.ccp.common.reflection;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class PrivateAccessorTest {

	private static final String SAMPLE_CONSTANT = "sample constant";

	@Test
	public void testGetPrivateStaticFieldValue() throws IllegalArgumentException, IllegalAccessException {
		assertEquals(SAMPLE_CONSTANT, PrivateAccessor.getPrivateStaticFieldValue(SampleClass.class, "CONSTANT"));
	}

	private static class SampleClass {
		private static final String CONSTANT = SAMPLE_CONSTANT;
	}

}
