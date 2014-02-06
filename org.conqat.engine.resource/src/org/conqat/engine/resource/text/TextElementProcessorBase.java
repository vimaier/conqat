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

import org.conqat.engine.resource.base.ElementTraversingProcessorBase;

/**
 * Base class for pipeline processors that traverse {@link ITextElement}s.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 94563AA519008ECAA8051E90DFA46BC8
 */
public abstract class TextElementProcessorBase extends
		ElementTraversingProcessorBase<ITextResource, ITextElement> {

	/** {@inheritDoc} */
	@Override
	protected Class<ITextElement> getElementClass() {
		return ITextElement.class;
	}
}