package edu.ucdenver.ccp.common.calendar;

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

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

import org.junit.Test;

import edu.ucdenver.ccp.common.reflection.PrivateAccessor;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

/**
 * This class contains tests for the CalendarUtil class
 * 
 * @author bill
 * 
 */
public class CalendarUtilTest extends DefaultTestCase {

	/**
	 * Tests the normal operation of the getDateStamp() method.
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	@Test
	public void testGetDateStamp() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, Calendar.JULY);
		c.set(Calendar.DAY_OF_MONTH, 16);
		c.set(Calendar.YEAR, 1980);

		String expectedDateStamp = "7/16/1980";
		assertEquals(String.format("Date stamp should match expected."), expectedDateStamp, callGetDateStamp(c, "/"));
	}

	/**
	 * Helper method for calling the private static getDateStamp() method in CalendarUtil
	 * 
	 * @param calendar
	 * @param delimiter
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private String callGetDateStamp(Calendar calendar, String delimiter) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return (String) PrivateAccessor.invokeStaticPrivateMethod(CalendarUtil.class, "getDateStamp", calendar,
				delimiter);
	}

}
