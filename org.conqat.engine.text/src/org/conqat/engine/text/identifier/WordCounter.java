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

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.CounterSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38919 $
 * @ConQAT.Rating GREEN Hash: BFE38C8787FCFB180CD811621ABDE2CA
 */
@AConQATProcessor(description = "This processor extracts a CounterSet of String "
		+ "containing the frequency of words extracted from a set of identifiers.")
public class WordCounter extends
		WordProcessorBase<CounterSet<String>> {

	/** CounterSet of words. */
	private final CounterSet<String> wordFrequencies = new CounterSet<String>();

	/** {@inheritDoc} */
	@Override
	protected CounterSet<String> obtainResult() {
		return wordFrequencies;
	}

	/** {@inheritDoc} */
	@Override
	protected void processWord(String word) {
		wordFrequencies.inc(word);
	}
}