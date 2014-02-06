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

/**
 * Efficient linear time constructible suffix tree using Ukkonen's online
 * construction algorithm (E. Ukkonen: "On-line construction of suffix trees").
 * Most of the comments reference this paper and it might be hard to follow
 * without knowing at least the basics of it.
 * <p>
 * We use some conventions which are slightly different from the paper however:
 * <ul>
 * <li>The names of the variables are different, but we give a translation into
 * Ukkonen's names.</li>
 * <li>Many variables are made "global" by realizing them as fields. This way we
 * can easily deal with those tuple return values without constructing extra
 * classes.</li>
 * <li>String indices start at 0 (not at 1).</li>
 * <li>Substrings are marked by the first index and the index after the last one
 * (just as in C++ STL) instead of the first and the last index (i.e. intervals
 * are right-open instead of closed). This makes it more intuitive to express
 * the empty string (i.e. (i,i) instead of (i,i-1)).</li>
 * </ul>
 * <p>
 * Everything but the construction itself is protected to simplify increasing
 * its functionality by subclassing but without introducing new method calls.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 4B2EF0606B3085A6831764ED042FF20D
 */
public class SuffixTree {

	/** Infinity in this context. */
	protected final int INFTY;

	/** The word we are working on. */
	protected final List<?> word;

	/** The number of nodes created so far. */
	protected int numNodes = 0;

	/**
	 * For each node this holds the index of the first character of
	 * {@link #word} labeling the transition <b>to</b> this node. This
	 * corresponds to the <em>k</em> for a transition used in Ukkonen's paper.
	 */
	protected final int[] nodeWordBegin;

	/**
	 * For each node this holds the index of the one after the last character of
	 * {@link #word} labeling the transition <b>to</b> this node. This
	 * corresponds to the <em>p</em> for a transition used in Ukkonen's paper.
	 */
	protected final int[] nodeWordEnd;

	/** For each node its suffix link (called function <em>f</em> by Ukkonen). */
	protected final int[] suffixLink;

	/**
	 * The next node function realized as a hash table. This corresponds to the
	 * <em>g</em> function used in Ukkonen's paper.
	 */
	protected final SuffixTreeHashTable nextNode;

	/**
	 * An array giving for each node the index where the first child will be
	 * stored (or -1 if it has no children). It is initially empty and will be
	 * filled "on demand" using
	 * {@link org.conqat.engine.code_clones.detection.suffixtree.SuffixTreeHashTable#extractChildLists(int[], int[], int[])}
	 * .
	 */
	protected int[] nodeChildFirst;

	/**
	 * This array gives the next index of the child list or -1 if this is the
	 * last one. It is initially empty and will be filled "on demand" using
	 * {@link org.conqat.engine.code_clones.detection.suffixtree.SuffixTreeHashTable#extractChildLists(int[], int[], int[])}
	 * .
	 */
	protected int[] nodeChildNext;

	/**
	 * This array stores the actual name (=number) of the mode in the child
	 * list. It is initially empty and will be filled "on demand" using
	 * {@link org.conqat.engine.code_clones.detection.suffixtree.SuffixTreeHashTable#extractChildLists(int[], int[], int[])}
	 * .
	 */
	protected int[] nodeChildNode;

	/**
	 * The node we are currently at as a "global" variable (as it is always
	 * passed unchanged). This is called <i>s</i> in Ukkonen's paper.
	 */
	private int currentNode = 0;

	/**
	 * Beginning of the word part of the reference pair. This is kept "global"
	 * (in constrast to the end) as this is passed unchanged to all functions.
	 * Ukkonen calls this <em>k</em>.
	 */
	private int refWordBegin = 0;

	/**
	 * This is the new (or old) explicit state as returned by
	 * {@link #testAndSplit(int, Object)}. Ukkonen calls this <em>r</em>.
	 */
	private int explicitNode;

