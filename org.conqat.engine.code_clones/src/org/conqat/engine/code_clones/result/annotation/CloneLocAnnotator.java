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
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.region.RegionSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Revision: 43764 $
 * @ConQAT.Rating GREEN Hash: 943176175E8574C802E1C056CDC20568
 */
@AConQATProcessor(description = "Computes the number of lines of code of a class that are part of at "
		+ "least one clone.")
public class CloneLocAnnotator extends CloneAnnotatorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key that stores clone LoC value", type = "java.lang.Double")
	public final static String CLONE_LOC_KEY = "Clone LoC";

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { CLONE_LOC_KEY };
	}

	/** {@inheritDoc} */
	@Override
	protected void annotateClones(ITextElement element,
			UnmodifiableList<Clone> clonesList) throws ConQATException {
		element.setValue(CLONE_LOC_KEY, calcCloneLoc(element, clonesList));
	}

	/**
	 * Computes the lines of code of an element that are covered by at least one
	 * clone. This corresponds to the non-overlapping sum of the length of the
	 * clones annotated to this class.
	 */
	private int calcCloneLoc(ITextElement element, List<Clone> clones)
			throws ConQATException {
		RegionSet regions = new RegionSet();
		for (Clone clone : clones) {
			int startLine = TextElementUtils.convertRawOffsetToFilteredLine(
					element, clone.getLocation().getRawStartOffset());
			int endLine = TextElementUtils.convertRawOffsetToFilteredLine(
					element, clone.getLocation().getRawEndOffset());
			regions.add(new Region(startLine, endLine));
		}
		return regions.getPositionCount();
	}
}