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
import static org.conqat.engine.sourcecode.shallowparser.EGenericParserStates.IN_MODULE;
import static org.conqat.engine.sourcecode.shallowparser.EGenericParserStates.IN_TYPE;
import static org.conqat.engine.sourcecode.shallowparser.EGenericParserStates.TOP_LEVEL;
import static org.conqat.lib.scanner.ETokenType.ADD;
import static org.conqat.lib.scanner.ETokenType.ALIAS;
import static org.conqat.lib.scanner.ETokenType.ASCENDING;
import static org.conqat.lib.scanner.ETokenType.ASSERT;
import static org.conqat.lib.scanner.ETokenType.ASYNC;
import static org.conqat.lib.scanner.ETokenType.AWAIT;
import static org.conqat.lib.scanner.ETokenType.BASE;
import static org.conqat.lib.scanner.ETokenType.BOOL;
import static org.conqat.lib.scanner.ETokenType.BREAK;
import static org.conqat.lib.scanner.ETokenType.BYTE;
import static org.conqat.lib.scanner.ETokenType.CASE;
import static org.conqat.lib.scanner.ETokenType.CHAR;
import static org.conqat.lib.scanner.ETokenType.CHECKED;
import static org.conqat.lib.scanner.ETokenType.CLASS;
import static org.conqat.lib.scanner.ETokenType.COLON;
import static org.conqat.lib.scanner.ETokenType.CONST;
import static org.conqat.lib.scanner.ETokenType.CONTINUE;
import static org.conqat.lib.scanner.ETokenType.DECIMAL;
import static org.conqat.lib.scanner.ETokenType.DELEGATE;
import static org.conqat.lib.scanner.ETokenType.DESCENDING;
import static org.conqat.lib.scanner.ETokenType.DOT;
import static org.conqat.lib.scanner.ETokenType.DOUBLE;
import static org.conqat.lib.scanner.ETokenType.DYNAMIC;
import static org.conqat.lib.scanner.ETokenType.ELSE;
import static org.conqat.lib.scanner.ETokenType.ENUM;
import static org.conqat.lib.scanner.ETokenType.EVENT;
import static org.conqat.lib.scanner.ETokenType.FINALLY;
import static org.conqat.lib.scanner.ETokenType.FIXED;
import static org.conqat.lib.scanner.ETokenType.FLOAT;
import static org.conqat.lib.scanner.ETokenType.FOR;
import static org.conqat.lib.scanner.ETokenType.FOREACH;
import static org.conqat.lib.scanner.ETokenType.FROM;
import static org.conqat.lib.scanner.ETokenType.GET;
import static org.conqat.lib.scanner.ETokenType.GLOBAL;
import static org.conqat.lib.scanner.ETokenType.GOTO;
import static org.conqat.lib.scanner.ETokenType.GROUP;
import static org.conqat.lib.scanner.ETokenType.GT;
import static org.conqat.lib.scanner.ETokenType.IDENTIFIER;
import static org.conqat.lib.scanner.ETokenType.INT;
import static org.conqat.lib.scanner.ETokenType.INTERFACE;
import static org.conqat.lib.scanner.ETokenType.INTO;
import static org.conqat.lib.scanner.ETokenType.JOIN;
import static org.conqat.lib.scanner.ETokenType.LBRACE;
import static org.conqat.lib.scanner.ETokenType.LBRACK;
import static org.conqat.lib.scanner.ETokenType.LET;
import static org.conqat.lib.scanner.ETokenType.LOCK;
import static org.conqat.lib.scanner.ETokenType.LONG;
import static org.conqat.lib.scanner.ETokenType.LPAREN;
import static org.conqat.lib.scanner.ETokenType.LT;
import static org.conqat.lib.scanner.ETokenType.MINUSMINUS;
import static org.conqat.lib.scanner.ETokenType.NAMESPACE;
import static org.conqat.lib.scanner.ETokenType.NEW;
import static org.conqat.lib.scanner.ETokenType.OBJECT;
import static org.conqat.lib.scanner.ETokenType.OPERATOR;
import static org.conqat.lib.scanner.ETokenType.ORDERBY;
import static org.conqat.lib.scanner.ETokenType.PARTIAL;
import static org.conqat.lib.scanner.ETokenType.PLUSPLUS;
import static org.conqat.lib.scanner.ETokenType.QUESTION;
import static org.conqat.lib.scanner.ETokenType.RBRACE;
import static org.conqat.lib.scanner.ETokenType.RBRACK;
import static org.conqat.lib.scanner.ETokenType.REMOVE;
import static org.conqat.lib.scanner.ETokenType.RETURN;
import static org.conqat.lib.scanner.ETokenType.RPAREN;
import static org.conqat.lib.scanner.ETokenType.SBYTE;
import static org.conqat.lib.scanner.ETokenType.SELECT;
import static org.conqat.lib.scanner.ETokenType.SEMICOLON;
import static org.conqat.lib.scanner.ETokenType.SET;
import static org.conqat.lib.scanner.ETokenType.SHORT;
import static org.conqat.lib.scanner.ETokenType.SIZEOF;
import static org.conqat.lib.scanner.ETokenType.STACKALLOC;
import static org.conqat.lib.scanner.ETokenType.STATIC;
import static org.conqat.lib.scanner.ETokenType.STRING;
import static org.conqat.lib.scanner.ETokenType.STRUCT;
import static org.conqat.lib.scanner.ETokenType.SWITCH;
import static org.conqat.lib.scanner.ETokenType.THIS;
import static org.conqat.lib.scanner.ETokenType.THROW;
import static org.conqat.lib.scanner.ETokenType.TYPEOF;
import static org.conqat.lib.scanner.ETokenType.UINT;
import static org.conqat.lib.scanner.ETokenType.ULONG;
import static org.conqat.lib.scanner.ETokenType.UNCHECKED;
import static org.conqat.lib.scanner.ETokenType.UNSAFE;
import static org.conqat.lib.scanner.ETokenType.USHORT;
import static org.conqat.lib.scanner.ETokenType.USING;
import static org.conqat.lib.scanner.ETokenType.VALUE;
import static org.conqat.lib.scanner.ETokenType.VAR;
import static org.conqat.lib.scanner.ETokenType.VOID;
import static org.conqat.lib.scanner.ETokenType.WHERE;
import static org.conqat.lib.scanner.ETokenType.WHILE;
import static org.conqat.lib.scanner.ETokenType.YIELD;

