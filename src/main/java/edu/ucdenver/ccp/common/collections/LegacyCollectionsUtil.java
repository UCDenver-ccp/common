package edu.ucdenver.ccp.common.collections;

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
