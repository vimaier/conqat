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
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.PairList;

/**
 * An extension of the suffix tree adding an algorithm for finding approximate
 * clones, i.e. substrings which are similar.
 * 
 * @author $Author: hummelb $
 * @version $Revision: 43151 $
 * @ConQAT.Rating GREEN Hash: BB94CD690760BC239F04D32D5BCAC33E
 */
public abstract class ApproximateCloneDetectingSuffixTree extends SuffixTree {

	/** The number of leaves reachable from the given node (1 for leaves). */
	private final int[] leafCount;

	/** This is the distance between two entries in the {@link #cloneInfos} map. */
	private static final int INDEX_SPREAD = 10;

	/** This map stores for each position the relevant clone infos. */
	private final ListMap<Integer, CloneInfo> cloneInfos = new ListMap<Integer, CloneInfo>();

	/**
	 * The maximal length of a clone. This influences the size of the
	 * (quadratic) {@link #edBuffer}.
	 */
	private static final int MAX_LENGTH = 1024;

	/** Buffer used for calculating edit distance. */
	private final int[][] edBuffer = new int[MAX_LENGTH][MAX_LENGTH];

	/** The consumer to which detected clones are reported */
	private ICloneReporter consumer;

	/** The minimal length of clones to return. */
	protected int minLength;

	/** Number of units that must be equal at the start of a clone */
	private int headEquality;

	/**
	 * Create a new suffix tree from a given word. The word given as parameter
	 * is used internally and should not be modified anymore, so copy it before
	 * if required.
	 * <p>
	 * This only word correctly if the given word is closed using a sentinel
	 * character.
	 */
	public ApproximateCloneDetectingSuffixTree(List<?> word) {
		super(word);
		ensureChildLists();
		leafCount = new int[numNodes];
		initLeafCount(0);
	}

	/**
	 * Initializes the {@link #leafCount} array which given for each node the
	 * number of leaves reachable from it (where leaves obtain a value of 1).
	 */
	private void initLeafCount(int node) {
		leafCount[node] = 0;
		for (int e = nodeChildFirst[node]; e >= 0; e = nodeChildNext[e]) {
			initLeafCount(nodeChildNode[e]);
			leafCount[node] += leafCount[nodeChildNode[e]];
		}
		if (leafCount[node] == 0) {
			leafCount[node] = 1;
		}
	}

	/**
	 * Finds all clones in the string (List) used in the constructor.
	 * 
	 * @param minLength
	 *            the minimal length of a clone
	 * @param maxErrors
	 *            the maximal number of errors/gaps allowed
	 * @param headEquality
	 *            the number of elements which have to be the same at the
	 *            beginning of a clone
	 * @param consumer
	 *            the consumer used for writing out clones
	 */
	public void findClones(int minLength, int maxErrors, int headEquality,
			ICloneReporter consumer) throws ConQATException {
		this.minLength = minLength;
		this.consumer = consumer;
		this.headEquality = headEquality;
		cloneInfos.clear();

		for (int i = 0; i < word.size(); ++i) {
			// Do quick start, as first character has to match anyway.
			int node = nextNode.get(0, word.get(i));
			if (node < 0 || leafCount[node] <= 1) {
				continue;
			}

			// we know that we have an exact match of at least 'length'
			// characters, as the word itself is part of the suffix tree.
			int length = nodeWordEnd[node] - nodeWordBegin[node];
			int numReported = 0;
			for (int e = nodeChildFirst[node]; e >= 0; e = nodeChildNext[e]) {
				if (matchWord(i, i + length, nodeChildNode[e], length,
						maxErrors)) {
					++numReported;
				}
			}
			if (length >= minLength && numReported != 1) {
				reportClone(i, i + length, node, length, length);
			}
		}
	}

