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
package org.conqat.engine.code_clones.lazyscope;

import org.conqat.engine.code_clones.normalization.provider.IProvider;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * Interface for {@link ITextResource} providing components.
 * 
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 121A4F6D5F58ABBCE0768123574CCCBC
 */
public interface IElementProvider<R extends ITextResource, E extends ITextElement>
		extends IProvider<R, E, NeverThrownRuntimeException> {
	// Nothing to do
}