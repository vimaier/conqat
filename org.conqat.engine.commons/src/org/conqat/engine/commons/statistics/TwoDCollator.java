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
package org.conqat.engine.commons.statistics;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.conqat.engine.commons.format.EValueFormatter;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.SmartStringComparator;
import org.conqat.lib.commons.collections.TwoDimHashMap;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 39875 $
 * @ConQAT.Rating GREEN Hash: 7E0AA9DC85F32149CD8F5EB53DD3B9A8
 */
@AConQATProcessor(description = "This processor produces a two-dimensional table. "
		+ "Each node of the input is assigned to one cell in this table, according to a row key and a column key. "
		+ "Row and column keys are treated as strings."
		+ "The value of this cell is then determined by the number of nodes (or the sum of a value of these nodes).")
public class TwoDCollator extends ConQATInputProcessorBase<IConQATNode> {

	/** The values for the cells. First key is row, second key is column. */
	private final TwoDimHashMap<String, String, Double> cells = new TwoDimHashMap<String, String, Double>();

	/** The values encountered for columns. */
	private Set<String> columnValues = new LinkedHashSet<String>();

	/** The values encountered for rows. */
	private Set<String> rowValues = new LinkedHashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "row", attribute = "key", description = "The key used to determine the row for a node.")
	public String rowKey;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "column", attribute = "key", description = "The key used to determine the column for a node.")
	public String columnKey;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "value", attribute = "key", optional = true, description = "The key used to determine the value of a node that is summed up in a cell. If this is not provided, each node has a value of 1.")
	public String valueKey = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "row-sum", attribute = "name", optional = true, description = "If this is provided, a sum row will be appended using the given name.")
	public String rowSumName = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "column-sum", attribute = "name", optional = true, description = "If this is provided, a sum column will be appended using the given name.")
	public String columnSumName = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "target", attribute = "nodes", optional = true, description = "The target nodes to operate on. Default is leaves.")
	public ETargetNodes targetNodes = ETargetNodes.LEAVES;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ensure-column", description = "Adds a value that is guaranteed to appear as a column, even if it is not part of the input data.")
	public void addColumnsValue(
			@AConQATAttribute(name = "value", description = "The value.") String value) {
		columnValues.add(value);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "columns-sorted", maxOccurrences = 1, description = "Can be used to sort the columns of the table. Default is false.")
	public void setColumnsSorted(
			@AConQATAttribute(name = "value", description = "If set to true, columns are sorted.") boolean sorted) {
		if (sorted) {
			// pass current values to method, as some values could have
			// been inserted already
			columnValues = smartSortedSet(columnValues);
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ensure-row", description = "Adds a value that is guaranteed to appear as a row, even if it is not part of the input data.")
	public void addRowsValue(
			@AConQATAttribute(name = "value", description = "The value.") String value) {
		rowValues.add(value);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "rows-sorted", maxOccurrences = 1, description = "Can be used to sort the rows of the table. Default is false.")
	public void setRowsSorted(
			@AConQATAttribute(name = "value", description = "If set to true, rows are sorted.") boolean sorted) {
		if (sorted) {
			// pass current values to method, as some values could have
			// been inserted already
			rowValues = smartSortedSet(rowValues);
		}
	}

	/**
	 * Returns a treeset that uses the {@link SmartStringComparator} and
	 * contains all of the given values.
	 */
	private Set<String> smartSortedSet(Set<String> values) {
		TreeSet<String> result = new TreeSet<String>(
				new SmartStringComparator());
		result.addAll(values);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode process() {

		// Do not collect leaf nodes if this has no children
		// (without this if, the root node would be collected)
		if (input.hasChildren() || targetNodes != ETargetNodes.LEAVES) {
			for (IConQATNode node : TraversalUtils.listDepthFirst(input,
					targetNodes)) {
				insertNode(node);
			}
		}

		return createTable();
	}

	/** Inserts the node into the {@link #cells}. */
	private void insertNode(IConQATNode node) {
		String row = NodeUtils.getStringValue(node, rowKey, "unknown");
		String column = NodeUtils.getStringValue(node, columnKey, "unknown");
		rowValues.add(row);
		columnValues.add(column);

		Double oldValue = cells.getValue(row, column);
		if (oldValue == null) {
			oldValue = 0.;
		}
		cells.putValue(row, column, oldValue + getNodeValue(node));
	}

	/** Returns the value for the node. */
	private double getNodeValue(IConQATNode node) {
		if (valueKey == null) {
			return 1;
		}
		try {
			return NodeUtils.getDoubleValue(node, valueKey);
		} catch (ConQATException e) {
			getLogger().error(
					"Missing value in key " + valueKey + " for node "
							+ node.getId());
			return 0;
		}
	}

	/** Creates a ConQAT node structure that contains the table. */
	private IConQATNode createTable() {
		ListNode rootNode = new ListNode("<table>");
		NodeUtils.setHideRoot(rootNode, true);

		DisplayList displayList = NodeUtils.getDisplayList(rootNode);
		for (String column : columnValues) {
			displayList.addKey(column, EValueFormatter.DOUBLE.getFormatter());
		}
		if (rowSumName != null) {
			displayList.addKey(rowSumName,
					EValueFormatter.DOUBLE.getFormatter());
		}

		// explicitly avoid sorting
		rootNode.setValue(NodeConstants.COMPARATOR, null);

		double[] columnSums = appendRows(rootNode);

		appendColumnSums(rootNode, columnSums);

		return rootNode;
	}

	/**
	 * Appends the nodes representing table rows to the root node.
	 * 
	 * @return an array of column sums.
	 */
	private double[] appendRows(ListNode rootNode) {
		double[] columnSums = new double[columnValues.size()];
		for (String row : rowValues) {
			double rowSum = 0;
			ListNode rowNode = new ListNode(row);

			int columnIndex = 0;
			for (String column : columnValues) {
				Double value = cells.getValue(row, column);
				if (value == null) {
					value = 0.;
				}
				rowSum += value;
				columnSums[columnIndex++] += value;
				rowNode.setValue(column, value);
			}

			if (rowSumName != null) {
				rowNode.setValue(rowSumName, rowSum);
			}

			rootNode.addChild(rowNode);
		}
		return columnSums;
	}

	/** Appends column sums if this is configured. */
	private void appendColumnSums(ListNode rootNode, double[] columnSums) {
		if (columnSumName == null) {
			return;
		}

		ListNode sumNode = new ListNode(columnSumName);
		double overallSum = 0;
		int columnIndex = 0;
		for (String column : columnValues) {
			double value = columnSums[columnIndex++];
			sumNode.setValue(column, value);
			overallSum += value;

			// also append the value to the root node to simplify some
			// operations
			rootNode.setValue(column, value);
		}

		if (rowSumName != null) {
			sumNode.setValue(rowSumName, overallSum);
			rootNode.setValue(rowSumName, overallSum);
		}

		rootNode.addChild(sumNode);
	}
}
