package edu.ucdenver.ccp.parser;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.ucdenver.ccp.util.file.FileLoaderUtil;

public abstract class LineFileParser<T extends LineFileData> implements Iterator<T> {
	private T nextFileData = null;
	protected final Iterator<String> lineIterator;

	public LineFileParser(File inputFile, String encoding, String commentIndicator) throws IOException {
		this.lineIterator = FileLoaderUtil.getLineIterator(inputFile, encoding, commentIndicator);
		initialize();
	}

	public LineFileParser(File inputFile, String commentIndicator) throws IOException {
		this.lineIterator = FileLoaderUtil.getLineIterator(inputFile, commentIndicator);
		initialize();
	}

	public LineFileParser(File inputFile) throws IOException {
		this.lineIterator = FileLoaderUtil.getLineIterator(inputFile);
		initialize();
	}

	/**
	 * Method to be optionally overriden by subclasses. Can be used for example, to advance the line
	 * iterator thereby skipping file headers
	 */
	protected void initialize() {
	}

	@Override
	public boolean hasNext() {
		if (nextFileData == null) {
			if (lineIterator.hasNext()) {
				nextFileData = parseFileDataFromLine(lineIterator.next());
				return true;
			}
			return false;
		}
		return true;
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		T fileDataToReturn = nextFileData;
		nextFileData = null;
		return fileDataToReturn;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove() is not supported by LineFileParser.");
	}

	protected abstract T parseFileDataFromLine(String line);

}
