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
package org.conqat.engine.sourcecode.pattern;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.IntList;

/**
 * A pattern that matches sequences of elements based on a mapping to
 * enumeration literals. It is implemented using {@link Pattern} by mapping the
 * enumeration literals onto characters. In the regular expression, the literals
 * are referred to by "<LITERAL>". The pattern is also able to match the content
 * of an element by using the following syntax: "<LITERAL=pattern>".
 * 
 * @param <D>
 *            Type of element which this pattern matches.
 * @param <E>
 *            Type of enumeration based on which the pattern is specified.
 * 
 * @author herrmama
 * @author $Author: juergens $
 * @version $Rev: 35204 $
 * @ConQAT.Rating GREEN Hash: FC22464A8C955EB7248A311DC63FCB34
 */
public abstract class EnumPattern<D, E extends Enum<E>> {

	/** Character to denote the starting of a literal */
	private static final char LITERAL_START = '<';

	/** Character to denote the ending of a literal */
	private static final char LITERAL_END = '>';

	/** Character to delimit the content pattern from the literal name */
	private static final char CONTENT_PATTERN_DELIMITER = '=';

	/** Character to denote the starting of content */
	private static final char CONTENT_START = LITERAL_START;

	/** Character to denote the ending of content */
	private static final char CONTENT_END = LITERAL_END;

	/** Character to denote the starting of a capturing group */
	private static final char GROUP_START = '(';

	/** Character to denote the ending of a capturing group */
	private static final char GROUP_END = ')';

	/** Suffix to match a character sequence reluctantly */
	private static final String RELUCTANT_SEQUENCE = "*?";

	/** Wildcard character. */
	private static final String WILDCARD = ".";

	/** Wildcard character that does not match the delimiter of content strings. */
	private static final String RESTRICTED_WILDCARD = "[^" + CONTENT_END + "]";

	/**
	 * Replacement for literals not used in the pattern. All other replacement
	 * characters are derived by incrementing a variable starting from this
	 * value. Thereby, it is ensured that OTHER is different from all
	 * replacement characters.
	 */
	private static final char OTHER = 200;

	/** Pattern with literals replaced by characters */
	private Pattern stringPattern;

	/** Mapping from literals to replacement characters */
	private final Map<Enum<E>, Character> replacements = new HashMap<Enum<E>, Character>();

	/** The type of the enumeration underlying this pattern. */
	private final Class<E> enumType;

	/** The regular expression on which this pattern was compiled */
	private final String expression;

	/** The literals whose content needs to be matched. */
	private final Set<Enum<E>> contentMatched = new HashSet<Enum<E>>();

	/**
	 * Constructor.
	 * 
	 * @param enumType
	 *            The enumeration
	 * @param expression
	 *            The regular expression
	 */
	public EnumPattern(Class<E> enumType, String expression)
			throws ConQATException {
		this.expression = expression;
		this.enumType = enumType;

		init(expression);
	}

	/** Initialize the string pattern based on the enumeration pattern. */
	private void init(String expression) throws ConQATException {
		Pattern literalTermPattern = Pattern.compile(LITERAL_START + WILDCARD
				+ RELUCTANT_SEQUENCE + LITERAL_END);
		Matcher literalTermMatcher = literalTermPattern.matcher(expression);
		initContentMatched(literalTermMatcher);
		buildStringPattern(literalTermMatcher);
	}

	/**
	 * Find the literals whose content is matched in the pattern.
	 * 
	 * For instance, "<LITERAL=pattern>" defines the content pattern "pattern",
	 * whereas "<LITERAL>" does not define a content pattern.
	 */
	private void initContentMatched(Matcher literalTermMatcher)
			throws ConQATException {
		while (literalTermMatcher.find()) {
			String literalTerm = getLiteralTerm(literalTermMatcher);
			int index = literalTerm.indexOf(CONTENT_PATTERN_DELIMITER);
			if (index >= 0) {
				String literalName = extractLiteralName(literalTerm, index);
				Enum<E> literal = getAndCheckLiteral(literalName);
				contentMatched.add(literal);
			}
		}
		literalTermMatcher.reset();
	}

	/** Extract the literal name from a literal term. */
	private String extractLiteralName(String literalTerm, int index) {
		return literalTerm.substring(0, index);
	}

	/** Transform the enumeration pattern to a regex pattern. */
	private void buildStringPattern(Matcher literalTermMatcher)
			throws ConQATException {
		char current = OTHER + 1;

		int last = 0;
		StringBuilder builder = new StringBuilder();

		while (literalTermMatcher.find()) {
			String betweenLiteralTerms = expression.substring(last,
					literalTermMatcher.start());
			builder.append(betweenLiteralTerms);

			String literalTerm = getLiteralTerm(literalTermMatcher);
			current = processLiteralTerm(builder, literalTerm, current);

			last = literalTermMatcher.end();
		}

		if (last <= expression.length()) {
			builder.append(expression.substring(last));
		}

		stringPattern = CommonUtils.compilePattern(builder.toString(),
				"The enum pattern is syntactically incorrect.");
		literalTermMatcher.reset();
	}

	/**
	 * Get the literal term of the current match. The literal term is either
	 * "LITERAL" or "LITERAL=pattern".
	 */
	private String getLiteralTerm(Matcher literalMatcher) {
		return expression.substring(literalMatcher.start() + 1,
				literalMatcher.end() - 1);
	}

