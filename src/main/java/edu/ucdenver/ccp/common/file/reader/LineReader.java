package edu.ucdenver.ccp.common.file.reader;

import java.io.Closeable;
import java.io.IOException;

public abstract class LineReader implements Closeable {

	protected final String skipLinePrefix;

	public LineReader(String skipLinePrefix) {
		this.skipLinePrefix = skipLinePrefix;
	}

	public abstract Line readLine() throws IOException;

	protected boolean skipLine(String line) {
		if (skipLinePrefix == null)
			return false;
		return line.trim().startsWith(skipLinePrefix);
	}

	public static class Line {
		protected final String text;
		protected final int lineNumber;

		public Line(String text, int lineNumber) {
			super();
			this.text = text;
			this.lineNumber = lineNumber;
		}

		public String getText() {
			return text;
		}

		public int getLineNumber() {
			return lineNumber;
		}

		@Override
		public String toString() {
			return String.format("(Line:%d) %s", lineNumber, text);
		}

	}

	public static class FileLine extends Line {
		private final long byteOffset;

		public FileLine(String text, int lineNumber, long byteOffset) {
			super(text, lineNumber);
			this.byteOffset = byteOffset;
		}

		public long getByteOffset() {
			return byteOffset;
		}
	}

}
