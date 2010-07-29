package edu.ucdenver.ccp.common.file.reader;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.reader.LineReader.Line;


public abstract class LineIterator<T extends Line> implements Iterator<T> {
private static final Logger logger = Logger.getLogger(LineIterator.class);
	
	protected T nextLine = null;
	protected final LineReader reader;
	
	public LineIterator(Object fileOrStream, String encoding, String skipLinePrefix) throws IOException {
		reader = initLineReader(fileOrStream, encoding, skipLinePrefix);
	}
	
	public abstract LineReader initLineReader(Object fileOrStream, String encoding, String skipLinePrefix) throws IOException;

	@Override
	public boolean hasNext() {
		if (nextLine != null)
			return true;
		try {
			@SuppressWarnings("unchecked")
			T line = (T) reader.readLine();
			if (line == null)
				return false;
			logger.info("READ LINE: " + line.toString());
			nextLine = line;
			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		T lineToReturn = nextLine;
		nextLine = null;
		return lineToReturn;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("The remove() operation is not supported by LineIterator and its subclasses.");
	}
	
}
