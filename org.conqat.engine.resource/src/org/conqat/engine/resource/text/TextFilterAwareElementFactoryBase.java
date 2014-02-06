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
package org.conqat.engine.resource.text;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.resource.build.ElementFactoryBase;
import org.conqat.engine.resource.text.filter.base.ITextFilter;
import org.conqat.engine.resource.text.filter.util.TextFilterChain;

/**
 * Base class for a factory that can deal with text filtering.
 * 
 * @author deissenb
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: C88C6C75BA73755FE56B60C6A4D06FDB
 */
public abstract class TextFilterAwareElementFactoryBase extends
		ElementFactoryBase {

	/** The filters to be applied to elements created here. */
	protected TextFilterChain filters = new TextFilterChain();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "text-filter", description = "Adds a text filter that can transparently delete portions of the text (such as generated code), which are then ignored by all analyses.")
	public void addTextFilter(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ITextFilter filter) {
		filters.add(filter);
	}

	/**
	 * Returns the stack of filters to be used (or null if the filter stack is
	 * empty).
	 */
	protected TextFilterChain getFilters() {
		if (filters.isEmpty()) {
			return null;
		}
		return filters;
	}
}