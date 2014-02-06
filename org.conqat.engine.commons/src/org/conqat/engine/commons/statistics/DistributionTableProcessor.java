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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.math.Range;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.SetNode;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 67F2C43DF076F2E65CD608A1C6C675F4
 */
@AConQATProcessor(description = "This processor creates a simple node structure that can be layouted as "
		+ "table with the TableLayouter. This table describes the distribution of a "
		+ "set of specified metrics with respect to a selected metric (called "
		+ "'principal metric'). Example: The input of the processor is a "
		+ "SourceCodeElementTree where each node is attributed with its Nesting Depth, "
		+ "its LOC and its SLOC. The principal metric is Nesting Depth and the "
		+ "specified boundaries are 3 and 5. Then this processor will create a table "
		+ "with x rows. Row 1 describes the bracket ]-y;3], the columns LOC and "
		+ "SLOC contain the added LOC/SLOC of all files that have nesting depth <=3 "
		+ "(y is min(smallest boundary, smallest value found for principal metric). "
		+ "Row  Row 2 describes the bracket ]3;5]; the columns LOC and SLOC contain "
		+ "the added LOC/SLOC of all files that have nesting depth 3<n<=5. If there "
		+ "are files with nesting depth > 5, row 3 will be automatically added. It "
		+ "describes the bracket ]5;z]; the columns LOC and SLOC contain the added "
		+ "LOC/SLOC of all files that have nesting depth >5. "
		+ "(z is max(greatest boundary, greatest value found for principal metric). "
		+ "The final row contains the total amount of LOC/SLOC. If 'showPercentages' "
		+ "is set to true, the table contains additional columns that show the "
		+ "LOC/SLOC in a relative manner. Note: If the number of nodes (e.g. files) "
		+ "should be used as a metric, use the ConstantAssigner to set value '1' on each node.")
