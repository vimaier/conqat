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
package org.conqat.engine.sourcecode.analysis.clike;

import java.util.List;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.shallowparsed.ShallowParsedFindingAnalyzerBase;
import org.conqat.engine.sourcecode.analysis.shallowparsed.ShallowParsingUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQATDoc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43919 $
 * @ConQAT.Rating GREEN Hash: 21CEBC775BF5E89DB0E61C6F8285BB73
 */
@AConQATProcessor(description = "Creates findings for statement blocks in loops "
		+ "and conditions that are not enclosed in braces - '{' and '}'.")
public class BracesAroundCodeBlocksAnalyzer extends
		ShallowParsedFindingAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException {
		for (ShallowEntity entity : ShallowParsingUtils
				.listNestedStatements(entities)) {
			analyzeStatement(element, entity);
		}
	}

	/** Analyzes a statement and creates finding if necessary. */
	private void analyzeStatement(ITokenElement element, ShallowEntity entity)
			throws ConQATException {
		// These statements always have at least one child
		ShallowEntity firstChild = entity.getChildren().get(0);

		// ignore if preprocessor statements in C++ are found
		if (firstChild.getType().equals(EShallowEntityType.META)) {
			return;
		}

		IToken lastTokenBeforeChild = entity.includedTokens().get(
				firstChild.getRelativeStartTokenIndex() - 1);

		if (lastTokenBeforeChild.getType() != ETokenType.LBRACE) {
			createFindingForEntityStart("Condition or loop without braces",
					element, entity);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Missing braces";
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingCategoryName() {
		return "Formatting";
	}

}
