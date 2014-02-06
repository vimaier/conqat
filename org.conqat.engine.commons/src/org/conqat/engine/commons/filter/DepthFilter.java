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
package org.conqat.engine.commons.filter;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Filter elements based on depth in node tree.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @levd.rating GREEN Hash: 10D4912C0FF3EF486CB15CE90AA54AC0
 */
@AConQATProcessor(description = "This filter filters nodes based on their depth. "
		+ "The root has depth 0, its children have depth 1 and so on. ")
public class DepthFilter extends
		ConQATPipelineProcessorBase<IRemovableConQATNode> {

	/** Maximum depth to include. */
	private int maxDepth;

	/** Set max depth. */
	@AConQATParameter(name = "max", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The maximal depth an element may have to survive filtering.")
	public void setMaxDepth(
			@AConQATAttribute(name = "depth", description = "The depth.")
			int maxDepth) {

		this.maxDepth = maxDepth;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(IRemovableConQATNode input) {
		filterNodes(input, 0);
	}

	/**
	 * This method traverses the children of the <code>IFileSystemElement</code>
	 * and filters elements deeper than the maximum depth. This method is called
	 * recursively.
	 * 
	 * @param element
	 *            The element to filter.
	 */
	private void filterNodes(IRemovableConQATNode element, int depth) {
		if (element.hasChildren()) {
			for (IRemovableConQATNode child : element.getChildren()) {
				if (depth >= maxDepth) {
					child.remove();
				} else {
					filterNodes(child, depth + 1);
				}
			}
		}
	}
}