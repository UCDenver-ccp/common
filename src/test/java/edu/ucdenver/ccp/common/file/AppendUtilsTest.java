package edu.ucdenver.ccp.common.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;


import org.junit.Test;
import org.junit.Assert;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class AppendUtilsTest extends DefaultTestCase {
	
	@Test
	public void test() throws Exception {
	
		File f = folder.newFile("junk.utf8");
		File g = folder.newFile("moreJunk.utf8");
		
		final String fString = "Some basic text.";
		final String gString = "Some more text.";
	
		BufferedWriter fbw = FileWriterUtil.initBufferedWriter(f, CharacterEncoding.UTF_8);
		fbw.write(fString);
		fbw.close();
		
		BufferedWriter gbw = FileWriterUtil.initBufferedWriter(g, CharacterEncoding.UTF_8);
		gbw.write(gString);
		gbw.close();
		
		FileInputStream gis = new FileInputStream(g);
		AppendUtils.appendToFile(gis,  f);
		gis.close();
		
		BufferedReader br = new BufferedReader(new FileReader(f));
		String one = br.readLine();
		Assert.assertEquals(one, fString + gString);
		br.close();
	}
	
	@Test
	public void test2() throws Exception {
		File f = folder.newFile("junk.utf8");
		
		final String fString = "Some basic text.";
		final String gString = "Some more text.";
	
		BufferedWriter fbw = FileWriterUtil.initBufferedWriter(f, CharacterEncoding.UTF_8);
		fbw.write(fString);
		fbw.close();
	
		AppendUtils.appendToFile(gString,  f);
		
		
		BufferedReader br = new BufferedReader(new FileReader(f));
		String one = br.readLine();
		Assert.assertEquals(one, fString + gString);
		br.close();
	}

}
