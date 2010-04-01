/*
 * Copyright (C) 2009 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
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
 * 
 */
package edu.ucdenver.ccp.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

import edu.ucdenver.ccp.util.string.StringUtil;

public class FileArchiveUtil {

	private static final String gz_suffix = ".gz";
	private static final String tgz_suffix = ".tgz";
	private static final String zip_suffix = ".zip";
	private static final String bzip_suffix = ".bz2";
	private static final String z_suffix = ".z";
	private static final String tar_suffix = ".tar";

	/**
	 * Returns an InputStream for the specified file. This method can handle both .gz and .zip
	 * files.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             - if a file using an unsupported compression format is detected.
	 */
	public static InputStream getInputStream(File file) throws FileNotFoundException, IOException,
			IllegalArgumentException {
		if (isGzipFile(file)) {
			return getGzipInputStream(file);
		} else if (isZipFile(file)) {
			return getZipInputStream(file);
		} else if (isUnsupportedZipFormatFile(file)) {
			throw new IllegalArgumentException(String.format(
					"Method for reading compressed format is not supported for file: %s", file.getAbsolutePath()));
		} else {
			return new FileInputStream(file);
		}
	}

	/**
	 * Returns a ZipInputStream for a .zip input file
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	private static ZipInputStream getZipInputStream(File file) throws FileNotFoundException {
		return new ZipInputStream(new BufferedInputStream(new CheckedInputStream(new FileInputStream(file),
				new Adler32())));
	}

	/**
	 * Returns a GZIPInputStream for a .gz file
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static InputStream getGzipInputStream(File file) throws IOException, FileNotFoundException {
		return new GZIPInputStream(new FileInputStream(file));
	}

	private static boolean isUnsupportedZipFormatFile(File file) {
		return isBZipFile(file) || isUnixCompressFile(file);
	}

	private static boolean hasCaseInsensitiveSuffix(File file, String fileSuffix) {
		return file.getName().endsWith(fileSuffix.toLowerCase()) || file.getName().endsWith(fileSuffix.toUpperCase());
	}

	private static boolean isGzipFile(File file) {
		return hasCaseInsensitiveSuffix(file, gz_suffix) || hasCaseInsensitiveSuffix(file, tgz_suffix);
	}

	private static boolean isZipFile(File file) {
		return hasCaseInsensitiveSuffix(file, zip_suffix);
	}

	private static boolean isBZipFile(File file) {
		return hasCaseInsensitiveSuffix(file, bzip_suffix);
	}

	private static boolean isUnixCompressFile(File file) {
		return hasCaseInsensitiveSuffix(file, z_suffix);
	}

	/**
	 * Untars a file into the specified output directory TODO: Use CheckSums?
	 * 
	 * @param tarFile
	 * @param outputDirectory
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static void untar(File tarFile, File outputDirectory) throws FileNotFoundException,
			IllegalArgumentException, IOException {
		FileUtil.validateDirectory(outputDirectory);
		TarInputStream tis = null;
		try {
			tis = new TarInputStream(getInputStream(tarFile));
			TarEntry tarEntry;
			while ((tarEntry = tis.getNextEntry()) != null) {
				copyTarEntryToFileSystem(tis, tarEntry, outputDirectory);
			}
		} finally {
			IOUtils.closeQuietly(tis);
		}
	}

	/**
	 * Copies the current TarEntry to the file system. If it is a directory, a directory is created,
	 * otherwise a file is created.
	 * 
	 * @param outputDirectory
	 * @param tis
	 * @param tarEntry
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void copyTarEntryToFileSystem(TarInputStream tis, TarEntry tarEntry, File outputDirectory)
			throws FileNotFoundException, IOException {
		File outputPath = new File(outputDirectory.getAbsolutePath() + File.separator + tarEntry.getName());
		if (tarEntry.isDirectory()) {
			FileUtil.mkdir(outputPath);
		} else {
			copyTarEntryToFile(tis, outputPath);
		}
	}

	/**
	 * Copies the current TarEntry to the specified output file
	 * 
	 * @param tis
	 * @param outputDirectory
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void copyTarEntryToFile(TarInputStream tis, File outputDirectory) throws FileNotFoundException,
			IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outputDirectory);
			tis.copyEntryContents(fos);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	public static void unzip(File zippedFile, File outputDirectory) throws IOException {
		InputStream is = null;
		try {
			if (isZipFile(zippedFile)) {
				is = getZipInputStream(zippedFile);
				unzip((ZipInputStream) is, outputDirectory);
			} else if (isGzipFile(zippedFile)) {
				is = getGzipInputStream(zippedFile);
				unzip((GZIPInputStream) is, getUnzippedFileName(zippedFile.getName()), outputDirectory);
			} else {
				throw new IllegalArgumentException(String.format("Unable to unzip file: %s", zippedFile
						.getAbsolutePath()));
			}
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * Takes as input a file name e.g. myFile.txt.gz and outputs myFile.txt. Handles both .gz and
	 * .tgz suffixes.
	 * 
	 * @param name
	 * @return
	 */
	private static String getUnzippedFileName(String filename) {
		if (filename.endsWith(gz_suffix.toLowerCase())) {
			return StringUtil.removeSuffix(filename, gz_suffix.toLowerCase());
		} else if (filename.endsWith(gz_suffix.toUpperCase())) {
			return StringUtil.removeSuffix(filename, gz_suffix.toUpperCase());
		} else if (filename.endsWith(tgz_suffix.toLowerCase())) {
			return StringUtil.replaceSuffix(filename, tgz_suffix.toLowerCase(), tar_suffix.toLowerCase());
		} else if (filename.endsWith(tgz_suffix.toUpperCase())) {
			return StringUtil.replaceSuffix(filename, tgz_suffix.toUpperCase(), tar_suffix.toUpperCase());
		} else {
			throw new IllegalArgumentException(String.format(
					"Only works for .gz and .tgz filenames. Input filename was: %s", filename));
		}
	}

