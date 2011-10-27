/* Copyright (C) 2009, 2010 Center for Computational Pharmacology, University of Colorado School of Medicine
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
package edu.ucdenver.ccp.common.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.tar.TarOutputStream;

import edu.ucdenver.ccp.common.string.StringUtil;

/**
 * Utility class for handling archive files of various types
 * 
 * @author bill
 * 
 */
/**
 * @author bill
 * 
 */
public class FileArchiveUtil {

	/**
	 * Suffix signifying a gzipped file
	 */
	private static final String gz_suffix = ".gz";
	/**
	 * Suffix signifying a gzipped tarball file
	 */
	private static final String tgz_suffix = ".tgz";
	/**
	 * suffix signifying a zip file
	 */
	private static final String zip_suffix = ".zip";
	/**
	 * suffix signifying a bzipped file
	 */
	private static final String bzip_suffix = ".bz2";
	/**
	 * suffix signifying a .Z file
	 */
	private static final String z_suffix = ".Z";
	/**
	 * suffix signifying a tarball file
	 */
	private static final String tar_suffix = ".tar";

	/**
	 * Returns an InputStream for the specified file. This method can handle both .gz and .zip
	 * files.
	 * 
	 * TODO: investigate apache commons-compress.. see if it can replace some/all of the code in
	 * this class (http://commons.apache.org/compress/)
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
		} else if (isUnixCompressFile(file)) {
			return getUncompressInputStream(file);
		} else if (isUnsupportedZipFormatFile(file)) {
			throw new IllegalArgumentException(String.format(
					"Method for reading compressed format is not supported for file: %s", file.getAbsolutePath()));
		} else {
			return new FileInputStream(file);
		}
	}
	
	
	/**
	 * GZIPs the input file and places the output file as specified by the zippedFile parameter
	 * 
	 * @param inputFile
	 * @param zippedFile
	 * @throws IOException
	 */
	public static void gzipFile(File inputFile, File zippedFile) throws IOException {
		GZIPOutputStream gzipStream = new GZIPOutputStream(new FileOutputStream(zippedFile));
		FileUtil.copy(inputFile, gzipStream);
	}

	
//	public static File gzipFile(File file, File outputDirectory) throws IOException {
//		File gzFile = new File(outputDirectory, file.getName() + ".gz");
//		FileUtil.copy(file,new GZIPOutputStream(new FileOutputStream(gzFile)));
//		return gzFile;
//	}

	/**
	 * Returns an UncompressInputStream for the given .Z file
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static UncompressInputStream getUncompressInputStream(File file) throws IOException {
		return new UncompressInputStream(new BufferedInputStream(new FileInputStream(file)));
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

	/**
	 * Returns false if the input file is a zip file that is not handled by this class. TODO: Check
	 * to see if UnixCompress should still be an unsupported zip file format
	 * 
	 * @param file
	 * @return
	 */
	private static boolean isUnsupportedZipFormatFile(File file) {
		return isBZipFile(file) || isUnixCompressFile(file);
	}

	/**
	 * Matches the file suffix to the input file in a case-insensitive manner
	 * 
	 * @param file
	 * @param fileSuffix
	 * @return true if the input file has the specified file suffix regardless of upper/lower case
	 */
	private static boolean hasCaseInsensitiveSuffix(File file, String fileSuffix) {
		return file.getName().endsWith(fileSuffix.toLowerCase()) || file.getName().endsWith(fileSuffix.toUpperCase());
	}

	/**
	 * Returns true if the input file is a gzip file. Determination is made by examining the file
	 * suffix.
	 * 
	 * @param file
	 * @return true if the file is a gzip file, false otherwise
	 */
	private static boolean isGzipFile(File file) {
		return hasCaseInsensitiveSuffix(file, gz_suffix) || hasCaseInsensitiveSuffix(file, tgz_suffix);
	}

	/**
	 * Returns true if the input file is a zip file. Determination is made by examining the file
	 * suffix.
	 * 
	 * @param file
	 * @return true if the file is a zip file, false otherwise
	 */
	private static boolean isZipFile(File file) {
		return hasCaseInsensitiveSuffix(file, zip_suffix);
	}

