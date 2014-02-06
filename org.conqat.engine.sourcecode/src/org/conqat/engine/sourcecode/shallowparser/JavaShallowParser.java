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
import static org.conqat.lib.scanner.ETokenType.ABSTRACT;
import static org.conqat.lib.scanner.ETokenType.ANNOTATION_INTERFACE;
import static org.conqat.lib.scanner.ETokenType.ASSERT;
import static org.conqat.lib.scanner.ETokenType.AT_OPERATOR;
import static org.conqat.lib.scanner.ETokenType.BOOLEAN;
import static org.conqat.lib.scanner.ETokenType.BREAK;
import static org.conqat.lib.scanner.ETokenType.BYTE;
import static org.conqat.lib.scanner.ETokenType.CASE;
import static org.conqat.lib.scanner.ETokenType.CHAR;
import static org.conqat.lib.scanner.ETokenType.CLASS;
import static org.conqat.lib.scanner.ETokenType.COLON;
import static org.conqat.lib.scanner.ETokenType.COMMA;
import static org.conqat.lib.scanner.ETokenType.CONTINUE;
import static org.conqat.lib.scanner.ETokenType.DEFAULT;
import static org.conqat.lib.scanner.ETokenType.DOT;
import static org.conqat.lib.scanner.ETokenType.DOUBLE;
import static org.conqat.lib.scanner.ETokenType.ELSE;
import static org.conqat.lib.scanner.ETokenType.ENUM;
import static org.conqat.lib.scanner.ETokenType.FINAL;
import static org.conqat.lib.scanner.ETokenType.FINALLY;
import static org.conqat.lib.scanner.ETokenType.FLOAT;
import static org.conqat.lib.scanner.ETokenType.FOR;
import static org.conqat.lib.scanner.ETokenType.GOTO;
import static org.conqat.lib.scanner.ETokenType.GT;
import static org.conqat.lib.scanner.ETokenType.IDENTIFIER;
import static org.conqat.lib.scanner.ETokenType.IMPORT;
import static org.conqat.lib.scanner.ETokenType.INT;
import static org.conqat.lib.scanner.ETokenType.INTERFACE;
import static org.conqat.lib.scanner.ETokenType.LBRACE;
import static org.conqat.lib.scanner.ETokenType.LBRACK;
import static org.conqat.lib.scanner.ETokenType.LONG;
import static org.conqat.lib.scanner.ETokenType.LPAREN;
import static org.conqat.lib.scanner.ETokenType.LT;
import static org.conqat.lib.scanner.ETokenType.MINUSMINUS;
import static org.conqat.lib.scanner.ETokenType.NATIVE;
import static org.conqat.lib.scanner.ETokenType.NEW;
import static org.conqat.lib.scanner.ETokenType.PACKAGE;
import static org.conqat.lib.scanner.ETokenType.PLUSPLUS;
import static org.conqat.lib.scanner.ETokenType.PRIVATE;
import static org.conqat.lib.scanner.ETokenType.PROTECTED;
import static org.conqat.lib.scanner.ETokenType.PUBLIC;
import static org.conqat.lib.scanner.ETokenType.RBRACE;
import static org.conqat.lib.scanner.ETokenType.RBRACK;
import static org.conqat.lib.scanner.ETokenType.RETURN;
import static org.conqat.lib.scanner.ETokenType.RPAREN;
import static org.conqat.lib.scanner.ETokenType.SEMICOLON;
import static org.conqat.lib.scanner.ETokenType.SHORT;
import static org.conqat.lib.scanner.ETokenType.STATIC;
import static org.conqat.lib.scanner.ETokenType.SUPER;
import static org.conqat.lib.scanner.ETokenType.SWITCH;
import static org.conqat.lib.scanner.ETokenType.SYNCHRONIZED;
import static org.conqat.lib.scanner.ETokenType.THIS;
import static org.conqat.lib.scanner.ETokenType.THROW;
import static org.conqat.lib.scanner.ETokenType.TRANSIENT;
import static org.conqat.lib.scanner.ETokenType.VOID;
import static org.conqat.lib.scanner.ETokenType.VOLATILE;
import static org.conqat.lib.scanner.ETokenType.WHILE;

