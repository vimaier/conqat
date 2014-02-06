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
package org.conqat.engine.commons.findings.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for finding filters.
 * 
 * @author $Author: goede $
 * @version $Rev: 41703 $
 * @ConQAT.Rating GREEN Hash: 64C321498D73BE300AF0FA16B5A4AC2E
 */
public abstract class FindingsFilterBase extends
		ConQATPipelineProcessorBase<IConQATNode> {

	/** Standard suffix to be used in the documentation of finding filters. */
	public static final String PROCESSOR_DOC_SUFFIX = " This can be either applied to a findings report or a node hierarchy containing findings. "
			+ "In the second case, findings are removed from both the element and the findings report.";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INVERT_NAME, attribute = ConQATParamDoc.INVERT_VALUE_NAME, optional = true, description = ConQATParamDoc.INVERT_PARAM_DOC)
	public boolean invert = false;

	/** The keys to be searched for findings. */
	private final Set<String> findingKeys = new HashSet<String>();

	/** Keeps track of excluded findings for logging purposes */
	private final int excludedFindingsCount = 0;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.FINDING_PARAM_NAME, description = ConQATParamDoc.FINDING_KEYS_PARAM_DOC)
	public void addFindingKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		findingKeys.add(key);
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode input) throws ConQATException {
		if (input instanceof FindingReport) {
			processReport((FindingReport) input);
			return;
		}

		if (findingKeys.isEmpty()) {
			findingKeys.addAll(NodeUtils.getDisplayList(input).getKeyList());
		}

		for (IConQATNode node : TraversalUtils.listAllDepthFirst(input)) {
			processNode(node);
		}

		getLogger().debug("Excluded " + excludedFindingsCount + " findings");
	}

	/** Processes the findings of a report. */
	private void processReport(FindingReport report) throws ConQATException {
		for (FindingCategory category : report.getChildren()) {
			for (FindingGroup group : category.getChildren()) {
				processGroup(group);
			}

			if (!category.hasChildren()) {
				category.remove();
			}
		}
	}

	/** Processes a finding group. */
	protected void processGroup(FindingGroup group) throws ConQATException {
		for (Finding finding : group.getChildren()) {
			if (shouldRemoveFinding(null, finding)) {
				finding.remove();
				getLogger().debug("Excluding finding: " + finding);
			}
		}

		if (!group.hasChildren()) {
			group.remove();
			getLogger().debug("Excluding findinggroup: " + group);
		}
	}

	/** Processes the findings of a single node. */
	private void processNode(IConQATNode node) throws ConQATException {
		if (skipNode(node)) {
			return;
		}

		setUpOnNode(node);

		for (String findingsKey : findingKeys) {
			FindingsList findingsList = NodeUtils.getFindingsList(node,
					findingsKey);
			if (findingsList == null || findingsList.isEmpty()) {
				continue;
			}

			for (Finding finding : new ArrayList<Finding>(findingsList)) {
				if (shouldRemoveFinding(node, finding)) {
					findingsList.remove(finding);
					finding.remove();
					getLogger().debug("Excluding finding: " + finding);
				}
			}
		}
	}

	/**
	 * Set up method which is called before a node is processed. By default
	 * nothing is done. If set up is required, this method must be overwritten.
	 */
	@SuppressWarnings("unused")
	protected void setUpOnNode(IConQATNode node) throws ConQATException {
		// by default do nothing
	}

	/**
	 * Returns whether the given finding should be removed.
	 * 
	 * @param node
	 *            the node the finding is attached to. May be null if filtering
	 *            on the report.
	 */
	protected boolean shouldRemoveFinding(IConQATNode node, Finding finding)
			throws ConQATException {
		if (invert) {
			return !isFiltered(node, finding);
		}
		return isFiltered(node, finding);
	}

	/**
	 * Template method used to decide whether a finding is filtered (i.e.
	 * removed).
	 * 
	 * @param node
	 *            the node the finding is attached to. May be null if filtering
	 *            on the report.
	 */
	protected abstract boolean isFiltered(IConQATNode node, Finding finding)
			throws ConQATException;

	/**
	 * Template method used to skip entire nodes during filtering. Default
	 * implementation returns false.
	 */
	@SuppressWarnings("unused")
	protected boolean skipNode(IConQATNode node) throws ConQATException {
		return false;
	}
}
