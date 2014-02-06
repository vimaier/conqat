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
package org.conqat.engine.code_clones.core.constraint;

import java.util.List;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.region.Region;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 0DFDF667C650FFA1F0FAB8BB8AF02488
 */
@AConQATProcessor(description = ""
		+ "Constraint that is satisfied if none of the clones of the CloneClass overlapp")
public class NonOverlappingConstraint extends ConstraintBase {

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) {
		ListMap<String, Region> cloneRegions = new ListMap<String, Region>();

		// look for overlaps
		for (Clone clone : cloneClass.getClones()) {
			String uniformPath = clone.getUniformPath();
			TextRegionLocation location = clone.getLocation();
			Region cloneRegion = new Region(location.getRawStartOffset(),
					location.getRawEndOffset());

			if (overlaps(cloneRegion, cloneRegions.getCollection(uniformPath))) {
				return false;
			}
			cloneRegions.add(uniformPath, cloneRegion);
		}

		// if code reaches here, no overlap was found
		return true;
	}

	/** Checks whether a region overlaps with any region in a list of regions */
	private boolean overlaps(Region cloneRegion, List<Region> regionsInSameFile) {
		if (regionsInSameFile == null) {
			return false;
		}

		for (Region region : regionsInSameFile) {
			if (region.overlaps(cloneRegion)) {
				return true;
			}
		}
		return false;
	}

}