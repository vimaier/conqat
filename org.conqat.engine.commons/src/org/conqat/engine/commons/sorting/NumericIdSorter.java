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
package org.conqat.engine.commons.sorting;

import java.util.Comparator;

import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * A processor that sorts node by their numeric id. If nodes have a non-numeric
 * id, they are not sorted.
 * 
 * @author $Author: feilkas $
 * @version $Rev: 41422 $
 * @ConQAT.Rating GREEN Hash: C8531C1F3BD29C543199D4DE416A5FEE
 */
@AConQATProcessor(description = "A processor that sorts node by their numeric id."
		+ "If nodes have a non-numeric id, they are not sorted. "
		+ "Correctly spoken the nodes are not sorted, but a "
		+ "corresponding comparator is assigned to all internal nodes which "
		+ "should be considered in the presentation.")
public class NumericIdSorter extends SorterBase {

	/** {@inheritDoc} */
	@Override
	protected Comparator<IConQATNode> getComparator(IConQATNode node) {
		return NumericIdComparator.INSTANCE;
	}

	/** The class used for sorting. */
	public static class NumericIdComparator implements Comparator<IConQATNode>,
			IDeepCloneable {

		/** The single instance. */
		private static final NumericIdComparator INSTANCE = new NumericIdComparator();

		/** Prevent instantiation. Use {@link #getInstance()} instead. */
		private NumericIdComparator() {
			// nothing to do
		}

		/** Returns the only instance of this class. */
		public static NumericIdComparator getInstance() {
			return INSTANCE;
		}

		/**
		 * Compares the numeric ids of to nodes. If they are not numeric, zero
		 * is returned.
		 */
		@Override
		public int compare(IConQATNode node1, IConQATNode node2) {
			return Double.compare(getNodeValue(node1), getNodeValue(node2));
		}

		/** Returns the value for this node or NaN. */
		private double getNodeValue(IConQATNode node) {
			try {
				return Double.parseDouble(node.getId());
			} catch (NumberFormatException e) {
				return Double.NaN;
			}
		}

		/** Do not clone. */
		@Override
		public NumericIdComparator deepClone() {
			return this;
		}
	}
}