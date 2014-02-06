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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.logging.IncludeExcludeListLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.region.RegionSet;
import org.conqat.lib.commons.string.LineOffsetConverter;

/**
 * Base class for region marker strategies. A strategy that annotates an
 * {@link ITextElement} with regions. These regions can then be used to guide
 * analyses, such as context sensitive normalization for clone detection.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36299 $
 * @ConQAT.Rating GREEN Hash: 575DD19969ADFFE7C93A47135F00E43D
 */
public abstract class RegionMarkerStrategyBase<Element extends ITextElement>
		extends ConQATProcessorBase implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The logger */
	private transient IConQATLogger logger;

	/** Name of the resulting region set */
	protected String regionSetName;

	/** Keeps track of found regions for logging message */
	private final List<String> regionDescriptors = new ArrayList<String>();

	/** Sets the name of the resulting region set */
	@AConQATParameter(name = "regions", description = "Name of the resulting region set", minOccurrences = 1, maxOccurrences = 1)
	public void setRegionSetName(
			@AConQATAttribute(name = "name", description = "Name of the resulting region set") String regionSetName) {
		this.regionSetName = regionSetName;
	}

	/** Sets the logger. */
	public void setLogger(IConQATLogger logger) {
		this.logger = logger;
	}

	/** {@inheritDoc} */
	@Override
	protected IConQATLogger getLogger() {
		return logger;
	}

	/** Determine regions that match the patterns for the element */
	public void annotate(Element element) {
		RegionSet foundRegions = new RegionSet(regionSetName);

		try {
			findRegionsForElement(element, foundRegions);
			storeRegions(element, foundRegions);
		} catch (ConQATException e) {
			getLogger().warn(
					"Could not mark regions in '" + element.getLocation()
							+ "': " + e.getMessage());
		}
	}

	/** Stores the regions in the region dictionary in the element */
	private void storeRegions(Element element, RegionSet regions)
			throws ConQATException {

		RegionSetDictionary dictionary = RegionSetDictionary
				.retrieveOrCreate(element);
		dictionary.add(regions);

		// we use the unfiltered text, since the line numbers are user-visible,
		// and the user has the original file content
		LineOffsetConverter converter = new LineOffsetConverter(
				element.getUnfilteredTextContent());
		for (Region region : regions) {
			regionDescriptors.add(createRegionDescriptor(element, region,
					converter));
		}
	}

	/** Create description message for region */
	private String createRegionDescriptor(Element element, Region region,
			LineOffsetConverter converter) throws ConQATException {
		int startLine = converter.getLine(element.getUnfilteredOffset(region
				.getStart()));
		int endLine = converter.getLine(element.getUnfilteredOffset(region
				.getEnd()));

		return element.getUniformPath() + "(" + startLine + "-" + endLine + ")";
	}

	/**
	 * Template method that allows deriving classes to implement their own
	 * search strategy
	 */
	protected abstract void findRegionsForElement(Element element,
			RegionSet result) throws ConQATException;

	/** Log information on discovered regions */
	public void logRegionInformation() {
		getLogger().info(
				new IncludeExcludeListLogMessage("regions", "Element content",
						regionDescriptors, StructuredLogTags.FILES));
	}

}