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
package org.conqat.engine.simulink.clones.model;

import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.lib.simulink.model.SimulinkLine;

/**
 * Implementation of the {@link IDirectedEdge} interface for Simulink.
 * 
 * @author $Author:hummelb $
 * @version $Rev: 35176 $
 * @ConQAT.Rating GREEN Hash: FBD7E0B0214141D18693F027934DC4D8
 */
public class SimulinkDirectedEdge implements IDirectedEdge {

	/** The corresponding simulink line. */
	private final SimulinkLine line;

	/** The normalized representation. */
	private final String normalized;

	/** The source node. */
	private final SimulinkNode source;

	/** The target node. */
	private final SimulinkNode target;

	/** Constructor. */
	public /* package */SimulinkDirectedEdge(SimulinkLine line, String normalized,
			SimulinkNode source, SimulinkNode target) {
		this.line = line;
		this.normalized = normalized;
		this.source = source;
		this.target = target;
	}

	/** {@inheritDoc} */
	@Override
	public INode getSourceNode() {
		return source;
	}

	/** {@inheritDoc} */
	@Override
	public INode getTargetNode() {
		return target;
	}

	/** Returns the line. */
	public SimulinkLine getLine() {
		return line;
	}

	/** {@inheritDoc} */
	@Override
	public String getEquivalenceClassLabel() {
		return normalized;
	}
}