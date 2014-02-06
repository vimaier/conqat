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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.region.RegionSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: D1927D8C012E6B8759BCDAB3CBE62F9E
 */
@AConQATProcessor(description = "Annotates each element in an resource tree with a human-readable "
		+ "description of its clones.")
public class CloneDescriptionAnnotator extends CloneAnnotatorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Contains human-readable descriptions of the clones of this elements, "
			+ "or empty list, if element has no clone", type = "java.util.List<String>")
	public static final String CLONES_DESCRIPTION_KEY = "Cloned Lines";

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { CLONES_DESCRIPTION_KEY };
	}

	/** Store description of clones at element */
	@Override
	protected void annotateClones(ITextElement element,
			UnmodifiableList<Clone> clonesList) {
		element.setValue(CLONES_DESCRIPTION_KEY, prettyPrint(clonesList));
	}

	/** Creates a human readable representation of a list of clones */
	private List<String> prettyPrint(List<Clone> clones) {
		Map<String, RegionSet> cloneRegions = initializeCloneRegions(clones);

		List<String> descriptions = new ArrayList<String>();
		for (String location : CollectionUtils.sort(cloneRegions.keySet())) {
			int clonedLines = cloneRegions.get(location).getPositionCount();
			String description = location + ": " + clonedLines;
			descriptions.add(description);
		}

		return descriptions;
	}

	/** Create mapping from location to clone region sets */
	private Map<String, RegionSet> initializeCloneRegions(List<Clone> clones) {
		Map<String, RegionSet> cloneRegions = new HashMap<String, RegionSet>();

		for (Clone clone : clones) {
			for (Clone sibling : CloneUtils.getSiblings(clone)) {
				RegionSet regions = getOrCreate(cloneRegions,
						sibling.getUniformPath());
				regions.add(new Region(sibling.getLocation().getRawStartLine(),
						sibling.getLocation().getRawEndLine()));
			}
		}

		return cloneRegions;
	}

	/**
	 * Retrieves the {@link RegionSet} for a clone file (identified by
	 * location). If no {@link RegionSet} is stored for a file, a new
	 * {@link RegionSet} is created.
	 */
	private RegionSet getOrCreate(Map<String, RegionSet> cloneRegions,
			String location) {
		if (!cloneRegions.containsKey(location)) {
			cloneRegions.put(location, new RegionSet());
		}
		return cloneRegions.get(location);
	}

}