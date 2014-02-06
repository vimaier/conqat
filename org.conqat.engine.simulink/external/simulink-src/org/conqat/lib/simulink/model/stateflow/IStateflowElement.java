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

/**
 * Interface for Stateflow elements.
 * 
 * @param
 * <P>
 * Type of the parent of this node.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 775E009B347B6522BCDEE20F4953B688
 */
public interface IStateflowElement<P extends IStateflowElement<?>> {

	/** Get parent element. */
	public P getParent();

	/**
	 * In the MDL file each Stateflow element is annotated with an Id. This
	 * method returns the Id.
	 */
	public String getStateflowId();

	/** Remove this element from the model */
	public void remove();

}