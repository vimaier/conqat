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
package org.conqat.engine.commons.findings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.CounterSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38372 $
 * @ConQAT.Rating GREEN Hash: CD9B202419CA833C02CE43A147CBC5D0
 */
@AConQATProcessor(description = "Produces a KeyedData object that contains the "
		+ "distribution of findings accross groups.")
public class FindingGroupDistributionProcessor extends
		FindingDistributionProcessorBase {

	/** Folded category names. */
	private final Map<String, String> foldedCategoryNames = new HashMap<String, String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "fold", minOccurrences = 0, description = "This allows to fold findings groups in a category into one element in "
			+ "the result. This e.g. useful for clones.")
	public void addFoldedCategory(
			@AConQATAttribute(name = "category", description = "Category name") String categoryName,
			@AConQATAttribute(name = "replacement", description = "String used as replacement for the group name.") String replacement) {
		foldedCategoryNames.put(categoryName, replacement);
	}

	/** {@inheritDoc} */
	@Override
	protected void processCategory(List<FindingCategory> categories,
			FindingCategory category, CounterSet<String> result) {
		for (FindingGroup group : category.getChildren()) {
			String name = determineName(group);

			// group names are only unique within a category
			if (categories.size() > 1) {
				name = category.getName() + "/" + name;
			}

			result.inc(name, group.getChildrenSize());
		}
	}

	/** Determine the group name. */
	private String determineName(FindingGroup group) {
		String categoryName = group.getParent().getName();
		if (foldedCategoryNames.containsKey(categoryName)) {
			return foldedCategoryNames.get(categoryName);
		}
		return group.getName();
	}
}