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

import static org.conqat.engine.sourcecode.shallowparser.AdaShallowParser.EAdaParserStates.DECLARATIONS;
import static org.conqat.engine.sourcecode.shallowparser.AdaShallowParser.EAdaParserStates.STATEMENTS;
import static org.conqat.lib.scanner.ETokenType.ABORT;
import static org.conqat.lib.scanner.ETokenType.ABSTRACT;
import static org.conqat.lib.scanner.ETokenType.ACCEPT;
import static org.conqat.lib.scanner.ETokenType.AND;
import static org.conqat.lib.scanner.ETokenType.BEGIN;
import static org.conqat.lib.scanner.ETokenType.BODY;
import static org.conqat.lib.scanner.ETokenType.CASE;
import static org.conqat.lib.scanner.ETokenType.COLON;
import static org.conqat.lib.scanner.ETokenType.DECLARE;
import static org.conqat.lib.scanner.ETokenType.DELAY;
import static org.conqat.lib.scanner.ETokenType.DO;
import static org.conqat.lib.scanner.ETokenType.ELSE;
import static org.conqat.lib.scanner.ETokenType.ELSEIF;
import static org.conqat.lib.scanner.ETokenType.END;
import static org.conqat.lib.scanner.ETokenType.ENDRECORD;
import static org.conqat.lib.scanner.ETokenType.ENTRY;
import static org.conqat.lib.scanner.ETokenType.EQ;
import static org.conqat.lib.scanner.ETokenType.EXCEPTION;
import static org.conqat.lib.scanner.ETokenType.EXIT;
import static org.conqat.lib.scanner.ETokenType.FOR;
import static org.conqat.lib.scanner.ETokenType.FUNCTION;
import static org.conqat.lib.scanner.ETokenType.GENERIC;
import static org.conqat.lib.scanner.ETokenType.GOTO;
import static org.conqat.lib.scanner.ETokenType.GT;
import static org.conqat.lib.scanner.ETokenType.IDENTIFIER;
import static org.conqat.lib.scanner.ETokenType.IF;
import static org.conqat.lib.scanner.ETokenType.IS;
import static org.conqat.lib.scanner.ETokenType.LEFT_LABEL_BRACKET;
import static org.conqat.lib.scanner.ETokenType.LOOP;
import static org.conqat.lib.scanner.ETokenType.NEW;
import static org.conqat.lib.scanner.ETokenType.NULL;
import static org.conqat.lib.scanner.ETokenType.OR;
import static org.conqat.lib.scanner.ETokenType.PACKAGE;
import static org.conqat.lib.scanner.ETokenType.PRAGMA;
import static org.conqat.lib.scanner.ETokenType.PREPROCESSOR_DIRECTIVE;
import static org.conqat.lib.scanner.ETokenType.PROCEDURE;
import static org.conqat.lib.scanner.ETokenType.PROTECTED;
import static org.conqat.lib.scanner.ETokenType.RAISE;
import static org.conqat.lib.scanner.ETokenType.RECORD;
import static org.conqat.lib.scanner.ETokenType.RENAMES;
import static org.conqat.lib.scanner.ETokenType.RETURN;
import static org.conqat.lib.scanner.ETokenType.RIGHT_LABEL_BRACKET;
import static org.conqat.lib.scanner.ETokenType.SELECT;
import static org.conqat.lib.scanner.ETokenType.SEMICOLON;
import static org.conqat.lib.scanner.ETokenType.SEPARATE;
import static org.conqat.lib.scanner.ETokenType.STRING_LITERAL;
import static org.conqat.lib.scanner.ETokenType.SUBTYPE;
import static org.conqat.lib.scanner.ETokenType.TASK;
import static org.conqat.lib.scanner.ETokenType.TERMINATE;
import static org.conqat.lib.scanner.ETokenType.THEN;
import static org.conqat.lib.scanner.ETokenType.TYPE;
import static org.conqat.lib.scanner.ETokenType.USE;
import static org.conqat.lib.scanner.ETokenType.WHEN;
import static org.conqat.lib.scanner.ETokenType.WHILE;
import static org.conqat.lib.scanner.ETokenType.WITH;

import java.util.EnumSet;

