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
package org.conqat.engine.code_clones.normalization.token;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.lazyscope.IElementProvider;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.scanner.IToken;

/**
 * Provides the tokens of an element.
 * 
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating YELLOW Hash: 7ED59B627D40F1D9DFC24EED33654314
 */
public class TokenProvider extends TokenProviderBase implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The source component yielding elements. */
	private final IElementProvider<ITokenResource, ITokenElement> elementProvider;

	/**
	 * Iterator keeps track of position in the list of tokens of the element
	 * currently being processed
	 */
	private transient Iterator<IToken> tokenIterator;

	/**
	 * Create new {@link TokenProvider}
	 */
	public TokenProvider(
			IElementProvider<ITokenResource, ITokenElement> inputComponent) {
		elementProvider = inputComponent;
	}

	/** Forward initialization to element provider */
	@Override
	public void init(ITokenResource root) {
		elementProvider.init(root, getLogger());
		tokenIterator = null;
	}

	/**
	 * Returns the next token, or null, if there are no tokens left.
	 * <p>
	 * This actually works in a file by file manner. A scanner processes a full
	 * file and stores its tokens locally. Calls to this method then return
	 * stored tokens. So no performance provisions can be given on calls to this
	 * method.
	 * <p>
	 * This method is currently implemented in a recursive manner. If it should
	 * ever give performance problems or recursion stack overflow errors,
	 * consider to remove recursion.
	 * <p>
	 * {@link CloneDetectionException}s produced by the underlying scanner are
	 * logged as warnings.
	 */
	@Override
	protected IToken provideNext() throws CloneDetectionException {

		// token iterator is initialized and has more tokens
		if (tokenIterator != null && tokenIterator.hasNext()) {
			return tokenIterator.next();
		}

		// move to next file
		ITokenElement currentElement = elementProvider.getNext();

		// if there are no more files, we're done
		if (currentElement == null) {
			return null;
		}

		// get list of tokens in the current file
		List<IToken> tokens = readTokensFrom(currentElement);

		// ignore empty source files
		if (tokens.isEmpty()) {
			tokenIterator = null;
			// recursive call to get next valid token
			return getNext();
		}

		// set iterator and call recursively
		tokenIterator = tokens.iterator();
		return getNext();
	}

	/** Read all tokens from the an {@link ITokenElement} into a list. */
	private List<IToken> readTokensFrom(ITokenElement element) {
		try {
			return element.getTokens(getLogger());
		} catch (ConQATException e) {
			getLogger().warn(
					"Could not read tokens from element '"
							+ element.getLocation() + "':" + e.getMessage());
			return CollectionUtils.emptyList();
		}
	}

}