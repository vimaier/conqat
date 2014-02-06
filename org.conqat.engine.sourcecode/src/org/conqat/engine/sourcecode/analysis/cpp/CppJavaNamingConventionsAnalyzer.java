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
package org.conqat.engine.sourcecode.analysis.cpp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.TokenStreamUtils;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.lib.commons.predicate.IPredicate;
import org.conqat.lib.commons.predicate.PredicateUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQATDoc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43899 $
 * @ConQAT.Rating GREEN Hash: ACDD104B2E44CEFDB8C95E5062BB7B72
 */
@AConQATProcessor(description = "Creates Findings for identifiers not adhering"
		+ " to the Java Naming Conventions.")
public class CppJavaNamingConventionsAnalyzer extends CppFindingAnalyzerBase {

	/** Variable identifiers are in mixed case with a lowercase first letter. */
	private static final String VARIABLE_PATTERN = "[a-z][a-zA-Z0-9]*";

	/**
	 * The names of constants should be all uppercase with words separated by
	 * underscores ("_").
	 */
	private static final String CONST_PATTERN = "[A-Z](_?[A-Z0-9]+)*";

	/**
	 * Class names should be in mixed case with the first letter of each word
	 * capitalized.
	 */
	private static final String CLASS_PATTERN = "[A-Z][a-zA-Z0-9]*";

	/**
	 * Methods should be in mixed case with the first letter lowercase, with the
	 * first letter of each internal word capitalized.
	 */
	private static final String METHOD_PATTERN = VARIABLE_PATTERN;

