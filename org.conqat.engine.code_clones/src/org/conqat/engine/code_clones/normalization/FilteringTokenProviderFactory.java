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
package org.conqat.engine.code_clones.normalization;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.code_clones.normalization.token.FilteringTokenProvider;
import org.conqat.engine.code_clones.normalization.token.ITokenProvider;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.region.RegionSet;

/**
 * Filters the tokens provided by another token provider according to the filter
 * regions stored in the corresponding elements.
 * 
 * @author Elmar Juergens
 * @author $Author: juergens $
 * 
 * @version $Revision: 34644 $
 * @ConQAT.Rating GREEN Hash: CBA2F31B1551391476BD6EE74A5975D2
 */
@AConQATProcessor(description = "Creates an ITokenProvider that only returns tokens that are not contained in a filtered region.")
public class FilteringTokenProviderFactory extends ConQATProcessorBase {

	/** Token provider whose tokens are filtered */
	private ITokenProvider tokenProvider;

	/**
	 * Names of the {@link RegionSet}s that for which matching tokens are
	 * filtered out.
	 */
	private final Set<String> ignoreRegionSetNames = new HashSet<String>();

	/** Token ignore patters. */
	private PatternList ignorePatterns = new PatternList();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
	public void setTokenProvider(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ITokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ignore-region-set", minOccurrences = 1, description = ""
			+ "Adds the name of a region set for which tokens are filtered out")
	public void addFilteredRegionsName(
			@AConQATAttribute(name = "name", description = "Name of the region set") String ignoreRegionSetName) {
		ignoreRegionSetNames.add(ignoreRegionSetName);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ignore-patterns", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Tokens that are matched by one of the patterns are ignored")
	public void setIgnorePatterns(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) PatternList ignorePatterns) {
		this.ignorePatterns = ignorePatterns;
	}

	/** Creates a FilteringTokenProvider */
	@Override
	public ITokenProvider process() {
		return new FilteringTokenProvider(tokenProvider, ignoreRegionSetNames,
				ignorePatterns);
	}

}