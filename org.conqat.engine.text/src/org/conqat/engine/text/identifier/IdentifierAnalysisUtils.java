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
import java.util.Collections;
import java.util.List;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility methods related to identifier analysis.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38924 $
 * @ConQAT.Rating GREEN Hash: 5DD154B710031E3D4D0947EF8332A894
 */
public class IdentifierAnalysisUtils {

	/**
	 * Breaks the given identifier into words and performs stemming with an
	 * English word stemmer.
	 */
	public static List<String> breakAndStemEN(String identifier) {
		return breakAndStemEN(identifier, false);
	}

	/**
	 * Breaks the given identifier into words and performs stemming with an
	 * English word stemmer. The parameter filterStopWords denotes whether
	 * English stop words as defined in {@link EStopWords} shall be filtered
	 * out. Identifier parts consisting of a single char are discarded. However,
	 * if the given identifier is a single char it is considered. Please not
	 * that the resulting list may be empty.
	 */
	public static List<String> breakAndStemEN(String identifier,
			boolean filterStopWords) {

		CCSMAssert.isFalse(StringUtils.isEmpty(identifier),
				"identifier must not be empty");

		List<String> parts = null;
		if (identifier.length() == 1) {
			// compound breaker discards single-char identifiers
			parts = Collections.singletonList(identifier);
		} else {
			parts = CompoundBreaker.breakCompound(identifier);
		}

		List<String> terms = new ArrayList<String>();
		for (String part : parts) {
			if (filterStopWords && EStopWords.ENGLISH.isStopWord(part)) {
				continue;
			}
			terms.add(EStemmer.ENGLISH.stem(part));
		}
		return terms;
	}
}