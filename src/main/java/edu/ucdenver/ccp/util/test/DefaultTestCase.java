package edu.ucdenver.ccp.util.test;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * A super class for other test classes. It sets up a logger among other things.
 * 
 * @author Bill Baumgartner
 * 
 */
public class DefaultTestCase {

	static {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
}
