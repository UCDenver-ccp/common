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

/**
 * Abstract class for reading a collection of <code>Line</code> objects.
 * 
 * @author bill
 * 
 */
public abstract class LineReader implements Closeable {

	/**
	 * The skipLinePrefix member variable is used as an indication that a line should be skipped
	 * (and not "read" by the LineReader). If a line starts with the skip line prefix then it is
	 * skipped
	 */
	protected final String skipLinePrefix;

	/**
	 * Constructor for the abstract LineReader
	 * 
	 * @param skipLinePrefix
	 *            if a line starts with the skip line prefix, then it is skipped (and not returned
	 *            by the LineReader)
	 */
	public LineReader(String skipLinePrefix) {
		this.skipLinePrefix = skipLinePrefix;
	}

	/**
	 * Abstract method that defines how an implementation of LineReader reads a line
	 * 
	 * @return the most recently read Line
	 * @throws IOException
	 */
	public abstract Line readLine() throws IOException;

	/**
	 * This method checks to see if a line should be skipped. It returns true if the line starts
	 * with the skip line prefix, fale otherwise.
	 * 
	 * @param line
	 * @return
	 */
	protected boolean skipLine(String line) {
		if (skipLinePrefix == null)
			return false;
		return line.trim().startsWith(skipLinePrefix);
	}

	/**
	 * Simple class for defining a line
	 * 
	 * @author bill
	 * 
	 */
	public static class Line {
		/**
		 * The text contained on this Line
		 */
		protected final String text;
		/**
		 * Stores the line number (relative to the specific collection from where the line was read)
		 */
		protected final int lineNumber;

		/**
		 * Initializes a new <code>Line</code>
		 * 
		 * @param text
		 * @param lineNumber
		 */
		public Line(String text, int lineNumber) {
			super();
			this.text = text;
			this.lineNumber = lineNumber;
		}

		/**
		 * @return the text of this particular line
		 */
		public String getText() {
			return text;
		}

		/**
		 * @return the line number for this particular line
		 */
		public int getLineNumber() {
			return lineNumber;
		}

		/**
		 * Returns a string representation of this line including the line number
		 */
		@Override
		public String toString() {
			return String.format("(Line:%d) %s", lineNumber, text);
		}

	}

}
