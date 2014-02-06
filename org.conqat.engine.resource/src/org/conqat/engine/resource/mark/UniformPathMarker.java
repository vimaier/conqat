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
package org.conqat.engine.resource.mark;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8A9B2010E561ECB42D61F7BEB192E6A9
 */
@AConQATProcessor(description = "Marks all elements whose uniform path matches "
		+ "one of the given regular expressions.")
public class UniformPathMarker extends ResourceMarkerBase<IResource, IElement> {

	/** {@inheritDoc} */
	@Override
	protected String getElementStringToMatch(IElement element) {
		return element.getUniformPath();
	}

	/** {@inheritDoc} */
	@Override
	protected String defaultLogCaption() {
		return "Element path";
	}
}