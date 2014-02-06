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
package org.conqat.engine.model_clones.model;

import java.util.Collection;

/**
 * (Directed) graph interface for the clone analysis. The graph may have
 * multiple edges between two nodes (multi graph) and self loops (although the
 * later are not relevant for clone detection).
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 5F37C44A52B89FBA7F4D84E72F52196E
 */
public interface IModelGraph {

	/** Returns the nodes in this model. */
	public Collection<INode> getNodes();

	/** Returns the edges in this model. */
	public Collection<IDirectedEdge> getEdges();
}