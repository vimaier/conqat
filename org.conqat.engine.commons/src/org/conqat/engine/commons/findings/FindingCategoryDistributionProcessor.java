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
package org.conqat.engine.commons.findings;

import java.util.List;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.CounterSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 29F8DD3F2FE16C704298761CBB47981E
 */
@AConQATProcessor(description = "Produces a KeyedData object that contains the "
		+ "distribution of findings accross categories.")
public class FindingCategoryDistributionProcessor extends
		FindingDistributionProcessorBase {

	/** {@inheritDoc} */
	@Override
	protected void processCategory(List<FindingCategory> categories,
			FindingCategory category, CounterSet<String> result) {
		result.inc(category.getName(), countFindings(category));
	}

	/** Count findings within a category. */
	private int countFindings(FindingCategory category) {
		int count = 0;
		for (FindingGroup group : category.getChildren()) {
			count += group.getChildrenSize();
		}
		return count;
	}
}
