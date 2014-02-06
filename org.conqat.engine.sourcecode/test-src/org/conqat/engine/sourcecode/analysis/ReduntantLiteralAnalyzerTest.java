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
package org.conqat.engine.sourcecode.analysis;

import java.util.regex.Pattern;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * Test for {@link RedundantLiteralAnalyzer}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 27B808FF75801DBF24ABC940447ADA2A
 */
public class ReduntantLiteralAnalyzerTest extends FindingsTokenTestCaseBase {

	/** Constructor. */
	public ReduntantLiteralAnalyzerTest() {
		super(RedundantLiteralAnalyzer.class, ELanguage.JAVA);
	}

	/** Test analyzer. */
	public void test() throws ConQATException {
		doTestElement(
				createTokenElement(
						useCanonicalTestFile("ReduntantLiterals.java"),
						ELanguage.JAVA), 3, 9, 10, 11);

		doTestElement(
				createTokenElement(
						useCanonicalTestFile("ReduntantLiterals.cpp"),
						ELanguage.CPP), 3, 9, 10, 11);
	}

	/**
	 * Run the test on a given element and expect the given single-line
	 * findings.
	 */
	private void doTestElement(ITokenElement element, int findingCount,
			Integer... findings) throws ConQATException {

		PatternList patterns = new PatternList();
		patterns.add(Pattern.compile("\\d"));

		executeProcessor(processor, "(input=(ref=", element,
				"),threshold=(value=2),exclude=('pattern-list'=", patterns,
				"))");

		for (int finding : findings) {
			assertFinding(element, finding,
					RedundantLiteralAnalyzer.DEFAULT_KEY);
		}

		assertFindingCount(element, findingCount,
				RedundantLiteralAnalyzer.DEFAULT_KEY);
	}
}