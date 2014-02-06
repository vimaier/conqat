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
package org.conqat.engine.commons.pattern;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Tests the {@link PatternListDef}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40864 $
 * @ConQAT.Rating GREEN Hash: 10BECB1DDBCF005F925F9E09558441D0
 */
public class PatternListDefTest extends CCSMTestCaseBase {

	/** Tests a complex setting with macros for parsing VB. */
	public void testVBPattern() throws ConQATException, IOException {
		PatternListDef def = new PatternListDef();
		def.init(new ProcessorInfoMock());

		def.defineMacro("DOT", "[.]");
		def.defineMacro("SIMPLE_OR_FQ_NAME",
				"(%{IDENTIFIER}%{DOT})*%{IDENTIFIER}");
		def.defineMacro("SPACE_OR_TAB", "[ %{TAB}]");
		def.defineMacro("SPACE_OR_TAB_0U", "%{SPACE_OR_TAB}*");
		def.defineMacro("LF_OR_CR", "[%{LINE_FEED}%{CARRIAGE_RETURN}]");
		def.defineMacro("NOT_LF_AND_NOT_CR",
				"[^%{LINE_FEED}%{CARRIAGE_RETURN}]");
		def.defineMacro("BEGIN_GENERATED_REGION",
				"^#Region%{SPACE_OR_TAB_0U}%{NOT_LF_AND_NOT_CR}*(generiert|generated)");
		def.defineMacro("END_REGION", "^#End Region");

		def.addPattern("(?m)^Imports%{SPACE_OR_TAB_0U}%{SIMPLE_OR_FQ_NAME}$");
		def.addPattern("(?m)%{BEGIN_GENERATED_REGION}(.|%{LF_OR_CR})*?%{END_REGION}");

		PatternList patternList = def.process();

		String fileContent = FileSystemUtils.readFile(useTestFile("test.vb"));
		List<String> matches = new ArrayList<String>();
		for (Pattern p : patternList) {
			Matcher matcher = p.matcher(fileContent);
			while (matcher.find()) {
				matches.add(StringUtils.normalizeLineBreaks(matcher.group()));
			}
		}

		List<String> expected = Arrays
				.asList("Imports AAA.BBB.CCC",
						StringUtils
								.normalizeLineBreaks("#Region \" Vom Windows Form Designer generierter Code \"\n\nRegion inhalt\n\n#End Region"));
		assertEquals(expected, matches);
	}

