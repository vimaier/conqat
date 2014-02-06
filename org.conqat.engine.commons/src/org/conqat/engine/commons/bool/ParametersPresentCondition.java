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
package org.conqat.engine.commons.bool;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37404 $
 * @ConQAT.Rating GREEN Hash: F2B68675480C0A8AA82382B51785DFA4
 */
@AConQATProcessor(description = "Returns true if at least one parameter is present. "
		+ "This can be used to check for optional parameters.")
public class ParametersPresentCondition extends ConditionBase {

	/** The result. */
	private boolean result = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "input", description = "The boolean input.")
	public void addValue(
			@SuppressWarnings("unused") @AConQATAttribute(name = "value", description = "true or false") Object value) {
		result = true;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean evaluateCondition() {
		return result;
	}
}