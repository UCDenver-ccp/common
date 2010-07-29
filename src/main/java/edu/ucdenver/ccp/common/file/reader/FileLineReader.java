package edu.ucdenver.ccp.common.file.reader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import edu.ucdenver.ccp.common.file.FileUtil;

public class FileLineReader extends LineReader {

	private final BufferedRafReader reader;
	private int lineNumber = 0;

	public FileLineReader(File dataFile, String encoding) throws IOException {
		super(null);
		FileUtil.validateFile(dataFile);
		reader = new BufferedRafReader(dataFile, encoding);
		lineNumber = 0;
	}

	public FileLineReader(File dataFile, String encoding, String skipLinePrefix) throws IOException {
		super(skipLinePrefix);
		FileUtil.validateFile(dataFile);
		reader = new BufferedRafReader(dataFile, encoding);
		lineNumber = 0;
	}

	@Override
	public FileLine readLine() throws IOException {
		try {
			long byteOffset = reader.getFilePointer();
			String line = reader.readBufferedLine();
			if (line == null)
				return null;
			if (skipLine(line))
				return readLine();
			return new FileLine(line, lineNumber++, byteOffset);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		if (reader != null)
			reader.close();
	}

	/**
	 * Get current file pointer
	 * 
	 * @return file pointer
	 * @throws IOException
	 * @see RandomAccessFile
	 */
	protected long getFilePointer() throws IOException {
		return reader.getFilePointer();
	}

	/**
	 * Set current file pointer.
	 * 
	 * @param position
	 * @throws RuntimeException
	 *             if errors occur
	 * @see RandomAccessFile#seek(long)
	 */
	public void seek(long position) {
		try {
			reader.seek(position);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
