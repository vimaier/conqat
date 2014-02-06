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
package org.conqat.engine.model_clones.detection;

import static org.conqat.lib.commons.string.StringUtils.CR;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.model_clones.detection.util.ICloneReporter;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Mock implementation of ICloneReporter. Gathers all clones reported during a
 * clone detection into a list of clones.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36733 $
 * @ConQAT.Rating GREEN Hash: 1069B5CE74F8C72C3A5924BAB268928E
 */
public class ModelCloneReporterMock implements ICloneReporter {

	/** The list of clones. */
	public final List<ModelClone> modelClones = new ArrayList<ModelClone>();

	/** Number of clones to be reported in current transmission. */
	private int numClones;

	/** Number of nodes contained in currently transmitted clone. */
	private int numNodes;

	/** Number of edges contained in currently transmitted clone. */
	private int numEdges;

	/** {@inheritDoc} */
	@Override
	public void startModelCloneGroup(int numClones, int numNodes, int numEdges) {
		modelClones.add(new ModelClone());
		this.numClones = numClones;
		this.numNodes = numNodes;
		this.numEdges = numEdges;
	}

	/** {@inheritDoc} */
	@Override
	public void addModelCloneInstance(List<INode> nodes,
			List<IDirectedEdge> edges) {
		CCSMAssert.isTrue(!modelClones.isEmpty(), "Protocol violation!");
		CCSMAssert.isTrue(nodes.size() == numNodes, "Wrong number of nodes!");
		CCSMAssert.isTrue(edges.size() == numEdges, "Wrong number of edges!");

		ModelClone mc = modelClones.get(modelClones.size() - 1);
		CCSMAssert.isTrue(mc.nodes.size() < numClones,
				"Too many clones reported!");

		mc.nodes.add(nodes);
		mc.edges.add(edges);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return StringUtils.concat(modelClones, CR + "======" + CR);
	}

	/** Class for storing a single model clone. */
	public static class ModelClone {

		/** Node lists. */
		public final List<List<INode>> nodes = new ArrayList<List<INode>>();

		/** Edge lists. */
		public final List<List<IDirectedEdge>> edges = new ArrayList<List<IDirectedEdge>>();

		/** {@inheritDoc} */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < nodes.size(); ++i) {
				if (i > 0) {
					sb.append("---" + CR);
				}
				List<INode> currentNodes = nodes.get(i);
				for (INode node : currentNodes) {
					sb.append("  " + currentNodes.indexOf(node) + ": "
							+ node.toString() + CR);
				}
				for (IDirectedEdge edge : edges.get(i)) {
					sb.append("  " + currentNodes.indexOf(edge.getSourceNode())
							+ " -> "
							+ currentNodes.indexOf(edge.getTargetNode()) + CR);
				}
			}
			return sb.toString();
		}
	}
}