	/**
	 * Performs the approximative matching between the input word and the tree.
	 * 
	 * @param wordStart
	 *            the start position of the currently matched word (position in
	 *            the input word).
	 * @param wordPosition
	 *            the current position along the input word.
	 * @param node
	 *            the node we are currently at (i.e. the edge leading to this
	 *            node is relevant to us).
	 * @param nodeWordLength
	 *            the length of the word found along the nodes (this may be
	 *            different from the length along the input word due to gaps).
	 * @param maxErrors
	 *            the number of errors still allowed.
	 * @return whether some clone was reported
	 */
	private boolean matchWord(int wordStart, int wordPosition, int node,
			int nodeWordLength, int maxErrors) throws ConQATException {

		// We are aware that this method is longer than desirable for code
		// reading. However, we currently do not see a refactoring that has a
		// sensible cost-benefit ratio. Suggestions are welcome!

		// self match?
		if (leafCount[node] == 1 && nodeWordBegin[node] == wordPosition) {
			return false;
		}

		int currentNodeWordLength = Math.min(nodeWordEnd[node]
				- nodeWordBegin[node], MAX_LENGTH - 1);
		// do min edit distance
		int currentLength = calculateMaxLength(wordStart, wordPosition, node,
				maxErrors, currentNodeWordLength);

		if (currentLength == 0) {
			return false;
		}

		if (currentLength >= MAX_LENGTH - 1) {
			reportBufferShortage(nodeWordBegin[node], currentNodeWordLength);
		}

		// calculate cheapest match
		int best = maxErrors + 42;
		int iBest = 0;
		int jBest = 0;
		for (int k = 0; k <= currentLength; ++k) {
			int i = currentLength - k;
			int j = currentLength;
			if (edBuffer[i][j] < best) {
				best = edBuffer[i][j];
				iBest = i;
				jBest = j;
			}

			i = currentLength;
			j = currentLength - k;
			if (edBuffer[i][j] < best) {
				best = edBuffer[i][j];
				iBest = i;
				jBest = j;
			}
		}

		while (wordPosition + iBest < word.size()
				&& jBest < currentNodeWordLength
				&& word.get(wordPosition + iBest) != word
						.get(nodeWordBegin[node] + jBest)
				&& word.get(wordPosition + iBest).equals(
						word.get(nodeWordBegin[node] + jBest))) {
			++iBest;
			++jBest;
		}

		int numReported = 0;
		if (currentLength == currentNodeWordLength) {
			// we may proceed
			for (int e = nodeChildFirst[node]; e >= 0; e = nodeChildNext[e]) {
				if (matchWord(wordStart, wordPosition + iBest,
						nodeChildNode[e], nodeWordLength + jBest, maxErrors
								- best)) {
					++numReported;
				}
			}
		}

		// do not report locally if had reports in exactly one subtree (would be
		// pure subclone)
		if (numReported == 1) {
			return true;
		}

		// disallow tail changes
		while (iBest > 0
				&& jBest > 0
				&& !word.get(wordPosition + iBest - 1).equals(
						word.get(nodeWordBegin[node] + jBest - 1))) {

			if (iBest > 1
					&& word.get(wordPosition + iBest - 2).equals(
							word.get(nodeWordBegin[node] + jBest - 1))) {
				--iBest;
			} else if (jBest > 1
					&& word.get(wordPosition + iBest - 1).equals(
							word.get(nodeWordBegin[node] + jBest - 2))) {
				--jBest;
			} else {
				--iBest;
				--jBest;
			}
		}

		// report if real clone
		if (iBest > 0 && jBest > 0) {
			numReported += 1;
			reportClone(wordStart, wordPosition + iBest, node, jBest,
					nodeWordLength + jBest);
		}

		return numReported > 0;
	}

