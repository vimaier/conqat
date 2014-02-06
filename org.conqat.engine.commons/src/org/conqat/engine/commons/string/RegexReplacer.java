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

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Processor for regex-based replacement on strings.
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: ADB7F9F126DF59C49F5B1BCF9AE3D7C2
 */
@AConQATProcessor(description = "Processor for regex-based replacement on strings.")
public class RegexReplacer extends ConQATProcessorBase {

	/** String to work on. */
	private String string;

	/** List of regexes. */
	private PatternTransformationList regexes;

	/** Add part. */
	@AConQATParameter(name = "string", minOccurrences = 1, description = "String")
	public void setString(
			@AConQATAttribute(name = "string", description = "String")
			String string) {

		this.string = string;
	}

	/** Set regex replacements. */
	@AConQATParameter(name = "regex", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "List of regexe replacements.")
	public void setAbbreviation(
			@AConQATAttribute(name = "list", description = "Transformation list")
			PatternTransformationList list) {

		regexes = list;
	}

	/** Concatenate parts */
	@Override
	public String process() {
		return regexes.applyTransformation(string);
	}
}