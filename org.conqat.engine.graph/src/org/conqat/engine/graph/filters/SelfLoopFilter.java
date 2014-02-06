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
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * Filters out edges that start and end at the same vertex.
 * 
 * @author Elmar Juergens
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: 803FCCF0C7F281391B7773DB9B717163
 */
@AConQATProcessor(description = "Filters out edges that start and end at the same vertex.")
public class SelfLoopFilter extends EdgeFilterBase {

	/** {@inheritDoc} */
	@Override
	protected boolean isFiltered(DirectedSparseEdge edge) {
		return edge.getSource() == edge.getDest();
	}

}