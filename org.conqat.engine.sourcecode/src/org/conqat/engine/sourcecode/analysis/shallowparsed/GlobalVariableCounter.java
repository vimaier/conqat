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
package org.conqat.engine.sourcecode.analysis.shallowparsed;

import java.util.List;

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.TokenStreamUtils;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.scanner.ETokenType;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 47063 $
 * @ConQAT.Rating GREEN Hash: E8122B1F8E11744E91FEF049316ED094
 */
@AConQATProcessor(description = "Counts the number of global variables per element. "
		+ "This does not include constants, even at global level.")
public class GlobalVariableCounter extends ShallowParsedMetricAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The number of global variables.", type = "java.lang.Number")
	public static final String KEY = "Global Variables";

	/**
	 * Tokens that are indicators that a variable declaration is not a global
	 * variable.
	 */
	private static final ETokenType[] NON_GLOBAL_TOKENS = {
			// java constant
			ETokenType.FINAL,
			// C constant
			ETokenType.CONST,
			// C++ type scope, i.e. contained in other type
			ETokenType.SCOPE };

	/** {@inheritDoc} */
	@Override
	protected String getKey() {
		return KEY;
	}

	/** {@inheritDoc} */
	@Override
	protected void calculateMetrics(ITokenElement element,
			List<ShallowEntity> entities) {
		reportMetricValue(countGlobals(entities));
	}

	/**
	 * Returns the number of global variables in the given (top-level) entities
	 * or in modules contained there.
	 */
	private int countGlobals(List<ShallowEntity> entities) {
		int count = 0;
		for (ShallowEntity entity : entities) {
			if (entity.getType() == EShallowEntityType.ATTRIBUTE) {
				if (canBeGlobal(entity)) {
					count += 1;
				}
			} else if (entity.getType() == EShallowEntityType.MODULE) {
				count += countGlobals(entity.getChildren());
			}
		}
		return count;
	}

	/**
	 * Language specific heuristics whether the given entity can be a global
	 * variable. Especially returns <code>false</code> for constants, as these
	 * are often found at global scope, but are considered harmless.
	 */
	private boolean canBeGlobal(ShallowEntity entity) {
		return !TokenStreamUtils.containsAny(entity.includedTokens(),
				NON_GLOBAL_TOKENS);
	}
}
