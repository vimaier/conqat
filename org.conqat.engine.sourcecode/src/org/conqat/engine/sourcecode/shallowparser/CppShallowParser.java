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
package org.conqat.engine.sourcecode.shallowparser;

import static org.conqat.engine.sourcecode.shallowparser.EGenericParserStates.IN_METHOD;
import static org.conqat.engine.sourcecode.shallowparser.EGenericParserStates.IN_TYPE;
import static org.conqat.engine.sourcecode.shallowparser.EGenericParserStates.TOP_LEVEL;
import static org.conqat.lib.scanner.ETokenType.AND;
import static org.conqat.lib.scanner.ETokenType.ASSERT;
import static org.conqat.lib.scanner.ETokenType.BOOL;
import static org.conqat.lib.scanner.ETokenType.BREAK;
import static org.conqat.lib.scanner.ETokenType.BYTE;
import static org.conqat.lib.scanner.ETokenType.CASE;
import static org.conqat.lib.scanner.ETokenType.CHAR;
import static org.conqat.lib.scanner.ETokenType.CLASS;
import static org.conqat.lib.scanner.ETokenType.COLON;
import static org.conqat.lib.scanner.ETokenType.COMP;
import static org.conqat.lib.scanner.ETokenType.CONST;
import static org.conqat.lib.scanner.ETokenType.CONTINUE;
import static org.conqat.lib.scanner.ETokenType.DELETE;
import static org.conqat.lib.scanner.ETokenType.DOUBLE;
import static org.conqat.lib.scanner.ETokenType.ELSE;
import static org.conqat.lib.scanner.ETokenType.ENUM;
import static org.conqat.lib.scanner.ETokenType.EQ;
import static org.conqat.lib.scanner.ETokenType.EXTERN;
import static org.conqat.lib.scanner.ETokenType.FINAL;
import static org.conqat.lib.scanner.ETokenType.FLOAT;
import static org.conqat.lib.scanner.ETokenType.FOR;
import static org.conqat.lib.scanner.ETokenType.GOTO;
import static org.conqat.lib.scanner.ETokenType.GT;
import static org.conqat.lib.scanner.ETokenType.IDENTIFIER;
import static org.conqat.lib.scanner.ETokenType.INT;
import static org.conqat.lib.scanner.ETokenType.LBRACE;
import static org.conqat.lib.scanner.ETokenType.LBRACK;
import static org.conqat.lib.scanner.ETokenType.LONG;
import static org.conqat.lib.scanner.ETokenType.LPAREN;
import static org.conqat.lib.scanner.ETokenType.LT;
import static org.conqat.lib.scanner.ETokenType.MINUSMINUS;
import static org.conqat.lib.scanner.ETokenType.MULT;
import static org.conqat.lib.scanner.ETokenType.NAMESPACE;
import static org.conqat.lib.scanner.ETokenType.NEW;
import static org.conqat.lib.scanner.ETokenType.OPERATOR;
import static org.conqat.lib.scanner.ETokenType.PLUSPLUS;
import static org.conqat.lib.scanner.ETokenType.PREPROCESSOR_DIRECTIVE;
import static org.conqat.lib.scanner.ETokenType.PREPROCESSOR_INCLUDE;
import static org.conqat.lib.scanner.ETokenType.RBRACE;
import static org.conqat.lib.scanner.ETokenType.RBRACK;
import static org.conqat.lib.scanner.ETokenType.RETURN;
import static org.conqat.lib.scanner.ETokenType.RPAREN;
import static org.conqat.lib.scanner.ETokenType.SCOPE;
import static org.conqat.lib.scanner.ETokenType.SEMICOLON;
import static org.conqat.lib.scanner.ETokenType.SHORT;
import static org.conqat.lib.scanner.ETokenType.SIGNED;
import static org.conqat.lib.scanner.ETokenType.STATIC;
import static org.conqat.lib.scanner.ETokenType.STRING_LITERAL;
import static org.conqat.lib.scanner.ETokenType.STRUCT;
import static org.conqat.lib.scanner.ETokenType.SUPER;
import static org.conqat.lib.scanner.ETokenType.SWITCH;
import static org.conqat.lib.scanner.ETokenType.TEMPLATE;
import static org.conqat.lib.scanner.ETokenType.THIS;
import static org.conqat.lib.scanner.ETokenType.THROW;
import static org.conqat.lib.scanner.ETokenType.TYPEDEF;
import static org.conqat.lib.scanner.ETokenType.TYPENAME;
import static org.conqat.lib.scanner.ETokenType.UNION;
import static org.conqat.lib.scanner.ETokenType.UNSIGNED;
import static org.conqat.lib.scanner.ETokenType.USING;
import static org.conqat.lib.scanner.ETokenType.VIRTUAL;
import static org.conqat.lib.scanner.ETokenType.VOID;
import static org.conqat.lib.scanner.ETokenType.WHILE;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.RecognizerBase;
import org.conqat.engine.sourcecode.shallowparser.framework.SequenceRecognizer.ITokenMatcher;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Shallow parser for C/C++.
 * <p>
 * What this parser does and does not:
 * <ul>
 * <li>The parser recognizes types (classes, enums, interfaces), methods and
 * attributes, and individual statements.</li>
 * <li>It recognizes the nesting of statements (e.g. in loops), but does not
 * parse into the statements. For example, it recognizes an if-statement and
 * provides the list of sub-statements, but does not provide direct access to
 * the if-condition.</li>
 * <li>All preprocessor statements are parsed as meta information.</li>
 * <li>Template declarations are parsed as preceding meta information.</li>
 * <li>Forward declarations are handled as meta information.</li>
 * <li>We heuristically filter code generating macros, such as
 * "CREATE_STUFF(MyClass)".</li>
 * </ul>
 * 
 * @author $Author: kinnen $
 * @version $Rev: 47150 $
 * @ConQAT.Rating GREEN Hash: 04BAAED4888266D5DB4BECB57E80E118
 */
