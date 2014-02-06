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

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.base.ElementTraversingProcessorBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43824 $
 * @ConQAT.Rating GREEN Hash: 2676782576CA2A420234B4ECFC06D714
 */
@AConQATProcessor(description = "This processor marks files as modified or unmodified where the modification "
		+ "information is provided by the ScopeDiffer processor (thus the ScopeDiffer must be run before). "
		+ "All files that are new or have any difference are marked as modified")
public class ModifiedMarker extends
		ElementTraversingProcessorBase<ITextResource, ITextElement> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Modified", type = "java.lang.Boolean")
	public static final String KEY = "Modified";

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITextResource root) {
		NodeUtils.addToDisplayList(root, ModifiedMarker.KEY);
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITextElement element) throws ConQATException {
		element.setValue(ModifiedMarker.KEY, isModified(element));
	}

	/** Checks if an element is modified. */
	private boolean isModified(ITextElement element) throws ConQATException {
		double churnLines = NodeUtils.getDoubleValue(element,
				ScopeDiffer.KEY_CHURN_LINES);
		boolean newFile = NodeUtils.getBooleanValue(element,
				ScopeDiffer.KEY_NEW);

		return churnLines > 0 || newFile;
	}
}
