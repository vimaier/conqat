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
package org.conqat.engine.model_clones.label;

import java.util.BitSet;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.digest.Digester;
import org.conqat.engine.model_clones.model.INode;

/**
 * A graph label combines a node partition with a representation of the
 * adjacency list which uniquely determines the structure.
 * 
 * @author pfaehlem
 * @author $Author: hummelb $
 * @version $Rev: 42074 $
 * @ConQAT.Rating GREEN Hash: 373E5675B58E2E42EA9EDEB0A1445A7D
 */
public class GraphLabel {

	/** The nodes. */
	private final List<INode> nodes;

	/**
	 * Flat bit vector representation of the adjacency matrix. This may be null
	 * to indicate a trivial label without the structural information.
	 */
	private final BitSet adjacencyMatrix;

	/** The number of edges of the labeled graph. */
	private final int edgeCount;

	/** Caches the result of {@link #hashCode()}. */
	private int hashCode = 0;

	/**
	 * Constructor.
	 * 
	 * @param adjacencyMatrix
	 *            this may be null to indicate a label without edge information.
	 */
	public GraphLabel(List<INode> nodes, int edgeCount, BitSet adjacencyMatrix) {
		this.nodes = nodes;
		this.edgeCount = edgeCount;
		this.adjacencyMatrix = adjacencyMatrix;
	}

	/** Returns the nodes stored in this label in the canonical order. */
	public UnmodifiableList<INode> getNodes() {
		return CollectionUtils.asUnmodifiable(nodes);
	}

	/** Returns whether this is a trivial label (with not adjacencyMatrix). */
	public boolean isTrivial() {
		return adjacencyMatrix == null;
	}

	/**
	 * Returns a compact string which describes this label. The string does not
	 * contain full information, but rather is a (MD5-based) hash value.
	 * However, comparison based on this will often be sufficient.
	 */
	public String getTextualHash() {
		StringBuilder sb = new StringBuilder();
		sb.append(edgeCount);
		for (INode node : nodes) {
			sb.append("-- " + node.getEquivalenceClassLabel());
		}

		if (adjacencyMatrix == null) {
			sb.append(" -- trivial --");
		} else {
			sb.append(adjacencyMatrix.toString());
		}

		return Digester.createMD5Digest(sb.toString());
	}

	/**
	 * Checks if two labels are equal. If adjacency matrices are present, this
	 * means that the graphs represented by both labels are isomorphic.
	 * Otherwise this at least hints to isomorphism.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GraphLabel)) {
			return false;
		}

		GraphLabel other = (GraphLabel) o;

		if (other.nodes.size() != nodes.size() || other.edgeCount != edgeCount) {
			return false;
		}

		for (int i = 0; i < nodes.size(); ++i) {
			if (!nodes.get(i).getEquivalenceClassLabel().equals(
					other.nodes.get(i).getEquivalenceClassLabel())) {
				return false;
			}
		}

		if (adjacencyMatrix == null) {
			return other.adjacencyMatrix == null;
		}

		return adjacencyMatrix.equals(other.adjacencyMatrix);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		if (hashCode == 0) {
			if (adjacencyMatrix != null) {
				hashCode = adjacencyMatrix.hashCode();
			}
			hashCode += 31 * edgeCount;
			for (INode node : nodes) {
				hashCode = 17 * hashCode
						+ node.getEquivalenceClassLabel().hashCode();
			}
		}
		return hashCode;
	}
}