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
 * An extension of the suffix tree adding an algorithms for finding clones, i.e.
 * repeated substrings.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 5871E2913C37E323D479FB3AAFBD29D2
 */
public class CloneDetectingSuffixTree extends SuffixTree {

	/**
	 * The number of clones induced by other clones for which this is a suffix.
	 * See {@link #initInducedClones(int)} for details.
	 */
	private final int[] inducedClones;

	/** Start positions of the string induced by the leaf nodes. */
	private final int[] leafPositions;

	/** The consumer to which detected clones are reported */
	protected ICloneReporter consumer;

	/** The minimal length of clones to return. */
	protected int minLength;

	/**
	 * Create a new suffix tree from a given word. The word given as parameter
	 * is used internally and should not be modified anymore, so copy it before
	 * if required.
	 * <p>
	 * This only word correctly if the given word is closed using a sentinel
	 * character.
	 */
	public CloneDetectingSuffixTree(List<?> word) {
		super(word);

		ensureChildLists();
		inducedClones = new int[numNodes];
		leafPositions = new int[numNodes];
		initInducedClones(0);
	}

	/**
	 * Initializes the number of induced clones for each node. We know for each
	 * node, that the number of occurrences of the string represented by this
	 * node is equal to the number of leaves reachable from there. As each
	 * suffix of a clone is itself a clone, we propagate this number down the
	 * suffix links, so we later know, that a clone is only relevant if the
	 * number of clones is larger than the number of induces clones.
	 */
	private int initInducedClones(int node) {
		int result = 0;
		for (int e = nodeChildFirst[node]; e >= 0; e = nodeChildNext[e]) {
			result += initInducedClones(nodeChildNode[e]);
		}
		if (result == 0) {
			result = 1;
		}
		if (node != 0) {
			inducedClones[suffixLink[node]] = result;
		}
		return result;
	}

	/**
	 * Finds all clones in the string (List) used in the constructor. The basic
	 * algorithm is just a modified DFS on the suffix tree.
	 * 
	 * @param minLength
	 *            the minimal length of clones to return (must be positive).
	 *            This can be used to get rid of clones which are too short.
	 * @param consumer
	 *            the class to which the result are reported.
	 */
	public void findClones(int minLength, ICloneReporter consumer)
			throws ConQATException {
		if (minLength <= 0) {
			throw new IllegalArgumentException("minLength must be positive.");
		}
		this.minLength = minLength;
		this.consumer = consumer;

		findClones(0, 0, 0);
	}

	/**
	 * Perform the DFS for finding the clones
	 * 
	 * @param node
	 *            the current node to search at.
	 * @param currentLength
	 *            the current length of the word spelled out starting from the
	 *            root node.
	 * @param leafPosStart
	 *            the first position of the {@link #leafPositions} array which
	 *            may be written.
	 * @return the first position not occupied in the {@link #leafPositions}
	 *         array (it is leafPosEnd).
	 */
	int findClones(int node, int currentLength, int leafPosStart)
			throws ConQATException {
		// is a leaf node?
		if (nodeChildFirst[node] < 0) {
			leafPositions[leafPosStart] = INFTY - currentLength;
			return leafPosStart + 1;
		}

		int leafPosEnd = leafPosStart;
		for (int e = nodeChildFirst[node]; e >= 0; e = nodeChildNext[e]) {
			int next = nodeChildNode[e];
			int len = nodeWordEnd[next] - nodeWordBegin[next];
			leafPosEnd = findClones(next, currentLength + len, leafPosEnd);
		}

		// report clones ?
		if (currentLength >= minLength
				&& leafPosEnd - leafPosStart > inducedClones[node]) {
			consumer.startCloneClass(currentLength);
			for (int i = leafPosStart; i < leafPosEnd; ++i) {
				consumer.addClone(leafPositions[i], currentLength);
			}
			consumer.completeCloneClass();
		}

		return leafPosEnd;
	}

}