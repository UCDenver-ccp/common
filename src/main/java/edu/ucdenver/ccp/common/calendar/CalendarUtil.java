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
