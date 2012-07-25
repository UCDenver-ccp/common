/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.common.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Utility class for working with Iterators
 * 
 * @author bill
 * 
 */
/**
 * @author bill
 * 
 */
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

	/**
	 * Convenience method for consolidating two Iterators
	 * 
	 * @param <T>
	 * @param iterator1
	 * @param iterator2
	 * @return
	 */
	public static <T> Iterator<T> consolidate(Iterator<T> iterator1, Iterator<T> iterator2) {
		return consolidate(CollectionsUtil.createList(iterator1, iterator2));
	}

}
