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
package org.conqat.engine.sourcecode.shallowparser;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.UniformPathHierarchyResourceSelectorBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenContainer;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 39096 $
 * @ConQAT.Rating GREEN Hash: 00FD6A8A2A6AEBD0DB169A45E199E3F7
 */
@AConQATProcessor(description = "This processor selects all token resources for which a shallow parser is available.")
public class ShallowParsableResourceSelector extends
		UniformPathHierarchyResourceSelectorBase<ITokenResource, TokenContainer> {

	/** {@inheritDoc} */
	@Override
	protected TokenContainer createRawContainer(String name) {
		return new TokenContainer(name);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean keepElement(IResource element) {
		if (!(element instanceof ITokenElement)) {
			return false;
		}

		return ShallowParserFactory.supportsLanguage(
		        ((ITokenElement) element).getLanguage());
	}
}
