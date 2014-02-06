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
package org.conqat.engine.code_clones.result.annotation;

import java.util.List;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * Base class for processors that annotate the resource tree with clone-related
 * information.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: FB55DF9667C6B01C47E771F471BF0A37
 */
public abstract class CloneAnnotatorBase extends
		ConQATPipelineProcessorBase<CloneDetectionResultElement> {

	/** Maps from uniform paths to a list of clones found in the element */
	private final ListMap<String, Clone> uniformPathToClones = new ListMap<String, Clone>();

	/**
	 * Performs annotation.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	protected void processInput(CloneDetectionResultElement input)
			throws ConQATException {
		NodeUtils.addToDisplayList(input.getRoot(), getKeys());
		CloneUtils.initElementMapping(input.getList(), uniformPathToClones);

		for (ITextElement element : ResourceTraversalUtils
				.listTextElements(input.getRoot())) {
			processElement(element);
		}

		checkLeftOverClones();
	}

	/**
	 * Template method that allows deriving classes to add Keys to the root
	 * node's display list
	 */
	protected String[] getKeys() {
		return new String[] {};
	}

	/** Perform annotation on an element */
	private void processElement(ITextElement element) throws ConQATException {
		List<Clone> clonesList = uniformPathToClones.getCollection(element
				.getUniformPath());
		if (clonesList == null) {
			clonesList = CollectionUtils.emptyList();
		}

		annotateClones(element, CollectionUtils.asUnmodifiable(clonesList));
		uniformPathToClones.removeCollection(element.getUniformPath());
	}

	/**
	 * Template method that deriving classes implement to perform their
	 * annotation.
	 * 
	 * @param element
	 *            Element to which clones are annotated
	 * @param clonesList
	 *            List of clones for this element, or empty list, if no clones
	 *            are present in this element
	 * 
	 */
	protected abstract void annotateClones(ITextElement element,
			UnmodifiableList<Clone> clonesList) throws ConQATException;

	/**
	 * Check if any clones are left, for which no corresponding element has been
	 * found and generate warning messages accordingly. (This could happen, if
	 * the elements have been removed from the resource tree after clone
	 * detection.)
	 */
	protected void checkLeftOverClones() {
		for (String uniformPath : uniformPathToClones.getKeys()) {
			getLogger().warn(
					"No element found for uniform path '" + uniformPath + "'");
		}
	}
}