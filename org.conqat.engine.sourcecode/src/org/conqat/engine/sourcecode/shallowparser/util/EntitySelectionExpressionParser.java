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
package org.conqat.engine.sourcecode.shallowparser.util;

import static org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParsingException.EParsingExceptionMessage.EXPECTED_BINARY_OPERATOR;
import static org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParsingException.EParsingExceptionMessage.EXPECTED_EXPRESSION;
import static org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParsingException.EParsingExceptionMessage.MISPLACED_CLOSING_PARENTHESIS;
import static org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParsingException.EParsingExceptionMessage.MISSING_CLOSING_PARENTHESIS;
import static org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParsingException.EParsingExceptionMessage.PARAMETER_MISSING;
import static org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParsingException.EParsingExceptionMessage.PREDICATE_CONSTRUCTION_FAILED;
import static org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParsingException.EParsingExceptionMessage.PREDICATE_NOT_FOUND;
import static org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParsingException.EParsingExceptionMessage.UNEXPECTED_CHARACTER;
import static org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParsingException.EParsingExceptionMessage.UNSUPPORTED_PARAMETER;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParsingException.EParsingExceptionMessage;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.predicate.AndPredicate;
import org.conqat.lib.commons.predicate.IPredicate;
import org.conqat.lib.commons.predicate.InvertingPredicate;
import org.conqat.lib.commons.predicate.OrPredicate;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Parses expressions for selecting {@link ShallowEntity}s from a parse tree.
 * The expressions may be formed by '&', '|', '!', parentheses, and all
 * predicates defined in {@link EntitySelectionPredicates} (i.e. you can use all
 * public methods defined there without respect of case). Predicates may contain
 * additional underscores and dashes to improve readability, so 'simpleGetter',
 * 'simplegetter', 'simple_getter' and 'simple-getter' would all be treated the
 * same.
 * 
 * An example for an expression that matches all public attributes and methods,
 * but no simple setters/getter and no methods annotated with '@Override' would
 * be
 * 
 * <pre>
 *     public & (attribute | method) & !(simple-getter | simple-setter | annotated(override))
 * </pre>
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46381 $
 * @ConQAT.Rating GREEN Hash: 56A11A9825AE0B5A4CE7C9B0801E8FDB
 */
public class EntitySelectionExpressionParser {

	/** The available factory methods by normalized name. */
	private static Map<String, Method> factoryMethods;

	/** The expression to parse. */
	private final String expression;

	/** The current position into {@link #expression} (during parsing). */
	private int position = 0;

	/** Constructor. */
	private EntitySelectionExpressionParser(String expression) {
		if (factoryMethods == null) {
			factoryMethods = loadFactoryMethods();
		}

		this.expression = expression;
	}

