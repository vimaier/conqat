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

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.StatementUnit;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35938 $
 * @ConQAT.Rating GREEN Hash: C4DC77C3509A11D98647ED69A4D652D5
 */
@AConQATProcessor(description = "Constraint that is satisfied if the statements of a clone contain at least a "
		+ "minimal number of tokens. Only applicable to clones that are formed from "
		+ "statements. Only applicable if units are stored. If not all clones in a clone class contain "
		+ "the same number of tokens, the largest number of statements is used for constraint evaluation. "
		+ "In order to be able to use the constraint in general purpose clone detection blocks, even if no minimal "
		+ "token count constraint is intended, the constraint is deactivated when the minimal token count is not set.")
public class NumberOfTokensConstraint extends StatementUnitsConstraintBase {

	/** Unknown value */
	private static final int UNKNOWN = -1;

	/** Minimal length in tokens. If not set, constraint is deactivated. */
	private int minTokens = UNKNOWN;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "min", minOccurrences = 0, maxOccurrences = 1, description = "Minimal number of tokens")
	public void setMinLength(
			@AConQATAttribute(name = "tokens", description = "If not set, constraint is deactivated") int minTokens) {
		this.minTokens = minTokens;
	}

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) throws ConQATException {
		if (minTokens == UNKNOWN) {
			return true;
		}

		int maxTokens = 0;
		for (Clone clone : cloneClass.getClones()) {
			maxTokens = Math.max(numberOfTokens(clone), maxTokens);
		}

		return maxTokens >= minTokens;
	}

	/** Determines the number of tokens stored in the statements of a clone */
	private int numberOfTokens(Clone clone) throws ConQATException {
		int tokenCount = 0;

		for (StatementUnit statement : getStatementUnits(clone)) {
			tokenCount += statement.getTokens().length;
		}

		return tokenCount;
	}

}