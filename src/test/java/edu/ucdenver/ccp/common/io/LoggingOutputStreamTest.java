package edu.ucdenver.ccp.common.io;

import java.io.PrintStream;

import junit.framework.Assert;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

public class LoggingOutputStreamTest {
	
	public static void main(String args[])
	{
		BasicConfigurator.configure();
		System.out.println("you should see this 1");
		
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;
        System.setOut(new PrintStream(new LoggingOutputStream(Category.getRoot(), Priority.WARN),true));
        System.setErr(new PrintStream(new LoggingOutputStream(Category.getRoot(), Priority.ERROR),true));
		System.out.println("you should NOT see this 2");
		System.err.println("You should not see this either 2.5");
		
	    System.setOut(oldOut);
	    System.setErr(oldErr);
		System.out.println("you should see this 3");
	}
	
	@Test
	public void masqueradeAsRealJUnitTest() {
		Assert.assertTrue(true);
	}

}
