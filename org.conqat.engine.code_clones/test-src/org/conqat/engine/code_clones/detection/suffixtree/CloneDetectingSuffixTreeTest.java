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

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.core.core.ConQATException;

/**
 * Tests for the {@link CloneDetectingSuffixTree}.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 44358CB787D0FB8D6024406911D1E916
 */
public class CloneDetectingSuffixTreeTest extends
		CloneDetectingSuffixTreeTestBase {

	/** Detects clones in a string */
	@Override
	protected List<List<String>> findClones(List<Character> word)
			throws ConQATException {
		word.add('$'); // append sentinel
		CloneConsumer consumer = new CloneConsumer(word);
		CloneDetectingSuffixTree stree = new CloneDetectingSuffixTree(word);
		stree.findClones(2, consumer);
		List<List<String>> cloneClasses = consumer.getCloneClasses();
		return cloneClasses;
	}

	/**
	 * Simple consumer implementation for testing purposes.
	 * <p>
	 * Does not work on units/tokens and {@link Clone}s and {@link CloneClass}
	 * es, but on characters instead, in order to simplify testing.
	 */
	public static class CloneConsumer implements ICloneReporter {

		/** The word we are doing clone detection for. */
		private final List<Character> word;

		/** Set that stores detected cloneClasses */
		private final List<List<String>> cloneClasses = new ArrayList<List<String>>();

		/** Clone class that currently gets constructed */
		private List<String> currentCloneClass;

		/** Constructor. */
		public CloneConsumer(List<Character> word) {
			this.word = word;
		}

		/** {@inheritDoc} */
		@Override
		public Clone addClone(int globalPosition, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < length; ++i) {
				sb.append(word.get(globalPosition + i));
			}
			currentCloneClass.add(sb.toString());

			return null;
		}

		/** {@inheritDoc} */
		@Override
		public void startCloneClass(int normalizedLength) {
			currentCloneClass = new ArrayList<String>();
			cloneClasses.add(currentCloneClass);
		}

		/** Gets the set of created clone classes */
		public List<List<String>> getCloneClasses() {
			return cloneClasses;
		}

		/** {@inheritDoc} */
		@Override
		public boolean completeCloneClass() {
			return true;
		}
	}
}