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
package org.conqat.engine.sourcecode.resource;

import org.conqat.engine.resource.base.ElementTraversingProcessorBase;

/**
 * Base class for pipeline processors that traverse {@link ITokenElement}s.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35204 $
 * @ConQAT.Rating GREEN Hash: 58FC063919E314FA741657A12EC020B0
 */
public abstract class TokenElementProcessorBase extends
		ElementTraversingProcessorBase<ITokenResource, ITokenElement> {

	/** {@inheritDoc} */
	@Override
	protected Class<ITokenElement> getElementClass() {
		return ITokenElement.class;
	}

}