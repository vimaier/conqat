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

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.normalization.token.ITokenProvider;
import org.conqat.engine.code_clones.normalization.token.TokenProviderBase;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessorInfo;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for shapers. Shapers modify the token stream before normalization
 * to influence the outcome of clone detection.
 * <p>
 * Shapers can either swallow tokens or insert synthetic pendingSentinel tokens.
 * Sentinel tokens are unequal to each other by definition and thus limit
 * clones.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 43263 $
 * @ConQAT.Rating GREEN Hash: 22A59F17863E87D25CAAFF41D979C2B5
 */
public abstract class ShaperBase extends TokenProviderBase implements
		IConQATProcessor {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Source of the tokens stream that gets shaped */
	protected ITokenProvider tokenProvider;

	/** Sentinel that gets returned upon next access */
	private IToken pendingSentinel = null;

	/** Set token provider whose tokens are filtered */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
	public void setTokenProvider(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_NAME) ITokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.ENABLE_NAME, attribute = ConQATParamDoc.ENABLE_PROCESSOR_NAME, description = ConQATParamDoc.ENABLE_DESC
			+ " [Default is enabled]", optional = true)
	public boolean enable = true;

	/** Returns reference to this */
	@Override
	public ShaperBase process() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	protected void init(ITokenResource root) throws CloneDetectionException {
		tokenProvider.init(root, getLogger());
	}

	/** {@inheritDoc} */
	@Override
	public void init(IConQATProcessorInfo processorInfo) {
		logger = processorInfo.getLogger();
	}

	/** {@inheritDoc} */
	@Override
	protected IToken provideNext() throws CloneDetectionException {
		if (!enable) {
			return tokenProvider.getNext();
		}

		IToken token = null;

		while (token == null) {
			// check for pending pendingSentinel
			if (pendingSentinel != null) {
				IToken reference = pendingSentinel;
				pendingSentinel = null;
				return reference;
			}

			// retrieve next token
			token = tokenProvider.getNext();
			if (token == null) {
				return null;
			}

			// check whether to append sentinel
			if (isBoundary(token)) {
				pendingSentinel = SentinelToken.createSentinelAfter(token);
			}

			// determine whether to skip token
			if (skip(token)) {
				token = null;
			}
		}

		return token;
	}

	/** Template method that allows deriving classes to skip tokens */
	protected boolean skip(@SuppressWarnings("unused") IToken token) {
		return false;
	}

	/** Determines whether a token represents a boundary */
	protected abstract boolean isBoundary(IToken token);

}