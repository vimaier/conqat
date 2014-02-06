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
package org.conqat.engine.code_clones.normalization.repetition;

import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * Repetition of a motif in a sequence of elements. Repetitions are immutable.
 * <p>
 * For example, the sequence of characters "xxx12312313xxx" contains the
 * repetition "123123123" at index 3 with motif "123".
 * <p>
 * 
 * @param <T>
 *            Type of the element contained in the sequence that contains the
 *            repetition. In the above example, this type would be integer.
 * 
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 18242140689A26C49DADE4AB3AD9A649
 */
public class Repetition<T> {

	/** Sequence that contains the repetition */
	private final T[] input;

	/** Index of first repetition element in input sequence */
	private final int startIndex;

	/** Index of last repetition element in input sequence */
	private final int endIndex;

	/** Length of motif */
	private final int motifLength;

	/**
	 * Default constructor
	 * 
	 * @param input
	 *            Sequence that contains the repetition
	 * @param startIndex
	 *            Index of first repetition element in input sequence
	 * @param endIndex
	 *            Index of last repetition element in input sequence
	 * @param motifLength
	 *            Length of motif
	 */
	/* package */Repetition(T[] input, int startIndex, int endIndex,
			int motifLength) {
		checkBounds(input, startIndex, endIndex, motifLength);

		this.input = input;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.motifLength = motifLength;
	}

	/** Performs sanity checks on the parameter bounds */
	private void checkBounds(T[] input, int startIndex, int endIndex,
			int motifLength) {
		// validate start index inside input
		CCSMPre.isFalse(startIndex < 0, "startIndex < 0!");
		CCSMPre.isFalse(startIndex >= input.length,
				"startIndex >= input.length!");

		// validate end index inside input
		CCSMPre.isFalse(endIndex < 0, "endIndex < 0");
		CCSMPre.isFalse(endIndex >= input.length, "endIndex >= input.length!");

		// validate start index not bigger than end index
		CCSMPre.isFalse(startIndex > endIndex, "startIndex > endIndex!");

		// validate motifLength > 0
		CCSMPre.isFalse(motifLength <= 0, "motifLenght <= 0!");
	}

	/** Get first element of repetition */
	public T getStart() {
		return input[startIndex];
	}

	/** Get last element of repetition */
	public T getEnd() {
		return input[endIndex];
	}

	/**
	 * Gets the repetition element at the corresponding position in the
	 * repetition.
	 * 
	 * @param offset
	 *            Index in repetition, relative to start of repetition
	 */
	public T getElement(int offset) {
		CCSMPre.isFalse(offset < 0, "offset < 0!");
		CCSMPre.isFalse(startIndex + offset >= input.length,
				"Attempting to access element outside of input array!");

		return input[startIndex + offset];
	}

	/** Returns length of motif */
	public int getMotifLength() {
		return motifLength;
	}

	/** Returns index of last repetition element in input sequence */
	public int getEndIndex() {
		return endIndex;
	}

	/** Returns index of first repetition element in input sequence */
	public int getStartIndex() {
		return startIndex;
	}

	/** Gets the length of the repetition in elements */
	public int getLength() {
		return getEndIndex() - getStartIndex() + 1;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Repetition [" + startIndex + ":" + endIndex + "]";
	}

}