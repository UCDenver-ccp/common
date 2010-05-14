package edu.ucdenver.ccp.util.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import edu.ucdenver.ccp.util.file.FileUtil;

/**
 * A super class for other test classes. It sets up a logger among other things.
 * 
 * @author Bill Baumgartner
 * 
 */
public class DefaultTestCase {
	private static final Logger logger = Logger.getLogger(DefaultTestCase.class);

	static {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	/**
	 * Helper method for grabbing a file from the classpath and returning an InputStream
	 * 
	 * @param clazz
	 * @param resourceName
	 * @return
	 */
	protected InputStream getResourceFromClasspath(Class<?> clazz, String resourceName) {
		InputStream is = clazz.getResourceAsStream(resourceName);
		Assert.assertNotNull(is);
		return is;
	}

	/**
	 *Helper method for grabbing a file from the classpath and copying it to the temporary folder.
	 * This method can be used for testing applications that require File input, but are incapable
	 * of consuming InputStreams.
	 * 
	 * @param clazz
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	protected File copyClasspathResourceToTemporaryFile(Class<?> clazz, String resourceName) throws IOException {
		File tempFile = folder.newFile(resourceName);
		FileUtil.copy(getResourceFromClasspath(clazz, resourceName), tempFile);
		logger.debug(String.format("created file: %s", tempFile.getAbsolutePath()));
		return tempFile;
	}
}
