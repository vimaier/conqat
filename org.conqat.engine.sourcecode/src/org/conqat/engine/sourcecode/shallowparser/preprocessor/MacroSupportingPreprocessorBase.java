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
package org.conqat.engine.sourcecode.shallowparser.preprocessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Base class of a preprocessor that supports C-style macros (i.e. identifiers
 * followed by parentheses).
 * 
 * @author $Author: goede $
 * @version $Rev: 43298 $
 * @ConQAT.Rating GREEN Hash: 4805FF8690F3A3860721060BB3F49FBB
 */
public class MacroSupportingPreprocessorBase extends ParserPreprocessorBase {

	/** Pattern for the #if/#ifdef. */
	private static final Pattern IF_PATTERN = Pattern
			.compile("(?is)^#\\s*if(?:def)?((\\s|[(])+.*)$");

	/** Pattern for the #ifndef. */
	private static final Pattern IF_NOT_PATTERN = Pattern
			.compile("(?is)^#\\s*ifndef((\\s|[(])+.*)$");

	/** Pattern for the #elif. */
	private static final Pattern ELSEIF_PATTERN = Pattern
			.compile("(?is)^#\\s*elif((\\s|[(])+.*)$");

	/** Pattern for the #else. */
	private static final Pattern ELSE_PATTERN = Pattern
			.compile("(?is)^#\\s*else.*");

	/** Pattern for the #endif. */
	private static final Pattern ENDIF_PATTERN = Pattern
			.compile("(?is)^#\\s*endif.*");

	/**
	 * Names of identifiers introducing a function macro, i.e. an identifier
	 * followed by tokens delimited by (possibly nested) parentheses. Such
	 * function macros are collapsed into a single identifier.
	 */
	private final Set<String> collapseMacros = new HashSet<String>();

	/**
	 * Names of identifiers introducing a group delimited by (possibly nested)
	 * parentheses. Such groups are discarded.
	 */
	private final Set<String> discardMacros = new HashSet<String>();

	/** Preprocessor conditions that are assumed to be true. */
	private final Set<String> trueConditions = new HashSet<String>();

	/** Preprocessor conditions that are assumed to be false. */
	private final Set<String> falseConditions = new HashSet<String>();

	/** Used for counting nested parentheses. */
	private int nesting = 0;

	/** Stores whether we currently are in a collapse group. */
	private boolean inGroup = false;

	/** Stores whether we currently are in a discard group. */
	private boolean inDiscard = false;

	/** Adds a macro to be collapsed. */
	public void addCollapseMacro(String name) {
		collapseMacros.add(name);
	}

	/** Adds a macro to be completely discarded. */
	protected void addDiscardMacro(String name) {
		discardMacros.add(name);
	}

	/** Adds a preprocessor condition assumed to be true. */
	protected void addTrueCondition(String condition) {
		trueConditions.add(normalizeCondition(condition));
	}

	/** Adds a preprocessor condition assumed to be false. */
	protected void addFalseCondition(String condition) {
		falseConditions.add(normalizeCondition(condition));
	}

	/** Normalizes a condition by discarding all white space. */
	private String normalizeCondition(String condition) {
		// Do not inline this method to allow more normalizations later on
		return StringUtils.removeWhitespace(condition);
	}

	/** {@inheritDoc} */
	@Override
	public List<IToken> preprocess(List<IToken> tokens) throws ConQATException {
		if (!trueConditions.isEmpty() || !falseConditions.isEmpty()) {
			tokens = resolvePreprocessorIf(tokens);
		}

		return super.preprocess(tokens);
	}

	/** Recursively resolves preprocessor if directives. */
	private List<IToken> resolvePreprocessorIf(List<IToken> tokens) {
		List<IToken> result = new ArrayList<IToken>();
		Stack<EIfState> states = new Stack<EIfState>();

		for (IToken token : tokens) {
			if (token.getType() == ETokenType.PREPROCESSOR_DIRECTIVE) {
				processPreprocessorDirective(token, states, result);
			} else if (states.isEmpty() || states.peek().keepTokens()) {
				result.add(token);
			}
		}
		return result;
	}

