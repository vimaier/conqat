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

import junit.framework.TestCase;
import org.conqat.engine.commons.node.SetNode;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.testutils.NodeTestUtils;
import org.conqat.engine.core.core.ConQATException;

/**
 * Test for {@link RankedMetric}.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 77BF356FB29F86A597BD2C1567E3EF0E
 */
public class RankedMetricTest extends TestCase {
	/** Read key. */
	private static final String KEY = "KEY";

	/** Simple test case for ranker. */
	public void test() throws ConQATException {
		SetNode<String> root = NodeTestUtils.createRootNode();
		SetNode<String> node1 = createNode(root, "node1", 1);
		SetNode<String> node2 = createNode(root, "node2", 5);
		SetNode<String> node3 = createNode(root, "node3", 9);
		SetNode<String> node4 = createNode(root, "node4", 20);
		SetNode<String> node5 = createNode(root, "node5", 25);
		SetNode<String> node6 = createNode(root, "node6", 31);
		SetNode<String> node7 = createNode(root, "node7", 50);

		RankedMetric metric = createMetric(root, 3);

		assertEquals(0d, metric.determineWeightedRank(node1));
		assertEquals(0d, metric.determineWeightedRank(node2));
		assertEquals(1d, metric.determineWeightedRank(node3));
		assertEquals(1d, metric.determineWeightedRank(node4));
		assertEquals(1d, metric.determineWeightedRank(node5));
		assertEquals(2d, metric.determineWeightedRank(node6));
		assertEquals(2d, metric.determineWeightedRank(node7));
	}

	/**
	 * Test for single rank.
	 * 
	 * @throws ConQATException
	 */
	public void testSingleRank() throws ConQATException {
		SetNode<String> root = NodeTestUtils.createRootNode();
		SetNode<String> node1 = createNode(root, "node1", 1);
		SetNode<String> node2 = createNode(root, "node2", 5);
		SetNode<String> node3 = createNode(root, "node3", 9);
		SetNode<String> node4 = createNode(root, "node4", 20);
		SetNode<String> node5 = createNode(root, "node5", 25);
		SetNode<String> node6 = createNode(root, "node6", 31);
		SetNode<String> node7 = createNode(root, "node7", 50);

		RankedMetric metric = createMetric(root, 1);

		assertEquals(0d, metric.determineWeightedRank(node1));
		assertEquals(0d, metric.determineWeightedRank(node2));
		assertEquals(0d, metric.determineWeightedRank(node3));
		assertEquals(0d, metric.determineWeightedRank(node4));
		assertEquals(0d, metric.determineWeightedRank(node5));
		assertEquals(0d, metric.determineWeightedRank(node6));
		assertEquals(0d, metric.determineWeightedRank(node7));
	}

	/**
	 * Test for equal values.
	 */
	public void testEqualValues() throws ConQATException {
		SetNode<String> root = NodeTestUtils.createRootNode();
		SetNode<String> node1 = createNode(root, "node1", 0);
		SetNode<String> node2 = createNode(root, "node2", 1);
		SetNode<String> node3 = createNode(root, "node3", 1);
		SetNode<String> node4 = createNode(root, "node4", 1);
		SetNode<String> node5 = createNode(root, "node5", 3);
		SetNode<String> node6 = createNode(root, "node6", 8);
		SetNode<String> node7 = createNode(root, "node7", 50);

		RankedMetric metric = createMetric(root, 6);

		assertEquals(0d, metric.determineWeightedRank(node1));
		assertEquals(1d, metric.determineWeightedRank(node2));
		assertEquals(1d, metric.determineWeightedRank(node3));
		assertEquals(1d, metric.determineWeightedRank(node4));
		assertEquals(3d, metric.determineWeightedRank(node5));
		assertEquals(4d, metric.determineWeightedRank(node6));
		assertEquals(5d, metric.determineWeightedRank(node7));
	}

	/** Create node, set value on it and return it. */
	private SetNode<String> createNode(SetNode<String> root, String path,
			double value) {
		NodeTestUtils.addNodes(root, path);
		SetNode<String> node = NodeTestUtils.getNode(root, path);
		node.setValue(KEY, value);
		return node;
	}

	/**
	 * Create metric and set up ranks.
	 */
	private RankedMetric createMetric(IConQATNode node, int ranks)
			throws ConQATException {
		RankedMetric metric = new RankedMetric(KEY, null, 1.0);
		metric.setUpRanks(node, ranks);
		return metric;
	}
}