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

package edu.ucdenver.ccp.common.lucene;

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
