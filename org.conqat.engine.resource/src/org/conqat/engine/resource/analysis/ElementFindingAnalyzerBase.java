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
package org.conqat.engine.resource.analysis;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceUtils;

/**
 * Base class for analyzers that create findings on elements.
 * 
 * @param <R>
 *            type of the resource
 * 
 * @param <E>
 *            type of the element (in the resource tree)
 * 
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 13CB8169E5BDCC41CFAA0701EBF6A2BC
 */
public abstract class ElementFindingAnalyzerBase<R extends ITextResource, E extends ITextElement>
		extends ElementAnalyzerBase<R, E> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The default key used for storing the findings.", type = "java.lang.List<Finding>")
	public static final String DEFAULT_KEY = "findings";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.WRITEKEY_NAME, attribute = ConQATParamDoc.WRITEKEY_KEY_NAME, optional = true, description = "The key to store the findings in. Default is "
			+ DEFAULT_KEY)
	public String findingsKey = DEFAULT_KEY;

	/** The finding report. */
	private FindingReport findingReport;

	/** The findings group used. */
	private FindingGroup findingGroup;

	/** {@inheritDoc} */
	@Override
	protected void setUp(R root) throws ConQATException {
		super.setUp(root);
		findingReport = NodeUtils.getFindingReport(root);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { findingsKey };
	}

	/** Creates a finding without line context. */
	protected void createFinding(String message, E element) {
		FindingUtils.createAndAttachFinding(
				getFindingsGroup(),
				message,
				element,
				new ElementLocation(element.getLocation(), element
						.getUniformPath()), findingsKey);
	}

	/** Creates a finding for a single (filtered) line. */
	protected void createFindingForFilteredLine(String message, E element,
			int filteredLineNumber) throws ConQATException {
		ResourceUtils.createAndAttachFindingForFilteredLine(getFindingsGroup(),
				message, element, filteredLineNumber, findingsKey);
	}

	/** Creates a finding for a (filtered) line region. */
	protected void createFindingForFilteredLines(String message, E element,
			int filteredStartLineNumber, int filteredEndLineNumber)
			throws ConQATException {
		ResourceUtils.createAndAttachFindingForFilteredLineRegion(
				getFindingsGroup(), message, element, filteredStartLineNumber,
				filteredEndLineNumber, findingsKey);
	}

	/**
	 * Creates a finding for a (filtered) region described by (filtered)
	 * offsets.
	 */
	protected void createFindingForFilteredOffsets(String message, E element,
			int filteredStartOffset, int filteredEndOffset)
			throws ConQATException {
		ResourceUtils.createAndAttachFindingForFilteredRegion(
				getFindingsGroup(), message, element, filteredStartOffset,
				filteredEndOffset, findingsKey);
	}

	/** Returns the findings group used. */
	private FindingGroup getFindingsGroup() {
		if (findingGroup == null) {
			findingGroup = findingReport.getOrCreateCategory(
					getFindingCategoryName()).getOrCreateFindingGroup(
					getFindingGroupName());
		}

		return findingGroup;
	}

	/** Template method returning the name of the finding group to be used. */
	protected abstract String getFindingGroupName();

	/** Template method returning the name of the finding category to be used. */
	protected abstract String getFindingCategoryName();
}
