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

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATVertex;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.utils.MutableDouble;

/**
 * This processor labels every vertex with its betweenness centrality.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AD672A790BBFF8238D4AF8D2BCC2D04E
 */
@AConQATProcessor(description = "This processor labels every vertex of the "
		+ "graph with its betweenness centrality. Basically this is a measure "
		+ " of the number of shortest paths passing through a vertex and thus "
		+ "its importance.")
public class BetweennessCentralityLabeler extends
		ConQATPipelineProcessorBase<ConQATGraph> {

	/** Key used for writing. */
	@AConQATKey(description = "The betweenness value calculated.", type = "java.lang.Double")
	public static final String BETWEENNESS_KEY = "betweenness-centrality";

	/** Whether to normalize the result. */
	private boolean normalize = false;

	/** Set whether to normalize or not. */
	@AConQATParameter(name = "normalize", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Select whether the results should be normalized by dividing the "
			+ "result by (n-1)(n-2)/2. Default is false.")
	public void setNormalize(
			@AConQATAttribute(name = "value", description = "true or false")
			boolean normalize) {
		this.normalize = normalize;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(ConQATGraph graph) {
		NodeUtils.addToDisplayList(graph, BETWEENNESS_KEY);

		Graph graph1 = graph.getGraph();
		BetweennessCentrality ranker = new BetweennessCentrality(graph1, true,
				false);

		ranker.setRemoveRankScoresOnFinalize(false);
		ranker.evaluate();

		// now copy results to outputKey (and normalize if required).
		double factor = 1;
		if (normalize && graph1.numVertices() > 2) {
			double n = graph1.numVertices();
			factor = 2. / ((n - 1) * (n - 2));
		}

		for (ConQATVertex v : graph.getVertices()) {
			MutableDouble md = (MutableDouble) v.getValue(ranker
					.getRankScoreKey());
			v.setValue(BETWEENNESS_KEY, md.doubleValue() * factor);
		}
	}
}