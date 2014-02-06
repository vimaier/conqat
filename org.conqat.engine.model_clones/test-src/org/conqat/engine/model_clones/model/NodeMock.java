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
 * Test implementation for the {@link INode} interface.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35170 $
 * @ConQAT.Rating GREEN Hash: 13D801C5DD6B3A774A894E371FBFD589
 */
public class NodeMock implements INode {

	/** Equivalence representative. */
	private final String eqRep;

	/** Weight of this node. */
	private final int weight;

	/** Constructor for unit weight nodes. */
	public NodeMock(int eqRep) {
		this(eqRep, 1);
	}

	/** Constructor. */
	public NodeMock(int eqRep, int weight) {
		this(Integer.toString(eqRep), weight);
	}

	/** Constructor. */
	public NodeMock(String eqRep) {
		this(eqRep, 1);
	}

	/** Constructor. */
	public NodeMock(String eqRep, int weight) {
		this.eqRep = eqRep;
		this.weight = weight;
	}

	/** {@inheritDoc} */
	@Override
	public String getEquivalenceClassLabel() {
		return eqRep;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "node " + hashCode() + ": " + eqRep;
	}

	/** {@inheritDoc} */
	@Override
	public int getWeight() {
		return weight;
	}
}