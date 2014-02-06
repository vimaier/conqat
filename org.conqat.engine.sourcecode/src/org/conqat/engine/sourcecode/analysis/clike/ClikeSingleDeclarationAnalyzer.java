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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.shallowparsed.ShallowParsedFindingAnalyzerBase;
import org.conqat.engine.sourcecode.analysis.shallowparsed.ShallowParsingUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * <p>
 * The processor uses the heuristic, that a top-level comma hints at multiple
 * declarations.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45071 $
 * @ConQAT.Rating GREEN Hash: 120EB6AB685144293B658045418A3C21
 */
@AConQATProcessor(description = "This processor ensures that no declaration statement contains multiple declarations. "
		+ "Works only for C-like languages.")
public class ClikeSingleDeclarationAnalyzer extends
		ShallowParsedFindingAnalyzerBase {

	/** Maps each opening brace type to the closing equivalent. */
	private static final Map<ETokenType, ETokenType> BRACES = new EnumMap<ETokenType, ETokenType>(
			ETokenType.class);

	static {
		BRACES.put(ETokenType.LPAREN, ETokenType.RPAREN);
		BRACES.put(ETokenType.LBRACE, ETokenType.RBRACE);
		BRACES.put(ETokenType.LBRACK, ETokenType.RBRACK);
		BRACES.put(ETokenType.LT, ETokenType.GT);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException {
		for (ShallowEntity statement : ShallowParsingUtils
				.listPrimitiveStatements(entities)) {
			analyzeStatement(element, statement);
		}
	}

	/** Analyzes a single statement for rule violation. */
	private void analyzeStatement(ITokenElement element, ShallowEntity statement)
			throws ConQATException {
		// stores expected closing element
		Stack<ETokenType> nestingStack = new Stack<ETokenType>();

		for (IToken token : statement.includedTokens()) {
			ETokenType tokenType = token.getType();
			ETokenType closing = BRACES.get(tokenType);
			if (closing != null) {
				nestingStack.push(closing);
			} else if (nestingStack.isEmpty()) {

				// if finding a comma outside of nested context, create finding
				if (tokenType == ETokenType.COMMA) {
					createFindingForEntityRegion(
							"Multiple declarations in statement", element,
							statement);
					return;
				}
			} else if (nestingStack.peek() == tokenType) {
				nestingStack.pop();
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Multiple declarations in same statement";
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingCategoryName() {
		return "Formatting";
	}

}
