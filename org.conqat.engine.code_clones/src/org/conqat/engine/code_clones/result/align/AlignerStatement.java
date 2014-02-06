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
package org.conqat.engine.code_clones.result.align;

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.IToken;

/**
 * Represents a statement as used in the {@link CloneAstAligner}.
 * 
 * @author $Author: goede $
 * @version $Rev: 45235 $
 * @ConQAT.Rating GREEN Hash: 9D3B424544B90A0947399E6DD45B2685
 */
/* package */class AlignerStatement {

	/** Start offset of the statement (raw, 0-based, inclusive). */
	private int rawStartOffset = -1;

	/** End offset of the statement (raw, 0-based, inclusive). */
	private int rawEndOffset = -1;

	/** Start line of the statement (raw, 1-based, inclusive). */
	private int rawStartLine = -1;

	/** End line of the statement (raw, 1-based, inclusive). */
	private int rawEndLine = -1;

	/**
	 * The index in the statement list when counting only non-artificial
	 * statements.
	 */
	private int nonArtificialStatementIndex = -1;

	/** The content of the statement used for fingerprint calculation. */
	private String content;

	/** Depth of the statement in the AST. */
	private final int depth;

	/**
	 * If this is false, the position data (lines, offsets) does not contain
	 * valid data, as this statement has no representation in the code. This may
	 * only be false for artificial statements.
	 */
	private final boolean hasPosition;

	/**
	 * Artificial statements are not really counted, but are required to close
	 * nodes in the AST (such as closing braces). Whether an artificial
	 * statement is represented in the code, is denoted by
	 * {@link #hasPosition()}. This is the case for closing braces (which are
	 * not counted as statements and hence are "artificial"). An artificial
	 * statement without a position would be the end of an if statement without
	 * braces, as the end is not marked by an explicit token in the code.
	 */
	private final boolean artificial;

	/** The type of statement. */
	private final EAlignerStatementType statementType;

	/** Constructor. */
	public AlignerStatement(List<IToken> tokens, ITokenElement element,
			int depth, boolean mayBeArtificial,
			EAlignerStatementType statementType) throws ConQATException {
		this.depth = depth;
		this.statementType = statementType;

		if (tokens.isEmpty()) {
			CCSMAssert.isTrue(mayBeArtificial,
					"Only artificial statements may be empty.");
			hasPosition = false;
		} else {
			hasPosition = true;

			IToken firstToken = tokens.get(0);
			rawStartOffset = element
					.getUnfilteredOffset(firstToken.getOffset());
			rawStartLine = element
					.convertUnfilteredOffsetToLine(rawStartOffset);

			IToken lastToken = CollectionUtils.getLast(tokens);
			rawEndOffset = element
					.getUnfilteredOffset(lastToken.getEndOffset());
			rawEndLine = element.convertUnfilteredOffsetToLine(rawEndOffset);

			content = buildContent(tokens);
		}

		artificial = mayBeArtificial && tokens.size() <= 2;
	}

	/** Creates the content based on the tokens. */
	private String buildContent(List<IToken> tokens) {
		StringBuilder builder = new StringBuilder();
		for (IToken token : tokens) {
			builder.append(token.getText());
			builder.append(StringUtils.SPACE);
		}
		return builder.toString();
	}

	/** Returns rawStartOffset. */
	public int getRawStartOffset() {
		return rawStartOffset;
	}

	/** Returns rawEndOffset. */
	public int getRawEndOffset() {
		return rawEndOffset;
	}

	/** Returns rawStartLine. */
	public int getRawStartLine() {
		return rawStartLine;
	}

	/** Returns rawEndLine. */
	public int getRawEndLine() {
		return rawEndLine;
	}

	/** Returns content. */
	public String getContent() {
		return content;
	}

	/** Returns depth. */
	public int getDepth() {
		return depth;
	}

	/** Returns the {@link #nonArtificialStatementIndex}. */
	public int getNonArtificialStatementIndex() {
		return nonArtificialStatementIndex;
	}

	/** Sets the {@link #nonArtificialStatementIndex}. */
	public void setNonArtificialStatementIndex(int nonArtificialStatementIndex) {
		this.nonArtificialStatementIndex = nonArtificialStatementIndex;
	}

	/** Returns hasPosition. */
	public boolean hasPosition() {
		return hasPosition;
	}

	/** Returns artificial. */
	public boolean isArtificial() {
		return artificial;
	}

	/** Returns statementType. */
	public EAlignerStatementType getStatementType() {
		return statementType;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return rawStartLine + "-" + rawEndLine + ": " + content;
	}
}