	/** Tests a complex setting with macros for parsing C#/C++ fragments. */
	public void testCSharpCPPPattern() throws ConQATException {
		PatternListDef def = new PatternListDef();
		def.init(new ProcessorInfoMock());

		def.defineMacro("ACCESS_MODIFIER", "(public|protected|private)");
		def.defineMacro("DOT", "[.]");
		def.defineMacro("FQ_SEPARATOR", "(%{DOT}|::)");
		def.defineMacro("SIMPLE_OR_FQ_NAME",
				"(%{IDENTIFIER}%{FQ_SEPARATOR})*%{IDENTIFIER}");
		def.defineMacro("SIMPLE_OR_FQ_NAME_SETTER",
				"(%{IDENTIFIER}%{FQ_SEPARATOR})*[sS]et%{IDENTIFIER}");
		def.defineMacro("WS", "(%{NEWLINE}|%{TAB}| )");
		def.defineMacro("WS0U", "%{WS}*");
		def.defineMacro("WS1U", "%{WS}+");
		def.defineMacro("LPAREN", "[(]");
		def.defineMacro("RPAREN", "[)]");
		def.defineMacro("LCURLY", "[{]");
		def.defineMacro("RCURLY", "[}]");
		def.defineMacro("GREATER", ">");
		def.defineMacro("AMPERSAND", "&");
		def.defineMacro("THIS_WITH_DELIMITER",
				"(this%{WS0U}(%{DOT}|-%{GREATER})%{WS0U})");
		def.defineMacro("JAVA_RETURN_OR_PARAMETER_TYPE",
				"((final%{WS1U})?%{SIMPLE_OR_FQ_NAME})");
		def.defineMacro("CPP_RETURN_OR_PARAMETER_TYPE",
				"((const%{WS1U})?%{SIMPLE_OR_FQ_NAME}(%{WS0U}[*%{AMPERSAND}])?)");
		def.defineMacro("RETURN_OR_PARAMETER_TYPE",
				"(%{JAVA_RETURN_OR_PARAMETER_TYPE}|%{CPP_RETURN_OR_PARAMETER_TYPE})");

		def.addPattern("(%{ACCESS_MODIFIER}%{WS1U})?%{RETURN_OR_PARAMETER_TYPE}%{WS1U}%{SIMPLE_OR_FQ_NAME}%{WS0U}%{LPAREN}%{WS0U}%{RPAREN}%{WS0U}(const%{WS0U})?%{LCURLY}"
				+ "%{WS0U}return%{WS1U}%{THIS_WITH_DELIMITER}?%{IDENTIFIER}%{WS0U};%{WS0U}"
				+ "%{RCURLY}");
		def.addPattern("(%{ACCESS_MODIFIER}%{WS1U})?void%{WS1U}%{SIMPLE_OR_FQ_NAME}%{WS0U}%{LPAREN}%{WS0U}%{RETURN_OR_PARAMETER_TYPE}%{WS1U}%{IDENTIFIER}%{RPAREN}%{WS0U}%{LCURLY}"
				+ "%{WS0U}%{THIS_WITH_DELIMITER}?%{IDENTIFIER}%{WS0U}=%{WS0U}%{IDENTIFIER}%{WS0U};%{WS0U}"
				+ "%{RCURLY}");

		PatternList patternList = def.process();
		for (int patternNumber = 0; patternNumber <= 6; ++patternNumber) {
			testWithNewline(patternList, patternNumber, "\n");
			testWithNewline(patternList, patternNumber, "\r\n");
		}
	}

	/** Performs the test with the given (numbered) input and newline string. */
	private void testWithNewline(PatternList patternList, int patternNumber,
			String newline) {
		String getterSetterClass = generateGetterSetterTestInput(patternNumber,
				newline);
		assertTrue(patternList.findsAnyIn(getterSetterClass));
	}

	/** Generates test input. */
	private final static String generateGetterSetterTestInput(
			final int patternNumber, final String newline) {
		switch (patternNumber) {
		case 0:
			return "public final String  getField(){" + newline
					+ "  return this.field ; " + newline + "}";
		case 1:
			return "protected   java.lang.String  getField( ) {" + newline
					+ "  return field; }";
		case 2:
			return " /* comment */" + "final String  getField() {" + newline
					+ "  return   this." + newline + "field; " + newline + "}";
		case 3:
			return " /* comment */" + newline
					+ "const A::B::String&  ClassName::field() const {"
					+ newline + "  return   this->field;" + newline + "}";
		case 4:
			return " /* comment */" + "A::B::String*" + newline
					+ "ClassName::getField ()const{" + newline
					+ "  return this->field;" + newline + "}";
		case 5:
			return " // comment " + newline
					+ " public void setDate(final java.util.Date  date) {"
					+ newline + "      this.date = date;" + newline + "}";
		case 6:
			return " // comment " + newline
					+ " void ClassName::setDate(const A::B::Date& date) {"
					+ newline + "      this->date= date ;" + newline + "}";
		}

		fail("Unknown pattern number!");
		return null;
	}

	/**
	 * Create pattern list from patterns. Useful for tests that require
	 * PatternLists
	 */
	public static PatternList createPatternList(String... patterns) {
		PatternList patternList = new PatternList();
		for (String pattern : patterns) {
			patternList.add(Pattern.compile(pattern));
		}
		return patternList;
	}
}
