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
package org.conqat.engine.commons.arithmetics;

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.math.EAggregationStrategy;
import org.conqat.lib.commons.math.MathUtils;
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
 * This processor aggregates multiple values stored at a node using a specified
 * aggregation method.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 7ADD8E7F719CCE638F0B156DDDA9814F
 */
@AConQATProcessor(description = "This processor aggregates multiple values "
		+ "stored at a node using a specified aggregation method.")
public class MultiValueAggregator extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** The key used to store the result. */
	private String writeKey;

	/** The argument keys. */
	private final List<String> argumentKeys = new ArrayList<String>();

	/** Strategy used for aggregation. */
	private EAggregationStrategy strategy;

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = ConQATParamDoc.WRITEKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The key to write the assessment into.")
	public void setWriteKey(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC)
			String writeKey) {
		this.writeKey = writeKey;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "keys", minOccurrences = 1, description = "Add argument key.")
	public void addKey(
			@AConQATAttribute(name = "value", description = "Name of the key")
			String key) {
		argumentKeys.add(key);
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = ConQATParamDoc.AGG_STRATEGY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.AGG_STRATEGY_DESC)
	public void setAggregationStrategy(
			@AConQATAttribute(name = ConQATParamDoc.STRATEGY_NAME, description = ConQATParamDoc.STRATEGY_DESC)
			EAggregationStrategy strategy) {
		this.strategy = strategy;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		super.setUp(root);
		NodeUtils.addToDisplayList(root, writeKey);
	}

	/**
	 * Performs the aggregation
	 * 
	 * @throws ConQATException
	 *             if one or more of the argument values are no valid double
	 *             values.
	 */
	@Override
	public void visit(IConQATNode node) throws ConQATException {
		List<Double> values = new ArrayList<Double>();

		for (String key : argumentKeys) {
			double value = NodeUtils.getDoubleValue(node, key);
			values.add(value);
		}

		double result = MathUtils.aggregate(values, strategy);

		node.setValue(writeKey, result);
	}

}