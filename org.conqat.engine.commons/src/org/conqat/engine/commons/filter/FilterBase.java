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
package org.conqat.engine.commons.filter;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.logging.IncludeExcludeListLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for filters, which are processors pruning
 * {@link IRemovableConQATNode} trees.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40332 $
 * @ConQAT.Rating GREEN Hash: F47DF44EDAA8DCDCDF8744BD35F6C82B
 * 
 * @param <N>
 *            the type of node being filtered.
 */
public abstract class FilterBase<N extends IRemovableConQATNode> extends
		ConQATPipelineProcessorBase<N> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "log", attribute = "element-type", optional = true, description = ""
			+ "If set, a user level log message is created with the specified element-type (e.g. files, ...).")
	public String logElementType = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INVERT_NAME, attribute = ConQATParamDoc.INVERT_VALUE_NAME, optional = true, description = ConQATParamDoc.INVERT_PARAM_DOC)
	public boolean invert = false;

	/** Keep track of filtered elements for logging purposes */
	private final List<String> filteredIds = new ArrayList<String>();

	/** Keep track of retained elements for logging purposes */
	private final List<String> retainedIds = new ArrayList<String>();

	/** List in which {@link LogSnippetInfo}s are stored */
	private final List<LogSnippetInfo> userLogSnippets = new ArrayList<LogSnippetInfo>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "log-value", minOccurrences = 0, description = ""
			+ "Adds a value stored under the given key to the include / exclude log message. Only works if the log parameter is also specified.")
	public void addUserLogSnippet(
			@AConQATAttribute(name = "prefix", description = "String that gets inserted into the log message before the value.") String prefix,
			@AConQATAttribute(name = "key", description = "Key under which value is stored") String key,
			@AConQATAttribute(name = "postfix", description = "String that gets inserted into the log message behind the value.") String postfix) {
		userLogSnippets.add(new LogSnippetInfo(prefix, key, postfix));
	}

	/** Returns whether this filter operates in inverted mode. */
	protected boolean isInverted() {
		return invert;
	}

	/**
	 * Returns the target type to operate on. Default implementation returns
	 * all.
	 */
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(N input) throws ConQATException {
		preProcessInput(input);

		filterNodes(input);

		getLogger().debug(
				"Filtered " + filteredIds.size() + " nodes, retained "
						+ retainedIds.size() + " nodes.");

		if (logElementType != null) {
			logResults();
		}
	}

	/**
	 * Template method for pre processing the input before filtering. Default
	 * implementation does nothing.
	 */
	@SuppressWarnings("unused")
	protected void preProcessInput(N input) throws ConQATException {
		// nothing to do
	}

	/**
	 * This method traverses the {@link IRemovableConQATNode} tree depth first,
	 * possibly removing elements.
	 */
	@SuppressWarnings("unchecked")
	private void filterNodes(N node) throws ConQATException {
		// is important to store this value before the loop, as filtering in
		// recursive calls might change the node from a target to a non-target
		// (or vice-versa)
		boolean isTargetNode = isTarget(node, getTargetNodes());

		if (getTargetNodes() != ETargetNodes.ROOT && node.hasChildren()) {
			for (IRemovableConQATNode child : NodeUtils
					.getRemovableSortedChildren(node)) {
				filterNodes((N) child);
			}
		}

		if (!isTargetNode) {
			return;
		}

		boolean filtered = isFiltered(node);
		if (invert) {
			filtered = !filtered;
		}

		if (filtered) {
			node.remove();
			filteredIds.add(getUserLogLabel(node));
		} else {
			retainedIds.add(getUserLogLabel(node));
		}
	}

	/**
	 * Template method that returns the label of a node for the user log.
	 * Default implementation returns the node id.
	 */
	private String getUserLogLabel(N node) {
		String label = node.getId();
		for (LogSnippetInfo snippet : userLogSnippets) {
			label += snippet.format(node);
		}
		return label;
	}

	/** Decides whether a node is a target or not. */
	protected boolean isTarget(N node, ETargetNodes targetNodes) {
		if (targetNodes == ETargetNodes.ALL) {
			return true;
		} else if (targetNodes == ETargetNodes.ROOT && node.getParent() == null) {
			return true;
		} else if (targetNodes == ETargetNodes.INNER && node.hasChildren()) {
			return true;
		} else if (targetNodes == ETargetNodes.LEAVES && !node.hasChildren()) {
			return true;
		}
		return false;
	}

	/**
	 * If this returns true, the provided node will not be included in the
	 * result (i.e. filtered away).
	 */
	protected abstract boolean isFiltered(N node) throws ConQATException;

	/** Create user-level log messages for included/excluded elements */
	private void logResults() {
		getLogger().info(
				new IncludeExcludeListLogMessage(logElementType, true,
						retainedIds, StructuredLogTags.FILES));
		getLogger().info(
				new IncludeExcludeListLogMessage(logElementType, false,
						filteredIds, StructuredLogTags.FILES));
	}

	/**
	 * Renders a value stored under a key in an {@link IConQATNode} for the
	 * user-log.
	 */
	private class LogSnippetInfo {

		/** String before stored value */
		private final String prefix;

		/** Key under which value is stored */
		private final String key;

		/** String appended behind stored value */
		private final String postfix;

		/** Constructor */
		public LogSnippetInfo(String prefix, String key, String postfix) {
			this.prefix = prefix;
			this.key = key;
			this.postfix = postfix;
		}

		/** Render {@link LogSnippetInfo} for node */
		public String format(IConQATNode node) {
			if (node.getValue(key) == null) {
				getLogger()
						.warn("No value stored for key '" + key + "' at node "
								+ node);
				return "NULL";
			}

			return prefix + node.getValue(key).toString() + postfix;
		}
	}
}