import java.util.EnumSet;

import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.RecognizerBase;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * Shallow parser for C#
 * <p>
 * What this parser does and does not:
 * <ul>
 * <li>The parser recognizes types (classes, enums, interfaces), methods and
 * attributes, and individual statements.</li>
 * <li>It recognizes the nesting of statements (e.g. in loops), but does not
 * parse into the statements. For example, it recognizes an if-statement and
 * provides the list of sub-statements, but does not provide direct access to
 * the if-condition.</li>
 * <li>Using statements and annotations are parsed as meta information.</li>
 * </ul>
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41874 $
 * @ConQAT.Rating GREEN Hash: 1FDA11EDB9C336C3EC4FB444E9723C98
 */
/* package */class CsShallowParser extends CStyleShallowParserBase {

	/** {@inheritDoc} */
	@Override
	protected void createMetaRules() {
		// using
		inState(TOP_LEVEL, IN_MODULE).sequence(EnumSet.of(USING))
				.createNode(EShallowEntityType.META, 0).skipTo(SEMICOLON)
				.endNode();

		// annotations
		inState(IN_TYPE, IN_MODULE, TOP_LEVEL).sequence(LBRACK)
				.createNode(EShallowEntityType.META, "annotation")
				.skipToWithNesting(RBRACK, LBRACK, RBRACK).endNode();

		super.createMetaRules();
	}

	/** {@inheritDoc} */
	@Override
	protected void createTypeRules() {
		// namespace
		inState(TOP_LEVEL, IN_MODULE)
				.sequence(NAMESPACE, getValidIdentifiers()).skipTo(LBRACE)
				.createNode(EShallowEntityType.MODULE, 0, new Region(1, -2))
				.parseUntil(IN_MODULE).sequence(RBRACE).endNode();

		super.createTypeRules();
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getTypeKeywords() {
		return EnumSet.of(CLASS, INTERFACE, ENUM, STRUCT);
	}

	/** {@inheritDoc} */
	@Override
	protected void createClassElementsRules() {
		// delegates
		typePattern(inState(TOP_LEVEL, IN_MODULE, IN_TYPE).sequence(DELEGATE))
				.sequence(getValidIdentifiers(), LPAREN)
				.createNode(EShallowEntityType.METHOD, 0, -2).skipTo(RPAREN)
				.skipTo(SEMICOLON).endNode();

		// indexers
		completeMethod(
				"indexer",
				EShallowEntityType.ATTRIBUTE,
				IN_TYPE,
				typePatternInState(IN_TYPE)
						.subRecognizer(
								createExplicitInterfaceQualifierRecognizer(),
								0, Integer.MAX_VALUE).markStart()
						.sequence(THIS, LBRACK).skipTo(RBRACK));

		// operator overloading
		completeMethod(
				"operator",
				EShallowEntityType.METHOD,
				IN_METHOD,
				typePatternInState(IN_TYPE)
						.sequence(OPERATOR)
						.markStart()
						.sequence(
								EnumSet.of(ETokenClass.OPERATOR,
										ETokenClass.KEYWORD), LPAREN)
						.skipTo(RPAREN));

		// methods
		completeMethod(
				"method",
				EShallowEntityType.METHOD,
				IN_METHOD,
				typePatternInState(IN_TYPE)
						.subRecognizer(
								createExplicitInterfaceQualifierRecognizer(),
								0, Integer.MAX_VALUE).markStart()
						.sequence(getValidIdentifiers()).skipNested(LT, GT)
						.sequence(LPAREN).skipTo(RPAREN));

		// constructor
		inState(IN_TYPE).sequence(getValidIdentifiers(), LPAREN).skipTo(RPAREN)
				.skipToWithNesting(LBRACE, LPAREN, RPAREN)
				.createNode(EShallowEntityType.METHOD, "constructor", 0)
				.parseUntil(IN_METHOD).sequence(RBRACE).endNode();

		// properties
		typePatternInState(IN_TYPE)
				.subRecognizer(createExplicitInterfaceQualifierRecognizer(), 0,
						Integer.MAX_VALUE)
				.sequence(getValidIdentifiers(), LBRACE)
				.createNode(EShallowEntityType.ATTRIBUTE, "property", -2)
				.parseUntil(IN_TYPE).sequence(RBRACE).endNode();

		// events
		RecognizerBase<EGenericParserStates> eventRecognizer = inState(
				TOP_LEVEL, IN_MODULE, IN_TYPE).sequence(EVENT,
				getValidIdentifiers(), getValidIdentifiers()).createNode(
				EShallowEntityType.ATTRIBUTE, 0, -1);
		eventRecognizer.sequence(LBRACE).parseUntil(IN_TYPE).sequence(RBRACE)
				.endNode();
		eventRecognizer.skipTo(SEMICOLON).endNode();

		// attributes, e.g., fields (must be after method, as this would also
		// match methods)
		typePatternInState(IN_TYPE).sequence(getValidIdentifiers())
				.createNode(EShallowEntityType.ATTRIBUTE, "attribute", -1)
				.skipToWithNesting(SEMICOLON, LBRACE, RBRACE).endNode();

		// static initializer, get/set for properties, add/remove in events
		inState(IN_TYPE)
				.sequence(EnumSet.of(STATIC, GET, SET, ADD, REMOVE), LBRACE)
				.createNode(EShallowEntityType.METHOD, 0).parseUntil(IN_METHOD)
				.sequence(RBRACE).endNode();
	}

	/**
	 * Creates a new recognizer that can match an explicit interface qualifier
	 * prefix for a method-like construct. This includes sequences of
	 * identifiers with dots, possibly intermixed with template arguments.
	 */
	private RecognizerBase<EGenericParserStates> createExplicitInterfaceQualifierRecognizer() {
		// remember the start of the recognizer chain (we can not used the
		// result of the method chain, as this would be the last recognizer!)
		RecognizerBase<EGenericParserStates> result = emptyRecognizer();
		result.sequence(getValidIdentifiers()).skipNested(LT, GT).sequence(DOT);
		return result;
	}

	/**
	 * Completes a method-like construct. This begins with searching for the
	 * first semicolon or brace, i.e., the parameter list should already be
	 * skipped. This ends either in a complete method with a body, or with a
	 * semicolon and thus is just an abstract method.
	 */
	private void completeMethod(String name, EShallowEntityType nodeType,
			EGenericParserStates subParseState,
			RecognizerBase<EGenericParserStates> start) {
		RecognizerBase<EGenericParserStates> alternative = start
				.skipBefore(EnumSet.of(LBRACE, SEMICOLON));
		alternative.sequence(LBRACE).createNode(nodeType, name, 0)
				.parseUntil(subParseState).sequence(RBRACE).endNode();
		alternative.sequence(SEMICOLON)
				.createNode(nodeType, "abstract " + name, 0).endNode();
	}

	/** {@inheritDoc} */
	@Override
	protected void createCaseRule() {
		super.createCaseRule();

		// C# also allows fully/partially qualified constants
		inState(IN_METHOD)
				.markStart()
				.sequence(CASE)
				.subRecognizer(createExplicitInterfaceQualifierRecognizer(), 0,
						Integer.MAX_VALUE)
				.sequence(getValidIdentifiers(), COLON)
				.createNode(EShallowEntityType.META, 0).endNode();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Also returns all contextual keywords, as they are valid identifiers in
	 * the language. See http://msdn.microsoft.com/en-us/library/x53a06bb.aspx
	 * for the full list.
	 */
	@Override
	protected EnumSet<ETokenType> getValidIdentifiers() {
		return EnumSet
				.of(IDENTIFIER, ADD, ALIAS, ASCENDING, ASYNC, AWAIT,
						DESCENDING, DYNAMIC, FROM, GET, GLOBAL, GROUP, INTO,
						JOIN, LET, ORDERBY, PARTIAL, REMOVE, SELECT, SET,
						VALUE, VAR, WHERE, YIELD);
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getSimpleBlockKeywordsWithParentheses() {
		return EnumSet.of(WHILE, FOR, SWITCH, LOCK, USING, FIXED, FOREACH);
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getSimpleBlockKeywordsWithoutParentheses() {
		return EnumSet.of(ELSE, FINALLY, CHECKED, UNCHECKED, UNSAFE);
	}

	/** {@inheritDoc} */
	@Override
	protected EnumSet<ETokenType> getStatementStartTokens() {
		return EnumSet.of(NEW, BREAK, CONTINUE, RETURN, ASSERT, CONST, GOTO,
				BASE, THROW, THIS, CHECKED, SIZEOF, STACKALLOC, TYPEOF, VALUE,
				YIELD, LPAREN, PLUSPLUS, MINUSMINUS);
	}

	/** {@inheritDoc} */
	@Override
	protected RecognizerBase<EGenericParserStates> typePattern(
			RecognizerBase<EGenericParserStates> currentState) {
		EnumSet<ETokenType> typeStart = EnumSet.of(VOID, BYTE, SHORT, INT,
				LONG, FLOAT, DOUBLE, CHAR, BOOL, STRING, OBJECT, DECIMAL,
				SBYTE, USHORT, UINT, OPERATOR, ULONG);
		typeStart.addAll(getValidIdentifiers());

		// we include "?" in the skipping section to deal with nullable types
		return currentState.sequence(typeStart).skipNested(LT, GT)
				.skipAny(EnumSet.of(LBRACK, RBRACK, QUESTION));
	}
}
