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
package org.conqat.engine.persistence;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.StringSetNode;
import org.conqat.engine.commons.statistics.DateValueSeries;
import org.conqat.engine.commons.statistics.MultiDateValueSeries;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.ListMap;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 40278 $
 * @ConQAT.Rating GREEN Hash: 1B91BF5479A9124B666D5A69D9F2F7DD
 */
@AConQATProcessor(description = "This processor takes multiple numeric values from a node tree "
		+ "and stores them in a database table with the current time (or a "
		+ "explicitly specified time) and returns "
		+ "a MultiDateValueSeries with all values stored "
		+ "in the table. If the database table does not exist it will be "
		+ "created. This is currently tested for Microsoft SQL Server only.")
public class MultiValueSeriesProcessor extends ValueSeriesProcessor {

	/** Maps series names to IDs of nodes to map to this name. */
	private final ListMap<String, String> namesToNodeIDs = new ListMap<String, String>(
			new LinkedHashMap<String, List<String>>());

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "series-node", description = "Provides a node whose value is used for a series. "
			+ "The same name can be used multiple times to map multiple nodes (IDs) to a name.")
	public void addNode(
			@AConQATAttribute(name = "name", description = "The name of the corresponding series.") String name,
			@AConQATAttribute(name = "id", description = "The ID of the node whose value is used in the series.") String nodeId) {
		namesToNodeIDs.add(name, nodeId);
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "collector", attribute = "name", optional = true, description = "If this parameter is provided, "
			+ "the difference between the value at the root node and the registered target nodes is stored under this name.")
	public String collectorName = null;

	/** {@inheritDoc} */
	@Override
	public MultiDateValueSeries process() throws ConQATException {
		MultiDateValueSeries result = new MultiDateValueSeries();
		String originalTableName = tableName;

		double overallValue = NodeUtils.getDoubleValue(node, key);
		double sum = createSeries(result, originalTableName);
		if (collectorName != null) {
			appendCollectorSeries(result, originalTableName, overallValue - sum);
		}
		return result;
	}

	/** Creates the series for all nodes. */
	private double createSeries(MultiDateValueSeries result,
			String originalTableName) throws ConQATException {
		Map<String, IConQATNode> idToNodes = TraversalUtils
				.createIdToNodeMap(node);

		double sum = 0;
		for (String name : namesToNodeIDs.getKeys()) {
			double localSum = 0;
			for (String nodeId : namesToNodeIDs.getCollection(name)) {
				IConQATNode localNode = idToNodes.get(nodeId);
				if (localNode == null) {
					throw new ConQATException("No node with ID " + nodeId
							+ " found!");
				}
				localSum += NodeUtils.getDoubleValue(localNode, key);
			}

			node = new StringSetNode();
			node.setValue(key, localSum);
			setTable(originalTableName, name);

			getLogger().info("Processing series " + name);
			DateValueSeries series = super.process();
			sum += series.getValues().get(series.getLatestDate());
			result.addSeries(name, series);
		}
		return sum;
	}

	/** Sets the table according to the given original/base name and extension. */
	private void setTable(String originalTableName, String extension) {
		tableName = originalTableName + "_"
				+ extension.replaceAll("[^a-zA-Z0-9]", "_");
	}

	/** Appends the series used for the collector. */
	private void appendCollectorSeries(MultiDateValueSeries result,
			String originalTableName, double value) throws ConQATException {
		node = new StringSetNode();
		node.setValue(key, value);
		setTable(originalTableName, collectorName);

		getLogger().info("Processing collector");
		result.addSeries(collectorName, super.process());
	}
}