	public static void unzip(GZIPInputStream gzipInputStream, String outputFileName, File outputDirectory)
			throws IOException {
		File outputFile = new File(outputDirectory.getAbsolutePath() + File.separator + outputFileName);
		FileUtil.copy(gzipInputStream, outputFile);
	}

	/**
	 * Unzips the input ZipInputStream to the specified directory
	 * 
	 * @param zis
	 * @param outputDirectory
	 * @throws IOException
	 */
	public static void unzip(ZipInputStream zis, File outputDirectory) throws IOException {
		ZipEntry zipEntry = null;
		while ((zipEntry = zis.getNextEntry()) != null) {
			copyZipEntryToFileSystem(zis, zipEntry, outputDirectory);
		}
	}

	/**
	 * Copies the contents of the current ZipEntry to the file system. If the ZipEntry is a
	 * directory, then a directory is created, otherwise a file is created.
	 * 
	 * @param zis
	 * @param zipEntry
	 * @param outputDirectory
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void copyZipEntryToFileSystem(ZipInputStream zis, ZipEntry zipEntry, File outputDirectory)
			throws FileNotFoundException, IOException {
		File outputPathFile = new File(outputDirectory.getAbsolutePath() + File.separator + zipEntry.getName());
		if (zipEntry.isDirectory()) {
			FileUtil.mkdir(outputPathFile);
		} else {
			copyZipEntryToFile(zis, zipEntry, outputPathFile);
		}
	}

	/**
	 * Creates a new file on the file system containing the contents of the current ZipEntry TODO:
	 * Use CheckSums?
	 * 
	 * @param zis
	 * @param zipEntry
	 * @param outputDirectory
	 * @throws IOException
	 */
	private static void copyZipEntryToFile(ZipInputStream zis, ZipEntry zipEntry, File outputDirectory)
			throws IOException {
		File unzippedFile = new File(outputDirectory.getAbsolutePath() + File.separator + zipEntry.getName());
		FileUtil.copy(zis, unzippedFile);
	}

}
