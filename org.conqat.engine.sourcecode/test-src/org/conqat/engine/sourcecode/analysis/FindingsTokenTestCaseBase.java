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
package org.conqat.engine.sourcecode.analysis;

import static org.conqat.engine.resource.analysis.ElementFindingAnalyzerBase.DEFAULT_KEY;

import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.scanner.ELanguage;

/**
 * Base class for test cases expecting findings.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 7F37E8706BBC73EE9FF8280CFE7E7E0E
 */
public abstract class FindingsTokenTestCaseBase extends TokenTestCaseBase {

	/** The processor to test. */
	protected final Class<? extends IConQATProcessor> processor;

	/** The programming language of the test files. */
	protected final ELanguage tokenLanguage;

	/** Constructor. */
	public FindingsTokenTestCaseBase(
			Class<? extends IConQATProcessor> processor, ELanguage tokenLanguage) {
		this.processor = processor;
		this.tokenLanguage = tokenLanguage;
	}

	/**
	 * Run the processor on the given file and assert that no findings are
	 * attached after the run.
	 */
	protected void checkFileAssertNoFindings(String filename) throws Exception {
		assertNoFindings(executeProcessor(filename));
	}

	/** Fails if the {@link ITokenElement} has attached findings. */
	protected void assertNoFindings(ITokenElement element) {
		assertNull("No findings expected, but at least one was found: "
				+ element.getValue(DEFAULT_KEY), element.getValue(DEFAULT_KEY));
	}

	/**
	 * Runs the processor for a file and expects findings in the given line
	 * numbers.
	 */
	protected void checkFileAssertFindingsAt(String filename, int... findings)
			throws ConQATException {
		ITokenElement element = executeProcessor(filename);
		for (int finding : findings) {
			assertFinding(element, finding);
		}

		// Check, that no additional findings are attached to element
		assertFindingCount(element, findings.length);
	}

	/** Checks if the analyzer returns the expected findings for a file. */
	protected void checkFileForFindings(String filename, boolean expectFindings)
			throws ConQATException {
		ITokenElement element = executeProcessor(filename);

		if (expectFindings) {
			assertNotNull("Findings expected, but none was found",
					element.getValue(DEFAULT_KEY));
		} else {
			assertNull("No findings expected, but at least one was found: "
					+ element.getValue(DEFAULT_KEY),
					element.getValue(DEFAULT_KEY));
		}
	}

	/** Run processor on given file. */
	protected ITokenElement executeProcessor(String filename)
			throws ConQATException {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), tokenLanguage);
		executeProcessor(processor, "(input=(ref=", element, "))");
		return element;
	}

	/**
	 * Checks if the analyzer returned the expected number of findings for an
	 * element.
	 */
	protected void assertFindingCount(ITokenElement element, int count) {
		assertFindingCount(element, count, DEFAULT_KEY);
	}

	/**
	 * Checks if the analyzer returned the expected number of findings for an
	 * element.
	 */
	protected void assertFindingCount(ITokenElement element, int count,
			String key) {
		assertEquals(count, ((FindingsList) element.getValue(key)).size());
	}

	/** Checks if the analyzer returned the expected findings for an element. */
	protected void assertFinding(ITokenElement element, int firstLine,
			int lastLine) {
		assertFinding(element, firstLine, lastLine, DEFAULT_KEY);
	}

	/** Checks if the analyzer returned the expected findings for an element. */
	protected void assertFinding(ITokenElement element, int firstLine,
			int lastLine, String key) {

		for (Finding finding : (FindingsList) element.getValue(key)) {
			TextRegionLocation regionLocation = (TextRegionLocation) finding
					.getLocation();
			if (regionLocation.getRawStartLine() == firstLine
					&& regionLocation.getRawEndLine() == lastLine) {
				return;
			}
		}

		fail("Element did not contain finding at lines " + firstLine + " - "
				+ lastLine);
	}

	/** Checks if the analyzer returned the expected findings for an element. */
	protected void assertFinding(ITokenElement element, int firstLine) {
		assertFinding(element, firstLine, DEFAULT_KEY);
	}

	/** Checks if the analyzer returned the expected findings for an element. */
	protected void assertFinding(ITokenElement element, int firstLine,
			String key) {
		Object value = element.getValue(key);
		assertTrue("Expected findings list to be created!",
				value instanceof FindingsList);

		for (Finding finding : (FindingsList) value) {
			TextRegionLocation lineLocation = (TextRegionLocation) finding
					.getLocation();
			if (lineLocation.getRawStartLine() == firstLine) {
				return;
			}
		}

		fail("Element did not contain finding at line " + firstLine);
	}

}