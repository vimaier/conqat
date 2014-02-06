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
package org.conqat.engine.java.findings;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.java.resource.IJavaResource;
import org.conqat.engine.resource.findings.FindingsAnnotatorBase;
import org.conqat.lib.commons.collections.ListMap;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35930 $
 * @ConQAT.Rating GREEN Hash: 22B346924FF72627A50C21B96937A3A7
 */
@AConQATProcessor(description = "Annotates a java node with findings from a report.")
public class FindingsJavaAnnotator extends
		FindingsAnnotatorBase<IJavaResource, IJavaElement> {

	/** Findings organized by java class name. */
	private final ListMap<String, Finding> findingsByClass = new ListMap<String, Finding>();

	/** {@inheritDoc} */
	@Override
	protected void setUp(IJavaResource root) throws ConQATException {
		super.setUp(root);
		for (FindingReport sourceReport : sourceReports) {
			findingsByClass.addAll(FindingUtils
					.getFindingsByQualifiedName(sourceReport));
		}
	}

	/** {@inheritDoc} */
	@Override
	protected List<Finding> determineFindings(IJavaElement element) {
		List<Finding> elementFindings = super.determineFindings(element);
		List<Finding> javaFindings = findingsByClass.getCollection(element
				.getId());

		if (elementFindings == null) {
			return javaFindings;
		}
		if (javaFindings == null) {
			return elementFindings;
		}

		List<Finding> result = new ArrayList<Finding>();
		result.addAll(elementFindings);
		result.addAll(javaFindings);
		return result;
	}
}