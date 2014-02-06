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
package org.conqat.engine.java.javadoc;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.lib.commons.string.StringUtils;

import com.sun.javadoc.ProgramElementDoc;

/**
 * Base class for JavaDoc comment analyzers. Method
 * {@link #init(FindingCategory)} must be called before the analyzer can be
 * used.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: B4F17927CAC393EFD8123900C4224DE1
 */
public abstract class CommentAnalyzerBase extends ConQATProcessorBase {

	/** Finding group this analyzer creates findings for. */
	private FindingGroup findingGroup;

	/** Initialize this analyzer. */
	public void init(FindingCategory category) {
		findingGroup = FindingUtils.getOrCreateFindingGroupAndSetRuleId(
				category, getFindingGroupName(), getFindingGroupName());
	}

	/** This creates a finding in the analyzer's finding group. */
	protected Finding createFinding(String message, ProgramElementDoc doc,
			IJavaElement element) throws ConQATException {
		int startLine = doc.position().line();
		// use max to protect against empty string
		int lineCount = Math.max(1,
				StringUtils.countLines(doc.getRawCommentText()));
		return ResourceUtils.createAndAttachFindingForFilteredLineRegion(
				findingGroup, message, element, startLine, startLine
						+ lineCount - 1, JavaDocAnalyzer.KEY);
	}

	/**
	 * Template method to obtain name of the finding group associated with this
	 * analyzer.
	 */
	protected abstract String getFindingGroupName();
}