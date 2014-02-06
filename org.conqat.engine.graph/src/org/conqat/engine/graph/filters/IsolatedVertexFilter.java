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
package org.conqat.engine.graph.filters;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.graph.nodes.ConQATVertex;

/**
 * This filter deletes all isolated vertices.
 * 
 * @author Tilman Seifert
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 181E84CB57A66C6F843531CBFF53E8E7
 */
@AConQATProcessor(description = "This filter removes all isolated vertices, i.e. "
		+ "those which are not connected to any other vertex.")
public class IsolatedVertexFilter extends VertexFilterBase {

	/** Filters out vertexes that have no incoming or outgoing edges */
	@Override
	protected boolean isFiltered(ConQATVertex vertex) {
		return vertex.numNeighbors() == 0;
	}
}