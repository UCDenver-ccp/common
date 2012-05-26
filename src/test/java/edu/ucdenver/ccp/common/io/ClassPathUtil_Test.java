package edu.ucdenver.ccp.common.io;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ClassPathUtil_Test {

	static String OSGI_CLASSLOADER_PREFIX = "org.eclipse.osgi";
	static boolean testOSGI = false; // TODO make this work in OSGI

	// org.osgi didn't work
	Logger logger = Logger.getLogger(ClassPathUtil_Test.class);

	@Before
	public void before() {
		BasicConfigurator.configure();
	}

	@Test
	public void testGetResourceStreamFromClasspath() {
		/*
		 * test_dir is in src/test/resources/test_dir target/test-classes/test_dir ...and should be
		 * on the classpath.
		 */
		doCL(ClassLoader.getSystemClassLoader(), "sys");
		// doCL(ClassPathUtil_Test.class.getClassLoader(), "ShowClasspath");

		InputStream is = ClassPathUtil.getResourceStreamFromClasspath(this.getClass(), "/test_dir/test_file.txt");

		// some pain to get there:
		// "/src/test/resources/test_dir/test_file.txt");
		// "src/test/resources/test_dir/test_file.txt");
		// "/test_dir.test_file.txt");
		// "test_dir.test_file.txt");
		// "test_dir/test_file.txt");
		Assert.assertNotNull(is);
	}

	@Test
	public void testFile() throws Exception {
		logger.error(this.getClass().getClassLoader().getClass().getName());
		// in IDE test: sun.misc.Launcher$AppClassLoader
		// in Hudson OSGI test: org.eclipse.osgi.internal.baseadaptor.DefaultClassLoader

		if (!this.getClass().getClassLoader().getClass().getName().startsWith(OSGI_CLASSLOADER_PREFIX) || testOSGI) {
			// org.osgi.framework.BundleReference

			List<String> list = ClassPathUtil.listResourceDirectory(this.getClass(), "test_dir/test_file.txt");
			Assert.assertTrue(list.size() > 0);
			Assert.assertTrue(list.get(0).endsWith("test_dir/test_file.txt"));
		}

	}

	/**
	 * If you name a directory, you get a list of paths of its non-directory contents...recursive.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDir() throws Exception {
		if (!this.getClass().getClassLoader().getClass().getName().startsWith(OSGI_CLASSLOADER_PREFIX) || testOSGI) {
			List<String> list = ClassPathUtil.listResourceDirectory(this.getClass(), "test_dir");
			String[] classes = { "test_dir/test_file.txt", "test_dir/other_test_file.txt" };

			for (String c : classes) {
				Assert.assertTrue(list.contains(c));
			}
		}
	}

	/**
	 * If you name the jar, you get it as a file.
	 * 
	 * @throws Exception
	 */
	@Ignore("Test stopped passing when MockFtpServer-2.1.jar file was deleted from src/main/resources")
	@Test
	public void testJarAsFile() throws Exception {
		if (!this.getClass().getClassLoader().getClass().getName().startsWith(OSGI_CLASSLOADER_PREFIX) || testOSGI) {
			List<String> list = ClassPathUtil.listResourceDirectory(this.getClass(), "MockFtpServer-2.1.jar");
			assertTrue("returned list must have an entry", list.size() > 0);
			Assert.assertTrue(String.format("Ending not as expected. Expected %s at end, but observed: %s",
					"MockFtpServer-2.1.jar", list.get(0)), list.get(0).endsWith("MockFtpServer-2.1.jar"));
		}
	}

	/**
	 * If you name a directory that happends to be in a jar, you get a list of its non-directory
	 * contents, recursively. YOu get a list of paths, where each path is within in ther jar.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDirInJar() throws Exception {
		if (!this.getClass().getClassLoader().getClass().getName().startsWith(OSGI_CLASSLOADER_PREFIX) || testOSGI) {
			List<String> list = ClassPathUtil.listResourceDirectory(this.getClass(), "org/junit");

			String[] classes = { "org/junit/After.class", "org/junit/runners/model/TestClass.class" };

			System.out.println(this.getClass().getClassLoader().getClass().getName());

			for (String c : classes) {
				Assert.assertTrue(list.contains(c));
			}
		}
	}

	/**
	 * If you name a file that happens to be in a jar, you get it's path within that jar.
	 */
	@Test
	public void testFileInJar() throws Exception {
		if (!this.getClass().getClassLoader().getClass().getName().startsWith(OSGI_CLASSLOADER_PREFIX) || testOSGI) {

			List<String> list = ClassPathUtil.listResourceDirectory(this.getClass(), "org/junit/runner/Runner.class");
			Assert.assertTrue(list.get(0).endsWith("Runner.class"));
		}
	}

	public static void doCL(ClassLoader classLoader, String name) {
		// Get the URLs
		URL[] urls = ((URLClassLoader) classLoader).getURLs();

		System.out.println("Showing classpath..." + name);
		for (int i = 0; i < urls.length; i++) {
			System.out.println(urls[i].getFile());
		}
		System.out.println("...that's all folks.");

		System.out.println("CWD:" + System.getProperty("user.dir"));

	}

}
