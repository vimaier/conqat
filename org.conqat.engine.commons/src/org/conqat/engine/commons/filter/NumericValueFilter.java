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

import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This filter removes all nodes without a result-value in the given range.
 * 
 * @author Aichner Michael
 * @author Puchinger Christian
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @levd.rating GREEN Hash: 135DC3718BD45F91CAF741F3B7A3D4B9
 */
@AConQATProcessor(description = "Filters nodes based on a numeric value stored at the provided key. "
		+ "Nodes are only kept, if this value is within a defined range. "
		+ "Directories are not explicitly handled, but are removed if empty.")
public class NumericValueFilter extends
		KeyBasedFilterBase<Number, IRemovableConQATNode> {

	/** The smallest value to take into account. */
	private double lowerBound = Double.NEGATIVE_INFINITY;

	/** The biggest value to take into account. */
	private double upperBound = Double.POSITIVE_INFINITY;

	/** Target nodes this processer operates on. */
	private ETargetNodes targetNodes;

	/** Sets the lower bound. */
	@AConQATParameter(name = "lower", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Range's lower bound. Default is negative infinity.")
	public void setLowerBound(
			@AConQATAttribute(name = "value", description = "The bound.") double lowerBound) {

		this.lowerBound = lowerBound;
	}

	/** Sets the upper bound. */
	@AConQATParameter(name = "upper", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Range's upper bound. Default is positive infinity.")
	public void setUpperBound(
			@AConQATAttribute(name = "value", description = "The bound.") double upperBound) {

		this.upperBound = upperBound;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "target", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The target nodes to operate on.")
	public void setTargets(
			@AConQATAttribute(name = "nodes", description = "the nodes this operation targets") ETargetNodes targets) {
		targetNodes = targets;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		if (targetNodes == null) {
			return super.getTargetNodes();
		}

		return targetNodes;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFilteredForValue(Number value) {
		double d = value.doubleValue();
		return !(lowerBound <= d && d <= upperBound);
	}
}