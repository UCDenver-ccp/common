package edu.ucdenver.ccp.common.file.reader;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import edu.ucdenver.ccp.common.io.ClassPathUtil;

public class ExcelFileLineReaderTest {

	@Test
	public void testReadXlsxFile() throws IOException {
		InputStream xlsxStream = ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.xlsx");
		ExcelFileLineReader lineReader = new ExcelFileLineReader(xlsxStream, null);
		Line line = lineReader.readLine();
		assertEquals("a\tb\tc\td", line.getText());
		line = lineReader.readLine();
		assertEquals("a\t\tc\td", line.getText());
		line = lineReader.readLine();
		assertEquals("\tb\tc\td", line.getText());
		line = lineReader.readLine();
		assertEquals("\t\tc\t", line.getText());
		lineReader.close();
	}

	@Test
	public void testReadXlsFile() throws IOException {
		InputStream xlsxStream = ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.xls");
		ExcelFileLineReader lineReader = new ExcelFileLineReader(xlsxStream, null);
		Line line = lineReader.readLine();
		assertEquals("a\tb\tc\td", line.getText());
		line = lineReader.readLine();
		assertEquals("a\t\tc\td", line.getText());
		line = lineReader.readLine();
		assertEquals("\tb\tc\td", line.getText());
		line = lineReader.readLine();
		assertEquals("\t\tc\t", line.getText());
		lineReader.close();
	}
	
}
