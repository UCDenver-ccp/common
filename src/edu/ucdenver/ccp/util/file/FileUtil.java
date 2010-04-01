package edu.ucdenver.ccp.util.file;

import java.io.File;
import java.io.FilenameFilter;

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
