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
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;

public class FileUtil {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FileUtil.class);

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

	public static String copyToString(File fromFile, CharacterEncoding encoding) throws IOException {
		validateFile(fromFile);
		FileInputStream fis = new FileInputStream(fromFile);
		StringWriter sw = new StringWriter();
		IOUtils.copy(fis, sw, encoding.getCharacterSetName());
		fis.close();
		sw.close();
		return sw.toString();
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
			public boolean accept(File dir, String name) {
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
		String path = directory.getPath();
		for (String pathElement : pathElements) {
			path += (File.separator + pathElement);
		}
		return new File(path);
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
	@SuppressWarnings("unchecked")
	public static Iterator<File> getFileIterator(File fileOrDirectory, boolean recurse, String... fileSuffixes)
			throws IOException {
		if (FileUtil.isFileValid(fileOrDirectory) == null) {
			return createSingleFileIterator(fileOrDirectory, fileSuffixes);
		} else if (FileUtil.isDirectoryValid(fileOrDirectory) == null) {
			fileSuffixes = removeLeadingPeriods(fileSuffixes);
			return FileUtils.iterateFiles(fileOrDirectory, createFileFilter(fileSuffixes),
					createDirectoryFilter(recurse));
		} else
			throw new IOException(String.format("Input is not a valid file or directory: %s", fileOrDirectory
					.getAbsolutePath()));
	}

	/**
	 * Returns an IOFileFilter that accepts only file with the input suffixes. Files must also be
	 * visible.
	 * 
	 * @param suffixes
	 * @return
	 */
	private static IOFileFilter createFileFilter(String... suffixes) {
		IOFileFilter fileFilter = FileFilterUtils.andFileFilter(FileFilterUtils.fileFileFilter(),
				createVisibleFileFilter());
		if (suffixes != null && suffixes.length > 0) {
			IOFileFilter suffixFilter = FileFilterUtils.suffixFileFilter(suffixes[0]);
			for (int i = 1; i < suffixes.length; i++) {
				suffixFilter = FileFilterUtils
						.orFileFilter(suffixFilter, FileFilterUtils.suffixFileFilter(suffixes[i]));
			}
			fileFilter = FileFilterUtils.andFileFilter(fileFilter, suffixFilter);
		}
		return fileFilter;
	}

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
			return FileFilterUtils.andFileFilter(FileFilterUtils.directoryFileFilter(), createVisibleFileFilter());
		else
			return FileFilterUtils.andFileFilter(FileFilterUtils.fileFileFilter(), createVisibleFileFilter());
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
	 * Returns a set containing the directory names in the input directory
	 * 
	 * @param directory
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Set<String> getDirectoryListing(File directory) throws FileNotFoundException {
		validateDirectory(directory);
		Set<String> directoryNames = new HashSet<String>();
		File[] directories = directory.listFiles(DIRECTORY_FILTER());
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
		if (file.getName().startsWith("."))
			file = null;

		if (file != null && fileSuffixes != null)
			if (!FileUtil.createFilenameSuffixFilter(fileSuffixes).accept(file.getParentFile(), file.getName()))
				file = null;

		final File singleFile = file;

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

}
