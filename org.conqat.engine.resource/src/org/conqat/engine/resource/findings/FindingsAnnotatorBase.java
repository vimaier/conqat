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
package org.conqat.engine.resource.findings;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.analysis.ElementAnalyzerBase;
import org.conqat.lib.commons.collections.ListMap;

/**
 * Base class for processors which annotate a resource hierarchy with findings
 * from a report. This base class is used to allow for a generic parameter.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38366 $
 * @ConQAT.Rating GREEN Hash: B6F5E4101190ACEDB5948DF3B7BEF648
 */
public class FindingsAnnotatorBase<R extends IResource, E extends IElement>
		extends ElementAnalyzerBase<R, E> {

	/** Key for findings result */
	@AConQATKey(description = "Findings", type = "org.conqat.engine.commons.findings.FindingsList")
	public static final String KEY = "Findings";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.WRITEKEY_NAME, attribute = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_DESC
			+ "(default is " + KEY + ")", optional = true)
	public String writeKey = KEY;

	/** The reports to copy from. */
	protected final List<FindingReport> sourceReports = new ArrayList<FindingReport>();

	/** The report to insert into. */
	private FindingReport targetReport;

	/** Findings grouped by element uniform path. */
	protected final ListMap<String, Finding> findingsByElement = new ListMap<String, Finding>();

	/** Keeps track of number of annotated findings */
	private int annotatedFindingsCount = 0;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "finding-report", minOccurrences = 1, description = "Add a report that gets read")
	public void addFindingReport(
			@AConQATAttribute(name = "ref", description = "The report read.") FindingReport findingReport) {
		sourceReports.add(findingReport);
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(R root) throws ConQATException {
		super.setUp(root);
		for (FindingReport sourceReport : sourceReports) {
			findingsByElement.addAll(FindingUtils
					.getFindingsByElement(sourceReport));
		}
		targetReport = NodeUtils.getFindingReport(root);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeElement(E element) throws ConQATException {
		List<Finding> findings = determineFindings(element);
		if (findings != null && !findings.isEmpty()) {
			NodeUtils.getOrCreateFindingsList(element, writeKey).addAll(
					FindingUtils.adoptFindings(targetReport, findings));
			annotatedFindingsCount += findings.size();
		}

	}

	/** {@inheritDoc} */
	@Override
	protected void finish(R root) {
		getLogger().debug("Annotated " + annotatedFindingsCount + " findings");
	}

	/** Determines the relevant findings for an element. */
	protected List<Finding> determineFindings(E element) {
		return findingsByElement.getCollection(element.getUniformPath());
	}

	/** Returns {@value #KEY} */
	@Override
	protected String[] getKeys() {
		return new String[] { writeKey };
	}

}