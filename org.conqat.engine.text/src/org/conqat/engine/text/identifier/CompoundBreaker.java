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
package org.conqat.engine.text.identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.lib.commons.string.StringUtils;

/**
 * This class breaks compound words in their components. It knows how to deal
 * with words separated with non-alphabetics (such as underscores, whitespace)
 * and camel cased words. The algorithm removes trailing digits from the
 * components and ignores components of length one.
 * 
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 41163D6B1E774255A8B0FB6B576DF373
 */
public class CompoundBreaker {

	/**
	 * Matches all characters that should be treated as word separators, i.e.
	 * sequences of non-alphanumeric characters (whitespace, control characters,
	 * etc.).
	 */
	private final static Pattern WORD_SEPARATION_PATTERN = Pattern
			.compile("\\P{Alnum}+");

	/**
	 * Matches any character that is not an upper case letter followed by an
	 * upper case letter.
	 */
	private final static Pattern LOWER_UPPER_PATTERN = Pattern
			.compile("(\\P{Lu})(\\p{Lu})");

	/**
	 * Matches acronyms like compounds beginning with an acronym like XMLReader.
	 */
	private final static Pattern ACRONYM = Pattern
			.compile("(\\p{Lu}+)(\\p{Lu}[\\P{Lu}&&[^_]])");

	/** Matches digits at the of a string. */
	private final static Pattern END_DIGITS_PATTERN = Pattern.compile("\\d+$");

	/** The underscore */
	private final static String UNDERSCORE = "_";

	/**
	 * Break compound word and return the components of the compound. Parts of
	 * length 1 are discarded.
	 */
	public static List<String> breakCompound(String compound) {
		List<String> result = new ArrayList<String>();

		Matcher matcher = WORD_SEPARATION_PATTERN.matcher(compound);
		compound = matcher.replaceAll(UNDERSCORE);

		matcher = LOWER_UPPER_PATTERN.matcher(compound);
		compound = matcher.replaceAll("$1" + UNDERSCORE + "$2");

		matcher = ACRONYM.matcher(compound);
		compound = matcher.replaceAll("$1" + UNDERSCORE + "$2");

		for (String part : compound.split(UNDERSCORE)) {
			part = part.toLowerCase();

			matcher = END_DIGITS_PATTERN.matcher(part);
			part = matcher.replaceFirst(StringUtils.EMPTY_STRING);

			if (part.length() >= 2) {
				result.add(part);
			}
		}
		return result;
	}
}