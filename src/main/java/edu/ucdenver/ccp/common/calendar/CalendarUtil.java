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

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import edu.ucdenver.ccp.common.string.StringConstants;

/**
 * Utility class for handling Calendar operations
 * 
 * @author bill
 * 
 */
public class CalendarUtil {

	/**
	 * Protected constructor allows for subclasses, but prevents general instantiation of this
	 * utility class.
	 */
	protected CalendarUtil() {
		throw new UnsupportedOperationException(); /* prevents calls from subclass */
	}

	/**
	 * Returns a String containing the month day and year with user-specified delimiter
	 * 
	 * @param delimiter
	 * @return
	 */
	public static String getDateStamp(String delimiter) {
		GregorianCalendar c = new GregorianCalendar();
		return getDateStamp(c, delimiter);
	}

	public static String getTimeStamp() {
		GregorianCalendar c = new GregorianCalendar();
		return getTimeStamp(c) + StringConstants.SPACE + getDateStamp(c, "/");
	}

	private static String getTimeStamp(GregorianCalendar c) {
		return String.format("%s:%s", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
	}

	/**
	 * Returns a String containing the month day and year with user-specified delimiter
	 * 
	 * @param c
	 *            {@link Calendar} used to obtain date
	 * @param delimiter
	 * @return a {@link String} containing the month day and year with user-specified delimiter
	 */
	private static String getDateStamp(GregorianCalendar c, String delimiter) {
		return String.format("%s%s%s%s%s", c.get(Calendar.MONTH) + 1, delimiter, c.get(Calendar.DAY_OF_MONTH),
				delimiter, c.get(Calendar.YEAR));
	}

	/**
	 * Sun Oct 30 16:07:39 MDT 2011
	 * 
	 * @param timeStamp
	 * @return
	 */
	public static Calendar loadTimeStamp(String timeStamp) {
		String[] toks = timeStamp.split("[\\s:]");

		Map<String, Integer> monthToNumMap = new HashMap<String, Integer>();
		monthToNumMap.put("sep", 8);
		monthToNumMap.put("oct", 9);
		monthToNumMap.put("nov", 10);
		monthToNumMap.put("dec", 11);
		monthToNumMap.put("jan", 0);
		monthToNumMap.put("feb", 1);
		monthToNumMap.put("mar", 2);
		monthToNumMap.put("apr", 3);
		monthToNumMap.put("may", 4);
		monthToNumMap.put("jun", 5);
		monthToNumMap.put("jul", 6);
		monthToNumMap.put("aug", 7);

		String MON = toks[1].toLowerCase();
		Integer DD = Integer.parseInt(toks[2]);
		Integer HH = Integer.parseInt(toks[3]);
		Integer MM = Integer.parseInt(toks[4]);
		Integer SS = Integer.parseInt(toks[5]);
		String timeZone = toks[6];
		Integer YYYY = Integer.parseInt(toks[7]);

		return new GregorianCalendar(YYYY, monthToNumMap.get(MON), DD, HH, MM, SS);

	}

}
