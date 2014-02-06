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
package org.conqat.engine.architecture.output;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.architecture.scope.ArchitectureDefinition;
import org.conqat.engine.architecture.scope.DependencyPolicy;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45849 $
 * @ConQAT.Rating GREEN Hash: CEE73A673B81C732BA2D47B546FC4E42
 */
@AConQATProcessor(description = "A processor to create the list of polices and dependencies for an architecture analysis.")
public class PolicyListCreator extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The dependencies for the policy", type = "java.util.List<String>")
	public static final String DEPENDENCY_KEY = "dependencies";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The assessment for the policy", type = "org.conqat.lib.commons.assessment.Assessment")
	public static final String ASSESSMENT_KEY = "assessment";

	/** The architecture we will render. */
	private ArchitectureDefinition arch;

	/** The render mode used. */
	private ERenderMode renderMode;

	/** The root node containing the policies. */
	private final ListNode root = new ListNode("<architecture>");

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = "Architecture to visualize.")
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ArchitectureDefinition arch) {
		this.arch = arch;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "render", minOccurrences = 1, maxOccurrences = 1, description = "Sets the render mode used.")
	public void setRenderMode(
			@AConQATAttribute(name = "mode", description = "The render mode (POLICIES, ASSESSMENT, VIOLATIONS, VIOLATIONS_AND_TOLERATIONS)") ERenderMode renderMode) {
		this.renderMode = renderMode;
	}

	/** {@inheritDoc} */
	@Override
	public IRemovableConQATNode process() {
		List<DependencyPolicy> policies = new ArrayList<DependencyPolicy>();
		arch.collectPolicies(policies);
		for (DependencyPolicy policy : policies) {
			insertPolicy(policy);
		}

		root.setValue(NodeConstants.HIDE_ROOT, true);
		NodeUtils.addToDisplayList(root, ASSESSMENT_KEY, DEPENDENCY_KEY);
		return root;
	}

	/** Inserts a policy. */
	private void insertPolicy(DependencyPolicy policy) {
		if (!renderMode.includePolicy(policy)) {
			return;
		}

		ListNode policyNode = new ListNode(policy.toString());

		// Use a list to ensure that number of dependencies and number of
		// findings is consistent
		List<String> dependencies = new ArrayList<String>();
		for (Finding dependency : policy.getDependencies()) {
			dependencies.add(dependency.getDependencySource() + " -> "
					+ dependency.getDependencyTarget());
		}
		// Sort dependencies, important for system tests
		policyNode.setValue(DEPENDENCY_KEY, CollectionUtils.sort(dependencies));

		policyNode.setValue(ASSESSMENT_KEY,
				new Assessment(renderMode.determineColor(policy)));
		root.addChild(policyNode);
	}
}