	/**
	 * Process the current literal term. The literal term is searched for a
	 * content pattern, resolved against the enumeration and mapped to a
	 * replacement character.
	 * 
	 * For instance, "LITERAL=pattern" is split into the enumeration literal
	 * "LITERAL" and the content pattern "pattern".
	 */
	private char processLiteralTerm(StringBuilder builder, String literalTerm,
			char current) throws ConQATException {
		int index = literalTerm.indexOf(CONTENT_PATTERN_DELIMITER);
		String contentPattern = null;
		if (index >= 0) {
			contentPattern = literalTerm.substring(index + 1);
			literalTerm = extractLiteralName(literalTerm, index);
		}

		Enum<E> literal = getAndCheckLiteral(literalTerm);

		Character c = getReplacementCharacter(literal);
		if (c == OTHER) {
			c = current++;
			addReplacementCharacter(literal, c);
		}

		appendReplacement(builder, literal, c, contentPattern);
		return current;
	}

	/** Get the replacement character for an enumeration literal. */
	private Character getReplacementCharacter(Enum<E> e) {
		Character c = replacements.get(e);
		if (c == null) {
			c = new Character(OTHER);
		}
		return c;
	}

	/** Add the replacement character for an enumeration literal. */
	private void addReplacementCharacter(Enum<E> literal, char c) {
		replacements.put(literal, c);
	}

	/**
	 * Append the replacement for the enumeration literal to the regex pattern.
	 * The replacement consists of a character and, optionally, a content
	 * pattern, if the enumeration literal is content matched.
	 * 
	 * For instance, "<LITERAL=pattern>" is replaced by "c<pattern>" where 'c'
	 * is the replacement character for the enumeration literal.
	 */
	private void appendReplacement(StringBuilder builder, Enum<E> enumValue,
			Character c, String contentPattern) throws ConQATException {
		if (contentMatched.contains(enumValue)) {
			builder.append(GROUP_START);
			builder.append(c);
			builder.append(CONTENT_START);
			if (contentPattern != null) {
				contentPattern = restrictWildcard(contentPattern);
				CommonUtils
						.compilePattern(
								contentPattern,
								("The content pattern \"" + contentPattern + "\" is syntactically incorrect."));
				builder.append(contentPattern);
			} else {
				builder.append(RESTRICTED_WILDCARD + RELUCTANT_SEQUENCE);
			}
			builder.append(CONTENT_END);
			builder.append(GROUP_END);
		} else {
			builder.append(c);
		}
	}

	/**
	 * Restrict wildcards in the content pattern to not match the delimiter of
	 * content strings.
	 */
	private String restrictWildcard(String contentPattern) {
		StringBuilder builder = new StringBuilder(contentPattern);
		int i = builder.indexOf(WILDCARD);
		while (i >= 0) {
			// only restrict wildcards that are not escaped
			// note: a wildcard used as first character cannot be escaped
			if (i == 0 || builder.charAt(i - 1) != '\\') {
				builder.replace(i, i + 1, RESTRICTED_WILDCARD);
			}
			i = builder.indexOf(WILDCARD, i + 1);
		}
		return builder.toString();
	}

	/** Create a matcher for a sequence of enumeration literals. */
	public EnumPatternMatcher matcher(List<D> sequence) {
		StringBuilder builder = new StringBuilder();
		IntList positions = convertSequence(builder, sequence);
		return new EnumPatternMatcher(this, builder.toString(), positions);
	}

	/**
	 * Convert the input sequence into a string on which the string pattern can
	 * operate.
	 */
	private IntList convertSequence(StringBuilder builder, List<D> sequence) {
		IntList positions = createPositions();
		for (D element : sequence) {
			Enum<E> e = getEnum(element);
			Character c = getReplacementCharacter(e);
			addCurrentPosition(positions, builder);
			builder.append(c);
			if (contentMatched.contains(e)) {
				builder.append(CONTENT_START + getContent(element)
						+ CONTENT_END);
			}
		}
		addCurrentPosition(positions, builder);
		return positions;
	}

	/**
	 * Create a list to maintain positions of the different elements. Returns
	 * null in case of an identity mapping.
	 */
	private IntList createPositions() {
		IntList positions = null;
		if (!contentMatched.isEmpty()) {
			positions = new IntList();
		}
		return positions;
	}

	/**
	 * Add the current position to the position list if it is not an identity
	 * mapping.
	 */
	private void addCurrentPosition(IntList positions, StringBuilder builder) {
		if (!contentMatched.isEmpty()) {
			// positions is null if and if only there are no content matched
			// terms. So positions can never be null here.
			positions.add(builder.length());
		}
	}

	/** Get the literal for an element. */
	protected abstract E getEnum(D element);

	/** Get the content of an element. */
	protected abstract String getContent(D element);

	/** Get the enumeration literal by name, checking its existence. */
	private E getAndCheckLiteral(String enumName) throws ConQATException {
		try {
			return Enum.valueOf(enumType, enumName);
		} catch (IllegalArgumentException e) {
			throw new ConQATException("\"" + enumName
					+ "\" is not a valid enumeration literal.");
		}
	}

	/** Returns expression. */
	public String getExpression() {
		return expression;
	}

	/** Returns pattern. */
	/* package */Pattern getPattern() {
		return stringPattern;
	}
}