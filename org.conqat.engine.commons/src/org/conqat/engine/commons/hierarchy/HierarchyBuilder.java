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
package org.conqat.engine.commons.hierarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.StringSetNode;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37466 $
 * @ConQAT.Rating GREEN Hash: 9B1FFF0C85FBB52F8FCA4F8902A96532
 */
@AConQATProcessor(description = ""
		+ "Builds a hierarchy from all leaf nodes contained in an input tree "
		+ "based on their fully qualified names. "
		+ "Examples for such names can be file paths or type names. "
		+ "A parameterizable regular expression governs the decomposition of the "
		+ "fully qualified names into their constituent local names. "
		+ "<br/>"
		+ "The nodes in the new hierarchy are newly created as IRemovableConQATNodes "
		+ "<br/>"
		+ "All values that are declared visible via the root node's display list "
		+ "are copied from the source nodes to the target nodes. ")
public class HierarchyBuilder extends ConQATProcessorBase {

	/** The input for this processor. */
	private IRemovableConQATNode input;

	/** Key in which value the node's name is stored (if not given, id is used). */
	private String fqNameKey = null;

	/** Pattern that is used to split a fully qualified name into constituents */
	private String splitRegex;

	/** String used to combine id from name parts */
	private String separator = ".";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "compact", attribute = "root", description = "Determines whether the root directory is considered as atomic, or as a hierarchy. Default is hierarchy.", optional = true)
	public boolean compactRoot = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IRemovableConQATNode input) {
		this.input = input;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "id", description = "Name of the key that contains the fully qualified name of the node.", minOccurrences = 0, maxOccurrences = 1)
	public void setIdKey(
			@AConQATAttribute(name = "key", description = "Optional. Default is to use the node ids.") String idKey) {
		this.fqNameKey = idKey;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "split", description = "Regular expression used to split the fully qualified name into its constituents. Must match the separator.", minOccurrences = 1, maxOccurrences = 1)
	public void setSplitRegex(
			@AConQATAttribute(name = "regex", description = "Use java regular expression sytax.") String splitRegex) {
		this.splitRegex = splitRegex;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "separator", description = "String used to combine id from name parts [by default the dot is used]", minOccurrences = 0, maxOccurrences = 1)
	public void setSeparator(
			@AConQATAttribute(name = "string", description = "Separator string") String separator) {
		this.separator = separator;
	}

	/** Performs the creation of a new hierarchy of nodes. */
	@Override
	public IRemovableConQATNode process() throws ConQATException {
		assertRegexMatchesSeparator();

		StringSetNode root = createRoot();
		List<String> copyList = NodeUtils.getDisplayList(input).getKeyList();

		for (IRemovableConQATNode node : TraversalUtils
				.listLeavesDepthFirst(input)) {

			String fqName = getFullyQualifiedName(node);
			if (compactRoot) {
				fqName = StringUtils.stripPrefix(root.getId(),
						fqName.replaceAll(splitRegex, separator));
			}
			if (StringUtils.isEmpty(fqName)) {
				getLogger().warn(
						"Skipping node: No value stored under key " + fqNameKey
								+ " in node: " + node.getId());
			} else {
				IConQATNode element = insert(root, fqName);
				// we do not deep-clone, since we basically replace the old node
				// hierarchy with the new one. This way, we can simply move
				// values hat cannot be deep-cloned.
				NodeUtils.copyValues(copyList, node, element, false);
			}
		}

		return root;
	}

	/** Makes sure that the {@link #splitRegex} matches the {@link #separator} */
	private void assertRegexMatchesSeparator() throws ConQATException {
		if (!separator.matches(splitRegex)) {
			throw new ConQATException("The split regex '" + splitRegex
					+ "' does not match the separator '" + separator + "'");
		}
	}

	/** Creates the root node and copies the display list from input */
	private StringSetNode createRoot() throws ConQATException {
		StringSetNode root = new StringSetNode(determineRootId());
		NodeUtils.addToDisplayList(root, NodeUtils.getDisplayList(input));
		return root;
	}

	/** Determines the id of the root node */
	private String determineRootId() throws ConQATException {
		String rootId = StringUtils.EMPTY_STRING;

		if (compactRoot) {
			String prefix = null;
			List<IRemovableConQATNode> leafs = TraversalUtils
					.listLeavesDepthFirst(input);
			if (leafs.size() > 1) {
				List<String> ids = new ArrayList<String>();
				for (IRemovableConQATNode node : leafs) {
					ids.add(getFullyQualifiedName(node));
				}

				prefix = StringUtils.longestCommonPrefix(ids);
			} else {
				prefix = getFullyQualifiedName(leafs.get(0));
			}

			// we only want the hierarchy part of the prefix. we cut off
			// everything from the last regex match. Then we reconstruct the
			// root using the separator
			String[] prefixParts = prefix.split(splitRegex, -1);
			List<String> hierarchyParts = Arrays.asList(prefixParts).subList(0,
					prefixParts.length);
			rootId = StringUtils.concat(hierarchyParts, separator);
		}

		return rootId;
	}

	/**
	 * Return the id of a node or, if the parameter has been set on the
	 * processor, the value stored under the id-key.
	 * 
	 * @throws ConQATException
	 *             if no name is stored under the fqNameKey
	 */
	private String getFullyQualifiedName(IRemovableConQATNode node)
			throws ConQATException {
		if (fqNameKey != null) {
			Object value = node.getValue(fqNameKey);
			if (value == null) {
				throw new ConQATException("No name stored for key " + fqNameKey
						+ " at node " + node);
			}
			return value.toString();
		}

		return node.getId();
	}

	/**
	 * Inserts a node into the hierarchy
	 * 
	 * @param element
	 *            Root element under which new node gets inserted
	 * @param fqName
	 *            Hierarchical path to element that gets inserted
	 */
	private IConQATNode insert(StringSetNode element, String fqName) {
		String[] parts = fqName.split(splitRegex);

		for (int i = 0; i < parts.length; ++i) {
			StringSetNode childElement = element.getNamedChild(parts[i]);
			if (childElement == null) {
				String parentId = element.getId();
				if (!StringUtils.isEmpty(parentId)) {
					parentId += separator;
				}
				childElement = new StringSetNode(parentId + parts[i], parts[i]);
				element.addChild(childElement);
			}
			element = childElement;
		}

		return element;
	}

}