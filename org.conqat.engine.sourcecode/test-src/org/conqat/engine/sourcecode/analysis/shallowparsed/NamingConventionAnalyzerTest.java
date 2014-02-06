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
package org.conqat.engine.sourcecode.analysis.shallowparsed;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.ElementFindingAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.commons.collections.Pair;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link NamingConventionAnalyzer}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 47154 $
 * @ConQAT.Rating GREEN Hash: 5123B0B012A101D3C66C134F4FF4399C
 */
@SuppressWarnings("unchecked")
public class NamingConventionAnalyzerTest extends TokenTestCaseBase {

	/** Line separator used for test output. */
	private static final String LINE_SEPARATOR = ": ";

	/**
	 * Pattern used to extract the identifier name from the findings message.
	 * This picks the first full identifier after the first colon.
	 */
	private static final Pattern NAME_EXTRACTION_PATTERN = Pattern
			.compile(": *([a-zA-Z_0-9]+)");

	/** Test naming conventions for modules. ConQATException */
	public void testModules() throws ConQATException {
		runTest("module-names", finding(2, "foo"));
	}

	/** Test naming conventions for types. */
	public void testTypes() throws ConQATException {
		runTest("type-names", finding(7, "C1"));
	}

	/** Test naming conventions for methods. */
	public void testMethods() throws ConQATException {
		runTest("method-names", finding(14, "method1"), finding(21, "method2"),
				finding(26, "method2"));
	}

	/** Test naming conventions for parameters. */
	public void testParameters() throws ConQATException {
		runTest("method-parameters", finding(15, "p1"), finding(16, "p2"),
				finding(21, "p3"), finding(27, "p3"));
	}

	/** Test naming conventions for attributes. */
	public void testAttributes() throws ConQATException {
		runTest("attribute-names", finding(12, "attribute1"));
	}

	/** Test naming conventions for local variables. */
	public void testLocalVariables() throws ConQATException {
		runTest("local-variables", finding(17, "lvar1"), finding(18, "v1"),
				finding(32, "i"), finding(32, "j"), finding(34, "k"),
				finding(35, "l"));
	}

	/** Test naming conventions for global variables. */
	public void testGlobalVariables() throws ConQATException {
		runTest("global-variables", finding(41, "global1"),
				finding(42, "global2"), finding(43, "global3"));
	}

	/** Test naming conventions for constants. */
	public void testConstants() throws ConQATException {
		runTest("constant-names", finding(4, "CONST1"), finding(10, "CONST2"),
				finding(24, "CONST2"));
	}

	/**
	 * Runs a test using a very strict constraint for the given processor
	 * parameter and checks that all given findings are found.
	 */
	private void runTest(String checkedProcessorParameter,
			Pair<Integer, String>... expectedFindings) throws ConQATException {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile("naming.cpp"), ELanguage.CPP);
		executeProcessor(NamingConventionAnalyzer.class, "(input=(ref=",
				element, "), '" + checkedProcessorParameter
						+ "'=(pattern='x'))");

		StringBuilder expected = new StringBuilder();
		for (Pair<Integer, String> expectedFinding : expectedFindings) {
			expected.append(expectedFinding.getFirst() + LINE_SEPARATOR
					+ expectedFinding.getSecond() + StringUtils.CR);
		}

		FindingsList findings = NodeUtils.getOrCreateFindingsList(element,
				ElementFindingAnalyzerBase.DEFAULT_KEY);
		StringBuilder actual = new StringBuilder();
		for (Finding finding : findings) {
			int line = ((TextRegionLocation) finding.getLocation())
					.getRawStartLine();
			Matcher matcher = NAME_EXTRACTION_PATTERN.matcher(finding
					.getMessage());
			assertTrue(matcher.find());
			actual.append(line + LINE_SEPARATOR + matcher.group(1)
					+ StringUtils.CR);
		}

		assertEquals(expected.toString(), actual.toString());
	}

	/**
	 * Returns a pair that describes the line and variable name for a naming
	 * convention finding.
	 */
	private Pair<Integer, String> finding(int line, String name) {
		return new Pair<Integer, String>(line, name);
	}

}
