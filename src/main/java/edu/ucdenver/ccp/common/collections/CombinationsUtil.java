package edu.ucdenver.ccp.common.collections;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Utility class for producing various combinations given one (or multiple)
 * collections as input
 * 
 * @author bill
 * 
 */
public class CombinationsUtil {

	/**
	 * This method produces combinations by taking a single entry for each input
	 * list. For example, if the input lists were: list0=[A,B], list1=[Q],
	 * list2=[Y,Z] <br>
	 * The resulting output would be Lists of length 3 containing the following
	 * tuples:<br>
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
				CombinationsUtil.createTuple(tuples, entries, lists);
			}
			return tuples.iterator();
		}
		throw new RuntimeException("Cannot compute combinations with less than two lists.");
	}

	/**
	 * This recursive method is used by the computeCombinations() method. It
	 * recursively adds tuples to the input tuples list as they are formed.
	 * 
	 * @param <T>
	 * @param tuples
	 * @param entries
	 * @param lists
	 */
	static <T> void createTuple(List<List<T>> tuples, List<T> entries, List<Collection<T>> lists) {
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
	 * This enum is used to control whether self pairs e.g. [A,A] or[1,1] are
	 * permitted
	 * 
	 * @author bill
	 * 
	 */
	public enum SelfPairing {
		/**
		 * ALLOW specifies that combinations (particularly pairwise
		 * combinations) should allow pairings between identical objects
		 */
		ALLOW,
		/**
		 * PROHIBIT specifies that combinations (particularly pairwise
		 * combinates) between identical objects should be prohibited
		 */
		PROHIBIT;
	}

	/**
	 * This method produces pairwise combinations of everything in the first
	 * collection with everything in the second collection. For example, if the
	 * input collections were: c1=[A,B], ct2=[Y,Z] <br>
	 * The resulting output would be Pairs containing the following tuples:<br>
	 * A Y<br>
	 * A Z<br>
	 * B Y<br>
	 * B Z<br>
	 * 
	 * @param <T>
	 * @param collection1
	 * @param collection2
	 * @return
	 */
	public static <T> Iterator<CombinationsUtil.Pair<T>> computePairwiseCombinations(Collection<T> collection1,
			Collection<T> collection2, final SelfPairing selfPairing) {
		List<Collection<T>> list = new ArrayList<Collection<T>>();
		list.add(collection1);
		list.add(collection2);
		final Iterator<List<T>> pairIter = computeCombinations(list);

		return new Iterator<CombinationsUtil.Pair<T>>() {
			private CombinationsUtil.Pair<T> nextPair = null;

			@Override
			public boolean hasNext() {

				if (nextPair == null) {
					if (pairIter.hasNext()) {
						List<T> listOf2 = pairIter.next();
						if (listOf2.size() != 2)
							throw new RuntimeException(String.format("List must be of size 2, but was size %d",
									listOf2.size()));
						nextPair = new CombinationsUtil.Pair<T>(listOf2.get(0), listOf2.get(1));
						if (selfPairing == SelfPairing.PROHIBIT) {
							if (nextPair.isSelfPair()) {
								nextPair = null;
								return hasNext();
							}
						}
						return true;
					}

					return false;
				}
				return true;
			}

			@Override
			public CombinationsUtil.Pair<T> next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				CombinationsUtil.Pair<T> thingToReturn = nextPair;
				nextPair = null;
				return thingToReturn;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("The remove() method is not supported for this iterator.");
			}
		};
	}

	/**
	 * Produces an Iterator of unique pairs composed of the input entries. The
	 * ordering of the returned pairs is based on the natural ordering of T.
	 * 
	 * @param <T>
	 * @param entries
	 * @return
	 */
	public static <T extends Comparable<T>> Iterator<CombinationsUtil.Pair<T>> computePairwiseCombinations(
			Set<T> entries) {
		return computePairwiseCombinations(entries, null);
	}

	/**
	 * Produces an Iterator of unique pairs composed of the input entries. The
	 * ordering of the returned pairs is based on the input {@link Comparator}.
	 * 
	 * @param <T>
	 * @param entries
	 * @return
	 */
	public static <T extends Comparable<T>> Iterator<CombinationsUtil.Pair<T>> computePairwiseCombinations(
			Set<T> entries, Comparator<? super T> comparator) {
		final List<T> outerList = Collections.list(Collections.enumeration(entries));
		if (comparator == null) {
			Collections.sort(outerList);
		} else {
			Collections.sort(outerList, comparator);
		}

		// logger.info("Entries size: " + entries.size() + " -- " +
		// entries.toString());
		if (entries.size() < 2)
			return IteratorUtil.getEmptyIterator();

		return new Iterator<CombinationsUtil.Pair<T>>() {
			private int outerIndex = 0;
			private int innerIndex = 1;
			private int listLength = outerList.size();

			private CombinationsUtil.Pair<T> nextPair = null;

			@Override
			public boolean hasNext() {
				if (nextPair == null) {
					if (outerIndex < listLength || innerIndex < listLength) {
						if (innerIndex == listLength) {
							outerIndex++;
							if (outerIndex == listLength - 1)
								return false;
							innerIndex = outerIndex + 1;
						}
						nextPair = new CombinationsUtil.Pair<T>(outerList.get(outerIndex), outerList.get(innerIndex));
						innerIndex++;
						return true;
					}
					return false;
				}
				return true;
			}

			@Override
			public CombinationsUtil.Pair<T> next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				CombinationsUtil.Pair<T> pairToReturn = nextPair;
				nextPair = null;
				return pairToReturn;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("The remove operation is not supported by this iterator.");
			}

		};

	}

	/**
	 * A generic class for storing a pair of something
	 * 
	 * @author bill
	 * 
	 * @param <T>
	 */
	public static class Pair<T> {
		/**
		 * A member of the pair
		 */
		private final T member1;
		/**
		 * The other member of the pair
		 */
		private final T member2;

		/**
		 * Constructor for creating a <code>Pair</code> of two objects of the
		 * same type
		 * 
		 * @param member1
		 * @param member2
		 */
		public Pair(T member1, T member2) {
			super();
			this.member1 = member1;
			this.member2 = member2;
		}

		/**
		 * @return true if the <code>Pair</code> is composed of two objects who
		 *         are equivalent
		 */
		public boolean isSelfPair() {
			return member1.equals(member2);
		}

		/**
		 * @return one member of the pair
		 */
		public T getMember1() {
			return member1;
		}

		/**
		 * @return the other member of the pair
		 */
		public T getMember2() {
			return member2;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * (member1.hashCode() * member2.hashCode());
			return result;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair<?> other = (Pair<?>) obj;

			if (member1 == null || member2 == null)
				throw new IllegalStateException("Pair.equals() is not equipped to handle null member values.");

			if (member1.equals(other.member1) && member2.equals(other.member2) || member1.equals(other.member2)
					&& member2.equals(other.member1))
				return true;

			return false;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Pair[" + member1 + ", " + member2 + "]";
		}

	}

}
