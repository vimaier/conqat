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

/**
 * Test implementation for the {@link IDirectedEdge} interface.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35170 $
 * @ConQAT.Rating GREEN Hash: 3ED9F393A5A8009359C00CE2C0BBFF90
 */
public class DirectedEdgeMock implements IDirectedEdge {

	/** Equivalence representative. */
	private final String eqRep;

	/** The source. */
	private final INode source;

	/** The target. */
	private final INode target;

	/** Constructor. */
	public DirectedEdgeMock(INode source, INode target, String eqRep) {
		this.source = source;
		this.target = target;
		this.eqRep = eqRep;
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

	/** {@inheritDoc} */
	@Override
	public String getEquivalenceClassLabel() {
		return eqRep;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "edge " + source + " -> " + target + " [" + eqRep + "]";
	}
}