import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.RecognizerBase;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowParserBase;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Shallow parser for Ada.
 * <p>
 * A good introduction to Ada can be found here:
 * http://en.wikibooks.org/wiki/Ada_Programming
 * <p>
 * A reference is available here:
 * http://www.adaic.org/resources/add_content/standards/05rm/html/RM-TOC.html
 * <p>
 * What this parser does and does not:
 * <ul>
 * <li>The "with" and "use" statements as well as pragmas are parsed as meta
 * data.</li>
 * <li>Packages, types, functions, procedures, entries are parsed as expected.</li>
 * <li>It recognizes the nesting of statements (e.g. in loops), but does not
 * parse into the statements. For example, it recognizes an if-statement and
 * provides the list of sub-statements, but does not provide direct access to
 * the if-condition.</li>
 * <li>The parser does not support non-ASCII characters in identifiers, although
 * Ada95 supports unicode here. Actually, this is not an issue of the parser but
 * the underlying scanner.</li>
 * </ul>
 * <p>
 * Implementation hints:
 * <ul>
 * <li>Several rules are registered for any state although they would be
 * expected to occur only in state DECLARATIONS. This is to allow the parser to
 * recover from state STATEMENTS, if an arbitrary chunk of Ada code is to be
 * parsed.</li>
 * </ul>
 * 
 * @author $Author: goede $
 * @version $Rev: 39142 $
 * @ConQAT.Rating GREEN Hash: ABCC6E35621219093804B96B38F1E4F8
 */
