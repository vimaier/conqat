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
package org.conqat.engine.html_presentation.links;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 42169 $
 * @ConQAT.Rating GREEN Hash: 55466CE6212133C6C64ABDBA9E9FE993
 */
@AConQATProcessor(description = "Stores a link target for each node containing a link.")
public class LinkTargetAssigner extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "link", attribute = "target", description = "The link target value to store.", optional = true)
	public ELinkTarget target;

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** Sets constant in every node */
	@Override
	public void visit(IConQATNode node) {
		if (target != null && LinkProviderBase.obtainLink(node) != null) {
			node.setValue(LinkProviderBase.LINK_KEY_TARGET, target);
		}
	}

}
