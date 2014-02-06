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

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.detection.suffixtree.CloneDetectingSuffixTreeTest.CloneConsumer;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for tests for the {@link ApproximateCloneDetectingSuffixTree}.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: F49AB49BB362D30BCD7E2A5EB2AABD65
 */
public abstract class ApproximateCloneDetectingSuffixTreeTestBase extends
		CloneDetectingSuffixTreeTestBase {

	/** Searches for single clone class in entry string */
	protected void testSingleCloneClass(String input, String... expectedClones)
			throws ConQATException {
		List<Character> word = SuffixTreeTest.stringToList(input);

		List<List<String>> cloneClasses = findClones(word, 1, 3);

		assertEquals(1, cloneClasses.size());
		assertCloneClass(cloneClasses.get(0), expectedClones);
	}

	/** Detects clones in a string */
	@Override
	protected final List<List<String>> findClones(List<Character> word)
			throws ConQATException {
		return findClones(word, 0, 2);
	}

	/**
	 * Detects approximate clones. Amount of difference is bounded by
	 * editDistance parameter.
	 */
	protected List<List<String>> findClones(List<Character> word,
			int editDistance, int minlength) throws ConQATException {
		word.add(new Character('$')); // append sentinel

		List<Object> word2 = new ArrayList<Object>();
		for (Character c : word) {
			if (c.equals(new Character('$'))) {
				word2.add(new SuffixTree.Sentinel());
			} else {
				word2.add(c);
			}
		}

		ApproximateCloneDetectingSuffixTree stree = new ApproximateCloneDetectingSuffixTree(
				word2) {
			@Override
			protected boolean mayNotMatch(Object character) {
				return character instanceof SuffixTree.Sentinel;
			}

			@Override
			protected void reportBufferShortage(int leafStart, int leafLength) {
				System.err.println("Encountered buffer shortage: " + leafStart
						+ " " + leafLength);
			}
		};
		CloneConsumer consumer = new CloneConsumer(word);
		stree.findClones(minlength, editDistance, 1, consumer);
		List<List<String>> cloneClasses = consumer.getCloneClasses();
		return cloneClasses;
	}

}