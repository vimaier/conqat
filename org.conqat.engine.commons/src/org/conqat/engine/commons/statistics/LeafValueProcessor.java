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
package org.conqat.engine.commons.statistics;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * This processor creates a KeyedData object by adding all leaves of a
 * {@link IConQATNode} tree and taking values stored at the leaves.
 * 
 * @author Florian Deissenboeck
 * @author $Author: goede $
 * @version $Rev: 40743 $
 * @ConQAT.Rating GREEN Hash: B725B5AB65ACB24563628A480BF68638
 */
@AConQATProcessor(description = "This processor creates a KeyedData object by "
		+ "adding all leaves of a IConQATNode-tree and taking values stored at the leaves, "
		+ "i.e. this creates a map from node ids to values.")
public class LeafValueProcessor extends ConQATInputProcessorBase<IConQATNode>
		implements INodeVisitor<IConQATNode, NeverThrownRuntimeException> {

	/** Key for the value. */
	private String key;

	/** Result object. */
	private final KeyedData<String> result = new KeyedData<String>();

	/** Set key for value. */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Key pointing to the desired value.")
	public void setKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		this.key = key;
	}

	/** {@inheritDoc} */
	@Override
	public KeyedData<?> process() {
		TraversalUtils.visitLeavesDepthFirst(this, input);
		return result;
	}

	/**
	 * This method obtains the values from the node and stores them in
	 * {@link #result}.
	 */
	@Override
	public void visit(IConQATNode node) {
		Object valueObject = node.getValue(key);
		if (valueObject == null) {
			getLogger().warn(
					"Null value for key " + key + "@" + node.getId()
							+ " ignored.");
			return;
		}
		if (!(valueObject instanceof Number)) {
			getLogger().warn(
					"Non-numeric value for key " + key + "@" + node.getId()
							+ " ignored: " + valueObject);
			return;
		}

		double value = ((Number) valueObject).doubleValue();
		result.add(node.getId(), value);
	}
}