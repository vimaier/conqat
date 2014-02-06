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
package org.conqat.engine.sourcecode.analysis.findings;

import java.util.List;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 41726 $
 * @ConQAT.Rating GREEN Hash: CCCEE131514EF3FE3315D1DE4BF34DCD
 */
@AConQATProcessor(description = "Creates findings for blocks that are longer than a threshold.")
public class BlockLengthAnalyzer extends BlockAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected int analyzeBlock(List<IToken> blockTokens, ITokenElement element)
			throws ConQATException {
		int firstLine = CollectionUtils.getLast(blockTokens).getLineNumber();
		int lastLine = blockTokens.get(0).getLineNumber();
		int length = firstLine - lastLine + 1;

		if (length > thresholdYellow) {
			createFinding(element, blockTokens, blockType + " has length "
					+ length, assessmentFor(length));
		}

		return length;
	}

	/** {@inheritDoc} */
	@Override
	protected String groupName() {
		return blockType + " too long";
	}
}