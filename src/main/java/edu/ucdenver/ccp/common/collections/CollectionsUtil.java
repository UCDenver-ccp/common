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

package edu.ucdenver.ccp.common.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.string.StringUtil;

/**
 * Utility class for working with the Java <code>Collection</code> family
 * 
 * @author bill
 * 
 */
public class CollectionsUtil {
	/**
	 * logger used primarily for debug output in this class
	 */
	private static final Logger logger = Logger.getLogger(CollectionsUtil.class);

	/**
	 * Returns a List implementation for any input collection
	 * 
	 * @param <T>
	 * @param collection
	 * @return
	 */
	public static <T> List<T> createList(Collection<T> collection) {
		return Collections.list(Collections.enumeration(collection));
	}

	/**
	 * Returns a sorted list for any input collection
	 * 
	 * @param <T>
	 *            must extend Comparable<T>
	 * @param collection
	 * @return
	 */
	public static <T extends Comparable<T>> List<T> createSortedList(Collection<T> collection) {
		List<T> list = createList(collection);
		Collections.sort(list);
		return list;
	}

	/**
	 * Returns an empty list
	 * 
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> createList() {
		return Collections.emptyList();
	}

	/**
	 * Returns a List<T> containing the input arguments.
	 * 
	 * @param <T>
	 * @param item1
	 * @return
	 */
	public static <T> List<T> createList(T item1) {
		List<T> list = new ArrayList<T>();
		list.add(item1);
		return list;
	}

	/**
	 * Returns a List<T> containing the input arguments.
	 * 
	 * @param <T>
	 * @param item1
	 * @param item2
	 * @return
	 */
	public static <T> List<T> createList(T item1, T item2) {
		List<T> list = new ArrayList<T>();
		list.add(item1);
		list.add(item2);
		return list;
	}

	/**
	 * Returns a List<T> containing the input arguments.
	 * 
	 * @param <T>
	 * @param item1
	 * @param item2
	 * @param item3
	 * @return
	 */
	public static <T> List<T> createList(T item1, T item2, T item3) {
		List<T> list = new ArrayList<T>();
		list.add(item1);
		list.add(item2);
		list.add(item3);
		return list;
	}

	/**
	 * Returns a List<T> containing the input arguments.
	 * 
	 * @param <T>
	 * @param item1
	 * @param item2
	 * @param item3
	 * @param item4
	 * @return
	 */
	public static <T> List<T> createList(T item1, T item2, T item3, T item4) {
		List<T> list = new ArrayList<T>();
		list.add(item1);
		list.add(item2);
		list.add(item3);
		list.add(item4);
		return list;
	}

