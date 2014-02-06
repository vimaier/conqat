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
package org.conqat.engine.commons.string;

import java.util.ArrayList;

import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Processor for string concatenation.
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 39FF7B5518BFE95D6B665786B6D46CA0
 */
@AConQATProcessor(description = "Processor for string concatenation. "
		+ "Individual parts of the string may be separated by separator.")
public class Concatenator extends ConQATProcessorBase {

	/** Parts of the string. */
	private final ArrayList<String> parts = new ArrayList<String>();

	/** The separator string. */
	private String separator = StringUtils.EMPTY_STRING;

	/** Add part. */
	@AConQATParameter(name = "part", minOccurrences = 1, description = "Add part of the string.")
	public void addPart(
			@AConQATAttribute(name = "string", description = "String part") Object part) {

		if (part == null) {
			parts.add("null");
		} else {
			parts.add(part.toString());
		}
	}

	/** Set separator. */
	@AConQATParameter(name = "separator", maxOccurrences = 1, description = "Define separator.")
	public void setSeparator(
			@AConQATAttribute(name = "string", description = "Separator string [empty string].") String separator) {

		this.separator = separator;
	}

	/** Concatenate parts */
	@Override
	public String process() {
		return StringUtils.concat(parts, separator);
	}
}