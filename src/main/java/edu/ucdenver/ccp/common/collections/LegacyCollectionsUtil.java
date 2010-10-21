package edu.ucdenver.ccp.common.collections;

import java.util.ArrayList;
import java.util.Collections;
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
	 * @param list
	 * @param clazz
	 * @return
	 */
	/*
	 * This method deals with the raw-type List. Because of this, type checking cannot be done by
	 * the compiler and is instead done at runtime therefore it is safe to ignore the "unchecked"
	 * compiler warnings.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> checkList(List list, Class<T> clazz) {
		List<T> checkedList = Collections.checkedList(new ArrayList<T>(), clazz);
		checkedList.addAll(list);
		return checkedList;
	}
}
