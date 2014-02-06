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
package org.conqat.engine.commons.collections;

import java.util.Collection;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.SetNode;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.math.MathUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38909 $
 * @ConQAT.Rating GREEN Hash: 7A703458575938E80B75563816D8958A
 */
@AConQATProcessor(description = "Extracts descriptive statistics for a CounterSet:"
		+ "Total number of keys, and min, max, sum, mean, median of values.")
public class CounterSetStatistics extends
		ConQATInputProcessorBase<CounterSet<?>> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Value key", type = "java.lang.Double")
	public static final String VALUE_KEY = "Value";

	/** {@inheritDoc} */
	@Override
	public SetNode<String> process() {
		SetNode<String> node = new SetNode<String>("");

		NodeUtils.setHideRoot(node, true);
		NodeUtils.addToDisplayList(node, VALUE_KEY);

		addStatisticValueNode(node, "Number of keys", input.getKeys().size());

		Collection<Integer> values = input.values();
		addStatisticValueNode(node, "Min value", MathUtils.min(values));
		addStatisticValueNode(node, "Max value", MathUtils.max(values));
		addStatisticValueNode(node, "Mean value", MathUtils.mean(values));
		addStatisticValueNode(node, "Median value", MathUtils.median(values));
		addStatisticValueNode(node, "Sum of values", MathUtils.sum(values));

		return node;
	}

	/** Adds a statistic value with the given name under the key VALUE_KEY */
	private void addStatisticValueNode(SetNode<String> parent, String name,
			Object value) {
		SetNode<String> valueNode = new SetNode<String>(name);
		valueNode.setValue(VALUE_KEY, value);
		parent.addChild(valueNode);
	}
}
