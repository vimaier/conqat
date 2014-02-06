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

import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.APipelineSource;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for switch constructs.
 * 
 * @param <T>
 *            the type used for switching.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 37540 $
 * @ConQAT.Rating GREEN Hash: 895BB2D75E571A713B5C5880769B6AD9
 */
public class SwitchBase<T> extends ConQATProcessorBase {

	/** Documentation used for switch processors. */
	public static final String DOC = "Data-flow switch that picks a value based on a switch value and a number of labeled input cases.";

	/** The default value. */
	private Object defaultValue = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "switch", attribute = "value", description = "The value that selects the element chosen.")
	public T switchValue;

	/** The map storing the possible choices. */
	private final Map<T, Object> values = new HashMap<T, Object>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "case", minOccurrences = 1, description = ""
			+ "Adds a case label. ")
	public void addCase(
			@AConQATAttribute(name = "label", description = "The label for the case entry.") T label,
			@APipelineSource @AConQATAttribute(name = "value", description = "The value to be used if the switch value matches the label.") Object value)
			throws ConQATException {
		if (value == null) {
			throw new ConQATException("Null value not allowed (for label "
					+ label + ").");
		}

		if (values.put(label, value) != null) {
			throw new ConQATException("Duplicate case entry for label " + label);
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "default", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Sets a default value to be used if the switch value matches none of the cases. "
			+ "If no default is provided, this situation leads to an exception.")
	public void setDefaultValue(
			@APipelineSource @AConQATAttribute(name = "value", description = "The default value") Object value)
			throws ConQATException {
		if (value == null) {
			throw new ConQATException("Null value not allowed as default!");
		}
		defaultValue = value;
	}

	/** {@inheritDoc} */
	@Override
	public Object process() throws ConQATException {
		Object result = values.get(switchValue);
		if (result != null) {
			return result;
		}

		if (defaultValue == null) {
			throw new ConQATException("No case entry matching value "
					+ switchValue + " found and no default provided!");
		}
		return defaultValue;
	}
}
