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
package org.conqat.engine.code_clones.normalization;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.normalization.token.ITokenProvider;
import org.conqat.engine.code_clones.normalization.token.configuration.ITokenConfiguration;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for factory classes that create classes that require token
 * configurations
 * <p>
 * Although this class is only used within this package, it must have public
 * visibility, since package visibility presents problems to the ConQAT driver.
 * 
 * @author juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 9E3EB607B363E2FCD35E159223DDE2BF
 */
public abstract class TokenBasedNormalizationFactoryBase extends
		ConQATProcessorBase {

	/** Determines the normalization */
	protected ITokenConfiguration defaultConfiguration;

	/** Provides tokens for normalization */
	protected ITokenProvider tokenProvider;

	/** Context-sensitive normalization configurations */
	protected List<ITokenConfiguration> configurationList = new ArrayList<ITokenConfiguration>();

	/**
	 * If this string is set to a non-empty value, a debug file (containing the
	 * normalized units) is written for each input file.
	 */
	protected String debugFileExtension = null;

	/** Adds a context-sensitive normalization configuration */
	@AConQATParameter(name = "configuration", description = "Adds a context-sensitive normalization configuration", minOccurrences = 0, maxOccurrences = -1)
	public void addConfiguration(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ITokenConfiguration configuration) {
		configurationList.add(configuration);
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "token", description = "Determines the token normalization", minOccurrences = 1, maxOccurrences = 1)
	public void setTokenConfiguration(
			@AConQATAttribute(name = "default-configuration", description = "Determines the token normalization") ITokenConfiguration tokenConfiguration,
			@AConQATAttribute(name = "provider", description = "Provides the tokens that get normalized") ITokenProvider tokenProvider) {
		defaultConfiguration = tokenConfiguration;
		this.tokenProvider = tokenProvider;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "debug", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "If this string is set to a non-empty value, a debug file is written for each input file")
	public void setDebugFileExtension(
			@AConQATAttribute(name = "extension", description = "File extension") String debugFileExtension) {
		if (StringUtils.isEmpty(debugFileExtension)) {
			throw new IllegalArgumentException(
					"Empty debug file extension not allowed, since it would overwrite existing files.");
		}
		if (!debugFileExtension.startsWith(".")) {
			debugFileExtension = "." + debugFileExtension;
		}
		this.debugFileExtension = debugFileExtension;
	}

}