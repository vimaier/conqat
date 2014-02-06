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
 * @ConQAT.Rating GREEN Hash: CB3EE708BA7903E209FD7F75ECF33F29
 */
@AConQATProcessor(description = "Checks that no SELECT * is used in the code.")
public class SelectAllAnalyzer extends PlsqlFindingAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException {
		for (ShallowEntity entity : ShallowEntityTraversalUtils
				.listEntitiesOfType(entities, EShallowEntityType.STATEMENT)) {
			List<IToken> tokens = entity.includedTokens();
			if (tokens.size() >= 2
					&& tokens.get(0).getType() == ETokenType.SELECT
					&& tokens.get(1).getType() == ETokenType.MULT) {
				createFindingForEntityStart("SELECT * should not be used.",
						element, entity);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Do not use SELECT *";
	}
}
