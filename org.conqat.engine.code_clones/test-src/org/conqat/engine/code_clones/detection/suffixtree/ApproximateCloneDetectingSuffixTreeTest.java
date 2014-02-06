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
 * Tests for the {@link ApproximateCloneDetectingSuffixTree}.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: E869617A0E4E43819501192C4403467D
 */
public class ApproximateCloneDetectingSuffixTreeTest extends
		ApproximateCloneDetectingSuffixTreeTestBase {

	/** Simple test case for approximate matching. */
	public void testApproximate() throws ConQATException {
		List<Character> word = SuffixTreeTest
				.stringToList("abTHE_QUICK_BROWN_FOXjumpsjumpsTHE_QWICK_BROWN_FOXhiho");

		List<List<String>> cloneClasses = findClones(word, 1, 2);

		assertEquals(2, cloneClasses.size());
		assertCloneClass(cloneClasses.get(0), "THE_QUICK_BROWN_FOX",
				"THE_QWICK_BROWN_FOX");
		assertCloneClass(cloneClasses.get(1), "jumps", "jumps");
	}

	/** Simple test case 1. */
	public void testSimple1() throws ConQATException {
		testSingleCloneClass("abXc123abYc", "abXc", "abYc");
	}

	/** Simple test case 2. */
	public void testSimple2() throws ConQATException {
		testSingleCloneClass("abc123abc", "abc", "abc");
	}

	/** Simple test case 3. */
	public void testSimple3() throws ConQATException {
		testSingleCloneClass("abc123abXc", "abc", "abXc");
	}

	/** Simple test case. */
	public void testGroupProblem() throws ConQATException {
		List<Character> word = SuffixTreeTest
				.stringToList("abXc123abYc456abXc");

		List<List<String>> cloneClasses = findClones(word, 1, 3);

		assertEquals(2, cloneClasses.size());
		assertCloneClass(cloneClasses.get(0), "abXc", "abYc");
		assertCloneClass(cloneClasses.get(1), "abYc", "abXc", "abXc");
	}

}