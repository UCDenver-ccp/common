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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This utility class is dedicated to dealing with raw type collections (used pre-Java 1.5) in such
 * a manner as they are safely converted to generic collections.
 * 
 * @author Center for Computational Pharmacology; ccpsupport@ucdenver.edu
 * 
 */
public class LegacyCollectionsUtil {

	/**
	 * This utility method ensures the content of the input raw-type list is as expected (in terms
	 * of the Classes being stored in the list) and returns a generified list. Type checking is done
	 * at runtime in this case, so compiler warnings for type checking are suppressed.
	 * 
	 * @param <T>
	 * @param list a raw-type list
	 * @param clazz the Class expected to comprise the contents of the input raw-type list
	 * @return a generified List which has been verified to contain only instances off the expected Class
	 * @throws ClassCastException if a member of the input list is not an instance of the expected class
	 */
	/*
	 * This method deals with the raw-type List. Because of this, type checking cannot be done by
	 * the compiler and is instead done at runtime, therefore it is safe to suppress the "unchecked"
	 * compiler warnings.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> checkList(@SuppressWarnings("rawtypes") List list, Class<T> clazz) {
		List<T> checkedList = Collections.checkedList(new ArrayList<T>(), clazz);
		try {
			checkedList.addAll(list);
		} catch (ClassCastException cce) {
			for (Object item : list)
				if (!(clazz.isInstance(item)))
					throw new ClassCastException(
							String.format(
									"Raw-type List contains an unexpected class. Cannot cast from class %s to expected class %s.",
									item.getClass().getName(), clazz.getName()));
		}
		return checkedList;
	}

	/**
	 * This utility method ensures the content of the input raw-type Iterator is as expected in
	 * regards to the Classes it returns. Type checking is done at runtime in this case so compiler
	 * warnings for unchecked types can be suppressed.
	 * 
	 * @param <T>
	 * @param iter a raw-type iterator
	 * @param clazz the Class expected by the user to be returned by the input raw-type iterator
	 * @return a generified Iterator containing only instances of the specified class
	 * @throws ClassCastException if the input iterator returns a class that is not an instance of the expected class
	 */
	public static <T> Iterator<T> checkIterator(@SuppressWarnings("rawtypes") final Iterator iter, final Class<T> clazz) {
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public T next() {
				return clazz.cast(iter.next());
			}

			@Override
			public void remove() {
				iter.remove();
			}

		};
	}

}
