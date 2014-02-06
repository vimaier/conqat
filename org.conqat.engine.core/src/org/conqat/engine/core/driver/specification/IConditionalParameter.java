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

/**
 * The interface is used to mark the synthetic parameter for conditional
 * execution. This class also hold the constants for the names.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 6988B2178DBAAB7DDBDDF25C04D25E69
 */
public interface IConditionalParameter {

	/**
	 * The name of the parameter. This includes a character not normally allowed
	 * in parameter names (the '*') to avoid possible collisions with
	 * user-defined parameters.
	 */
	public static final String PARAMETER_NAME = "*condition";

	/** The name of the attribute used for enablement. */
	public static final String VALUE_ATTRIBUTE = "enabled";

	/** The name of the attribute used for inversion. */
	public static final String INVERT_ATTRIBUTE = "invert";

}