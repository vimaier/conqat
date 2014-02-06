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
package org.conqat.lib.simulink.model.stateflow;

import org.conqat.lib.commons.collections.UnmodifiableSet;

/**
 * This interface describes entities that contain {@link StateflowNodeBase}s.
 * 
 * @param
 * <P>
 * Type of the parent of this node.
 * 
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 27FF40F437F7DC20D2DA9CF0AE05B294
 */
public interface IStateflowNodeContainer<P extends IStateflowElement<?>>
		extends IStateflowElement<P> {

	/** Add Stateflow node to this containter. */
	public void addNode(StateflowNodeBase node);

	/** Get nodes held by this container. */
	public UnmodifiableSet<StateflowNodeBase> getNodes();
}