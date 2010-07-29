package edu.ucdenver.ccp.common.file.reader;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.reader.LineReader.FileLine;

/**
 * A simple utility for providing an iterator over the lines from a file. The Line data structure
 * that is returned contains not only the text for the given line, but the line number as well as
 * the byte offset indicating the start of the line within the file.
 * 
 * @author bill
 * 
 */
public class FileLineIterator extends LineIterator<FileLine> {
	private static final Logger logger = Logger.getLogger(FileLineIterator.class);

	public FileLineIterator(File inputFile, String encoding, String skipLinePrefix) throws IOException {
		super(inputFile, encoding, skipLinePrefix);
		logger.info(String.format("Iterating through lines for file: %s", inputFile.getAbsolutePath()));
	}

	@Override
	public FileLineReader initLineReader(Object inputFile, String encoding, String skipLinePrefix) throws IOException {
		return new FileLineReader((File) inputFile, encoding, skipLinePrefix);
	}

}