	/**
	 * Calculates the maximum length we may take along the word to the current
	 * node (respecting the number of errors to make). *
	 * 
	 * @param wordStart
	 *            the start position of the currently matched word (position in
	 *            the input word).
	 * @param wordPosition
	 *            the current position along the input word.
	 * @param node
	 *            the node we are currently at (i.e. the edge leading to this
	 *            node is relevant to us).
	 * @param maxErrors
	 *            the number of errors still allowed.
	 * @param currentNodeWordLength
	 *            the length of the word found along the nodes (this may be
	 *            different from the actual length due to buffer limits).
	 * @return the maximal length that can be taken.
	 */
	private int calculateMaxLength(int wordStart, int wordPosition, int node,
			int maxErrors, int currentNodeWordLength) {
		edBuffer[0][0] = 0;
		int currentLength = 1;
		for (; currentLength <= currentNodeWordLength; ++currentLength) {
			int best = currentLength;
			edBuffer[0][currentLength] = currentLength;
			edBuffer[currentLength][0] = currentLength;

			if (wordPosition + currentLength >= word.size()) {
				break;
			}

			// deal with case that character may not be matched (sentinel!)
			Object iChar = word.get(wordPosition + currentLength - 1);
			Object jChar = word.get(nodeWordBegin[node] + currentLength - 1);
			if (mayNotMatch(iChar) || mayNotMatch(jChar)) {
				break;
			}

			// usual matrix completion for edit distance
			for (int k = 1; k < currentLength; ++k) {
				best = Math.min(
						best,
						fillEDBuffer(k, currentLength, wordPosition,
								nodeWordBegin[node]));
			}
			for (int k = 1; k < currentLength; ++k) {
				best = Math.min(
						best,
						fillEDBuffer(currentLength, k, wordPosition,
								nodeWordBegin[node]));
			}
			best = Math.min(
					best,
					fillEDBuffer(currentLength, currentLength, wordPosition,
							nodeWordBegin[node]));

			if (best > maxErrors
					|| wordPosition - wordStart + currentLength <= headEquality
					&& best > 0) {
				break;
			}
		}
		--currentLength;
		return currentLength;
	}

	/**
	 * Fills the edit distance buffer at position (i,j).
	 * 
	 * @param i
	 *            the first index of the buffer.
	 * @param j
	 *            the second index of the buffer.
	 * @param iOffset
	 *            the offset where the word described by i starts.
	 * @param jOffset
	 *            the offset where the word described by j starts.
	 * @return the value inserted into the buffer.
	 */
	private int fillEDBuffer(int i, int j, int iOffset, int jOffset) {
		Object iChar = word.get(iOffset + i - 1);
		Object jChar = word.get(jOffset + j - 1);

		int insertDelete = 1 + Math.min(edBuffer[i - 1][j], edBuffer[i][j - 1]);
		int change = edBuffer[i - 1][j - 1] + (iChar.equals(jChar) ? 0 : 1);
		return edBuffer[i][j] = Math.min(insertDelete, change);
	}

