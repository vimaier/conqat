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
package org.conqat.engine.dotnet.coverage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.commons.format.EValueFormatter;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementUtils;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.engine.sourcecode.shallowparser.util.ShallowParsedNameUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44377 $
 * @ConQAT.Rating GREEN Hash: DFF5E3BD1D93959DB47C8AB08BF75595
 */
@AConQATProcessor(description = "Converts a tree of token resources into a tree that reaches "
		+ "to the method level and annotates these methods with size and coverage information.")
public class MethodCoverageTreeExpander extends
		ConQATInputProcessorBase<ITokenResource> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The size of the methods in LOC", type = "java.lang.Integer")
	private static final String SIZE_KEY = "LoC";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The relative coverage of the methods.", type = "java.lang.Double")
	public static final String COVERAGE_KEY = "coverage";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "coverage", attribute = "tree", description = "The coverage tree as created from the CoverageReportReader. "
			+ "The reader must be configured to process methods as well.")
	public ListNode coverageTree;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "skip-elements-without-methods", attribute = "value", optional = true, description = ""
			+ "If this is true, elements without methods are not included in the output tree. Default is true.")
	public boolean skipElementsWithoutMethods = true;

	/** Maps method name to coverage info object. */
	private Map<String, CoverageInfo> methodCoverageMap = new HashMap<String, CoverageInfo>();

	/** Maps resource (input) nodes to the corresponding result nodes. */
	private final Map<IResource, ListNode> resourceToResult = new IdentityHashMap<IResource, ListNode>();

	/** {@inheritDoc} */
	@Override
	public ListNode process() throws ConQATException {

		ListNode result = new ListNode("<root>");
		resourceToResult.put(input, result);

		DisplayList displayList = NodeUtils.getDisplayList(result);
		displayList.addKey(SIZE_KEY, EValueFormatter.INTEGER.getFormatter());
		displayList
				.addKey(COVERAGE_KEY, EValueFormatter.PERCENT.getFormatter());

		fillCoverageMap();

		for (ITokenElement element : TokenElementUtils.listTokenElements(input)) {
			processElement(element);
		}
		return result;
	}

	/**
	 * Fills the {@link #methodCoverageMap} by traversing the
	 * {@link #coverageTree}.
	 */
	private void fillCoverageMap() throws ConQATException {
		for (ListNode node : TraversalUtils.listLeavesDepthFirst(coverageTree)) {
			// clean up the method name (may end in "()")
			String methodName = node.getId().replaceFirst("\\(\\)$", "");

			// remove any template parameters
			methodName = methodName.replaceAll("<.*?>",
					StringUtils.EMPTY_STRING);

			methodCoverageMap.put(methodName, new CoverageInfo(node,
					methodCoverageMap.get(methodName)));
		}
	}

	/**
	 * Processes a single element and adds the corresponding nodes into the
	 * result node.
	 */
	private void processElement(ITokenElement element) throws ConQATException {
		ListNode elementNode = getOrCreateResultNode(element);

		List<ShallowEntity> entities = ShallowParserFactory.parse(element,
				getLogger());
		List<ShallowEntity> methods = ShallowEntityTraversalUtils
				.listEntitiesOfType(entities, EShallowEntityType.METHOD);
		Set<String> methodsSeen = new HashSet<String>();

		for (ShallowEntity method : methods) {
			if (method.getChildren().isEmpty()) {
				// skip abstract methods and methods without code (these can not
				// be covered)
				continue;
			}

			String methodName = ShallowParsedNameUtils
					.getFullyQualifiedName(method);
			if (!methodsSeen.add(methodName)) {
				// process overloaded methods only once
				continue;
			}

			ListNode methodNode = new ListNode(methodName);
			elementNode.addChild(methodNode);

			CoverageInfo coverageInfo = methodCoverageMap.get(methodName);
			if (coverageInfo == null) {
				getLogger()
						.warn("Missing coverage information for method "
								+ methodName);
			} else {
				methodNode.setValue(COVERAGE_KEY, coverageInfo.getCoverage());
				methodNode.setValue(SIZE_KEY, coverageInfo.getSizeInLines());
			}
		}

		if (skipElementsWithoutMethods && !elementNode.hasChildren()) {
			elementNode.remove();
		}
	}

	/**
	 * Returns the result node corresponding to the given node or creates one if
	 * required.
	 */
	private ListNode getOrCreateResultNode(IResource resource) {
		ListNode resultNode = resourceToResult.get(resource);
		if (resultNode != null) {
			return resultNode;
		}

		ListNode parentNode = getOrCreateResultNode(resource.getParent());
		String id = resource.getName();
		if (parentNode.getParent() != null) {
			// omit artificial root node in id
			id = parentNode.getId() + "/" + id;
		}

		resultNode = new ListNode(id, resource.getName());
		parentNode.addChild(resultNode);
		resourceToResult.put(resource, resultNode);
		return resultNode;
	}

}
