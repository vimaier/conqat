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
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 39987 $
 * @ConQAT.Rating GREEN Hash: AE9B1E88DD63FC0AB45FEEE452924DA2
 */
@AConQATProcessor(description = "A processor to sort nodes according to  "
		+ "the string representation of a value stored under a provided key. "
		+ "Correctly spoken, the nodes are not sorted, but a corresponding "
		+ "comparator is assigned to all internal nodes which should be considered "
		+ "in the presentation.")
public class StringValueSorter extends SorterBase {

	/** The comparator used. */
	private StringValueComparator comparator;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The key used to read the values for sorting. ")
	public void setKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		this.comparator = new StringValueComparator(key);
	}

	/** {@inheritDoc} */
	@Override
	protected Comparator<IConQATNode> getComparator(IConQATNode node) {
		return comparator;
	}

	/** The class used for sorting. */
	public static class StringValueComparator implements
			Comparator<IConQATNode>, IDeepCloneable {

		/** The key used to find the value. */
		private final String key;

		/** Constructor. */
		public StringValueComparator(String key) {
			this.key = key;
		}

		/**
		 * Compare the string representation of the values at both nodes. This
		 * handles cases where one or both of the values are not defined.
		 */
		@Override
		public int compare(IConQATNode node1, IConQATNode node2) {
			Object o1 = node1.getValue(key);
			Object o2 = node2.getValue(key);

			if (o1 == null) {
				if (o2 == null) {
					return 0;
				}
				return -1;
			}
			if (o2 == null) {
				return 1; // we already know o1 != null
			}

			return o1.toString().compareTo(o2.toString());
		}

		/** {@inheritDoc} */
		@Override
		public IDeepCloneable deepClone() {
			// no mutable fields, so no cloning needed
			return this;
		}
	}
}