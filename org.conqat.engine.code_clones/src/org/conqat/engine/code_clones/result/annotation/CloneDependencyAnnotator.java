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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: BAD2D6E7492E201F23BE3E143FCB0ADB
 */
@AConQATProcessor(description = "Annotates each file in the resource tree with a list of the files to which "
		+ "it has clone dependencies. These dependency lists can be used to create "
		+ "graph representations of the clone relationships using the ConQAT Graph bundle.")
public class CloneDependencyAnnotator extends CloneAnnotatorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Holds list of element ids with which this element has "
			+ "cloning relations, or empty list, if it has no cloning relations", type = "java.util.List<String>")
	public static final String CLONE_DEPENDENCY_LIST_KEY = "Clone Dependency";

	/** Mapping from uniform paths to IDs. */
	private Map<String, String> pathToIdMap;

	/** {@inheritDoc} */
	@Override
	protected void processInput(CloneDetectionResultElement input)
			throws ConQATException {
		pathToIdMap = ResourceTraversalUtils.createUniformPathToIdMapping(
				input, ITextElement.class);
		super.processInput(input);
	}

	/** Stores the dependency list at an element */
	@Override
	protected void annotateClones(ITextElement element,
			UnmodifiableList<Clone> clonesList) {
		List<String> fileDependencies = createFileDependencies(clonesList);

		element.setValue(CLONE_DEPENDENCY_LIST_KEY, fileDependencies);
	}

	/** Create a list of the files involved in list of clones */
	private List<String> createFileDependencies(List<Clone> clonesList) {
		Set<String> ids = new HashSet<String>();

		if (!clonesList.isEmpty()) {
			// all all sibling files
			for (Clone clone : clonesList) {
				// we tolerate creation of self loops here, since we remove them
				// below anyway
				for (Clone sibling : clone.getCloneClass().getClones()) {
					ids.add(pathToIdMap.get(sibling.getUniformPath()));
				}
			}

			// remove self loops
			ids.remove(pathToIdMap.get(CollectionUtils.getAny(clonesList)
					.getUniformPath()));
			ids.remove(null);
		}

		return new ArrayList<String>(ids);
	}

}