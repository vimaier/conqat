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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.TokenUnit;
import org.conqat.engine.code_clones.detection.SentinelUnit;
import org.conqat.engine.code_clones.normalization.UnitProviderBase;
import org.conqat.engine.code_clones.normalization.token.configuration.ITokenConfiguration;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.regions.RegionSetDictionary;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.IdManager;
import org.conqat.lib.commons.collections.TwoDimHashMap;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.region.RegionSet;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * According to the specified configuration, this class applies normalizing
 * transformations to the tokens. Some tokens can be swallowed entirely.
 * <p>
 * Additionally, this component performs the conversion from scanner tokens
 * {@link org.conqat.lib.scanner.IToken} to tokens units
 * {@link org.conqat.engine.code_clones.core.TokenUnit}.
 * 
 * @author $Author: juergens $
 * @version $Revision: 44685 $
 * @ConQAT.Rating GREEN Hash: F222DAF26435E2F82371A141D15E20C5
 */
public class TokenNormalization extends
		UnitProviderBase<ITokenResource, TokenUnit> implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Number of upcoming tokens that are to be ignored */
	private int ignoreNextTokens = 0;

	/** The provider that yields the tokens that get normalized. */
	private final ITokenProvider tokenProvider;

	/**
	 * The {@link ITokenConfiguration} that determines how normalization is
	 * performed per default
	 */
	private final ITokenConfiguration defaultConfig;

	/**
	 * List of configurations used for normalization. Order of configuration
	 * names determines preference, in case more than a one configuration
	 * matches a token.
	 */
	private final Map<String, ITokenConfiguration> configurations = new LinkedHashMap<String, ITokenConfiguration>();

	/**
	 * Maps from a uniform path and a configuration name to the
	 * {@link RegionSet} that determines for which parts of the element the
	 * configuration holds.
	 */
	private transient TwoDimHashMap<String, String, RegionSet> configurationMap;

	/** Index of unit in its file */
	private int indexInFile = 0;

	/** Maps from tokens to their normalized units. Used to write debug files */
	private final Map<IToken, TokenUnit> normalization = new LinkedHashMap<IToken, TokenUnit>();

	/** Flag that can be set to turn off filtering of end of statement tokens */
	private boolean alwaysKeepEndOfStatementTokens = false;

	/** Resource that needs to be initialized */
	private ITokenResource toInit = null;

	/**
	 * Mapping from uniform path to location. This is only used for specific
	 * cases (e.g. writing debug files) and thus initialized in a lazy fashion.
	 * This should only be accessed via {@link #getElement(String)}.
	 */
	private transient Map<String, ITokenElement> uniformPathToElement;

	/**
	 * This manager is used for normalizing identifiers. Within a statement same
	 * identifiers are normalized with the same id. Example:
	 * 
	 * <pre>
	 * b = a * a;
	 * </pre>
	 * 
	 * becomes
	 * 
	 * <pre>
	 * id0 = id1 * id1;
	 * </pre>
	 */
	private transient IdManager<String> identifierManager;

	/**
	 * If this string is set to a non-empty value, a debug file (containing the
	 * normalized units) is written for each input file.
	 */
	private final String debugFileExtension;

	/**
	 * Create new {@link TokenNormalization} that does not write debug
	 * information
	 */
	public TokenNormalization(ITokenProvider tokenProvider,
			List<ITokenConfiguration> configurationList,
			ITokenConfiguration defaultConfig) {
		this(tokenProvider, configurationList, defaultConfig, null);
	}

	/**
	 * Create new {@link TokenNormalization} that optionally writes debug
	 * information
	 */
	public TokenNormalization(ITokenProvider tokenProvider,
			List<ITokenConfiguration> configurationList,
			ITokenConfiguration defaultConfig, String debugFileExtension) {
		this.tokenProvider = tokenProvider;
		this.defaultConfig = defaultConfig;
		this.debugFileExtension = debugFileExtension;

		for (ITokenConfiguration configuration : configurationList) {
			configurations.put(configuration.getName(), configuration);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void init(ITokenResource root) throws CloneDetectionException {
		tokenProvider.init(root, getLogger());
		toInit = root;
		uniformPathToElement = null;
		initRegions(root);
	}

	/** Perform initialization */
	private void initRegions(ITokenResource root) {
		configurationMap = new TwoDimHashMap<String, String, RegionSet>();
		identifierManager = new IdManager<String>();

		for (ITokenElement element : TokenElementUtils.listTokenElements(root)) {
			retrieveAndStoreRegions(element);
		}
	}

	/** Retrieve regions information and store in map */
	private void retrieveAndStoreRegions(ITokenElement element) {
		try {
			RegionSetDictionary dictionary = RegionSetDictionary
					.retrieve(element);
			if (dictionary == null) {
				return;
			}
			for (RegionSet regions : dictionary) {
				configurationMap.putValue(element.getUniformPath(),
						regions.getName(), regions);
			}
		} catch (ConQATException e) {
			getLogger().warn(
					"Could not access regions for element '"
							+ element.getName() + "': " + e.getMessage());
		}
	}

	/**
	 * This method consumes a token from the token structure, transforms it and
	 * returns the transformed token. Tokens may be dropped.
	 */
	@Override
	protected TokenUnit provideNext() throws CloneDetectionException {
		if (identifierManager == null) {
			initRegions(toInit);
		}

		// Determine next token that is not ignored
		IToken token = tokenProvider.getNext();
		while (token != null && isIgnored(token)) {
			handleEndOfFile(token);
			normalization.put(token, null);
			token = tokenProvider.getNext();
		}

		// no more tokens, done!
		if (token == null) {
			return null;
		}

		// update identifier normalization
		if (token.getLanguage().getStatementOracle()
				.isEndOfStatementToken(token.getType(), tokenProvider)) {
			identifierManager.clear();
		}

		// handle sentinels
		if (token.getType() == ETokenType.SENTINEL) {
			return new SentinelUnit(token.getOriginId());
		}

		// update index in file
		int currentIndexInFile = indexInFile++;

		// create normalized token unit
		if (getConfigurationForToken(token).isNormalizeTypeKeywords()) {
			token = token
					.newToken(normalizeType(token), token.getOffset(),
							token.getLineNumber(), token.getText(),
							token.getOriginId());
		}

		TokenUnit unit = new TokenUnit(normalizeContent(token),
				token.getText(), token.getOffset(), token.getEndOffset(),
				token.getOriginId(), normalizeType(token), currentIndexInFile);
		normalization.put(token, unit);

		// handle case that token is last in file
		handleEndOfFile(token);

		return unit;
	}

	/** Check if next token if in other file and clean up */
	private void handleEndOfFile(IToken token) throws CloneDetectionException {
		if (!inSameFile(token, tokenProvider.lookahead(1))) {
			// write debug file if last unit of file reached
			if (!StringUtils.isEmpty(debugFileExtension)) {
				ITokenElement element = getElement(token.getOriginId());
				CanonicalFile baseFile = ResourceUtils.getFile(element);
				if (baseFile == null) {
					throw new CloneDetectionException(
							"Can not create debug file as underlying system does not reside in file system!");
				}
				NormalizationDebugUtils.writeDebugFile(baseFile, element,
						normalization, getLogger(), debugFileExtension);
			}

			identifierManager.clear();
			indexInFile = 0;
			normalization.clear();
		}
	}

	/**
	 * Returns the location for a uniform path using {@link #toInit} for lookup.
	 * The lookup map is cached in {@link #uniformPathToElement}.
	 */
	private ITokenElement getElement(String uniformPath) {
		if (uniformPathToElement == null) {
			uniformPathToElement = ResourceTraversalUtils
					.createUniformPathToElementMap(toInit, ITokenElement.class);
		}
		return uniformPathToElement.get(uniformPath);
	}

	/** Checks whether two tokens are in the same file */
	private boolean inSameFile(IToken token, IToken token2) {
		if (token == null || token2 == null) {
			return false;
		}
		return token.getOriginId().equals(token2.getOriginId());
	}

	/** Determines whether this token is to be ignored */
	private boolean isIgnored(IToken token) throws CloneDetectionException {
		// check whether we always keep end of statement tokens
		if (alwaysKeepEndOfStatementTokens && isEndOfStatementToken(token)) {
			ignoreNextTokens = 0; // we don't ignore tokens in this case.
			return false;
		}

		if (ignoreNextTokens > 0) {
			ignoreNextTokens--;
			return true;
		}

		return isIgnoredEndOfStatementToken(token) || isIgnoredComment(token)
				|| isIgnoredDelimiter(token)
				|| isIgnoredPreprocessorDirective(token)
				|| isIgnoredThisReference(token) || isIgnoredFQNameHead(token)
				|| isIgnoredModifier(token) || isIgnoredStopWord(token);
	}

	/** Determines whether a token is an end of statement token */
	private boolean isEndOfStatementToken(IToken token)
			throws CloneDetectionException {
		return token.getLanguage().getStatementOracle()
				.isEndOfStatementToken(token.getType(), tokenProvider);
	}

	/** Determines whether this token is an ignored End of Statement Token */
	private boolean isIgnoredEndOfStatementToken(IToken token)
			throws CloneDetectionException {
		if (alwaysKeepEndOfStatementTokens
				|| !getConfigurationForToken(token)
						.isIgnoreEndOfStatementTokens()) {
			return false;
		}
		return isEndOfStatementToken(token);
	}

	/** Determines whether this token is an ignored comment */
	private boolean isIgnoredComment(IToken token) {
		if (!getConfigurationForToken(token).isIgnoreComments()) {
			return false;
		}
		return token.getType().getTokenClass() == ETokenClass.COMMENT;
	}

	/**
	 * Determines whether this token is an ignored delimiter
	 * <p>
	 * 
	 */
	private boolean isIgnoredDelimiter(IToken token) {
		if (!getConfigurationForToken(token).isIgnoreDelimiters()) {
			return false;
		}
		return token.getType() == ETokenType.LPAREN
				|| token.getType() == ETokenType.RPAREN
				|| token.getType() == ETokenType.LBRACK
				|| token.getType() == ETokenType.RBRACK;
	}

	/** Determines whether this token is an ignored preprocessor directive */
	private boolean isIgnoredPreprocessorDirective(IToken token) {
		if (!getConfigurationForToken(token).isIgnorePreprocessorDirectives()) {
			return false;
		}
		return token.getType() == ETokenType.PREPROCESSOR_DIRECTIVE;
	}

	/** Determines whether this token is an ignored <em>this</em> reference */
	private boolean isIgnoredThisReference(IToken token)
			throws CloneDetectionException {
		if (token.getType() != ETokenType.THIS
				|| !getConfigurationForToken(token).isIgnoreThis()) {
			return false;
		}
		if (tokenProvider.lookahead(1) == null
				|| tokenProvider.lookahead(1).getType() != ETokenType.DOT
				&& tokenProvider.lookahead(1).getType() != ETokenType.POINTERTO) {
			return false;
		}
		ignoreNextTokens = 1;
		return true;
	}

	/**
	 * Tests whether the token is the head of a fully qualified name. If so, the
	 * entire fq header is ignored in one step.
	 */
	private boolean isIgnoredFQNameHead(IToken token)
			throws CloneDetectionException {
		if (token.getType() != ETokenType.IDENTIFIER
				|| !getConfigurationForToken(token)
						.isNormalizeFullyQualifiedNames()) {
			return false;
		}

		// check whether next token is a dot or a ::
		boolean isDot = lookaheadTokenType(1, ETokenType.DOT);
		if (!isDot && !lookaheadTokenType(1, ETokenType.SCOPE)) {
			return false;
		}

		// look for end of fq header
		ETokenType fqSeparatorTokenType = ETokenType.DOT;
		if (!isDot) {
			fqSeparatorTokenType = ETokenType.SCOPE;
		}

		int position = 1;
		while (lookaheadTokenType(position + 1, ETokenType.IDENTIFIER)
				&& lookaheadTokenType(position + 2, fqSeparatorTokenType)) {
			position += 2;
		}
		ignoreNextTokens = position;

		return true;
	}

	/** Tests whether the token is an ignored visibility modifier. */
	private boolean isIgnoredModifier(IToken token) {
		if (!getConfigurationForToken(token).isIgnoreModifier()) {
			return false;
		}
		return token.getType() == ETokenType.PUBLIC
				|| token.getType() == ETokenType.PROTECTED
				|| token.getType() == ETokenType.INTERNAL
				|| token.getType() == ETokenType.PRIVATE
				|| token.getType() == ETokenType.FINAL;
	}

	/** Tests whether the token is an ignored stop word. */
	private boolean isIgnoredStopWord(IToken token) {
		ITokenConfiguration configuration = getConfigurationForToken(token);

		if (!configuration.isIgnoreStopWords()) {
			return false;
		}
		CCSMAssert
				.isNotNull(configuration.getStopWords(),
						"Stop word elimination is enabled, but no stop words are found!");

		return token.getType() == ETokenType.WORD
				&& configuration.getStopWords().isStopWord(token.getText());
	}

	/** Performs lookahead and checks whether the found token has a certain type */
	private boolean lookaheadTokenType(int index, ETokenType tokenType)
			throws CloneDetectionException {
		IToken token = tokenProvider.lookahead(index);
		if (token == null) {
			return false;
		}
		return token.getType() == tokenType;
	}

	/** Normalizes the content of the token */
	private String normalizeContent(IToken token) {
		String content = token.getText();
		if (!token.getLanguage().isCaseSensitive()
				|| token.getType() != ETokenType.IDENTIFIER) {
			content = content.toLowerCase();
		}

		ITokenConfiguration tokenConfig = getConfigurationForToken(token);
		switch (token.getType()) {
		case IDENTIFIER:
			if (tokenConfig.isNormalizeIdentifiers()) {
				content = "id" + identifierManager.obtainId(content);
			}
			break;

		case STRING_LITERAL:
			if (tokenConfig.isNormalizeStringLiterals()) {
				content = StringUtils.EMPTY_STRING;
			}
			break;

		case CHARACTER_LITERAL:
			if (tokenConfig.isNormalizeCharacterLiterals()) {
				content = "char";
			}
			break;

		case INTEGER_LITERAL:
			if (tokenConfig.isNormalizeNumberLiterals()) {
				content = "0";
			}
			break;

		case FLOATING_POINT_LITERAL:
			if (tokenConfig.isNormalizeNumberLiterals()) {
				content = "0.0";
			}
			break;

		case BOOLEAN_LITERAL:
			if (tokenConfig.isNormalizeBooleanLiterals()) {
				content = "true";
			}
			break;

		case WORD:
			if (tokenConfig.isStemWords()) {
				CCSMAssert.isNotNull(tokenConfig.getStemmer(),
						"Stemming is enabled, but no stemmer has been set!");
				content = tokenConfig.getStemmer().stem(content);
			}
			break;
			
		case LINE:
			content = content.replaceAll("\\s+", "");

		default:
			// All other tokens will not be altered.
		}

		return content;
	}

	/**
	 * Gets the {@link ITokenConfiguration} determining the normalization for
	 * this token. The token-based choice of a configuration allows for
	 * context-sensitive normalization.
	 */
	private ITokenConfiguration getConfigurationForToken(IToken token) {
		for (String configName : configurations.keySet()) {
			RegionSet regions = configurationMap.getValue(token.getOriginId(),
					configName);
			if (regions != null && regions.contains(token.getOffset())) {
				return configurations.get(configName);
			}
		}

		// Return default configuration
		return defaultConfig;
	}

	/** Normalize the type of a token */
	private ETokenType normalizeType(IToken token) {
		if (!getConfigurationForToken(token).isNormalizeTypeKeywords()) {
			return token.getType();
		}

		switch (token.getType()) {
		case STRING:
			if (token.getLanguage().equals(ELanguage.COBOL)) {
				return ETokenType.STRING; // is verb in COBOL
			}
			// fall-through
		case BOOL:
			// fall-through
		case BOOLEAN:
			// fall-through
		case DECIMAL:
			// fall-through
		case DOUBLE:
			// fall-through
		case FLOAT:
			// fall-through
		case SINGLE:
			// fall-through
		case CHAR:
			// fall-through
		case BYTE:
			// fall-through
		case OBJECT:
			// fall-through
		case INT:
			return ETokenType.IDENTIFIER;
		}

		return token.getType();
	}

	/** Returns flag that turns off filtering of end of statement tokens */
	public boolean isAlwaysKeepEndOfStatementTokens() {
		return alwaysKeepEndOfStatementTokens;
	}

	/** Sets flag that turns off filtering of end of statement tokens */
	public void setAlwaysKeepEndOfStatementTokens(
			boolean alwaysKeepEndOfStatementTokens) {
		this.alwaysKeepEndOfStatementTokens = alwaysKeepEndOfStatementTokens;
	}

	/**
	 * Get the underlying token provider. Retrieving tokens from here changes
	 * the state of the normalization.
	 */
	public ITokenProvider getTokenProvider() {
		return tokenProvider;
	}

}