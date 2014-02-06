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
package org.conqat.engine.resource.scope.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 41755 $
 * @ConQAT.Rating GREEN Hash: E289E5E99F7C4913D44033CD5DFCBDBC
 */
@AConQATProcessor(description = "Filters content accessors based on patterns on the accessors' uniform paths.")
public class ContentAccessorPathFilter extends ConQATProcessorBase {

	/** The content accessors. */
	private final List<IContentAccessor> contentAccessors = new ArrayList<IContentAccessor>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "include-patterns", attribute = "ref", optional = true, description = "Patterns for included uniform paths.")
	public PatternList includePatterns = new PatternList();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "exclude-patterns", attribute = "ref", optional = true, description = "Patterns for excluded uniform paths.")
	public PatternList excludePatterns = new PatternList();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "scope", minOccurrences = 1, description = ""
			+ "Reference to the scope defining the resources to be filtered.")
	public void addContentAccessors(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IContentAccessor[] contentAccessors) {
		this.contentAccessors.addAll(Arrays.asList(contentAccessors));
	}

	/** {@inheritDoc} */
	@Override
	public IContentAccessor[] process() throws ConQATException {

		if (includePatterns.isEmpty() && excludePatterns.isEmpty()) {
			throw new ConQATException(
					"May not have both include and exclude pattern empty");
		}

		List<IContentAccessor> result = new ArrayList<IContentAccessor>();
		for (IContentAccessor accessor : contentAccessors) {
			if (isIncluded(accessor.getUniformPath())) {
				result.add(accessor);
			}
		}

		return CollectionUtils.toArray(result, IContentAccessor.class);
	}

	/** Returns whether the given name should be included. */
	private boolean isIncluded(String name) {
		return includePatterns.emptyOrMatchesAny(name)
				&& !excludePatterns.matchesAny(name);
	}
}