/* package */class CppShallowParser extends CStyleShallowParserBase {

	/** Keywords used for primitive types. */
	private static final EnumSet<ETokenType> PRIMITIVE_TYPES = EnumSet
			.of(VOID, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, SIGNED, UNSIGNED,
					CHAR, BOOL);

	/**
	 * Set of common "keywords" that are not actually part of the language but
	 * rather used by certain compilers and implicitly defined using macros. The
	 * solution used here is to filter them out.
	 */
	private static final Set<String> PSEUDO_KEYWORDS = new HashSet<String>(
			Arrays.asList(
					// typically found in Windows compilers
					"__fastcall", "__export", "__forceinline", "_cdecl",
					"_stdcall", "__stdcall", "WINAPI", "APIENTRY", "CALLBACK",
					// keywords found in ISA dialog manager
					"DML_c", "DM_CALLBACK", "__1", "__2", "__3", "__4", "__5",
					"__6", "__7", "DM_ENTRY", "DML_pascal", "DML_default",
					// project specific keywords
					"IGVPWORD_API"));

	/** Constructor. */
	public CppShallowParser() {
		createNamespaceRules();
	}

	/** {@inheritDoc} */
	@Override
	protected void createMetaRules() {
		// preprocessor directives
		inAnyState()
				.sequence(
						EnumSet.of(PREPROCESSOR_DIRECTIVE, PREPROCESSOR_INCLUDE))
				.createNode(EShallowEntityType.META, 0).endNode();

		// template declaration
		inAnyState().sequence(TEMPLATE).createNode(EShallowEntityType.META, 0)
				.skipNested(LT, GT).endNode();

		// forward declarations
		inState(TOP_LEVEL, IN_TYPE)
				.sequence(EnumSet.of(CLASS, STRUCT, ENUM, UNION))
				.subRecognizer(createScopeRecognizer(), 0, Integer.MAX_VALUE)
				.sequence(IDENTIFIER).sequence(SEMICOLON)
				.createNode(EShallowEntityType.META, "forward declaration", -2)
				.endNode();

		// friend
		inAnyState().sequence(USING).createNode(EShallowEntityType.META, 0)
				.skipTo(SEMICOLON).endNode();

		// extern "C"
		RecognizerBase<EGenericParserStates> externCAlternative = inState(
				TOP_LEVEL).sequence(EXTERN, new CStringMatcher());
		externCAlternative.sequence(LBRACE)
				.createNode(EShallowEntityType.META, "extern C block")
				.parseUntil(TOP_LEVEL).sequence(RBRACE).endNode();
		externCAlternative.createNode(EShallowEntityType.META,
				"extern C prefix").endNode();

		inState(IN_METHOD).sequence(new ASMIdentifierMatcher(), LBRACE)
				.createNode(EShallowEntityType.META, "inline assembler block")
				.skipTo(RBRACE).endNode();

		super.createMetaRules();
	}

	/** Creates namespace specific rules. */
	private void createNamespaceRules() {
		// using
		inAnyState().sequence(USING).createNode(EShallowEntityType.META, 0)
				.skipTo(SEMICOLON).endNode();

		// namespace
		RecognizerBase<EGenericParserStates> namespaceAlternative = inAnyState()
				.sequence(NAMESPACE).skipBefore(EnumSet.of(SEMICOLON, LBRACE));
		namespaceAlternative.sequence(LBRACE)
				.createNode(EShallowEntityType.MODULE, 0, new Region(1, -2))
				.parseUntil(TOP_LEVEL).sequence(RBRACE).endNode();
		namespaceAlternative.sequence(SEMICOLON)
				.createNode(EShallowEntityType.META, 0, new Region(1, -2))
				.endNode();
	}

	/** {@inheritDoc} */
	@Override
	protected void createTypeRules() {
		// typedef
		RecognizerBase<EGenericParserStates> typedefAlternative = inAnyState()
				.sequence(TYPEDEF).createNode(EShallowEntityType.TYPE, 0);
		RecognizerBase<EGenericParserStates> typedefStructAlternative = typedefAlternative
				.optional(CONST).sequence(getTypeKeywords())
				.skipBefore(EnumSet.of(SEMICOLON, LBRACE));
		typedefStructAlternative.sequence(LBRACE).parseUntil(IN_TYPE)
				.sequence(RBRACE).skipTo(SEMICOLON).endNode();
		typedefStructAlternative.skipTo(SEMICOLON).endNode();
		typedefAlternative.skipTo(SEMICOLON).endNode();

		// enum (both anonymous and named)
		RecognizerBase<EGenericParserStates> enumAlternative = inAnyState()
				.sequence(ENUM);
		enumAlternative.sequence(LBRACE).createNode(EShallowEntityType.TYPE, 0)
				.skipTo(RBRACE).endNode();
		enumAlternative.sequence(IDENTIFIER, EnumSet.of(LBRACE, COLON))
				.createNode(EShallowEntityType.TYPE, 0, 1).skipTo(RBRACE)
				.endNode();

		// types; we have to ensure when skipping to the LBRACE, that there is
		// no earlier SEMICOLON or EQ, as in these cases it is a forward
		// declaration or a variable.
		RecognizerBase<EGenericParserStates> typeAlternative = inAnyState()
				.sequence(getTypeKeywords(), getValidIdentifiers())
				.skipBefore(EnumSet.of(SEMICOLON, LBRACE, EQ)).sequence(LBRACE)
				.createNode(EShallowEntityType.TYPE, 0, 1).parseUntil(IN_TYPE)
				.sequence(RBRACE);
		typeAlternative.sequence(IDENTIFIER)
				.skipToWithNesting(SEMICOLON, LBRACE, RBRACE).endNode();
		typeAlternative.optional(SEMICOLON).endNode();

		// anonymous types
		inAnyState().sequence(getTypeKeywords(), LBRACE)
				.createNode(EShallowEntityType.TYPE, 0, "<anonymous>")
				.parseUntil(IN_TYPE).sequence(RBRACE).endNode();
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getTypeKeywords() {
		return EnumSet.of(CLASS, STRUCT, UNION);
	}

	/** {@inheritDoc} */
	@Override
	protected void createClassElementsRules() {
		// operator overloading
		completeMethod("operator", typePatternInState(IN_TYPE, TOP_LEVEL)
				.subRecognizer(createScopeRecognizer(), 0, Integer.MAX_VALUE)
				.markStart().sequence(OPERATOR).skipTo(LPAREN),
				new Region(0, 1));

		// operator overloading without type (conversion operators)
		completeMethod("cast operator", inState(IN_TYPE, TOP_LEVEL)
				.subRecognizer(createScopeRecognizer(), 0, Integer.MAX_VALUE)
				.markStart().sequence(OPERATOR).skipTo(LPAREN), new int[] { 0,
				1 });

		// functions, procedures, methods
		completeMethod("function", typePatternInState(IN_TYPE, TOP_LEVEL)
				.subRecognizer(createScopeRecognizer(), 0, Integer.MAX_VALUE)
				.markStart().sequence(IDENTIFIER).sequence(LPAREN));

		// destructor
		completeMethod(
				"destructor",
				inState(IN_TYPE, TOP_LEVEL)
						.optional(VIRTUAL)
						.subRecognizer(createScopeRecognizer(), 0,
								Integer.MAX_VALUE).sequence(COMP).markStart()
						.sequence(IDENTIFIER).skipNested(LT, GT)
						.sequence(LPAREN));

		// constructor
		completeMethod(
				"constructor",
				inState(IN_TYPE, TOP_LEVEL)
						.subRecognizer(createScopeRecognizer(), 0,
								Integer.MAX_VALUE).markStart()
						.sequence(IDENTIFIER).skipNested(LT, GT)
						.sequence(LPAREN));

		// heuristic for preprocessor additions; we specify it here although we
		// parse it as META, as we want it to match between the constructor and
		// the attribute
		inState(TOP_LEVEL, IN_TYPE)
				.sequence(new UppercaseIdentifierMatcher(), LPAREN)
				.skipToWithNesting(RPAREN, LPAREN, RPAREN)
				.createNode(EShallowEntityType.META,
						"preprocessor generated code", 0).endNode();

		// attributes and global variables
		typePatternInState(IN_TYPE, TOP_LEVEL)
				.subRecognizer(createScopeRecognizer(), 0, Integer.MAX_VALUE)
				.markStart().sequence(IDENTIFIER)
				.createNode(EShallowEntityType.ATTRIBUTE, "attribute", 0)
				.skipToWithNesting(SEMICOLON, LBRACE, RBRACE).endNode();
	}

	/**
	 * Creates a new recognizer that can match a scope prefix for a method-like
	 * construct. This includes sequences of identifiers with double colon,
	 * possibly intermixed with template arguments.
	 */
	private RecognizerBase<EGenericParserStates> createScopeRecognizer() {
		// remember the start of the recognizer chain (we can not used the
		// result of the method chain, as this would be the last recognizer!)
		RecognizerBase<EGenericParserStates> result = emptyRecognizer();
		result.sequence(IDENTIFIER).skipNested(LT, GT).sequence(SCOPE);
		return result;
	}

	/**
	 * Completes a method-like construct. This begins with skipping the
	 * parameter list (i.e. the construct has to already match the left
	 * parenthesis). This end either in a complete method with a body, or with a
	 * semicolon and thus is just a declaration.
	 */
	private void completeMethod(String name,
			RecognizerBase<EGenericParserStates> start) {
		completeMethod(name, start, 0);
	}

	/**
	 * Completes a method-like construct. This begins with skipping the
	 * parameter list (i.e. the construct has to already match the left
	 * parenthesis). This end either in a complete method with a body, or with a
	 * semicolon and thus is just a declaration.
	 * 
	 * @param methodName
	 *            the name of the method. This object accepts the same types as
	 *            the name object in createNode.
	 */
	private void completeMethod(String name,
			RecognizerBase<EGenericParserStates> start, Object methodName) {

		// the keywords we break on to check for K&R style
		EnumSet<ETokenType> krCheck = EnumSet.of(LBRACE, SEMICOLON, IDENTIFIER,
				COLON);
		krCheck.addAll(PRIMITIVE_TYPES);

		// the next check is a bit more involved, as it is part of our heuristic
		// to recognize code generating preprocessor statements and also should
		// support K&R style functions
		RecognizerBase<EGenericParserStates> krStyleAlternative = start
				.skipToWithNesting(RPAREN, LPAREN, RPAREN)
				// 1.) go to first LBRACE, SEMICOLON, IDENTIFIER,
				// COLON, or primitive type keyword
				.skipBeforeWithNesting(krCheck, LPAREN, RPAREN);

		// 2.) If we find a type keyword first, this must be K&R style and we
		// continue at the brace
		krStyleAlternative.sequence(PRIMITIVE_TYPES).skipTo(LBRACE)
				.createNode(EShallowEntityType.METHOD, name, methodName)
				.parseUntil(IN_METHOD).sequence(RBRACE).endNode();

		RecognizerBase<EGenericParserStates> declarationAlternative = krStyleAlternative
		// 3.) break if it is an IDENTIFIER, as these are only expected after a
		// colon (constructor) or in parentheses (throw decl)
				.sequenceBefore(EnumSet.of(LBRACE, SEMICOLON, COLON))
				// 4.) skip again (in case we stopped at the COLON)
				.skipBefore(EnumSet.of(LBRACE, SEMICOLON));

		// 4.) LBRACE means that this is a definition
		declarationAlternative.sequence(LBRACE)
				.createNode(EShallowEntityType.METHOD, name, methodName)
				.parseUntil(IN_METHOD).sequence(RBRACE).endNode();

		// 5.) SEMICOLON means that this is a declaration
		declarationAlternative
				.sequence(SEMICOLON)
				.createNode(EShallowEntityType.METHOD, name + " declaration",
						methodName).endNode();
	}

	/** {@inheritDoc} */
	@Override
	protected void createCaseRule() {
		super.createCaseRule();

		// C/C++ also allows parentheses here and type casts (hence two sets of
		// nested parentheses).
		inState(IN_METHOD).markStart().sequence(CASE).skipTo(COLON)
				.createNode(EShallowEntityType.META, 0).endNode();
	}

	/** {@inheritDoc} */
	@Override
	protected void createSimpleStatementRule() {

		EnumSet<ETokenType> separators = EnumSet.of(LBRACE, RBRACE);
		separators.addAll(ETokenType.KEYWORDS);

		inState(IN_METHOD).sequence(new UppercaseIdentifierMatcher())
				.skipNested(LPAREN, RPAREN).optional(PREPROCESSOR_DIRECTIVE)
				.sequenceBefore(separators)
				.createNode(EShallowEntityType.STATEMENT, "Expanded macro", 0)
				.endNode();

		super.createSimpleStatementRule();
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getSimpleBlockKeywordsWithParentheses() {
		return EnumSet.of(WHILE, FOR, SWITCH);
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getSimpleBlockKeywordsWithoutParentheses() {
		return EnumSet.of(ELSE);
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getStatementStartTokens() {
		return EnumSet.of(NEW, DELETE, BREAK, CONTINUE, RETURN, ASSERT, FINAL,
				GOTO, SUPER, THIS, THROW, MULT, LPAREN, PLUSPLUS, MINUSMINUS,
				SCOPE);
	}

	/** {@inheritDoc} */
	@Override
	protected RecognizerBase<EGenericParserStates> typePattern(
			RecognizerBase<EGenericParserStates> currentState) {

		EnumSet<ETokenType> extendedTypeKeywords = EnumSet
				.copyOf(getTypeKeywords());
		extendedTypeKeywords.add(TYPENAME);

		EnumSet<ETokenType> typeOrIdentifier = EnumSet.of(IDENTIFIER);
		typeOrIdentifier.addAll(PRIMITIVE_TYPES);

		return currentState
				.repeated(EnumSet.of(CONST, STATIC, VIRTUAL, EXTERN))
				.optional(extendedTypeKeywords)
				.subRecognizer(createScopeRecognizer(), 0, Integer.MAX_VALUE)
				.sequence(typeOrIdentifier).skipNested(LT, GT)
				.skipAny(EnumSet.of(MULT, AND, CONST))
				.skipNested(LBRACK, RBRACK)
				.skipAny(EnumSet.of(MULT, AND, CONST, LBRACK, RBRACK));
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFilteredToken(IToken token, IToken previousToken) {
		if (token.getType() == IDENTIFIER
				&& PSEUDO_KEYWORDS.contains(token.getText())) {
			return true;
		}

		return super.isFilteredToken(token, previousToken);
	}

	/**
	 * Matches all uppercase identifiers with at least 4 characters. This is
	 * part of the heuristic to recognize macros, so the limit is somewhat
	 * arbitrary
	 */
	private static class UppercaseIdentifierMatcher implements ITokenMatcher {

		/**
		 * Pattern containing all valid characters for C++ uppercase
		 * identifiers.
		 */
		private static final Pattern UPPERCASE_PATTERN = Pattern
				.compile("[_A-Z0-9]+");

		/** {@inheritDoc} */
		@Override
		public boolean matches(IToken token) {
			return token.getType() == IDENTIFIER
					&& token.getText().length() > 3
					&& UPPERCASE_PATTERN.matcher(token.getText()).matches();
		}
	}

	/** Matches the string "C" or "C++". */
	private static class CStringMatcher implements ITokenMatcher {

		/** {@inheritDoc} */
		@Override
		public boolean matches(IToken token) {
			return token.getType() == STRING_LITERAL
					&& ("\"C\"".equals(token.getText()) || "\"C++\""
							.equals(token.getText()));
		}
	}

	/** Matches the string "asm", "_asm", or "__asm". */
	private static class ASMIdentifierMatcher implements ITokenMatcher {

		/** {@inheritDoc} */
		@Override
		public boolean matches(IToken token) {
			return token.getType().getTokenClass() == ETokenClass.IDENTIFIER
					&& ("asm".equals(token.getText())
							|| "_asm".equals(token.getText()) || "__asm"
								.equals(token.getText()));
		}
	}
}
