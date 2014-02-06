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

import java.util.List;

import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.BlockParser.BlockParserException;
import org.conqat.engine.sourcecode.analysis.findings.BlockParserBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 36006 $
 * @ConQAT.Rating GREEN Hash: AC4CBC63E0564CE3A821B38EF29E30A5
 */
@AConQATProcessor(description = "Calculates the length of the longest method for each class.")
public class LongestMethodLengthAnalyzer extends BlockParserBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Longest Method Length in Class", type = "java.lang.Integer")
	public static final String LONGEST_METHOD_KEY = NodeConstants.LONGEST_METHOD_KEY;

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Start line of the longest method in the element", type = "java.lang.Integer")
	public static final String LML_LINE_KEY = "LML_LINE";

	/** {@inheritDoc} */
	@Override
	protected void analyzeTokens(List<IToken> tokens, ITokenElement element)
			throws ConQATException {
		List<Block> methodBlocks;
		try {
			methodBlocks = parseBlocks(tokens);
		} catch (BlockParserException e) {
			getLogger().warn(
					"Ill-formed nesting encountered in "
							+ element.getLocation());
			return;
		}

		int longestMethodLength = 0;
		int longestMethodLine = 0;

		for (int blockIndex = 0; blockIndex < methodBlocks.size(); blockIndex++) {
			Block block = methodBlocks.get(blockIndex);
			int endLine = block.getLast().getLineNumber();
			int startLine = block.getFirst().getLineNumber();
			int length = endLine - startLine + 1;

			if (length > longestMethodLength) {
				longestMethodLength = length;
				longestMethodLine = startLine;
			}
		}

		longestMethodLine += 1; // line numbers are 0 based
		element.setValue(LONGEST_METHOD_KEY, longestMethodLength);
		element.setValue(LML_LINE_KEY, longestMethodLine);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { LONGEST_METHOD_KEY, LML_LINE_KEY };
	}

}