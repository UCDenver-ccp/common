/* Copyright (C) 2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP common library.
 * The CCP common library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package edu.ucdenver.ccp.common.calendar;

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
