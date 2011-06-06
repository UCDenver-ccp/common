package edu.ucdenver.ccp.common.osgi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * Generic OSGI launcher that uses {@link ServiceLoader} to obtain an OSGI
 * {@link Framework} instance from any library found on the classpath.
 * 
 * @author Yuriy Malenkiy
 * 
 * @throws RuntimeException
 *             if errors occur while initializing {@link Framework} instance or
 *             interacting with it.
 */
public class OsgiLauncher {

	private static final Logger logger = Logger.getLogger(OsgiLauncher.class);
	private final Map<String, String> frameworkProperties;
	private Framework framework;

	/**
	 * Default constructor. 
	 * 
	 * @param frameworkProperties properties as documented by an OSGI framework.
	 */
	public OsgiLauncher(Map<String, String> frameworkProperties) {
		this.frameworkProperties = Collections.unmodifiableMap(frameworkProperties);
	}

	public void stopFramework() {
		try {
			getFramework().stop();
		} catch (BundleException e) {
			throw new RuntimeException(e);
		}
	}

	public void startFramework() {
		try {
			getFramework().start();
		} catch (BundleException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Invoke method 'main' on class in bundle.
	 * 
	 * @param symbolicBundleName bundle name
	 * @param mainClassName full class name (with package)
	 * @param args arguments to main
	 * 
	 * @see OsgiLauncher#invokeMain(String, Version, String, String[]) 
	 */
	public void invokeMain(String symbolicBundleName, String mainClassName, String[] args) {
		invokeMain(symbolicBundleName, null, mainClassName, args);
	}

	/**
	 * Invoke method 'main' on class in bundle of specified version
	 * 
	 * @param symbolicBundleName bundle name
	 * @param bundleVersion bundle version; if null, version isn't checked
	 * @param mainClassName full class name (with package)
	 * @param args arguments to main
	 */
	public void invokeMain(String symbolicBundleName, Version bundleVersion, String mainClassName, String[] args) {
		Framework f = getFramework();

		for (Bundle b : f.getBundleContext().getBundles()) {
			if (b.getSymbolicName().equals(symbolicBundleName) 
					&& (bundleVersion == null || b.getVersion().equals(bundleVersion)) ) {
				logger.debug(String.format("Launching %s in bundle symbolic name = %s ; header = %s", mainClassName,
						b.getSymbolicName(), b.getHeaders()));
				
				String errorMessage = String.format("Error occured while launching %s#main(String args[]) in bundle",
						mainClassName, symbolicBundleName);
				
				try {
					Class<?> klass = b.loadClass(mainClassName);
					Method main = klass.getMethod("main", String[].class);
					main.invoke(null, (Object) args);
				} catch (SecurityException e) {
					throw new org.mockftpserver.core.IllegalStateException(errorMessage, e);
				} catch (IllegalArgumentException e) {
					throw new org.mockftpserver.core.IllegalStateException(errorMessage, e);
				} catch (ClassNotFoundException e) {
					throw new org.mockftpserver.core.IllegalStateException(errorMessage, e);
				} catch (NoSuchMethodException e) {
					throw new org.mockftpserver.core.IllegalStateException(errorMessage, e);
				} catch (IllegalAccessException e) {
					throw new org.mockftpserver.core.IllegalStateException(errorMessage, e);
				} catch (InvocationTargetException e) {
					throw new org.mockftpserver.core.IllegalStateException(errorMessage, e);
				}
			}
		}
	}	
	
	/**
	 * Get {@link Framework} instance. This provides full access to framework functionality. 
	 * 
	 * @return framework
	 */
	public Framework getFramework() {
		if (framework == null) {
			ServiceLoader<FrameworkFactory> sl = ServiceLoader.load(FrameworkFactory.class);
			if (!sl.iterator().hasNext())
				throw new IllegalStateException("OSGI Framework couldn't be launched "
						+ "- no implementation was included on classpath");

			FrameworkFactory ff = sl.iterator().next();
			framework = ff.newFramework(frameworkProperties);

			try {
				framework.start();
			} catch (BundleException e) {
				throw new RuntimeException("Unable to start framework " + framework, e);
			}

			Runtime.getRuntime().addShutdownHook(new Thread("OSGI Framework shutdown hook") {
				@Override
				public void run() {
					try {
						framework.stop();
						framework.waitForStop(0);
						logger.info("Stopped OSGI framework.");
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			});

			logger.debug("Started framework : " + framework.getHeaders());
		}

		return framework;
	}

	/**
	 * Print installed bundle information to {@link PrintStream} 
	 * 
	 * @param ps output print stream
	 */
	public void listBundles(PrintStream ps) {
		ps.println("Framework bundles:");
		for (Bundle b : getFramework().getBundleContext().getBundles())
			ps.format("Bundle: symbolic name = %s, id = %s, state=%s headers = %s\n", b.getSymbolicName(), b.getBundleId(), b.getState(), b.getHeaders());
	}
	
	/**
	 * Install bundle from directory into framework.
	 * 
	 * @param srcBundleDirectory
	 * @throws IllegalArgumentException if {@code srcBundleDirectory} isn't a directory or doesn't exist
	 * @throws RuntimeException if errors occur while installing and/or starting bundles
	 */
	public void installBundles(String srcBundleDirectory)  {
		File bundleDir = new File(srcBundleDirectory);
		if (!bundleDir.exists() || !bundleDir.isDirectory())
			throw new IllegalArgumentException(String.format("Invalid bundle directory %s", srcBundleDirectory));
		
		for (File f : bundleDir.listFiles()) {
			try {
				getFramework().getBundleContext().installBundle(f.toURI().toURL().toExternalForm());
			} catch (BundleException e) {
				throw new RuntimeException(e);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		
		// start after all are installed
		for (Bundle b : getFramework().getBundleContext().getBundles()) { 
			try {
				b.start();
			} catch (BundleException e) {
				throw new RuntimeException(e);
			}
		}
	}	
	
	/**
	 * Main allowing specification of framework config properties file, symbolic
	 * bundle name and class with main(String[]) method. Rest of arguments are
	 * forwarded to that main method.
	 * 
	 * @param args
	 *            [0] - framework properties file ; [1] - symbolic bundle name;
	 *            [2] - Main class
	 *            
	 * @throws IllegalArgumentException
	 *             if there are less then 3 arguments
	 * @throws FileNotFoundException
	 *             if errors occur while reading framework config file
	 * @throws IOException
	 *             if errors occur while reading framework config file
	 */
	public static void main(String[] args) throws IllegalArgumentException, FileNotFoundException, IOException {
		BasicConfigurator.configure();
		if (args.length == 0)
			throw new IllegalArgumentException(
					"Arguments must at least include <path to framework config properties file> <bundle name> <class with main in named bundle>");

		File configFile = new File(args[0]);
		Properties config = new Properties();
		config.load(new FileReader(configFile));

		OsgiLauncher launcher = new OsgiLauncher((Map) config);
		
		if (args.length > 2) {
			String symbolicBundleName = args[1];
			String mainClassName = args[2];

			String[] mainArgs = Arrays.copyOfRange(args, 3, args.length);
			launcher.invokeMain(symbolicBundleName, mainClassName, mainArgs);
		}
	}
}