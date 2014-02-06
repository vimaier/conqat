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
package org.conqat.engine.commons.traversal;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;

/**
 * This is a {@link NodeTraversingProcessorBase} where the target node field is
 * exposed (i.e. settable by the ConQAT user).
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 36404 $
 * @ConQAT.Rating GREEN Hash: 5D57DF03F6E2325C5D576C7BDA8F5252
 */
public abstract class TargetExposedNodeTraversingProcessorBase<E extends IConQATNode>
		extends NodeTraversingProcessorBase<E> {

	/** The notes targeted by this operation. */
	private ETargetNodes targetNodes = null;

	/** Set the targets to use. */
	@AConQATParameter(name = "target", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The target nodes to operate on.")
	public void setTargets(
			@AConQATAttribute(name = "nodes", description = "the nodes this operation targets")
			ETargetNodes targets) {
		this.targetNodes = targets;
	}

	/** {@inheritDoc} */
	@Override
	protected final ETargetNodes getTargetNodes() {
		if (targetNodes == null) {
			return getDefaultTargetNodes();
		}
		return targetNodes;
	}

	/** Returns the targets of the node traversal if the user did not define it. */
	protected abstract ETargetNodes getDefaultTargetNodes();
}