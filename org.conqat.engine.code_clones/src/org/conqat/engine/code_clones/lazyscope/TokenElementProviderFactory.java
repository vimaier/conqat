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

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.sourcecode.resource.ITokenElement;

/**
 * Creates a {@link TokenElementProvider}.
 * <p>
 * This class could be unified with {@link TextElementProvider} using generics.
 * However, since the ConQAT load time type checking mechanism cannot deal with
 * generics, this was deliberately not done.
 * 
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: BDFACEB7F2D04A0695980EF445086BA8
 */
@AConQATProcessor(description = "Creates a TokenElementProvider")
public class TokenElementProviderFactory extends
		ElementProviderFactoryBase<ITokenElement> {

	/** Creates a {@link TokenElementProvider} */
	@Override
	public TokenElementProvider process() {
		return new TokenElementProvider(strategies);
	}
}