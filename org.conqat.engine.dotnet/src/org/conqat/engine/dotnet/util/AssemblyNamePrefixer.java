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
package org.conqat.engine.dotnet.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.keys.IDependencyListKey;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.dotnet.ila.ILImporterProcessorBase;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * Note: There is an official name format for full qualified type names in .Net
 * that includes the assembly name:
 * http://msdn.microsoft.com/en-us/library/yfsftwz6.aspx. However, this makes
 * the assembly name a suffix of the type name. As this would make the patterns
 * in the architecture specification quite awkward, we chose not to use this
 * format. One issue here is, that the separator between the type name and the
 * assembly name is the comma which also appears in the names of types with
 * multiple generic parameters.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C4FB709329C32742A18E3AAFBF96FC53
 */
@AConQATProcessor(description = "This processor creates a copy of a tree of .Net types and "
		+ "prefixes each type name with the name of the assembly this type is defined in. This "
		+ "is useful if an architecture analysis should be performed w.r.t. to the assembly "
		+ "names instead of the namespaces. The processor does only prefix the id of the node "
		+ "but also the names found in the dependency list stored at key "
		+ IDependencyListKey.DEPENDENCY_LIST_KEY
		+ ". The assembly name and the full qualified name of the type are separated by '::'. "
		+ "Types whose assembly are not known are prefixed with the separator only. "
		+ " NOTE: This processor only performs the prefixing if explicitly enabled by the "
		+ "provided parameter. Otherwise it simply returns its input.")
public class AssemblyNamePrefixer extends
		ConQATInputProcessorBase<IRemovableConQATNode> implements
		INodeVisitor<IConQATNode, ConQATException> {

	/** Separator that separates assembly name from type name. */
	public static final String SEPARATOR = "::";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.ENABLE_NAME, attribute = ConQATParamDoc.ENABLE_PROCESSOR_NAME, description = ConQATParamDoc.ENABLE_DESC
			+ " [Default is disabled]", optional = true)
	public boolean enabled = false;

	/** Maps from FQ name to assembly name. */
	private final Map<String, String> nameMap = new HashMap<String, String>();

	/** Result node. */
	private ListNode result;

	/** The display list of the tree being copied. */
	private DisplayList displayList;

	/** {@inheritDoc} */
	@Override
	public IRemovableConQATNode process() throws ConQATException {
		if (!enabled) {
			return input;
		}

		result = new ListNode(input.getId(), input.getName());
		displayList = NodeUtils.getDisplayList(input);
		result.setValue(NodeConstants.DISPLAY_LIST, displayList);
		result.setValue(NodeConstants.HIDE_ROOT, true);

		initNameMap();

		// create copy
		TraversalUtils.visitLeavesDepthFirst(this, input);
		return result;
	}

	/**
	 * Initialize the map that maps from FQ name to assembly name.
	 */
	private void initNameMap() throws ConQATException {
		for (IConQATNode node : TraversalUtils.listLeavesDepthFirst(input)) {
			// this fails if the assembly name is not stored. This intended as
			// strange things would happen otherwise
			String assemblyName = NodeUtils.getStringValue(node,
					ILImporterProcessorBase.ASSEMBLY_NAME);
			if (nameMap.containsKey(node.getId())) {
				getLogger().warn(
						"Multiple types with same full qualified name: "
								+ node.getId());
			} else {
				nameMap.put(node.getId(), assemblyName);
			}
		}
	}

	/**
	 * Create child node, copy values and add to result root.
	 */
	@Override
	public void visit(IConQATNode node) throws ConQATException {
		String assemblyName = NodeUtils.getStringValue(node,
				ILImporterProcessorBase.ASSEMBLY_NAME);

		ListNode child = new ListNode(assemblyName + SEPARATOR + node.getId(),
				node.getName());

		for (String key : displayList) {
			Object value = node.getValue(key);
			if (IDependencyListKey.DEPENDENCY_LIST_KEY.equals(key)) {
				value = prefix(node, value);
			}

			child.setValue(key, value);
		}
		result.addChild(child);
	}

	/**
	 * Create a dependencies list whose entries are prefixed with the assembly
	 * names.
	 */
	private Object prefix(IConQATNode node, Object value) {

		if (!(value instanceof Collection<?>)) {
			getLogger().warn(
					"Collection of strings expected as dependency list but node "
							+ node.getId() + " has "
							+ value.getClass().getName());
			return value;
		}

		List<String> result = new ArrayList<String>();
		for (Object element : (Collection<?>) value) {
			if (element instanceof String) {
				result.add(obtainAssemblyName((String) element) + SEPARATOR
						+ element);
			} else {
				getLogger().warn(
						"Collection of strings expected as dependency list but node "
								+ node.getId()
								+ " has a collection containing a "
								+ element.getClass().getName());
				return value;
			}
		}
		return result;

	}

	/** Obtain the name of the assembly. */
	private String obtainAssemblyName(String name) {
		// strip inner class
		name = name.replaceFirst("/.*$", StringUtils.EMPTY_STRING);

		String assemblyName = nameMap.get(name);
		if (assemblyName != null) {
			return assemblyName;
		}
		return StringUtils.EMPTY_STRING;
	}

}
