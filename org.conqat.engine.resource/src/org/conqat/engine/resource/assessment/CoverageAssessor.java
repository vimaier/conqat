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
package org.conqat.engine.resource.assessment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 103B48B372E1A6BD86DE65CA054A1454
 */
@AConQATProcessor(description = "This processor assesses the nodes in a ConQAT node hierarchy based on coverage, "
		+ "i.e. covered nodes are rated green while uncovered ones are rated red. "
		+ "Whether a node is covered or not is read from text elements that contain the ID of the covered nodes, one at each line.")
public class CoverageAssessor extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "key used for coverage", type = "org.conqat.lib.commons.assessment.Assessment")
	public static final String COVERAGE_KEY = "coverage";

	/** Hierarchy storing the coverage elements. */
	private ITextResource coverageElements;

	/** This set contains all covered IDs. */
	private final Set<String> covered = new HashSet<String>();

	/**
	 * This set contains all visited IDs. We hold this as we cannot simply
	 * recreate this in {@link #finish(IConQATNode)} since the traversal depends
	 * on the strategy applied by the super class.
	 */
	private final Set<String> visited = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "coverage-data", minOccurrences = 1, maxOccurrences = 1, description = "Reference to the scope that contains the coverage infomration.")
	public void setCoverageElements(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ITextResource coverageElements) {
		this.coverageElements = coverageElements;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		for (ITextElement element : ResourceTraversalUtils
				.listTextElements(coverageElements)) {
			covered.addAll(Arrays.asList(TextElementUtils.getLines(element)));
		}

		super.setUp(root);

		NodeUtils.addToDisplayList(root, COVERAGE_KEY);
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		String id = node.getId();
		visited.add(id);
		ETrafficLightColor color = ETrafficLightColor.RED;
		if (covered.contains(id)) {
			color = ETrafficLightColor.GREEN;
		}
		node.setValue(COVERAGE_KEY, new Assessment(color));
	}

	/** {@inheritDoc} */
	@Override
	protected void finish(IConQATNode root) throws ConQATException {
		for (String id : covered) {
			if (!visited.contains(id)) {
				getLogger()
						.warn("ID "
								+ id
								+ " found in coverage elements was not found in input scope!");
			}
		}

		super.finish(root);
	}
}