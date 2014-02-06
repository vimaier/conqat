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
package org.conqat.engine.commons.filter;

import java.util.EnumSet;

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This class allows filtering based on assessment obtained by other analyzers.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @levd.rating GREEN Hash: 1ED11C4F965F7A3CD67646ADD36FE46F
 */
@AConQATProcessor(description = "Filters nodes that have a specific assessement color."
		+ " By default all colors are included. To exclude specific use either the include "
		+ " or exclude parameters. Exclusion is stronger than inclusion.")
public class AssessmentFilter extends
		KeyBasedFilterBase<Assessment, IRemovableConQATNode> {

	/** Set of colors to include. */
	private final EnumSet<ETrafficLightColor> includedColors = EnumSet
			.noneOf(ETrafficLightColor.class);

	/** Set of colors to exclude. */
	private final EnumSet<ETrafficLightColor> excludedColors = EnumSet
			.noneOf(ETrafficLightColor.class);

	/** Add included color. */
	@AConQATParameter(name = ConQATParamDoc.INCLUDE_NAME, description = "Color to include.")
	public void addIncludedColor(
			@AConQATAttribute(name = "color", description = "The assessment color to include.") ETrafficLightColor color) {

		includedColors.add(color);
	}

	/** Add excluded color. */
	@AConQATParameter(name = ConQATParamDoc.EXCLUDE_NAME, description = "Color to exclude.")
	public void addExcludedColor(
			@AConQATAttribute(name = "color", description = "The assessment color to exclude.") ETrafficLightColor color) {

		excludedColors.add(color);
	}

	/**
	 * Determines node exclusion. Nodes are excluded if their assessment is not
	 * in the set of included colors or in the set of excluded colors.
	 */
	@Override
	protected boolean isFilteredForValue(Assessment value) {
		ETrafficLightColor color = value.getDominantColor();

		if (excludedColors.contains(color)) {
			return true;
		}
		if (includedColors.isEmpty()) {
			return false;
		}
		return !includedColors.contains(color);
	}
}