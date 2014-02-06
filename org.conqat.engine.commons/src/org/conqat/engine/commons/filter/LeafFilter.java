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

import java.util.List;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Filters out leaf nodes.
 * 
 * @author Elmar Juergens
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: CFA3A7271D9E2824DEB07ABCDB750128
 */
@AConQATProcessor(description = "This filter filters out leaf nodes.")
public class LeafFilter extends
		ConQATPipelineProcessorBase<IRemovableConQATNode> {

	/** {@inheritDoc} */
	@Override
	protected void processInput(IRemovableConQATNode input) {
		List<IRemovableConQATNode> leafs = TraversalUtils
				.listLeavesDepthFirst(input);
		
		for (IRemovableConQATNode leaf : leafs) {
			leaf.remove();
		}
	}
}