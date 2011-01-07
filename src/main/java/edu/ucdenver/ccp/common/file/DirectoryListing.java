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

package edu.ucdenver.ccp.common.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

import org.apache.log4j.Logger;



public class DirectoryListing {
	
	static Logger logger = Logger.getLogger(DirectoryListing.class);

	public static List<File> getFiles(String dirPath, String suffix, Boolean recurse)
	throws IOException {
	    /* Find all nxml files in the directory and place in List nxmlFiles */
	    
		IOFileFilter dirFilter = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), HiddenFileFilter.VISIBLE);
		IOFileFilter nxmlFileFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(suffix));
	    
	    FileFilter filter = FileFilterUtils.or(dirFilter, nxmlFileFilter);
	    CollectFilesWalker dirWalker=null;
	    if (recurse) {
	    	dirWalker = new CollectFilesWalker(filter,-1);
	    }
	    else {
			dirWalker = new CollectFilesWalker(filter,1);
	    }
	    
	    List<File> files = null;
	    File dirFile=null;
    	dirFile = new File(dirPath);
    	files = dirWalker.getFiles(dirFile);
    	java.util.Collections.sort(files);
    	logger.debug(" returning " + files.size() + " list from " + dirPath);
    	return files;
	}
}

// nearly trivial extension required by abstract DirectoryWalker used below
class CollectFilesWalker extends DirectoryWalker<File> {
	public CollectFilesWalker(FileFilter filter,int levels) {
			super(filter, levels);
	}
	
	@Override
	protected void handleFile(File file, int depth, Collection<File> results) throws IOException {
		results.add(file);
	}
	
	public List<File> getFiles(File dir) throws IOException {
		List<File> files = new ArrayList<File>();
		walk(dir, files);
		return files;
	}
}


