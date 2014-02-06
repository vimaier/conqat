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
package org.conqat.engine.cpp.preprocessor.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Parser for preprocessor conditionals. This does not (yet) deal with macros
 * (#define) and includes.
 * 
 * @author $Author: feilkas $
 * @version $Rev: 41746 $
 * @ConQAT.Rating GREEN Hash: F8EC1AFB42F3CC55D0E678E45EDE2171
 */
public class PreprocessorParser {

	/** Pattern for the #if/#if(n)def. */
	private static final Pattern IF_PATTERN = Pattern
			.compile("(?is)^#\\s*if(?:n?def)?((\\s|[(]).*)$");

	/** Pattern for the #elif. */
	private static final Pattern ELIF_PATTERN = Pattern
			.compile("(?is)^#\\s*elif((\\s|[(]).*)$");

	/** Pattern for the #if/#if(n)def/#elif. First group matches condition. */
	/* package */static final Pattern IF_OR_ELIF_PATTERN = Pattern
			.compile("(?is)^#\\s*(?:el)?if(?:n?def)?((\\s|[(]).*)$");

	/** Pattern for the #else. */
	private static final Pattern ELSE_PATTERN = Pattern
			.compile("(?is)^#\\s*else.*");

	/** Pattern for the #endif. */
	private static final Pattern ENDIF_PATTERN = Pattern
			.compile("(?is)^#\\s*endif.*");

	/** The result list. */
	private final List<PreprocessorNodeBase> result = new ArrayList<PreprocessorNodeBase>();

	/** Stack of currently open conditions. */
	private final Stack<PreprocessorConditionNode> openCondition = new Stack<PreprocessorConditionNode>();

	/** Constructor. */
	private PreprocessorParser() {
		// prevent instantiation
	}

	/**
	 * Performs the actual parsing. This should be only called once for a newly
	 * constructed instance.
	 */
	private List<PreprocessorNodeBase> doParse(List<IToken> tokens)
			throws ConQATException {
		for (IToken token : tokens) {
			if (token.getType() != ETokenType.PREPROCESSOR_DIRECTIVE) {
				insertToken(token);
			} else if (isIfToken(token)) {
				PreprocessorConditionNode condition = new PreprocessorConditionNode(
						token);
				// important: insert first, then push, as we might insert to the
				// head of the stack
				insertCondition(condition);
				openCondition.push(condition);
			} else if (isElifToken(token) || isElseToken(token)) {
				if (openCondition.isEmpty()) {
					throw new ConQATException(
							"Found #elif/#else without previous #if* in line "
									+ token.getLineNumber());
				}
				openCondition.peek().addBranch(token);
			} else if (isEndifToken(token)) {
				if (openCondition.isEmpty()) {
					throw new ConQATException(
							"Found #endif without previous #if* in line "
									+ token.getLineNumber());
				}
				openCondition.pop().close(token);
			} else {
				// handle #define, #error, etc. just as normal tokens
				insertToken(token);
			}
		}

		if (!openCondition.isEmpty()) {
			throw new ConQATException("There are unclosed conditionals!");
		}

		return result;
	}

	/** Returns if the given pattern is an #if, #ifdef, or #ifndef token. */
	public static boolean isIfToken(IToken token) {
		return IF_PATTERN.matcher(token.getText()).matches();
	}

	/** Returns if the given pattern is an #else token. */
	public static boolean isElseToken(IToken token) {
		return ELSE_PATTERN.matcher(token.getText()).matches();
	}

	/** Returns if the given pattern is an #elif token. */
	public static boolean isElifToken(IToken token) {
		return ELIF_PATTERN.matcher(token.getText()).matches();
	}

	/** Returns if the given pattern is an #endif token. */
	public static boolean isEndifToken(IToken token) {
		return ENDIF_PATTERN.matcher(token.getText()).matches();
	}

	/**
	 * Inserts a new condition node into the currently open condition branch or
	 * the result list.
	 */
	private void insertCondition(PreprocessorConditionNode node) {
		if (openCondition.isEmpty()) {
			result.add(node);
		} else {
			openCondition.peek().getLastBranch().containedCode.add(node);
		}
	}

	/**
	 * Inserts a token into the currently open condition branch or the result
	 * list.
	 */
	private void insertToken(IToken token) {
		if (openCondition.isEmpty()) {
			insertIntoNodeList(token, result);
		} else {
			insertIntoNodeList(token,
					openCondition.peek().getLastBranch().containedCode);
		}
	}

	/** Inserts a non-preprocessor token into a list of nodes. */
	private void insertIntoNodeList(IToken token,
			List<PreprocessorNodeBase> nodeList) {
		PreprocessorNodeBase lastNode = null;
		if (!nodeList.isEmpty()) {
			lastNode = CollectionUtils.getLast(nodeList);
		}

		if (!(lastNode instanceof TokenSequencePreprocessorNode)) {
			lastNode = new TokenSequencePreprocessorNode();
			nodeList.add(lastNode);
		}
		((TokenSequencePreprocessorNode) lastNode).addToken(token);
	}

	/** Parses the given element. */
	public static List<PreprocessorNodeBase> parse(ITokenElement element,
			IConQATLogger logger) throws ConQATException {
		return parse(element.getTokens(logger));
	}

	/** Parses the given tokens. */
	private static List<PreprocessorNodeBase> parse(List<IToken> tokens)
			throws ConQATException {
		return new PreprocessorParser().doParse(tokens);
	}
}
