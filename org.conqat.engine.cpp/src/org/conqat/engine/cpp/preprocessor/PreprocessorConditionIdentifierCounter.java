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
package org.conqat.engine.cpp.preprocessor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: feilkas $
 * @version $Rev: 41746 $
 * @ConQAT.Rating GREEN Hash: 5CB4052E263C3BD7CB6CA1B1CC5CE35C
 */
@AConQATProcessor(description = "Counts the frequency of identifiers in #if and #elif conditions.")
public class PreprocessorConditionIdentifierCounter extends
		ConQATInputProcessorBase<ITokenResource> {

	/** Pattern used to find identifiers. */
	private static final Pattern IDENTIFIER_PATTERN = Pattern
			.compile("[a-zA-Z_][a-zA-Z0-9_]*");

	/**
	 * The inspected preprocessor directives. This explicitly does not include
	 * #ifndef.
	 */
	private static final Set<String> INSPECTED_DIRECTIVES = new HashSet<String>(
			Arrays.asList("if", "elif", "ifdef"));

	/** Identifiers that are skipped during analysis. */
	private static final Set<String> IGNORED_IDENTIFIERS = new HashSet<String>(
			Arrays.asList("defined", "sizeof"));

	/** Counts the identifiers in preprocessor conditionals. */
	private CounterSet<String> identifiers = new CounterSet<String>();

	/** {@inheritDoc} */
	@Override
	public CounterSet<String> process() throws ConQATException {
		for (ITokenElement element : ResourceTraversalUtils.listElements(input,
				ITokenElement.class)) {
			for (IToken token : element.getTokens(getLogger())) {
				if (token.getType() == ETokenType.PREPROCESSOR_DIRECTIVE) {
					countIdentifiers(token.getText());
				}
			}
		}

		return identifiers;
	}

	/** Inserts the identifiers of the token into {@link #identifiers}. */
	private void countIdentifiers(String text) {
		Matcher matcher = IDENTIFIER_PATTERN.matcher(text);
		if (!matcher.find()) {
			return;
		}

		String first = matcher.group().toLowerCase();
		if (!INSPECTED_DIRECTIVES.contains(first)) {
			return;
		}

		while (matcher.find()) {
			String identifier = matcher.group();
			if (!IGNORED_IDENTIFIERS.contains(identifier)) {
				identifiers.inc(identifier);
			}
		}
	}
}
