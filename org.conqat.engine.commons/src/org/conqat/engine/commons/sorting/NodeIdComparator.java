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

import org.conqat.engine.commons.node.IConQATNode;

/**
 * Compares {@link IConQATNode}s by their ids.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35124 $
 * @ConQAT.Rating GREEN Hash: BBC5CE71482BA518C593E461E78259EB
 */
public class NodeIdComparator implements Comparator<IConQATNode> {

	/** Singleton instance of this comparator */
	public static final NodeIdComparator INSTANCE = new NodeIdComparator();

	/** {@inheritDoc} */
	@Override
	public int compare(IConQATNode o1, IConQATNode o2) {
		return o1.getId().compareTo(o2.getId());
	}

}