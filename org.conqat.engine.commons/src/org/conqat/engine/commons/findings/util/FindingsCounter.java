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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.DetachedFinding;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ConQATNodePredicateBase;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.IdentityHashSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45915 $
 * @ConQAT.Rating YELLOW Hash: AA4A518B47C40065459F3F9A9DE89AD0
 */
@AConQATProcessor(description = ""
		+ "This processor counts the number of findings in a ConQAT node structure. "
		+ "Findings are counted both within the nodes and in finding lists "
		+ "stored in given keys. This way in can be used both for plain nodes as "
		+ "well as finding reports.")
public class FindingsCounter extends NodeTraversingProcessorBase<IConQATNode> {

	/** The key to store the result in. */
	@AConQATKey(description = "The number of findings found.", type = "java.lang.Integer")
	public static final String DEFAULT_KEY = "#findings";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.WRITEKEY_NAME, attribute = ConQATParamDoc.WRITEKEY_KEY_NAME, optional = true, description = "The key to store the number of findings in. Default is "
			+ DEFAULT_KEY)
	public String findingsKey = DEFAULT_KEY;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "count-detached-findings", attribute = "value", optional = true, description = "Determines whether detached findings are counted. "
			+ "Default is true.")
	public boolean countDetachedFindings = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "finding-inclusion", attribute = ConQATParamDoc.INCLUSION_PREDICATE_ATTRIBUTE, optional = true, description = ""
			+ "If set, only matching findings are processed. Note that this will not be used for processing detached findings!")
	public ConQATNodePredicateBase findingInclusionPredicate;

	/** The findings found. */
	private final Set<Object> findings = new IdentityHashSet<Object>();

	/**
	 * Determines whether the display list should be included for the inspected
	 * keys.
	 */
	private boolean useDisplayList = true;

	/** The keys to look in for findings. */
	private final Set<String> keys = new HashSet<String>();

	/** Flag for storing whether any detached findings have been processed. */
	private boolean hadDetachedFindings = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "findings", description = "If keys are given, all findings stored at these keys are counted as well.")
	public void addKey(
			@AConQATAttribute(name = "key", description = "The key to look into.") String key) {
		keys.add(key);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "use-display-list", maxOccurrences = 1, description = "If this is set to true, all keys in the display list will be inspected for findings. Default is true.")
	public void setUseDisplayList(
			@AConQATAttribute(name = "value", description = "True or false.") boolean value) {
		useDisplayList = value;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		super.setUp(root);
		if (useDisplayList) {
			keys.addAll(NodeUtils.getDisplayList(root).getKeyList());
		}
		NodeUtils.addToDisplayList(root, findingsKey);
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		int localCount = 0;
		if (isRelevant(node)) {
			++localCount;
			findings.add(node);
		}
		for (String key : keys) {
			Object value = node.getValue(key);
			if (isRelevant(value)) {
				++localCount;
				findings.add(value);
			} else if (value instanceof Collection<?>) {
				for (Object o : (Collection<?>) value) {
					if (isRelevant(o)) {
						++localCount;
						findings.add(o);
					}
				}
			}
		}
		node.setValue(findingsKey, localCount);
	}

	/** Determines whether the given object should be counted. */
	private boolean isRelevant(Object o) {
		if (o instanceof Finding) {
			return isIncluded((Finding) o);
		}
		if (o instanceof DetachedFinding) {
			hadDetachedFindings = true;
			return countDetachedFindings;
		}
		return false;
	}

	/** Checks if the finding is included by the inclusion predicate */
	private boolean isIncluded(Finding finding) {
		return findingInclusionPredicate == null
				|| findingInclusionPredicate.isContained(finding);
	}

	/** {@inheritDoc} */
	@Override
	protected void finish(IConQATNode root) {
		root.setValue(findingsKey, findings.size());

		if (hadDetachedFindings && countDetachedFindings
				&& findingInclusionPredicate != null) {
			getLogger()
					.warn("You are using an inclusion predicate which can not process detached findings, "
							+ "but still count them. This may lead to unexpected results.");
		}
	}
}