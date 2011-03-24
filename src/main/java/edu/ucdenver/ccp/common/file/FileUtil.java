/* Copyright (C) 2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP common library.
 * The CCP common library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package edu.ucdenver.ccp.common.file;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.io.StreamUtil;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.common.string.StringUtil;

/**
 * Utility class for working with files and directories
 * 
 * @author Center for Computational Pharmacology; ccp-support@ucdenver.edu
 * 
 */
public class FileUtil {

	/**
	 * Private constructor; do not instantiate this utility class
	 */
	/* @formatter:off */
	private FileUtil() {/* do not instantiate */
	}

	/* @formatter:on */

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
		if (!directory.exists()) {
			boolean succeeded = directory.mkdirs();
			if (!succeeded) {
				throw new IllegalStateException(String.format("Error while creating directory: %s", directory
						.getAbsolutePath()));
			}
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
					success = success && f.delete();
				}
			}
		}
		return success && directory.delete();
	}

	/**
	 * Deletes the specified file
	 * 
	 * @param file
	 * @throws RuntimeException
	 *             if the file.delete() operation fails
	 */
	public static void deleteFile(File file) {
		if (file.exists())
			if (!file.delete())
				throw new RuntimeException(String.format("Error while deleting file: %s", file.getAbsolutePath()));
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
	 * Deletes the input file and replaces it with a new empty file of the same name
	 * 
	 * @param file
	 *            the file to clean
	 * @throws IOException
	 *             if an error occurs during the cleaning process
	 */
	public static void cleanFile(File file) throws IOException {
		deleteFile(file);
		if (!file.createNewFile())
			throw new IOException("File could not be re-created during clean: " + file.getAbsolutePath());
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
	 * 
	 * @param fromFile
	 * @param toFile
	 * @throws IOException
	 */
	public static void copy(File fromFile, File toFileOrDirectory) throws IOException {
		validateFile(fromFile);
		FileInputStream fis = new FileInputStream(fromFile);
		File toFile = toFileOrDirectory;
		if (toFileOrDirectory.isDirectory()) {
			toFile = FileUtil.appendPathElementsToDirectory(toFileOrDirectory, fromFile.getName());
		}
		FileOutputStream fos = new FileOutputStream(toFile);
		copy(fis, fos);
		fis.close();
		fos.close();
		validateFile(toFile);
	}

	/**
	 * Copies the contents of the specified file to a <code>String</code> using the specified
	 * character encoding
	 * 
	 * @param fromFile
	 * @param fromFileEncoding
	 * @return
	 * @throws IOException
	 */
	public static String copyToString(File fromFile, CharacterEncoding fromFileEncoding) throws IOException {
		validateFile(fromFile);
		return StreamUtil.toString(new InputStreamReader(new FileInputStream(fromFile), fromFileEncoding.getDecoder()));
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
	public static FilenameFilter createFilenameSuffixFilter(final String... fileSuffixes) {
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(@SuppressWarnings("unused") File dir, String name) {
				for (String fileSuffix : fileSuffixes) {
					if (name.endsWith(fileSuffix)) {
						return true;
					}
				}
				return false;
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
	 * Returns a reference to a File that is specified by the input file name, and located in the
	 * input directory. The file is not created, only the reference.
	 * 
	 * @param directory
	 * @param fileName
	 * @return
	 */
	public static File appendPathElementsToDirectory(File directory, String... pathElements) {
		StringBuffer sb = new StringBuffer(directory.getPath());
		for (String pathElement : pathElements) {
			sb.append(File.separator + pathElement);
		}
		return new File(sb.toString());
	}

	/**
	 * Returns an Iterator<File> over the files in the input directory. Only visible (i.e. not
	 * hidden) files and directories will be processed.
	 * 
	 * @param fileOrDirectory
	 * @param recurse
	 * @param fileSuffixes
	 * @return
	 * @throws IOException
	 */
	public static Iterator<File> getFileIterator(File fileOrDirectory, boolean recurse, String... fileSuffixes)
			throws IOException {
		if (FileUtil.isFileValid(fileOrDirectory) == null) {
			return createSingleFileIterator(fileOrDirectory, fileSuffixes);
		} else if (FileUtil.isDirectoryValid(fileOrDirectory) == null) {
			return FileUtils.iterateFiles(fileOrDirectory, createFileFilter(removeLeadingPeriods(fileSuffixes)),
					createDirectoryFilter(recurse));
		} else
			throw new IOException(String.format("Input is not a valid file or directory: %s", fileOrDirectory
					.getAbsolutePath()));
	}

	/**
	 * Returns a List<File> over the files in the input directory. Only visible (i.e. not hidden)
	 * files and directories will be processed. The list is sorted using the
	 * java.util.Collections.sort() method.
	 * 
	 * @param fileOrDirectory
	 * @param recurse
	 * @param fileSuffixes
	 * @return
	 * @throws IOException
	 */
	public static List<File> getFileListing(File fileOrDirectory, boolean recurse, String... fileSuffixes)
			throws IOException {
		List<File> list = CollectionsUtil.createList(getFileIterator(fileOrDirectory, recurse, fileSuffixes));
		Collections.sort(list);
		return list;
	}

	/**
	 * Returns an IOFileFilter that accepts only file with the input suffixes. Files must also be
	 * visible.
	 * 
	 * @param suffixes
	 * @return
	 */
	private static IOFileFilter createFileFilter(String... suffixes) {
		IOFileFilter fileFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), createVisibleFileFilter());
		if (suffixes != null && suffixes.length > 0) {
			IOFileFilter suffixFilter = FileFilterUtils.suffixFileFilter(suffixes[0]);
			for (int i = 1; i < suffixes.length; i++) {
				suffixFilter = FileFilterUtils.or(suffixFilter, FileFilterUtils.suffixFileFilter(suffixes[i]));
			}
			fileFilter = FileFilterUtils.and(fileFilter, suffixFilter);
		}
		return fileFilter;
	}

	/**
	 * Creates a file filter that ignores hidden files
	 * 
	 * @return
	 */
	private static IOFileFilter createVisibleFileFilter() {
		return FileFilterUtils.notFileFilter(FileFilterUtils.prefixFileFilter("."));
	}

	/**
	 * Returns an IOFileFilter set up to either accept directories (if recurse == true) or to only
	 * accept files (if recurse == false). Directories must be visible.
	 * 
	 * @param recurse
	 * @return
	 */
	private static IOFileFilter createDirectoryFilter(boolean recurse) {
		if (recurse)
			return FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), createVisibleFileFilter());

		return FileFilterUtils.and(FileFilterUtils.fileFileFilter(), createVisibleFileFilter());
	}

	/**
	 * Returns a FileFilter that accepts directories only
	 * 
	 * @return
	 */
	public static FileFilter DIRECTORY_FILTER() {
		return new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
	}

	/**
	 * Returns the set of directories in the specified directory
	 * 
	 * @param directory
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Set<File> getDirectories(File directory) throws FileNotFoundException {
		validateDirectory(directory);
		File[] directories = directory.listFiles(DIRECTORY_FILTER());
		return new HashSet<File>(Arrays.asList(directories));

	}

	/**
	 * Returns a set containing the directory names in the input directory
	 * 
	 * @param directory
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Set<String> getDirectoryNames(File directory) throws FileNotFoundException {
		Set<File> directories = getDirectories(directory);
		Set<String> directoryNames = new HashSet<String>();
		for (File dir : directories)
			directoryNames.add(dir.getName());
		return directoryNames;
	}

	/**
	 * 
	 * leading periods need to be removed or else the FileUtils.iteratorFiles method does not work
	 * as expected
	 * 
	 * @param fileSuffixes
	 * @return
	 */
	private static String[] removeLeadingPeriods(String[] fileSuffixes) {
		if (fileSuffixes != null) {
			for (int i = 0; i < fileSuffixes.length; i++) {
				if (fileSuffixes[i].startsWith(".")) {
					fileSuffixes[i] = fileSuffixes[i].substring(1);
				}
			}
		}
		return fileSuffixes;
	}

	/**
	 * Creates an iterator over a single File object. File must be visible and must match one of the
	 * input file suffixes if specified.
	 * 
	 * @param file
	 * @param fileSuffixes
	 * @return
	 */
	private static Iterator<File> createSingleFileIterator(File file, String... fileSuffixes) {
		File inputFile = file;
		if (file.getName().startsWith("."))
			inputFile = null;

		if (inputFile != null && fileSuffixes != null)
			if (!FileUtil.createFilenameSuffixFilter(fileSuffixes).accept(inputFile.getParentFile(),
					inputFile.getName()))
				inputFile = null;

		final File singleFile = inputFile;

		return new Iterator<File>() {
			private File nextFile = singleFile;

			@Override
			public boolean hasNext() {
				return nextFile != null;
			}

			@Override
			public File next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				File fileToReturn = nextFile;
				nextFile = null;
				return fileToReturn;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("The remove() method is not supported for this iterator.");
			}
		};
	}

	/**
	 * Returns an Iterator<File> over the files in the input directory
	 * 
	 * @param fileOrDirectory
	 * @param recurse
	 * @return
	 * @throws IOException
	 */
	public static Iterator<File> getFileIterator(File fileOrDirectory, boolean recurse) throws IOException {
		return getFileIterator(fileOrDirectory, recurse, (String[]) null);
	}

	/**
	 * Returns a File object with a relative path to the input directory
	 * 
	 * @param file
	 * @param directory
	 * @return
	 */
	public static File getFileRelativeToDirectory(File file, File directory) {
		String directoryStr = directory.getAbsolutePath();
		String fileStr = file.getAbsolutePath();
		if (!fileStr.startsWith(directoryStr))
			throw new IllegalArgumentException(String.format(
					"Cannot determine relative file. Input file is not inside input directory. File=%s Directory=%s",
					fileStr, directoryStr));
		String relativeFileStr = fileStr.substring(directoryStr.length());
		return new File(relativeFileStr);
	}

	/**
	 * @param file
	 * @return the terminal file suffix for the specified file
	 */
	public static String getFileSuffix(File file) {
		String fileName = file.getName();
		return getFileSuffix(fileName);
	}

	/**
	 * Returns the terminal file suffix for the input file name
	 * 
	 * @param fileName
	 *            the name of the file to process
	 * @return the last file suffix that is part of the input file name
	 */
	private static String getFileSuffix(String fileName) {
		int lastIndexOfPeriod = fileName.lastIndexOf(".");
		if (lastIndexOfPeriod == -1)
			return "";
		return fileName.substring(lastIndexOfPeriod);
	}

	/**
	 * Returns a reference to the input file with all file suffixes removed
	 * 
	 * @param file
	 *            the file to process
	 * @return a reference to the input file with all file suffixes removed
	 */
	public static File removeFileSuffixes(File file) {
		File suffixlessFile = new File(file.getAbsolutePath());
		String fileSuffix = null;
		while (!(fileSuffix = getFileSuffix(suffixlessFile)).isEmpty())
			suffixlessFile = new File(StringUtil.removeSuffix(suffixlessFile.getAbsolutePath(), fileSuffix));
		return suffixlessFile;
	}

	/**
	 * Appends the specified file suffix to the end of the file name referenced by the input File
	 * object
	 * 
	 * @param file
	 *            the file to add the suffix to
	 * @param suffix
	 *            the suffix to add
	 * @return a reference to a file that is the input file appended with the input suffix
	 */
	public static File appendFileSuffix(File file, String suffix) {
		File path = file.getParentFile();
		String fileName = file.getName();
		if (suffix.startsWith(StringConstants.PERIOD))
			suffix = suffix.substring(1);
		return new File(path, String.format("%s.%s", fileName, suffix));
	}

	/**
	 * Returns the number of lines in the input file
	 * 
	 * @param file
	 *            the number of lines in this file will be returned
	 * @param encoding
	 *            the character encoding used in the file
	 * @return the number of lines in the input file
	 * @throws IOException
	 */
	public static long getLineCount(File file, CharacterEncoding encoding) throws IOException {
		long lineCount = 0;
		BufferedReader reader = null;
		try {
			reader = FileReaderUtil.initBufferedReader(file, encoding);
			/*
			 * The 'line' variable is unnecessary here, however if (reader.readLine() == null) is
			 * used, a FindBugs warning is generated.
			 */
			@SuppressWarnings("unused")
			String line;
			while ((line = reader.readLine()) != null)
				lineCount++;
			return lineCount;
		} finally {
			if (reader != null)
				reader.close();
		}
	}

}
