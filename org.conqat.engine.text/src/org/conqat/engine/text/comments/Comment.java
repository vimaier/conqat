/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.text.comments;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.text.comments.classification.features.MethodFinder;
import org.conqat.lib.scanner.IToken;

/**
 * Wrapper for a comment within source code. Includes context information about
 * the comment used in machine learning for comment classification.
 * 
 * @author $Author: steidl$
 * @version $Rev: 45905 $
 * @ConQAT.Rating GREEN Hash: 52095ACADFEF3010E9533DFE23F4DC73
 */
public class Comment {

	/** The comment content. */
	private final String commentString;

	/** Position of the comment in the underlying token list. */
	private final int position;

	/**
	 * The ast location value, i.e. 0 for header and copyrights, 1 for interface
	 * and 2 for inline comments.
	 */
	private final int astLocation;

	/** The underlying element. */
	private final ITokenElement element;

	/** The underlying tokens. */
	private final List<IToken> tokens;

	/**
	 * Helper class to get the next method for a comment.
	 */
	private final MethodFinder methodFinder;

	/**
	 * Constructor
	 */
	public Comment(String comment, int position, int astLocation,
			ITokenElement element, List<IToken> tokens,
			MethodFinder methodFinder) {
		this.commentString = comment;
		this.position = position;
		this.astLocation = astLocation;
		this.element = element;
		this.tokens = tokens;
		this.methodFinder = methodFinder;
	}

	/** Returns the method finder. */
	public MethodFinder getMethodFinder() {
		return methodFinder;
	}

	/**
	 * Returns the ast location value, i.e. 0 for header and copyrights, 1 for
	 * interface and 2 for inline comments.
	 */
	public int getASTLocation() {
		return astLocation;
	}

	/**
	 * Returns the position of the comment in the underlying token list.
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Returns the comment content as a string.
	 */
	public String getCommentString() {
		return commentString;
	}

	/**
	 * Returns the next tokens in the underlying token list, starting from token
	 * at position start.
	 * 
	 * @param count
	 *            the number of tokens to be returned
	 */
	public List<IToken> getNextTokens(int start, int count) {
		List<IToken> nextTokens = new ArrayList<IToken>();
		int end = Math.min(tokens.size(), start + count + 1);
		for (int i = start + 1; i < end; i++) {
			if (i < tokens.size()) {
				nextTokens.add(tokens.get(i));
			}
		}
		return nextTokens;
	}

	/**
	 * Returns the tokens of the file which contains the comment.
	 */
	public List<IToken> getTokens() {
		return tokens;
	}

	/**
	 * Returns the element which contains the comments.
	 */
	public ITokenElement getElement() {
		return element;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return commentString;
	}

	/**
	 * Returns the token for this comment.
	 */
	public IToken getToken() {
		return tokens.get(position);
	}
}
