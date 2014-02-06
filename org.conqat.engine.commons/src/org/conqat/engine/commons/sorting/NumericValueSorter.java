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

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * This processor sorts all nodes according to some numeric value.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 7A445FAC627BFFD22F3862F52AA1142D
 */
@AConQATProcessor(description = "A processor to sort nodes according to a "
		+ "numeric value stored under a provided key. "
		+ "If the numeric values of two nodes are. equal, they are ordered based on the"
		+ "lexical ordering of their node ids. Correctly spoken the "
		+ "nodes are not sorted, but a corresponding comparator is assigned "
		+ "to all internal nodes which should be considered in the "
		+ "presentation.")
public class NumericValueSorter extends SorterBase {

	/** The comparator used. */
	private NumericValueComparator comparator;

	/** Set the key used for sorting. */
	@AConQATParameter(name = ConQATParamDoc.READKEY_KEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The key used to read the values for sorting. The value received "
			+ "via this key should be numeric, otherwise a value of infinity is "
			+ "assumed.")
	public void setKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		comparator = new NumericValueComparator(key);
	}

	/** {@inheritDoc} */
	@Override
	protected Comparator<IConQATNode> getComparator(IConQATNode node) {
		return comparator;
	}

	/** The class used for sorting. */
	private static class NumericValueComparator implements
			Comparator<IConQATNode>, IDeepCloneable {

		/** The key used to find the value. */
		private final String key;

		/** Constructor. */
		public NumericValueComparator(String key) {
			this.key = key;
		}

		/**
		 * Compare the numbers stored at both nodes at the specified key. This
		 * handles cases where one or both of the values are not defined or are
		 * no numbers.
		 */
		@Override
		public int compare(IConQATNode node1, IConQATNode node2) {
			Object o1 = node1.getValue(key);
			Object o2 = node2.getValue(key);

			if (!(o1 instanceof Number)) {
				if (!(o2 instanceof Number)) {
					return 0;
				}
				return -1;
			}
			if (!(o2 instanceof Number)) {
				return 1; // we already know d1 != null
			}

			double d1 = ((Number) o1).doubleValue();
			double d2 = ((Number) o2).doubleValue();

			if (d1 < d2) {
				return -1;
			}

			if (d1 > d2) {
				return 1;
			}

			// if values are equal, compare based on ids
			if (node1.getId() != null) {
				return node1.getId().compareTo(node2.getId());
			}

			if (node2.getId() != null) {
				return -1;
			}

			// if both ids are null, we simply return 0
			return 0;
		}

		/** {@inheritDoc} */
		@Override
		public IDeepCloneable deepClone() {
			// no changeable fields, so no cloning needed
			return this;
		}
	}
}