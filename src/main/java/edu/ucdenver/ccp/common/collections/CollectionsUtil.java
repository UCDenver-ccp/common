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
	 * Simple utility method for creating a map using 2 keys and 2 values
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

}
