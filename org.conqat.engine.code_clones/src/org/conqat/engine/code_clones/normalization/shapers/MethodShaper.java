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
package org.conqat.engine.code_clones.normalization.shapers;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Revision: 36296 $
 * @ConQAT.Rating GREEN Hash: 062B98756534F0BC624F78220DD114AD
 */
@AConQATProcessor(description = ""
		+ "Inserts sentinels after method blocks. Whether a block represents a method "
		+ "is determined by its nesting-depth inside the file. "
		+ "Since the nesting depth of a method depends on the scope it is contained in, "
		+ "this class performs simple scope management. Scope keywords are set via the "
		+ "constructor.")
public class MethodShaper extends ShaperBase {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Keywords that open a new scope */
	private final Set<ETokenType> scopeKeywords = new HashSet<ETokenType>();

	/** Counter that keeps track of nesting depth */
	private final Stack<Integer> scopes = new Stack<Integer>();

	/** Stores origin of last seen token */
	private String lastTokenOrigin = null;

	/** Nesting depth at which we expect method blocks */
	private int methodDepth;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "method", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Nesting depth of method blocks")
	public void setMethodDepth(
			@AConQATAttribute(name = "depth", description = "Nesting depth of method blocks") int methodDepth) {
		this.methodDepth = methodDepth;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "scope", minOccurrences = 0, maxOccurrences = -1, description = ""
			+ "Keyword that opens a scope")
	public void addScopeKeyword(
			@AConQATAttribute(name = "keywords", description = "Examples are class, namespace, ...") ETokenType scopeKeyword) {
		scopeKeywords.add(scopeKeyword);
	}

	/** Determines whether a token opens or closes a method */
	@Override
	protected boolean isBoundary(IToken token) {
		resetDepthForNewOrigin(token);

		checkCreateScope(token);

		if (token.getType() == ETokenType.LBRACE) {
			incNestingDepth();
			return false;
		}

		if (token.getType() == ETokenType.RBRACE) {
			boolean boundary = getDepth() == methodDepth;
			decNestingDepth();
			checkTearDownScope();
			return boundary;
		}

		return false;
	}

	/** Creates a new scope, if a scope keyword is seen */
	private void checkCreateScope(IToken token) {
		if (scopeKeywords.contains(token.getType())) {
			// push new scope
			scopes.push(-1);
		}
	}

	/** Tears down a scope, if its closing bracket is seen */
	private void checkTearDownScope() {
		// don't delete the last scope
		if (getDepth() == -1 && scopes.size() > 1) {
			scopes.pop();
		}
	}

	/** Skip all tokens if we are not inside a method */
	@Override
	protected boolean skip(IToken token) {
		return getDepth() < methodDepth;
	}

	/**
	 * Resets depth counter for new file. This way, inconsistent nesting in one
	 * file does not affect subsequent files.
	 */
	private void resetDepthForNewOrigin(IToken token) {
		if (lastTokenOrigin == null
				|| !token.getOriginId().equals(lastTokenOrigin)) {
			lastTokenOrigin = token.getOriginId();
			scopes.clear();
			scopes.push(0);
		}
	}

	/** Returns depth */
	private int getDepth() {
		return scopes.peek();
	}

	/** Increments current depth counter */
	private void incNestingDepth() {
		scopes.push(scopes.pop() + 1);
	}

	/** Decrements current depth counter */
	private void decNestingDepth() {
		scopes.push(scopes.pop() - 1);
	}

}