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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.APipelineSource;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * A processor performing arbitrary calculations on the values stored on the
 * nodes. The error behaviour is to not create any output (i.e. write into the
 * key).
 * 
 * @author Benjamin Hummel
 * @author $Author: steidl $
 * @version $Rev: 43637 $
 * @ConQAT.Rating GREEN Hash: 9D96FDC15483D9A08FAB8867B5BBEB05
 */
@AConQATProcessor(description = "This processor calculates a function on "
		+ "every node in the provided input based on a mathematical expression. "
		+ "The expression can reference the results from other processors "
		+ "using const-variables and also the value stored in the node using "
		+ "key-variables. The result of the calculation is stored in the "
		+ "provided key at each node."
		+ "If any kind of error occurs (key not found, value not numeric, "
		+ "division by zero, etc.) no value is written. "
		+ CalculatorBase.PROCESSOR_DOC)
public class NodeValueCalculator extends CalculatorBase implements
		INodeVisitor<IConQATNode, NeverThrownRuntimeException> {

	/** The node to work on. */
	private IConQATNode input;

	/** The key to store the result into. */
	private String outputKey;

	/** Key variables are a mapping from variable names to (node) keys. */
	private final Map<String, String> keyVariables = new HashMap<String, String>();

	/** The keys to be removed from the display list. */
	private final Set<String> removeKeys = new HashSet<String>();

	/** Constant variables are introduced externally. */
	private final Map<String, Double> constVariables = new HashMap<String, Double>();

	/** The nodes to work on. */
	private ETargetNodes targets = ETargetNodes.ALL;

	/** Set the input. */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
	public void setInput(
			@APipelineSource @AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IConQATNode input) {
		this.input = input;
	}

	/** Set the key used for writing. */
	@AConQATParameter(name = ConQATParamDoc.WRITEKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The key used to store the calculated result into.")
	public void setOutputKey(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC) String key) {
		outputKey = key;
	}

	/** Set a variable that is read from a key. */
	@AConQATParameter(name = "key-variable", description = "Declare a variable "
			+ "whose value depends on the value of a key in the current node.")
	public void addKeyVariable(
			@AConQATAttribute(name = "name", description = "the name of the variable") String varName,
			@AConQATAttribute(name = "key", description = "the key used to look up the variable's value") String readKey,
			@AConQATAttribute(name = "remove", defaultValue = "true", description = ""
					+ "whether to remove this key from the display list") boolean remove) {

		keyVariables.put(varName, readKey);
		if (remove) {
			removeKeys.add(readKey);
		}
	}

	/** Set a variable to a constant value. */
	@AConQATParameter(name = "const-variable", description = "Declare a variable"
			+ "whose value is given in advance.")
	public void addConstVariable(
			@AConQATAttribute(name = "name", description = "the name of the variable") String varName,
			@AConQATAttribute(name = "value", description = "the value used for the variable") double value) {

		constVariables.put(varName, value);
	}

	/** Set the targets to use. */
	@AConQATParameter(name = "target", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The target nodes of this processor. Default is ALL.")
	public void setTargets(
			@AConQATAttribute(name = "nodes", description = "the nodes this operation targets") ETargetNodes targets) {
		this.targets = targets;
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode process() {

		for (String name : constVariables.keySet()) {
			addBshVariable(name, constVariables.get(name));
		}

		for (String name : keyVariables.keySet()) {
			addBshVariable(name, 0);
		}

		TraversalUtils.visitDepthFirst(this, input, targets);
		updateDisplayList(input);
		return input;
	}

	/** Update the display list of the provided root node. */
	private void updateDisplayList(IConQATNode root) {
		NodeUtils.getDisplayList(root).removeKeys(removeKeys);
		NodeUtils.addToDisplayList(root, outputKey);
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {

		for (String varName : keyVariables.keySet()) {
			Object o = node.getValue(keyVariables.get(varName));
			if (!(o instanceof Number)) {
				return; // ignore
			}
			addBshVariable(varName, ((Number) o).doubleValue());
		}

		try {
			node.setValue(outputKey, evaluateExpression());
		} catch (ConQATException e) {
			getLogger().warn("Had evaluation error: " + e);
		}
	}
}