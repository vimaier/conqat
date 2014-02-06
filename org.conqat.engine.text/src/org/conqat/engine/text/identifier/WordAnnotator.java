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
package org.conqat.engine.text.identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementProcessorBase;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43282 $
 * @ConQAT.Rating GREEN Hash: D2A850FCA2FE1CF39AB6149D7C05BCD9
 */
@AConQATProcessor(description = "Annotates each element with the set of words taken from the identifiers."
		+ "The words are annotated as a Set (key 'words') as well as a CounterSet (key 'word-count')")
public class WordAnnotator extends TokenElementProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The set of words", type = "java.util.Set<String>")
	public static final String KEY = "words";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The CounterSet of words", type = "org.conqat.commons.lib.CounterSet<String>")
	public static final String COUNT_KEY = "word-count";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "stemmer", attribute = "value", description = ""
			+ "Stemmer to be used. Default is none.", optional = true)
	public EStemmer stemmer = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "stop-words", attribute = "filter", description = ""
			+ "Stop words to filter out. Default is none.", optional = true)
	public EStopWords stopWords = null;

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) throws ConQATException {
		super.setUp(root);
		NodeUtils.addToDisplayList(root, KEY);
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITokenElement element) throws ConQATException {
		Set<String> identifiers = new HashSet<String>();
		for (IToken token : element.getTokens(getLogger())) {
			if (token.getType().getTokenClass() == ETokenClass.IDENTIFIER) {
				identifiers.add(token.getText());
			}
		}

		Set<String> elementWords = new HashSet<String>();
		CounterSet<String> wordCount = new CounterSet<String>();
		for (String identifier : identifiers) {
			List<String> words = CompoundBreaker.breakCompound(identifier);
			for (String word : words) {
				if (stopWords != null && stopWords.isStopWord(word)) {
					continue;
				}
				if (stemmer != null) {
					word = stemmer.stem(word);
				}
				elementWords.add(word);
				wordCount.inc(word);
			}
		}
		element.setValue(KEY, elementWords);
		element.setValue(COUNT_KEY, wordCount);
	}

}
