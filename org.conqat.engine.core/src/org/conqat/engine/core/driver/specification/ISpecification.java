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

/**
 * Interface for specifications. Specifications contain detail information on
 * the type of blocks and processors (such as parameters and outputs and their
 * types), but also on the implementation.
 * <p>
 * For processors this information is taken from the class file via reflection,
 * for blocks it partially comes from the XML file and is partially created
 * during the compilation stage (e.g., using type inference for parameters and
 * outputs).
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 41CFE768D73C81AC16D974E0AA2A9CCF
 */
public interface ISpecification extends IDocumented, IErrorLocatable {

	/** Returns the name of this specification. */
	public String getName();

	/** Returns the named parameter of this specification. */
	public ISpecificationParameter getParameter(String name);

	/**
	 * Returns all parameters of this specification. Uses an array as return
	 * type to allow for covariant return type.
	 */
	public ISpecificationParameter[] getParameters();

	/**
	 * Returns all non-synthetic parameters of this specification. Uses an array
	 * as return type to allow for covariant return type.
	 */
	public ISpecificationParameter[] getNonSyntheticParameters();

	/**
	 * Returns all outputs of this specification. The return value is an array
	 * to allow for the usage of covariance, since covariance is not supported
	 * for generic collections.
	 */
	public SpecificationOutput[] getOutputs();
}