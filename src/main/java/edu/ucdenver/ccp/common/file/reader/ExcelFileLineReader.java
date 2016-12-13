package edu.ucdenver.ccp.common.file.reader;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2016 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import edu.ucdenver.ccp.common.file.reader.Line.LineTerminator;

/**
 * Simple class to read lines from an Excel file. This class assumes that the
 * first line in a header (and uses the header to determine the number of
 * columns in the file).
 */
public class ExcelFileLineReader extends LineReader<Line> {

	private final int columnCount;
	private final int rowCount;
	private int currentRow = 1;
	private final Workbook wb;
	private final Sheet sheet;
	private long byteOffset = -1;

	
	public ExcelFileLineReader(File file, String skipLinePrefix) throws IOException {
		this(new FileInputStream(file), skipLinePrefix);
	}
	
	public ExcelFileLineReader(InputStream inputStream, String skipLinePrefix) throws IOException {
		super(skipLinePrefix);
		try {
			wb = WorkbookFactory.create(inputStream);
		} catch (EncryptedDocumentException | InvalidFormatException e) {
			throw new IOException(e);
		}
		sheet = wb.getSheetAt(0);

		// use the header row to define the number of columns
		Row headerRow = sheet.getRow(0);
		columnCount = headerRow.getLastCellNum();
		rowCount = sheet.getLastRowNum();

	}

	@Override
	public void close() throws IOException {
		wb.close();

	}

	@Override
	protected Line getNextLine() throws IOException {
		StringBuffer lineText = new StringBuffer();
		String delimiter = "\t";
		if (currentRow <= rowCount) {
			Row r = sheet.getRow(currentRow++);
			for (int col = 0; col < columnCount; col++) {
				Cell cell = r.getCell(col, MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (col > 0) {
					lineText.append(delimiter);
				}
				if (cell != null) {
					lineText.append(cell.getStringCellValue());
				}
			}
			return new Line(lineText.toString(), LineTerminator.CR, getCharacterOffset(), getCodePointOffset(),
					currentRow, byteOffset);
		}
		return null;
	}

}
