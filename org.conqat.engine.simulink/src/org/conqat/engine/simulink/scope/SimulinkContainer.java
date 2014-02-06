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
package org.conqat.engine.simulink.scope;

import org.conqat.engine.resource.base.ContainerBase;

import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Container for {@link ISimulinkResource}s.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 192579CA3FF9DEAE5180BEE8D873667F
 */
public class SimulinkContainer extends ContainerBase<ISimulinkResource>
		implements ISimulinkResource {

	/** Constructor. */
	public SimulinkContainer(String name) {
		super(name);
	}

	/** Copy constructor. */
	public SimulinkContainer(ContainerBase<ISimulinkResource> other)
			throws DeepCloneException {
		super(other);
	}

	/** {@inheritDoc} */
	@Override
	protected ISimulinkResource[] allocateArray(int size) {
		return new ISimulinkResource[size];
	}

	/** {@inheritDoc} */
	@Override
	public SimulinkContainer deepClone() throws DeepCloneException {
		return new SimulinkContainer(this);
	}
}