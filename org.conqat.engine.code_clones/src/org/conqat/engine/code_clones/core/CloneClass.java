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
package org.conqat.engine.code_clones.core;

import java.util.LinkedHashSet;
import java.util.Set;

import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;

/**
 * A clone class groups a set of related clones. The relationship between the
 * clones in a clone class depends on the detection algorithm used.
 * <p>
 * For ungapped clone detection, all clones inside a clone class are equal with
 * respect to an equivalence relation. In this implementation, this equivalence
 * relation is textual equivalence of the clone's units after normalization.
 * <p>
 * For gapped clone detection, all clones inside a clone class are similar with
 * respect to a maximal edit distance that gives an upper bound of the required
 * insert or delete operations that are required to establish textual
 * equivalence of two clones in the clone class. (Textual equivalence is again
 * defined on the clone's units after normalization.)
 * <p>
 * Since the edit-distance based notion of similarity as used in the gapped case
 * is not transitive (), gapped clones do not share an equivalence relation,
 * since equivalence relations need to be transitive. In practice, as opposed to
 * the ungapped case, this means that clone classes do not need to be disjunct.
 * <p>
 * Each clone class has a fingerprint that describes its characteristic
 * properties. It can be used to identify clone classes and is thus employed for
 * filtering.
 * <p>
 * Moreover, the clone class provides a key/value mechanism to store additional
 * information that is persisted in the clone reports.
 * 
 * @author Rainer Spitzhirn
 * @author Julian Much
 * @author Florian Deissenboeck
 * @author $Author: juergens $
 * 
 * @version $Revision: 43751 $
 * @ConQAT.Rating GREEN Hash: 8A44F4A164098A21E682D56502642274
 */
public class CloneClass extends KeyValueStoreBase {

	/** The length of the clone class (number of units). */
	private int normalizedLength;

	/** A list containing all clones of a class. */
	private final Set<Clone> clones = new LinkedHashSet<Clone>();

	/**
	 * Create a new clone class with a given id
	 * 
	 * @param normalizedLength
	 *            Length of the clones (in units) in this clone class
	 */
	public CloneClass(int normalizedLength, long id) {
		super(id);
		this.normalizedLength = normalizedLength;
	}

	/**
	 * The normalized length of all clones in this class in units and thereby
	 * the length of this class.
	 */
	public int getNormalizedLength() {
		return normalizedLength;
	}

	/** Set normalized length */
	public void setNormalizedLength(int normalizedLength) {
		this.normalizedLength = normalizedLength;
	}

	/** Computes and returns the fingerprint for this clone class */
	public String getFingerprint() {
		return CloneUtils.createFingerprint(getClones());
	}

	/** Returns the clones of this clone class. */
	public UnmodifiableSet<Clone> getClones() {
		return CollectionUtils.asUnmodifiable(clones);
	}

	/** The size (number of clones) of this clone class. */
	public int size() {
		return clones.size();
	}

	/** Two clone classes are equal, if their fingerprints are equal */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CloneClass)) {
			return false;
		}

		return getFingerprint().equals(((CloneClass) obj).getFingerprint());
	}

	/** Returns hash code of fingerprint */
	@Override
	public int hashCode() {
		return getFingerprint().hashCode();
	}

	/** Returns the number of gaps in the clone with the most gaps */
	public int getMaxGapNumber() {
		int gapCount = 0;
		for (Clone clone : getClones()) {
			if (gapCount < clone.gapCount()) {
				gapCount = clone.gapCount();
			}
		}
		return gapCount;
	}

	/** Returns sum of gaps contained in all clones */
	public int getGapCount() {
		int gapCount = 0;
		for (Clone clone : getClones()) {
			gapCount += clone.gapCount();
		}
		return gapCount;
	}

	/**
	 * Adds a clone to this class.
	 * <p>
	 * No sanity check is performed that makes sure that a clone really belongs
	 * to a clone class in order to allow different clone detection approaches
	 * to form clone classes for different notions of similarity
	 */
	public void add(Clone clone) {
		boolean cloneIsInOtherClass = clone.getCloneClass() != this
				&& clone.getCloneClass() != null;
		if (cloneIsInOtherClass) {
			clone.getCloneClass().remove(clone);
		}

		clones.add(clone);
		clone.setCloneClass(this);
	}

	/** Removes a clone */
	public void remove(Clone clone) {
		if (clones.remove(clone)) {
			clone.setCloneClass(null);
		}
	}
}