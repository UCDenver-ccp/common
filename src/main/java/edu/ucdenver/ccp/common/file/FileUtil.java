package edu.ucdenver.ccp.common.file;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

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
	 * This enum replaces the need for the boolean cleanDirectory parameter that
	 * has been used in the past
	 * 
	 * @author Colorado Computational Pharmacology, UC Denver;
	 *         ccpsupport@ucdenver.edu
	 * 
	 */
	public enum CleanDirectory {
		YES, NO
	}

	/**
	 * Private constructor; do not instantiate this utility class
	 */
	/* @formatter:off */
	private FileUtil() {/* do not instantiate */
	}
	/* @formatter:on */

	/**
	 * Returns a temporary directory based on the File.createTempFile() method.
	 * The directory returned will be unique as it will have a UUID appended to
	 * it.
	 * 
	 * @param directoryName
	 * @return
	 * @throws IOException
	 *             if an error occurs while creating the temporary directory
	 */
	public static File createTemporaryDirectory(String directoryName) throws IOException {
		File tempFile = File.createTempFile("tmp", "tmp");
		File directory = new File(tempFile.getParentFile(), directoryName + UUID.randomUUID().toString());
		FileUtil.mkdir(directory);
		tempFile.delete();
		return directory;
	}

	/**
	 * Simple utility method that checks whether the input directory exists (and
	 * is a directory). Returns an error message if the directory does not exist
	 * or is not a directory. Returns null if the directory exists as expected.
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
	 * Checks to see if the directory exists, and if it is truly a directory.
	 * Throws a FileNotFoundException if the input is not a true/existing
	 * directory.
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
	 * Simple utility method that checks whether the input file exists (and is a
	 * file). Returns an error message if the file does not exist or is not a
	 * file (i.e. it's a directory instead). Returns null if the directory
	 * exists as expected.
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
	 * Checks to see if the file exists, and if it is truly a file. Throws a
	 * FileNotFoundException if the input is not a true/existing file.
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
	 * Creates the specified directory. Throws an IllegalStateException if the
	 * directory cannot be created.
	 * 
	 * @param directory
	 * @throws IllegalStateException
	 */
	public static void mkdir(File directory) throws IllegalStateException {
		if (!directory.exists()) {
			boolean succeeded = directory.mkdirs();
			if (!succeeded) {
				throw new IllegalStateException(String.format("Error while creating directory: %s",
						directory.getAbsolutePath()));
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

			// delete contents
			File[] files = directory.listFiles();
			for (File f : files) {
				if (f.isDirectory()) {
					success = success && deleteDirectory(f);
				} else {
					success = success && f.delete();
				}
			}

			// delete directory itself
			success = success && directory.delete();
		}

		return success;
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
	 * Deletes a directory (including any contents) and recreates it so that it
	 * is empty
	 * 
	 * @param directory
	 * @return
	 */
	public static void cleanDirectory(File directory) throws IOException {
		deleteDirectory(directory);
		if (!directory.mkdirs())
			throw new IOException("Directory could not be re-created during clean: " + directory.getAbsolutePath());
	}

	/**
	 * Deletes the input file and replaces it with a new empty file of the same
	 * name
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
		copy(is, file, false);
	}

	public static void copy(InputStream is, File file, boolean append) throws IOException {
		BufferedOutputStream outStream = null;
		try {
			outStream = new BufferedOutputStream(new FileOutputStream(file, append));
			copy(is, outStream);
		} finally {
			IOUtils.closeQuietly(outStream);
		}
	}

	/**
	 * Copies a file to the specified output stream
	 * 
	 * @param file
	 * @param os
	 * @throws IOException
	 */
	public static void copy(File file, OutputStream os) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			copy(fis, os);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

	/**
	 * Copies the contents of one file to another
	 * 
	 * @param fromFile
	 * @param toFile
	 * @throws IOException
	 *             , The exception is always wrapped. Look at the embedded
	 *             exception to see what it was that caused it.
	 */
	public static void copy(File fromFile, File toFileOrDirectory) throws IOException {
		validateFile(fromFile);

		if (!toFileOrDirectory.canWrite()) {
			// throw ...
		}

		FileInputStream fis = null;
		FileOutputStream fos = null;
		File toFile = toFileOrDirectory;
		try {
			fis = new FileInputStream(fromFile);

			if (toFileOrDirectory.isDirectory()) {
				toFile = FileUtil.appendPathElementsToDirectory(toFileOrDirectory, fromFile.getName());
			}
			fos = new FileOutputStream(toFile);
			copy(fis, fos);
		} catch (IOException x) {
			throw new IOException(x);
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(fos);
		}
		validateFile(toFile);
	}

	/**
	 * Copies the contents of one directory to another. The target directory
	 * must not exist prior to this method being invoked.
	 * 
	 * @param fromDirectory
	 * @param toDirectory
	 * @throws IOException
	 */
	public static void copyDirectory(File fromDirectory, File toDirectory) throws IOException {
		if (toDirectory.exists())
			throw new IllegalArgumentException(
					"Target directory already exists. Please delete and re-try copy command.");
		FileUtil.mkdir(toDirectory);
		File[] directoryContent = fromDirectory.listFiles();
		for (File file : directoryContent) {
			if (file.isFile())
				copy(file, toDirectory);
			else if (file.isDirectory())
				copyDirectory(file, new File(toDirectory, file.getName()));
			else
				throw new IOException("Unknown file type for file: " + file.getAbsolutePath());
		}

	}

	/**
	 * Copies the contents of the specified file to a <code>String</code> using
	 * the specified character encoding
	 * 
	 * @param fromFile
	 * @param fromFileEncoding
	 * @return
	 * @throws IOException
	 */
	public static String copyToString(File fromFile, CharacterEncoding fromFileEncoding) throws IOException {
		InputStreamReader isr = null;
		try {
			validateFile(fromFile);
			isr = new InputStreamReader(new FileInputStream(fromFile), fromFileEncoding.getDecoder());
			return StreamUtil.toString(isr);
		} catch (ArrayIndexOutOfBoundsException x) {
			throw new RuntimeException("failed getting contents of file: " + fromFile.getAbsolutePath(), x);
		} finally {
			IOUtils.closeQuietly(isr);
		}
	}

	public static boolean fileContainsAstralCharacters(File fromFile, CharacterEncoding fromFileEncoding)
			throws IOException {
		return StringUtil.containsAstralChars(copyToString(fromFile, fromFileEncoding));
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
	 * Creates a file name filter that accepts files based on the fileSuffix
	 * input parameter
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
	 * http://www.java-tips.org/java-se-tips/java.io/reading
	 * -a-file-into-a-byte-array.html
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(File file) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] bytes = IOUtils.toByteArray(fis);
			return bytes;
		} finally {
			IOUtils.closeQuietly(fis);
		}

	}

	/**
	 * Returns a reference to a File that is specified by the input file name,
	 * and located in the input directory. The file is not created, only the
	 * reference.
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
	 * Returns an Iterator<File> over the files in the input directory. Only
	 * visible (i.e. not hidden) files and directories will be processed.
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
			throw new IOException(String.format("Input is not a valid file or directory: %s",
					fileOrDirectory.getAbsolutePath()));
	}

	/**
	 * Returns an Iterator<File> over the files in the input directory that
	 * returns only files listed in the files listed. Only visible (i.e. not
	 * hidden unix-style with leading periods) files and directories will be
	 * processed. The list of filenames is the file.getName() of the
	 * file...without any path. So the assumption is that the filenames are
	 * unique or equivalent when not considering the path.
	 * 
	 * @param fileOrDirectory
	 * @param recurse
	 * @param fileSuffixes
	 * @return
	 * @throws IOException
	 */
	public static Iterator<File> getFileIterator(File fileOrDirectory, boolean recurse, Collection<String> filenames,
			String... fileSuffixes) throws IOException {
		Set<String> filenameSet = new TreeSet<String>();
		filenameSet.addAll(filenames);
		if (FileUtil.isFileValid(fileOrDirectory) == null && filenameSet.contains(fileOrDirectory.getName())) {
			return createSingleFileIterator(fileOrDirectory, fileSuffixes);
		} else if (FileUtil.isDirectoryValid(fileOrDirectory) == null) {
			IOFileFilter visible = createFileFilter(removeLeadingPeriods(fileSuffixes));
			IOFileFilter inList = new FileListFilter(filenames);
			return FileUtils.iterateFiles(fileOrDirectory, FileFilterUtils.and(visible, inList),
					createDirectoryFilter(recurse));
		} else
			throw new IOException(String.format("Input is not a valid file or directory: %s",
					fileOrDirectory.getAbsolutePath()));
	}

	/**
	 * Returns an Iterator<File> over the files in the input directory. Only
	 * visible (i.e. not hidden) files and directories will be processed.
	 * 
	 * @param fileOrDirectory
	 * @param recurse
	 * @param fileSuffixes
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static Iterator<File> getSortedFileIterator(File fileOrDirectory, boolean recurse, String... fileSuffixes)
			throws IOException {
		if (FileUtil.isFileValid(fileOrDirectory) == null) {
			return createSingleFileIterator(fileOrDirectory, fileSuffixes);
		} else if (FileUtil.isDirectoryValid(fileOrDirectory) == null) {
			fileSuffixes = removeLeadingPeriods(fileSuffixes);
			Collection<File> c = FileUtils.listFiles(fileOrDirectory, createFileFilter(fileSuffixes),
					createDirectoryFilter(recurse));
			ArrayList<File> list = new ArrayList<File>(c);
			Collections.sort(list);
			return list.iterator();
		} else
			throw new IOException(String.format("Input is not a valid file or directory: %s",
					fileOrDirectory.getAbsolutePath()));
	}

	/**
	 * Returns a List<File> over the files in the input directory. Only visible
	 * (i.e. not hidden) files and directories will be processed. The list is
	 * sorted using the java.util.Collections.sort() method.
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
	 * Returns an IOFileFilter that accepts only file with the input suffixes.
	 * Files must also be visible.
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
	 * Returns an IOFileFilter set up to either accept directories (if recurse
	 * == true) or to only accept files (if recurse == false). Directories must
	 * be visible.
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
	 * leading periods need to be removed or else the FileUtils.iteratorFiles
	 * method does not work as expected
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
	 * Creates an iterator over a single File object. File must be visible and
	 * must match one of the input file suffixes if specified.
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
	 * Appends the specified file suffix to the end of the file name referenced
	 * by the input File object
	 * 
	 * @param file
	 *            the file to add the suffix to
	 * @param suffix
	 *            the suffix to add
	 * @return a reference to a file that is the input file appended with the
	 *         input suffix
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
			 * The 'line' variable is unnecessary here, however if
			 * (reader.readLine() == null) is used, a FindBugs warning is
			 * generated.
			 */
			@SuppressWarnings("unused")
			String line;
			while ((line = reader.readLine()) != null)
				lineCount++;
			return lineCount;
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

}
