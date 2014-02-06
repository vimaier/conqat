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
package org.conqat.engine.model_clones.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for creating graphs for testing.
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 36620 $
 * @ConQAT.Rating GREEN Hash: 30521D7CA2896ADC9BA8A4D2797471F6
 */
public class TestGraphUtils {

	/** Creates a list of nodes. */
	public static List<INode> createNodes(int numNodes, boolean uniqueNodeLabels) {
		List<INode> nodes = new ArrayList<INode>();
		for (int i = 0; i < numNodes; ++i) {
			int nodeLabel = 0;
			if (uniqueNodeLabels) {
				nodeLabel = i;
			}
			nodes.add(new NodeMock(nodeLabel));
		}
		return nodes;
	}

}