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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Test implementation for the {@link IModelGraph} interface.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35170 $
 * @ConQAT.Rating GREEN Hash: 6937CC34742B85424F6341CD54812B49
 */
public class ModelGraphMock implements IModelGraph {

	/** The nodes. */
	public final List<INode> nodes = new ArrayList<INode>();

	/** The edges. */
	public final List<IDirectedEdge> edges = new ArrayList<IDirectedEdge>();

	/** {@inheritDoc} */
	@Override
	public Collection<IDirectedEdge> getEdges() {
		return edges;
	}

	/** {@inheritDoc} */
	@Override
	public Collection<INode> getNodes() {
		return nodes;
	}

}