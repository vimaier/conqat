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
package org.conqat.engine.code_clones.core.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.IdProvider;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ImmutablePair;

/**
 * Base class for classes that generate reports containing clone pairs.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 487BE5418EFE6B11D8165984AAA3571F
 */
public abstract class PairReportGeneratorBase {

	/** Id provider used to create new clone class ids */
	protected final IdProvider idProvider = new IdProvider();

	/** Create paired {@link CloneClass}es */
	public List<CloneClass> createDetectionResult(List<CloneClass> cloneClasses) {
		List<CloneClass> pairClasses = new ArrayList<CloneClass>();

		for (CloneClass cloneClass : cloneClasses) {
			createPairClasses(cloneClass, pairClasses);
		}

		return pairClasses;
	}

	/** Create pair clone classes for all clones in a clone class */
	private void createPairClasses(CloneClass cloneClass,
			List<CloneClass> pairClasses) {
		for (ImmutablePair<Clone, Clone> clonePair : CollectionUtils
				.computeUnorderedPairs(cloneClass.getClones())) {
			pairClasses.addAll(createPairClasses(clonePair));
		}
	}

	/**
	 * Template method that deriving classes override to implement pair creation
	 */
	protected abstract Collection<? extends CloneClass> createPairClasses(
			ImmutablePair<Clone, Clone> clonePair);

}