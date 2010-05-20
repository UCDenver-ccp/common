package edu.ucdenver.ccp.util.file;

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



public class DirectoryListing {

	static List<File> getFiles(String dirPath, String suffix, Boolean recurse){
	    /* Find all nxml files in the directory and place in List nxmlFiles */
	    
	    IOFileFilter dirFilter = FileFilterUtils.andFileFilter(
	    		FileFilterUtils.directoryFileFilter(),
	    		HiddenFileFilter.VISIBLE);
	    IOFileFilter nxmlFileFilter = FileFilterUtils.andFileFilter(
	    		FileFilterUtils.fileFileFilter(),
	    		FileFilterUtils.suffixFileFilter(suffix));
	    
	    FileFilter filter = FileFilterUtils.orFileFilter(dirFilter, nxmlFileFilter);
	    CollectFilesWalker dirWalker=null;
	    if (recurse) {
	    		dirWalker = new CollectFilesWalker(filter,-1);
	    }
	    else {
			dirWalker = new CollectFilesWalker(filter,1);
	    }
	    
	    List<File> files = null;
	    File dirFile=null;
	    try {	
	    	dirFile = new File(dirPath);
	    	files = dirWalker.getFiles(dirFile);
	    }
	    catch (Exception x) {
	    	System.out.println("error" + x);
	    	x.printStackTrace();
	    	System.out.println(dirPath + ", " + dirFile);
	    }
	    finally {
	    	return files;
	    }

	}
}

// nearly trivial extension required by abstract DirectoryWalker used below
class CollectFilesWalker extends DirectoryWalker {
	public CollectFilesWalker(FileFilter filter,int levels) {
			super(filter, levels);
	}
	protected void handleFile(File file, int depth, Collection results) throws IOException {
		results.add(file);
	}
	
	public List<File> getFiles(File dir) throws IOException {
		List<File> files = new ArrayList<File>();
		walk(dir, files);
		return files;
	}
}


