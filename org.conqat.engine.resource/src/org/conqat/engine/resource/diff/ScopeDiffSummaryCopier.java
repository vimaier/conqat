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
package org.conqat.engine.resource.diff;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 42558 $
 * @ConQAT.Rating YELLOW Hash: 1071B2BCB6F59D48216834F3270C49BF
 */
@AConQATProcessor(description = "This processor copies the summary information of the scope differ (elments added, removed, modified, unmodified) "
		+ "to the root of the input node.")
public class ScopeDiffSummaryCopier extends ConQATPipelineProcessorBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for number of added elements", type = "int")
	public static final String KEY_ADDED = "elements-added";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for number of added elements", type = "int")
	public static final String KEY_REMOVED = "elements-removed";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for number of added elements", type = "int")
	public static final String KEY_MODIFIED = "elements-modified";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for number of added elements", type = "int")
	public static final String KEY_UNMODIFIED = "elements-unmodified";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "scope-diff-info", attribute = "ref", description = "ScopeDiff-Info-Object from where the values are copied.", optional = false)
	public ScopeDiffInfo scopeDiffInfo;

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode input) {
		input.setValue(KEY_ADDED, scopeDiffInfo.getAddedElements().size());
		input.setValue(KEY_REMOVED, scopeDiffInfo.getRemovedElements().size());
		input.setValue(KEY_MODIFIED, scopeDiffInfo.getModifiedElements().size());
		input.setValue(KEY_UNMODIFIED, scopeDiffInfo.getUnmodifiedElements()
				.size());

	}

}
