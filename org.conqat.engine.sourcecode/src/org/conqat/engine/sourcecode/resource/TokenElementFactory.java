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

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.text.TextElementFactory;
import org.conqat.lib.scanner.ELanguage;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 339237A2F90383E73A098F889DA3AA6A
 */
@AConQATProcessor(description = "Factory for token elements.")
public class TokenElementFactory extends TextElementFactory {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "language", attribute = "name", description = "Defines programming language. ")
	public ELanguage language;

	/** {@inheritDoc} */
	@Override
	public ITokenElement create(IContentAccessor accessor) {
		return new TokenElement(accessor, encoding, language, getFilters());
	}
}