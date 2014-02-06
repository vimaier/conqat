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
package org.conqat.engine.code_clones.normalization.token;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.normalization.shapers.SentinelToken;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.regions.RegionSetDictionary;
import org.conqat.engine.resource.text.filter.base.ITextFilter;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.region.RegionSet;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Token provider that filters tokens based on the filter region information
 * stored in the IFileSystemElements the tokens originated from, based on an
 * optional pattern list against which tokens are matched and based on filtered
 * text excluded through {@link ITextFilter}s.
 * 
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: F421ABDB7119D414E42DE64C94EEAC30
 */
public class FilteringTokenProvider extends TokenProviderBase implements
		Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Default name of region set of ignored tokens */
	public static final String IGNORE = "ignore";

	/** Source of the tokens that are filtered */
	private final ITokenProvider tokenProvider;

	/** Maps uniform paths to their filtered regions */
	private transient ListMap<String, RegionSet> uniformPathToFilteredregions;

	/** Ignore patterns for tokens */
	private final PatternList ignorePatterns;

	/** Element that needs to be initialized */
	private ITokenResource toInit = null;

	/** The last token that was returned. */
	private IToken lastToken;

	/**
	 * Names of the {@link RegionSet}s that for which matching tokens are
	 * filtered out.
	 */
	private final Set<String> ignoreRegionSetNames;

	/** Map from uniform paths to elements */
	private Map<String, ITokenElement> uniformPathToElement;

	/**
	 * Default constructor. Ignores tokens from default ignore set
	 * {@value #IGNORE} and empty ignore patterns.
	 * 
	 * @param tokenProvider
	 *            Source of unfiltered tokens.
	 */
	public FilteringTokenProvider(ITokenProvider tokenProvider) {
		this(tokenProvider, CollectionUtils.asHashSet(new String[] { IGNORE }),
				new PatternList());
	}

	/**
	 * Creates a {@link FilteringTokenProvider}
	 * 
	 * @param tokenProvider
	 *            Source of unfiltered tokens.
	 * 
	 * @param ignoreRegionSetNames
	 *            Name of the {@link RegionSet} for which matching tokens get
	 *            ignored
	 * 
	 * @param ignorePatterns
	 *            Tokens that are matched by these patterns are ignored.
	 */
	public FilteringTokenProvider(ITokenProvider tokenProvider,
			Set<String> ignoreRegionSetNames, PatternList ignorePatterns) {
		this.tokenProvider = tokenProvider;
		this.ignoreRegionSetNames = ignoreRegionSetNames;
		this.ignorePatterns = ignorePatterns;
	}

	/** Returns next token that is not filtered out */
	@Override
	protected IToken provideNext() throws CloneDetectionException {
		if (toInit != null) {
			initRegionMap(toInit);
			toInit = null;
		}

		IToken token = tokenProvider.lookahead(1);
		if (lastToken != null && token != null
				&& ETokenType.SENTINEL != lastToken.getType()
				&& gapBefore(token)) {
			token = createSentinelToken();
		} else {
			token = tokenProvider.getNext();

			// We replace the current token with a sentinel. It thus never gets
			// returned. This is ok, since its an ignored token anyway.
			if (ignoredRegionStarts(token)) {
				token = createSentinelToken();
			}

			while (token != null && isFilteredOut(token)) {
				token = tokenProvider.getNext();
			}
		}

		lastToken = token;
		return token;
	}

	/**
	 * Checks if there is a filtered gap between the token and the lastToken in
	 * the raw content
	 */
	private boolean gapBefore(IToken token) throws CloneDetectionException {
		if (!lastToken.getOriginId().equals(token.getOriginId())) {
			return false;
		}

		ITokenElement element = uniformPathToElement.get(token.getOriginId());
		try {
			return element.isFilterGapBetween(lastToken.getOffset(),
					token.getOffset());
		} catch (ConQATException e) {
			throw new CloneDetectionException(
					"Could not determine gap existence: ", e);
		}
	}

	/** Check whether an ignored region starts. */
	private boolean ignoredRegionStarts(IToken token) {
		if (lastToken != null && token != null) {
			return !isFilteredOut(lastToken) && isFilteredOut(token);
		}
		return false;
	}

	/** Insert a sentinel token for an ignore region. */
	private IToken createSentinelToken() {
		return SentinelToken.createSentinelAfter(lastToken);
	}

	/** Returns true, if a token should be filtered out */
	private boolean isFilteredOut(IToken token) {
		int tokenOffset = token.getOffset();

		List<RegionSet> ignoredRegionSets = uniformPathToFilteredregions
				.getCollection(token.getOriginId());
		if (ignoredRegionSets != null) {
			for (RegionSet ignoredRegionSet : ignoredRegionSets) {
				if (ignoredRegionSet.contains(tokenOffset)) {
					return true;
				}
			}
		}

		return ignorePatterns.matchesAny(token.getText());
	}

	/** {@inheritDoc} */
	@Override
	protected void init(ITokenResource root) throws CloneDetectionException {
		// init token provider
		tokenProvider.init(root, getLogger());

		toInit = root;
		lastToken = null;
	}

	/** Init region map for element */
	private void initRegionMap(ITokenResource root) {
		uniformPathToFilteredregions = new ListMap<String, RegionSet>();

		// init map that maps from element id to set of filtered regions
		uniformPathToElement = ResourceTraversalUtils
				.createUniformPathToElementMap(root, ITokenElement.class);

		for (String file : uniformPathToElement.keySet()) {
			ITokenElement element = uniformPathToElement.get(file);

			RegionSet filteredRegions;
			try {
				for (String ignoreRegionSetName : ignoreRegionSetNames) {
					filteredRegions = RegionSetDictionary.retrieve(element,
							ignoreRegionSetName);
					if (filteredRegions != null) {
						uniformPathToFilteredregions.add(file, filteredRegions);
					}
				}
			} catch (ConQATException e) {
				getLogger().warn(
						"Skipping element '" + element.getId() + "': "
								+ e.getMessage());
			}

		}
	}
}