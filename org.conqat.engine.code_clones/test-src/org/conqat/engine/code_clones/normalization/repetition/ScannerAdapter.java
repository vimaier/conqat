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
package org.conqat.engine.code_clones.normalization.repetition;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.normalization.provider.ProviderBase;
import org.conqat.engine.code_clones.normalization.token.ITokenProvider;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IScanner;
import org.conqat.lib.scanner.IToken;

/**
 * Adapts from {@link IScanner} to {@link ProviderBase}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43157 $
 * @ConQAT.Rating GREEN Hash: F9E15AC9EAE8F6247F8061E40D5E746F
 */
@SuppressWarnings("serial")
/* package */class ScannerAdapter extends
		ProviderBase<ITokenResource, IToken, CloneDetectionException> implements
		ITokenProvider {

	/** Underlying scanner */
	private final IScanner scanner;

	/** Constructor */
	public ScannerAdapter(final IScanner scanner) {
		this.scanner = scanner;
	}

	/** {@inheritDoc} */
	@Override
	protected IToken provideNext() throws CloneDetectionException {
		try {
			IToken token = scanner.getNextToken();
			if (token.getType().equals(ETokenType.EOF)) {
				return null;
			}
			return token;
		} catch (Exception e) {
			throw new CloneDetectionException("Exception from scanner: ", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void init(ITokenResource root) {
		// do nothing
	}

}