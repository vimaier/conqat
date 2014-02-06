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
package org.conqat.engine.graph.cluster;

import org.conqat.engine.core.core.AConQATProcessor;
import edu.uci.ics.jung.algorithms.cluster.GraphClusterer;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;

/**
 * Divide the graph into its weakly connected components.
 * 
 * @author Tilman Seifert
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7AEBCEB18D563ABD83BED5FF2DA2205C
 */
@AConQATProcessor(description = "Clusters the graph into its weakly connected components. "
		+ "The weakly connected components of a directed graph are the maximal connected "
		+ "components of the induced undirected graph (i.e. when interpreting each edge "
		+ "as undirected).")
public class WeaklyConnectedComponentClusterer extends ClustererBase {

	/** {@inheritDoc} */
	@Override
	protected GraphClusterer obtainGraphClusterer() {
		return new WeakComponentClusterer();
	}
}