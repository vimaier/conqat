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
package org.conqat.engine.resource.scope;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.StringSetNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.UniformPathUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author Elmar Juergens
 * @author juergens
 * @author $Author: heinemann $
 * @version $Rev: 42080 $
 * @ConQAT.Rating RED Hash: A69F01C3D5E4831C8ACA33CD1F459EE7
 */
// TODO (LH) Please document in processor description what metrics this
// processor computes. Currently one has to look into the code. Also state in
// what unit 'size' is measured.
@AConQATProcessor(description = "Creates a summary of the elements contained in a scope, aggregated "
		+ "by their extension.")
public class ScopeSummary extends ConQATProcessorBase {

	/** Root of input resource hierarchy */
	private IResource input;

	/** Key for Number of all elements of one type */
	@AConQATKey(description = "Number of all elements of one type", type = "java.lang.Integer")
	public static final String COUNT = "Count";

	/** Key for Size of all elements of one type */
	@AConQATKey(description = "Size of all elements of one type", type = "java.lang.Integer")
	public static final String SIZE = "Size";

	/** Key for Lines of Code of elements of one type */
	@AConQATKey(description = "Lines of Code of elements of one type", type = "java.lang.Integer")
	public static final String LOC = "LoC";

	/**
	 * Minimal size of element group. Smaller groups don't appear in the scope
	 * summary
	 */
	private int minCount = 0;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IResource input) {
		this.input = input;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "min", description = "Minimal count of a element group to appear in the summary", minOccurrences = 0, maxOccurrences = 1)
	public void setMinCount(
			@AConQATAttribute(name = "count", description = "Default is 0") int minCount) {
		this.minCount = minCount;
	}

	/** {@inheritDoc} */
	@Override
	public StringSetNode process() {
		Set<ElementGroupInfo> groups = groupElements();

		StringSetNode root = createRoot();

		for (ElementGroupInfo group : groups) {
			if (group.getCount() >= minCount) {
				StringSetNode groupNode = new StringSetNode(
						group.getExtension());

				groupNode.setValue(COUNT, group.getCount());
				groupNode.setValue(SIZE, group.getSize());

				if (group.isTextElementGroup) {
					groupNode.setValue(LOC, group.getLoc());
				}

				root.addChild(groupNode);
			}
		}

		return root;
	}

	/** Checks whether an {@link IElement} is an {@link ITextElement} */
	private static boolean isTextElement(IElement element) {
		return element instanceof ITextElement;
	}

	/**
	 * Groups the elements contained in the hierarchy according to their
	 * extensions.
	 */
	private Set<ElementGroupInfo> groupElements() {
		Map<String, ElementGroupInfo> elementGroups = new HashMap<String, ElementGroupInfo>();
		for (IElement element : ResourceTraversalUtils.listElements(input)) {

			String extension = UniformPathUtils.getExtension(element
					.getUniformPath());

			if (extension == null) {
				continue;
			}

			if (!elementGroups.containsKey(extension)) {
				elementGroups.put(extension, new ElementGroupInfo(extension,
						isTextElement(element)));
			}
			ElementGroupInfo info = elementGroups.get(extension);

			info.add(element);
		}

		return new HashSet<ElementGroupInfo>(elementGroups.values());
	}

	/** Create root node and set display list options */
	private StringSetNode createRoot() {
		StringSetNode root = new StringSetNode();
		root.setValue(NodeConstants.HIDE_ROOT, true);
		NodeUtils.addToDisplayList(root, COUNT, SIZE, LOC);
		return root;
	}

	/**
	 * Stores information about groups of elements.
	 */
	private class ElementGroupInfo {

		/** Extension of the elements in this group */
		private final String extension;

		/** Number of elements in the group */
		private int count = 0;

		/** Sum of the size of the elements in the group */
		private int size = 0;

		/** Sum of the lines of code of the elements in the group */
		private int loc = 0;

		/**
		 * Flag that determines whether the group represents
		 * {@link ITextElement}s
		 */
		private final boolean isTextElementGroup;

		/** Creates a {@link ElementGroupInfo} for an extension */
		public ElementGroupInfo(String extension, boolean isTextElementGroup) {
			this.extension = extension;
			this.isTextElementGroup = isTextElementGroup;
		}

		/** Adds an element to the group */
		public void add(IElement element) {
			count++;
			try {
				size += element.getContent().length;
				if (isTextElement(element)) {
					loc += countLoc((ITextElement) element);
				}
			} catch (ConQATException e) {
				getLogger().warn(
						"Could not read element " + element.getLocation()
								+ ": " + e.getMessage());
			}
		}

		/** Computes LOC for an {@link ITextElement}. */
		private int countLoc(ITextElement element) throws ConQATException {
			return TextElementUtils.countLOC(element);
		}

		/** Returns the extension. */
		public String getExtension() {
			return extension;
		}

		/** Returns the number of elements. */
		public int getCount() {
			return count;
		}

		/** Returns the LOC value. */
		public int getLoc() {
			return loc;
		}

		/** Returns the size in bytes. */
		public int getSize() {
			return size;
		}
	}

}