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
package org.conqat.engine.commons.dataflow;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.APipelineSource;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 37508 $
 * @ConQAT.Rating GREEN Hash: 96EB1C61F9EE55F84390CD3FC95BF1DD
 */
@AConQATProcessor(description = "This processor allows to define default values. "
		+ "If the actual value is not set, the default value is returned, otherwise the "
		+ "actual value is returned.")
public class DefaultValue extends Optional {

	/** The value to use if no actual value is set. */
	private Object defaultValue;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "default", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Default value. If actual value is not set, this will be used.")
	public void setDefaultValue(
			@APipelineSource @AConQATAttribute(name = "value", description = "Value") Object value)
			throws ConQATException {
		if (value == null) {
			throw new ConQATException(
					"null value for default value not supported.");
		}
		defaultValue = value;
	}

	/** {@inheritDoc} */
	@Override
	public Object process() {
		if (actualValue == null) {
			return defaultValue;
		}
		return actualValue;
	}
}
