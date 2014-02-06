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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.region.RegionSet;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 44788 $
 * @ConQAT.Rating GREEN Hash: F0FDC81F2CFB08BF6C69D10390633F8A
 */
@AConQATProcessor(description = ""
		+ "Marks regions that form a programming-language-level block for Java-like"
		+ "languages. This strategy can e.g. be used to match methods in source code."
		+ "It's functionality is simple: It uses regular expressions to determine the"
		+ "start of a block. If you want to match a specific method, simply create an"
		+ "according regexp. The regular expression must end on \"\\{\" in order to assure"
		+ "that the last matched character is an opening brace (it can contain other opening braces before the last character). "
		+ "The processor then determines the closing brace that corresponds to the opening brace,"
		+ "respecting nesting. The region that gets marked starts at the first character"
		+ "matched by the regular expression and ends with the closing brace."
		+ "")
public class BlockMarkerStrategy extends
		SourceCodeElementRegionMarkerStrategyBase {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Name of the origin used for created regions */
	private static final String BLOCK_MARKER_REGION_ORIGIN = "BlockMarker";

	/** Patterns that match beginning of block */
	private PatternList patterns;

	/**
	 * {@ConQAT.Doc}
	 * 
	 * Asserts that the pattern matches a block start. More specifically, it
	 * needs to end on an LBRACE, and not contain any other LBRACES.
	 */
	@AConQATParameter(name = ConQATParamDoc.BLOCK_MARKER_PATTERNS_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.BLOCK_MARKER_PATTERNS_DESC)
	public void setPatterns(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) PatternList patterns)
			throws ConQATException {
		validatePatternFormat(patterns);

		this.patterns = patterns;
	}

	/** Make sure that pattern ends with left curly brace */
	public static void validatePatternFormat(PatternList patterns)
			throws ConQATException {
		// validate pattern format
		for (Pattern pattern : patterns) {
			String regex = pattern.toString();
			if (!regex.endsWith("\\{")) {
				throw new ConQATException(
						"Illegal pattern format. Must end with \\{.");
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void findRegionsForElement(ITokenElement element, RegionSet result)
			throws ConQATException {

		// if tokens cannot be retrieved, base class handles exception
		List<IToken> tokens = element.getTokens(getLogger());

		// determine match start positions
		String content = element.getTextContent();

		for (Pattern pattern : patterns) {
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				int blockStart = matcher.start();

				if (isOffsetInComment(blockStart, tokens)) {
					continue;
				}

				int blockEnd = BlockParser.findBlockEnd(blockStart, tokens);

				if (blockEnd == BlockParser.CLOSING_RBRACE_NOT_FOUND) {
					getLogger().warn(
							"Could not find end of block in: "
									+ element.getLocation()
									+ ". Is content correctly nested?");
				} else {
					result.add(new Region(blockStart, blockEnd,
							BLOCK_MARKER_REGION_ORIGIN));
				}
			}
		}

	}

	/** Determines if an offset lies within a comment. */
	private boolean isOffsetInComment(int offset, List<IToken> tokens) {
		for (IToken token : tokens) {
			if (token.getOffset() <= offset && offset <= token.getEndOffset()) {
				if (ETokenType.COMMENTS.contains(token.getType())) {
					return true;
				}
				return false;
			}
		}
		return false;
	}
}