package edu.ucdenver.ccp.util.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public class FileUtil {

	private FileUtil() {
		// do not instantiate
	}

	/**
	 * Simple utility method that checks whether the input directory exists (and is a directory).
	 * Returns an error message if the directory does not exist or is not a directory. Returns null
	 * if the directory exists as expected.
	 * 
	 * @param directory
	 * @return
	 */
	public static String isDirectoryValid(File directory) {
		String errorMessage = null;
		if (!directory.exists()) {
			errorMessage = String.format("Directory does not exist: %s", directory.getAbsolutePath());
		} else if (!directory.isDirectory()) {
			errorMessage = String.format("Input directory is not a directory: %s", directory.getAbsolutePath());
		}
		return errorMessage;
	}

	/**
	 * Checks to see if the directory exists, and if it is truly a directory. Throws a
	 * FileNotFoundException if the input is not a true/existing directory.
	 * 
	 * @param directory
	 * @throws FileNotFoundException
	 */
	public static void validateDirectory(File directory) throws FileNotFoundException {
		String message = FileUtil.isDirectoryValid(directory);
		if (message != null) {
			throw new FileNotFoundException(message);
		}
	}

	/**
	 * Simple utility method that checks whether the input file exists (and is a file). Returns an
	 * error message if the file does not exist or is not a file (i.e. it's a directory instead).
	 * Returns null if the directory exists as expected.
	 * 
	 * @param directory
	 * @return
	 */
	public static String isFileValid(File file) {
		String errorMessage = null;
		if (!file.exists()) {
			errorMessage = String.format("File does not exist: %s", file.getAbsolutePath());
		} else if (!file.isFile()) {
			errorMessage = String.format("Input file is not a file: %s", file.getAbsolutePath());
		}
		return errorMessage;
	}

	/**
	 * Checks to see if the file exists, and if it is truly a file. Throws a FileNotFoundException
	 * if the input is not a true/existing file.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public static void validateFile(File file) throws FileNotFoundException {
		String message = FileUtil.isFileValid(file);
		if (message != null) {
			throw new FileNotFoundException(message);
		}
	}

	/**
	 * Creates the specified directory. Throws an IllegalStateException if the directory cannot be
	 * created.
	 * 
	 * @param directory
	 * @throws IllegalStateException
	 */
	public static void mkdir(File directory) throws IllegalStateException {
		boolean succeeded = directory.mkdirs();
		if (!succeeded) {
			throw new IllegalStateException(String.format("Error while creating directory: %s", directory
					.getAbsolutePath()));
		}
	}

	/**
	 * Recursively deletes the contents of a directory, and the directory itself
	 * 
	 * @param directory
	 * @return
	 */
	public static boolean deleteDirectory(File directory) {
		boolean success = true;
		if (directory.exists()) {
			File[] files = directory.listFiles();
			for (File f : files) {
				if (f.isDirectory()) {
					deleteDirectory(f);
				} else {
					success = success & f.delete();
				}
			}
		}
		return success & directory.delete();
	}

	/**
	 * Deletes files from a given directory, does not touch directories.
	 * 
	 * @param directory
	 * @return
	 */
	public static boolean deleteFilesFromDirectory(File directory) {
		if (directory.exists()) {
			for (File f : directory.listFiles()) {
				if (f.isFile()) {
					if (!f.delete()) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Deletes a directory (including any contents) and recreates it so that it is empty
	 * 
	 * @param directory
	 * @return
	 */
	public static boolean cleanDirectory(File directory) {
		deleteDirectory(directory);
		return directory.mkdirs();
	}

	/**
	 * Copies the contents of the InputStream to the specified File
	 * 
	 * @param is
	 * @param file
	 * @throws IOException
	 */
	public static void copy(InputStream is, File file) throws IOException {
		BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
		copy(is, outStream);
	}

	/**
	 * Copies a file to the specified output stream
	 * 
	 * @param file
	 * @param os
	 * @throws IOException
	 */
	public static void copy(File file, OutputStream os) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		copy(fis, os);
		fis.close();
	}
	
	/**
	 * Copies the contents of one file to another
	 * @param fromFile
	 * @param toFile
	 * @throws IOException
	 */
	public static void copy(File fromFile, File toFile) throws IOException {
		validateFile(fromFile);
		FileInputStream fis = new FileInputStream(fromFile);
		FileOutputStream fos = new FileOutputStream(toFile);
		copy(fis, fos);
		fis.close();
		fos.close();
		validateFile(toFile);
	}

	/**
	 * Copies the specified InputStream to the specified OutputStream
	 * 
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	public static void copy(InputStream is, OutputStream os) throws IOException {
		try {
			IOUtils.copyLarge(is, os);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}

	/**
	 * Creates a file name filter that accepts files based on the fileSuffix input parameter
	 * 
	 * @return
	 */
	public static FilenameFilter createFilenameSuffixFilter(final String fileSuffix) {
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(fileSuffix);
			}
		};
		return filter;
	}

	/**
	 * This method modified from:
	 * http://www.java-tips.org/java-se-tips/java.io/reading-a-file-into-a-byte-array.html
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		byte[] bytes = IOUtils.toByteArray(fis);
		fis.close();
		return bytes;
	}

	/**
	 * Copies a resource (file on the classpath) to the specified directory
	 * 
	 * @param resourceName
	 * @param directory
	 * @return
	 * @throws IOException
	 */
	public static void copyResourceToFile(Class<?> cls, String resourceName, File file) throws IOException {
		FileUtil.copy(cls.getResourceAsStream(resourceName), file);
	}

	/**
	 * Returns an InputStream for the specified resource (file on the classpath)
	 * 
	 * @param resourceName
	 * @return
	 */
	public static InputStream getResourceStream(Class<?> cls, String resourceName) {
		return cls.getResourceAsStream(resourceName);
	}

	/**
	 * Returns a reference to a File that is specified by the input file name, and 
	 * located in the input directory. The file is not created, only the reference.
	 * @param directory
	 * @param fileName
	 * @return
	 */
	public static File appendPathElementsToDirectory(File directory, String... pathElements) {
		String path = directory.getPath();
		for (String pathElement : pathElements) {
			path+= (File.separator + pathElement);
		}
		return new File(path);
	}
	
}