	/** Processes a preprocessor directive token. */
	private void processPreprocessorDirective(IToken token,
			Stack<EIfState> states, List<IToken> result) {
		String text = token.getText();
		Matcher ifMatcher = IF_PATTERN.matcher(text);
		Matcher ifNotMatcher = IF_NOT_PATTERN.matcher(text);
		Matcher elseifMatcher = ELSEIF_PATTERN.matcher(text);

		if (ifMatcher.matches()) {
			handleIf(token, normalizeCondition(ifMatcher.group(1)), states,
					result, false);
		} else if (ifNotMatcher.matches()) {
			handleIf(token, normalizeCondition(ifNotMatcher.group(1)), states,
					result, true);
		} else if (states.isEmpty()) {
			result.add(token);
		} else if (elseifMatcher.matches()) {
			handleElseIf(token, normalizeCondition(elseifMatcher.group(1)),
					states, result);
		} else if (ELSE_PATTERN.matcher(text).matches()) {
			handleElse(token, states, result);
		} else if (ENDIF_PATTERN.matcher(text).matches()) {
			// the closing #endif should only be kept in state PRESERVE, as in
			// state TRUE the initial #if was also not shown
			if (states.pop() == EIfState.PRESERVE) {
				result.add(token);
			}
		} else if (states.peek().keepTokens()) {
			result.add(token);
		}
	}

	/** Handles the if-case. */
	private void handleIf(IToken token, String condition,
			Stack<EIfState> states, List<IToken> result, boolean inverted) {
		if (!states.isEmpty() && !states.peek().keepTokens()) {
			// if we are already in a disabled region, discard the tokens
			states.push(EIfState.DISCARD);
		} else if (inverted ^ trueConditions.contains(condition)) {
			states.push(EIfState.TRUE);
		} else if (inverted ^ falseConditions.contains(condition)) {
			states.push(EIfState.WAIT_FOR_TRUE);
		} else {
			states.push(EIfState.PRESERVE);
			result.add(token);
		}
	}

	/** Handles the elif-case. May only be called with non-empty state. */
	private void handleElseIf(IToken token, String condition,
			Stack<EIfState> states, List<IToken> result) {
		CCSMAssert.isFalse(states.isEmpty(),
				"May not be called with empty state!");
		if (states.peek() == EIfState.WAIT_FOR_TRUE) {
			if (trueConditions.contains(condition)) {
				states.pop();
				states.push(EIfState.TRUE);
			}
		} else {
			handleCommonElseIfAndElseCases(token, states, result);
		}
	}

	/** Handles the else-case. May only be called with non-empty state. */
	private void handleElse(IToken token, Stack<EIfState> states,
			List<IToken> result) {
		CCSMAssert.isFalse(states.isEmpty(),
				"May not be called with empty state!");
		if (states.peek() == EIfState.WAIT_FOR_TRUE) {
			states.pop();
			states.push(EIfState.TRUE);
		} else {
			handleCommonElseIfAndElseCases(token, states, result);
		}
	}

	/** Handles the commons cases from #elif and #else */
	private void handleCommonElseIfAndElseCases(IToken token,
			Stack<EIfState> states, List<IToken> result) {
		switch (states.peek()) {
		case DISCARD:
			break;
		case PRESERVE:
			result.add(token);
			break;
		case TRUE:
			states.pop();
			states.push(EIfState.DISCARD);
			break;
		default:
			CCSMAssert
					.fail("This is no common case and should have been handled by the caller!");
		}
	}

	/** {@inheritDoc} */
	@Override
	protected ETokenAction processToken(IToken token) {
		if (inGroup || inDiscard) {
			if (token.getType() == ETokenType.LPAREN) {
				nesting += 1;
			} else if (token.getType() == ETokenType.RPAREN) {
				nesting -= 1;
				if (nesting == 0) {
					if (inGroup) {
						inGroup = false;
						return ETokenAction.END_COLLAPSE;
					}
					inDiscard = false;
					return ETokenAction.DISCARD;
				}
			}

			// DISCARD while in any group to prevent duplicate token appearance
			return ETokenAction.DISCARD;
		}

		if (token.getType() == ETokenType.IDENTIFIER) {
			if (discardMacros.contains(token.getText())) {
				nesting = 0;
				inDiscard = true;
				return ETokenAction.DISCARD;
			}

			if (collapseMacros.contains(token.getText())) {
				nesting = 0;
				inGroup = true;
				return ETokenAction.START_COLLAPSE;
			}
		}

		return ETokenAction.KEEP;
	}

	/** Enumeration of parsing states for the simple preprocessor parser. */
	private static enum EIfState {

		/**
		 * We are in a true branch, i.e., all tokens should be kept. Any
		 * following elif/else should have its contents discarded.
		 */
		TRUE,

		/**
		 * We are in a false branch, but true might still come (as else or
		 * elif).
		 */
		WAIT_FOR_TRUE,

		/**
		 * We are in "discard everything" mode. For example the else after the
		 * TRUE mode. Different from WAIT_FOR_TRUE, this state is not left for
		 * the rest of the if.
		 */
		DISCARD,

		/**
		 * Keep all tokens including the preprocessor directives (e.g. for "if"
		 * where we have neither a true nor a false condition configured).
		 */
		PRESERVE;

		/** Returns whether in this state tokens should be kept. */
		public boolean keepTokens() {
			return this == TRUE || this == PRESERVE;
		}
	}
}
