/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.commons.string;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ConQATProcessorTestCaseBase;

/**
 * Test case for {@link StringSplitter}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 42175 $
 * @ConQAT.Rating GREEN Hash: 4902F07C9D0D10DC652CE5599EDDBFB2
 */
public class StringSplitterTest extends ConQATProcessorTestCaseBase {

	/** Separator used to split parts */
	private static final String SEPARATOR = "/";

	/** Split string used in tests */
	private static final String A_B_C = "a/b/c";

	/** Test splitting and part access using positive indexes */
	public void testPositiveIndex() throws ConQATException {
		assertEquals("a", executeStringSplitter(A_B_C, SEPARATOR, 0));
		assertEquals("b", executeStringSplitter(A_B_C, SEPARATOR, 1));
		assertEquals("c", executeStringSplitter(A_B_C, SEPARATOR, 2));
	}

	/** Test splitting and part access using negative indexes */
	public void testNegativeIndex() throws ConQATException {
		assertEquals("c", executeStringSplitter(A_B_C, SEPARATOR, -1));
		assertEquals("b", executeStringSplitter(A_B_C, SEPARATOR, -2));
		assertEquals("a", executeStringSplitter(A_B_C, SEPARATOR, -3));
	}

	/** Test exception handling. */
	public void testTooLargePositiveIndes() {
		try {
			executeStringSplitter(A_B_C, SEPARATOR, 3);
			fail("Access to too large index should not work.");
		} catch (ConQATException e) {
			// expected
		}
	}

	/** Test exception handling. */
	public void testTooLargeNegativeIndes() {
		try {
			executeStringSplitter(A_B_C, SEPARATOR, -4);
			fail("Access to too large index should not work.");
		} catch (ConQATException e) {
			// expected
		}
	}

	/** Runs StringSplitter and returns result */
	private String executeStringSplitter(String splitString, String regex,
			int index) throws ConQATException {
		Object result = executeProcessor(StringSplitter.class,
				"(split=(regex='" + regex + "', index=" + index
						+ "), input=(ref='" + splitString + "'))");
		return result.toString();
	}

}
