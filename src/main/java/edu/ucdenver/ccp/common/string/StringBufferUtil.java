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

package edu.ucdenver.ccp.common.string;

public class StringBufferUtil {

	/**
	 * Inserts the input String at the beginning of the StringBuffer text. (Note: this method is
	 * likely slow as insert causes a copy of the entire StringBuffer)
	 * 
	 * @param sb
	 * @param s
	 * @return
	 */
	public static StringBuffer prepend(StringBuffer sb, String s) {
		return sb.insert(0, s);
	}
	
	
	public static void appendLine(StringBuffer sb, String line) {
		sb.append(line + StringConstants.NEW_LINE);
	}
}
