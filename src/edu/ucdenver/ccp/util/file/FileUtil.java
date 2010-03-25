package edu.ucdenver.ccp.util.file;

import java.io.File;

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
	
}