/* package */class AdaShallowParser extends
		ShallowParserBase<AdaShallowParser.EAdaParserStates> {

	/** The states used in this parser. */
	public static enum EAdaParserStates {

		/** A state to recognize declarations. */
		DECLARATIONS,

		/** A state to recognize statements. */
		STATEMENTS
	}

	/** Constructor. */
	public AdaShallowParser() {
		super(EAdaParserStates.class, DECLARATIONS);

		createMetaRules();
		createSpecificationRules();
		createBodyAndTypeRules();
		createMethodAndAttributeRules();
		createTypeRules();
		createStatementRules();
	}

	/** Create rules for parsing meta elements. */
	private void createMetaRules() {
		// parse generic prefix as meta
		// we skip from "with" to ";" to not stumble over subprogram parameters
		inAnyState()
				.sequence(GENERIC)
				.createNode(EShallowEntityType.META, 0)
				.skipBeforeWithNesting(
						EnumSet.of(PACKAGE, PROCEDURE, FUNCTION, ENTRY), WITH,
						SEMICOLON).endNodeWithContinuation();

		// parse use and with as meta
		inAnyState().sequence(EnumSet.of(WITH, USE, PRAGMA))
				.createNode(EShallowEntityType.META, 0).skipTo(SEMICOLON)
				.endNode();

		// parse pragma as meta
		inAnyState().sequence(PREPROCESSOR_DIRECTIVE)
				.createNode(EShallowEntityType.META, "pragma").endNode();

		// deal with dangling end by inserting broken node
		inAnyState().sequence(END)
				.createNode(EShallowEntityType.META, "dangling end")
				.skipTo(SEMICOLON); // endNode() omitted!
	}

	/** Creates parsing rules for package and task specifications. */
	private void createSpecificationRules() {
		// package specification
		RecognizerBase<EAdaParserStates> packageSpecAlternative = inAnyState()
				.sequence(PACKAGE, IDENTIFIER).skipBefore(
						EnumSet.of(SEMICOLON, IS, RENAMES));
		packageSpecAlternative
				.sequence(SEMICOLON)
				.createNode(EShallowEntityType.MODULE, "package specification",
						-2).endNode();
		packageSpecAlternative.sequence(RENAMES)
				.createNode(EShallowEntityType.MODULE, "package renaming", -2)
				.skipTo(SEMICOLON).endNode();
		packageSpecAlternative
				.sequence(IS, NEW)
				.createNode(EShallowEntityType.TYPE,
						"generic package instantiation", -3).skipTo(SEMICOLON)
				.endNode();
		packageSpecAlternative
				.sequence(IS)
				.createNode(EShallowEntityType.MODULE, "package specification",
						-2).parseUntil(DECLARATIONS).sequence(END)
				.skipTo(SEMICOLON).endNode();

		// task specification
		RecognizerBase<EAdaParserStates> taskSpecAlternative = inAnyState()
				.sequence(EnumSet.of(TASK, PROTECTED), IDENTIFIER)
				.createNode(EShallowEntityType.MODULE,
						new Object[] { 0, "specification" }, -1)
				.skipBefore(EnumSet.of(SEMICOLON, IS));
		taskSpecAlternative.sequence(SEMICOLON).endNode();
		taskSpecAlternative.sequence(IS).parseUntil(DECLARATIONS).sequence(END)
				.skipTo(SEMICOLON).endNode();
	}

	/**
	 * Creates parsing rules for package/task/protected body and task/protected
	 * type.
	 */
	private void createBodyAndTypeRules() {
		// package body, task body, protected body
		RecognizerBase<EAdaParserStates> packageBodyAlternative1 = inAnyState()
				.sequence(EnumSet.of(PACKAGE, TASK, PROTECTED), BODY,
						IDENTIFIER).skipTo(IS)
				.createNode(EShallowEntityType.MODULE, new int[] { 0, 1 }, -2);
		packageBodyAlternative1.sequence(SEPARATE, SEMICOLON).endNode();
		RecognizerBase<EAdaParserStates> packageBodyAlternative2 = packageBodyAlternative1
				.parseUntil(DECLARATIONS);
		packageBodyAlternative2.sequence(END).skipTo(SEMICOLON).endNode();
		completeBlock(packageBodyAlternative2.sequence(BEGIN));

		// task types and protected types
		RecognizerBase<EAdaParserStates> taskTypeAlternative = inAnyState()
				.sequence(EnumSet.of(TASK, PROTECTED), TYPE, IDENTIFIER)
				.createNode(EShallowEntityType.MODULE, new int[] { 0, 1 })
				.skipBefore(EnumSet.of(SEMICOLON, IS));
		taskTypeAlternative.sequence(SEMICOLON).endNode();
		taskTypeAlternative.sequence(IS).parseUntil(DECLARATIONS).sequence(END)
				.skipTo(SEMICOLON).endNode();

		// new...with is skipped
		inState(DECLARATIONS).sequence(NEW).skipTo(WITH);
	}

	/**
	 * Creates rules for parsing methods (functions, etc.) and attributes (and
	 * local variables).
	 */
	private void createMethodAndAttributeRules() {
		// functions, procedures (including operator overloading), entries
		RecognizerBase<EAdaParserStates> functionAlternative = inAnyState()
				.sequence(EnumSet.of(PROCEDURE, FUNCTION, ENTRY),
						EnumSet.of(IDENTIFIER, STRING_LITERAL))
				.createNode(EShallowEntityType.METHOD, 0, 1)
				.skipBefore(EnumSet.of(SEMICOLON, IS));
		functionAlternative.sequence(SEMICOLON).endNode();
		functionAlternative.sequence(IS, EnumSet.of(SEPARATE, ABSTRACT, NEW))
				.skipTo(SEMICOLON).endNode();
		completeBlock(functionAlternative.sequence(IS).parseUntil(DECLARATIONS)
				.sequence(BEGIN));

		// variables and constants
		inState(DECLARATIONS).sequence(IDENTIFIER, COLON)
				.createNode(EShallowEntityType.ATTRIBUTE, "variable", 0)
				.skipTo(SEMICOLON).endNode();
	}

	/** Creates rules for parsing types and similar constructs. */
	private void createTypeRules() {
		// types/subtypes
		RecognizerBase<EAdaParserStates> typeAlternative = inAnyState()
				.sequence(EnumSet.of(TYPE, SUBTYPE), IDENTIFIER)
				.createNode(EShallowEntityType.TYPE, 0, 1)
				.skipBefore(EnumSet.of(SEMICOLON, IS));
		typeAlternative.sequence(SEMICOLON).endNode();
		typeAlternative.sequence(IS, NULL, RECORD, SEMICOLON).endNode();

		RecognizerBase<EAdaParserStates> typeAlternative2 = typeAlternative
				.sequence(IS).skipBefore(EnumSet.of(SEMICOLON, RECORD, NULL));
		typeAlternative2.sequence(SEMICOLON).endNode();
		typeAlternative2.sequence(NULL).skipTo(SEMICOLON).endNode();
		typeAlternative2.sequence(RECORD).skipTo(END, ENDRECORD, SEMICOLON)
				.endNode();

		// representation clauses
		RecognizerBase<EAdaParserStates> overlayAlternative = inState(
				DECLARATIONS).sequence(FOR)
				.createNode(EShallowEntityType.TYPE, "representation clause")
				.skipBefore(EnumSet.of(SEMICOLON, RECORD));
		overlayAlternative.sequence(SEMICOLON).endNode();
		overlayAlternative.sequence(RECORD).skipTo(END, ENDRECORD, SEMICOLON)
				.endNode();
	}

	/** Creates the rules needed for parsing statements. */
	private void createStatementRules() {
		// if/elseif
		RecognizerBase<EAdaParserStates> ifAlternative = inState(STATEMENTS)
				.sequence(EnumSet.of(IF, ELSEIF))
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(THEN)
				.parseUntil(STATEMENTS)
				.sequenceBefore(EnumSet.of(ELSEIF, ELSE, END));
		ifAlternative.sequence(END).skipTo(SEMICOLON).endNode();
		ifAlternative.endNodeWithContinuation();

		// else (both for if and select)
		inState(STATEMENTS).sequence(ELSE)
				.createNode(EShallowEntityType.STATEMENT, 0)
				.parseUntil(STATEMENTS).sequence(END).skipTo(SEMICOLON)
				.endNode();

		// case
		inState(STATEMENTS).sequence(CASE)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(IS)
				.parseUntil(STATEMENTS).sequence(END).skipTo(SEMICOLON)
				.endNode();

		// when (in case, select, and exception handlers)
		inState(STATEMENTS).sequence(WHEN)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(EQ, GT)
				.endNode();

		// ignore loop/block labels
		inState(STATEMENTS).sequence(IDENTIFIER, COLON).sequenceBefore(
				EnumSet.of(WHILE, FOR, LOOP, BEGIN));

		// loops
		inState(STATEMENTS).sequence(LOOP)
				.createNode(EShallowEntityType.STATEMENT, 0)
				.parseUntil(STATEMENTS).sequence(END).skipTo(SEMICOLON)
				.endNode();
		inState(STATEMENTS).sequence(EnumSet.of(WHILE, FOR))
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(LOOP)
				.parseUntil(STATEMENTS).sequence(END).skipTo(SEMICOLON)
				.endNode();

		// blocks
		completeBlock(inState(STATEMENTS).sequence(IDENTIFIER, COLON, DECLARE)
				.createNode(EShallowEntityType.STATEMENT, "block")
				.parseUntil(DECLARATIONS).sequence(BEGIN));
		completeBlock(inState(STATEMENTS).sequence(DECLARE)
				.createNode(EShallowEntityType.STATEMENT, "block")
				.parseUntil(DECLARATIONS).sequence(BEGIN));
		completeBlock(inState(STATEMENTS).sequence(BEGIN).createNode(
				EShallowEntityType.STATEMENT, "block"));

		// accept/do
		inState(STATEMENTS).sequence(ACCEPT)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DO)
				.parseUntil(STATEMENTS).sequence(END).skipTo(SEMICOLON)
				.endNode();

		// select/or
		RecognizerBase<EAdaParserStates> selectAlternative = inState(STATEMENTS)
				.sequence(EnumSet.of(SELECT, OR, ELSE))
				.createNode(EShallowEntityType.STATEMENT, 0)
				.parseUntil(STATEMENTS);
		selectAlternative.sequenceBefore(EnumSet.of(OR, ELSE))
				.endNodeWithContinuation();
		selectAlternative.sequence(END).skipTo(SEMICOLON).endNode();

		// skip labels
		inState(STATEMENTS).sequence(LEFT_LABEL_BRACKET).skipTo(
				RIGHT_LABEL_BRACKET);

		// basic statement
		inState(STATEMENTS)
				.sequence(
						EnumSet.of(IDENTIFIER, NULL, RETURN, GOTO, EXIT, ABORT,
								DELAY, RAISE, TERMINATE))
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(SEMICOLON)
				.endNode();
	}

	/** Completes the rule describing blocks. */
	private void completeBlock(RecognizerBase<EAdaParserStates> initialSequence) {
		RecognizerBase<EAdaParserStates> alt = initialSequence
				.parseUntil(STATEMENTS);
		alt.sequence(EXCEPTION).parseUntil(STATEMENTS).sequence(END)
				.skipTo(SEMICOLON).endNode();
		alt.sequence(END).skipTo(SEMICOLON).endNode();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Maps "and then" and "or else" to simple "and" and "or", as the additional
	 * "then" and "else" keywords may shallow parsing much harder.
	 */
	@Override
	protected boolean isFilteredToken(IToken token, IToken previousToken) {
		ETokenType previousType = null;
		if (previousToken != null) {
			previousType = previousToken.getType();
		}
		ETokenType type = token.getType();

		if (previousType == AND && type == THEN) {
			return true;
		}
		if (previousType == OR && type == ELSE) {
			return true;
		}
		return super.isFilteredToken(token, previousToken);
	}
}
