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
package org.conqat.engine.commons.findings.util;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for processors that traverse findings.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46057 $
 * @ConQAT.Rating RED Hash: C643379AF4D6382FD56E3EAF7778CBF9
 */
public abstract class FindingsTraversingProcessorBase<T extends IConQATNode>
		extends NodeTraversingProcessorBase<T> {

	/** The keys to be searched for findings. */
	private final Set<String> findingKeys = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "group-replacement", attribute = "patterns", optional = true, description = ""
			+ "Transformation patterns which are applied to the findings groups "
			+ "to match findings with differ in group name. This option is mainly "
			+ "applied if finding groups have changed in different versions of "
			+ "an external analysis tool. For replacment the group name is prefixed "
			+ "with the category name and a slash, e. g. <i>category_name/group_name</i>. "
			+ "Thus matchting can be restricted to a finding category by an appropriate pattern")
	public PatternTransformationList groupReplacmentPatterns = null;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.FINDING_PARAM_NAME, description = ConQATParamDoc.FINDING_KEYS_PARAM_DOC)
	public void addFindingKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		findingKeys.add(key);
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(T root) throws ConQATException {
		super.setUp(root);

		if (findingKeys.isEmpty()) {
			findingKeys.addAll(NodeUtils.getDisplayList(root).getKeyList());
		}
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** {@inheritDoc} */
	@Override
	public void visit(T node) throws ConQATException {
		for (String findingsKey : findingKeys) {
			FindingsList findingsList = NodeUtils.getFindingsList(node,
					findingsKey);
			// TODO (LH) Consider using CollectionUtils#isNullOrEmpty
			if (findingsList != null && !findingsList.isEmpty()) {
				visitFindings(node, findingsList);
			}
		}
	}

	/** Template method for visiting the findings. */
	protected abstract void visitFindings(T node, FindingsList findingsList)
			throws ConQATException;
}
