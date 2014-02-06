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

/**
 * The hash table used for the {@link SuffixTree} class. It is specifically
 * written and optimized for its implementation and is thus probably of little
 * use for any other application.
 * <p>
 * It hashes from (node, character) pairs to the next node, where nodes are
 * represented by integers and the type of characters is determined by the
 * generic parameter.
 * 
 * @author Benjamin Hummel
 * @author $Author: juergens $
 * 
 * @version $Revision: 34670 $
 * @ConQAT.Rating GREEN Hash: 6A7A830078AF0CA9C2D84C148F336DF4
 */
/* package */class SuffixTreeHashTable {

	/**
	 * These numbers were taken from
	 * http://planetmath.org/encyclopedia/GoodHashTablePrimes.html
	 */
	private static final int[] allowedSizes = { 53, 97, 193, 389, 769, 1543,
			3079, 6151, 12289, 24593, 49157, 98317, 196613, 393241, 786433,
			1572869, 3145739, 6291469, 12582917, 25165843, 50331653, 100663319,
			201326611, 402653189, 805306457, 1610612741 };

	/** The size of the hash table. */
	private final int tableSize;

	/** Storage space for the node part of the key */
	private final int[] keyNodes;

	/** Storage space for the character part of the key. */
	private final Object[] keyChars;

	/** Storage space for the result node. */
	private final int[] resultNodes;

	/** Debug info: number of stored nodes. */
	private int _numStoredNodes = 0;

	/** Debug info: number of calls to find so far. */
	private int _numFind = 0;

	/** Debug info: number of collisions (i.e. wrong finds) during find so far. */
	private int _numColl = 0;

	/**
	 * Creates a new hash table for the given number of nodes. Trying to add
	 * more nodes will result in worse performance down to entering an infinite
	 * loop on some operations.
	 */
	public SuffixTreeHashTable(final int numNodes) {
		int minSize = (int) Math.ceil(1.5 * numNodes);
		int sizeIndex = 0;
		while (allowedSizes[sizeIndex] < minSize) {
			++sizeIndex;
		}
		tableSize = allowedSizes[sizeIndex];

		keyNodes = new int[tableSize];
		keyChars = new Object[tableSize];
		resultNodes = new int[tableSize];
	}

	/**
	 * Returns the position of the (node,char) key in the hash map or the
	 * position to insert it into if it is not yet in.
	 */
	private int hashFind(int keyNode, Object keyChar) {
		++_numFind;
		int hash = keyChar.hashCode();
		int pos = posMod(primaryHash(keyNode, hash));
		int secondary = secondaryHash(keyNode, hash);
		while (keyChars[pos] != null) {
			if (keyNodes[pos] == keyNode && keyChar.equals(keyChars[pos])) {
				break;
			}
			++_numColl;
			pos = (pos + secondary) % tableSize;
		}
		return pos;
	}

	/**
	 * Returns the next node for the given (node, character) key pair or a
	 * negative value if no next node is stored for this key.
	 */
	public int get(int keyNode, Object keyChar) {
		int pos = hashFind(keyNode, keyChar);
		if (keyChars[pos] == null) {
			return -1;
		}
		return resultNodes[pos];
	}

	/** Inserts the given result node for the (node, character) key pair. */
	public void put(int keyNode, Object keyChar, int resultNode) {
		int pos = hashFind(keyNode, keyChar);
		if (keyChars[pos] == null) {
			++_numStoredNodes;
			keyChars[pos] = keyChar;
			keyNodes[pos] = keyNode;
		}
		resultNodes[pos] = resultNode;
	}

	/** Returns the primary hash value for a (node, character) key pair. */
	private int primaryHash(int keyNode, int keyCharHash) {
		return keyCharHash ^ (13 * keyNode);
	}

	/** Returns the secondary hash value for a (node, character) key pair. */
	private int secondaryHash(int keyNode, int keyCharHash) {
		int result = posMod((keyCharHash ^ (1025 * keyNode)));
		if (result == 0) {
			return 2;
		}
		return result;
	}

	/**
	 * Returns the smallest non-negative number congruent to x modulo
	 * {@link #tableSize}.
	 */
	private int posMod(int x) {
		x %= tableSize;
		if (x < 0) {
			x += tableSize;
		}
		return x;
	}

	/**
	 * Extracts the list of child nodes for each node from the hash table
	 * entries as a linked list. All arrays are expected to be initially empty
	 * and of suitable size (i.e. for <em>n</em> nodes it should have size
	 * <em>n</em> given that nodes are numbered 0 to n-1). Those arrays will be
	 * filled from this method.
	 * <p>
	 * The method is package visible, as it is tighly coupled to the
	 * {@link SuffixTree} class.
	 * 
	 * @param nodeFirstIndex
	 *            an array giving for each node the index where the first child
	 *            will be stored (or -1 if it has no children).
	 * @param nodeNextIndex
	 *            this array gives the next index of the child list or -1 if
	 *            this is the last one.
	 * @param nodeChild
	 *            this array stores the actual name (=number) of the mode in the
	 *            child list.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if any of the given arrays was too small.
	 */
	public void extractChildLists(int[] nodeFirstIndex, int[] nodeNextIndex,
			int[] nodeChild) {
		Arrays.fill(nodeFirstIndex, -1);
		int free = 0;
		for (int i = 0; i < tableSize; ++i) {
			if (keyChars[i] != null) {
				// insert keyNodes[i] -> resultNodes[i]
				nodeChild[free] = resultNodes[i];
				nodeNextIndex[free] = nodeFirstIndex[keyNodes[i]];
				nodeFirstIndex[keyNodes[i]] = free++;
			}
		}
	}

	/**
	 * Prints some internal statistics, such as fill factor and collisions to
	 * std err.
	 */
	public void _printDebugInfo() {
		System.err.println("STHashMap info: ");
		System.err.println("  Table size: " + tableSize);
		System.err.println("  Contained entries: " + _numStoredNodes);
		System.err.println("  Fill factor: "
				+ ((double) _numStoredNodes / tableSize));
		System.err.println("  Number of finds: " + _numFind);
		System.err.println("  Number of collisions: " + _numColl);
	}
}