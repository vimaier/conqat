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
package org.conqat.engine.text.comments.analysis.finding;

import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.ElementFindingAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link CommentCompletenessFindingsAnalyzer}.
 * 
 * @author $Author: steidl $
 * @version $Rev: 47099 $
 * @ConQAT.Rating YELLOW Hash: D2291561EEF03C038C4290456CA9CF33
 */
public class CommentCompletenessFindingsAnalyzerTest extends TokenTestCaseBase {

	/** Tests requirement of comments (any will work). */
	public void testAnyComment() throws ConQATException {
		FindingsList findings = runAnalysis(false, "CommentCompleteness.java");
		assertEquals(1, findings.size());

		assertEquals("TEST/CommentCompleteness.java:24-24", findings.get(0)
				.getLocationString());
		assertEquals("Interface comment missing", findings.get(0).getMessage());
	}

	/** Tests requirement of doc comments. */
	public void testDocComments() throws ConQATException {
		FindingsList findings = runAnalysis(true, "CommentCompleteness.java");
		assertEquals(4, findings.size());

		assertEquals("TEST/CommentCompleteness.java:7-7", findings.get(0)
				.getLocationString());
		assertEquals("TEST/CommentCompleteness.java:12-13", findings.get(1)
				.getLocationString());
		assertEquals("TEST/CommentCompleteness.java:24-24", findings.get(2)
				.getLocationString());
		assertEquals("TEST/CommentCompleteness.java:28-28", findings.get(3)
				.getLocationString());
	}

	/** Runs the analysis on a test Java file. */
	private FindingsList runAnalysis(boolean requireDocComment, String filename)
			throws ConQATException {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);
		executeProcessor(CommentCompletenessFindingsAnalyzer.class,
				"(input=(ref=", element, "), 'doc-comment'=(require=",
				requireDocComment,
				"), selector=(expression='(type | method | attribute) & !simple-getter'))");

		FindingsList findings = NodeUtils.getFindingsList(element,
				ElementFindingAnalyzerBase.DEFAULT_KEY);
		return findings;
	}

	/** Test file without findings. */
	public void testConstructorComment() throws ConQATException {
		FindingsList findings = runAnalysis(false, "CommentedConstructor.java");
		assertNull(findings);
	}

	/** Test file without findings. */
	public void testSingleMethod() throws ConQATException {
		FindingsList findings = runAnalysis(false, "CommentedSingleMethod.java");
		assertNull(findings);
	}

	/** Test file with two findings. */
	public void testConstructorUncomment() throws ConQATException {
		FindingsList findings = runAnalysis(false,
				"UncommentedConstructor.java");
		assertEquals(2, findings.size());
	}
}
