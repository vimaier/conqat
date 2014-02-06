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
package org.conqat.engine.commons.collections;

import java.util.Collection;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.SetNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;

/**
 * Abstract base class for map converters.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 5EF7DE7C0945E7F3D4F8EF059FEB320C
 */
public abstract class MapConverterBase extends ConQATProcessorBase {

	/** The key to write the result into. */
	private String key;

	/** Root node. */
	private SetNode<Object> node;

	/** Node to add values to. */
	@AConQATParameter(name = "node", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Node to add values to. "
			+ "If not defined a new node is created.")
	public void setNode(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC)
			SetNode<Object> node) {
		this.node = node;
	}

	/** Set the key used for writing the result. */
	@AConQATParameter(name = ConQATParamDoc.WRITEKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.WRITEKEY_DESC)
	public void setWriteKey(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC)
			String key) {
		this.key = key;
	}

	/** {@inheritDoc} */
	@Override
	public SetNode<Object> process() {

		// if we're not extending an existing node, create a new one
		if (node == null) {
			node = new SetNode<Object>("<dummy root>");
			node.setValue(NodeConstants.HIDE_ROOT, true);
		}

		// iterate over all elements of the mapping
		for (Object keyObject : getKeyElements()) {
			// check if node is already present, if not, create new one to add
			SetNode<Object> childNode = node.getChild(keyObject);
			if (childNode == null) {
				childNode = new SetNode<Object>(keyObject);
				node.addChild(childNode);
			}

			// obtain value and set it
			Object value = getValue(keyObject);
			if (value != null) {
				childNode.setValue(key, value);
			}
		}

		NodeUtils.addToDisplayList(node, key);
		return node;
	}

	/** Template method to obtain map's key elements. */
	protected abstract Collection<?> getKeyElements();

	/** Template method to obtain an item's value. */
	protected abstract Object getValue(Object object);
}