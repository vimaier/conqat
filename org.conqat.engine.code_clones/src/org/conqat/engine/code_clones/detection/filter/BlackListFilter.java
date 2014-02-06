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
package org.conqat.engine.code_clones.detection.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author Elmar Juergens
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 7B08B4BA86B529759423B3B396CCB4C4
 */
@AConQATProcessor(description = "Filters out clone classes based on their fingerprints"
		+ "Blacklists are simply text elements that contain a fingerprint on each"
		+ "line. In order to enable flexible integration into a continuous build"
		+ "environment, this processor takes a blacklist root ITextResource"
		+ "as input and processes all elements that are located under it as black lists."
		+ "This way, a user only needs to copy a"
		+ "blacklist into the blacklist root directory, and it will be considered"
		+ "by the next detection run."
		+ "The blacklist root parameter is optional on purpose. This way, it can be used"
		+ "as an optional parameter in a clone detection block. If it is not set,"
		+ "blacklisting is simply deactivated.")
public class BlackListFilter extends CloneClassFilterBase {

	/** Set of fingerprints of clone classes that get filtered out */
	private final Set<String> blacklist = new HashSet<String>();

	/** Root of blacklist resource hierarchy */
	private ITextResource blacklistRoot = null;

	/** ConQAT Parameter */
	@AConQATParameter(name = "blacklist", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Resource under which blacklists are located")
	public void addFingerprintDirectory(
			@AConQATAttribute(name = "root", description = "If not set, blacklisting is deactivated") ITextResource blacklistRoot) {
		this.blacklistRoot = blacklistRoot;
	}

	/** Load the blacklisted {@link CloneClass} fingerprints */
	@Override
	protected void setUp(CloneDetectionResultElement input)
			throws ConQATException {
		// if not set, skip
		if (blacklistRoot == null) {
			return;
		}

		List<ITextElement> blacklists = ResourceTraversalUtils
				.listTextElements(blacklistRoot);
		for (ITextElement blacklistElement : blacklists) {
			readBlacklist(blacklistElement);
		}
	}

	/** Reads all contained fingerprints from a blacklist */
	private void readBlacklist(ITextElement blacklistElement)
			throws ConQATException {
		getLogger().info("Reading blacklist: " + blacklistElement);

		String[] fingerprints = TextElementUtils.getLines(blacklistElement);
		if (fingerprints.length == 0) {
			// in order to provide graceful behaviour in a nightly
			// build, we warn if an empty blacklist element is found,
			// and raise no error.
			getLogger().warn("Empty blacklist found: " + blacklistElement);
			return;
		}

		for (String fingerprint : fingerprints) {
			if (!StringUtils.isEmpty(fingerprint)) {
				blacklist.add(fingerprint);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected boolean filteredOut(CloneClass cloneClass) {
		return blacklist.contains(cloneClass.getFingerprint());
	}

}