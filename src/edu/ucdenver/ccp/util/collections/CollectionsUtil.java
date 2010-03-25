package edu.ucdenver.ccp.util.collections;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionsUtil {

	/**
	 * Returns a List<T> containing the input arguments.
	 * 
	 * @param <T>
	 * @param listEntries
	 * @return
	 */
	public static <T> List<T> createList(T... listEntries) {
		if (listEntries == null) {
			return null;
		}
		return Arrays.asList(listEntries);
	}

	/**
	 * Returns a Set<T> containing the input arguments.
	 * 
	 * @param <T>
	 * @param setEntries
	 * @return
	 */
	public static <T> Set<T> createSet(T... setEntries) {
		if (setEntries == null) {
			return null;
		}
		return new HashSet<T>(Arrays.asList(setEntries));
	}

}
