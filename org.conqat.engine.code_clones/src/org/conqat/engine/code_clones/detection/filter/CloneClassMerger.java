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
package org.conqat.engine.code_clones.detection.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.IdentityHashSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 06E2E07A858DC37B86DC7D2D7379E3C8
 */
@AConQATProcessor(description = ""
		+ "Merges clone classes that contain clones with identical fingerprints. "
		+ "Clone classes that contain clones with the same fingerprints are replaced with a single clone class "
		+ "that contains all clones of the formerly seperated clone classes. "
		+ "Clone class merging is required if clones contain the same code after normalization, "
		+ "but with a different distribution accross the statements of the clone. "
		+ "For example, single line if statements that contain curly braces are normalized into 2 statements, "
		+ "but only into a single statement, if they do not contain curly braces. "
		+ "This effect can lead to clone class partitioning, and can be remedied through merging. "
		+ "For gapped clone detection, it can be used to unify clone classes that share at least one common clone, "
		+ "but otherwise contain different clones.")
public class CloneClassMerger extends
		ConQATPipelineProcessorBase<CloneDetectionResultElement> {

	/** {@inheritDoc} */
	@Override
	protected void processInput(CloneDetectionResultElement input) {

		Set<CloneClass> result = mergeCloneClasses(input);

		int mergedCount = input.getList().size() - result.size();
		getLogger().debug(
				"Removed " + mergedCount + " clone classes through merging");

		// update clone classes list in detection result
		input.getList().clear();
		input.getList().addAll(result);
	}

	/** Merges clone classes. */
	private Set<CloneClass> mergeCloneClasses(CloneDetectionResultElement input) {
		Set<CloneClass> result = new IdentityHashSet<CloneClass>();
		Map<String, CloneClass> clonesMap = new HashMap<String, CloneClass>();

		for (CloneClass cloneClass : input.getList()) {
			Set<CloneClass> mergeClasses = determineMergeClasses(clonesMap,
					cloneClass);

			// merge clones into cloneclass
			if (!mergeClasses.isEmpty()) {
				result.removeAll(mergeClasses);
				addClonesToCloneClass(cloneClass, mergeClasses);
			}

			// update clones map
			for (Clone clone : cloneClass.getClones()) {
				clonesMap.put(clone.getFingerprint(), cloneClass);
			}

			result.add(cloneClass);
		}
		return result;
	}

	/** Adds clones from mergeClasses to cloneClass. */
	private void addClonesToCloneClass(CloneClass cloneClass,
			Set<CloneClass> mergeClasses) {
		for (CloneClass mergeClass : mergeClasses) {
			// put clones into set to avoid duplicates
			for (Clone clone : new HashSet<Clone>(mergeClass.getClones())) {
				cloneClass.add(clone);
			}
		}
	}

	/** Determine clone classes with which clone class needs to be merged */
	private Set<CloneClass> determineMergeClasses(
			Map<String, CloneClass> clonesMap, CloneClass cloneClass) {
		Set<CloneClass> mergeClasses = new IdentityHashSet<CloneClass>();

		for (Clone clone1 : cloneClass.getClones()) {
			CloneClass mergeClass = clonesMap.get(clone1.getFingerprint());
			if (mergeClass != null) {
				mergeClasses.add(mergeClass);
			}
		}

		return mergeClasses;
	}

}