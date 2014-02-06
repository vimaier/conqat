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
package org.conqat.engine.architecture.assessment;

import java.util.Map;

import org.conqat.engine.architecture.format.EAssessmentType;
import org.conqat.engine.architecture.scope.AnnotatedArchitecture;
import org.conqat.engine.architecture.scope.DependencyPolicy;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.string.IStringResolver;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46835 $
 * @ConQAT.Rating YELLOW Hash: A63475C40333AD68F943BB848141F94B
 */
@AConQATProcessor(description = "Creates findings for architecture violations and annotates them to the source node.")
public class ArchitectureFindingCreator extends
		ConQATPipelineProcessorBase<IConQATNode> {

	/** Default category name of created findings */
	public static final String DEFAULT_CATEGORY_NAME = "Architecture Conformance";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "architecture", attribute = ConQATParamDoc.INPUT_REF_NAME, optional = false, description = "Assessed architecture")
	public AnnotatedArchitecture assessedArchitecture;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "finding", attribute = "key", optional = true, description = "The key used for storing the findings in.")
	public String key = "findings";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "category", attribute = "name", optional = true, description = "Category name of created findings. Default is "
			+ DEFAULT_CATEGORY_NAME)
	public String categoryName = DEFAULT_CATEGORY_NAME;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "group", attribute = "name", optional = true, description = "Group name of dependency violation findings. Default is "
			+ "Architecture components: <Component1> -> <Component2>")
	public String dependencyViolationFindingGroupName = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "location-resolver", attribute = "delegate", optional = true, description = "Delegate that modifies the location of a dependency.")
	public IStringResolver locationResolver = null;

	/** Map from id to node in input scope */
	private Map<String, IConQATNode> idToNode;

	/** The finding report. */
	private FindingReport findingReport;

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode input) throws ConQATException {
		NodeUtils.addToDisplayList(input, key);
		idToNode = TraversalUtils.createIdToLeafNodeMap(input);

		findingReport = NodeUtils.getFindingReport(input);
		FindingCategory category = findingReport
				.getOrCreateCategory(categoryName);

		for (DependencyPolicy policy : assessedArchitecture.getSortedPolicies()) {
			if (policy.getAssessment() != EAssessmentType.INVALID) {
				continue;
			}
			String groupName = "Architecture components: " + policy.toString();
			if (dependencyViolationFindingGroupName != null) {
				groupName = dependencyViolationFindingGroupName;
			}
			FindingGroup group = category.getOrCreateFindingGroup(groupName);
			createFindings(policy, group);
		}

	}

	/** Create findings for policy */
	private void createFindings(DependencyPolicy policy, FindingGroup group)
			throws ConQATException {
		for (Finding dependency : policy.getDependencies()) {
			IConQATNode source = getNode(dependency.getDependencySource());

			ElementLocation location = dependency.getLocation();
			if (locationResolver != null) {
				location = resolveLocation(source);
			}

			Finding finding = FindingUtils
					.createAndAttachFinding(
							group,
							"Type depends on "
									+ dependency.getDependencyTarget()
									+ ". This violates the architecture specification.",
							source, location, key);
			getLogger().debug("Created finding " + finding);
		}
	}

	/** Resolve location with {@link #locationResolver} */
	private ElementLocation resolveLocation(IConQATNode source) {

		String id = source.getId();
		try {
			id = locationResolver.resolve(id);
		} catch (ConQATException e) {
			getLogger().warn("Could not resolve location for " + id, e);
		}

		return new ElementLocation(id, id);
	}

	/** Get node for Id */
	private IConQATNode getNode(String sourceId) throws ConQATException {
		IConQATNode source = idToNode.get(sourceId);
		if (source == null) {
			throw new ConQATException("Node '" + sourceId
					+ "' not found in scope.");
		}
		return source;
	}
}