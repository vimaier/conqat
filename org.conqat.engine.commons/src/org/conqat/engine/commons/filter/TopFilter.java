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
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @levd.rating GREEN Hash: B4EC5961075773E1E337D27B479A461C
 */
@AConQATProcessor(description = "This filter works like the SQL 'TOP' directive. "
		+ " For every tree node it includes the specified number of children and "
		+ "removes all others. Hence, filtering is defined by the assigned sorter. "
		+ "If not sorter is assigned, the filtering mechanism is undefined.")
public class TopFilter extends
		ConQATPipelineProcessorBase<IRemovableConQATNode> {

	/** Number of children to include. */
	private int numOfChildren;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "top", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The maximal number of children to include.")
	public void setMaxDepth(
			@AConQATAttribute(name = "value", description = "The number of children.") int numOfChildren) {

		this.numOfChildren = numOfChildren;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(IRemovableConQATNode input) {
		filterNodes(input);
	}

	/**
	 * This method traverses the a node tree and filters children.
	 */
	private void filterNodes(IRemovableConQATNode element) {
		if (element.hasChildren()) {

			int childCount = 0;
			for (IRemovableConQATNode child : NodeUtils
					.getRemovableSortedChildren(element)) {
				if (childCount < numOfChildren) {
					filterNodes(child);
				} else {
					child.remove();
				}
				childCount++;
			}
		}
	}

}