public class DistributionTableProcessor extends
		ConQATInputProcessorBase<IConQATNode> {

	/** Key used to store color */
	private static final String COLOR = "color";

	/** The key for the principal metric. */
	private String principalKey;

	/** This counter set stores the totals for each metric. */
	private final CounterSet<String> totals = new CounterSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "default", attribute = "color", description = "Default color that is used for ranges for which no color is specified (the range to infinity).")
	public ETrafficLightColor defaultColor;

	/**
	 * For each upper boundary (inclusive) this stores a counter set which
	 * stores the added metric values with this bracket.
	 */
	private final TreeMap<Double, CounterSet<String>> values = new TreeMap<Double, CounterSet<String>>();

	/**
	 * For each upper boundary (inclusive) this stores the corresponding
	 * assessment color.
	 */
	private final Map<Double, ETrafficLightColor> colors = new HashMap<Double, ETrafficLightColor>();

	/** Flag to turn on/off percentages. */
	private boolean showPercentages = true;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "principal-metric", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The principal metric defines.")
	public void setPrincipalMetric(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		principalKey = key;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "boundary", minOccurrences = 1, description = ""
			+ "The boundary defines the upper value (inclusive) of a bracket.")
	public void addBoundary(
			@AConQATAttribute(name = "value", description = "Upper bracket boundary (inclusive)") double boundary,
			@AConQATAttribute(name = "color", description = "Assessment color for this range up to boundary (inclusive)") ETrafficLightColor color) {
		values.put(boundary, new CounterSet<String>());
		colors.put(boundary, color);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "metric", minOccurrences = 1, description = ""
			+ "Metric to calculate distribution for.")
	public void addMetric(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		totals.inc(key, 0);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "showPercentages", minOccurrences = 0, maxOccurrences = 1, description = "Turn on/off percentage columns [default is true].")
	public void setShowPercentages(
			@AConQATAttribute(name = "value", description = "Use false to turn off percentage columns") boolean showPercentages) {
		this.showPercentages = showPercentages;
	}

	/** {@inheritDoc} */
	@Override
	public SetNode<String> process() {

		double lowerBoundary = determineLimits();

		initDataStructures();

		SetNode<String> result = new SetNode<String>("root");

		createTableBody(result, lowerBoundary);

		createSumRow(result);

		for (String key : totals.getKeys()) {
			NodeUtils.addToDisplayList(result, COLOR, key);
			if (showPercentages) {
				NodeUtils.addToDisplayList(result, getPercentageKey(key));
			}
		}

		result.setValue(NodeConstants.HIDE_ROOT, true);

		// we want to retain order of addition
		result.setValue(NodeConstants.COMPARATOR, null);
		return result;
	}

	/**
	 * Determine the upper and lower limits of the values of the principal
	 * metric.
	 * 
	 * @return the minimum value used in the table.
	 */
	private double determineLimits() {
		List<IConQATNode> leaves = TraversalUtils.listLeavesDepthFirst(input);

		double max = Double.NEGATIVE_INFINITY;
		double min = Double.POSITIVE_INFINITY;

		for (IConQATNode node : leaves) {
			double value;
			try {
				value = NodeUtils.getDoubleValue(node, principalKey);
			} catch (ConQATException e) {
				getLogger().warn(
						"Key " + principalKey + " undefined for node "
								+ node.getId());
				continue;
			}
			if (!Double.isNaN(value)) {
				min = Math.min(min, value);
				max = Math.max(max, value);
			}
		}

		if (max > values.lastKey()) {
			values.put(max, new CounterSet<String>());
		}

		return Math.min(min, values.firstKey());
	}

	/**
	 * This method fills the data structures {@link #totals} and {@link #values}
	 * that are subsequently used to create the table.
	 */
	private void initDataStructures() {
		List<IConQATNode> leaves = TraversalUtils.listLeavesDepthFirst(input);

		for (IConQATNode leaf : leaves) {
			visit(leaf);
		}
	}

	/**
	 * This method adds the values for a single node to {@link #totals} and
	 * {@link #values}.
	 */
	public void visit(IConQATNode node) {
		double value;
		try {
			value = NodeUtils.getDoubleValue(node, principalKey);
		} catch (ConQATException e) {
			// we logged this before in determineBounds
			return;
		}

		if (Double.isNaN(value)) {
			return;
		}

		CounterSet<String> valueSet = values.ceilingEntry(value).getValue();

		for (String key : totals.getKeys()) {
			// we use zero as default here to deal with possibly missing values.
			// No reason to log this, as this might happen frequently.
			int inc = (int) NodeUtils.getDoubleValue(node, key, 0);
			totals.inc(key, inc);
			valueSet.inc(key, inc);
		}
	}

	/** Create the table rows for each bracket. */
	private void createTableBody(SetNode<String> result, double lowerBoundary) {

		ArrayList<Double> keys = new ArrayList<Double>(values.keySet());

		// we don't have to repeat this if the smallest element is the minimum
		if (lowerBoundary == values.firstKey()) {
			keys.remove(0);
		}

		for (double upperBoundary : keys) {
			// we use the range class only for the nice formatting.
			Range range = new Range(lowerBoundary, false, upperBoundary, true);
			SetNode<String> child = new SetNode<String>(range
					.format(NumberFormat.getInstance()));
			ETrafficLightColor color = colors.get(upperBoundary);
			if (color == null) {
				color = defaultColor;
			}
			child.setValue(COLOR, new Assessment(color));

			CounterSet<String> valueSet = values.get(upperBoundary);
			for (String key : totals.getKeys()) {
				child.setValue(key, valueSet.getValue(key));

				if (showPercentages && totals.getValue(key) != 0) {
					double percentage = (double) valueSet.getValue(key)
							/ totals.getValue(key) * 100;
					child.setValue(getPercentageKey(key), percentage);
				}
			}
			result.addChild(child);
			lowerBoundary = upperBoundary;
		}
	}

	/** Create the bottom row that contains the sums. */
	private void createSumRow(SetNode<String> result) {
		SetNode<String> child = new SetNode<String>("Sum");
		result.addChild(child);

		for (String key : totals.getKeys()) {
			child.setValue(key, totals.getValue(key));
			if (showPercentages) {
				child.setValue(getPercentageKey(key), 100d);
			}
		}
	}

	/**
	 * Determine key to be used for the percentage display of the given key.
	 * This ensure that there are no overlaps between the originally specified
	 * keys and the keys used for percentages.
	 */
	private String getPercentageKey(String key) {
		key = "%" + key;
		while (totals.contains(key)) {
			key += "_";
		}
		return key;
	}
}