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
package org.conqat.engine.sourcecode.analysis;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.logging.IncludeExcludeListLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.regions.RegionMarkerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35204 $
 * @ConQAT.Rating GREEN Hash: 0C726D98F7AA7CE12C12D351B50EFEBD
 */
@AConQATProcessor(description = ""
		+ "Marks regions that form a programming-language-level block for Java-like "
		+ "languages. This processor can e.g. be used to match methods in source code. "
		+ "It's functionality is simple: It uses regular expressions to determine the "
		+ "start of a block. If you want to match a specific method, simply create an "
		+ "according regexp. The regular expression must end on \"\\{\" in order to assure "
		+ "that the last matched character is an opening brace. The processor then "
		+ "determines the closing brace that corresponds to the opening brace, "
		+ "respecting nesting. The region that gets marked starts at the first character "
		+ "matched by the regular expression and ends with the closing brace.")
public class BlockMarker extends
		RegionMarkerBase<ITokenResource, ITokenElement, BlockMarkerStrategy> {

	/** Patterns that match beginning of block */
	private PatternList patterns;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.BLOCK_MARKER_PATTERNS_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.BLOCK_MARKER_PATTERNS_DESC)
	public void setPatterns(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) PatternList patterns)
			throws ConQATException {
		BlockMarkerStrategy.validatePatternFormat(patterns);

		this.patterns = patterns;

		if (!patterns.isEmpty()) {
			getLogger().info(
					new IncludeExcludeListLogMessage("patterns",
							"Element region content", patterns.asStringList(),
							StructuredLogTags.PATTERN));
		}
	}

	/** {@inheritDoc} */
	@Override
	protected BlockMarkerStrategy createStrategy() {
		return new BlockMarkerStrategy();
	}

	/** {@inheritDoc} */
	@Override
	protected void setStrategyParameters(BlockMarkerStrategy strategy)
			throws ConQATException {
		strategy.setPatterns(patterns);
	}

	/** {@inheritDoc} */
	@Override
	protected Class<ITokenElement> getElementClass() {
		return ITokenElement.class;
	}
}