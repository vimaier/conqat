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

import org.conqat.engine.resource.text.ITextElement;

import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.simulink.model.SimulinkModel;

/**
 * Interface for Simulink element.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: DC77E7E652F569DC9CC880FD31D0ADC1
 */
public interface ISimulinkElement extends ISimulinkResource, ITextElement {

	/** {@inheritDoc} */
	public ISimulinkElement[] getChildren();

	/** {@inheritDoc} */
	public ISimulinkElement deepClone() throws DeepCloneException;

	/** Obtain the simulink model stored in this element. */
	public SimulinkModel getModel();
}