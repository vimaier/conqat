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
package org.conqat.engine.graph.concentrate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATGraphInnerNode;
import org.conqat.engine.graph.nodes.ConQATVertex;

/**
 * This processor simplifies the hierarchy of a graph by moving children up in
 * the hierarchy (closer to the root).
 * 
 * @author Benjamin Hummel
 * @author $Author: deissenb $
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: 5F6D9C9C345C64C1311151E43BB61D15
 */
@AConQATProcessor(description = ""
		+ "This processor simplifies the hierarchy of a graph by moving vertices up in "
		+ "the hierarchy (closer to the root). How far the vertices are moved is determined "
		+ "by a list of regular expressions, where the first match (i.e. the first "
		+ "pattern included in the id of the vertex) determines the target depth.")
public class HierarchySimplifier extends
		ConQATPipelineProcessorBase<ConQATGraph> {

	/** The mappings from pattern to depth. */
	private final List<PatternDepthPair> pairs = new ArrayList<PatternDepthPair>();

	/** The list of all parent nodes (used stack-like) during DFS traversal. */
	private final List<ConQATGraphInnerNode> parentList = new ArrayList<ConQATGraphInnerNode>();

	/** Add collapse pattern pair. */
	@AConQATParameter(name = "collapse", description = "Adds a rule to the collapse list. "
			+ "This list is checked in order an for a vertex the first matching entry is used.")
	public void addPatternDepthPair(
			@AConQATAttribute(name = "regex", description = ConQATParamDoc.REGEX_PATTERN_DESC) String regex,
			@AConQATAttribute(name = "depth", description = ""
					+ "The depth to which the vertex is relocated (where 0 is the root "
					+ "node). If the vertex has lower depth it is kept where it is.") int depth)
			throws ConQATException {
		if (depth < 0) {
			throw new ConQATException("Depth must be non-negative!");
		}
		pairs
				.add(new PatternDepthPair(CommonUtils.compilePattern(regex),
						depth));
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(ConQATGraph graph) throws ConQATException {
		traverse(graph);
	}

	/**
	 * Traverse the node hierarchie, relocate the vertices and remove empty
	 * inner nodes.
	 */
	private void traverse(ConQATGraphInnerNode node) throws ConQATException {
		int depth = parentList.size();
		parentList.add(node);

		for (ConQATGraphInnerNode inner : new ArrayList<ConQATGraphInnerNode>(
				node.getInnerNodes())) {
			traverse(inner);
		}

		for (ConQATVertex vertex : new ArrayList<ConQATVertex>(node
				.getChildVertices())) {
			int targetDepth = determineDepth(vertex.getId());
			if (targetDepth < depth) {
				vertex.relocate(parentList.get(targetDepth));
			}
		}

		parentList.remove(depth);
		if (!node.hasChildren()) {
			node.remove();
		}
	}

	/** For a given vertex id returns the target depth. */
	private int determineDepth(String id) {
		for (PatternDepthPair pdp : pairs) {
			if (pdp.pattern.matcher(id).find()) {
				return pdp.depth;
			}
		}
		return Integer.MAX_VALUE;
	}

	/** Storage for a mapping from a pattern to the desired depth. */
	private static class PatternDepthPair {

		/** The pattern to be included. */
		public final Pattern pattern;

		/** The target depth. */
		public final int depth;

		/** Create new pair. */
		public PatternDepthPair(Pattern pattern, int depth) {
			this.pattern = pattern;
			this.depth = depth;
		}
	}
}