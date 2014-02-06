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
package org.conqat.engine.code_clones.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.digest.Digester;
import org.conqat.lib.commons.region.Region;

/**
 * This class offers utility methods useful in the context of clone detection.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 66D7D901DB145ABB5E501B1B25CC4929
 */
public class CloneUtils {

	/** Key under which list of units is stored */
	public static final String UNIT_LIST = "unit_list";

	/** Constant that depicts the number unlimited */
	public static final int UNLIMITED = -1;

	/** Count the clones contained in a list of clone classes. */
	public static int countClones(List<CloneClass> cloneClasses) {
		int count = 0;
		for (CloneClass cloneClass : cloneClasses) {
			count += cloneClass.size();
		}
		return count;
	}

	/** Gets the siblings of a clone in its clone class */
	public static ArrayList<Clone> getSiblings(Clone clone) {
		ArrayList<Clone> siblings = new ArrayList<Clone>();
		for (Clone sibling : clone.getCloneClass().getClones()) {
			if (clone != sibling) {
				siblings.add(sibling);
			}
		}
		return siblings;
	}

	/**
	 * Computes the fingerprint for a collection of clones. If the clones have
	 * different fingerprints, a new fingerprint is created, else the common
	 * fingerprint is returned.
	 */
	public static String createFingerprint(Collection<Clone> clones) {
		CCSMAssert.isFalse(clones.isEmpty(),
				"Cannot create fingerprint for empty collection of clones");

		if (allFingerprintsEqual(clones)) {
			return CollectionUtils.getAny(clones).getFingerprint();
		}

		// as long as the fingerprints of the individual clones do not change,
		// we need to assure that the clone class fingerprint does not change
		// either. in order to compute a stable fingerprint for a clone class,
		// we thus need to establish a stable order in which clone fingerprints
		// are added to the clone class fingerprint. since we want to be
		// independent of clone filenames or clone positions in files, we
		// compute this order directly on the fingerprints.
		Set<String> fingerprints = new HashSet<String>();
		for (Clone clone : clones) {
			fingerprints.add(clone.getFingerprint());
		}

		return Digester.createMD5Digest(fingerprints);
	}

	/** Determines whether the fingerprints of all clones are equal. */
	private static boolean allFingerprintsEqual(Collection<Clone> clones) {
		String commonFingerprint = CollectionUtils.getAny(clones)
				.getFingerprint();
		for (Clone clone : clones) {
			if (!commonFingerprint.equals(clone.getFingerprint())) {
				return false;
			}
		}

		return true;
	}

	/** Creates a map from element to list of contained clones */
	public static ListMap<String, Clone> createFileMapping(
			List<CloneClass> cloneClasses) {
		return initElementMapping(cloneClasses, new ListMap<String, Clone>());
	}

	/**
	 * Store mapping from elements to clones in an existing map. This way, maps
	 * that are stored as fields in classes can be declared final, if they never
	 * change.
	 */
	public static ListMap<String, Clone> initElementMapping(
			List<CloneClass> cloneClasses,
			ListMap<String, Clone> clonesPerElement) {
		CCSMAssert.isTrue(clonesPerElement.getValues().isEmpty(),
				"Map is not empty");

		for (CloneClass cloneClass : cloneClasses) {
			for (Clone clone : cloneClass.getClones()) {
				clonesPerElement.add(clone.getUniformPath(), clone);
			}
		}
		return clonesPerElement;
	}

	/**
	 * Returns a list that is truncated after n clones. Use {@link #UNLIMITED}
	 * to switch off truncation.
	 */
	public static List<CloneClass> cloneClassesForFirstNClones(
			List<CloneClass> allCloneClasses, int maxCloneCount) {
		List<CloneClass> keptCloneClasses = new ArrayList<CloneClass>();

		int clonesSoFar = 0;
		for (CloneClass cloneClass : allCloneClasses) {
			if (maxCloneCount == CloneUtils.UNLIMITED
					|| clonesSoFar + cloneClass.size() <= maxCloneCount) {
				keptCloneClasses.add(cloneClass);
				clonesSoFar += cloneClass.size();
			}
		}

		return keptCloneClasses;
	}

	/** Return list of clone units, or <code>null</code>, if no list was stored */
	@SuppressWarnings("unchecked")
	public static List<Unit> getUnits(Clone clone) {
		return (List<Unit>) clone.getValue(UNIT_LIST);
	}

	/** Store list of units at a clone */
	public static void setUnits(Clone clone, List<Unit> cloneUnits) {
		clone.setValue(UNIT_LIST, cloneUnits);
		clone.setTransient(UNIT_LIST, true);
	}

	/**
	 * Computes a set of strings that identifies gap positions for a clone
	 * class. Gap identifier contain uniform path and raw start offset of gap.
	 */
	public static Set<String> gapIdentifierFor(CloneClass cloneClass) {
		Set<String> gapIdentifiers = new HashSet<String>();

		for (Clone clone : cloneClass.getClones()) {
			String uniformPath = clone.getUniformPath();
			for (Region gapPosition : clone.getGapPositions()) {
				String gapIdentifier = uniformPath + ":"
						+ gapPosition.getStart();
				gapIdentifiers.add(gapIdentifier);
			}
		}
		return gapIdentifiers;
	}
}