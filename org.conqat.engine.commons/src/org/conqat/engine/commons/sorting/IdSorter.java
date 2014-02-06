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
package org.conqat.engine.commons.sorting;

import java.util.Comparator;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: FCC0B14EBBCDBB2A21B74B010C96CB12
 */
@AConQATProcessor(description = "A processor to sort nodes according to their "
		+ "id. Correctly spoken the nodes are not sorted, but a "
		+ "corresponding comparator is assigned to all internal nodes which "
		+ "should be considered in the presentation.")
public class IdSorter extends SorterBase {

	/** {@inheritDoc} */
	@Override
	protected Comparator<IConQATNode> getComparator(IConQATNode node) {
		return NodeIdComparator.INSTANCE;
	}

}
