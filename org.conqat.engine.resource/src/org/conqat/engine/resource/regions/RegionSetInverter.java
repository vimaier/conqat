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
package org.conqat.engine.resource.regions;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementProcessorBase;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.region.RegionSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: DBEEC063B4EA3F3A292377B044554916
 */
@AConQATProcessor(description = "Inverts the regions annotated at a text element")
public class RegionSetInverter extends TextElementProcessorBase {

	/** Flag that determines whether empty region sets get inverted */
	private boolean invertEmptyRegionSets = false;

	/** Flag that determines whether the original region set is removed */
	private boolean originalRegionSetRemove = true;

	/** Name of the original region set */
	protected String originalRegionSetName;

	/** Name of the inverted region set */
	protected String invertedRegionSetName;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "original-regions", description = "Settings concerning the region set that gets inverted", minOccurrences = 1, maxOccurrences = 1)
	public void setOriginalRegionSetName(
			@AConQATAttribute(name = "name", description = "Name of the original region set") String regionSetName,
			@AConQATAttribute(name = "remove", description = "Flag that determines whether the original region set is removed") boolean remove) {
		originalRegionSetName = regionSetName;
		originalRegionSetRemove = remove;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "inverted-regions", description = "Name of the inverted region set", minOccurrences = 1, maxOccurrences = 1)
	public void setInvertedRegionSetName(
			@AConQATAttribute(name = "key", description = "Name of the inverted region set") String invertedRegionSetName) {
		this.invertedRegionSetName = invertedRegionSetName;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "invert", description = "Flag that determines whether empty region sets get inverted", minOccurrences = 0, maxOccurrences = 1)
	public void setInvertEmptyRegionSets(
			@AConQATAttribute(name = "empty", description = "Flag that determines whether empty region sets get inverted") boolean invertEmptyRegionSets) {
		this.invertEmptyRegionSets = invertEmptyRegionSets;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "remove", description = "Flag that determines whether the original regionset is removed", minOccurrences = 0, maxOccurrences = 1)
	public void setRemoveOriginalRegionSets(
			@AConQATAttribute(name = "original", description = "Flag that determines whether the original regionset is removed") boolean invertEmptyRegionSets) {
		this.invertEmptyRegionSets = invertEmptyRegionSets;
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITextElement element) throws ConQATException {
		RegionSet originalRegions = RegionSetDictionary.retrieve(element,
				originalRegionSetName);

		RegionSetDictionary dictionary = RegionSetDictionary
				.retrieveOrCreate(element);

		if (originalRegionSetRemove) {
			dictionary.remove(originalRegionSetName);
		}

		if (!invertEmptyRegionSets
				&& (originalRegions == null || originalRegions
						.getPositionCount() == 0)) {
			return;
		}

		RegionSet invertedRegions = invert(element, originalRegions);
		dictionary.add(invertedRegions);
	}

	/** Inverts a RegionSet */
	private RegionSet invert(ITextElement element, RegionSet originalRegions)
			throws ConQATException {

		int contentLength = element.getTextContent().length() - 1;

		RegionSet invertedRegions;
		if (originalRegions != null) {
			invertedRegions = originalRegions.createInverted(
					invertedRegionSetName, contentLength);
		} else {
			invertedRegions = new RegionSet(invertedRegionSetName);
			invertedRegions.add(new Region(0, contentLength,
					invertedRegionSetName));
		}

		return invertedRegions;
	}
}