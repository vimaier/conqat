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
package org.conqat.engine.resource.scope;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;

/**
 * Base class for processors working with include and exclude patterns
 * represented as Strings.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40883 $
 * @ConQAT.Rating GREEN Hash: 027354CC54BFDA02AE4DE03F30AA2971
 */
public abstract class PatternProcessorBase extends ConQATProcessorBase {

	/** The patterns that are included. */
	protected final List<String> includePatterns = new ArrayList<String>();

	/** The patterns that are excluded. */
	protected final List<String> excludePatterns = new ArrayList<String>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "case-sensitive", attribute = "value", description = "Set case-sensitivity for "
			+ "file scanning [case-sensitive by default].", optional = true)
	public boolean caseSensitive = true;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INCLUDE_NAME, minOccurrences = 1, description = ""
			+ "Add a pattern for elements to include. At least one pattern is required. If everything should be included, use '**'.")
	public void addIncludePattern(
			@AConQATAttribute(name = ConQATParamDoc.ANT_PATTERN_NAME, description = ConQATParamDoc.ANT_PATTERN_DESC) String pattern) {
		includePatterns.add(pattern);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.EXCLUDE_NAME, description = ""
			+ "Add a pattern for elements to exclude.")
	public void addExcludePattern(
			@AConQATAttribute(name = ConQATParamDoc.ANT_PATTERN_NAME, description = ConQATParamDoc.ANT_PATTERN_DESC) String pattern) {
		excludePatterns.add(pattern);
	}
}
