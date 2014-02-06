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
package org.conqat.engine.sourcecode.analysis.java;

import java.util.List;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.TokenStreamUtils;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43899 $
 * @ConQAT.Rating GREEN Hash: 4A5FED4C1F88EC58F35B17872F97C692
 */
@AConQATProcessor(description = "Checks for public (non constant) attributes in classes.")
public class JavaPublicAttributesAnalyzer extends JavaFindingAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException {
		for (ShallowEntity entity : ShallowEntityTraversalUtils
				.listEntitiesOfType(entities, EShallowEntityType.ATTRIBUTE)) {
			List<IToken> tokens = entity.includedTokens();
			int identifier = TokenStreamUtils.find(tokens,
					ETokenType.IDENTIFIER);

			CCSMAssert.isTrue(identifier != TokenStreamUtils.NOT_FOUND,
					"Attribute without identifier?");

			boolean isPublic = TokenStreamUtils.tokenStreamContains(tokens, 0,
					identifier, ETokenType.PUBLIC);
			boolean isStatic = TokenStreamUtils.tokenStreamContains(tokens, 0,
					identifier, ETokenType.STATIC);
			boolean isFinal = TokenStreamUtils.tokenStreamContains(tokens, 0,
					identifier, ETokenType.FINAL);

			if (isPublic && !(isStatic && isFinal)) {
				createFindingForEntityStart("Non-constant public attribute",
						element, entity);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Public attributes";
	}

}
