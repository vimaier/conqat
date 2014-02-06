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
package org.conqat.engine.code_clones.normalization.provider;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.conqat.engine.code_clones.normalization.token.TokenProviderBase;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.scanner.IToken;

/**
 * Provides tokens from a list.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36296 $
 * @ConQAT.Rating GREEN Hash: 7B50C4E9790A0BF05492BC8AC96CA778
 */
public class ListBasedTokenProvider extends TokenProviderBase implements
		Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;
	/** Iterator that keeps track of position in tokens list */
	private Iterator<IToken> tokenIterator;

	/** Constructor */
	public ListBasedTokenProvider(List<IToken> tokens) {
		tokenIterator = tokens.iterator();
	}

	/** {@inheritDoc} */
	@Override
	protected void init(ITokenResource root) {
		// Do nothing
	}

	/** {@inheritDoc} */
	@Override
	protected IToken provideNext() {
		if (tokenIterator != null && tokenIterator.hasNext()) {
			return tokenIterator.next();
		}

		tokenIterator = null;
		return null;
	}
}