package edu.ucdenver.ccp.util.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * A utility class used to merge Lucene indexes.
 * 
 * @author Bill Baumgartner
 * 
 */
public class IndexMerger {

	/**
	 * Merges indexes into a single directory
	 * 
	 * @param args
	 *            args[0] - the directory to store the merged index<br>
	 *            args[1..n] - directories containing indexes to include in the merge
	 */
	public static void main(String[] args) {
		File mergedIndexDirectory = new File(args[0]);
		List<File> directoriesOfIndexesToMerge = new ArrayList<File>();
		for (int i = 1; i < args.length; i++) {
			directoriesOfIndexesToMerge.add(new File(args[i]));
		}
		try {
			LuceneUtil.mergeIndexes(mergedIndexDirectory, new StandardAnalyzer(LuceneUtil.DEFAULT_VERSION),
					directoriesOfIndexesToMerge.toArray(new File[directoriesOfIndexesToMerge.size()]));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
