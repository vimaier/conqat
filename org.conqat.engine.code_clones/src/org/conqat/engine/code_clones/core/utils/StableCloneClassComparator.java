/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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

import java.util.Collections;
import java.util.Comparator;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;

/**
 * Comparator that creates stable order of {@link CloneClass}es. The order is
 * not specified.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 34953 $
 * @ConQAT.Rating GREEN Hash: C1E8DCC12B5D8AA555080065727234EE
 */
public class StableCloneClassComparator implements Comparator<CloneClass> {

	/** Singleton instance */
	public static StableCloneClassComparator INSTANCE = new StableCloneClassComparator();

	/** {@inheritDoc} */
	@Override
	public int compare(CloneClass cc1, CloneClass cc2) {

		// sort by fingerprints
		int fingerprintOrder = cc1.getFingerprint().compareTo(
				cc2.getFingerprint());
		if (fingerprintOrder != 0) {
			return fingerprintOrder;
		}

		// sort by filenames of first clones
		Clone c1 = firstClone(cc1);
		Clone c2 = firstClone(cc2);
		int pathOrder = c1.getUniformPath().compareTo(c2.getUniformPath());
		if (pathOrder != 0) {
			return pathOrder;
		}

		// sort by start positions
		int startPositionOrder = c1.getStartUnitIndexInElement()
				- c2.getStartUnitIndexInElement();
		if (startPositionOrder != 0) {
			return startPositionOrder;
		}

		// we assume this case to be so unlikely that we don't throw an
		// exception here.
		return 0;
	}

	/** Sort clones and retrieve first one */
	private Clone firstClone(CloneClass cc1) {
		return Collections.min(cc1.getClones(), StableCloneComparator.INSTANCE);
	}

}