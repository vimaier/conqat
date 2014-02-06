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
package org.conqat.engine.resource.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1AC450EB4CE8F12407407F36631C261D
 */
@AConQATProcessor(description = "This processor reads all text elements from "
		+ "the input and returns an array of all lines contained in the elements. "
		+ "The order of the elements included in the result is specified by the uniform paths.")
public class TextElementReader extends ConQATProcessorBase {

	/** Root element. */
	private ITextResource root;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
	public void setRoot(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ITextResource root) {
		this.root = root;
	}

	/** {@inheritDoc} */
	@Override
	public String[] process() throws ConQATException {
		ArrayList<String> allLines = new ArrayList<String>();
		List<ITextElement> elements = ResourceTraversalUtils
				.listTextElements(root);

		Collections.sort(elements, new ElementUniformPathComparator());
		for (ITextElement element : elements) {
			allLines.addAll(Arrays.asList(TextElementUtils.getLines(element)));
		}
		return CollectionUtils.toArray(allLines, String.class);
	}
}