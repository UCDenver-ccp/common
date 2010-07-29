package edu.ucdenver.ccp.common.file.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.reader.LineReader.Line;

public class StreamLineIterator extends LineIterator<Line> {

	private static final Logger logger = Logger.getLogger(StreamLineIterator.class);

	public StreamLineIterator(InputStream inputStream, String encoding, String skipLinePrefix) throws IOException {
		super(inputStream, encoding, skipLinePrefix);
		logger.info(String.format("Iterating through lines of InputStream"));
	}

	public StreamLineIterator(File inputFile, String encoding, String skipLinePrefix) throws IOException {
		super(new FileInputStream(inputFile), encoding, skipLinePrefix);
		logger.info(String.format("Iterating through lines for file: %s", inputFile.getAbsolutePath()));
	}

	@Override
	public StreamLineReader initLineReader(Object inputStream, String encoding, String skipLinePrefix)
			throws IOException {
		return new StreamLineReader((InputStream) inputStream, encoding, skipLinePrefix);
	}
	
}
