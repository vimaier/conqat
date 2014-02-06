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
 * Test case for the {@link SAPLinkXMLAttributeFixInputStream}
 * 
 * @author herrmama
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: BA2654A154C2D996D36C09F5D6AEADB8
 */
public class SAPLinkXMLAttributeFixInputStreamTest extends
		InputStreamTestBase {

	/**
	 * Test a mixture of escape characters and line breaks in an attribute value
	 */
	public void testEscapedLineBreak() throws Exception {
		assertEqualsStreamOutput("<x y=\"ab&#xA;&#xA;cd\">",
				"<x y=\"ab\r\r\n\r\r\ncd\">");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected XMLAttributeFixInputStream createInputStream(InputStream input) {
		return new SAPLinkXMLAttributeFixInputStream(input);
	}
}