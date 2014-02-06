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

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37243 $
 * @ConQAT.Rating GREEN Hash: 066473B6CFAFCDFF0C3C1624ED470E25
 */
@AConQATProcessor(description = "Annotates each node with the number of elements contained as children (including the node itself).")
public class NumberOfElementsCounter extends
		ConQATPipelineProcessorBase<IResource> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Contains the number of elements.", type = "java.lang.Integer")
	public static final String KEY = "#elements";

	/** {@inheritDoc} */
	@Override
	protected void processInput(IResource input) {
		NodeUtils.addToDisplayList(input, KEY);
		countElements(input);
	}

	/** Recursively counts the elements and adds the values to all nodes. */
	private int countElements(IResource resource) {
		int count = 0;
		if (resource instanceof IElement) {
			count += 1;
		}

		if (resource.hasChildren()) {
			for (IResource child : resource.getChildren()) {
				count += countElements(child);
			}
		}

		resource.setValue(KEY, count);
		return count;
	}
}
