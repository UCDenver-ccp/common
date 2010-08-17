package edu.ucdenver.ccp.common.calendar;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

import org.junit.Test;

import edu.ucdenver.ccp.common.calendar.CalendarUtil;
import edu.ucdenver.ccp.common.reflection.PrivateAccessor;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class CalendarUtilTest extends DefaultTestCase {

	@Test
	public void test() throws Exception {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, Calendar.JULY);
		c.set(Calendar.DAY_OF_MONTH, 16);
		c.set(Calendar.YEAR, 1980);

		String expectedDateStamp = "7/16/1980";
		assertEquals(String.format("Date stamp should match expected."), expectedDateStamp, callGetDateStamp(c, "/"));
	}

	private String callGetDateStamp(Calendar calendar, String delimiter) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return (String) PrivateAccessor.invokeStaticPrivateMethod(CalendarUtil.class, "getDateStamp", calendar,
				delimiter);
	}

}