	/** Namespaces are all lower case. */
	private static final String NAMESPACE_PATTERN = "[a-z0-9_]+(::[a-z0-9_]+)*";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "idm-method-names", attribute = "ignore", optional = true, description = "Ignores violations caused by method names enforced by the IDM (ISA Dialog Manager). Default is not to ignore.")
	public boolean ignoreIDMMethodNames = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "basics-method-names", attribute = "ignore", optional = true, description = "Ignores violations caused by method names enforced by BASICS. Default is not to ignore.")
	public boolean ignoreBASICSMethodNames = false;

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException {
		List<ShallowEntity> attributes = CppUtils.listAttributes(entities);
		List<ShallowEntity> constants = CppUtils.listConstants(attributes);
		attributes.removeAll(constants);
		List<ShallowEntity> methods = ShallowEntityTraversalUtils
				.listEntitiesOfType(entities, EShallowEntityType.METHOD);
		methods = identifyMethodsWhichAreInFactConstants(methods, constants);

		analyzeShallowEntities(element, attributes, VARIABLE_PATTERN);

		analyzeShallowEntities(element, constants, CONST_PATTERN);

		analyzeShallowEntities(element,
				CppUtils.listClassesAndStructs(entities), CLASS_PATTERN);

		analyzeMethodEntities(element, methods);

		analyzeShallowEntities(element, CppUtils.listNamespaces(entities),
				NAMESPACE_PATTERN);
	}

	/**
	 * This method goes through the list of given methods and identifies those
	 * entities which have been parsed as a method, but are in fact constants.
	 * This problem cannot be fixed easily in the shallow parser and therefore,
	 * this post-processing is done here, because the difference is relevant for
	 * checking the naming conventions.
	 * 
	 * The returned list contains those method-entities which are truly methods,
	 * whereas the methods which are in fact constants are added to the given
	 * list of constants.
	 */
	private List<ShallowEntity> identifyMethodsWhichAreInFactConstants(
			List<ShallowEntity> methods, List<ShallowEntity> constants) {
		List<ShallowEntity> result = new ArrayList<ShallowEntity>();
		for (ShallowEntity method : methods) {
			if (isConstant(method)) {
				constants.add(method);
			} else {
				result.add(method);
			}
		}
		return result;
	}

	/**
	 * Checks whether the given entity which has been parsed as a method is in
	 * fact a constant. The heuristic looks at the tokens between the name of
	 * the entity and the first semicolon or opening brace.
	 */
	private boolean isConstant(ShallowEntity entity) {

		// If the entity has children, this has to be a method.
		if (!entity.getChildren().isEmpty()) {
			return false;
		}

		boolean nameSeen = false;
		List<IToken> tokens = new ArrayList<IToken>();
		for (IToken token : entity.includedTokens()) {
			if (nameSeen) {
				if (token.getType() == ETokenType.SEMICOLON
						|| token.getType() == ETokenType.LBRACE) {
					break;
				}
				tokens.add(token);
			} else if (token.getText().equals(entity.getName())) {
				nameSeen = true;
			}
		}

		// If there are only parentheses, there are no hints available and we
		// decide for function, although it could be both.
		if (containsOnlyParentheses(tokens)) {
			return false;
		}

		// If the parameter list contains a literal this must be a constant
		// initialization. Assignments of literals probably indicate default
		// arguments, hence a method declaration.
		if (!TokenStreamUtils.tokenStreamContains(tokens, ETokenType.EQ)
				&& TokenStreamUtils.tokenStreamContains(tokens,
						ETokenClass.LITERAL)) {
			return true;
		}

		// If the parameter list contains a keyword this is probably a function.
		// It could still be a constant if the keyword is, e.g., a cast, but
		// this case is not covered.
		if (TokenStreamUtils.tokenStreamContains(tokens, ETokenClass.KEYWORD)) {
			return false;
		}

		// If there is only a single token between each pair of commas
		// (disregarding dereferences, address-operators, and parentheses) this
		// is likely to be a constant initialization.
		if (!containsMutlipleTokensBetweenCommas(tokens)) {
			return true;
		}

		return false;
	}

	/** Checks if the given list of tokens contains only parentheses. */
	private boolean containsOnlyParentheses(List<IToken> tokens) {
		for (IToken token : tokens) {
			if (token.getType() != ETokenType.LPAREN
					&& token.getType() != ETokenType.RPAREN) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether there are more than two tokens between any pair of commas.
	 * Dereferences, address-operators, and parentheses are ignored.
	 */
	private boolean containsMutlipleTokensBetweenCommas(List<IToken> tokens) {
		boolean tokenSeenSinceLastComma = false;
		for (IToken token : tokens) {
			ETokenType type = token.getType();
			if (type == ETokenType.LPAREN || type == ETokenType.RPAREN
					|| type == ETokenType.MULT || type == ETokenType.AND) {
				continue;
			} else if (type == ETokenType.COMMA) {
				tokenSeenSinceLastComma = false;
			} else if (tokenSeenSinceLastComma) {
				return true;
			} else {
				tokenSeenSinceLastComma = true;
			}
		}
		return false;
	}

	/**
	 * Checks the names of all entities from the given list of entities which
	 * are of type 'METHOD'.
	 */
	private void analyzeMethodEntities(ITokenElement element,
			List<ShallowEntity> methods) throws ConQATException {

		if (ignoreIDMMethodNames) {
			methods = removeIDMEntities(methods);
		}

		if (ignoreBASICSMethodNames) {
			methods = removeBASICSEntities(methods);
		}

		methods = removeOverloadedOperators(methods);

		analyzeShallowEntities(element, methods, METHOD_PATTERN, "constructor",
				"destructor");
	}

	/**
	 * Removes all METHOD entities which are overloaded operators
	 */
	private List<ShallowEntity> removeOverloadedOperators(
			List<ShallowEntity> methods) {
		return PredicateUtils.obtainNonContained(methods,
				new IPredicate<ShallowEntity>() {
					@Override
					public boolean isContained(ShallowEntity entity) {
						return isOverloadedOperator(entity);
					}
				});
	}

	/**
	 * Returns true if the given method is a overloaded operator
	 */
	private static boolean isOverloadedOperator(ShallowEntity method) {
		return method.getSubtype().equals("operator");
	}

	/** Removes all METHOD entities whose names are enforced by IDM. */
	private static List<ShallowEntity> removeIDMEntities(
			List<ShallowEntity> methods) {
		return PredicateUtils.obtainNonContained(methods,
				new IPredicate<ShallowEntity>() {
					@Override
					public boolean isContained(ShallowEntity entity) {
						return nameIsEnforcedByIDM(entity);
					}
				});
	}

	/**
	 * Returns true if the method's name is enforced by the IDM. The check is
	 * positive if the return type starts with "DM_". The heuristic used checks
	 * all identifiers before the method's name to see whether any of them
	 * starts with "DM_".
	 */
	private static boolean nameIsEnforcedByIDM(ShallowEntity method) {
		for (IToken token : method.includedTokens()) {
			if (method.getName().startsWith(token.getText())) {
				return false;
			}
			if (token.getType() == ETokenType.IDENTIFIER
					&& token.getText().startsWith("DM_")) {
				return true;
			}
		}
		return false;
	}

	/** Removes all METHOD entities whose names are enforced by BASICS. */
	private static List<ShallowEntity> removeBASICSEntities(
			List<ShallowEntity> methods) {
		return PredicateUtils.obtainNonContained(methods,
				new IPredicate<ShallowEntity>() {
					@Override
					public boolean isContained(ShallowEntity entity) {
						return nameIsEnforcedByBASICS(entity);
					}
				});
	}

	/**
	 * Removes all METHOD entities whose names are enforced by BASICS. The check
	 * is positive if any parameter is named 'data'. The heuristic used checks
	 * all identifiers between the method's name and the first semicolon or
	 * opening brace for equality with 'data'.
	 */
	private static boolean nameIsEnforcedByBASICS(ShallowEntity method) {
		boolean parameterListStarted = false;
		for (IToken token : method.includedTokens()) {
			if (parameterListStarted) {
				if (token.getType() == ETokenType.SEMICOLON
						|| token.getType() == ETokenType.LBRACE) {
					return false;
				}
				if (token.getType() == ETokenType.IDENTIFIER
						&& token.getText().equals("data")) {
					return true;
				}
			} else if (token.getType().equals(ETokenType.LPAREN)) {
				parameterListStarted = true;
			}
		}
		return false;
	}

	/**
	 * Checks entities against pattern and creates a finding for each entity
	 * that doesn't match.
	 */
	private void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities, String pattern,
			String... excludedSubtypePrefixes) throws ConQATException {
		for (ShallowEntity entity : entities) {
			if (StringUtils.startsWithOneOf(entity.getSubtype(),
					excludedSubtypePrefixes)) {
				continue;
			}

			if (!Pattern.matches(pattern, entity.getName())) {
				createFindingForEntityStart(
						"Identifier violates Java Naming Conventions", element,
						entity);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Java Naming Conventions violations";
	}
}
