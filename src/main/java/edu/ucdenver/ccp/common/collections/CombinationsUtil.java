package edu.ucdenver.ccp.common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class CombinationsUtil {

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
				CombinationsUtil.createTuple(tuples, entries, lists);
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
	 * This enum is used to control whether self pairs e.g. [A,A] or[1,1] are permitted
	 * 
	 * @author bill
	 * 
	 */
	public enum SelfPairing {
		ALLOW, PROHIBIT;
	}

	/**
	 * This method produces pairwise combinations of everything in the first collection with
	 * everything in the second collection. For example, if the input collections were: c1=[A,B],
	 * ct2=[Y,Z] <br>
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
					} else {
						return false;
					}
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
	 * Produces an Iterator of unique pairs composed of the input entries
	 * 
	 * @param <T>
	 * @param entries
	 * @return
	 */
	public static <T> Iterator<CombinationsUtil.Pair<T>> computePairwiseCombinations(Set<T> entries) {
		final List<T> outerList = Collections.list(Collections.enumeration(entries));

		// logger.info("Entries size: " + entries.size() + " -- " + entries.toString());
		if (entries.size() < 2)
			return IteratorUtil.getEmptyIterator();
		else
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
							nextPair = new CombinationsUtil.Pair<T>(outerList.get(outerIndex),
									outerList.get(innerIndex));
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
		private T member1;
		private T member2;

		public Pair(T member1, T member2) {
			super();
			this.member1 = member1;
			this.member2 = member2;
		}

		public boolean isSelfPair() {
			return member1.equals(member2);
		}

		public T getMember1() {
			return member1;
		}

		public void setMember1(T member1) {
			this.member1 = member1;
		}

		public T getMember2() {
			return member2;
		}

		public void setMember2(T member2) {
			this.member2 = member2;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * (member1.hashCode() * member2.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;

			if (member1 == null || member2 == null)
				throw new IllegalStateException("Pair.equals() is not equipped to handle null member values.");

			if (member1.equals(other.member1) && member2.equals(other.member2) || member1.equals(other.member2)
					&& member2.equals(other.member1))
				return true;

			return false;
		}

		@Override
		public String toString() {
			return "Pair[" + member1 + ", " + member2 + "]";
		}

	}

}