	/**
	 * Create a new suffix tree from a given word. The word given as parameter
	 * is used internally and should not be modified anymore, so copy it before
	 * if required.
	 */
	public SuffixTree(List<?> word) {
		this.word = word;
		int size = word.size();
		INFTY = size;

		int expectedNodes = 2 * size;
		nodeWordBegin = new int[expectedNodes];
		nodeWordEnd = new int[expectedNodes];
		suffixLink = new int[expectedNodes];
		nextNode = new SuffixTreeHashTable(expectedNodes);

		createRootNode();

		for (int i = 0; i < size; ++i) {
			update(i);
			canonize(i + 1);
		}
	}

	/** Creates the root node. */
	private void createRootNode() {
		numNodes = 1;
		nodeWordBegin[0] = 0;
		nodeWordEnd[0] = 0;
		suffixLink[0] = -1;
	}

	/**
	 * The <em>update</em> function as defined in Ukkonen's paper. This inserts
	 * the character at charPos into the tree. It works on the canonical
	 * reference pair ({@link #currentNode}, ({@link #refWordBegin}, charPos)).
	 */
	private void update(int charPos) {
		int lastNode = 0;
		while (!testAndSplit(charPos, word.get(charPos))) {
			int newNode = numNodes++;
			nodeWordBegin[newNode] = charPos;
			nodeWordEnd[newNode] = INFTY;
			nextNode.put(explicitNode, word.get(charPos), newNode);

			if (lastNode != 0) {
				suffixLink[lastNode] = explicitNode;
			}
			lastNode = explicitNode;
			currentNode = suffixLink[currentNode];
			canonize(charPos);
		}
		if (lastNode != 0) {
			suffixLink[lastNode] = currentNode;
		}
	}

	/**
	 * The <em>test-and-split</em> function as defined in Ukkonen's paper. This
	 * checks whether the state given by the canonical reference pair (
	 * {@link #currentNode}, ({@link #refWordBegin}, refWordEnd)) is the end
	 * point (by checking whether a transition for the
	 * <code>nextCharacter</code> exists). Additionally the state is made
	 * explicit if it not already is and this is not the end-point. It returns
	 * true if the end-point was reached. The newly created (or reached)
	 * explicit node is returned in the "global" variable.
	 */
	private boolean testAndSplit(int refWordEnd, Object nextCharacter) {
		if (currentNode < 0) {
			// trap state is always end state
			return true;
		}

		if (refWordEnd <= refWordBegin) {
			if (nextNode.get(currentNode, nextCharacter) < 0) {
				explicitNode = currentNode;
				return false;
			}
			return true;
		}

		int next = nextNode.get(currentNode, word.get(refWordBegin));
		if (nextCharacter.equals(word.get(nodeWordBegin[next] + refWordEnd
				- refWordBegin))) {
			return true;
		}

		// not an end-point and not explicit, so make it explicit.
		explicitNode = numNodes++;
		nodeWordBegin[explicitNode] = nodeWordBegin[next];
		nodeWordEnd[explicitNode] = nodeWordBegin[next] + refWordEnd
				- refWordBegin;
		nextNode.put(currentNode, word.get(refWordBegin), explicitNode);

		nodeWordBegin[next] += refWordEnd - refWordBegin;
		nextNode.put(explicitNode, word.get(nodeWordBegin[next]), next);
		return false;
	}

