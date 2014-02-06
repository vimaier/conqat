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

import static org.conqat.engine.sourcecode.shallowparser.AbapShallowParser.EAbapParserStates.DECLARATIONS;
import static org.conqat.engine.sourcecode.shallowparser.AbapShallowParser.EAbapParserStates.STATEMENTS;
import static org.conqat.engine.sourcecode.shallowparser.AbapShallowParser.EAbapParserStates.TOPLEVEL;
import static org.conqat.lib.scanner.ETokenType.*;

import java.util.EnumSet;
import java.util.List;

import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ParserState;
import org.conqat.engine.sourcecode.shallowparser.framework.RecognizerBase;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowParserBase;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Shallow parser for ABAP. The following links are useful for writing the
 * parser:
 * <ul>
 * <li><a href="http://help.sap.com/abapdocu_702/en/">ABAP Keyword
 * Documentation</a></li>
 * <li><a
 * href="http://help.sap.com/abapdocu_702/en/abenabap_statements_overview.htm"
 * >ABAP Statements Overview</a></li>
 * </ul>
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46995 $
 * @ConQAT.Rating GREEN Hash: 59E954DD30BAF7C74912FAC5BD611F0E
 */
public class AbapShallowParser extends
		ShallowParserBase<AbapShallowParser.EAbapParserStates> {

	/** Tokens that can introduce a simple statement. */
	private static final EnumSet<ETokenType> SIMPLE_STATEMENT_START_TOKENS = EnumSet
			.of(ADD, ADD_CORRESPONDING, APPEND, ASSERT, ASSIGN,
					AUTHORITY_CHECK, BACK, BREAK_POINT, CALL, CHECK, CLEAR,
					CLOSE, COLLECT, COMMIT, COMMUNICATION, COMPUTE,
					CONCATENATE, CONDENSE, CONSTANTS, CONTEXTS, CONTINUE,
					CONTROLS, CONVERT, CREATE, DATA, DELETE, DEMAND, DESCRIBE,
					DETAIL, DIVIDE, DIVIDE_CORRESPONDING, EDITOR_CALL,
					ENHANCEMENT_POINT, EXISTS, EXIT, EXPORT, EXTRACT, FETCH,
					FIELDS, FIND, FORMAT, FREE, GENERATE, GET, HIDE,
					IDENTIFIER, IMPORT, INCLUDE, INFOTYPES, INPUT, INSERT,
					LEAVE, LOAD, LOCAL, LOG_POINT, MAXIMUM, MESSAGE, MINIMUM,
					MODIFY, MOVE, MOVE_CORRESPONDING, MULTIPLY_CORRESPONDING,
					MULTIPLY, NAME, NEW_LINE, NEW_PAGE, NEW_SECTION, OPEN,
					OVERLAY, PACK, PACKAGE, PERFORM, POSITION, PRINT_CONTROL,
					PUT, RAISE, RANGES, READ, REFRESH, REJECT, REPLACE,
					RESERVE, RESUME, RETURN, ROLLBACK, SCROLL, SEARCH, SET,
					SHIFT, SKIP, SORT, SPLIT, STOP, SUBMIT, SUBTRACT,
					SUBTRACT_CORRESPONDING, SUM, SUMMARY, SUMMING, SUPPLY,
					SUPPRESS, SYNTAX_CHECK, TRANSFER, TRANSLATE, TRUNCATE,
					TYPES, ULINE, UNPACK, UPDATE, WAIT, WINDOW, WRITE);

	/** The states used in this parser. */
	public static enum EAbapParserStates {

		/**
		 * Top level state used for parsing constructs that are not nested in
		 * other constructs.
		 */
		TOPLEVEL,

		/**
		 * A state to recognize declarations within classes. As many constructs
		 * are allowed both top-level and in declarations, many rules are
		 * registered for both.
		 */
		DECLARATIONS,

		/** A state to recognize statements, i.e. plain code in functions, etc. */
		STATEMENTS
	}

	/** Constructor. */
	public AbapShallowParser() {
		super(EAbapParserStates.class, TOPLEVEL);

		createMetaRules();
		createTopLevelRules();
		createTypeRules();
		createMethodAndAttributeRules();
		createStatementRules();

		inAnyState()
				.sequence(DOT)
				.createNode(EShallowEntityType.STATEMENT,
						SubTypeNames.EMPTY_STATEMENT).endNode();
	}

	/** Rules for parsing elements that are only expected top-level. */
	private void createTopLevelRules() {

		// set of keywords that start an event block (without keywords that
		// require a preceeding "at")
		EnumSet<ETokenType> eventBlocks = EnumSet.of(INITIALIZATION,
				START_OF_SELECTION, END_OF_SELECTION, TOP_OF_PAGE, END_OF_PAGE,
				LOAD_OF_PROGRAM, GET);

		// set of keywords that end an event block (possibly indicating the
		// start of the next one)
		EnumSet<ETokenType> eventBlocksEnd = EnumSet.of(AT, FORM, CLASS,
				INTERFACE);
		eventBlocksEnd.addAll(eventBlocks);

		// since the report is not really a method, its statements are still
		// parsed in the toplevel scope, not the statement scope
		inState(TOPLEVEL).sequence(REPORT)
				.createNode(EShallowEntityType.METHOD, 0, 1).skipTo(DOT)
				.parseUntilOrEof(TOPLEVEL).sequenceBefore(eventBlocksEnd)
				.endNode();

		inState(TOPLEVEL)
				.sequence(
						EnumSet.of(SELECTION_SCREEN, PARAMETER, SELECT_OPTIONS))
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.endNode();

		inState(TOPLEVEL).sequence(eventBlocks)
				.createNode(EShallowEntityType.METHOD, 0).skipTo(DOT)
				.parseUntilOrEof(STATEMENTS).sequenceBefore(eventBlocksEnd)
				.endNode();
		inState(TOPLEVEL)
				.sequence(
						AT,
						EnumSet.of(SELECTION_SCREEN, LINE_SELECTION,
								USER_COMMAND))
				.createNode(EShallowEntityType.METHOD, new int[] { 0, 1 })
				.skipTo(DOT).parseUntilOrEof(STATEMENTS)
				.sequenceBefore(eventBlocksEnd).endNode();
	}

	/** Rules for parsing of meta elements. */
	private void createMetaRules() {

		inState(DECLARATIONS)
				.sequence(EnumSet.of(PUBLIC, PROTECTED, PRIVATE), SECTION, DOT)
				.createNode(EShallowEntityType.META, SubTypeNames.VISIBILITY, 0)
				.endNode();

		inAnyState().sequence(EnumSet.of(TYPE_POOLS, TABLES, PARAMETERS))
				.createNode(EShallowEntityType.META, 0).skipTo(DOT).endNode();

		inAnyState().sequence(DEFINE)
				.createNode(EShallowEntityType.META, SubTypeNames.MACRO)
				.skipTo(END_OF_DEFINITION, DOT).endNode();

		inState(DECLARATIONS).sequence(EnumSet.of(INTERFACES, ALIASES))
				.createNode(EShallowEntityType.META, 0).skipTo(DOT).endNode();
	}

	/** Rules for parsing types. */
	private void createTypeRules() {

		// classes
		RecognizerBase<EAbapParserStates> classDefinitionAlternative = inState(
				TOPLEVEL, DECLARATIONS).sequence(CLASS, IDENTIFIER, DEFINITION);
		classDefinitionAlternative
				.sequence(EnumSet.of(LOAD, DEFERRED, LOCAL))
				.createNode(EShallowEntityType.TYPE,
						SubTypeNames.CLASS_PUBLICATION, 1).skipTo(DOT)
				.endNode();
		classDefinitionAlternative
				.createNode(EShallowEntityType.TYPE,
						SubTypeNames.CLASS_DEFINITION, 1).skipTo(DOT)
				.parseUntil(DECLARATIONS).sequence(ENDCLASS, DOT).endNode();

		inState(TOPLEVEL, DECLARATIONS)
				.sequence(CLASS, IDENTIFIER, IMPLEMENTATION)
				.createNode(EShallowEntityType.TYPE,
						SubTypeNames.CLASS_IMPLEMENTATION, 1).skipTo(DOT)
				.parseUntil(DECLARATIONS).sequence(ENDCLASS, DOT).endNode();

		// interfaces
		RecognizerBase<EAbapParserStates> interfaceAlternative = inState(
				TOPLEVEL, DECLARATIONS).sequence(INTERFACE, IDENTIFIER);
		interfaceAlternative
				.sequence(EnumSet.of(LOAD, DEFERRED, LOCAL))
				.createNode(EShallowEntityType.TYPE,
						SubTypeNames.INTERFACE_PUBLICATION, 1).skipTo(DOT)
				.endNode();
		interfaceAlternative
				.createNode(EShallowEntityType.TYPE,
						SubTypeNames.INTERFACE_DEFINITION, 1).skipTo(DOT)
				.parseUntil(DECLARATIONS).sequence(ENDINTERFACE, DOT).endNode();

		// types, events, class events
		inState(TOPLEVEL, DECLARATIONS)
				.sequence(EnumSet.of(TYPES, EVENTS, CLASS_EVENTS))
				.createNode(EShallowEntityType.ATTRIBUTE, 0).skipTo(DOT)
				.endNode();
	}

	/** Rules for parsing attributes/methods. */
	private void createMethodAndAttributeRules() {
		inState(TOPLEVEL, DECLARATIONS)
				.sequence(EnumSet.of(CONSTANTS, NODES, STATICS))
				.createNode(EShallowEntityType.ATTRIBUTE, 0, 1).skipTo(DOT)
				.endNode();
		inState(TOPLEVEL, DECLARATIONS)
				.sequence(EnumSet.of(DATA, FIELD_GROUPS, CLASS_DATA))
				.createNode(EShallowEntityType.ATTRIBUTE, 0, 1).skipTo(DOT)
				.endNode();
		inState(TOPLEVEL, DECLARATIONS, STATEMENTS)
				.sequence(EnumSet.of(FIELD_SYMBOLS))
				.createNode(EShallowEntityType.ATTRIBUTE, 0, 1).skipTo(DOT)
				.endNode();

		inState(DECLARATIONS)
				.sequence(EnumSet.of(METHODS, CLASS_METHODS))
				.createNode(EShallowEntityType.METHOD,
						SubTypeNames.METHOD_DECLARATION, 1).skipTo(DOT)
				.endNode();

		inState(DECLARATIONS)
				.sequence(METHOD)
				.markStart()
				.skipTo(DOT)
				.createNode(EShallowEntityType.METHOD,
						SubTypeNames.METHOD_IMPLEMENTATION, new Region(0, -2))
				.parseUntil(STATEMENTS).sequence(ENDMETHOD, DOT).endNode();

		inState(TOPLEVEL, DECLARATIONS)
				.sequence(FUNCTION)
				.markStart()
				.skipTo(DOT)
				.createNode(EShallowEntityType.METHOD, SubTypeNames.FUNCTION,
						new Region(0, -2)).parseUntil(STATEMENTS)
				.sequence(ENDFUNCTION, DOT).endNode();
		inState(TOPLEVEL, DECLARATIONS)
				.sequence(MODULE)
				.markStart()
				.skipTo(DOT)
				.createNode(EShallowEntityType.METHOD, SubTypeNames.MODULE,
						new Region(0, -2)).parseUntil(STATEMENTS)
				.sequence(ENDMODULE, DOT).endNode();

		inState(TOPLEVEL).sequence(FORM)
				.createNode(EShallowEntityType.METHOD, SubTypeNames.FORM, 1)
				.skipTo(DOT).parseUntil(STATEMENTS).sequence(ENDFORM, DOT)
				.endNode();
	}

	/** Rules for parsing statements. */
	private void createStatementRules() {

		// special rule that matches assignments to variables that have the same
		// name as keywords.
		inState(STATEMENTS).sequence(ETokenClass.KEYWORD, EQ)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.endNode();

		// if/elseif
		RecognizerBase<EAbapParserStates> ifAlternative = inState(TOPLEVEL,
				STATEMENTS).sequence(EnumSet.of(IF, ELSEIF))
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.parseUntil(STATEMENTS)
				.sequenceBefore(EnumSet.of(ELSEIF, ELSE, ENDIF));
		ifAlternative.sequence(ENDIF, DOT).endNode();
		ifAlternative.endNodeWithContinuation();

		// else
		inState(TOPLEVEL, STATEMENTS).sequence(ELSE)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.parseUntil(STATEMENTS).sequence(EnumSet.of(ENDIF, ENDON), DOT)
				.endNode();

		// case/when
		inState(TOPLEVEL, STATEMENTS).sequence(CASE)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.parseUntil(STATEMENTS).sequence(ENDCASE, DOT).endNode();
		// we parse when as meta, so we add no additional nesting
		inState(STATEMENTS).sequence(WHEN)
				.createNode(EShallowEntityType.META, 0).skipTo(DOT).endNode();

		// on change
		RecognizerBase<EAbapParserStates> changeAlternative = inAnyState()
				.sequence(ON, CHANGE, OF)
				.createNode(EShallowEntityType.STATEMENT,
						SubTypeNames.ON_CHANGE).skipTo(DOT)
				.parseUntil(STATEMENTS).sequenceBefore(EnumSet.of(ELSE, ENDON));
		changeAlternative.sequence(ENDON, DOT).endNode();
		changeAlternative.endNodeWithContinuation();

		// loops
		inState(TOPLEVEL, STATEMENTS).sequence(LOOP)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.parseUntil(STATEMENTS).sequence(ENDLOOP, DOT).endNode();
		inState(TOPLEVEL, STATEMENTS).sequence(DO)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.parseUntil(STATEMENTS).sequence(ENDDO, DOT).endNode();
		inState(TOPLEVEL, STATEMENTS).sequence(WHILE)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.parseUntil(STATEMENTS).sequence(ENDWHILE, DOT).endNode();
		inState(STATEMENTS).sequence(AT)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.parseUntil(STATEMENTS).sequence(ENDAT, DOT).endNode();

		// loop likes
		inAnyState().sequence(PROVIDE)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.parseUntil(STATEMENTS).sequence(ENDPROVIDE, DOT).endNode();
		inAnyState().sequence(ENHANCEMENT)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.parseUntil(STATEMENTS).sequence(ENDENHANCEMENT, DOT).endNode();
		inAnyState().sequence(ENHANCEMENT_SECTION)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.parseUntil(STATEMENTS).sequence(END_ENHANCEMENT_SECTION, DOT)
				.endNode();

		// try/catch
		RecognizerBase<EAbapParserStates> tryAlternative = inState(TOPLEVEL,
				STATEMENTS).sequence(EnumSet.of(TRY, CATCH, CLEANUP))
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.parseUntil(STATEMENTS)
				.sequenceBefore(EnumSet.of(ENDTRY, CATCH, ENDCATCH, CLEANUP));
		tryAlternative.sequence(EnumSet.of(ENDTRY, ENDCATCH), DOT).endNode();
		tryAlternative.endNodeWithContinuation();

		createSelectRules();

		// exec
		inState(TOPLEVEL, STATEMENTS)
				.sequence(EXEC, SQL)
				.createNode(EShallowEntityType.STATEMENT,
						SubTypeNames.NATIVE_SQL).skipTo(ENDEXEC, DOT).endNode();

		inState(TOPLEVEL, STATEMENTS).sequence(SIMPLE_STATEMENT_START_TOKENS)
				.createNode(EShallowEntityType.STATEMENT, 0).skipTo(DOT)
				.endNode();
	}

	/**
	 * Creates the parsing rules for the select clause. This is tricky, because
	 * the rules whether a select block or a single statement select is
	 * expected, are not trivial.
	 */
	private void createSelectRules() {
		RecognizerBase<EAbapParserStates> selectAlternative = inState(TOPLEVEL,
				STATEMENTS).sequence(SELECT);
		selectAlternative.sequence(LPAREN)
				.createNode(EShallowEntityType.STATEMENT, "method call")
				.skipToWithNesting(RPAREN, LPAREN, RPAREN).skipTo(DOT)
				.endNode();
		selectAlternative
				.subRecognizer(new SingleSelectRecognizer(), 1, 1)
				.createNode(EShallowEntityType.STATEMENT,
						SubTypeNames.SINGLE_SELECT).endNode();
		selectAlternative
				.createNode(EShallowEntityType.STATEMENT,
						SubTypeNames.SELECT_BLOCK).skipTo(DOT)
				.parseUntil(STATEMENTS).sequence(ENDSELECT, DOT).endNode();
	}

	/**
	 * Recognizer that matches single statements selects according to the rules
	 * found <a
	 * href="http://help.sap.com/abapdocu_702/en/abapselect.htm">here</a>. The
	 * recognozer should be called directly after finding the SELECT keyword.
	 */
	private static class SingleSelectRecognizer extends
			RecognizerBase<EAbapParserStates> {

		/**
		 * Token types to be skipped from the select start to reach the result
		 * description.
		 */
		private static final EnumSet<ETokenType> SELECT_TO_RESULTS_SKIP_TOKENS = EnumSet
				.of(SINGLE, FOR, UPDATE, DISTINCT);

		/** Token types for aggregate functions. */
		private static final EnumSet<ETokenType> AGGREGATE_FUNCTIONS = EnumSet
				.of(MIN, MAX, SUM, AVG, COUNT);

		/** Token types that terminate the aggregate functions. */
		private static final EnumSet<ETokenType> AGGREGATE_TERMINATOR = EnumSet
				.of(FROM, INTO);

		/** {@inheritDoc} */
		@Override
		protected int matchesLocally(
				ParserState<EAbapParserStates> parserState,
				List<IToken> tokens, int startOffset) {
			int dotOffset = startOffset;
			while (dotOffset < tokens.size()
					&& tokens.get(dotOffset).getType() != DOT) {
				dotOffset += 1;
			}

			// no match if closing dot was not found
			if (dotOffset >= tokens.size()) {
				return NO_MATCH;
			}

			int matchSingleSelect = dotOffset + 1;

			// the following is statements correspond directly to the rules in
			// http://help.sap.com/abapdocu_702/en/abapselect.htm, where a
			// result of matchSingleSelect means that no ENDSELECT is expected,
			// while a NO_MATCH indicates that an ENDSELECT is required
			if (!hasIntoAppendingTable(tokens, startOffset, dotOffset)) {
				if (isSingle(tokens, startOffset)
						|| (hasOnlyAggregateFunctions(tokens, startOffset,
								dotOffset) && !hasGroupBy(tokens, startOffset,
								dotOffset))) {
					return matchSingleSelect;
				}
				return NO_MATCH;
			}
			if (hasPackageSize(tokens, startOffset, dotOffset)) {
				return NO_MATCH;
			}
			return matchSingleSelect;
		}

		/** Returns whether the SINGLE keyword was found right at the start. */
		private boolean isSingle(List<IToken> tokens, int startOffset) {
			return tokens.get(startOffset).getType() == SINGLE;
		}

		/** Returns whether this has the INTO|APPEND ... TABLE clause. */
		private boolean hasIntoAppendingTable(List<IToken> tokens,
				int startOffset, int endOffset) {
			return TokenStreamUtils.containsAny(tokens, startOffset, endOffset,
					INTO, APPENDING)
					&& TokenStreamUtils.containsAny(tokens, startOffset,
							endOffset, TABLE);
		}

		/** Returns whether this has the PACKAGE SIZE clause. */
		private boolean hasPackageSize(List<IToken> tokens, int startOffset,
				int endOffset) {
			return TokenStreamUtils.containsSequence(tokens, startOffset,
					endOffset, PACKAGE, SIZE);
		}

		/** Returns whether this has the GROUP BY clause. */
		private boolean hasGroupBy(List<IToken> tokens, int startOffset,
				int endOffset) {
			return TokenStreamUtils.containsSequence(tokens, startOffset,
					endOffset, GROUP, BY);
		}

		/** Returns whether this only contains aggregate functions. */
		private boolean hasOnlyAggregateFunctions(List<IToken> tokens,
				int startOffset, int endOffset) {

			while (startOffset < endOffset
					&& SELECT_TO_RESULTS_SKIP_TOKENS.contains(tokens.get(
							startOffset).getType())) {
				startOffset += 1;
			}

			while (startOffset < endOffset
					&& !AGGREGATE_TERMINATOR.contains(tokens.get(startOffset)
							.getType())) {
				if (!AGGREGATE_FUNCTIONS.contains(tokens.get(startOffset)
						.getType())) {
					// found non-aggregate
					return false;
				}
				startOffset = skipAggregate(tokens, startOffset + 1, endOffset);
			}
			return true;
		}

		/**
		 * Skips the remainder of an aggregate function, i.e. a block in
		 * parentheses and the optional AS part. Returns the new startOffset.
		 */
		private int skipAggregate(List<IToken> tokens, int startOffset,
				int endOffset) {
			if (startOffset >= endOffset
					|| tokens.get(startOffset).getType() != LPAREN) {
				return startOffset;
			}
			int rparenPos = TokenStreamUtils.find(tokens, RPAREN, startOffset,
					endOffset);
			if (rparenPos == TokenStreamUtils.NOT_FOUND) {
				return startOffset;
			}

			startOffset = rparenPos + 1;

			// optionally skip AS part
			if (startOffset < endOffset
					&& tokens.get(startOffset).getType() == AS) {
				startOffset += 2;
			}

			return startOffset;
		}
	}
}
