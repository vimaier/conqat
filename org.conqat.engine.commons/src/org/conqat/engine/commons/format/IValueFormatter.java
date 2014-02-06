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
package org.conqat.engine.commons.format;

import org.conqat.engine.core.core.ConQATException;

/**
 * A formatter for arbitrary types/values.
 * <p>
 * Implementations of this interface should be immutable.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41317 $
 * @ConQAT.Rating GREEN Hash: 62A6E565DF346706DA74CFE67A6FFA51
 */
public interface IValueFormatter {

	/**
	 * Converts the input value into another format.
	 * 
	 * @throws ConQATException
	 *             if the value is not of a supported type or the conversion
	 *             failed.
	 */
	Object format(Object value) throws ConQATException;
}
