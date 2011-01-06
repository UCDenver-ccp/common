package edu.ucdenver.ccp.common.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteratorUtil {

	/**
	 * Returns an empty iterator
	 * 
	 * @param <T>
	 * @return
	 */
	public static <T> Iterator<T> getEmptyIterator() {
		return new Iterator<T>() {
	
			@Override
			public boolean hasNext() {
				return false;
			}
	
			@Override
			public T next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return null;
			}
	
			@Override
			public void remove() {
				throw new UnsupportedOperationException("The remove operation is not supported by this iterator.");
			}
	
		};
	}

	/**
	 * Consolidates a group of Iterators into a single Iterator
	 * 
	 * @param <T>
	 * @param inputIterators
	 * @return
	 */
	public static <T> Iterator<T> consolidate(final Iterable<Iterator<T>> inputIterators) {
		final Iterator<Iterator<T>> iteratorIter = inputIterators.iterator();
		return new Iterator<T>() {
			private T nextT = null;
			private Iterator<T> currentIterator = null;
	
			@Override
			public boolean hasNext() {
				if (nextT == null) {
					if (currentIterator == null) {
						if (iteratorIter.hasNext()) {
							currentIterator = iteratorIter.next();
						} else
							return false;
					}
					if (currentIterator.hasNext()) {
						nextT = currentIterator.next();
						return true;
					}
					
					currentIterator = null;
					return hasNext();
				}
				return true;
			}
	
			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
	
				T thingToReturn = nextT;
				nextT = null;
				return thingToReturn;
			}
	
			@Override
			public void remove() {
				throw new UnsupportedOperationException("The remove() method is not supported for this iterator.");
			}
		};
	}
	
	public static <T> Iterator<T> consolidate(Iterator<T> iterator1, Iterator<T> iterator2) {
		return consolidate(CollectionsUtil.createList(iterator1, iterator2));
	}

}
