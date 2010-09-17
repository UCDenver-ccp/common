/* Copyright (C) 2010 Center for Computational Pharmacology, University of Colorado School of Medicine
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
