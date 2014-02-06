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

import org.conqat.engine.code_clones.normalization.statement.StatementNormalization;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Creates a {@link StatementNormalization}.
 * 
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 07E1895AB58DE98CE3FB3649BEB51CCF
 */
@AConQATProcessor(description = "Creates a StatementNormalization")
public class StatementNormalizationFactory extends
		TokenBasedNormalizationFactoryBase {

	/** Flag that determines whether underlying tokens are stored */
	private boolean storeTokens = false;

	/** ConQAT Parameter */
	@AConQATParameter(name = "store", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Flag that determines whether underlying tokens are stored")
	public void setStoreTokens(
			@AConQATAttribute(name = "tokens", description = "Default is false") boolean storeTokens) {
		this.storeTokens = storeTokens;
	}

	/** {@inheritDoc} */
	@Override
	public StatementNormalization process() {
		return new StatementNormalization(tokenProvider, configurationList,
				defaultConfiguration, storeTokens, debugFileExtension);
	}

}