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
package org.conqat.engine.scripting;

import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Base class for processor that evaluate Beanshell expressions. {@value #DOC}.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F944C0110CAC91EAE403D749D9BE9390
 */
public abstract class LocalExpressionProcessorBase<N extends IConQATNode>
		extends TargetExposedNodeTraversingProcessorBase<N> {

	/** Binding name for the node id. */
	public static final String NODE_ID_BINDING = "__id__";

	/** Binding name for the node name. */
	public static final String NODE_NAME_BINDING = "__name__";

	/** Documentation string. */
	public static final String DOC = "For each node "
			+ "an expression can refer to all values stored at keys that are present in the"
			+ "display list. Additionally, the node id and the node name can be accessed via "
			+ NODE_ID_BINDING + " and " + NODE_NAME_BINDING + ".";

	/** The display list. */
	private DisplayList displayList;

	/** Returns {@link ETargetNodes#LEAVES} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** Get current display list and add keys defined by {@link #getKeys()}. */
	@Override
	protected void setUp(N root) {
		displayList = NodeUtils.getDisplayList(root);
		NodeUtils.addToDisplayList(root, getKeys());
	}

	/**
	 * Template method to determine keys that need to be added to the display
	 * list.
	 */
	protected abstract String[] getKeys();

	/**
	 * This method creates a Beanshell interpreter. {@value #DOC}.
	 */
	protected Interpreter createInterpreter(N node) throws EvalError {
		Interpreter interpreter = new Interpreter();

		for (String key : displayList) {
			Object value = node.getValue(key);
			if (value == null) {
				getLogger().warn(
						"Value for " + key + " stored at node " + node.getId()
								+ " was null.");
			} else {
				// replace whitespace with underscore (CR #1951)
				interpreter.set(key.replaceAll("\\s+", "_"), value);
			}
		}
		interpreter.set(NODE_ID_BINDING, node.getId());
		interpreter.set(NODE_NAME_BINDING, node.getName());
		return interpreter;
	}

}