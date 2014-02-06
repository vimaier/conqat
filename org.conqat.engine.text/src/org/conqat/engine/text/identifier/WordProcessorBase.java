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

import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;

/**
 * Base class for processors working on words.
 * 
 * @param <T>
 *            the return type of this processor.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38919 $
 * @ConQAT.Rating GREEN Hash: F05B66EFEADD9FCDFF2120F5AB121347
 */
public abstract class WordProcessorBase<T> extends ConQATProcessorBase {

	/** Set of identifiers. */
	private Set<String> identifiers;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "stemmer", attribute = "value", description = ""
			+ "Stemmer to be used. Default is none.", optional = true)
	public EStemmer stemmer = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "stop-words", attribute = "filter", description = ""
			+ "Stop words to filter out. Default is none.", optional = true)
	public EStopWords stopWords = null;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "identifiers", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The set of identifiers which are broken up into words.")
	public void setList(@AConQATAttribute(name = "set", description = ""
			+ "The set of identifiers (strings).") Set<String> identifiers) {
		this.identifiers = identifiers;
	}

	/** {@inheritDoc} */
	@Override
	public T process() {
		for (String identifier : identifiers) {
			List<String> words = CompoundBreaker.breakCompound(identifier);
			for (String word : words) {
				if (stopWords != null && stopWords.isStopWord(word)) {
					continue;
				}
				if (stemmer != null) {
					word = stemmer.stem(word);
				}
				processWord(word);
			}
		}
		return obtainResult();
	}

	/** Template method that calculates and returns the processor's result. */
	protected abstract T obtainResult();

	/** Template method that is called for each word. */
	protected abstract void processWord(String word);
}