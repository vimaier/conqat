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
package org.conqat.engine.commons.filter;

import java.util.Arrays;

import org.apache.commons.math.stat.descriptive.rank.Percentile;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @levd.rating GREEN Hash: 5949B13CC296761B65569F9927E5E7EA
 */
@AConQATProcessor(description = "This processor filters all leaves whose values are below the specified "
		+ "percentile. Example: For a certain key values 1, 3, 7, 9, 11, 16 are stored "
		+ "at the leaves and the filter is run for the 70th percentile. As the 70th "
		+ "percentile of the values is is 10.8, all leaves but the ones with values 11 "
		+ "and 16 would be filtered out.")
public class PercentileFilter extends
		KeyBasedFilterBase<Number, IRemovableConQATNode> {

	/** Percentile instance used to calculate percentiles. */
	private final Percentile percentile = new Percentile();

	/** Defines the percentile. */
	private double n;

	/** The bound is the nth percentile. */
	private double bound;

	/** ConQAT Parameter. */
	@AConQATParameter(name = "percentile", minOccurrences = 1, maxOccurrences = 1, description = "Specify percentile.")
	public void setPercentile(
			@AConQATAttribute(name = "value", description = "percentile must be > 0 and <=100") double n) {
		this.n = n;
	}

	/** Determine bound. */
	@Override
	protected void preProcessInput(IRemovableConQATNode input)
			throws ConQATException {
		bound = determineBound(input);
		getLogger().debug("Bound: " + bound);
	}

	/** Determine bound of percentile */
	private double determineBound(IConQATNode input) throws ConQATException {
		double[] values = TraversalUtils.getLeaveValues(input, getKey());
		Arrays.sort(values);
		return percentile.evaluate(values, n);
	}

	/** Restrict filtering to leaf nodes */
	@Override
	protected boolean isFiltered(IRemovableConQATNode node) {
		if (node.hasChildren()) {
			return false;
		}
		return super.isFiltered(node);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFilteredForValue(Number value) {
		return value.doubleValue() < bound;
	}

}