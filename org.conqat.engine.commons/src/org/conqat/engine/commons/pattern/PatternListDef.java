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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.config.KeyedConfig;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45115 $
 * @ConQAT.Rating GREEN Hash: ED52AF95187503411E7FAAE9A2FFB224
 */
@AConQATProcessor(description = "Defines a pattern list. "
        + "This processor supports macro expansion for expressions that are directly provided. "
        + "Patterns that are inherited from another pattern list are not expanded. "
        + "The default macros are %{LINE_FEED} %{CARRIAGE_RETURN} %{TAB} %{NEWLINE_UNIX} %{NEWLINE_WINDOWS} %{NEWLINE} %{IDENTIFIER}.")
public class PatternListDef extends ConQATProcessorBase {

	/** Regular expression for identifiers. */
	private static final String IDENTIFIER_REGEX = "[_a-zA-Z][_a-zA-Z0-9]*";

	/** Pattern describing valid macro names. */
	private static final Pattern MACRO_NAME_PATTERN = Pattern
	        .compile(IDENTIFIER_REGEX);

	/** Pattern matching macro applications */
	private static final Pattern MACRO_PATTERN = Pattern.compile("%\\{("
	        + IDENTIFIER_REGEX + ")\\}");

	/** Maps macro names to their replacement. */
	private final Map<String, String> macroTable =
	        new HashMap<String, String>();

	/** Resulting pattern list. */
	private final PatternList patternList = new PatternList();

	/** Constructor inserts default macros. */
	public PatternListDef() {
		try {
			defineMacro("LINE_FEED", "\n");
			defineMacro("CARRIAGE_RETURN", "\r");
			defineMacro("TAB", StringUtils.TAB);
			defineMacro("NEWLINE_UNIX", "%{LINE_FEED}");
			defineMacro("NEWLINE_WINDOWS", "(%{CARRIAGE_RETURN}%{LINE_FEED})");
			defineMacro("NEWLINE", "(%{NEWLINE_UNIX}|%{NEWLINE_WINDOWS})");
			defineMacro("IDENTIFIER", "(" + IDENTIFIER_REGEX + ")");
		} catch (ConQATException e) {
			CCSMAssert.fail("Default macros should not cause problems: "
			        + e.getMessage());
		}
	}

	/** Performs macro expansion on the given string. */
	private String expand(String value) throws ConQATException {
		Matcher matcher = MACRO_PATTERN.matcher(value);

		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			String name = matcher.group(1);
			String replacement = macroTable.get(name);
			if (replacement == null) {
				throw new ConQATException("Unknown macro: " + name);
			}

			matcher.appendReplacement(result, replacement);
		}
		matcher.appendTail(result);

		return result.toString();
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "macro", description = "Definies a macro that can be used in later patterns. The macro can be referenced via %{macro_name}.")
	public void defineMacro(
	        @AConQATAttribute(name = "name", description = "The name of the macro. This must be a valid Java identifier.") String name,
	        @AConQATAttribute(name = "regex", description = ConQATParamDoc.REGEX_PATTERN_DESC
	                + " This may also include references to macros defined in this processor before.") String regex)
	        throws ConQATException {
		if (!MACRO_NAME_PATTERN.matcher(name).matches()) {
			throw new ConQATException("Invalid macro name " + name);
		}

		if (macroTable.put(name, expand(regex)) != null) {
			throw new ConQATException("Duplicate macro definition: " + name);
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "pattern", description = "Definition of a pattern.")
	public void addPattern(
	        @AConQATAttribute(name = "regex", description = ConQATParamDoc.REGEX_PATTERN_DESC) String regex)
	        throws ConQATException {
		expandAndAddRegex(regex);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.PATTERN_LIST, description = ConQATParamDoc.PATTERN_LIST_DESC)
	public void addPatternList(
	        @AConQATAttribute(name = "list", description = "The referenced pattern list.") PatternList list) {
		patternList.addAll(list);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "keyed-config-patterns", description = "Patterns taken from a keyed configuration. For each key that starts with the given prefix, the corresponding value is interpreted as a regular expression and added to the list of patterns.")
	public void addPatternsFromKeyedConfig(
	        @AConQATAttribute(name = "config", description = "The configuration to read patterns from.") KeyedConfig config,
	        @AConQATAttribute(name = "key-prefix", description = "The prefix that identifies key-value pairs to include.") String prefix)
	        throws ConQATException {
		for (String key : config.getKeysWithPrefix(prefix)) {
			expandAndAddRegex(config.get(key));
		}
	}

	/**
	 * Expands the given regular expression and adds it to the list of patterns.
	 */
	private void expandAndAddRegex(String regex) throws ConQATException {
		String expanded = expand(regex);
		try {
			patternList.add(CommonUtils.compilePattern(expanded));
		} catch (ConQATException e) {
			getLogger().info("Expanded RegEx is: " + expanded);
			throw e;
		}
	}

	/** {@inheritDoc} */
	@Override
	public PatternList process() {
		return patternList;
	}
}