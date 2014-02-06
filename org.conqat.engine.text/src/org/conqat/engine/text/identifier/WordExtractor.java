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

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7A8FDFCCFE839448CC9840CBFA15A00B
 */
@AConQATProcessor(description = "This processor extracts the words from a "
		+ "set of identifiers and returns the set of words. ")
public class WordExtractor extends WordProcessorBase<Set<String>> {

	/** Set of words. */
	private final Set<String> words = new HashSet<String>();

	/** {@inheritDoc} */
	@Override
	protected Set<String> obtainResult() {
		return words;
	}

	/** {@inheritDoc} */
	@Override
	protected void processWord(String word) {
		words.add(word);
	}
}