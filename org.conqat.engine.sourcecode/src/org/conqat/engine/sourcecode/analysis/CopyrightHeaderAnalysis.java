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
package org.conqat.engine.sourcecode.analysis;

import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.StringSetNode;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementUtils;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44783 $
 * @ConQAT.Rating YELLOW Hash: FD4AD7DF3DEC9BD677A8321EB3BA1804
 */
@AConQATProcessor(description = "Processor for performing copyright header analysis by grouping files by their header. ")
public class CopyrightHeaderAnalysis extends
		ConQATInputProcessorBase<ITokenResource> {

	/** Comment text used if not header was found. */
	private static final String NO_HEADER = "<no header>";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The normalized content of the header.", type = "java.lang.String")
	public static final String COPYRIGHT_HEADER_KEY = "Copyright Header";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "match", attribute = "patterns", description = ""
			+ "Only comments matching one of the patterns are considered a copyright. "
			+ "Note that this required a full match, i.e. pad the pattern with '.*' if only keywords are provided.")
	public PatternList matchPatterns;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "normalization", attribute = "ref", optional = true, description = ""
			+ "Transformation applied to normalize the comment's content.")
	public PatternTransformationList commentNormalization;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "lowercase-header", attribute = "value", optional = true, description = ""
			+ "If this is true, the header comment is lower-cased after the normalization was applied. Default is true.")
	public boolean lowercaseHeader = true;

	/** Stores elements by normalized */
	private final ListMap<String, ITokenElement> elementsByHeader = new ListMap<String, ITokenElement>();

	/** {@inheritDoc} */
	@Override
	public IConQATNode process() throws ConQATException {

		DisplayList inputDisplayList = NodeUtils.getDisplayList(input);

		for (ITokenElement element : TokenElementUtils.listTokenElements(input)) {
			elementsByHeader.add(getNormalizedHeader(element), element);
		}

		return createResult(inputDisplayList);
	}

	/**
	 * Returns the normalized header comment for the given element. Returns
	 * {@link #NO_HEADER} if no header is found.
	 */
	private String getNormalizedHeader(ITokenElement element)
			throws ConQATException {
		StringBuilder singleLineCommentContent = new StringBuilder();
		for (IToken token : element.getTokens(getLogger())) {
			if (token.getType() == ETokenType.END_OF_LINE_COMMENT) {
				singleLineCommentContent.append(token.getText());
			} else {
				if (singleLineCommentContent.length() > 0) {
					if (isCopyrightHeader(singleLineCommentContent.toString())) {
						return normalizeCommentText(singleLineCommentContent
								.toString());
					}
					singleLineCommentContent.setLength(0);
				}

				if (token.getType().getTokenClass() == ETokenClass.COMMENT
						&& isCopyrightHeader(token.getText())) {
					return normalizeCommentText(token.getText());
				}
			}
		}

		return NO_HEADER;
	}

	/** Returns whether the given text is considered a copyright header comment. */
	private boolean isCopyrightHeader(String text) {
		return matchPatterns.matchesAny(text);
	}

	/** Returns the normalized version of the given header text. */
	private String normalizeCommentText(String text) {
		text = commentNormalization.applyTransformation(text);
		if (lowercaseHeader) {
			text = text.toLowerCase();
		}
		return text;
	}

	/** Creates the result representation from the {@link #elementsByHeader}. */
	private StringSetNode createResult(DisplayList inputDisplayList)
			throws ConQATException {
		StringSetNode root = new StringSetNode("<root>");
		NodeUtils.setHideRoot(root, true);
		NodeUtils.addToDisplayList(root, COPYRIGHT_HEADER_KEY);
		NodeUtils.getDisplayList(root).addAll(inputDisplayList);

		int count = 0;
		for (String key : CollectionUtils.sort(elementsByHeader.getKeys())) {
			String headerId = String.format("header%06d", count++);
			StringSetNode headerNode = new StringSetNode(headerId);
			headerNode.setValue(COPYRIGHT_HEADER_KEY, key);
			root.addChild(headerNode);

			for (ITokenElement element : elementsByHeader.getCollection(key)) {
				StringSetNode elementNode = new StringSetNode(headerId + ":"
						+ element.getUniformPath(), element.getUniformPath());
				NodeUtils.copyValues(inputDisplayList.getKeyList(), element,
						elementNode);
				headerNode.addChild(elementNode);
			}
		}
		return root;
	}
}
