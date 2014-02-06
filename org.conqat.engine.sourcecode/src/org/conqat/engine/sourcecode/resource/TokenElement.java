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
package org.conqat.engine.sourcecode.resource;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.text.TextElement;
import org.conqat.engine.resource.text.filter.base.ITextFilter;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.cache4j.CacheFactory;
import org.conqat.lib.commons.cache4j.ICache;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.factory.ForwardingFactory;
import org.conqat.lib.commons.factory.IFactory;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ILenientScanner;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerFactory;
import org.conqat.lib.scanner.ScannerUtils;

/**
 * Default implementation of {@link ITokenElement}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43041 $
 * @ConQAT.Rating GREEN Hash: 4E0544B94B42BE3E1DFC621CA446BF36
 */
public class TokenElement extends TextElement implements ITokenElement {

	/** The cache used for storing tokens. */
	@SuppressWarnings("unchecked")
	private static final ICache<ObtainTokensKey, UnmodifiableList<IToken>, ConQATException> tokenCache = CacheFactory
			.obtainCache(TokenElement.class, ForwardingFactory.INSTANCE);

	/** Number of scanner warnings logged for an element. */
	private static final int NUM_OF_SCANNER_WARNINGS = 5;

	/**
	 * Elements whose percentage of invalid tokens is higher than this threshold
	 * are discarded entirely. The rationale is that the scanner recognizes the
	 * element so badly, that it won't provide any sensible input for analysis.
	 */
	private static final double INVALID_TOKENS_THRESHOLD = 0.15;

	/** The language. */
	private final ELanguage language;

	/** Constructor. */
	public TokenElement(IContentAccessor accessor, Charset encoding,
			ELanguage language) {
		this(accessor, encoding, language, null);
	}

	/** Constructor. */
	public TokenElement(IContentAccessor accessor, Charset encoding,
			ELanguage language, ITextFilter filter) {
		super(accessor, encoding, filter);
		this.language = language;
	}

	/** Copy Constructor. */
	protected TokenElement(TokenElement other) throws DeepCloneException {
		super(other);
		language = other.language;
	}

	/** {@inheritDoc} */
	@Override
	public TokenElement deepClone() throws DeepCloneException {
		return new TokenElement(this);
	}

	/** {@inheritDoc} */
	@Override
	public ELanguage getLanguage() {
		return language;
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableList<IToken> getTokens(IConQATLogger logger)
			throws ConQATException {
		return tokenCache.obtain(new ObtainTokensKey(this, logger));
	}

	/**
	 * This is the same as {@link #getTokens(IConQATLogger)}, but doas not
	 * perform caching.
	 */
	protected UnmodifiableList<IToken> getTokensUncached(IConQATLogger logger)
			throws ConQATException {

		List<IToken> allTokens = obtainAllTokens();
		List<IToken> validTokens = obtainValidTokens(allTokens, logger);

		if (tooManyErrorTokens(allTokens.size(), validTokens.size())) {
			logger.error("Could not read tokens from element '"
					+ getLocation()
					+ "': Too many tokens could not be recognized by the scanner.");
			return CollectionUtils.emptyList();
		}

		return CollectionUtils.asUnmodifiable(validTokens);
	}

	/** Obtain all tokens. */
	private List<IToken> obtainAllTokens() throws ConQATException {
		ILenientScanner scanner = ScannerFactory.newLenientScanner(language,
				getTextContent(), getUniformPath());

		try {
			return ScannerUtils.readTokens(scanner);
		} catch (IOException e) {
			throw new ConQATException("IO error while reading tokens from "
					+ getLocation() + ": " + e.getMessage(), e);
		}
	}

	/**
	 * Given a list of tokens, this returns all valid (non-error) tokens. Error
	 * tokens are logged. Maximum number of error tokens is limited to
	 * {@value #NUM_OF_SCANNER_WARNINGS}.
	 */
	private List<IToken> obtainValidTokens(List<IToken> allTokens,
			IConQATLogger logger) {
		List<IToken> validTokens = new ArrayList<IToken>();

		int logMessageCount = 0;

		for (IToken token : allTokens) {
			if (token.getType().isError()) {
				if (logMessageCount < NUM_OF_SCANNER_WARNINGS) {

					// toString() of tokens contains detail info
					logger.warn("Ignored illegal token: " + token);
					logMessageCount++;
				}
			} else {
				validTokens.add(token);
			}
		}

		return validTokens;
	}

	/** Determines whether error token ratio exceeds threshold */
	private boolean tooManyErrorTokens(int allTokensCount, int validTokensCount) {
		if (allTokensCount == 0) {
			return false;
		}

		int errorTokensCount = allTokensCount - validTokensCount;

		double invalidRatio = (double) errorTokensCount
				/ (double) allTokensCount;
		return invalidRatio >= INVALID_TOKENS_THRESHOLD;
	}

	/** Returns <code>null</code>. Must override this to fulfill interface. */
	@Override
	public ITokenElement[] getChildren() {
		return null;
	}

	/**
	 * Contains all attributes of a {@link TokenElement} that can be used to
	 * uniquely identify it.
	 */
	public static class TokenElementKey extends TextElementKey {

		/** The language used. */
		protected final ELanguage language;

		/** Constructor. */
		protected TokenElementKey(TokenElement element) {
			super(element);
			language = element.language;
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(Object obj) {
			return super.equals(obj) && (obj instanceof TokenElementKey)
					&& ((TokenElementKey) obj).language == language;
		}

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			return super.hashCode() * 17 + language.hashCode();
		}
	}

	/**
	 * The key/factory used for obtaining the filtered text.
	 */
	protected static class ObtainTokensKey extends TokenElementKey implements
			IFactory<UnmodifiableList<IToken>, ConQATException> {

		/**
		 * We need a reference to the token element, but do not want to keep
		 * this key (which may live a long time in the cache) to stop the
		 * element from being collected by the GC later on. Thus we use a weak
		 * reference here.
		 */
		private final WeakReference<TokenElement> elementRef;

		/**
		 * Reference to the logger (same rationale as for as {@link #elementRef}
		 * ).
		 */
		private final WeakReference<IConQATLogger> logger;

		/** Constructor. */
		protected ObtainTokensKey(TokenElement element, IConQATLogger logger) {
			super(element);
			elementRef = new WeakReference<TokenElement>(element);
			this.logger = new WeakReference<IConQATLogger>(logger);
		}

		/** {@inheritDoc} */
		@SuppressWarnings("null")
		@Override
		public UnmodifiableList<IToken> create() throws ConQATException {
			TokenElement element = elementRef.get();
			IConQATLogger logger = this.logger.get();

			CCSMAssert
					.isTrue(element != null && logger != null,
							"The element and the logger should not be null, "
									+ "as creation is only issued from the cache via a live "
									+ "TokenElement, which in turn means that the reference must "
									+ "still be valid.");

			return element.getTokensUncached(logger);
		}
	}
}