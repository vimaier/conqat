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

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;

/**
 * Determines regions in elements that match (one of) a set of regular
 * expressions.
 * <p>
 * The resulting regions are stored in the {@link RegionSetDictionary} at the
 * {@link ITextElement} in which the region was found.
 * 
 * @author $Author: juergens $
 * @version $Revision: 35198 $
 * @ConQAT.Rating GREEN Hash: 4F8DBF1614C499B84F5CCC3B432D760E
 */
@AConQATProcessor(description = "Determines regions in files that match (one of) a set of regular expressions.")
public class RegexRegionMarker
		extends
		RegionMarkerBase<ITextResource, ITextElement, RegexRegionMarkerStrategy> {

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

	/** {@inheritDoc} */
	@Override
	protected RegexRegionMarkerStrategy createStrategy() {
		return new RegexRegionMarkerStrategy();
	}

	/** {@inheritDoc} */
	@Override
	protected void setStrategyParameters(RegexRegionMarkerStrategy strategy) {
		strategy.setRegionParameters(regexRegionParams.patterns,
				regexRegionParams.origin,
				regexRegionParams.startRegionAtElementBegin);
	}

	/** {@inheritDoc} */
	@Override
	protected Class<ITextElement> getElementClass() {
		return ITextElement.class;
	}
}