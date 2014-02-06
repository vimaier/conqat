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
package org.conqat.engine.resource.text.filter.base;

import java.util.List;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * Base class for processors that return {@link ITextFilter}s. The
 * implementations should be immutable.
 * 
 * @author $Author: juergens $
 * @version $Rev: 40976 $
 * @ConQAT.Rating GREEN Hash: E75981BBCDC510E1792CBF0CC6FF5CF6
 */
public abstract class TextFilterBase extends ConQATProcessorBase implements
		ITextFilter, IDeepCloneable {

	/** {@inheritDoc} */
	@Override
	public ITextFilter process() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}

	/** Create debug-level logging statement with details about filtered region. */
	protected void logDeletion(Deletion deletion, String elementUniformPath) {
		getLogger().debug(
				"Filtering content region: " + elementUniformPath + ": "
						+ deletion);
	}

	/** Create a debug-level log statement with the number of filtered regions. */
	protected void logDeletions(List<Deletion> deletions,
			String elementUniformPath) {
		getLogger().debug(
				"Filtering content: " + elementUniformPath + ": "
						+ deletions.size() + " deletions");
	}
}