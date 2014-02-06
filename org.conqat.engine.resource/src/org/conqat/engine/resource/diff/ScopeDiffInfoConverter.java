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

import java.util.Collection;

import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.util.UniformPathUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 45937 $
 * @ConQAT.Rating YELLOW Hash: 4526C5645951871D641AB8DBA2BCA7D0
 */
@AConQATProcessor(description = "Converts a ScopeDiffInfo into a ConQAT node structure.")
public class ScopeDiffInfoConverter extends
		ConQATInputProcessorBase<ScopeDiffInfo> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Absolute number of elements", type = "java.lang.Integer")
	public static final String KEY_ABS_ELEMENTS = "# Elements";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Relative  number of elements", type = "java.lang.Double")
	public static final String KEY_REL_ELEMENTS = "% Elements";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Elements", type = "java.lang.List<String>")
	public static final String KEY_ELEMENTS = "Elements";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "element-list", attribute = "include", optional = true, description = ""
			+ "Include list of uniform paths of elements in key? Default is false.")
	public boolean includeElementList = false;

	/** {@inheritDoc} */
	@Override
	public ListNode process() {
		ListNode root = new ListNode();
		root.setValue(NodeConstants.HIDE_ROOT, true);

		NodeUtils.addToDisplayList(root, KEY_ABS_ELEMENTS, KEY_REL_ELEMENTS);
		if (includeElementList) {
			NodeUtils.addToDisplayList(root, KEY_ELEMENTS);
		}

		addNode(root, "# Elements Added", input.getAddedElements());
		addNode(root, "# Elements Removed", input.getRemovedElements());
		addNode(root, "# Elements Modified", input.getModifiedElements());
		addNode(root, "# Elements Unmodified", input.getUnmodifiedElements());

		if (input.hasValidLineChurn()) {
			double relativeChurn = input.getChurnLines()
					/ (double) input.getNormalizedLines();
			addNode(root, "# Lines Churned", input.getChurnLines(),
					relativeChurn);
			addNode(root, "# Lines Total (normalized)",
					input.getNormalizedLines(), 1.0);
			double relativeInRemovedElements = input
					.getLinesInRemovedElements()
					/ (double) input.getNormalizedLines();
			addNode(root, "# Lines in Removed Elements",
					input.getLinesInRemovedElements(),
					relativeInRemovedElements);
		}

		return root;
	}

	/**
	 * Add an info node with values.
	 */
	private void addNode(ListNode root, String title, int value, double ratio) {
		ListNode child = new ListNode(title);
		child.setValue(KEY_ABS_ELEMENTS, value);
		child.setValue(KEY_REL_ELEMENTS, ratio * 100);
		root.addChild(child);

	}

	/** Add an info node. */
	private void addNode(ListNode root, String title,
			Collection<ITextElement> elements) {
		ListNode child = new ListNode(title);
		child.setValue(KEY_ABS_ELEMENTS, elements.size());
		double ratio = (double) elements.size() / input.getTotalElementCount();
		child.setValue(KEY_REL_ELEMENTS, ratio * 100);

		if (includeElementList) {
			child.setValue(KEY_ELEMENTS,
					UniformPathUtils.uniformPathList(elements));
		}

		root.addChild(child);
	}

}
