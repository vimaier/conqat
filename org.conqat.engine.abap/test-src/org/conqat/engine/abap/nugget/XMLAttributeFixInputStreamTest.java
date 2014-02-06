/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.engine.abap.nugget;

import java.io.InputStream;

/**
 * Test case for the {@link XMLAttributeFixInputStream}
 * 
 * @author herrmama
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 5FD1AFC91921FE411CA9EA6C68A36EBC
 */
public class XMLAttributeFixInputStreamTest extends
		InputStreamTestBase {

	/** Expected output for a number of tests */
	private static final String EXPECTED_STRING = "<x y=\"ab&#xA;&#xA;cd\">";

	/**
	 * Test for normal placement of a Windows line break in an attribute value
	 */
	public void testLineBreakWindows() throws Exception {
		assertEqualsStreamOutput(EXPECTED_STRING, "<x y=\"ab\r\n\r\ncd\">");
	}

	/**
	 * Test for normal placement of a Mac line break in an attribute value
	 */
	public void testLineBreakMac() throws Exception {
		assertEqualsStreamOutput(EXPECTED_STRING, "<x y=\"ab\r\rcd\">");
	}

	/**
	 * Test for normal placement of a Unix line break in an attribute value
	 */
	public void testLineBreakUnix() throws Exception {
		assertEqualsStreamOutput(EXPECTED_STRING, "<x y=\"ab\n\ncd\">");
	}

	/**
	 * Test a mixture of escape characters and line breaks in an attribute value
	 */
	public void testEscapedLineBreak() throws Exception {
		assertEqualsStreamOutput("<x y=\"a\\nb\\rc&#xA;&#xA;cd\">",
				"<x y=\"a\\nb\\rc&#xA;&#xA;cd\">");
	}

	/**
	 * Test for placement of a line break outside attribute values
	 */
	public void testLineBreakOutsideAttribute() {
		assertEqualsStreamOutput("<x\ny=\"abcd\">\n<z\n>",
				"<x\ny=\"abcd\">\n<z\n>");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected XMLAttributeFixInputStream createInputStream(InputStream input) {
		return new XMLAttributeFixInputStream(input);
	}

}