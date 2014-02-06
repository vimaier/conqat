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
package org.conqat.engine.dotnet.analysis;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.ElementAnalyzerBase;
import org.conqat.engine.resource.regions.RegionSetDictionary;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.region.RegionSet;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * The keys in which valid and ignored loc are parameterized, since the reason
 * why code gets ignored could vary between different use cases. The use case
 * might be reflected in the key name. By default, we assume that ignored code
 * is generated code.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35167 $
 * @ConQAT.Rating GREEN Hash: BDD4727BA5665BDD13B887BA91FEB197
 */
@AConQATProcessor(description = "Counts lines of code. Lines in regions marked as ignored are not counted.")
public class IgnoreAwareLOCAnalyzer extends
		ElementAnalyzerBase<ITextResource, ITextElement> {

	/** Default value under which ignored loc are stored */
	private static final String DEFAULT_LOC_IGNORED_KEY = "LoCG";

	/** Default value under which valid loc are stored */
	private static final String DEFAULT_LOC_VALID_KEY = "LoCM";

	/** Key in which ignore flags are stored */
	private final Set<String> ignoreKeys = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "filter", description = "Determines whether elements and regions that are marked as ignored, "
			+ "e.g. because they are in genereated code, are ignored.", minOccurrences = 0, maxOccurrences = -1)
	public void setFilterIgnored(
			@AConQATAttribute(name = "key", description = "Key that contains ignore flags. It is evaluated in two ways: "
					+ "1) elements that are marked as ignore are ignored entirely."
					+ "2) lines in regions with that name are ignored.", defaultValue = "ignore") String ignoreKey)
			throws ConQATException {

		if (StringUtils.isEmpty(ignoreKey)) {
			throw new ConQATException("Ignore key must not be empty");
		}

		ignoreKeys.add(ignoreKey);
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "loc-ignored", attribute = "key", description = "Key in which ignored LOC are stored.", optional = true)
	public String keyIgnoredLoc = DEFAULT_LOC_IGNORED_KEY;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "loc-valid", attribute = "key", description = "Key in which LOC that are not ignored are stored.", optional = true)
	public String keyValidLoc = DEFAULT_LOC_VALID_KEY;

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { keyValidLoc, keyIgnoredLoc };
	}

	/** Determines lines of code. */
	@Override
	protected void analyzeElement(ITextElement element) throws ConQATException {
		int loc = TextElementUtils.countLOC(element);

		int locIgnored = 0;
		int locValid = 0;
		if (ResourceTraversalUtils.isIgnored(element, ignoreKeys)) {
			locIgnored = loc;
			getLogger().debug(
					"Ignoring " + locIgnored + " " + element + " completely");
		} else {
			locIgnored = countIgnoredLoc(element);
			if (locIgnored > 0) {
				getLogger().debug(
						"Ignoring " + locIgnored + " LOC in " + element);
			}
			locValid = loc - locIgnored;
		}

		CCSMAssert.isFalse(locIgnored < 0, "Negative ignored LOC counted!");
		CCSMAssert.isFalse(locValid < 0, "Negative valid LOC counted!");

		element.setValue(keyValidLoc, locValid);
		element.setValue(keyIgnoredLoc, locIgnored);
	}

	/** Counts loc in regins of ignored code */
	private int countIgnoredLoc(ITextElement element) throws ConQATException {
		RegionSetDictionary dictionary = RegionSetDictionary.retrieve(element);
		if (dictionary == null) {
			return 0;
		}

		String content = element.getTextContent();

		int ignoredLoc = 0;
		RegionSet union = new RegionSet();
		for (String ignoreKey : ignoreKeys) {
			RegionSet regions = dictionary.get(ignoreKey);
			union.addAll(regions);
		}
		union = mergeRegions(content, union);
		ignoredLoc += countRegionsLoc(content, union);
		return ignoredLoc;
	}

	/** Merges overlapping regions into a single one */
	private RegionSet mergeRegions(String content, RegionSet union) {
		String name = "ignore";
		int length = content.length();
		union = union.createInverted(name, length);
		union = union.createInverted(name, length);
		return union;
	}

	/**
	 * Count LOC for region. If region contains no line break, 0 gets returned
	 * to avoid that a single line in an element can contribute to more than 1
	 * loc.
	 * 
	 * Regions are expected to not overlap.
	 */
	private int countRegionsLoc(String elementContent, RegionSet regions) {
		if (regions == null) {
			return 0;
		}

		int ignoredLoc = 0;
		for (Region region : regions) {
			String regionContent = elementContent.substring(region.getStart(),
					region.getEnd() + 1);
			if (regionContent.contains("\r") || regionContent.contains("\n")) {
				ignoredLoc += StringUtils.splitLines(regionContent).length;
			}
		}
		return ignoredLoc;
	}

}