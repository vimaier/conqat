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

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.TokenElementFindingAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for analyzing shallow parsed code.
 * 
 * @author $Author: goede $
 * @version $Rev: 41798 $
 * @ConQAT.Rating GREEN Hash: 004A76FCB340775A7BB8A9A9C9A6B67C
 */
public abstract class ShallowParsedFindingAnalyzerBase extends
		TokenElementFindingAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected void analyzeTokens(List<IToken> tokens, ITokenElement element) {
		try {
			analyzeShallowEntities(element,
					ShallowParserFactory.parse(element, getLogger()));
		} catch (ConQATException e) {
			getLogger().warn(
					"Ignoring element " + element.getLocation() + ": "
							+ e.getMessage());
		}
	}

	/** Template method for checking the shallow entities. */
	protected abstract void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException;

	/** Creates a finding taking the position from the entity. */
	protected void createFindingForEntityStart(String message,
			ITokenElement element, ShallowEntity entity) throws ConQATException {
		createFindingForFilteredLine(message, element, entity.getStartLine());
	}

	/** Creates a finding taking the position from the entity. */
	protected void createFindingForEntityRegion(String message,
			ITokenElement element, ShallowEntity entity) throws ConQATException {
		createFindingForFilteredOffsets(message, element,
				entity.getStartOffset(), entity.getEndOffset());
	}
}
