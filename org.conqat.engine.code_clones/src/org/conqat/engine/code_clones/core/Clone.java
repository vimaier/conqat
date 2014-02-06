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

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.region.Region;

/**
 * Class that represents cloned code regions.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 57629E7BE7C9B5E9FE544796110FBDB1
 */
public class Clone extends KeyValueStoreBase {

	/** {@link CloneClass} this clone belongs to */
	private CloneClass cloneClass;

	/** The location of the clone. */
	private final TextRegionLocation location;

	/** Fingerprint of clone */
	private final String fingerprint;

	/** Position of the first unit of the clone in its element */
	private final int startUnitIndexInElement;

	/** Length of the clone in units */
	private final int lengthInUnits;

	/**
	 * The gaps in the clone stored as region of raw offsets (absolute in
	 * element).
	 */
	private List<Region> gaps;

	/** Delta size in units */
	protected int deltaInUnits;

	/** Creates a clone with a delta in units of 0 */
	public Clone(long id, CloneClass cloneClass, TextRegionLocation location,
			int startUnitIndexInElement, int lengthInUnits, String fingerprint) {
		this(id, cloneClass, location, startUnitIndexInElement, lengthInUnits,
				fingerprint, 0);
	}

	/**
	 * Constructor
	 * 
	 * @param cloneClass
	 *            this may be null to explicitly create a clone instance without
	 *            clone class.
	 */
	public Clone(long id, CloneClass cloneClass, TextRegionLocation location,
			int startUnitIndexInElement, int lengthInUnits, String fingerprint,
			int deltaInUnits) {
		super(id);

		CCSMAssert.isNotNull(location);

		this.cloneClass = cloneClass;

		// We use the Java string pool here for because:
		// - during clone detection, many clones can be created
		// - all fingerprints of non-gapped clones in same clone class are equal
		// (but created as different instances)
		this.fingerprint = fingerprint.intern();

		this.location = location;
		this.startUnitIndexInElement = startUnitIndexInElement;
		this.lengthInUnits = lengthInUnits;
		this.deltaInUnits = deltaInUnits;

		CCSMPre.isTrue(
				location.getRawEndOffset() >= location.getRawStartOffset(),
				"Length must not be negative: " + this);

		if (cloneClass != null) {
			cloneClass.add(this);
		}
	}

	/**
	 * Two clones are considered equal, if they describe the same region of code
	 * in the same element with the same gaps.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Clone)) {
			return false;
		}

		Clone other = (Clone) obj;

		// compare source
		if (!location.getUniformPath().equals(other.location.getUniformPath())) {
			return false;
		}

		// compare start units
		if (getStartUnitIndexInElement() != other.getStartUnitIndexInElement()) {
			return false;
		}

		// compare length in units
		if (getLastUnitInElement() != other.getLastUnitInElement()) {
			return false;
		}

		// compare gaps
		if (gapCount() != other.gapCount()) {
			return false;
		}
		for (int i = 0; i < gapCount(); ++i) {
			if (!gaps.get(i).equalsStartEnd(other.gaps.get(i))) {
				return false;
			}
		}

		// if code reaches here, clones are equal
		return true;
	}

	/**
	 * Returns the hash code based on the uniform path, the start and length.
	 * Gap information is ignored.
	 */
	@Override
	public int hashCode() {
		return (location.getUniformPath().hashCode() * 13 + getStartUnitIndexInElement())
				* 17 + getLengthInUnits();
	}

	/** {@link CloneClass} this clone belongs to */
	public CloneClass getCloneClass() {
		return cloneClass;
	}

	/**
	 * Fingerprint of clone. A clone fingerprint characterizes the piece of
	 * cloned code after normalization. All ungapped clones inside a single
	 * clone class have the same fingerprint.
	 */
	public String getFingerprint() {
		return fingerprint;
	}

	/** Position of the first unit of the clone in its element */
	public int getStartUnitIndexInElement() {
		return startUnitIndexInElement;
	}

	/** Length of the clone in units. */
	public int getLengthInUnits() {
		return lengthInUnits;
	}

	/** Position of the last unit of the clone in its element */
	public int getLastUnitInElement() {
		return getStartUnitIndexInElement() + getLengthInUnits() - 1;
	}

	/** Edit distance to first clone in clone class */
	public int getDeltaInUnits() {
		return deltaInUnits;
	}

	/** Returns the clone's location. */
	public TextRegionLocation getLocation() {
		return location;
	}

	/** Returns the uniform path of this clone's location. */
	public String getUniformPath() {
		return location.getUniformPath();
	}

	/** Determines whether the clone contains gaps */
	public boolean containsGaps() {
		return gaps != null && gaps.size() > 0;
	}

	/** Return number of gaps, or 0, if clone has no gaps */
	public int gapCount() {
		if (gaps == null) {
			return 0;
		}
		return gaps.size();
	}

	/**
	 * Returns a list of the gap positions as raw start end offsets (absolute
	 * offsets in the element), or empty list, if clone has no gaps.
	 */
	public UnmodifiableList<Region> getGapPositions() {
		if (gaps == null) {
			return CollectionUtils.emptyList();
		}

		return CollectionUtils.asSortedUnmodifiableList(gaps);
	}

	/** Adds a gap position. */
	public void addGap(Region gapRegion) {
		if (gaps == null) {
			gaps = new ArrayList<Region>();
		}
		gaps.add(gapRegion);
	}

	/** Set delta size in units */
	public void setDeltaInUnits(int deltaInUnits) {
		this.deltaInUnits = deltaInUnits;
	}

	/** String representation of the essential clone data. */
	@Override
	public String toString() {
		return "Clone [" + location.toLocationString() + "]";
	}

	/** Sets the clone class. Only called from CloneClass. */
	/* package */void setCloneClass(CloneClass cloneClass) {
		this.cloneClass = cloneClass;
	}
}