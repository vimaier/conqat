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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.statistics.KeyedData;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.lib.commons.collections.CounterSet;

/**
 * Base class for finding distribution processors.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 0121141EC2DF3EE9359ADF9F3E1D04ED
 */
public abstract class FindingDistributionProcessorBase extends
		ConQATInputProcessorBase<FindingReport> {

	/** Category names. */
	private final Set<String> categoryNames = new LinkedHashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.LOG_LEVEL_NAME, attribute = ConQATParamDoc.ATTRIBUTE_VALUE_NAME, description = ConQATParamDoc.LOG_LEVEL_DESCRIPTION
			+ " [default is WARN].", optional = true)
	public ELogLevel logLevel = ELogLevel.WARN;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "category", minOccurrences = 0, description = "Name of the findings category for which the "
			+ "distribution gets computed. If unspecified all categories are taken into account.")
	public void addCategory(
			@AConQATAttribute(name = "name", description = "Category name") String categoryName) {
		categoryNames.add(categoryName);
	}

	/** {@inheritDoc} */
	@Override
	public KeyedData<?> process() {
		if (!input.hasChildren()) {
			getLogger().log(logLevel, "Findings report is empty!");
			return new KeyedData<String>();
		}

		CounterSet<String> result = new CounterSet<String>();

		List<FindingCategory> categories = determineCategories();
		for (FindingCategory category : categories) {
			processCategory(categories, category, result);
		}

		return new KeyedData<String>(result);
	}

	/** Determine the finding categories to take into account. */
	private List<FindingCategory> determineCategories() {
		if (categoryNames.isEmpty()) {
			return Arrays.asList(input.getChildren());
		}

		List<FindingCategory> result = new ArrayList<FindingCategory>();
		for (String categoryName : categoryNames) {
			FindingCategory category = input.getCategory(categoryName);

			if (category == null) {
				getLogger().log(logLevel,
						"No finding category '" + categoryName + "' found");
			} else {
				result.add(category);
			}
		}
		return result;
	}

	/**
	 * Template method to process a category.
	 * 
	 * @param categories
	 *            List of all categories.
	 * @param category
	 *            The category to process (is present in the category list)
	 * @param result
	 *            the counter set to write results to.
	 */
	protected abstract void processCategory(List<FindingCategory> categories,
			FindingCategory category, CounterSet<String> result);

}