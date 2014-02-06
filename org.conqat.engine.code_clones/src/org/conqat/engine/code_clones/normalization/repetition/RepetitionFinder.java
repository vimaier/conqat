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

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.equals.IEquator;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.region.RegionSet;

/**
 * Detects repetitions of fixed length motifs in an input array.
 * <p>
 * Equality of elements of the input array is determined by an {@link IEquator}.
 * This externalisation of equality has been chosen since for repetition
 * detection a different notion of equality might apply than for object
 * comparison.
 * 
 * @author Elmar Juergens
 * @author $Author: heinemann $
 * 
 * @version $Revision: 46755 $
 * @ConQAT.Rating GREEN Hash: 9D107C6ABF53DD007DA8309F627F0269
 */
public class RepetitionFinder<T> {

	/** Array that is searched for repetitions */
	private final T[] input;

	/** Comparator used to determine whether two input elements are equal. */
	private final IEquator<? super T> comparator;

	/** Repetition must have at least this length in order not to be discarded. */
	private final int minLength;

	/**
	 * Repetition must have at least this number of motive instances in order
	 * not to be discarded.
	 */
	private final int minMotifInstances;

	/**
	 * Creates a repetition detector that works on an array of input elements.
	 * 
	 * @param input
	 *            Array that is searched for repetitions
	 * @param comparator
	 *            Comparator that determines whether two input elements are
	 *            equal
	 * @param minLength
	 *            Minimal length of repetition
	 * @param minMotifInstances
	 *            Minimal number of motive instances
	 */
	public RepetitionFinder(T[] input, IEquator<? super T> comparator,
			int minLength, int minMotifInstances) {
		CCSMPre.isFalse(minLength < 1, "minLength < 1!");
		CCSMPre.isFalse(minMotifInstances < 2, "minMotifInstances < 2!");

		this.input = input;
		this.comparator = comparator;
		this.minLength = minLength;
		this.minMotifInstances = minMotifInstances;
	}

	/**
	 * Performs detection of repetitions of length between minMotifLength and
	 * maxMotifLength
	 * <p>
	 * It avoids multiple detection of the same regions for different motif
	 * lengths. I.e., in the string "x111111x", a repetition of motif length 1
	 * is detected, repetitions of larger motifs are not reported. (Although the
	 * string "1111" can be interpreted as 4 times "1", or two times "11", ...)
	 * 
	 * @return List of detected repetitions
	 */
	public List<Repetition<T>> findRepetitions(int minMotifLength,
			int maxMotifLength) {

		CCSMAssert.isTrue(maxMotifLength >= minMotifLength,
				"Max motif length must be at least as big as min motif length");

		List<Repetition<T>> result = new ArrayList<Repetition<T>>();

		// we loop over all motif lengths, even if they are too short. else
		// repetitions of too short motives are mistakenly identified as longer
		// motifs. we use a regionset that stores locations of all repetitive
		// regions, including too short onces, in order to remove duplicates and
		// avoid miss-detections.
		RegionSet repetitiveRegions = new RegionSet();
		for (int motifLength = 1; motifLength <= maxMotifLength; motifLength++) {
			List<Repetition<T>> repetitions = findRepetitionFor(motifLength);

			for (Repetition<T> repetition : repetitions) {
				Region region = new Region(repetition.getStartIndex(),
						repetition.getEndIndex());

				if (!repetitiveRegions.contains(region)) {
					// only add repetitions with long-enough motifs
					if (motifLength >= minMotifLength) {
						result.add(repetition);
					}
					repetitiveRegions.add(region);
				}
			}
		}

		return result;
	}

	/**
	 * Performs detection of repetitions of a fixed motif length.
	 * 
	 * @param motifLength
	 *            Length of the motif
	 * 
	 * @return List of detected repetitions
	 */
	/* package */List<Repetition<T>> findRepetitionFor(int motifLength) {
		// create list for results
		List<Repetition<T>> repetitions = new ArrayList<Repetition<T>>();

		// Initialize left and right cursors
		int lPos = 0;
		int rPos = lPos + motifLength;

		// loop while both cursors are inside input array.
		while (lPos < input.length && rPos + motifLength <= input.length) {
			if (match(lPos, rPos, motifLength)) {
				// match found. increment right pointer and continue matching
				rPos += motifLength;
			} else {
				// no more matches: repetition cannot be made longer
				if (repetitionFound(lPos, rPos, motifLength)) {
					// we have found a repetition
					appendRepetition(lPos, rPos, motifLength, repetitions);
					lPos = rPos;
				} else {
					// we have not found a repetition
					lPos += 1;
				}
				rPos = lPos + motifLength;
			}
		}

		// check for outstanding match
		if (repetitionFound(lPos, rPos, motifLength)) {
			appendRepetition(lPos, rPos, motifLength, repetitions);
		}

		return repetitions;
	}

	/**
	 * Compares the motifLength- elements after lPos against the motifLength-
	 * elements after rPos. Returns true, if the comparer considers all to be
	 * equal, false otherwise.
	 */
	private boolean match(int lPos, int rPos, int motifLength) {
		for (int i = 0; i < motifLength; i++) {
			if (!comparator.equals(input[lPos + i], input[rPos + i])) {
				return false;
			}
		}
		return true;
	}

	/** Adds a repetition to the repetitions list */
	private void appendRepetition(int lPos, int rPos, int motifLength,
			List<Repetition<T>> repetitions) {
		Repetition<T> s = new Repetition<T>(input, lPos, rPos - 1, motifLength);
		repetitions.add(s);
	}

	/**
	 * Checks whether distance between cursors is larger than motifLength and
	 * that repetition is long enough
	 */
	private boolean repetitionFound(int lPos, int rPos, int motifLength) {
		return (rPos > (lPos + motifLength)) && ((rPos - lPos) >= minLength)
				&& ((rPos - lPos) / motifLength >= minMotifInstances);
	}

}