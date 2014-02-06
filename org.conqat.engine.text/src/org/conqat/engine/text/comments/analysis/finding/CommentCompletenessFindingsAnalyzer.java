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

import org.conqat.engine.commons.findings.FindingCategoryNames;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.text.comments.analysis.CommentCompletenessAnalyzerBase;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: steidl $
 * @version $Rev: 46589 $
 * @ConQAT.Rating GREEN Hash: 3A97D29AA7E1C6E8675296F682423B9B
 */
@AConQATProcessor(description = "Checks for selected shallow entities whether they are commented and creates findings if not.")
public class CommentCompletenessFindingsAnalyzer extends
		CommentCompletenessAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The key used for storing the findings.", type = "java.lang.List<Finding>")
	public static final String FINDINGS_KEY = "findings";

	/** The finding report. */
	private FindingReport findingReport;

	/** The findings group used. */
	private FindingGroup findingGroup;

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) throws ConQATException {
		super.setUp(root);
		findingReport = NodeUtils.getFindingReport(root);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeSelectedEntity(ShallowEntity entity,
			ITokenElement element, boolean isCommented) throws ConQATException {
		if (!isCommented) {
			int endOffset = CollectionUtils.getLast(entity.ownStartTokens())
					.getEndOffset();
			ResourceUtils.createAndAttachFindingForFilteredRegion(
					getFindingsGroup(), "Interface comment missing", element,
					entity.getStartOffset(), endOffset, FINDINGS_KEY);
		}
	}

	/** Returns the findings group used. */
	private FindingGroup getFindingsGroup() {
		if (findingGroup == null) {
			findingGroup = findingReport.getOrCreateCategory(
					FindingCategoryNames.COMMENTS_CATEGORY)
					.getOrCreateFindingGroup("Missing Interface Comment");
		}

		return findingGroup;
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { FINDINGS_KEY };
	}
}
