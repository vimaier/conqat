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
package org.conqat.engine.simulink.clones.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.conqat.engine.model_clones.model.INode;

/**
 * Implementation of the {@link IModelGraph} interface for Simulink.
 * 
 * @author $Author:hummelb $
 * @version $Rev: 35176 $
 * @ConQAT.Rating GREEN Hash: 6700C1DD9991B4D385B288B87C0CDDC1
 */
public class SimulinkModelGraph implements IModelGraph {

	/** The nodes of this model. */
	private final List<SimulinkNode> nodes = new ArrayList<SimulinkNode>();

	/** The edges of this model. */
	private final List<SimulinkDirectedEdge> edges = new ArrayList<SimulinkDirectedEdge>();

	/** Constructor. */
	/* package */SimulinkModelGraph() {
		// nothing to do
	}

	/** Adds a node to the graph. */
	/* package */void addNode(SimulinkNode node) {
		nodes.add(node);
	}

	/** Adds an edge to the graph. */
	/* package */void addEdge(SimulinkDirectedEdge edge) {
		edges.add(edge);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<IDirectedEdge> getEdges() {
		return (List) edges;
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<INode> getNodes() {
		return (List) nodes;
	}

}