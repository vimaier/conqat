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
package org.conqat.engine.code_clones.result.annotation;

import java.util.List;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.region.RegionSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Revision: 34670 $
 * @ConQAT.Rating GREEN Hash: EBF3E5D17E2ED8CFFBE8316B09FBD8E7
 */
@AConQATProcessor(description = "Computes the number of units of a class that are part of at "
		+ "least one clone.")
public class CloneUnitsAnnotator extends CloneAnnotatorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key that stores Clone units", type = "java.lang.Integer")
	public final static String CLONE_UNITS_KEY = "Clone Units";

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { CLONE_UNITS_KEY };
	}

	/** {@inheritDoc} */
	@Override
	protected void annotateClones(ITextElement element,
			UnmodifiableList<Clone> clonesList) {
		element.setValue(CLONE_UNITS_KEY, calcCloneUnits(clonesList));
	}

	/**
	 * Computes the number of units of an element that are covered by at least
	 * one clone. This corresponds to the non-overlapping sum of the length of
	 * the clones annotated to this class, measured in units.
	 */
	public static int calcCloneUnits(List<Clone> clones) {
		RegionSet regions = new RegionSet("clones");

		for (Clone clone : clones) {
			Region region = new Region(clone.getStartUnitIndexInElement(),
					clone.getStartUnitIndexInElement()
							+ clone.getLengthInUnits() - 1, "clone");
			regions.add(region);
		}

		return regions.getPositionCount();
	}

}