	/**
	 * Returns a List<T> containing the input arguments.
	 * 
	 * @param <T>
	 * @param item1
	 * @param item2
	 * @param item3
	 * @param item4
	 * @param otherItems
	 * @return
	 */
	public static <T> List<T> createList(T item1, T item2, T item3, T item4, T... otherItems) {
		List<T> list = new ArrayList<T>();
		list.add(item1);
		list.add(item2);
		list.add(item3);
		list.add(item4);
		for (T item : otherItems)
			list.add(item);
		return list;
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
	 * Helper method for initializing a HashMap. This saves the developer from having to repeat K
	 * and V on the right hand side.
	 * 
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> HashMap<K, V> initHashMap() {
		return new HashMap<K, V>();
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
	 * Creates a set using the members of the input Iterable<T>
	 * 
	 * @param <T>
	 * @param iterable
	 * @return
	 */
	public static <T> Set<T> createSet(Iterable<T> iterable) {
		Set<T> set = new HashSet<T>();
		for (T entry : iterable)
			set.add(entry);
		return set;
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

	// /**
	// * Returns a mapping based on the input key/value pairings. Keys and Values must be the same
	// * type, and there must be an even number of input parameters, i.e. there must be a value for
	// * every key.
	// *
	// * @param <T>
	// * @param mapKeyValuePairs
	// * @return
	// */
	// public static <T> Map<T, T> createMap(T... mapKeyValuePairs) {
	// Map<T, T> map = new HashMap<T, T>();
	// for (int i = 0; i < mapKeyValuePairs.length; i += 2) {
	// map.put(mapKeyValuePairs[i], mapKeyValuePairs[i + 1]);
	// }
	// return map;
	// }

	/**
	 * Simple utility to initialize a map with one key/value pair
	 * 
	 * @param <K>
	 * @param <V>
	 * @param key
	 * @param value
	 * @return
	 */
	public static <K, V> Map<K, V> createMap(K key, V value) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(key, value);
		return map;
	}

	/**
	 * Initialize a map using a variable number of {@link Entry} objects as input
	 * 
	 * @param entries
	 * @return
	 */
	public static <K, V> Map<K, V> createMap(Iterable<? extends Entry<K, V>> entries) {
		Map<K, V> map = new HashMap<K, V>();
		for (Entry<K, V> entry : entries)
			map.put(entry.getKey(), entry.getValue());
		return map;
	}

	/**
	 * Simple utility to initialize a map with two key/value pairs.
	 * 
	 * @param <K>
	 * @param <V>
	 * @param key1
	 * @param value1
	 * @param key2
	 * @param value2
	 * @return
	 */
	public static <K, V> Map<K, V> createMap(K key1, V value1, K key2, V value2) {
		if (key1.equals(key2))
			throw new IllegalArgumentException(
					"Key1 = Key2, value1 will be overwritten! The createMap method requires unique keys.");
		Map<K, V> map = new HashMap<K, V>();
		map.put(key1, value1);
		map.put(key2, value2);
		return map;
	}

	/**
	 * Simple utility to initialize a mpa with three key/value pairs.
	 * 
	 * @param <K>
	 * @param <V>
	 * @param key1
	 * @param value1
	 * @param key2
	 * @param value2
	 * @param key3
	 * @param value3
	 * @return
	 */
	public static <K, V> Map<K, V> createMap(K key1, V value1, K key2, V value2, K key3, V value3) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(key1, value1);
		map.put(key2, value2);
		map.put(key3, value3);
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

	/**
	 * Combines the set of input maps into a single <code>Map</code>
	 * 
	 * @param <K>
	 * @param <V>
	 * @param maps
	 * @return the consolidation of all input maps as a Map<K, Set<V>>
	 */
	public static <K, V> Map<K, Set<V>> combineUniqueMaps(Map<K, Set<V>>... maps) {
		Map<K, Set<V>> combinedMap = new HashMap<K, Set<V>>();
		for (Map<K, Set<V>> inputMap : maps) {
			for (Entry<K, Set<V>> entry : inputMap.entrySet()) {
				for (V value : entry.getValue())
					addToOne2ManyUniqueMap(entry.getKey(), value, combinedMap);
			}
		}
		return combinedMap;
	}

	/**
	 * Returns a filtered version of the input map by returning a map containing only the specified
	 * keys
	 * 
	 * @param inputMap
	 * @param keysToReturn
	 * @return
	 * @throws IllegalArgumentException
	 *             if a key to return is not present in the input map
	 */
	public static <K, V> Map<K, V> filterMap(Map<K, V> inputMap, Set<K> keysToReturn) {
		Map<K, V> returnMap = new HashMap<K, V>();
		for (K key : keysToReturn) {
			if (!inputMap.containsKey(key))
				throw new IllegalArgumentException("Filter map failed. Expected key not in input map: "
						+ key.toString());
			returnMap.put(key, inputMap.get(key));
		}
		return returnMap;
	}

	/**
	 * Simple enum for expressing the sort order to be used by a sorting operation
	 * 
	 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum SortOrder {
		ASCENDING(1),
		DESCENDING(-1);
		private final int modifier;

		private SortOrder(int modifier) {
			this.modifier = modifier;
		}

		public int modifier() {
			return modifier;
		}
	}

	/**
	 * Sorts the input map by its values and return a sorted version of the map
	 * 
	 * @param inputMap
	 * @return
	 */
	public static <K, V extends Comparable<V>> Map<K, V> sortMapByValues(Map<K, V> inputMap, final SortOrder sortOrder) {
		ArrayList<Entry<K, V>> entryList = new ArrayList<Entry<K, V>>(inputMap.entrySet());
		Collections.sort(entryList, new Comparator<Entry<K, V>>() {

			@Override
			public int compare(Entry<K, V> entry1, Entry<K, V> entry2) {
				return entry1.getValue().compareTo(entry2.getValue()) * sortOrder.modifier();
			}

		});
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();
		for (Entry<K, V> entry : entryList)
			sortedMap.put(entry.getKey(), entry.getValue());
		return sortedMap;
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
				logger.debug(String.format(
						"Adding value to Map<K, Set<V>>. Value (%s) for key (%s) is already present in the map.",
						value, key));
			} else
				one2ManyMap.get(key).add(value);
		} else {
			Set<V> newSet = new HashSet<V>();
			newSet.add(value);
			one2ManyMap.put(key, newSet);
		}
	}

