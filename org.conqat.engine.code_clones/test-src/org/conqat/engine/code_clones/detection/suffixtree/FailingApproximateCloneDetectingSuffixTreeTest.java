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

import java.util.List;

import org.conqat.engine.core.core.ConQATException;

/**
 * Tests for the {@link ApproximateCloneDetectingSuffixTree} which fail. This is
 * kept separately to document the current limitations of our algorithm
 * implementation.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: F4873EAA2548828FA9237FE8CDDFD843
 */
public class FailingApproximateCloneDetectingSuffixTreeTest extends
		ApproximateCloneDetectingSuffixTreeTestBase {

	/** Simple test case 4. */
	public void disabled_testSimple4() throws ConQATException {
		testSingleCloneClass("abXc$abc", "abXc", "abc");
	}

	/** Tests more complex clone class */
	public void disabled_testComplex() throws ConQATException {
		List<Character> word = SuffixTreeTest
				.stringToList("abcAde$abcBCdXe$abcDEde");

		List<List<String>> cloneClasses = findClones(word, 2, 4);

		print(cloneClasses);
		assertEquals(2, cloneClasses.size());
		assertCloneClass(cloneClasses.get(0), "abcAde", "abcDEde");
		assertCloneClass(cloneClasses.get(1), "abcBCd", "abcDEd");
	}

	/**
	 * Tests how the clone detection algorithm deals with clones that have a
	 * repetitive start. This tests asserts that these regions don't produce
	 * nested clones that only differ in their start.
	 */
	public void disabled_testStartRepetition() throws ConQATException {
		List<Character> word = SuffixTreeTest.stringToList("aaab1aaab");

		List<List<String>> cloneClasses = findClones(word, 1, 2);

		print(cloneClasses);

		assertEquals(1, cloneClasses.size());
		assertCloneClass(cloneClasses.get(0), "aaab", "aaab");
	}

	/**
	 * Tests how the clone detection algorithm deals with clones that have a
	 * repetitive start. This tests asserts that these regions don't produce
	 * nested clones that only differ in their start.
	 */
	public void disabled_testEndRepetition() throws ConQATException {
		List<Character> word = SuffixTreeTest.stringToList("baaa1baaa");

		List<List<String>> cloneClasses = findClones(word, 1, 2);

		print(cloneClasses);

		assertEquals(1, cloneClasses.size());
		assertCloneClass(cloneClasses.get(0), "baaa", "baaa");
	}

}