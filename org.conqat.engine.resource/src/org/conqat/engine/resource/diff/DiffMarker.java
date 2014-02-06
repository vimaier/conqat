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
import org.conqat.engine.core.core.AConQATFieldParameter;
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
 * @ConQAT.Rating GREEN Hash: 6F3283DD77597DEDF22643F1B580901F
 */
@AConQATProcessor(description = "This processor marks files as modified or unmodified where the modification "
		+ "information is provided by the ScopeDiffer processor (thus the ScopeDiffer must be run before). "
		+ "Parameters allow to ajdust how strongly an "
		+ "element needs to be changed to be considered modified by this processor. The absolute and "
		+ "relative tresholds are OR'red.")
public class DiffMarker extends
		ElementTraversingProcessorBase<ITextResource, ITextElement> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "relative-threshold", attribute = "value", description = "Threshold for relative diff. "
			+ "An element is considered 'modified' if its relative difference greater than this treshold."
			+ " [default is 0]", optional = true)
	public double relThreshold = 0;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "absolute-threshold", attribute = "value", description = "Threshold for absolute diff. "
			+ "An element is considered 'modified' if the number of (non-whitespace) lines added is greater than this treshold."
			+ " [default is 0]", optional = true)
	public int absThreshold = 0;

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
		double relativeDiff = NodeUtils.getDoubleValue(element,
				ScopeDiffer.KEY_RELATIVE_CHURN);
		int lineChurn = (int) NodeUtils.getDoubleValue(element,
				ScopeDiffer.KEY_CHURN_LINES);

		boolean result = relativeDiff > relThreshold
				|| lineChurn > absThreshold;
		return result;
	}

}
