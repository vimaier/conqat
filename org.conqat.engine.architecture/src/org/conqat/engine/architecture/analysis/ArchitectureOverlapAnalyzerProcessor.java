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
package org.conqat.engine.architecture.analysis;

import org.conqat.engine.architecture.assessment.shared.IComponent;
import org.conqat.engine.architecture.overlap.OverlapComponent;
import org.conqat.engine.architecture.scope.ArchitectureDefinition;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43813 $
 * @ConQAT.Rating GREEN Hash: AE06597F7BB56ACECDE2A9065BA88430
 */
@AConQATProcessor(description = "This processor analyzes the given architecture for overlaps and reports them in a list, if there are any.")
public class ArchitectureOverlapAnalyzerProcessor extends
		ConQATPipelineProcessorBase<ArchitectureDefinition> {

	/** Result key. */
	@AConQATKey(description = "The list of overlaps in the architecture", type = "java.util.List<String>")
	public static final String OVERLAP_LIST_KEY = "overlap list";

	/** {@inheritDoc} */
	@Override
	protected void processInput(ArchitectureDefinition input) {
		NodeUtils.addToDisplayList(input, OVERLAP_LIST_KEY);

		OverlapComponent overlapRoot =
		        OverlapComponent.convertFromArchitectureComponents(
		                TraversalUtils.listAllDepthFirst(input));
		ListMap<IComponent, String> errorMap =
		        overlapRoot.checkForOverlaps(null);
		for (IComponent component : errorMap.getKeys()) {
			((ConQATNodeBase) component).setValue(OVERLAP_LIST_KEY,
			        CollectionUtils.sort(errorMap.getCollection(component)));
		}
	}
}
