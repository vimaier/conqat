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
package org.conqat.engine.code_clones.detection.suffixtree;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for clone detecting suffix tree tests.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 2F99319D3C33A72E572C1FEA0E2AA57B
 */
public abstract class CloneDetectingSuffixTreeTestBase extends TestCase {

	/** Simple test case. */
	public void testSimple() throws ConQATException {
		List<Character> word = SuffixTreeTest.stringToList("ab1ab");

		List<List<String>> cloneClasses = findClones(word);

		print(cloneClasses);

		assertEquals(1, cloneClasses.size());
		assertCloneClass(cloneClasses.get(0), "ab", "ab");
	}

	/** Simple test case. */
	public void testTriple() throws ConQATException {
		List<Character> word = SuffixTreeTest.stringToList("ab1ab2ab");

		List<List<String>> cloneClasses = findClones(word);

		print(cloneClasses);

		assertEquals(1, cloneClasses.size());
		assertCloneClass(cloneClasses.get(0), "ab", "ab", "ab");
	}

	/** Simple test case. */
	public void testConnectedTriple() throws ConQATException {
		List<Character> word = SuffixTreeTest.stringToList("ababab");

		List<List<String>> cloneClasses = findClones(word);

		print(cloneClasses);

		assertEquals(2, cloneClasses.size());
		assertCloneClass(cloneClasses.get(1), "ab", "ab", "ab");
	}

	/** Simple test case. */
	public void test2_3Nested() throws ConQATException {
		List<Character> word = SuffixTreeTest.stringToList("abcXabcYab");

		List<List<String>> cloneClasses = findClones(word);

		print(cloneClasses);

		assertEquals(2, cloneClasses.size());
		assertCloneClass(cloneClasses.get(0), "abc", "abc");
		assertCloneClass(cloneClasses.get(1), "ab", "ab", "ab");
	}

	/** Simple test case. */
	public void testMultipleOverlapping() throws ConQATException {
		List<Character> word = SuffixTreeTest.stringToList("abc1ab2abc");

		List<List<String>> cloneClasses = findClones(word);

		print(cloneClasses);

		assertEquals(2, cloneClasses.size());
		assertCloneClass(cloneClasses.get(0), "abc", "abc");
		assertCloneClass(cloneClasses.get(1), "ab", "ab", "ab");
	}

	/** Detects clones in a string */
	protected abstract List<List<String>> findClones(List<Character> word)
			throws ConQATException;

	/** Asserts that the actual clones are as expected */
	protected void assertCloneClass(List<String> actualClones,
			String... expectedClones) {
		assertTrue("Had difference: " + StringUtils.concat(actualClones, ", "),
				Arrays.equals(actualClones.toArray(), expectedClones));
	}

	/** Prints a clone class to stderr. Can be used for debugging purposes */
	protected void print(List<List<String>> cloneClasses) {
		System.err.println("----------");
		for (List<String> cloneClass : cloneClasses) {
			System.err.println("CC:");
			for (String clone : cloneClass) {
				System.err.println("\t" + clone);
			}
		}
	}

}