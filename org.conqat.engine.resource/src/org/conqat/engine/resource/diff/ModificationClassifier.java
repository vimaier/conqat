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
package org.conqat.engine.resource.diff;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.base.ElementTraversingProcessorBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8F29E3027D9E488A47BCE2835546F71F
 */
@AConQATProcessor(description = "For each element this processor stores one of the values '"
		+ ModificationClassifier.VALUES
		+ "' to reflect its change since the baseline. "
		+ "Information is provided by the ScopeDiffer processor (thus the ScopeDiffer must be run before).")
public class ModificationClassifier extends
		ElementTraversingProcessorBase<ITextResource, ITextElement> {

	/** Value for unmodified elements. */
	public static final String CLASS_UNMODIFIED = "Unmodified";

	/** Value for modified elements. */
	public static final String CLASS_MODIFIED = "Modified";

	/** Value for new elements. */
	public static final String CLASS_NEW = "New";

	/** Doc string */
	public static final String VALUES = CLASS_UNMODIFIED + ", "
			+ CLASS_MODIFIED + ", " + CLASS_NEW;

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "This stores the classification. Values are: "
			+ VALUES, type = "java.lang.String")
	public static final String KEY = "Modification";

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITextResource root) {
		NodeUtils.addToDisplayList(root, KEY);
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITextElement element) throws ConQATException {
		element.setValue(KEY, determineClass(element));
	}

	/** Checks if an element is modified. */
	private String determineClass(ITextElement element) throws ConQATException {
		boolean newFile = NodeUtils.getBooleanValue(element,
				ScopeDiffer.KEY_NEW);

		if (newFile) {
			return CLASS_NEW;
		}

		double churnLines = NodeUtils.getDoubleValue(element,
				ScopeDiffer.KEY_CHURN_LINES);

		if (churnLines > 0) {
			return CLASS_MODIFIED;
		}

		return CLASS_UNMODIFIED;
	}
}
