package edu.ucdenver.ccp.common.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class CollectionsUtil {
	private static final Logger logger = Logger.getLogger(CollectionsUtil.class);

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
	 * Returns the contents of an iterator as a list
	 * 
	 * @param <T>
	 * @param iter
	 * @return
	 */
	public static <T> List<T> createList(Iterator<T> iter) {
		List<T> list = new ArrayList<T>();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		return list;
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

	/**
	 * Converts an array<T> into a Set<T>
	 * 
	 * @param <T>
	 * @param array
	 * @return
	 */
	public static <T> Set<T> array2Set(T[] array) {
		return new HashSet<T>(Arrays.asList(array));
	}

	/**
	 * Returns a mapping based on the input key/value pairings. Keys and Values must be the same
	 * type, and there must be an even number of input parameters, i.e. there must be a value for
	 * every key.
	 * 
	 * @param <T>
	 * @param mapKeyValuePairs
	 * @return
	 */
	public static <T> Map<T, T> createMap(T... mapKeyValuePairs) {
		Map<T, T> map = new HashMap<T, T>();
		for (int i = 0; i < mapKeyValuePairs.length; i += 2) {
			map.put(mapKeyValuePairs[i], mapKeyValuePairs[i + 1]);
		}
		return map;
	}

	/**
	 * Combines all input maps and returns the aggregate map. Note, if a key appears in more than
	 * one map it will be overwritten by the last value that is merged into the combined map.
	 * 
	 * @param <K>
	 * @param <V>
	 * @param maps
	 * @return
	 */
	public static <K, V> Map<K, V> combineMaps(Map<K, V>... maps) {
		Map<K, V> combinedMap = new HashMap<K, V>();
		for (Map<K, V> map : maps) {
			combinedMap.putAll(map);
		}
		return combinedMap;
	}

	public static <K, V> Map<K, Set<V>> combineUniqueMaps(Map<K, Set<V>>... maps) {
		Map<K, Set<V>> combinedMap = new HashMap<K, Set<V>>();
		for (Map<K, Set<V>> inputMap : maps) {
			for (K key : inputMap.keySet()) {
				Set<V> values = inputMap.get(key);
				for (V value : values)
					addToOne2ManyUniqueMap(key, value, combinedMap);
			}
		}
		return combinedMap;
	}

	/**
	 * Converts a list of Strings to a list of Integers. Assumes the Strings are all integers.
	 * 
	 * @param intsAsStrings
	 * @return
	 */
	public static List<Integer> parseInts(List<String> intsAsStrings) {
		List<Integer> intsAsIntegers = new ArrayList<Integer>();
		for (String intAsString : intsAsStrings) {
			intsAsIntegers.add(Integer.parseInt(intAsString));
		}
		return intsAsIntegers;
	}

	/**
	 * Returns an array that goes from 0 to length-1 in value
	 * 
	 * @param length
	 * @return
	 */
	public static int[] createZeroBasedSequence(int length) {
		int[] sequence = new int[length];
		for (int i = 0; i < sequence.length; i++) {
			sequence[i] = i;
		}
		return sequence;
	}

	/**
	 * Adds a key/value pair to a one2many map, where the many are stored in a set.
	 * 
	 * @param <K>
	 * @param <V>
	 * @param key
	 * @param value
	 * @param one2ManyMap
	 */
	public static <K, V> void addToOne2ManyUniqueMap(K key, V value, Map<K, Set<V>> one2ManyMap) {
		if (one2ManyMap.containsKey(key)) {
			if (one2ManyMap.get(key).contains(value)) {
				logger.debug(String.format("Duplicate key/value pair detected: %s/%s", key, value));
			} else
				one2ManyMap.get(key).add(value);
		} else {
			Set<V> newSet = new HashSet<V>();
			newSet.add(value);
			one2ManyMap.put(key, newSet);
		}
	}

	public static <K, V> void addToOne2ManyMap(K key, V value, Map<K, Collection<V>> one2ManyMap) {
		if (one2ManyMap.containsKey(key)) {
			one2ManyMap.get(key).add(value);
		} else {
			Collection<V> newCollection = new ArrayList<V>();
			newCollection.add(value);
			one2ManyMap.put(key, newCollection);
		}
	}

	/**
	 * This method produces combinations by taking a single entry for each input list. For example,
	 * if the input lists were: list0=[A,B], list1=[Q], list2=[Y,Z] <br>
	 * The resulting output would be Lists of length 3 containing the following tuples:<br>
	 * A Q Y<br>
	 * A Q Z<br>
	 * B Q Y<br>
	 * B Q Z<br>
	 * 
	 * @param <T>
	 * @param lists
	 * @return
	 */
	public static <T> Iterator<List<T>> computeCombinations(List<Collection<T>> lists) {
		if (lists.size() > 1) {
			List<List<T>> tuples = new ArrayList<List<T>>();
			for (T t : lists.get(0)) {
				List<T> entries = new ArrayList<T>();
				entries.add(t);
				createTuple(tuples, entries, lists);
			}
			return tuples.iterator();
		}
		throw new RuntimeException("Cannot compute combinations with less than two lists.");
	}

	/**
	 * This recursive method is used by the computeCombinations() method. It recursively adds tuples
	 * to the input tuples list as they are formed.
	 * 
	 * @param <T>
	 * @param tuples
	 * @param entries
	 * @param lists
	 */
	private static <T> void createTuple(List<List<T>> tuples, List<T> entries, List<Collection<T>> lists) {
		if (entries.size() == lists.size() - 1) {
			for (T t : lists.get(entries.size())) {
				List<T> tuple = new ArrayList<T>(entries);
				tuple.add(t);
				tuples.add(tuple);
			}
		} else {
			for (T t : lists.get(entries.size())) {
				List<T> updatedEntries = new ArrayList<T>(entries);
				updatedEntries.add(t);
				createTuple(tuples, updatedEntries, lists);
			}
		}
	}

}
