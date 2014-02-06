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
package org.conqat.engine.code_clones.core.constraint;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.CounterSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 3E213C92D4FB94C5DF15B200B0E36158
 */
@AConQATProcessor(description = ""
		+ "Constraint that is satisfied if at least 2 clones of the clone class are in the same file")
public class SameFileConstraint extends ConstraintBase {

	/** Constant that indicates that all clones need to be in the same file */
	private static final int ALL = -1;

	/** Number of clones that need to be in the same file */
	private int minCount = 2;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "min", description = "Number of clones that need to be in the same file for the constraint to be satisfied")
	public void setMinCount(
			@AConQATAttribute(name = "count", description = "Default is 2. Use "
					+ ALL
					+ " to indicate that all clones need to be in same file") int minCount) {
		this.minCount = minCount;
	}

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) {
		int maxClonesInSameFile = maxClonesInSameFile(cloneClass);

		if (minCount == ALL) {
			return maxClonesInSameFile == cloneClass.size();
		}

		return maxClonesInSameFile >= minCount;
	}

	/** Determine maximal number of clones in same file */
	private int maxClonesInSameFile(CloneClass cloneClass) {
		// count clones per file
		CounterSet<String> cloneCount = new CounterSet<String>();
		for (Clone clone : cloneClass.getClones()) {
			cloneCount.inc(clone.getUniformPath());
		}

		// determine max count
		int maxClonesInSameFile = 0;
		for (String originId : cloneCount.getKeys()) {
			maxClonesInSameFile = Math.max(maxClonesInSameFile,
					cloneCount.getValue(originId));
		}

		return maxClonesInSameFile;
	}
}