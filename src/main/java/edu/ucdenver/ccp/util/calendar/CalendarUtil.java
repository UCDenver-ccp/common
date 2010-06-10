package edu.ucdenver.ccp.util.calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarUtil {
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

	private static String getDateStamp(GregorianCalendar c, String delimiter) {
		return String.format("%s%s%s%s%s", c.get(Calendar.MONTH) + 1, delimiter, c.get(Calendar.DAY_OF_MONTH),
				delimiter, c.get(Calendar.YEAR));
	}

}