	/**
	 * Adds the specified key-value pair to the input Map<K, Collection<V>>
	 * 
	 * @param <K>
	 * @param <V>
	 * @param key
	 * @param value
	 * @param one2ManyMap
	 *            this Map<K, Collection<V>> is updated by adding the specified key-value pair
	 */
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
	 * Updates the input key-to-count map by incrementing the count for the specified key
	 * 
	 * @param key
	 * @param keyToCountMap
	 */
	public static <K> void addToCountMap(K key, Map<K, Integer> keyToCountMap) {
		if (keyToCountMap.containsKey(key)) {
			int count = keyToCountMap.get(key) + 1;
			keyToCountMap.remove(key);
			keyToCountMap.put(key, count);
		} else {
			keyToCountMap.put(key, 1);
		}
	}

	/**
	 * Updates the input key-to-count map by incrementing the count for the specified key by the
	 * specified count amount
	 * 
	 * @param key
	 * @param count
	 * @param keyToCountMap
	 */
	public static <K> void addToCountMap(K key, int inputCount, Map<K, Integer> keyToCountMap) {
		if (keyToCountMap.containsKey(key)) {
			int count = keyToCountMap.get(key) + inputCount;
			keyToCountMap.remove(key);
			keyToCountMap.put(key, count);
		} else {
			keyToCountMap.put(key, inputCount);
		}
	}

	/**
	 * Consolidates the Collection of input Collections into a single Collection
	 * 
	 * @param <T>
	 * @param inputCollections
	 * @return
	 */
	public static <T> Collection<T> consolidate(Collection<Collection<T>> inputCollections) {
		Collection<T> collection = new ArrayList<T>();
		for (Collection<T> inputCollection : inputCollections)
			collection.addAll(inputCollection);
		return collection;
	}

	/**
	 * Consolidates the Collection of input Collections into a single Collection
	 * 
	 * @param <T>
	 * @param inputSets
	 * @return
	 */
	public static <T> Set<T> consolidateSets(Collection<Set<T>> inputSets) {
		Set<T> set = new HashSet<T>();
		for (Set<T> inputSet : inputSets)
			set.addAll(inputSet);
		return set;
	}

	/**
	 * Given an input collection, this method returns a delimited String containing the items in the
	 * collection
	 * 
	 * @param <T>
	 * @param collection
	 * @param delimiter
	 * @return
	 */
	public static <T> String createDelimitedString(Collection<T> collection, String delimiter) {
		if (collection == null)
			return null;
		if (collection.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		for (T element : collection) {
			sb.append(element.toString());
			sb.append(delimiter);
		}
		return StringUtil.removeSuffix(sb.toString(), delimiter);
	}

	/**
	 * Returns the single element contained by the input collection
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the collections contains either zerso or >1 elements
	 * @param <T>
	 * @param collection
	 * @return
	 */
	public static <T> T getSingleElement(Collection<T> collection) {
		if (collection.size() != 1)
			throw new IndexOutOfBoundsException(String.format(
					"Expected collection to contain a single element, but observed %d elements: %s", collection.size(),
					collection.toString()));
		return collection.iterator().next();
	}

	/**
	 * Returns the single element contained by the input Iterable<T>
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the iterable contains either zerso or >1 elements
	 * @param <T>
	 * @param collection
	 * @return
	 */
	public static <T> T getSingleElement(Iterable<T> iterable) {
		List<T> list = createList(iterable.iterator());
		return getSingleElement(list);
	}

	/**
	 * Returns a String representation of the input collection by calling each element's toString()
	 * method.
	 * 
	 * @param <T>
	 * @param collection
	 * @return
	 */
	public static <T> Collection<String> toString(Collection<T> collection) {
		Collection<String> strings = new ArrayList<String>();
		for (T item : collection)
			strings.add(item.toString());
		return strings;
	}

	/**
	 * Splices the input lists into a single list
	 * 
	 * @param list1
	 * @param list2
	 * 
	 * @return
	 */
	public static <T> List<T> splice(List<T> list1, List<T> list2) {
		List<T> outputList = new ArrayList<T>();
		outputList.addAll(list1);
		outputList.addAll(list2);
		return outputList;
	}

}
