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

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.UniformPathHierarchyResourceSelectorBase;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 7AB22B809B712F573EDC31E63FEEE955
 */
@AConQATProcessor(description = "This processor filters all resources "
		+ "that are not ITextResources.")
public class TextResourceSelector extends
		UniformPathHierarchyResourceSelectorBase<ITextResource, TextContainer> {

	/** {@inheritDoc} */
	@Override
	protected boolean keepElement(IResource element) {
		return element instanceof ITextElement;
	}

	/** {@inheritDoc} */
	@Override
	protected TextContainer createRawContainer(String name) {
		return new TextContainer(name);
	}
}