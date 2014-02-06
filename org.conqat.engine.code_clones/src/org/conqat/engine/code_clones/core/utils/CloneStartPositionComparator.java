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

import java.util.Comparator;

import org.conqat.engine.code_clones.core.Clone;

/**
 * Compares clones by comparing their start positions.
 * <p>
 * Can be used to sort clones in a file by their start positions
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: D0CAC79830B9FE0D141B165272F9D4C4
 */
public class CloneStartPositionComparator implements Comparator<Clone> {

	/** Singleton instance */
	private static CloneStartPositionComparator instance = null;

	/** Enforce use of singleton instance */
	private CloneStartPositionComparator() {
		// Nothing to do
	}

	/** {@inheritDoc} */
	@Override
	public int compare(Clone clone1, Clone clone2) {
		Integer start1 = clone1.getLocation().getRawStartOffset();
		Integer start2 = clone2.getLocation().getRawStartOffset();
		return start1.compareTo(start2);
	}

	/** Get singleton instance of this comparator */
	public static CloneStartPositionComparator getInstance() {
		if (instance == null) {
			instance = new CloneStartPositionComparator();
		}
		return instance;
	}

}