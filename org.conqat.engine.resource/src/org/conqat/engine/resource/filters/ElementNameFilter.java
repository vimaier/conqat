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
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 9633236F367B011950F499BDEA92A807
 */
@AConQATProcessor(description = "Removes all elements whose uniform path matches "
		+ "one of the given regular expressions.")
public class ElementNameFilter extends PatternElementFilterBase<IResource> {

	/** Remove files. */
	@Override
	protected boolean isFiltered(IResource node) {
		if (!(node instanceof IElement)) {
			return false;
		}

		IElement element = (IElement) node;
		if (patternList.findsAnyIn(element.getUniformPath())) {
			return true;
		}

		return false;
	}
}