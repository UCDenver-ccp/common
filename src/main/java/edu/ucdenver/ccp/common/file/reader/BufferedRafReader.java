/** A subclass of RandomAccessFile to enable basic buffering to a byte array
 * 
 *    Copyright (C) 2009 minddumped.blogspot.com

 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.ucdenver.ccp.common.file.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.reader.Line.LineTerminator;
import edu.ucdenver.ccp.common.string.StringUtil;

/**
 * Original code downloaded from http://minddumped.blogspot.com/search/label/buffered. The original
 * class was called BufferedRaf. It has since been renamed BufferedRafReader because it does not
 * implement any buffered writer functionality.
 * 
 * Changes: <br>
 * 1) Renamed class from BufferedRaf to BufferedRafReader <br>
 * 2) Implemented character encoding handling by adding the ByteList class and updating readLine2()<br>
 * 3) Changed readLine2() to readBufferedLine()<br>
 * 4) Added storage of the line break character(s) found on the line <br>
 * 5) Added tracking of the line terminators used on each line
 * 
 * @author minddumped.blogspot.com
 * @author Center for Computational Pharmacology
 */
public class BufferedRafReader extends RandomAccessFile {

	private byte[] bytebuffer;
	private int bufferlength;
	private int maxread;
	private int buffpos;
	private ByteList byteList;
	private LineTerminator lineTerminator = null;

	public BufferedRafReader(File file, CharacterEncoding encoding) throws FileNotFoundException {
		super(file, "r");
		bufferlength = 65536;
		bytebuffer = new byte[bufferlength];
		maxread = 0;
		buffpos = 0;
		byteList = new ByteList(encoding);
	}

	public int getbuffpos() {
		return buffpos;
	}

	@Override
	public int read() throws IOException {
		if (buffpos >= maxread) {
			maxread = readchunk();
			if (maxread == -1) {
				return -1;
			}
		}
		buffpos++;
		return bytebuffer[buffpos - 1];
	}

	public String readBufferedLine() throws IOException {
		byteList.clear();
		int c = -1;
		boolean eol = false;
		while (!eol) {
			switch (c = read()) {
			case -1:
			case '\n':
				eol = true;
				lineTerminator = LineTerminator.LF;
				break;
			case '\r':
				eol = true;
				lineTerminator = LineTerminator.CR;
				long cur = getFilePointer();
				if ((read()) != '\n') {
					seek(cur);
				} else
					lineTerminator = LineTerminator.CRLF;
				break;
			default:
				if (c < -128 || c > 127)
					throw new RuntimeException("Cast from int to byte will overflow!!");
				byteList.add((byte) c);
				break;
			}
		}

		if ((c == -1) && (byteList.size() == 0)) {
			return null;
		}
		return byteList.toString();
	}

	/**
	 * @return a {@link LineTerminator} representing the terminator found on the line most recently
	 *         read by readBufferedLine()
	 */
	public LineTerminator getLineTerminator() {
		if (lineTerminator == null)
			throw new IllegalStateException(
					"The line terminator has not been set. A call to readBufferedLine() must precede calls to getLineTerminator().");
		return lineTerminator;
	}

	@Override
	public long getFilePointer() throws IOException {
		return super.getFilePointer() + buffpos;
	}

	@Override
	public void seek(long pos) throws IOException {
		if (maxread != -1 && pos < (super.getFilePointer() + maxread) && pos > super.getFilePointer()) {
			Long diff = (pos - super.getFilePointer());
			if (diff < Integer.MAX_VALUE) {
				buffpos = diff.intValue();
			} else {
				throw new IOException("something wrong w/ seek");
			}
		} else {
			buffpos = 0;
			super.seek(pos);
			maxread = readchunk();
		}
	}

	private int readchunk() throws IOException {
		long pos = super.getFilePointer() + buffpos;
		super.seek(pos);
		int read = super.read(bytebuffer);
		super.seek(pos);
		buffpos = 0;
		return read;
	}

	/**
	 * Utility class for incrementally storing a list of bytes. This class also handles conversion
	 * from the byte array to a String using a particular character encoding.
	 * 
	 * @author Center for Computational Pharmacology
	 * 
	 */
	public static class ByteList {
		private List<Byte> bytes;
		private final CharacterEncoding encoding;

		public ByteList(CharacterEncoding encoding) {
			bytes = new ArrayList<Byte>();
			this.encoding = encoding;
		}

		public void add(byte aByte) {
			bytes.add(Byte.valueOf(aByte));
		}

		@Override
		public String toString() {
			byte[] byteArray = getByteArray();
			try {
				return StringUtil.decode(byteArray, encoding);
			} catch (IOException e) {
				throw new RuntimeException("Error converting ByteList to String.", e);
			}
		}

		private byte[] getByteArray() {
			byte[] byteArray = new byte[bytes.size()];
			int index = 0;
			for (Byte aByte : bytes)
				byteArray[index++] = aByte.byteValue();
			return byteArray;
		}

		void clear() {
			bytes.clear();
		}

		int size() {
			return bytes.size();
		}

	}
}
