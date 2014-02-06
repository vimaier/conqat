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
package org.conqat.engine.html_presentation.util;

import java.util.ArrayList;

import org.conqat.engine.html_presentation.IPageDescriptor;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * A list of {@link IPageDescriptor}s, which can be used to bundle layout
 * results.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 38265 $
 * @ConQAT.Rating GREEN Hash: 613C9EF1DE1AC1C78C688D3C9F579CFA
 */
public class PageDescriptorList extends ArrayList<IPageDescriptor> implements
		IDeepCloneable {

	/** Serial version UID. */
	private static final long serialVersionUID = 1;

	/** {@inheritDoc} */
	@Override
	public PageDescriptorList deepClone() throws DeepCloneException {
		PageDescriptorList result = new PageDescriptorList();
		for (IPageDescriptor descriptor : this) {
			result.add(descriptor.deepClone());
		}
		return result;
	}
}
