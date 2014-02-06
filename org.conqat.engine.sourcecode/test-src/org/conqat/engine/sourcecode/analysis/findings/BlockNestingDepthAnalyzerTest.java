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
package org.conqat.engine.sourcecode.analysis.findings;

import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;

/**
 * Tests the {@link BlockNestingDepthAnalyzer}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 9E012D678949F69E2DC8E93A526499D8
 */
public class BlockNestingDepthAnalyzerTest extends TokenTestCaseBase {

	/** Test different cases. */
	public void test() throws ConQATException {
		assertNesting("SimpleNesting.java", 12);
		assertNesting("ComplexNesting01.java", 12, 24);
		assertNesting("ComplexNesting02.java", 12, 25);
	}

	/**
	 * Check single and multi finding mode. In multiple mode, this asserts that
	 * the findings are created in the correct lines.
	 */
	private void assertNesting(String filename, int... lineNumbers)
			throws ConQATException {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);

		runNestingDepthAnalyzer(element, 5, false);

		// expect exactly one finding
		FindingsList findingsList = NodeUtils.getFindingsList(element,
				BlockAnalyzerBase.BLOCK_FINDINGS);
		assertNotNull(findingsList);
		assertEquals(1, findingsList.size());

		element = createTokenElement(useCanonicalTestFile(filename),
				ELanguage.JAVA);

		runNestingDepthAnalyzer(element, 5, true);

		findingsList = NodeUtils.getFindingsList(element,
				BlockAnalyzerBase.BLOCK_FINDINGS);
		assertNotNull(findingsList);
		assertEquals(lineNumbers.length, findingsList.size());

		for (int i = 0; i < lineNumbers.length; i++) {
			assertEquals(lineNumbers[i], ((TextRegionLocation) findingsList
					.get(i).getLocation()).getRawStartLine());
		}
	}

	/** Executes the analyzer. */
	private void runNestingDepthAnalyzer(ITokenElement element, int threshold,
			boolean multi) throws ConQATException {
		executeProcessor(BlockNestingDepthAnalyzer.class, "(input=(ref=",
				element, "), 'open-block'=(token=", ETokenType.LBRACE,
				"), 'close-block'=(token=", ETokenType.RBRACE,
				"), block=(depth=1), category=(name=nesting), threshold=(yellow="
						+ threshold + ",red=" + threshold + 1
						+ "), 'multiple-findings'=(value='" + multi + "'))");
	}
}