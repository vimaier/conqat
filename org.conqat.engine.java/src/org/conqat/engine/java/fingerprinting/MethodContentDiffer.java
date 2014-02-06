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
package org.conqat.engine.java.fingerprinting;

import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: 77EFC6147B2987F057AC639517B7DBFE
 */
@AConQATProcessor(description = "Processor for comparing two lists of methods. "
		+ "The methods in the first list will be annotated with a key that states "
		+ "whether the method has been changed or is missing compared to the reference list.")
public class MethodContentDiffer extends
		NodeTraversingProcessorBase<IConQATNode> {

	/** The values are equal. */
	public static final String COMPARISON_EQUAL = "equal";

	/** The reference does not contain a corresponding node. */
	public static final String COMPARISON_MISSING = "missing";

	/** The values are different. */
	public static final String COMPARISON_DIFFERENT = "different";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The result of the comparison, which is one of "
			+ COMPARISON_EQUAL + ", " + COMPARISON_MISSING + ", or "
			+ COMPARISON_DIFFERENT, type = "java.lang.String")
	public static final String COMPARISON_KEY = "comparison";

	/** Mapping from method ID to hash code in the reference nodes. */
	private final Map<String, String> referenceMap = new HashMap<String, String>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "reference", attribute = ConQATParamDoc.INPUT_REF_NAME, description = ""
			+ "The reference with which the list should be compared.")
	public IConQATNode reference;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "hash", attribute = "key", description = "The key that stores the hash with which the method should be compared.")
	public String hashKey;

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		super.setUp(root);

		NodeUtils.addToDisplayList(root, COMPARISON_KEY);

		for (IConQATNode node : TraversalUtils.listLeavesDepthFirst(reference)) {
			String value = NodeUtils.getStringValue(node, hashKey, null);
			if (value == null) {
				getLogger().warn(
						"Missing value in reference for node " + node.getId());
			} else {
				referenceMap.put(node.getId(), value);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		String value = NodeUtils.getStringValue(node, hashKey, null);
		if (value == null) {
			getLogger().warn("Missing value in input for node " + node.getId());
		} else {
			node.setValue(COMPARISON_KEY,
					compare(value, referenceMap.get(node.getId())));
		}
	}

	/**
	 * Performs the comparison of the key with the reference and returns one of
	 * the key values.
	 */
	private String compare(String value, String reference) {
		if (reference == null) {
			return COMPARISON_MISSING;
		}

		if (reference.equals(value)) {
			return COMPARISON_EQUAL;
		}

		return COMPARISON_DIFFERENT;
	}

}