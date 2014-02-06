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
package org.conqat.engine.core.driver.specification;

import org.conqat.engine.core.driver.error.IErrorLocatable;
import org.conqat.engine.core.driver.util.IDocumented;
import org.conqat.engine.core.driver.util.Multiplicity;

/**
 * A parameter of a specification. This consists of a name and the allowed
 * multiplicity as well as a list of contained attributes.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 491C13A699C1DDC2FAACCCCD983403E0
 */
public interface ISpecificationParameter extends IDocumented, IErrorLocatable {

	/** Returns the name of this parameter. */
	String getName();

	/** Returns the multiplicity of this parameter. */
	Multiplicity getMultiplicity();

	/**
	 * Returns all attributes of this parameter in order. We use an array as we
	 * want to use covanriance on the return value, and covariance is not
	 * supported for generic collections.
	 */
	SpecificationAttribute[] getAttributes();

	/**
	 * Returns whether this is a synthetic parameter. Synthetic parameters
	 * should be kept internal and not exposed to the user (e.g. by ConQATDoc).
	 */
	boolean isSynthetic();
}