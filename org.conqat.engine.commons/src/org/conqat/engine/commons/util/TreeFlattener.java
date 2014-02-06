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
package org.conqat.engine.commons.util;

import java.util.LinkedHashSet;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * This processor flattens tree structures by adding all leaves directly to a
 * dummy root node.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 38367 $
 * @ConQAT.Rating GREEN Hash: C9F27F3AB65776A57D38DE2159B25C29
 */
@AConQATProcessor(description = "This processor flattens tree structures by "
		+ "adding all leaves directly to a dummy root node. By default, only values included "
		+ "in the display list will be copied to the flat result structure. Additional "
		+ "keys may be added.")
public class TreeFlattener extends ConQATInputProcessorBase<IConQATNode>
		implements INodeVisitor<IConQATNode, NeverThrownRuntimeException> {

	/** Result node. */
	private ListNode result;

	/** Keys to copy. */
	private LinkedHashSet<String> keys = new LinkedHashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 0, description = ""
			+ "Additional key")
	public void setReadKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		keys.add(key);
	}

	/** {@inheritDoc} */
	@Override
	public ListNode process() {
		result = new ListNode(input.getId(), input.getName());
		DisplayList displayList = NodeUtils.getDisplayList(input);
		for (String key : displayList) {
			keys.add(key);
		}
		result.setValue(NodeConstants.DISPLAY_LIST, displayList);
		result.setValue(NodeConstants.HIDE_ROOT, true);

		TraversalUtils.visitLeavesDepthFirst(this, input);
		return result;
	}

	/** Create child node, copy values and add to result root. */
	@Override
	public void visit(IConQATNode node) {
		ListNode child = new ListNode(node.getId(), node.getName());
		for (String key : keys) {
			child.setValue(key, node.getValue(key));
		}
		result.addChild(child);
	}

}