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
package org.conqat.engine.resource.filters;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E3681004FFAC4AB213C2AC895258B271
 */
@AConQATProcessor(description = "Removes all elements that contain "
		+ "one of the given regular expressions.")
public class ContentFilter extends PatternElementFilterBase<ITextResource> {

	/** {@inheritDoc} */
	@Override
	protected boolean isFiltered(ITextResource resource) {
		if (!(resource instanceof ITextElement)) {
			return false;
		}

		if (patternList.isEmpty()) {
			return false;
		}

		ITextElement element = (ITextElement) resource;
		try {
			return patternList.findsAnyIn(element.getTextContent());
		} catch (ConQATException e) {
			getLogger().warn(
					"Could not read element's content: "
							+ element.getLocation() + ": " + e.getMessage());
			return false;
		}
	}
}