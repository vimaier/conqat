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
package org.conqat.engine.commons.assessment;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.IdentityHashSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37466 $
 * @ConQAT.Rating GREEN Hash: 45C02F672C261A306CE0561199210813
 */
@AConQATProcessor(description = "This processor creates a categorized view "
		+ "of multiple assessments of one or more input trees. In each category "
		+ "it lists all leave nodes whose assessment color is one of the colors "
		+ "specified via the include parameter. If no categories were defined "
		+ "this processor creates a category for each key found in the display lists "
		+ "in all input trees.")
public class AssessmentCategorizer extends ConQATProcessorBase {

	/** Default value for write key. */
	private final static String DEFAULT_WRITE_KEY = "Assessment";

	/** Dummy value for default key. */
	private final static String DUMMY_VALUE = "#DUMMY#";

	/** The key to write the result into. */
	private String writeKey = DEFAULT_WRITE_KEY;

	/** Colors to include. */
	private final EnumSet<ETrafficLightColor> colors = EnumSet
			.noneOf(ETrafficLightColor.class);

	/** Roots of the input trees. */
	private final IdentityHashSet<IConQATNode> roots = new IdentityHashSet<IConQATNode>();

	/** The categories. */
	private LinkedHashMap<String, String> categories = new LinkedHashMap<String, String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.WRITEKEY_NAME, minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The key to write the aggregation value to ["
			+ DEFAULT_WRITE_KEY
			+ "]")
	public void setWriteKey(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC) String key) {
		writeKey = key;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "include", minOccurrences = 1, maxOccurrences = -1, description = "Add color to include in groups.")
	public void addColor(
			@AConQATAttribute(name = "value", description = "Traffic light color") ETrafficLightColor color) {
		colors.add(color);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = -1, description = ConQATParamDoc.INPUT_DESC)
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IConQATNode input) {
		roots.add(input);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "category", minOccurrences = 0, maxOccurrences = -1, description = "Add assessment category.")
	public void addCategory(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key,
			@AConQATAttribute(name = "description", description = "Category description", defaultValue = DUMMY_VALUE) String description) {
		categories.put(key, description);
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode process() {
		if (categories.isEmpty()) {
			initCategories();
		}
		ListNode result = new ListNode();
		NodeUtils.addToDisplayList(result, writeKey);
		result.setValue(NodeConstants.HIDE_ROOT, true);

		for (String key : categories.keySet()) {
			addCategory(key, result);
		}

		return result;
	}

	/** Set up categories based on display lists. */
	private void initCategories() {
		for (IConQATNode root : roots) {
			for (String key : NodeUtils.getDisplayList(root)) {
				categories.put(key, DUMMY_VALUE);
			}
		}
	}

	/**
	 * Add a category.
	 * 
	 * @param key
	 *            the key that describes the category.
	 * @param result
	 *            the not the category will be attached to.
	 */
	private void addCategory(String key, ListNode result) {
		String description = categories.get(key);
		String nodeId;
		if (DUMMY_VALUE.equals(description)) {
			nodeId = key;
		} else {
			nodeId = description + " (" + key + ")";
		}
		ListNode category = new ListNode(nodeId);
		result.addChild(category);

		for (IConQATNode root : roots) {
			addNodes(category, key, root);
		}
	}

	/**
	 * Add nodes to category.
	 * 
	 * @param category
	 *            the category node.
	 * @param key
	 *            the key that describes the category.
	 * @param root
	 *            the root of the input tree
	 */
	private void addNodes(ListNode category, String key, IConQATNode root) {
		List<IConQATNode> nodes = TraversalUtils.listLeavesDepthFirst(root);

		for (IConQATNode node : nodes) {
			try {
				Assessment assessment = NodeUtils.getValue(node, key,
						Assessment.class);
				if (colors.contains(assessment.getDominantColor())) {
					ListNode child = new ListNode(node.getId());
					child.setValue(writeKey, assessment);
					category.addChild(child);
				}
			} catch (ConQATException e) {
				getLogger().warn(e.getMessage());
			}
		}
	}

}