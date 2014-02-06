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
package org.conqat.engine.code_clones.core.constraint;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.StatementUnit;
import org.conqat.engine.code_clones.core.TokenUnit;
import org.conqat.engine.code_clones.core.matching.UnitListDelta;
import org.conqat.engine.code_clones.core.matching.UnitListDiffer;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46641 $
 * @ConQAT.Rating GREEN Hash: 4DCE629DC84C76602AAE93A16EB7463C
 */
@AConQATProcessor(description = "Filters clones based on the similarity / equality / term equality of their identifiers and literals. "
		+ "If all thresholds are set to < 0, the processor is deactivated. Default is deactivated. "
		+ "Equality denotes the fraction of identifiers/literals that are equal in both clones (same name and position). "
		+ "Similarity denotes the fraction of identifiers/literals that can be consistenly renamed, which subsumes equal ones. "
		+ "Term equality denotes the fraction of used terms (treated as set) that are equal in both clones.")
public class SimilarityConstraint extends StatementUnitsConstraintBase {

	/** Threshold that indicates that processor is inactive */
	private static final double DEACTIVATED = -1;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "similarity", attribute = "threshold", optional = true, description = ""
			+ "Minimal similarity threshold (inclusive). Ranges between 0 (no similarity) and 1 (perfect similarity). "
			+ "Default is disabled similarity check.")
	public double similarityThreshold = DEACTIVATED;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "equality", attribute = "threshold", optional = true, description = "Minimal equality threshold (inclusive). Ranges between 0 (no equality) and 1 (perfect equality). "
			+ "Default is disabled equality check.")
	public double equalityThreshold = DEACTIVATED;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "term-equality", attribute = "threshold", optional = true, description = ""
			+ "Minimal term equality threshold (inclusive). Ranges between 0 (no term equality) and 1 (perfect term equality). "
			+ "Default is disabled term equality check.")
	public double termEqualityThreshold = DEACTIVATED;

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) throws ConQATException {
		if (similarityThreshold < 0 && equalityThreshold < 0
				&& termEqualityThreshold < 0) {
			return true;
		}

		List<List<String>> textLists = new ArrayList<List<String>>();

		int length = -1;
		for (Clone clone : cloneClass.getClones()) {
			List<String> unnormalizedTokenContent = getUnnormalizedTokenContent(clone);
			if (length == -1) {
				length = unnormalizedTokenContent.size();
			} else if (length != unnormalizedTokenContent.size()) {
				throw new ConQATException(
						"This constraint can only be applied to clone classes in which all "
								+ "clones have the same number of statements. Problematic clones: "
								+ StringUtils.concat(cloneClass.getClones(),
										", "));
			}
			textLists.add(unnormalizedTokenContent);
		}

		UnitListDelta delta = UnitListDiffer.computeDelta(textLists);

		return delta.getSimilarity() >= similarityThreshold
				&& delta.getEquality() >= equalityThreshold
				&& delta.getTermEquality() >= termEqualityThreshold;
	}

	/** Get list of unnormalized text content of units that could be normalized */
	private List<String> getUnnormalizedTokenContent(Clone clone)
			throws ConQATException {
		List<String> statementTexts = new ArrayList<String>();

		for (StatementUnit statement : getStatementUnits(clone)) {
			for (TokenUnit tokenUnit : statement.getTokens()) {
				if (TokenUnit.couldBeNormalized(tokenUnit.getType())
						&& !StringUtils.isEmpty(tokenUnit.getContent())) {
					String unnormalizedContent = tokenUnit
							.getUnnormalizedContent();
					// we ignore casing differences here, since some languages
					// are case-insensitive
					unnormalizedContent = unnormalizedContent.toLowerCase();
					statementTexts.add(unnormalizedContent);
				}
			}
		}

		return statementTexts;
	}

}
