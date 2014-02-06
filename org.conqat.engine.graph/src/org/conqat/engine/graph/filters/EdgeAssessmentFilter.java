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
package org.conqat.engine.graph.filters;

import java.util.EnumSet;

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * Filters out edges based on an assessment
 * 
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: 98D85887ECE08652354505CC1F5A03F1
 */
@AConQATProcessor(description = "Filters out edges from a graph based on the edge assessment.")
public class EdgeAssessmentFilter extends EdgeFilterBase {

	/** Default value for the read key. */
	private static final String READ_KEY_DEFAULT = "assessment";

	/** Key under which assessment is stored */
	private String key = READ_KEY_DEFAULT;

	/** Assessment colors that are filtered out by this processor */
	private final EnumSet<ETrafficLightColor> excludedColors = EnumSet
			.noneOf(ETrafficLightColor.class);

	/**
	 * Set the key to use.
	 * 
	 * @param key
	 *            The key to get the value from that is used for comparsion.
	 */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 0, maxOccurrences = 1, description = ConQATParamDoc.READKEY_DESC
			+ " Default is to read from '" + READ_KEY_DEFAULT + "'.")
	public void setKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC)
			String key) {

		this.key = key;
	}

	/** Add excluded color. */
	@AConQATParameter(name = ConQATParamDoc.EXCLUDE_NAME, description = "Color to exclude.")
	public void addExcludedColor(
			@AConQATAttribute(name = "color", description = "The assessment color to exclude.")
			ETrafficLightColor color) {

		excludedColors.add(color);
	}

	/** Filters out all edges that carry an assessment with an excluded color */
	@Override
	protected boolean isFiltered(DirectedSparseEdge edge) {
		Object potentialAssessment = edge.getUserDatum(key);

		// don't filter edges without assessment
		if (potentialAssessment == null) {
			return false;
		}

		// don't filter edges with incompatible assessment types
		if (!(potentialAssessment instanceof Assessment)) {
			getLogger().warn(
					"Object stored under key '" + key + "' in edge " + edge
							+ " could not be casted to Assessment");
			return false;
		}

		// we can safely cast to Assessment at this point
		Assessment assessment = (Assessment) potentialAssessment;
		return excludedColors.contains(assessment.getDominantColor());
	}
}