	/**
	 * Returns true if the input file is a bzip file. Determination is made by examining the file
	 * suffix.
	 * 
	 * @param file
	 * @return true if the file is a bzip file, false otherwise
	 */
	private static boolean isBZipFile(File file) {
		return hasCaseInsensitiveSuffix(file, bzip_suffix);
	}

	/**
	 * Returns true if the input file is a .Z file. Determination is made by examining the file
	 * suffix.
	 * 
	 * @param file
	 * @return true if the file is a .Z file, false otherwise
	 */
	private static boolean isUnixCompressFile(File file) {
		return hasCaseInsensitiveSuffix(file, z_suffix);
	}

	/**
	 * Returns true if the input file is a zipped file. Determination is made by examining the file
	 * suffix.
	 * 
	 * @param file
	 * @return true if the file is a zipped file, false otherwise
	 */
	public static boolean isZippedFile(File file) {
		return isGzipFile(file) || isZipFile(file) || isBZipFile(file) || isUnixCompressFile(file);
	}

	/**
	 * Returns true if the input file is a tar file. Determination is made by examining the file
	 * suffix.
	 * 
	 * @param file
	 * @return true if the file is a tar file, false otherwise
	 */
	private static boolean isTarFile(File file) {
		return hasCaseInsensitiveSuffix(file, tar_suffix) || hasCaseInsensitiveSuffix(file, tgz_suffix);
	}

	/**
	 * Untars a file into the specified output directory TODO: Use CheckSums? TODO: Validate that
	 * unpackTarFile works as advertised. Noticed some incomplete unpacking when dealing with large
	 * tar files in the Elsevier collection
	 * 
	 * @param tarFile
	 * @param outputDirectory
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static void unpackTarFile(File tarFile, File outputDirectory) throws IllegalArgumentException, IOException {
		FileUtil.validateDirectory(outputDirectory);
		if (!isTarFile(tarFile)) {
			throw new IllegalArgumentException(String.format("Cannot unpack. Input file is not a tarball: %s",
					tarFile.getAbsolutePath()));
		}
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
	 * Enum representing a boolean parameter indicating whether or not the base directory should be
	 * included when packaging a tar file
	 * 
	 * @author bill
	 * 
	 */
	public enum IncludeBaseDirectoryInPackage {
		/**
		 * Indicates that the base directory should be included as part of the tar file contents
		 */
		YES,
		/**
		 * Indicates that the base directory should not be included as part of the tar file contents
		 */
		NO
	}

	/**
	 * Packs a directory and its contents into a tarball
	 * 
	 * @param directoryToPack
	 * @param tarFile
	 * @throws IOException
	 */
	public static void packTarFile(File directoryToPack, File tarFile,
			IncludeBaseDirectoryInPackage includeBaseDirectory) throws IOException {
		FileUtil.validateDirectory(directoryToPack);
		TarOutputStream tos = new TarOutputStream(new FileOutputStream(tarFile));
		tos.setLongFileMode(TarOutputStream.LONGFILE_GNU);
		try {
			for (Iterator<File> fileIter = FileUtil.getFileIterator(directoryToPack, true); fileIter.hasNext();) {
				File file = fileIter.next();
				File relativeDirectoryTarget = (includeBaseDirectory.equals(IncludeBaseDirectoryInPackage.YES)) ? directoryToPack
						.getParentFile() : directoryToPack;
				TarEntry tarEntry = new TarEntry(FileUtil.getFileRelativeToDirectory(file, relativeDirectoryTarget));
				tarEntry.setSize(file.length());
				tos.putNextEntry(tarEntry);
				FileInputStream fis = new FileInputStream(file);
				IOUtils.copyLarge(fis, tos);
				fis.close();
				tos.closeEntry();
			}
		} finally {
			tos.close();
		}
	}

