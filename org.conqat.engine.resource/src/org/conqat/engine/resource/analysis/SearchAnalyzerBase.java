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
package org.conqat.engine.resource.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;

/**
 * Base class for processors that perform text-based search in elements.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D5E38CEDA5B24C1E74EBE095EE5EDA3E
 */
public abstract class SearchAnalyzerBase<R extends ITextResource, E extends ITextElement>
		extends ElementAnalyzerBase<R, E> {

	/** Key used to store number of occurrences. */
	@AConQATKey(description = "Key for number of occurrences", type = "java.lang.Integer")
	public static final String KEY_OCCURRENCES_COUNT = "# Occurrences";

	/** Key used to store occurrences. */
	@AConQATKey(description = "Key for number of occurrences", type = "java.util.List<java.lang.String>")
	public static final String KEY_OCCURRENCES = "Occurrences";

	/** Write key for occurrences count. */
	private String occurrencesWriteKey = KEY_OCCURRENCES;

	/** Write key for occurrences list. */
	private String occurrencesCountWriteKey = KEY_OCCURRENCES_COUNT;

	/** List of search patterns. */
	protected PatternList patternList;

	/** Add a search pattern. */
	@AConQATParameter(name = "search", minOccurrences = 1, maxOccurrences = 1, description = "Adds a search pattern.")
	public void setPattern(
			@AConQATAttribute(name = "patterns", description = "The list of pattern to be searched.") PatternList patternList) {
		this.patternList = patternList;
	}

	/** Set the keys used for writing. */
	@AConQATParameter(name = "write-keys", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The keys to write results into.")
	public void setWriteKey(
			@AConQATAttribute(name = "count", description = "Key for occurrence count. Default is '"
					+ KEY_OCCURRENCES_COUNT + "'.") String countKey,
			@AConQATAttribute(name = "list", description = "Key for occurrence list. Default is '"
					+ KEY_OCCURRENCES + "'.") String listKey) {
		occurrencesCountWriteKey = countKey;
		occurrencesWriteKey = listKey;
	}

	/** Determines the matches of the patterns. */
	protected List<String> matchPatterns(String content) {
		List<String> matches = new ArrayList<String>();
		for (Pattern pattern : patternList) {
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				matches.add(matcher.group());
			}
		}
		return matches;
	}

	/**
	 * Determines content via {@link #getText(ITextElement)} and counts
	 * occurrences of the pattern.
	 */
	@Override
	protected void analyzeElement(E element) {
		try {
			String content = getText(element);
			List<String> occurrences = matchPatterns(content);
			element.setValue(occurrencesCountWriteKey, occurrences.size());
			element.setValue(occurrencesWriteKey, occurrences);
		} catch (ConQATException e) {
			getLogger().warn("Could not count for " + element.getId(), e);
			element.setValue(occurrencesCountWriteKey, 0);
		}

	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { occurrencesCountWriteKey, occurrencesWriteKey };
	}

	/** Returns the contents of the element. */
	protected abstract String getText(E element) throws ConQATException;
}