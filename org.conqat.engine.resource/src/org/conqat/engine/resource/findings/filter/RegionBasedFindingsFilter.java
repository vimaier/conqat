/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.resource.findings.filter;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.filter.FindingsFilterBase;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.regions.RegionSetDictionary;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.region.RegionSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: F16265A3E9C1DD1595E871A0171DC856
 */
@AConQATProcessor(description = "Filters findings in text elements based on regions identifed by the given key. "
		+ "It can, e.g., be used to filter findings from ignored regions. "
		+ "The regions must be based on offests in the text element. "
		+ "If a region fails to be converted to a region based on lines, a ConQATException it thrown.")
public class RegionBasedFindingsFilter extends FindingsFilterBase {

	/** The ignored regions for each element */
	private RegionSet regionSet = new RegionSet();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.READKEY_NAME, attribute = ConQATParamDoc.READKEY_KEY_NAME, optional = false, description = ConQATParamDoc.READKEY_DESC)
	public String key;

	/** The origins of the regions which are evaluated. */
	private List<String> regionOrigins = new ArrayList<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "evaluated-regions", minOccurrences = 1, description = "The regions which should be evaluated by this filter,"
			+ " identified by the region origin. Findings in regions with other regions are not filtered.")
	public void addRegionOrigin(
			@AConQATAttribute(name = "origin", description = "origion of the region") String origin) {
		regionOrigins.add(origin);
	}

	/**
	 * Extracts the list of all ignored regions which should be evaluated in
	 * this filter form the given node. Only regions with the origins specified
	 * by parameter 'evaluated-regions' are evaluated. This is required for
	 * improving performance to omit checks of irrelevant regions for every
	 * finding (e. g. package statements).
	 */
	@Override
	protected void setUpOnNode(IConQATNode node) throws ConQATException {
		super.setUpOnNode(node);

		regionSet.clear();

		if (!(node instanceof ITextElement)) {
			return;
		}

		ITextElement element = (ITextElement) node;

		RegionSetDictionary dictionary = RegionSetDictionary.retrieve(element);
		if (dictionary == null) {
			return;
		}

		RegionSet elementRegionSet = dictionary.get(key);

		for (Region region : elementRegionSet) {
			if (regionOrigins.contains(region.getOrigin())) {
				Region lineRegion = TextElementUtils
						.convertFilteredOffsetRegionToRawLineRegion(element,
								region);
				regionSet.add(lineRegion);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFiltered(IConQATNode node, Finding finding) {
		for (Region region : regionSet) {
			if (FindingUtils.overlapsRawLineRegion(finding, region)) {
				getLogger().info(
						"Filtered finding: " + finding + " in "
								+ finding.getLocationString());
				return true;
			}
		}
		return false;
	}
}
