package edu.ucdenver.ccp.common.io;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class ClassPathUtil_Test {
	
	static String OSGI_CLASSLOADER_PREFIX="org.osgi";
	
	@Test
	public  void testFile() throws Exception {

			if (!this.getClass().getClassLoader().getClass().getName().startsWith(OSGI_CLASSLOADER_PREFIX)) {
			//org.osgi.framework.BundleReference

			List<String> list = ClassPathUtil.listResourceDirectory(
				this.getClass(), "test_dir/test_file.txt");
			Assert.assertTrue(list.get(0).endsWith("test_dir/test_file.txt"));
			}
	}
	
	/**
	 * If you name a directory, you get a list of paths of its non-directory
	 * contents...recursive.
	 * @throws Exception
	 */
	@Test
	public void testDir() throws Exception {
		if (!this.getClass().getClassLoader().getClass().getName().startsWith(OSGI_CLASSLOADER_PREFIX)) {
		List<String> list = ClassPathUtil.listResourceDirectory(
				this.getClass(), "test_dir");
		String[] classes = { "test_dir/test_file.txt",
		"test_dir/other_test_file.txt" };
	
		for (String c : classes ) {
			Assert.assertTrue(list.contains(c));
		}
		}
	}
	
	/**
	 * If you name the jar, you get it as a file.
	 * @throws Exception
	 */
	@Test
	public void testJarAsFile() throws Exception {
		if (!this.getClass().getClassLoader().getClass().getName().startsWith(OSGI_CLASSLOADER_PREFIX)) {
		List<String> list = ClassPathUtil.listResourceDirectory(
				this.getClass(), "junit-4.8.2.jar");
		Assert.assertTrue(list.get(0).endsWith("junit-4.8.2.jar"));
		}
	}
	
	/**
	 * If you name a directory that happends to be in a jar,
	 * you get a list of its non-directory contents, recursively. YOu get
	 * a list of paths, where each path is within in ther jar.
	 * @throws Exception
	 */
	@Test
	public void testDirInJar() throws Exception {
		if (!this.getClass().getClassLoader().getClass().getName().startsWith(OSGI_CLASSLOADER_PREFIX)) {
		List<String> list = ClassPathUtil.listResourceDirectory(
				this.getClass(), "org/junit");
		
		String[] classes = { "org/junit/After.class",
			"org/junit/runners/model/TestClass.class" };
		
		for (String c : classes ) {
			Assert.assertTrue(list.contains(c));
		}
		}
	}
	
	/**
	 *  If you name a file that happens to be in a jar,
	 *  you get it's path within that jar.
	 */
	@Test
	public void testFileInJar() throws Exception {
		if (!this.getClass().getClassLoader().getClass().getName().startsWith(OSGI_CLASSLOADER_PREFIX)) {
	
		List<String> list = ClassPathUtil.listResourceDirectory(
				this.getClass(), "org/junit/runner/Runner.class");
		Assert.assertTrue(list.get(0).endsWith("Runner.class"));
		}
	}

}
