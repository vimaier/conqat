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
package org.conqat.engine.model_clones.detection.util;

import java.util.List;

import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.lib.commons.algo.UnionFind;

/**
 * Utility methods for dealing with the underlying graphs.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36615 $
 * @ConQAT.Rating GREEN Hash: 352A60061C8511BDD79674E125EE61DB
 */
public class GraphUtils {

	/**
	 * Returns whether the graph represented by the given nodes and edges is
	 * connected. The edges may only reference nodes from the given list. This
	 * method is optimized for small graphs (less than 10 nodes).
	 */
	public static boolean isConnectedSmallGraph(List<INode> nodes,
			List<IDirectedEdge> edges) {
		if (nodes.isEmpty()) {
			return false;
		}

		UnionFind uf = new UnionFind(true);
		int size = nodes.size();

		for (int i = 0; i < size; ++i) {
			uf.addElement();
		}

		// the indexOf operation is what makes this algorithm slow on bigger
		// graphs.
		for (IDirectedEdge edge : edges) {
			uf.union(nodes.indexOf(edge.getSourceNode()),
					nodes.indexOf(edge.getTargetNode()));
		}

		int representative = uf.find(0);
		for (int i = 1; i < size; ++i) {
			if (uf.find(i) != representative) {
				return false;
			}
		}

		return true;
	}
}
