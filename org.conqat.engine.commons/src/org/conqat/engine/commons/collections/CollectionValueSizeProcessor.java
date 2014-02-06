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
import java.util.HashSet;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * This processor sums the sizes of collections stored at multiple keys and
 * stores the result as a new value. Nodes without defined collections are
 * ignored. By default only leaves are processed.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 26C7FDB0D836816BB491EC8FDF7FF916
 */

@AConQATProcessor(description = "This processor sums the sizes of collections "
		+ "stored at multiple keys and stores the result as a new value. "
		+ "Nodes without defined collections are ignored. By default only "
		+ "leaves are processed. This processor can ideally be used to count "
		+ "the number of warnings stored at a node.")
public class CollectionValueSizeProcessor extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** Set of keys to check for collections. */
	private final HashSet<String> keys = new HashSet<String>();

	/** Key to write result to. */
	private String outputKey;

	/** Set the key to use. */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, description = ConQATParamDoc.READKEY_DESC)
	public void addKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC)
			String key) {

		keys.add(key);
	}

	/** Set the key used for writing. */
	@AConQATParameter(name = ConQATParamDoc.WRITEKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The key to write the aggregation value to. "
			+ "If not provided use the same key as for reading.")
	public void setWriteKey(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC)
			String key) {
		outputKey = key;
	}

	/** Add output key to display list. */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		super.setUp(root);
		NodeUtils.addToDisplayList(root, outputKey);
	}

	/** Process node. */
	@Override
	public void visit(IConQATNode node) {
		int sum = 0;
		for (String key : keys) {
			sum += getValue(node, key);
		}
		node.setValue(outputKey, sum);
	}

	/**
	 * Check if nodes has a value of type {@link Collection} at the specified
	 * key and return its size. Returns 0 if no collections was found.
	 */
	private int getValue(IConQATNode node, String key) {
		Object valueObject = node.getValue(key);
		if (!(valueObject instanceof Collection<?>)) {
			return 0;
		}
		return ((Collection<?>) valueObject).size();
	}

	/** By default all leaves are analyzed. */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.LEAVES;
	}

}