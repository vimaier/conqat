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
package org.conqat.engine.html_presentation.image;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.format.Summary;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.html_presentation.treemap.TreeMapCreator;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * {ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 45268 $
 * @ConQAT.Rating YELLOW Hash: 682C1DC2E92294D029E4128A1D8EC8F5
 */
@AConQATProcessor(description = "This processor renders multiple treemaps to a "
        + "single page, each of which highlights which nodes contain findings "
        + "from a particular group.")
public class FindingTreemapRenderer extends HTMLImageRenderer {

	/** {ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "input", attribute = "ref", description = "The input scope that should be shown in the treemap and whose nodes are colored according to the contained findings.")
	public IConQATNode input;

	/** {ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "finding-category", attribute = "name", optional = true, description = "Only finding groups from the category with this name are used for rendering treemaps. Default is to use finding groups in all categories.")
	public String findingCategoryName = null;

	/** {ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "finding-group", attribute = "name", optional = true, description = "Only finding groups with this name are used for rendering treemaps. Default is to use all finding groups.")
	public String findingGroupName = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "size", attribute = ConQATParamDoc.READKEY_KEY_NAME, optional = true, description = ""
	        + "Set the key used to retrieve the size of a node. "
	        + "If no key is given, each node will be weighted with 1, i.e. just the number of leaves is counted.")
	public String sizeKey = null;
	
	/** The number of finding groups used to draw treemaps. */
	private int relevantGroups = 0;

	/** {@ConQAT.Doc} */
	@Override
	@AConQATParameter(name = "image", minOccurrences = 0, description = "This parameter has no effect.")
	public void addImageDescriptor(
	        @AConQATAttribute(name = "descriptor", description = "") IImageDescriptor descriptor,
	        @AConQATAttribute(name = "name", description = "") String name) {
		// The superclass requires this parameter to occur at least once. Since
		// all images are created within this processor, there is no use of
		// setting any image from the outside. This method is overwritten to
		// change the minimum number of occurrences of this parameter to 0.
	}

	/** {@inheritDoc} */
	@Override
	protected String getIconName() {
		return "tree_map.gif";
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutPage() throws ConQATException {
		List<FindingGroup> groups = getRelevantFindingGroups();

		Collections.sort(groups, new Comparator<FindingGroup>() {
			@Override
			public int compare(FindingGroup a, FindingGroup b) {
				return a.getName().compareTo(b.getName());
			}

		});

		for (FindingGroup group : groups) {
			createImageDescriptorForFindingGroup(group);
		}
		super.layoutPage();
	}

	/**
	 * Gets the finding groups that should be used for drawing the treemap
	 * considering the category and group name that has possibly been specified
	 * by the user.
	 */
	private List<FindingGroup> getRelevantFindingGroups()
	        throws ConQATException {
		List<FindingGroup> groups = new ArrayList<FindingGroup>();
		FindingReport report = NodeUtils.getFindingReport(input);

		for (FindingCategory category : report.getChildren()) {
			if (findingCategoryName == null
			        || category.getName().equals(findingCategoryName)) {
				for (FindingGroup group : category.getChildren()) {
					if (findingGroupName == null
					        || group.getName().equals(findingGroupName)) {
						groups.add(group);
					}
				}
			}
		}

		if (groups.isEmpty()) {
			throw new ConQATException(
			        "No finding groups found with the given name!");
		}
		return groups;
	}

	/**
	 * Creates the treemap that highlights all elements that contain at least
	 * one finding from the given finding group.
	 */
	private void createImageDescriptorForFindingGroup(FindingGroup group)
	        throws ConQATException {
		IConQATNode preprocessedInput;
		try {
			preprocessedInput = input.deepClone();
		} catch (DeepCloneException e) {
			throw new ConQATException(e);
		}
		int highlightedNodes = prepareInput(preprocessedInput, group);
		if (highlightedNodes > 0) {
			relevantGroups++;
			TreeMapCreator creator = new TreeMapCreator();
			creator.init(new ProcessorInfoMock());
			if (sizeKey != null) {
				creator.setSizeKey(sizeKey);
			}
			creator.setInput(preprocessedInput);
			creator.setCushions(0.5, 0.85);

			super.addImageDescriptor(creator.process(), group.getName());
		}
	}

	/** {@inheritDoc} */
	@Override
	protected Object getSummary() {
		return new Summary(relevantGroups);
	}

	/**
	 * Sets the color for all leaves of the given input tree. By default, the
	 * color is white. If there is a finding attached to the leaf that is part
	 * of the given finding group, the leaf is colored in red. Returns the
	 * number of nodes colored in red.
	 */
	private int prepareInput(IConQATNode node, FindingGroup g) {
		int highlightedNodes = 0;
		for (IConQATNode leaf : TraversalUtils.listLeavesDepthFirst(node)) {
			Color color = Color.WHITE;
			for (String key : NodeUtils.getDisplayList(node).getKeyList()) {
				List<Finding> findings = NodeUtils.getFindingsList(leaf, key);
				if (findings != null) {
					for (Finding f : findings) {
						if (f.getParent().getName().equals(g.getName())) {
							highlightedNodes++;
							color = Color.RED;
							break;
						}
					}
				}
				if (color != Color.WHITE) {
					break;
				}
			}
			leaf.setValue("color", color);
		}
		return highlightedNodes;
	}
}
