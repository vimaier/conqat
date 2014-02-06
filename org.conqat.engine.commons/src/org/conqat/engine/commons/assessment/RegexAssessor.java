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
package org.conqat.engine.commons.assessment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * This processor checks if a string value matches a given set of regular
 * expressions.
 * 
 * @author Florian Deissenboeck
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: E620F2DB320C7CD27A045EA95A5AE2B1
 */
@AConQATProcessor(description = "This processor creates an assessment based on a "
		+ "string value stored in a key. For this value patterns with assigned "
		+ "colors can be specified. The resulting assessment for a node is the color "
		+ "of the first pattern which is found in the string or a default color if no "
		+ "pattern matched. Note that not the entire string has to match, but just a "
		+ "substring. To match the entire string, use the ^ and $ regex operators. "
		+ "Default is to assess all nodes.")
public class RegexAssessor extends LocalAssessorBase<Object> {

	/** The default color. */
	private ETrafficLightColor defaultColor = ETrafficLightColor.RED;

	/** The patters. */
	private final List<PatternColorPair> patterns = new ArrayList<PatternColorPair>();

	/** Add pattern. */
	@AConQATParameter(name = "regex", minOccurrences = 1, description = "Add a regular expression pattern and its color. "
			+ "See description of class java.util.regex.Pattern for details on regex format.")
	public void addPattern(
			@AConQATAttribute(name = "pattern", description = ConQATParamDoc.REGEX_PATTERN_DESC) String pattern,
			@AConQATAttribute(name = "color", description = "The color assigned.") ETrafficLightColor color)
			throws ConQATException {

		patterns.add(new PatternColorPair(pattern, color));
	}

	/** Set the color used if no pattern matches. */
	@AConQATParameter(name = "default", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Set the assessment color returned if no pattern is found in the value. Default is RED.")
	public void setDefaultColor(
			@AConQATAttribute(name = "color", description = "traffic light color") ETrafficLightColor color) {
		defaultColor = color;
	}

	/** {@inheritDoc} */
	@Override
	protected Assessment assessValue(Object value) {
		if (value != null) {
			String string = value.toString();
			for (PatternColorPair patternColorPair : patterns) {
				if (patternColorPair.contains(string)) {
					return new Assessment(patternColorPair.color);
				}
			}
		}
		return new Assessment(defaultColor);
	}

	/** Local class for storing (regex, color) pairs. */
	private static class PatternColorPair {

		/** The pattern being applied. */
		private final Pattern pattern;

		/** The color relevant for this pattern. */
		public final ETrafficLightColor color;

		/** Constructor. */
		public PatternColorPair(String patternString, ETrafficLightColor color)
				throws ConQATException {
			pattern = CommonUtils.compilePattern(patternString);
			this.color = color;
		}

		/** Determine if the regex is contained in the string. */
		public boolean contains(String string) {
			return pattern.matcher(string).find();
		}
	}
}