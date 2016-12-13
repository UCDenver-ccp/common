package edu.ucdenver.ccp.common.file.reader;

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
