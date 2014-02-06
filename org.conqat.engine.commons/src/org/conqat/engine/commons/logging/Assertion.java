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
package org.conqat.engine.commons.logging;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37517 $
 * @ConQAT.Rating GREEN Hash: 17F28706E2E956942DDA7521CB07749D
 */
@AConQATProcessor(description = "This processor fails with an exception if the provided condition is false. "
		+ "This can be used for runtime checks in blocks. The result just forwards the condition.")
public class Assertion extends ConQATProcessorBase {

	/** The parts of the message. */
	private final List<Object> messageParts = new ArrayList<Object>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "condition", attribute = "value", description = "The condition that is expected to be true.")
	public boolean condition;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "message", minOccurrences = 1, description = "Adds a part of the message. Individual parts are joined by whitespace")
	public void addMessagePart(
			@AConQATAttribute(name = "text", description = "Message text.") String text) {
		messageParts.add(text);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "value", description = "Adds a value as part of the message. Individual parts are joined by whitespace")
	public void addMessageObject(
			@AConQATAttribute(name = "ref", description = "Reference to the object to be included in the message.") Object value) {
		messageParts.add(value);
	}

	/** {@inheritDoc} */
	@Override
	public Boolean process() throws ConQATException {
		if (!condition) {
			throw new ConQATException(StringUtils.concat(messageParts));
		}
		return condition;
	}
}
