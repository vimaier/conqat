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

import static org.conqat.engine.sourcecode.shallowparser.PlsqlShallowParser.EPlsqlParserStates.DECLARATIONS;
import static org.conqat.engine.sourcecode.shallowparser.PlsqlShallowParser.EPlsqlParserStates.STATEMENTS;
import static org.conqat.lib.scanner.ETokenType.*;

import java.util.Arrays;
import java.util.EnumSet;

import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.RecognizerBase;
import org.conqat.engine.sourcecode.shallowparser.framework.SequenceRecognizer.ITokenMatcher;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowParserBase;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Shallow parser for PL/SQL.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45275 $
 * @ConQAT.Rating GREEN Hash: 07B07D03FD56AD8882CBDD2B5ED459A6
 */
public class PlsqlShallowParser extends
		ShallowParserBase<PlsqlShallowParser.EPlsqlParserStates> {

	/** The states used in this parser. */
	public static enum EPlsqlParserStates {

		/** A state to recognize declarations. */
		DECLARATIONS,

		/** A state to recognize statements. */
		STATEMENTS
	}

	/**
	 * In PL/SQL all keywords may also be used as identifiers. There are also
	 * reserved words, which are not allowed as identifiers.
	 */
	private static final EnumSet<ETokenType> PLSQL_IDENTIFIERS = EnumSet.of(
			IDENTIFIER, A, ADD, AGENT, AGGREGATE, ARRAY, ATTRIBUTE, AUTHID,
			AVG, BFILE_BASE, BINARY, BLOB_BASE, BLOCK, BODY, BOTH, BOUND, BULK,
			BYTE, C, CALL, CALLING, CASCADE, CHAR, CHAR_BASE, CHARACTER,
			CHARSETFORM, CHARSETID, CHARSET, CLOB_BASE, CLOSE, COLLECT,
			COMMENT, COMMIT, COMMITTED, COMPILED, CONSTANT, CONSTRUCTOR,
			CONTEXT, CONTINUE, CONVERT, COUNT, CURSOR, CUSTOMDATUM, DANGLING,
			DATA, DATE, DATE_BASE, DAY, DEFINE, DETERMINISTIC, DOUBLE,
			DURATION, ELEMENT, ELSIF, EMPTY, ESCAPE, EXCEPT, EXCEPTIONS,
			EXECUTE, EXIT, EXTERNAL, FINAL, FIXED, FLOAT, FORALL, FORCE,
			FUNCTION, GENERAL, HASH, HEAP, HIDDEN, HOUR, IMMEDIATE, INCLUDING,
			INDICATOR, INDICES, INFINITE, INSTANTIABLE, INT, INTERFACE,
			INTERVAL, INVALIDATE, ISOLATION, JAVA, LANGUAGE, LARGE, LEADING,
			LENGTH, LEVEL, LIBRARY, LIKE2, LIKE4, LIKEC, LIMIT, LIMITED, LOCAL,
			LONG, LOOP, MAP, MAX, MAXLEN, MEMBER, MERGE, MIN, MINUTE, MOD,
			MODIFY, MONTH, MULTISET, NAME, NAN, NATIONAL, NATIVE, NCHAR, NEW,
			NOCOPY, NUMBER_BASE, OBJECT, OCICOLL, OCIDATETIME, OCIDATE,
			OCIDURATION, OCIINTERVAL, OCILOBLOCATOR, OCINUMBER, OCIRAW,
			OCIREFCURSOR, OCIREF, OCIROWID, OCISTRING, OCITYPE, ONLY, OPAQUE,
			OPEN, OPERATOR, ORACLE, ORADATA, ORGANIZATION, ORLANY, ORLVARY,
			OTHERS, OUT, OVERRIDING, PACKAGE, PARALLEL_ENABLE, PARAMETER,
			PARAMETERS, PARTITION, PASCAL, PIPE, PIPELINED, PRAGMA, PRECISION,
			PRIVATE, RAISE, RANGE, RAW, READ, RECORD, REF, REFERENCE,
			RELIES_ON, REM, REMAINDER, RENAME, REPLACE, RESULT, RESULT_CACHE,
			RETURN, RETURNING, REVERSE, ROLLBACK, ROW, SAMPLE, SAVE, SAVEPOINT,
			SB1, SB2, SB4, SECOND, SEGMENT, SELF, SEPARATE, SEQUENCE,
			SERIALIZABLE, SET, SHORT, SIZE_T, SOME, SPARSE, SQLCODE, SQLDATA,
			SQLNAME, SQLSTATE, STANDARD, STATIC, STDDEV, STORED, STRING,
			STRUCT, STYLE, SUBMULTISET, SUBPARTITION, SUBSTITUTABLE, SUBTYPE,
			SUM, SYNONYM, TDO, THE, TIME, TIMESTAMP, TIMEZONE_ABBR,
			TIMEZONE_HOUR, TIMEZONE_MINUTE, TIMEZONE_REGION, TRAILING,
			TRANSACTION, TRANSACTIONAL, TRUSTED, TYPE, UB1, UB2, UB4, UNDER,
			UNSIGNED, UNTRUSTED, USE, USING, VALIST, VALUE, VARIABLE, VARIANCE,
			VARRAY, VARYING, VOID, WHILE, WORK, WRAPPED, WRITE, YEAR, ZONE,
			// these are not "official" keywords, but may be used as identifiers
			// as well
			DELETE, ON, OFF);

	/** Constructor. */
	public PlsqlShallowParser() {
		super(EPlsqlParserStates.class, DECLARATIONS);

		createMetaRules();
		createPackageAndTypeRules();
		createMethodAndAttributeRules();
		createStatementRules();
	}

	/** Create rules for parsing meta elements. */
	private void createMetaRules() {

		// SHOW ERROR is tricky, as it allows for multiple abbreviations
		inState(DECLARATIONS)
				.sequence(new IdentifierPrefixMatcher("sho"),
						new IdentifierPrefixMatcher("err")).optional(SEMICOLON)
				.createNode(EShallowEntityType.META, "show errors").endNode();

		// exit
		inState(DECLARATIONS).sequence(EXIT).optional(INTEGER_LITERAL)
				.optional(SEMICOLON)
				.createNode(EShallowEntityType.META, "exit").endNode();

		// pragma
		inAnyState().sequence(PRAGMA)
				.createNode(EShallowEntityType.META, "pragma")
				.skipTo(SEMICOLON).endNode();

		// exception section
		inState(STATEMENTS).sequence(EXCEPTION)
				.createNode(EShallowEntityType.META, "exception section")
				.endNode();

		// single and double 'at' sign execution; see
		// http://docs.oracle.com/cd/B19306_01/server.102/b14357/ch12003.htm#BACIEHDJ
		inState(DECLARATIONS).sequence(AT).optional(AT)
				.createNode(EShallowEntityType.META, "run script")
				.repeated(EnumSet.of(IDENTIFIER, DOT, MINUS, MOD)).endNode();

		// SQL statements
		inAnyState()
				.sequence(
						EnumSet.of(AGGREGATE, ALTER, COMMIT, DELETE, GRANT,
								INSERT, LOCK, ROLLBACK, SAVEPOINT, SELECT,
								DROP, MERGE, UPDATE))
				.createNode(EShallowEntityType.STATEMENT, "SQL", 0)
				.skipTo(SEMICOLON).endNode();
		inAnyState().sequence(SET, TRANSACTION)
				.createNode(EShallowEntityType.STATEMENT, "SQL", 0)
				.skipTo(SEMICOLON).endNode();
		inAnyState().sequence(CREATE).optional(OR, REPLACE).optional(PUBLIC)
				.sequence(SYNONYM)
				.createNode(EShallowEntityType.STATEMENT, "SQL", 0)
				.skipTo(SEMICOLON).endNode();

		// set
		EnumSet<ETokenType> setIdentifiers = EnumSet.of(IDENTIFIER, DEFINE);
		inState(DECLARATIONS).sequence(SET, setIdentifiers, PLSQL_IDENTIFIERS)
				.createNode(EShallowEntityType.META, "set")
				.repeated(setIdentifiers, PLSQL_IDENTIFIERS)
				.optional(SEMICOLON).endNode();

		// deal with dangling end by inserting broken node
		inAnyState().sequence(END)
				.createNode(EShallowEntityType.META, "dangling end")
				.skipTo(SEMICOLON); // endNode() omitted!
	}

	/** Creates parsing rules for packages and types. */
	private void createPackageAndTypeRules() {

		// packages
		RecognizerBase<EPlsqlParserStates> optionalBeginAlternative = createOrReplace()
				.sequence(PACKAGE)
				.optional(BODY)
				.markStart()
				.repeated(PLSQL_IDENTIFIERS, DOT)
				.sequence(PLSQL_IDENTIFIERS)
				.createNode(EShallowEntityType.MODULE, "package",
						new Region(0, -1)).skipTo(EnumSet.of(IS, AS))
				.parseUntil(DECLARATIONS);
		optionalBeginAlternative.sequence(BEGIN).parseUntil(STATEMENTS)
				.sequence(END).skipTo(SEMICOLON).endNode();
		optionalBeginAlternative.sequence(END).skipTo(SEMICOLON).endNode();

		// type body
		createOrReplace()
				.sequence(TYPE, BODY)
				.markStart()
				.repeated(PLSQL_IDENTIFIERS, DOT)
				.sequence(PLSQL_IDENTIFIERS)
				.createNode(EShallowEntityType.MODULE, "type body",
						new Region(0, -1)).skipTo(EnumSet.of(IS, AS))
				.parseUntil(DECLARATIONS).sequence(END).skipTo(SEMICOLON)
				.endNode();

		// type
		RecognizerBase<EPlsqlParserStates> typeMatcher = createOrReplace()
				.markStart().sequence(TYPE).repeated(PLSQL_IDENTIFIERS, DOT)
				.sequence(PLSQL_IDENTIFIERS)
				.createNode(EShallowEntityType.TYPE, 0, new Region(1, -1))
				.skipBefore(EnumSet.of(IS, AS, UNDER, SEMICOLON, DIV));
		typeMatcher.sequence(EnumSet.of(SEMICOLON, DIV)).endNode();
		RecognizerBase<EPlsqlParserStates> typeMatcher2 = typeMatcher
				.skipBefore(EnumSet.of(IS, AS, UNDER)).optional(
						EnumSet.of(IS, AS));
		typeMatcher2.sequence(EnumSet.of(OBJECT, UNDER)).skipTo(LPAREN)
				.parseUntil(DECLARATIONS)
				// closing paren is swallowed by decl rules
				.repeated(EnumSet.of(NOT, FINAL, INSTANTIABLE))
				.sequence(EnumSet.of(SEMICOLON, DIV)).endNode();
		typeMatcher2.skipTo(EnumSet.of(SEMICOLON, DIV)).endNode();

		// top-level code block
		inState(DECLARATIONS).sequence(BEGIN)
				.createNode(EShallowEntityType.METHOD, "top-level code")
				.parseUntil(STATEMENTS).sequence(END).skipTo(SEMICOLON)
				.endNode();
	}

	/** Create parser rules for functions, procedures, constructors, etc. */
	private void createMethodAndAttributeRules() {

		// function/procedure
		RecognizerBase<EPlsqlParserStates> methodStart = createOrReplace()
				.repeated(
						EnumSet.of(MAP, NOT, OVERRIDING, ORDER, FINAL,
								INSTANTIABLE, MEMBER, STATIC, CONSTRUCTOR))
				.markStart().sequence(EnumSet.of(PROCEDURE, FUNCTION))
				.repeated(PLSQL_IDENTIFIERS, DOT).sequence(PLSQL_IDENTIFIERS)
				.createNode(EShallowEntityType.METHOD, 0, new Region(1, -1))
				.skipNested(LPAREN, RPAREN).optional(RETURN, SELF, AS, RESULT)
				.skipBefore(EnumSet.of(SEMICOLON, IS, AS, RPAREN, COMMA));
		methodStart.sequence(EnumSet.of(SEMICOLON, RPAREN, COMMA)).endNode();
		RecognizerBase<EPlsqlParserStates> methodStart2 = methodStart
				.sequence(EnumSet.of(IS, AS));
		methodStart2.sequence(EnumSet.of(LANGUAGE, EXTERNAL)).skipTo(SEMICOLON)
				.endNode();
		methodStart2
				.parseUntil(DECLARATIONS)
				.sequence(BEGIN)
				.parseUntil(STATEMENTS)
				.sequence(END)
				.skipToWithNesting(EnumSet.of(SEMICOLON, RPAREN, COMMA),
						LPAREN, RPAREN).endNode();

		// exception declaration
		inState(DECLARATIONS)
				.sequence(PLSQL_IDENTIFIERS, EXCEPTION, SEMICOLON)
				.createNode(EShallowEntityType.META, "exception declaration", 0)
				.endNode();

		// trigger
		RecognizerBase<EPlsqlParserStates> triggerMatch = createOrReplace()
				.markStart().sequence(TRIGGER).repeated(PLSQL_IDENTIFIERS, DOT)
				.sequence(PLSQL_IDENTIFIERS)
				.createNode(EShallowEntityType.METHOD, 0, new Region(1, -1))
				.skipBefore(EnumSet.of(SEMICOLON, DECLARE, BEGIN));
		triggerMatch.sequence(SEMICOLON).endNode();
		triggerMatch.sequence(BEGIN).parseUntil(STATEMENTS).sequence(END)
				.skipTo(SEMICOLON).endNode();
		triggerMatch.sequence(DECLARE).parseUntil(DECLARATIONS).sequence(BEGIN)
				.parseUntil(STATEMENTS).sequence(END).skipTo(SEMICOLON)
				.endNode();

		// cursor declaration
		inState(DECLARATIONS).sequence(CURSOR).skipTo(SEMICOLON)
				.createNode(EShallowEntityType.ATTRIBUTE, "cursor", 1)
				.endNode();

		// variables and constants
		inState(DECLARATIONS)
				.sequence(PLSQL_IDENTIFIERS)
				.createNode(EShallowEntityType.ATTRIBUTE, "variable", 0)
				.skipToWithNesting(EnumSet.of(SEMICOLON, RPAREN, COMMA),
						LPAREN, RPAREN).endNode();
	}

	/** Matches the optional CREATE OR REPLACE clause. */
	private RecognizerBase<EPlsqlParserStates> createOrReplace() {
		return inState(DECLARATIONS).optional(CREATE).optional(OR, REPLACE);
	}

	/** Creates parser rules for statements. */
	private void createStatementRules() {
		// if/elseif
		RecognizerBase<EPlsqlParserStates> ifAlternative = inState(STATEMENTS)
				.sequence(EnumSet.of(IF, ELSIF))
				.createNode(EShallowEntityType.STATEMENT, 0)
				.skipToWithNesting(THEN, CASE, END).parseUntil(STATEMENTS)
				.sequenceBefore(EnumSet.of(ELSIF, ELSE, END));
		ifAlternative.sequence(END, IF, SEMICOLON).endNode();
		ifAlternative.endNodeWithContinuation();

		// else (both for if and case)
		RecognizerBase<EPlsqlParserStates> elseMatcher = inState(STATEMENTS)
				.sequence(ELSE).createNode(EShallowEntityType.STATEMENT, 0)
				.parseUntil(STATEMENTS);
		elseMatcher.sequence(END, IF).skipTo(SEMICOLON).endNode();
		elseMatcher.sequenceBefore(END, CASE).endNode();

		// loops
		inState(STATEMENTS).sequence(LOOP)
				.createNode(EShallowEntityType.STATEMENT, 0)
				.parseUntil(STATEMENTS).sequence(END, LOOP).skipTo(SEMICOLON)
				.endNode();
		inState(STATEMENTS).sequence(EnumSet.of(WHILE, FOR))
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(LOOP)
				.parseUntil(STATEMENTS).sequence(END, LOOP).skipTo(SEMICOLON)
				.endNode();

		// blocks
		inState(STATEMENTS).sequence(DECLARE)
				.createNode(EShallowEntityType.STATEMENT, "block")
				.parseUntil(DECLARATIONS).sequence(BEGIN)
				.parseUntil(STATEMENTS).sequence(END).skipTo(SEMICOLON)
				.endNode();
		inState(STATEMENTS).sequence(BEGIN)
				.createNode(EShallowEntityType.STATEMENT, "block")
				.parseUntil(STATEMENTS).sequence(END).skipTo(SEMICOLON)
				.endNode();

		// case
		inState(STATEMENTS).sequence(CASE)
				.createNode(EShallowEntityType.STATEMENT, 0)
				.skipBefore(EnumSet.of(WHEN, ELSE)).parseUntil(STATEMENTS)
				.sequence(END, CASE).skipTo(SEMICOLON).endNode();

		// when (in exceptions or case)
		inState(STATEMENTS).sequence(WHEN).skipTo(THEN)
				.createNode(EShallowEntityType.META, "when", 1).endNode();

		// labels
		inState(STATEMENTS)
				.sequence(LEFT_LABEL_BRACKET, PLSQL_IDENTIFIERS,
						RIGHT_LABEL_BRACKET)
				.createNode(EShallowEntityType.META, "label", 1).endNode();

		// basic statement
		EnumSet<ETokenType> basicStatementStarts = EnumSet
				.copyOf(PLSQL_IDENTIFIERS);
		basicStatementStarts.addAll(Arrays.asList(SELF, RETURN, GOTO, FETCH,
				NULL_LITERAL));
		inState(STATEMENTS).sequence(basicStatementStarts)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(SEMICOLON)
				.endNode();
	}

	/** Matcher for identifiers by a prefix. */
	private static class IdentifierPrefixMatcher implements ITokenMatcher {

		/** The prefix. */
		private final String prefix;

		/** Constructor. */
		public IdentifierPrefixMatcher(String prefix) {
			this.prefix = prefix.toLowerCase();
		}

		/** {@inheritDoc} */
		@Override
		public boolean matches(IToken token) {
			return token.getType() == IDENTIFIER
					&& token.getText().toLowerCase().startsWith(prefix);
		}
	}
}
