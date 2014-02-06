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
package org.conqat.engine.sourcecode.analysis.plsql;

import java.util.List;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 43229 $
 * @ConQAT.Rating GREEN Hash: 6BA2870847FBFD2F1821BC61B0D4F6EE
 */
@AConQATProcessor(description = "Checks whether each exception handler contains a default (WHEN OTHERS) handler.")
public class PlsqlExceptionHandlerOthersAnalyzer extends
		PlsqlFindingAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException {
		for (ShallowEntity method : ShallowEntityTraversalUtils
				.listEntitiesOfType(entities, EShallowEntityType.METHOD)) {

			ShallowEntity exceptionStartChild = null;
			boolean hadWhenOthers = false;

			// we check only top level META and STATEMENT entities, searching
			// for a exceptions block followed by "when others"
			for (ShallowEntity child : method.getChildren()) {
				if (isExceptionStart(child)) {
					exceptionStartChild = child;
				} else if (exceptionStartChild != null && isWhenOthers(child)) {
					hadWhenOthers = true;
				}
			}

			if (exceptionStartChild != null && !hadWhenOthers) {
				createFindingForEntityStart(
						"WHEN OTHERS missing from exception handlers.",
						element, exceptionStartChild);
			}
		}
	}

	/** Returns whether the child starts the exception section. */
	private boolean isExceptionStart(ShallowEntity child) {
		return child.getType() == EShallowEntityType.META
				&& child.includedTokens().get(0).getType() == ETokenType.EXCEPTION;
	}

	/** Returns whether the child is a "when others" statement. */
	private boolean isWhenOthers(ShallowEntity child) {
		List<IToken> tokens = child.includedTokens();
		return child.getType() == EShallowEntityType.META && tokens.size() >= 2
				&& tokens.get(0).getType() == ETokenType.WHEN
				&& tokens.get(1).getType() == ETokenType.OTHERS;
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Exception handling";
	}
}
