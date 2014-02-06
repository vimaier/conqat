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
package org.conqat.engine.graph.label;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATVertex;
import edu.uci.ics.jung.algorithms.importance.PageRank;
import edu.uci.ics.jung.algorithms.importance.PageRankWithPriors;

/**
 * This processor labels every vertex with its page rank. Page rank is a way of
 * measuring the importance of a vertex and is famous for being the basis of the
 * google ranking algorithm.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 163CEC766F07FCF2D5D8D8C6AC7F6594
 */
@AConQATProcessor(description = "This processor labels every vertex of the "
		+ "graph with its page rank. Page rank is a way of measuring the "
		+ "importance of a vertex and is famous for being the basis of the "
		+ "google ranking algorithm. ")
public class PageRankLabeler extends ConQATPipelineProcessorBase<ConQATGraph> {

	/** Key used for writing. */
	@AConQATKey(description = "The page-rank value calculated.", type = "java.lang.Double")
	public static final String PAGERANK_KEY = "page-rank";

	/** The bias used in the algorithm. */
	private double bias = 0.15;

	/** prior set of names */
	private final Set<String> priors = new HashSet<String>();

	/** Set bias for page rank algorithm. */
	@AConQATParameter(name = "bias", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The bias (alpha) used in the page rank algorithm. Should be "
			+ "between 0.1 and 0.2. Default is 0.15.")
	public void setBias(
			@AConQATAttribute(name = "value", description = "value between 0.1 and 0.2")
			double bias) {
		this.bias = bias;
	}

	/** Add element of special interest. */
	@AConQATParameter(name = "prior", description = "Extend the PageRank algorithm "
			+ "by incorporating root nodes (priors). Whereas in PageRank the "
			+ "importance of a node is implicitly computed relative to all "
			+ "nodes in the graph now importance is computed relative to the "
			+ "specified root nodes.")
	public void addPrior(
			@AConQATAttribute(name = "id", description = "The ID of a vertex considered 'prior'")
			String elementName) {
		priors.add(elementName);
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(ConQATGraph graph) {
		NodeUtils.addToDisplayList(graph, PAGERANK_KEY);
		PageRank ranker = constructRanker(graph);
		ranker.setRemoveRankScoresOnFinalize(false);
		ranker.evaluate();

		// copy results to outputKey.
		for (ConQATVertex v : graph.getVertices()) {
			double value = ranker.getRankScore(v);
			v.setValue(PAGERANK_KEY, value);
		}
	}

	/** Returns the ranker used. */
	private PageRank constructRanker(ConQATGraph graph) {
		if (priors.isEmpty()) {
			return new PageRank(graph.getGraph(), bias);
		}

		Set<ConQATVertex> priorVertices = new HashSet<ConQATVertex>();
		for (String id : priors) {
			ConQATVertex vertex = graph.getVertexByID(id);
			if (vertex != null) {
				priorVertices.add(vertex);
			} else {
				getLogger().warn("No vertex with id " + id + " found!");
			}
		}
		return new PageRankWithPriors(graph.getGraph(), bias, priorVertices,
				null);
	}

}