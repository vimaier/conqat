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
import java.util.Set;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Filters out duplicate clone classes. Duplication detection is based on clone
 * class fingerprints.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: A00CDF11A0831B92985FBC535A862EA8
 */
@AConQATProcessor(description = "Filters out duplicate clone classes.")
public class DuplicateCloneClassFilter extends CloneClassFilterBase {

	/** Set that stores seen clone class fingerprints */
	private final Set<String> seenFingerprints = new HashSet<String>();

	/** {@inheritDoc} */
	@Override
	protected boolean filteredOut(CloneClass cloneClass) {
		return !seenFingerprints.add(cloneClass.getFingerprint());
	}

}