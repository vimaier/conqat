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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.region.RegionSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Revision: 36296 $
 * @ConQAT.Rating GREEN Hash: 9DB1E35B2ED1CAF7B77B6EAD64B8CA37
 */
@AConQATProcessor(description = "Determines regions in text elements that match (one of) a set of regular expressions.")
public class RegexRegionMarkerStrategy extends
		TextElementRegionMarkerStrategyBase implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Parameter object */
	private RegexRegionParameters regexRegionParams;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.REGEX_REGIONS_NAME, description = ConQATParamDoc.REGEX_REGIONS_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setRegionParameters(
			@AConQATAttribute(name = ConQATParamDoc.REGEX_REGIONS_PATTERNS_NAME, description = ConQATParamDoc.REGEX_REGIONS_PATTERNS_DESC) PatternList patterns,
			@AConQATAttribute(name = ConQATParamDoc.REGEX_REGIONS_ORIGIN_NAME, description = ConQATParamDoc.REGEX_REGIONS_ORIGIN_DESC) String origin,
			@AConQATAttribute(name = ConQATParamDoc.REGEX_REGIONS_START_AT_FILE_BEGIN_NAME, description = ConQATParamDoc.REGEX_REGIONS_START_AT_FILE_BEGIN_DESC) boolean startRegionAtFileBegin) {
		regexRegionParams = new RegexRegionParameters(patterns, origin,
				startRegionAtFileBegin);
	}

	/**
	 * Determine the regions that are matched by the list of regular expressions
	 * of this processor.
	 */
	@Override
	protected void findRegionsForElement(ITextElement element,
			RegionSet resultRegions) {

		try {
			// determine all matching regions
			String content = element.getTextContent();
			for (Pattern pattern : regexRegionParams.patterns) {
				findRegionsForPattern(content, pattern, resultRegions);
			}

			// if startRegionAtElementBegin is set, merge all regions
			if (regexRegionParams.startRegionAtElementBegin
					&& !resultRegions.isEmpty()) {
				mergeRegions(resultRegions);
			}
		} catch (ConQATException e) {
			getLogger().warn("Could not read '" + element.getLocation() + "'",
					e);
		}
	}

	/**
	 * Determines the regions in an element that are matched by a pattern.
	 * 
	 * @param pattern
	 *            Pattern that gets searched for
	 * @param resultRegions
	 *            RegionSet into which found regions are stored in
	 */
	private void findRegionsForPattern(String content, Pattern pattern,
			RegionSet resultRegions) {
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			int start = matcher.start();
			Region region = new Region(start, matcher.end(),
					regexRegionParams.origin);
			resultRegions.add(region);
		}
	}

	/**
	 * Merges individual regions into a single region that starts at element
	 * begin and ends at the last character matched by any region.
	 * 
	 * @param resultRegions
	 *            Set of regions that gets merged.
	 */
	private void mergeRegions(RegionSet resultRegions) {
		int end = 0;
		for (Region region : resultRegions) {
			end = Math.max(end, region.getEnd());
		}

		resultRegions.clear();
		resultRegions.add(new Region(0, end, regexRegionParams.origin));
	}

}