package edu.ucdenver.ccp.util.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

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
		BufferedOutputStream outStream = null;
		try {
			outStream = new BufferedOutputStream(new FileOutputStream(file));
			IOUtils.copyLarge(is, outStream);
		} finally {
			IOUtils.closeQuietly(outStream);
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

}
