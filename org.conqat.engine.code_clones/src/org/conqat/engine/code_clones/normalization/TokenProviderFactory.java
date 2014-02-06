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
package org.conqat.engine.code_clones.normalization;

import org.conqat.engine.code_clones.lazyscope.IElementProvider;
import org.conqat.engine.code_clones.normalization.token.ITokenProvider;
import org.conqat.engine.code_clones.normalization.token.TokenProvider;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A7F95782254CB33957DD095C8BB6D601
 */
@AConQATProcessor(description = "Creates a token provider")
public class TokenProviderFactory extends ConQATProcessorBase {

	/** Provides the elements the tokens stem from */
	private IElementProvider<ITokenResource, ITokenElement> inputProvider;

	/** Set element provider */
	@AConQATParameter(description = ConQATParamDoc.INPUT_DESC, name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1)
	public void setElementProvider(
			@AConQATAttribute(description = ConQATParamDoc.INPUT_REF_DESC, name = ConQATParamDoc.INPUT_REF_NAME) IElementProvider<ITokenResource, ITokenElement> inputProvider) {
		this.inputProvider = inputProvider;
	}

	/** {@inheritDoc} */
	@Override
	public ITokenProvider process() {
		return new TokenProvider(inputProvider);
	}
}