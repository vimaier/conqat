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
package org.conqat.engine.code_clones.result.annotation;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Revision: 34670 $
 * @ConQAT.Rating GREEN Hash: 53D576DE7B1739C35354BB87C246D36A
 */
@AConQATProcessor(description = "Annotates an element with the number of clones it contains.")
public class CloneCountAnnotator extends CloneAnnotatorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key that stores Clone Count value", type = "java.lang.Integer")
	public final static String CLONE_COUNT_KEY = "Clone Count";

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { CLONE_COUNT_KEY };
	}

	/** Annotates Clone Count metric at element */
	@Override
	protected void annotateClones(ITextElement element,
			UnmodifiableList<Clone> clonesList) {
		element.setValue(CLONE_COUNT_KEY, clonesList.size());
	}
}