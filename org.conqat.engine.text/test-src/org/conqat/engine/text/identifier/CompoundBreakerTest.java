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
package org.conqat.engine.text.identifier;

import java.util.List;

import junit.framework.TestCase;

/**
 * Test for {@link CompoundBreaker}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A58BEEFAB86EDD29908C82862CD3E7B3
 */
public class CompoundBreakerTest extends TestCase {

	/** Test {@link CompoundBreaker#breakCompound(String)}. */
	public void test() {
		assertCompound("errorDB", "error", "db");

		assertCompound("testMethod", "test", "method");
		assertCompound("test Method", "test", "method");
		assertCompound("test_Method", "test", "method");
		assertCompound("test_method", "test", "method");
		assertCompound("TEST_METHOD", "test", "method");
		assertCompound("test2_method", "test", "method");

		assertCompound("betterTestMethod", "better", "test", "method");
		assertCompound("better1Test2Method3", "better", "test", "method");
		assertCompound("bet1ter1Test2Method3", "bet1ter", "test", "method");

		assertCompound("test_123", "test");

		assertCompound("bet1ter1$Test#Method3", "bet1ter", "test", "method");
	}

	/** Tests an identifier with an acronym */
	public void testAcronym() {
		// Bugzilla #2562
		assertCompound("DBQueryAnalyzer", "db", "query", "analyzer");
		assertCompound("XMLReader", "xml", "reader");
	}

	/**
	 * Break compound and assert correctness of parts.
	 * 
	 * @param compound
	 *            compound to brake
	 * @param expectedParts
	 *            expected parts
	 */
	private void assertCompound(String compound, String... expectedParts) {
		List<String> actualParts = CompoundBreaker.breakCompound(compound);

		assertEquals(expectedParts.length, actualParts.size());

		for (int i = 0; i < expectedParts.length; i++) {
			assertEquals(expectedParts[i], actualParts.get(i));
		}

	}

}