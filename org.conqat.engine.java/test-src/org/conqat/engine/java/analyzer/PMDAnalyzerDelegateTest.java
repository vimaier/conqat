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
package org.conqat.engine.java.analyzer;

import net.sourceforge.pmd.lang.LanguageVersion;

import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.LoggerMock;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link PMDAnalyzerDelegate}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47044 $
 * @ConQAT.Rating GREEN Hash: 871A4EDF2F35626DE83AC7BC7A73A536
 */
public class PMDAnalyzerDelegateTest extends TokenTestCaseBase {

	/** Tests running the analysis of unused private elements. */
	public void testUnusedCodeAnalysis() throws ConQATException {
		ITokenElement element = performPmdAnalysis("unused_fields.java");
		Object value = element.getValue("key");
		assertTrue(value instanceof FindingsList);

		FindingsList findings = (FindingsList) value;
		assertEquals(4, findings.size());
		assertEquals("TEST/unused_fields.java:21-21", findings.get(0)
				.getLocationString());
		assertEquals("TEST/unused_fields.java:25-25", findings.get(1)
				.getLocationString());
		assertEquals("TEST/unused_fields.java:29-29", findings.get(2)
				.getLocationString());
		assertEquals("TEST/unused_fields.java:37-37", findings.get(3)
				.getLocationString());
	}

	/** Tests analysis in the context of compile errors in the code. */
	public void testCompileErrors() throws ConQATException {
		performPmdAnalysis("does_not_compile.java");
	}

	/**
	 * Performs the PMD analysis and returns the element with annotated
	 * findings.
	 */
	private ITokenElement performPmdAnalysis(String filename)
			throws ConQATException {
		PMDAnalyzerDelegate pmdDelegate = new PMDAnalyzerDelegate();
		pmdDelegate.addRulesFromExtConfig(useTestFile("pmd-unused-code.xml")
				.getAbsolutePath());

		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);
		pmdDelegate.init(element, "category", "group", "key");
		pmdDelegate.analyze(element, LanguageVersion.JAVA_17, new LoggerMock());
		return element;
	}
}