	/**
	 * The <em>canonize</em> function as defined in Ukkonen's paper. Changes the
	 * reference pair (currentNode, (refWordBegin, refWordEnd)) into a canonical
	 * reference pair. It works on the "global" variables {@link #currentNode}
	 * and {@link #refWordBegin} and the parameter, writing the result back to
	 * the globals.
	 * 
	 * @param refWordEnd
	 *            one after the end index for the word of the reference pair.
	 */
	private void canonize(int refWordEnd) {
		if (currentNode == -1) {
			// explicitly handle trap state
			currentNode = 0;
			++refWordBegin;
		}

		if (refWordEnd <= refWordBegin) {
			// empty word, so already canonical
			return;
		}

		int next = nextNode.get(currentNode, word.get(refWordBegin));
		while (nodeWordEnd[next] - nodeWordBegin[next] <= refWordEnd
				- refWordBegin) {
			refWordBegin += nodeWordEnd[next] - nodeWordBegin[next];
			currentNode = next;
			if (refWordEnd > refWordBegin) {
				next = nextNode.get(currentNode, word.get(refWordBegin));
			} else {
				break;
			}
		}
	}

	/**
	 * This method makes sure the child lists are filled (required for
	 * traversing the tree).
	 */
	protected void ensureChildLists() {
		if (nodeChildFirst == null || nodeChildFirst.length < numNodes) {
			nodeChildFirst = new int[numNodes];
			nodeChildNext = new int[numNodes];
			nodeChildNode = new int[numNodes];
			nextNode.extractChildLists(nodeChildFirst, nodeChildNext,
					nodeChildNode);
		}
	}

	/** Prints some internal numbers to std error. */
	protected void _dumpDebugInfos() {
		System.err.println("Number of nodes created: " + numNodes);
		System.err.println("Hash table infos: ");
		nextNode._printDebugInfo();
	}

	/**
	 * Returns a GraphViz DOT string describing this suffix tree.
	 * 
	 * @param separationChar
	 *            the character used for separating the charaters along a child
	 *            link (for "normal" characters this might be an empty string,
	 *            for integers it might be a comma).
	 * @param includeSuffixLinks
	 *            if true, suffix links will be included in the output.
	 */
	protected String _dumpAsDOT(String separationChar,
			boolean includeSuffixLinks) {
		ensureChildLists();

		StringBuilder sb = new StringBuilder();
		sb.append("digraph G {\n");
		for (int i = 0; i < numNodes; ++i) {
			sb.append("  n" + i + ";\n");
		}
		for (int i = 0; i < numNodes; ++i) {
			for (int e = nodeChildFirst[i]; e >= 0; e = nodeChildNext[e]) {
				sb.append("  n" + i + " -> n" + nodeChildNode[e] + " [label=\"");
				String sep = "";
				for (int j = nodeWordBegin[nodeChildNode[e]]; j < nodeWordEnd[nodeChildNode[e]]; ++j) {
					sb.append(sep);
					sb.append(word.get(j).toString());
					sep = separationChar;
				}
				sb.append("\"];\n");
			}
		}
		if (includeSuffixLinks) {
			for (int i = 1; i < numNodes; ++i) {
				if (nodeChildFirst[i] >= 0) {
					sb.append("  n" + i + " -> n" + suffixLink[i]
							+ " [color=red,constraint=false];\n");
				}
			}
		}
		sb.append("}\n");
		return sb.toString();
	}

	/**
	 * Returns whether the given word is contained in the string given at
	 * construction time.
	 */
	public boolean containsWord(List<?> find) {
		int node = 0;
		int findSize = find.size();
		for (int i = 0; i < findSize;) {
			int next = nextNode.get(node, find.get(i));
			if (next < 0) {
				return false;
			}
			for (int j = nodeWordBegin[next]; j < nodeWordEnd[next]
					&& i < findSize; ++i, ++j) {
				if (!word.get(j).equals(find.get(i))) {
					return false;
				}
			}
			node = next;
		}
		return true;
	}

	/**
	 * A sentinel character which can be used to produce explicit leaves for all
	 * suffixes. The sentinel just has to be appended to the list before handing
	 * it to the suffix tree. For the sentinel equality and object identity are
	 * the same!
	 */
	public static class Sentinel {

		/** The hash value used. */
		private final int hash = (int) (Math.random() * Integer.MAX_VALUE);

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			return hash;
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(Object obj) {
			return obj == this;
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "$";
		}
	}
}