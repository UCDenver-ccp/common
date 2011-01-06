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

package edu.ucdenver.ccp.common.xml;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ucdenver.ccp.common.xml.XmlUtil;

public class XmlUtilTest {

	@Test
	public void testConvertXmlEscapeCharacters_quote() {
		String inputText = "This phrase has \"quotation marks\" in it.";
		String expectedText = "This phrase has &quot;quotation marks&quot; in it.";
		assertEquals(String.format("Should have converted quotes."), expectedText, XmlUtil
				.convertXmlEscapeCharacters(inputText));
	}
	
	@Test
	public void testConvertXmlEscapeCharacters_all() {
		String inputText = "This 'phrase has everything in it\" & <more>.";
		String expectedText = "This &apos;phrase has everything in it&quot; &amp; &lt;more&gt;.";
		assertEquals(String.format("Should have converted everything."), expectedText, XmlUtil
				.convertXmlEscapeCharacters(inputText));
	}
}
