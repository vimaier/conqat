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
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATVertex;
import edu.uci.ics.jung.algorithms.importance.HITS;
import edu.uci.ics.jung.utils.MutableDouble;

/**
 * This processor labels every vertex with its HITS (hypertext induced topic
 * selection) value. HITS is based on hub and authority values.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 58FF7A21F71F08086ECDCCAB2314FCF9
 */
@AConQATProcessor(description = "This processor labels every vertex of the "
		+ "graph with its HITS (hypertext induced topic selection) value. "
		+ "HITS is based on hub and authority values.")
public class HITSLabeler extends ConQATPipelineProcessorBase<ConQATGraph> {

	/** Key used for writing. */
	@AConQATKey(description = "The HITS value calculated.", type = "java.lang.Double")
	public static final String HITS_KEY = "hits";

	/** {@inheritDoc} */
	@Override
	protected void processInput(ConQATGraph graph) {
		NodeUtils.addToDisplayList(graph, HITS_KEY);

		HITS ranker = new HITS(graph.getGraph());
		ranker.setRemoveRankScoresOnFinalize(false);
		ranker.evaluate();

		// copy results to outputKey
		for (ConQATVertex v : graph.getVertices()) {
			MutableDouble md = (MutableDouble) v.getValue(ranker
					.getRankScoreKey());
			v.setValue(HITS_KEY, md.doubleValue());
		}
	}
}