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
package org.conqat.engine.code_clones.core.constraint;

import org.conqat.engine.commons.ConQATProcessorBase;

/**
 * Base class for constraints. Instead of implementing a factory processor for
 * each single constraint, constraints are ConQAT processors that return a
 * reference to themselves.
 * 
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 41972 $
 * @ConQAT.Rating GREEN Hash: 5DF393249767E457C42FCCCB50C2EE82
 */
public abstract class ConstraintBase extends ConQATProcessorBase implements
		ICloneClassConstraint {

	/** {@inheritDoc} */
	@Override
	public ICloneClassConstraint process() {
		setup();

		return this;
	}

	/**
	 * Template method that deriving classes can override to perform
	 * initialization
	 */
	protected void setup() {
		// Empty default implementation
	}

}