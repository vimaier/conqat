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

import org.conqat.engine.code_clones.core.TokenUnit;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.code_clones.normalization.token.TokenNormalization;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.sourcecode.resource.ITokenResource;

/**
 * Creates a {@link TokenNormalization}.
 * <p>
 * Sentinels are inserted between tokens from different elements.
 * 
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 04514B48489E432F1F2EC525DD414CA8
 */
@AConQATProcessor(description = "Creates a TokenNormalization")
public class TokenNormalizationFactory extends
		TokenBasedNormalizationFactoryBase {

	/** {@inheritDoc} */
	@Override
	public IUnitProvider<ITokenResource, TokenUnit> process() {
		return new TokenNormalization(tokenProvider, configurationList,
				defaultConfiguration, debugFileExtension);
	}
}