	/**
	 * Packages the contents of the specified directory into a jar file. Can also be used to create
	 * WAR files.
	 * 
	 * @param directoryToPack
	 * @param jarFile
	 * @param includeBaseDirectory
	 * @throws IOException
	 */
	public static void packJarFile(File directoryToPack, File jarFile,
			IncludeBaseDirectoryInPackage includeBaseDirectory) throws IOException {
		FileUtil.validateDirectory(directoryToPack);
		JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile));
		try {
			for (Iterator<File> fileIter = FileUtil.getFileIterator(directoryToPack, true); fileIter.hasNext();) {
				File file = fileIter.next();
				File relativeDirectoryTarget = (includeBaseDirectory.equals(IncludeBaseDirectoryInPackage.YES)) ? directoryToPack
						.getParentFile() : directoryToPack;
				JarEntry tarEntry = new JarEntry(FileUtil.getFileRelativeToDirectory(file, relativeDirectoryTarget)
						.getAbsolutePath());
				tarEntry.setSize(file.length());
				jos.putNextEntry(tarEntry);
				FileInputStream fis = new FileInputStream(file);
				IOUtils.copyLarge(fis, jos);
				fis.close();
				jos.closeEntry();
			}
		} finally {
			jos.close();
		}
	}

	/**
	 * Unpacks a jar file into the specified directory
	 * 
	 * @param jarFile
	 * @param outputDirectory
	 * @throws IOException
	 */
	public static void unpackJarFile(File jarFile, File outputDirectory) throws IOException {
		JarFile jar = new JarFile(jarFile);
		for (Enumeration<JarEntry> jarEntryEnum = jar.entries(); jarEntryEnum.hasMoreElements();) {
			JarEntry jarEntry = jarEntryEnum.nextElement();
			copyJarEntryToFileSystem(jar.getInputStream(jarEntry), jarEntry, outputDirectory);
		}
	}

	/**
	 * Untars the collection of files into the specified output directory
	 * 
	 * @param tarFiles
	 * @param outputDirectory
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static void unpackTarFiles(Collection<File> tarFiles, File outputDirectory) throws FileNotFoundException,
			IllegalArgumentException, IOException {
		for (File tarFile : tarFiles) {
			unpackTarFile(tarFile, outputDirectory);
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
			FileUtil.mkdir(outputPath.getParentFile());
			copyTarEntryToFile(tis, outputPath);
		}
	}

	/**
	 * Copies the current TarEntry to the specified output file
	 * 
	 * @param tis
	 * @param outputFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void copyTarEntryToFile(TarInputStream tis, File outputFile) throws FileNotFoundException,
			IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outputFile);
			tis.copyEntryContents(fos);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Unzips the specified file into the specified directory
	 * 
	 * @param zippedFile
	 * @param outputDirectory
	 * @return a reference to the unzipped file
	 * @throws IOException
	 */
	public static File unzip(File zippedFile, File outputDirectory) throws IOException {
		InputStream is = null;
		try {
			if (isZipFile(zippedFile)) {
				is = getZipInputStream(zippedFile);
				return unzip((ZipInputStream) is, outputDirectory);
			} else if (isGzipFile(zippedFile)) {
				is = getGzipInputStream(zippedFile);
				return unzip((GZIPInputStream) is, getUnzippedFileName(zippedFile.getName()), outputDirectory);
			} else if (isUnixCompressFile(zippedFile)) {
				is = getUncompressInputStream(zippedFile);
				return unzip((UncompressInputStream) is, getUnzippedFileName(zippedFile.getName()), outputDirectory);
			} else {
				throw new IllegalArgumentException(String.format("Unable to unzip file: %s",
						zippedFile.getAbsolutePath()));
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
	public static String getUnzippedFileName(String filename) {
		if (filename.endsWith(gz_suffix.toLowerCase())) {
			return StringUtil.removeSuffix(filename, gz_suffix.toLowerCase());
		} else if (filename.endsWith(gz_suffix.toUpperCase())) {
			return StringUtil.removeSuffix(filename, gz_suffix.toUpperCase());
		} else if (filename.endsWith(tgz_suffix.toLowerCase())) {
			return StringUtil.replaceSuffix(filename, tgz_suffix.toLowerCase(), tar_suffix.toLowerCase());
		} else if (filename.endsWith(tgz_suffix.toUpperCase())) {
			return StringUtil.replaceSuffix(filename, tgz_suffix.toUpperCase(), tar_suffix.toUpperCase());
		} else if (filename.endsWith(z_suffix)) {
			return StringUtil.removeSuffix(filename, z_suffix);
		} else if (filename.endsWith(zip_suffix)) {
			return StringUtil.removeSuffix(filename, zip_suffix);
		} else {
			throw new IllegalArgumentException(String.format(
					"Only works for .gz and .tgz filenames. Input filename was: %s", filename));
		}
	}

	/**
	 * Given a reference to a zipped file, this method returns a reference to the appropriately
	 * names unzipped version of this file (typically by stripping off the zip suffix)
	 * 
	 * @param zippedFile
	 * @return
	 */
	public static File getUnzippedFileReference(File zippedFile) {
		String unzippedFileName = getUnzippedFileName(zippedFile.getName());
		File unzippedFile = FileUtil.appendPathElementsToDirectory(zippedFile.getParentFile(), unzippedFileName);
		return unzippedFile;
	}

	/**
	 * Unzip method specific to gzipped files
	 * 
	 * @param gzipInputStream
	 * @param outputFileName
	 * @param outputDirectory
	 * @return
	 * @throws IOException
	 */
	public static File unzip(GZIPInputStream gzipInputStream, String outputFileName, File outputDirectory)
			throws IOException {
		File outputFile = new File(outputDirectory.getAbsolutePath() + File.separator + outputFileName);
		FileUtil.copy(gzipInputStream, outputFile);
		return outputFile;
	}

	/**
	 * Unzips the input ZipInputStream to the specified directory
	 * 
	 * @param zis
	 * @param outputDirectory
	 * @throws IOException
	 */
	public static File unzip(ZipInputStream zis, File outputDirectory) throws IOException {
		ZipEntry zipEntry = null;
		boolean isFirst = true;
		File outputFile = null;
		while ((zipEntry = zis.getNextEntry()) != null) {
			File unzippedFile = copyZipEntryToFileSystem(zis, zipEntry, outputDirectory);
			if (isFirst) {
				outputFile = unzippedFile;
				isFirst = false;
			}
		}
		return outputFile;
	}

	/**
	 * Unzip method specific to .Z files
	 * 
	 * @param uncompressInputStream
	 * @param outputFileName
	 * @param outputDirectory
	 * @return
	 * @throws IOException
	 */
	public static File unzip(UncompressInputStream uncompressInputStream, String outputFileName, File outputDirectory)
			throws IOException {
		File outputFile = new File(outputDirectory.getAbsolutePath() + File.separator + outputFileName);
		FileUtil.copy(uncompressInputStream, outputFile);
		return outputFile;
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
	private static File copyZipEntryToFileSystem(ZipInputStream zis, ZipEntry zipEntry, File outputDirectory)
			throws FileNotFoundException, IOException {
		File outputPathFile = new File(outputDirectory.getAbsolutePath() + File.separator + zipEntry.getName());
		if (zipEntry.isDirectory()) {
			FileUtil.mkdir(outputPathFile);
		} else {
			FileUtil.mkdir(outputPathFile.getParentFile());
			copyZipEntryToFile(zis, outputPathFile);
		}
		return outputPathFile;
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
	private static void copyZipEntryToFile(ZipInputStream zis, File unzippedFile) throws IOException {
		FileUtil.copy(zis, unzippedFile);
	}

	/**
	 * Copies the contents of the input JarEntry InputStream to a file
	 * 
	 * @param jarEntryStream
	 * @param jarEntry
	 * @param outputDirectory
	 * @return
	 * @throws IOException
	 */
	private static File copyJarEntryToFileSystem(InputStream jarEntryStream, JarEntry jarEntry, File outputDirectory)
			throws IOException {
		File outputFile = FileUtil.appendPathElementsToDirectory(outputDirectory, jarEntry.getName());
		if (jarEntry.isDirectory()) {
			FileUtil.mkdir(outputFile);
		} else {
			FileUtil.mkdir(outputFile.getParentFile());
			FileUtil.copy(jarEntryStream, outputFile);
		}
		return outputFile;
	}

	
}
