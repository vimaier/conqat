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
package org.conqat.engine.resource.analysis;

import java.util.regex.Pattern;

import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 078B1B57585523F67DEADDBA46824E50
 */
@AConQATProcessor(description = "Checks each element for presence of a specific header. "
		+ "This works on the unfiltered content of the element.")
public class HeaderAssessor extends TextElementAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Element Header Findings", type = "java.util.List<org.conqat.engine.commons.findings.Finding>")
	public static final String KEY = "HeaderFindings";

	/** List of normalization patterns */
	private PatternList normalizationPatterns;

	/** Expected header */
	private String expectedHeader;

	/** The group used. */
	private FindingGroup group;

	/** ConQAT Parameter */
	@AConQATParameter(name = "normalization", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Normalization patterns. All matched text is removed before header presence check")
	public void setPattern(
			@AConQATAttribute(name = "patterns", description = "Default is to perform no normalizations") PatternList normalizationPatterns) {
		this.normalizationPatterns = normalizationPatterns;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "expected", minOccurrences = 1, maxOccurrences = 1, description = "Expected header")
	public void setHeader(
			@AConQATAttribute(name = "header", description = "Expected header") String expectedHeader) {
		this.expectedHeader = expectedHeader;
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { KEY };
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITextResource root) throws ConQATException {
		super.setUp(root);

		expectedHeader = normalize(expectedHeader);
		FindingCategory category = NodeUtils.getFindingReport(root)
				.getOrCreateCategory("Header Assessment");
		group = category.createFindingGroup("Header Violations "
				+ (category.getChildren().length + 1));
	}

	/** Assess presence of header */
	@Override
	protected void analyzeElement(ITextElement element) {

		try {
			String content = element.getUnfilteredTextContent();
			String normalizedContent = normalize(content);
			boolean startsWithExpectedHeader = normalizedContent
					.startsWith(expectedHeader);

			if (!startsWithExpectedHeader) {
				// it is not meaningful to annotate a missing header to lines,
				// so we use an element location instead
				ElementLocation location = new ElementLocation(
						element.getLocation(), element.getUniformPath());
				FindingUtils.createAndAttachFinding(group, "Invalid header",
						element, location, KEY);
			}
		} catch (ConQATException e) {
			getLogger().warn(
					"Could not read element: " + element.getLocation() + ": "
							+ e.getMessage());
		}
	}

	/**
	 * Performs the specified normalizations on the string by removing all
	 * matched content.
	 */
	private String normalize(String string) {
		if (normalizationPatterns == null) {
			return string;
		}

		String fixpoint = "";
		while (!fixpoint.equals(string)) {
			fixpoint = string;
			for (Pattern p : normalizationPatterns) {
				string = p.matcher(string).replaceAll(StringUtils.EMPTY_STRING);
			}
		}
		return string;
	}
}