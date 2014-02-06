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
package org.conqat.engine.commons.filter;

import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: E59CC0F2B734440BAEDD249498F0B013
 */
@AConQATProcessor(description = "This processor filters/removes all nodes for which the value stored at the given node is true.")
public class BooleanValueFilter extends
		KeyBasedFilterBase<Boolean, IRemovableConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "target", attribute = "nodes", description = "the nodes this filter operates on", optional = true)
	public ETargetNodes targetNodes;

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		if (targetNodes == null) {
			return super.getTargetNodes();
		}

		return targetNodes;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFilteredForValue(Boolean value) {
		return value;
	}
}
