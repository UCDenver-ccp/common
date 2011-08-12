package edu.ucdenver.ccp.common.file;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.filefilter.IOFileFilter;

public class FileListFilter implements IOFileFilter {
	
	Set<String> filenameSet;
	FileListFilter(Collection<String> list) {
		filenameSet = new TreeSet<String>();
		filenameSet.addAll(list);
	}

	@Override
	public boolean accept(File arg0) {
		return filenameSet.contains(arg0.getName());
	}

	/**
	 * ...ignores the directory. Could be  a bug...
	 */
	@Override
	public boolean accept(File arg0, String arg1) {
		return filenameSet.contains(arg1);
	}

}
