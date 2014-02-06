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
package org.conqat.engine.commons.input;

import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;

/**
 * Test for {@link XPathReader}.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: A9F8935982AADF307A1EF5C02009E4A5
 */
public class XPathReaderTest extends CCSMTestCaseBase {

	/** Test with legal XPath expressions. */
	public void test() throws ConQATException {
		assertEquals("deissenb", read("//username"));
		assertEquals("deissenb", read("/user/username"));
		assertEquals("11", read("/user/@id"));
		assertEquals(StringUtils.EMPTY_STRING, read("//email"));
		assertEquals("false", read("//boolean-test"));
	}

	/** Check that processor raises an exception for non-existent elements. */
	public void testNonExistentElement() {
		try {
			assertEquals(StringUtils.EMPTY_STRING,
					read("/non-existent-element"));
			fail();
		} catch (ConQATException e) {
			// expected
		}
	}

	/** Test with invalid expression. */
	public void testInvalidXPath() {
		try {
			read("//username[@id=]");
			fail();
		} catch (ConQATException e) {
			// expected
		}
	}

	/** Run processor and return value. */
	private String read(String xPath) throws ConQATException {
		XPathReader reader = new XPathReader();
		reader.init(new ProcessorInfoMock());
		reader.setXPath(xPath);
		reader.setFilename(useTestFile("test01.xml").getAbsolutePath());
		return reader.process();
	}
}