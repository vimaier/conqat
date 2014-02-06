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
 * This processor sorts all nodes according to their name.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: F5230A5DE5E101EC4E20939FE21A0B68
 */
@AConQATProcessor(description = "A processor to sort nodes according to their "
		+ "name. Correctly spoken the nodes are not sorted, but a "
		+ "corresponding comparator is assigned to all internal nodes which "
		+ "should be considered in the presentation.")
public class NameSorter extends SorterBase {

	/** {@inheritDoc} */
	@Override
	protected Comparator<IConQATNode> getComparator(IConQATNode node) {
		return NameComparator.instance;
	}

	/** The class used for sorting. */
	public static class NameComparator implements Comparator<IConQATNode>,
			IDeepCloneable {

		/** The single instance. */
		private static final NameComparator instance = new NameComparator();

		/** Prevent instantiation. Use {@link #getInstance()} instead. */
		private NameComparator() {
			// nothing to do
		}

		/** Returns the only instance of this class. */
		public static NameComparator getInstance() {
			return instance;
		}

		/**
		 * Compare the names of both nodes at the specified key. This handles
		 * cases where one or both of the names is <code>null</code>.
		 */
		@Override
		public int compare(IConQATNode node1, IConQATNode node2) {
			String name1 = node1.getName();
			String name2 = node2.getName();

			if (name1 == null) {
				name1 = "";
			}
			if (name2 == null) {
				name2 = "";
			}

			return name1.compareTo(name2);
		}

		/**
		 * Do not clone.
		 */
		@Override
		public NameComparator deepClone() {
			return this;
		}
	}
}