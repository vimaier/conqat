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
package org.conqat.engine.commons.aggregation;

import junit.framework.TestCase;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.SetNode;
import org.conqat.engine.commons.testutils.NodeTestUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;

/**
 * Tests for aggregator processors.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E11FB34CE1F884E18781BA1DE86D7BA6
 */
public class AggregatorTest extends TestCase {

	/** Root node of the test tree. */
	private SetNode<String> root;

	/** Create test tree. */
	@Override
	public void setUp() {
		root = NodeTestUtils.createRootNode();
		NodeTestUtils.addNodes(root, "test1/test2/test3/test4/test5");
		NodeTestUtils.addNodes(root, "test1/test2/test3/test4/test6");
		NodeTestUtils.addNodes(root, "test1/test2/test3/test4/test7");
		NodeTestUtils.addNodes(root, "test1/test2/test3/test5/test8");

	}

	/** Test {@link SumAggregator}. */
	public void testSumAggregator() throws ConQATException {
		testAggregator(new SumAggregator(), 40);
	}

	/** Test {@link MaximumAggregator}. */
	public void testMaximumAggregator() throws ConQATException {
		testAggregator(new MaximumAggregator(), 10);
	}

	/** Test {@link MinimumAggregator}. */
	public void testMinimumAggregator() throws ConQATException {
		testAggregator(new MinimumAggregator(), 10);
	}

	/** Test {@link AverageAggregator}. */
	public void testAverageAggregator() throws ConQATException {
		testAggregator(new AverageAggregator(), 10);
	}

	/** Test {@link LeafCounter}. */
	public void testLeafCounter() throws ConQATException {
		LeafCounter aggregator = new LeafCounter();
		aggregator.setWriteKey("#leafs");
		aggregator.init(new ProcessorInfoMock());
		aggregator.setRoot(root);

		IConQATNode node = aggregator.process();

		assertEquals(4, node.getValue("#leafs"));
	}

	/** Test ignoreKey with {@link SumAggregator}. */
	public void testIgnoreKey() throws ConQATException {
		SumAggregator aggregator = new SumAggregator();
		aggregator.ignoreKey = "ignore";

		// Set ignore key for first leaf
		IConQATNode node = root;
		while (node.hasChildren()) {
			IConQATNode[] children = node.getChildren();
			node = children[0];
		}
		node.setValue(aggregator.ignoreKey, true);

		testAggregator(aggregator, 30);
	}

	/** Test aggregator. */
	private void testAggregator(AggregatorBase<?, ?> aggregator,
			double expectedValue) throws ConQATException {
		NodeTestUtils.setValue(root, "testKey", 10);
		aggregator.setReadKey("testKey", AggregatorBase.DEFAULT_TARGET_KEY);
		aggregator.init(new ProcessorInfoMock());

		aggregator.setRoot(root);

		IConQATNode node = aggregator.process();

		Number value = (Number) node.getValue("testKey");
		assertEquals(expectedValue, value.doubleValue());
	}
}