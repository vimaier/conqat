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
package org.conqat.engine.text.identifier;

import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementUtils;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for processors working on identifiers.
 * 
 * @param <T>
 *            the return type of this processor.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35207 $
 * @ConQAT.Rating GREEN Hash: 69177F56B4A8847260A2E8EDB12F972C
 */
public abstract class IdentifierProcessorBase<T> extends
		ConQATInputProcessorBase<ITokenResource> {

	/** {@inheritDoc} */
	@Override
	public T process() throws ConQATException {
		for (ITokenElement element : TokenElementUtils.listTokenElements(input)) {
			processElement(element);
		}
		return obtainResult();
	}

	/** Template method that calculates and returns the processor's result. */
	protected abstract T obtainResult();

	/** Process a single source element. */
	private void processElement(ITokenElement element) throws ConQATException {
		for (IToken token : element.getTokens(getLogger())) {
			if (token.getType().getTokenClass() == ETokenClass.IDENTIFIER) {
				processIdentifier(token.getText());
			}
		}
	}

	/** Template method that is called for each identifier. */
	protected abstract void processIdentifier(String identifier);
}