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
package org.conqat.lib.simulink.builder;

/**
 * This enumeration stores port configuration of some well-known built-in
 * Simulink blocks.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1316472BF269FE6C27C07F05FF35A023
 */
/* package */enum EBlockType {

	/** DigitalClock */
	DigitalClock(0, 1),

	/** Clock */
	Clock(0, 1),

	/** FromWorkspace */
	FromWorkspace(0, 1),

	/** FromFile */
	FromFile(0, 1),

	/** Step */
	Step(0, 1),

	/** UniformRandomNumber */
	UniformRandomNumber(0, 1),

	/** RandomNumber */
	RandomNumber(0, 1),

	/** ToWorkspace */
	ToWorkspace(1, 0),

	/** Stop */
	Stop(1, 0),

	/** ToFile */
	ToFile(1, 0),

	/** From */
	From(0, 1),

	/** Goto */
	Goto(1, 0),

	/** DataStoreRead */
	DataStoreRead(0, 1),

	/** DataStoreWrite */
	DataStoreWrite(1, 0),

	/** DataStoreMemory */
	DataStoreMemory(0, 0),

	/** GotoTagVisibility */
	GotoTagVisibility(0, 0),

	/** Assertion */
	Assertion(1, 0),

	/** Terminator */
	Terminator(1, 0),

	/** Ground */
	Ground(0, 1),

	/** Constant */
	Constant(0, 1),

	/** Inport */
	Inport(0, 1),

	/** Outport */
	Outport(1, 0),

	/** Switch block. */
	Switch(3, 1),

	/** Relational operator block. */
	RelationalOperator(2, 1),

	/** Lookup2D block. */
	Lookup2D(2, 1),

	/** Variable transport delay block. */
	VariableTransportDelay(2, 1),

	/** Dot product. */
	DotProduct(2, 1);

	/** Number of inports. */
	/* package */final int numInPorts;

	/** Number of outports. */
	/* package */final int numOutPorts;

	/**
	 * Create new block type.
	 * 
	 * @param numInports
	 *            number of inports
	 * @param numOutports
	 *            number of outports
	 */
	private EBlockType(int numInports, int numOutports) {
		numInPorts = numInports;
		numOutPorts = numOutports;
	}
}