	/**
	 * Returns a map of factory methods extracted from
	 * {@link EntitySelectionPredicates}.
	 */
	private static Map<String, Method> loadFactoryMethods() {
		Map<String, Method> methods = new HashMap<String, Method>();
		for (Method method : EntitySelectionPredicates.class
				.getDeclaredMethods()) {
			if (!Modifier.isPublic(method.getModifiers())
					|| !Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			assertParameterTypes(method);

			methods.put(normalizeName(method.getName()), method);
		}
		return methods;
	}

	/**
	 * Raises an assertion error if the method has more then one parameter or
	 * its single parameter is not a string.
	 */
	private static void assertParameterTypes(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		boolean noParameters = parameterTypes.length == 0;
		boolean oneStringParameter = parameterTypes.length == 1
				&& parameterTypes[0] == String.class;
		CCSMAssert.isTrue(
				noParameters || oneStringParameter,
				"Factory methods in "
						+ EntitySelectionPredicates.class.getSimpleName()
						+ " must have no parameters or one string parameter.");
	}

	/** Parses the given expression and returns a corresponding predicate. */
	public static IPredicate<ShallowEntity> parse(String expression)
			throws ConQATException {
		return new EntitySelectionExpressionParser(expression).parse();
	}

	/** Parses the {@link #expression}. */
	private IPredicate<ShallowEntity> parse() throws ConQATException {
		IPredicate<ShallowEntity> predicate = parse(false, true);
		if (position < expression.length()) {
			error(MISPLACED_CLOSING_PARENTHESIS);
		}
		return predicate;
	}

	/**
	 * Parses the {@link #expression}.
	 * 
	 * @param expectClosingParenthesis
	 *            if this is true, the parse was started from an opening
	 *            parenthesis and a closing parenthesis would be expected at the
	 *            end of the local expression.
	 * @param mayParseBinary
	 *            if this is true, parse not only a single term but potentially
	 *            continue with parsing binary operators. This is used to
	 *            implement a very simple operator precedence for '!'.
	 */
	@SuppressWarnings("unchecked")
	private IPredicate<ShallowEntity> parse(boolean expectClosingParenthesis,
			boolean mayParseBinary) throws ConQATException {

		IPredicate<ShallowEntity> result = null;
		while (position < expression.length()) {
			char next = expression.charAt(position++);

			if (Character.isWhitespace(next)) {
				continue;
			}

			if (isIdentifierCharacter(next)) {
				assertNoResult(result);
				position -= 1;
				result = parsePrimitiveExpression();
				if (!mayParseBinary) {
					return result;
				}
				continue;
			}

			switch (next) {
			case '(':
				assertNoResult(result);
				result = parse(true, true);
				break;

			case ')':
				if (!expectClosingParenthesis) {
					position -= 1; // leave for one of the outer calls
				}
				return assertResult(result);

			case '&':
				if (!mayParseBinary) {
					return assertResult(result);
				}
				result = AndPredicate.create(assertResult(result),
						parse(false, true));
				break;

			case '|':
				if (!mayParseBinary) {
					return assertResult(result);
				}
				result = OrPredicate.create(assertResult(result),
						parse(false, true));
				break;

			case '!':
				assertNoResult(result);
				result = InvertingPredicate.create(parse(false, false));
				break;

			default:
				error(UNEXPECTED_CHARACTER);
			}
		}

		if (expectClosingParenthesis) {
			error(MISSING_CLOSING_PARENTHESIS);
		}
		return assertResult(result);
	}

	/** Parses a primitive expression. */
	private IPredicate<ShallowEntity> parsePrimitiveExpression()
			throws ConQATException {
		StringBuilder nameBuilder = new StringBuilder();
		while (position < expression.length()
				&& isIdentifierCharacter(expression.charAt(position))) {
			nameBuilder.append(expression.charAt(position++));
		}

		return createPredicate(normalizeName(nameBuilder.toString()),
				extractParameter());
	}

	/**
	 * Extracts a predicate parameter or returns null if no parameter is
	 * provided.
	 */
	private String extractParameter()
			throws EntitySelectionExpressionParsingException {
		// skip whitespace
		while (position < expression.length()
				&& Character.isWhitespace(expression.charAt(position))) {
			position += 1;
		}

		if (position >= expression.length()
				|| expression.charAt(position) != '(') {
			return null;
		}

		StringBuilder parameterBuilder = new StringBuilder();
		position += 1;
		while (position < expression.length()
				&& expression.charAt(position) != ')') {
			char next = expression.charAt(position);
			parameterBuilder.append(next);
			position += 1;
		}

		if (position >= expression.length()) {
			error(MISSING_CLOSING_PARENTHESIS);
		}
		position += 1;

		if (parameterBuilder.length() == 0) {
			return null;
		}

		return parameterBuilder.toString().replaceAll("['\"]",
				StringUtils.EMPTY_STRING);
	}

	/**
	 * Normalizes the predicate name. This removes the prefix "select", makes
	 * the text lowercase, and discards all underscores and dashes.
	 */
	private static String normalizeName(String name) {
		name = name.toLowerCase();
		name = name.replaceAll("[-_]", StringUtils.EMPTY_STRING);
		name = StringUtils.stripPrefix("select", name);
		return name;
	}

	/**
	 * Returns whether the given character is expected to occur in identifiers,
	 * i.e. is alphanumeric, an underscore, or a dash.
	 */
	private boolean isIdentifierCharacter(char character) {
		return Character.isJavaIdentifierPart(character) || character == '-';
	}

	/**
	 * Creates a predicate of given (normalized) name. This never returns null.
	 * 
	 * @param parameter
	 *            the parameter to the predicate. May be null to indicate a
	 *            parameterless predicate.
	 */
	@SuppressWarnings("unchecked")
	private IPredicate<ShallowEntity> createPredicate(String name,
			String parameter) throws ConQATException {
		Method method = factoryMethods.get(name);

		try {
			if (method == null) {
				error(PREDICATE_NOT_FOUND);
			} else if (method.getParameterTypes().length == 0) {
				if (parameter != null) {
					error(UNSUPPORTED_PARAMETER);
				}
				return (IPredicate<ShallowEntity>) method.invoke(null);
			} else {
				if (parameter == null) {
					error(PARAMETER_MISSING);
				}
				return (IPredicate<ShallowEntity>) method.invoke(null,
						parameter);
			}
		} catch (IllegalAccessException e) {
			error(PREDICATE_CONSTRUCTION_FAILED, e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof ConQATException) {
				throw (ConQATException) e.getCause();
			}
			error(PREDICATE_CONSTRUCTION_FAILED, e.getCause());
		}
		throw new AssertionError("This line should not be reachable!");
	}

	/**
	 * Asserts that the result is still null, i.e. there has been no previous
	 * expression at this level. The error message hence reports a missing
	 * binary operator.
	 */
	private void assertNoResult(IPredicate<ShallowEntity> result)
			throws EntitySelectionExpressionParsingException {
		if (result != null) {
			error(EXPECTED_BINARY_OPERATOR);
		}
	}

	/**
	 * Asserts that the result is not null, i.e. there has been a previous
	 * expression at this level. The error message hence reports a missing
	 * previous expression. Returns the parameter for convenience.
	 */
	private IPredicate<ShallowEntity> assertResult(
			IPredicate<ShallowEntity> result)
			throws EntitySelectionExpressionParsingException {
		if (result == null) {
			error(EXPECTED_EXPRESSION);
		}
		return result;
	}

	/**
	 * Throws an exception with given message and details on the current parsing
	 * position.
	 */
	private void error(EParsingExceptionMessage messageIdentifier)
			throws EntitySelectionExpressionParsingException {
		throw new EntitySelectionExpressionParsingException(messageIdentifier,
				expression, position);
	}

	/**
	 * Throws an exception with given message and details on the current parsing
	 * position.
	 */
	private void error(EParsingExceptionMessage messageIdentifier,
			Throwable cause) throws EntitySelectionExpressionParsingException {
		throw new EntitySelectionExpressionParsingException(messageIdentifier,
				expression, position, cause);
	}
}