	/**
	 * Reports a clone to the {@link #consumer}.
	 * 
	 * @param wordBegin
	 *            the start position on the input word.
	 * @param wordEnd
	 *            the end position on the input word (exclusive).
	 * @param currentNode
	 *            the node we are currently at when reporting this clone.
	 * @param nodeWordPos
	 *            the current position on the subword leading to the node.
	 * @param nodeWordLength
	 *            the length of the word found along the nodes.
	 */
	private void reportClone(int wordBegin, int wordEnd, int currentNode,
			int nodeWordPos, int nodeWordLength) throws ConQATException {
		int length = wordEnd - wordBegin;
		if (length < minLength || nodeWordLength < minLength) {
			return;
		}

		PairList<Integer, Integer> otherClones = new PairList<Integer, Integer>();
		findRemainingClones(otherClones, nodeWordLength, currentNode,
				nodeWordEnd[currentNode] - nodeWordBegin[currentNode]
						- nodeWordPos, wordBegin);

		int occurrences = 1 + otherClones.size();

		// check whether we may start from here
		CloneInfo newInfo = new CloneInfo(length, occurrences);
		for (int index = Math.max(0, wordBegin - INDEX_SPREAD + 1); index <= wordBegin; ++index) {
			List<CloneInfo> existingClones = cloneInfos.getCollection(index);
			if (existingClones != null) {
				for (CloneInfo cloneInfo : existingClones) {
					if (cloneInfo.dominates(newInfo, wordBegin - index)) {
						// we already have a dominating clone, so ignore
						return;
					}
				}
			}
		}

		// report clone
		consumer.startCloneClass(length);
		consumer.addClone(wordBegin, length);
		for (int clone = 0; clone < otherClones.size(); ++clone) {
			int start = otherClones.getFirst(clone);
			int otherLength = otherClones.getSecond(clone);
			consumer.addClone(start, otherLength);
		}

		// is this clone actually relevant?
		if (!consumer.completeCloneClass()) {
			return;
		}

		// add clone to otherClones to avoid getting more duplicates
		for (int i = wordBegin; i < wordEnd; i += INDEX_SPREAD) {
			cloneInfos.add(i, new CloneInfo(length - (i - wordBegin),
					occurrences));
		}
		for (int clone = 0; clone < otherClones.size(); ++clone) {
			int start = otherClones.getFirst(clone);
			int otherLength = otherClones.getSecond(clone);
			for (int i = 0; i < otherLength; i += INDEX_SPREAD) {
				cloneInfos.add(start + i, new CloneInfo(otherLength - i,
						occurrences));
			}
		}
	}

	/**
	 * Fills a list of pairs giving the start positions and lengths of the
	 * remaining clones.
	 * 
	 * @param clonePositions
	 *            the clone positions being filled (start position and length)
	 * @param nodeWordLength
	 *            the length of the word along the nodes.
	 * @param currentNode
	 *            the node we are currently at.
	 * @param distance
	 *            the distance along the word leading to the current node.
	 * @param wordStart
	 *            the start of the currently searched word.
	 */
	private void findRemainingClones(PairList<Integer, Integer> clonePositions,
			int nodeWordLength, int currentNode, int distance, int wordStart) {
		for (int nextNode = nodeChildFirst[currentNode]; nextNode >= 0; nextNode = nodeChildNext[nextNode]) {
			int node = nodeChildNode[nextNode];
			findRemainingClones(clonePositions, nodeWordLength, node, distance
					+ nodeWordEnd[node] - nodeWordBegin[node], wordStart);
		}

		if (nodeChildFirst[currentNode] < 0) {
			int start = word.size() - distance - nodeWordLength;
			if (start != wordStart) {
				clonePositions.add(start, nodeWordLength);
			}
		}
	}

	/**
	 * This should return true, if the provided character is not allowed to
	 * match with anything else (e.g. is a sentinel).
	 */
	protected abstract boolean mayNotMatch(Object character);

	/**
	 * This method is called whenever the {@link #MAX_LENGTH} is to small and
	 * hence the {@link #edBuffer} was not large enough. This may cause that a
	 * really large clone is reported in multiple chunks of size
	 * {@link #MAX_LENGTH} and potentially minor parts of such a clone might be
	 * lost.
	 */
	@SuppressWarnings("unused")
	protected void reportBufferShortage(int leafStart, int leafLength) {
		// empty base implementation
	}

	/** Stores information on a clone. */
	private static class CloneInfo {

		/** Length of the clone. */
		private final int length;

		/** Number of occurrences of the clone. */
		private final int occurrences;

		/** Constructor. */
		public CloneInfo(int length, int occurrences) {
			this.length = length;
			this.occurrences = occurrences;
		}

		/**
		 * Returns whether this clone info dominates the given one, i.e. whether
		 * both {@link #length} and {@link #occurrences} s not smaller.
		 * 
		 * @param later
		 *            The amount the given clone starts later than the "this"
		 *            clone.
		 */
		public boolean dominates(CloneInfo ci, int later) {
			return length - later >= ci.length && occurrences >= ci.occurrences;
		}
	}
}