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
package org.conqat.engine.simulink.clones.preprocess;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * Base class for {@link ISimulinkPreprocessor}s.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36729 $
 * @ConQAT.Rating GREEN Hash: 2BACCD78CA981F12D0DCB71FC9ED45B0
 */
public abstract class SimulinkPreprocessorBase extends ConQATProcessorBase
		implements ISimulinkPreprocessor {

	/** {@inheritDoc} */
	@Override
	public ISimulinkPreprocessor process() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns <code>this</code>
	 */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}
}