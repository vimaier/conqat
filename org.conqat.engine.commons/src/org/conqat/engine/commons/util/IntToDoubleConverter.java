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
package org.conqat.engine.commons.util;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 39835 $
 * @ConQAT.Rating GREEN Hash: 5A4809923187CF3FD1FB51FC71C5C15A
 */
@AConQATProcessor(description = "Converts the int input to a double value. "
		+ "This processor fails if the input is null. ")
public class IntToDoubleConverter extends ConQATInputProcessorBase<Number> {

	/** {@inheritDoc} */
	@Override
	public Double process() throws ConQATException {
		if (input == null) {
			throw new ConQATException(
					"Null value encountered but int value expected.");
		}
		return input.doubleValue();
	}
}
