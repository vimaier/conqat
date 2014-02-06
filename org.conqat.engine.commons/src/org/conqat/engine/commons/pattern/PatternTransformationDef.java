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
package org.conqat.engine.commons.pattern;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.string.RegexReplacement;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 44532 $
 * @ConQAT.Rating YELLOW Hash: 140E78E1E22A7A9EA7DA79094910102C
 */
@AConQATProcessor(description = "Defines a pattern transformation list.")
public class PatternTransformationDef extends ConQATProcessorBase {

	/** Underlying pattern transformation list. */
	private final PatternTransformationList patternTransformationList = new PatternTransformationList();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "pattern", description = "Definition of a pattern.")
	public void addPattern(
			@AConQATAttribute(name = "regex", description = ConQATParamDoc.REGEX_PATTERN_DESC) String regex,
			@AConQATAttribute(name = "replacement", description = ""
					+ "The replacement string.") String replacement)
			throws ConQATException {
		try {
			patternTransformationList.add(new RegexReplacement(regex,
					replacement));
		} catch (PatternSyntaxException e) {
			throw CommonUtils.wrapPatternSyntaxException(e);
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "prefix-transform", description = "Defines a pattern that replaces a prefix with another one.")
	public void addPrefixPattern(
			@AConQATAttribute(name = "prefix", description = "The prefix string; all characters are treated as plain text (no regex interpretation).") String prefix,
			@AConQATAttribute(name = "replacement", description = ""
					+ "The replacement string.") String replacement)
			throws ConQATException {
		addPattern("^" + Pattern.quote(prefix), replacement);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "equals-transform", description = "Defines a pattern that replaces the string with another one if it is equal to the comparee. ")
	public void addEqualsPattern(
			@AConQATAttribute(name = "equals", description = "The prefix string; all characters are treated as plain text (no regex interpretation).") String prefix,
			@AConQATAttribute(name = "replacement", description = ""
					+ "The replacement string.") String replacement,
			@AConQATAttribute(name = "case-sensitive", defaultValue = "true", description = "Case sensitive (default: true)") boolean caseSensitive)
			throws ConQATException {
		String regex = "^" + Pattern.quote(prefix) + "$";
		if (!caseSensitive) {
			regex = "(?i)" + regex;
		}
		addPattern(regex, replacement);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "pattern-transformation-list", description = ""
			+ "Takes all entries of a pattern transformation list.")
	public void addPatternList(
			@AConQATAttribute(name = "list", description = "The referenced pattern transformation list.") PatternTransformationList list) {
		patternTransformationList.addAll(list);
	}

	/** {@inheritDoc} */
	@Override
	public PatternTransformationList process() {
		return patternTransformationList;
	}
}