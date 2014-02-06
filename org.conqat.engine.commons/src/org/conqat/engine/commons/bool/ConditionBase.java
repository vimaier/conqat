/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.commons.bool;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for condition processors.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37404 $
 * @ConQAT.Rating GREEN Hash: 0F0AB8C8DC7B0D44D89A7963A0A3DD55
 */
public abstract class ConditionBase extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "invert", attribute = "value", description = "If set to true, result is inverted", optional = true)
	public boolean invert = false;

	/** {@inheritDoc} */
	@Override
	public Boolean process() throws ConQATException {
		boolean result = evaluateCondition();
		if (invert) {
			result = !result;
		}
		return result;
	}

	/** Template method for evaluating the condition. */
	protected abstract boolean evaluateCondition() throws ConQATException;
}
