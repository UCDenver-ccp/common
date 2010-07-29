package edu.ucdenver.ccp.common.file.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamLineReader extends LineReader {

	private final BufferedReader reader;
	private int lineNumber = 0;

	public StreamLineReader(InputStream inputStream, String encoding, String skipLinePrefix) throws IOException {
		super(skipLinePrefix);
		lineNumber = 0;
		reader = new BufferedReader(new InputStreamReader(inputStream, encoding));
	}

	public StreamLineReader(File inputFile, String encoding, String skipLinePrefix) throws IOException {
		super(skipLinePrefix);
		lineNumber = 0;
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encoding));
	}

	@Override
	public Line readLine() throws IOException {
		String lineText = reader.readLine();
		if (lineText == null)
			return null;
		if (skipLine(lineText))
			return readLine();
		return new Line(lineText, lineNumber++);
	}

	@Override
	public void close() throws IOException {
		if (reader != null)
			reader.close();
	}

}
