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

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 28FCE4789A62CE9ABEC6655897379649
 */
@AConQATProcessor(description = "This processor creates a set of "
		+ "stemmed words from a set of inputs words. Different languages are supported for stemming.")
public class WordStemmer extends ConQATProcessorBase {

	/** Set of original words. */
	private Set<String> words;

	/** Set of stemmed words. */
	private final Set<String> stemmedWords = new HashSet<String>();

	/** The stemmer used. */
	private EStemmer stemmer = EStemmer.ENGLISH;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "words", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Defines the set of words being stemmed.")
	public void setList(
			@AConQATAttribute(name = "set", description = "The set of input words (strings).") Set<String> list) {
		words = list;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "stemmer", maxOccurrences = 1, description = "Defines the stemmer used. The default language is english.")
	public void setStemmer(
			@AConQATAttribute(name = "language", description = "The language used for stemming.") EStemmer stemmer) {
		this.stemmer = stemmer;
	}

	/** {@inheritDoc} */
	@Override
	public Set<String> process() {
		for (String word : words) {
			stemmedWords.add(stemmer.stem(word));
		}
		return stemmedWords;
	}
}