package edu.ucdenver.ccp.common.file.reader;

/**
 * Simple class for defining a line extracted from a file. It tracks the byte offset from the
 * beginning of the file for the beginning of the line.
 * 
 * @author bill
 * 
 */
public class FileLine extends Line {
	/**
	 * Stores the number of bytes from the beginning of the file to the start of the text
	 * represented by this line
	 */
	private final long byteOffset;

	/**
	 * Initializes a new file line
	 * 
	 * @param text
	 * @param lineTerminator
	 * @param characterOffset
	 * @param codePointOffset
	 * @param lineNumber
	 * @param byteOffset
	 *            the number of bytes from the beginning of the file to the start of the text
	 *            represented by this line
	 */
	public FileLine(String text, LineTerminator lineTerminator, long characterOffset, long codePointOffset, long lineNumber, long byteOffset) {
		super(text, lineTerminator, characterOffset, codePointOffset, lineNumber);
		this.byteOffset = byteOffset;
	}

	/**
	 * @return the byte offset - the number of bytes from the beginning of the file to the start of
	 *         the text represented by this line
	 */
	public long getByteOffset() {
		return byteOffset;
	}
}