import java.util.EnumSet;

import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.RecognizerBase;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.scanner.ETokenType;

/**
 * Shallow parser for Java.
 * <p>
 * What this parser does and does not:
 * <ul>
 * <li>The parser recognizes types (classes, enums, interfaces), methods and
 * attributes, and individual statements.</li>
 * <li>It recognizes the nesting of statements (e.g. in loops), but does not
 * parse into the statements. For example, it recognizes an if-statement and
 * provides the list of sub-statements, but does not provide direct access to
 * the if-condition.</li>
 * <li>Import and package statements are parsed as meta information.</li>
 * <li>Annotations are recognized as meta information, but only annotations at
 * types and methods. Annotations at parameters are not parsed, as the parser
 * does not parse into the parameter list of methods.</li>
 * <li>The parser does not recognize anonymous classes. These are treated as a
 * single long statement or attribute. Inner classes, however, are parsed
 * correctly.</li>
 * <li>The parser can deal with multiple classes in a single file.</li>
 * </ul>
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47088 $
 * @ConQAT.Rating GREEN Hash: 0DD7E312AE63D0BAED0D747EC8B9B9DA
 */
/* package */class JavaShallowParser extends CStyleShallowParserBase {

	/** {@inheritDoc} */
	@Override
	protected void createMetaRules() {
		// imports and package
		inAnyState().sequence(EnumSet.of(IMPORT, PACKAGE))
				.createNode(EShallowEntityType.META, 0).skipTo(SEMICOLON)
				.endNode();

		// annotations; the spec allows both whitespace and comments between the
		// '@' and the identifier, but as we filter comments before-hand, this
		// is not an issue.
		inAnyState()
				.sequence(AT_OPERATOR, IDENTIFIER)
				.repeated(DOT, IDENTIFIER)
				.createNode(EShallowEntityType.META, "annotation",
						new Region(1, -1)).skipNested(LPAREN, RPAREN).endNode();

		super.createMetaRules();
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getTypeModifier() {
		return EnumSet.of(PUBLIC, PROTECTED, PRIVATE, ABSTRACT, STATIC, FINAL);
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getTypeKeywords() {
		return EnumSet.of(CLASS, INTERFACE, ENUM, ANNOTATION_INTERFACE);
	}

	/** {@inheritDoc} */
	@Override
	protected void createClassElementsRules() {
		createMethodRule();

		// simple enum literals (without parentheses)
		RecognizerBase<EGenericParserStates> enumLiteral = inState(IN_TYPE)
				.sequence(IDENTIFIER)
				.sequenceBefore(EnumSet.of(SEMICOLON, COMMA, LBRACE))
				.createNode(EShallowEntityType.ATTRIBUTE, "enum literal", 0);
		enumLiteral.sequence(EnumSet.of(SEMICOLON, COMMA)).endNode();
		enumLiteral.sequence(LBRACE).parseUntil(IN_TYPE)
				.sequence(RBRACE, EnumSet.of(SEMICOLON, COMMA)).endNode();

		createConstructorRule();

		// attributes (must be after method, as this would also match methods)
		typePatternInState(IN_TYPE).sequence(IDENTIFIER)
				.createNode(EShallowEntityType.ATTRIBUTE, "attribute", -1)
				.skipToWithNesting(SEMICOLON, LBRACE, RBRACE).endNode();

		// static initializer
		inState(IN_TYPE)
				.sequence(STATIC, LBRACE)
				.createNode(EShallowEntityType.METHOD, "static initializer",
						"<sinit>").parseUntil(IN_METHOD).sequence(RBRACE)
				.endNode();

		// non-static initializer
		inState(IN_TYPE)
				.sequence(LBRACE)
				.createNode(EShallowEntityType.METHOD,
						"non-static initializer", "<init>")
				.parseUntil(IN_METHOD).sequence(RBRACE).endNode();
	}

	/** Recognizes methods. */
	private void createMethodRule() {
		RecognizerBase<EGenericParserStates> methodAlternative = typePatternInState(
				IN_TYPE).markStart().sequence(IDENTIFIER, LPAREN)
				.skipToWithNesting(RPAREN, LPAREN, RPAREN)
				.skipBefore(EnumSet.of(LBRACE, SEMICOLON, DEFAULT));
		methodAlternative.sequence(LBRACE)
				.createNode(EShallowEntityType.METHOD, "method", 0)
				.parseUntil(IN_METHOD).sequence(RBRACE).endNode();
		methodAlternative.sequence(DEFAULT)
				.createNode(EShallowEntityType.METHOD, "abstract method", 0)
				.skipToWithNesting(SEMICOLON, LBRACE, RBRACE).endNode();
		methodAlternative.sequence(SEMICOLON)
				.createNode(EShallowEntityType.METHOD, "abstract method", 0)
				.endNode();
	}

	/** Recognizes constructors or enum literals with parentheses. */
	private void createConstructorRule() {
		RecognizerBase<EGenericParserStates> constructorOrEnum = inState(
				IN_TYPE).optional(EnumSet.of(PUBLIC, PRIVATE, PROTECTED))
				.markStart().sequence(IDENTIFIER, LPAREN)
				.skipToWithNesting(RPAREN, LPAREN, RPAREN);
		constructorOrEnum.sequence(EnumSet.of(SEMICOLON, COMMA))
				.createNode(EShallowEntityType.ATTRIBUTE, "enum literal", 0)
				.endNode();
		RecognizerBase<EGenericParserStates> constructorOrEnum2 = constructorOrEnum
				.skipTo(LBRACE);
		// at this point this still could be a constructor or an enum literal.
		// Heuristic used: If we encounter the '@' sign (from an annotation) or
		// a method modifier, we assume enum literal.
		constructorOrEnum2
				.sequenceBefore(
						EnumSet.of(AT_OPERATOR, PUBLIC, PRIVATE, PROTECTED))
				.createNode(EShallowEntityType.ATTRIBUTE, "enum literal", 0)
				.parseUntil(IN_TYPE).sequence(RBRACE)
				.sequence(EnumSet.of(SEMICOLON, COMMA)).endNode();
		constructorOrEnum2
				.createNode(EShallowEntityType.METHOD, "constructor", 0)
				.parseUntil(IN_METHOD).sequence(RBRACE).endNode();
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getSimpleBlockKeywordsWithParentheses() {
		return EnumSet.of(WHILE, FOR, SWITCH, SYNCHRONIZED);
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getSimpleBlockKeywordsWithoutParentheses() {
		return EnumSet.of(ELSE, FINALLY);
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getStatementStartTokens() {
		return EnumSet.of(NEW, BREAK, CONTINUE, RETURN, ASSERT, FINAL, GOTO,
				SUPER, THIS, THROW, LPAREN, PLUSPLUS, MINUSMINUS);
	}

	/** {@inheritDoc} */
	@Override
	protected void createCaseRule() {
		super.createCaseRule();

		// Java also allows fully/partially qualified constants
		inState(IN_METHOD).markStart().sequence(CASE).repeated(IDENTIFIER, DOT)
				.sequence(IDENTIFIER, COLON)
				.createNode(EShallowEntityType.META, 0, new Region(1, -2))
				.endNode();
	}

	/** {@inheritDoc} */
	@Override
	protected RecognizerBase<EGenericParserStates> typePattern(
			RecognizerBase<EGenericParserStates> currentState) {
		EnumSet<ETokenType> modifierKeywords = EnumSet.of(STATIC, FINAL,
				PRIVATE, PROTECTED, PUBLIC, ABSTRACT, NATIVE, SYNCHRONIZED,
				TRANSIENT, VOLATILE);
		EnumSet<ETokenType> typeNames = EnumSet.of(IDENTIFIER, VOID, BYTE,
				SHORT, INT, LONG, FLOAT, DOUBLE, CHAR, BOOLEAN);
		return currentState.repeated(modifierKeywords).sequence(typeNames)
				.skipNested(LT, GT).skipAny(EnumSet.of(LBRACK, RBRACK));
	}
}
