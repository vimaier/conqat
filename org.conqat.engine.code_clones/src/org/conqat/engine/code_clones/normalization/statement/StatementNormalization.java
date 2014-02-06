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
package org.conqat.engine.code_clones.normalization.statement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.StatementUnit;
import org.conqat.engine.code_clones.core.TokenUnit;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.detection.SentinelUnit;
import org.conqat.engine.code_clones.normalization.UnitProviderBase;
import org.conqat.engine.code_clones.normalization.token.ITokenProvider;
import org.conqat.engine.code_clones.normalization.token.TokenNormalization;
import org.conqat.engine.code_clones.normalization.token.configuration.ITokenConfiguration;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IStatementOracle;

/**
 * The {@link StatementNormalization} wraps the token normalization but
 * concatenates multiples tokens to statements.
 * 
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: A40B26C7B55135B8ADA16D459F0DD9AA
 */
public class StatementNormalization extends
		UnitProviderBase<ITokenResource, Unit> implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Normalizes tokens before the creation of statement units. */
	private final TokenNormalization normalization;

	/** Flag that determines whether the tokens of a statement are stored */
	private final boolean storeTokens;

	/** Index of the unit in its file */
	private int indexInFile;

	/** Stores last retrieved token unit */
	private TokenUnit lastToken;

	/** Maps from uniform paths to elements */
	private final Map<String, ITokenElement> elements = new HashMap<String, ITokenElement>();

	/**
	 * Creates a new {@link StatementNormalization} that does not store the
	 * underlying tokens in the StatementUnit
	 */
	public StatementNormalization(ITokenProvider tokenProvider,
			List<ITokenConfiguration> configurationList,
			ITokenConfiguration defaultConfig) {

		this(tokenProvider, configurationList, defaultConfig, false, null);
	}

	/**
	 * Create a new {@link StatementNormalization}.
	 * 
	 * @param storeTokens
	 *            If this flag is set, the underlying tokens are stored in the
	 *            {@link StatementUnit}. Be careful: This can have a tremendous
	 *            impact on memory consumption during clone detection!!
	 */
	public StatementNormalization(ITokenProvider tokenProvider,
			List<ITokenConfiguration> configurationList,
			ITokenConfiguration defaultConfig, boolean storeTokens,
			String debugFileExtension) {
		this(new TokenNormalization(tokenProvider, configurationList,
				defaultConfig, debugFileExtension), storeTokens);
	}

	/**
	 * Create a new {@link StatementNormalization}.
	 * 
	 * @param storeTokens
	 *            If this flag is set, the underlying tokens are stored in the
	 *            {@link StatementUnit}. Be careful: This can have a tremendous
	 *            impact on memory consumption during clone detection!!
	 */
	public StatementNormalization(TokenNormalization normalization,
			boolean storeTokens) {
		this.normalization = normalization;
		// make sure that, independent of how normalization is parameterized, no
		// end-of-statement-tokens are filtered out.
		normalization.setAlwaysKeepEndOfStatementTokens(true);
		this.storeTokens = storeTokens;
	}

	/** {@inheritDoc} */
	@Override
	protected void init(ITokenResource root) throws CloneDetectionException {
		normalization.init(root, getLogger());
		indexInFile = 0;
		lastToken = null;
		elements.clear();

		List<ITokenElement> tokenElements = TokenElementUtils
				.listTokenElements(root);
		for (ITokenElement tokenElement : tokenElements) {
			elements.put(tokenElement.getUniformPath(), tokenElement);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected Unit provideNext() throws CloneDetectionException {
		List<TokenUnit> statementTokens = new ArrayList<TokenUnit>();

		// determine first token in statement
		TokenUnit firstToken = findStartOfStatement();
		if (firstToken == null) {
			return null;
		}

		// handle sentinels
		if (isSentinel(firstToken)) {
			return new SentinelUnit(firstToken.getElementUniformPath());
		}

		statementTokens.add(firstToken);

		// at this point we know that we will really create a statement and
		// increment the counter for the next free unit index in this file
		int currentIndexInFile = indexInFile++;

		// determine rest of statement tokens
		TokenUnit nextToken = normalization.lookahead(1);
		while (inSameStatement(firstToken, nextToken)) {
			statementTokens.add(getNextToken());
			nextToken = normalization.lookahead(1);
		}

		return new StatementUnit(statementTokens,
				firstToken.getElementUniformPath(), storeTokens,
				currentIndexInFile);
	}

	/**
	 * Retrieves the next token from the normalization and and resets counters,
	 * if token is last in file.
	 * 
	 * Use this method to access retrieve next token to keep statement index in
	 * file up to date.
	 */
	private TokenUnit getNextToken() throws CloneDetectionException {
		TokenUnit nextToken = normalization.getNext();
		if (lastToken != null && !inSameFile(lastToken, nextToken)) {
			indexInFile = 0;
		}
		lastToken = nextToken;
		return nextToken;
	}

	/** Checks whether a token is a sentinel token */
	private boolean isSentinel(TokenUnit firstToken) {
		return firstToken.getType() == ETokenType.SENTINEL;
	}

	/**
	 * Finds the next token that starts a statement
	 * 
	 * @return {@link TokenUnit} that starts next statement, or null, if no
	 *         statement start can be found
	 */
	private TokenUnit findStartOfStatement() throws CloneDetectionException {
		TokenUnit firstToken = getNextToken();
		while (firstToken != null
				&& !firstToken.getType().equals(ETokenType.SENTINEL)
				&& isEndOfStatementToken(firstToken)) {
			firstToken = getNextToken();
		}
		return firstToken;
	}

	/** Checks whether token is end of statement token */
	private boolean isEndOfStatementToken(TokenUnit token)
			throws CloneDetectionException {
		ITokenElement element = elements.get(token.getElementUniformPath());
		CCSMAssert.isNotNull(element, "Element for token '" + token
				+ "' not found in map!");
		ELanguage language = element.getLanguage();
		IStatementOracle statementOracle = language.getStatementOracle();
		return statementOracle.isEndOfStatementToken(token.getType(),
				normalization.getTokenProvider());
	}

	/** Checks whether two tokens are part of the same statement */
	private boolean inSameStatement(TokenUnit firstUnit, TokenUnit nextUnit)
			throws CloneDetectionException {
		return nextUnit != null && !isEndOfStatementToken(nextUnit)
				&& inSameFile(firstUnit, nextUnit);
	}

	/** Checks whether two tokens units stem from the same file */
	private boolean inSameFile(TokenUnit unit1, TokenUnit unit2) {
		if (unit2 == null) {
			return false;
		}
		return unit1.inSameElement(unit2);
	}
}