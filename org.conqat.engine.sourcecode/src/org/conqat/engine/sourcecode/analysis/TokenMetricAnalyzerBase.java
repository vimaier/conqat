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
package org.conqat.engine.sourcecode.analysis;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.NumericMetricAnalyzerBase;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for metric processors working on token elements.
 * 
 * @author $Author: goede $
 * @version $Rev: 41798 $
 * @ConQAT.Rating GREEN Hash: 0C0449A5B75009AE7E89FEE45FF8748A
 */
public abstract class TokenMetricAnalyzerBase extends
		NumericMetricAnalyzerBase<ITokenResource, ITokenElement> {

	/** {@inheritDoc} */
	@Override
	protected Class<ITokenElement> getElementClass() {
		return ITokenElement.class;
	}

	/**
	 * Reports a metric value. The location is a single token. The token must be
	 * from the filtered text, as this method also performs conversion to "raw"
	 * positions.
	 */
	protected void reportMetricValueForFilteredToken(double value, IToken token)
			throws ConQATException {
		reportMetricValueForFilteredTokenRegion(value, token, token);
	}

	/**
	 * Reports a metric value. The location is a range of tokens denoted by
	 * first and last token. The tokens must be from the filtered text, as this
	 * method also performs conversion to "raw" positions.
	 */
	protected void reportMetricValueForFilteredTokenRegion(double value,
			IToken firstToken, IToken lastToken) throws ConQATException {
		reportMetricValue(value,
				ResourceUtils.createTextRegionLocationForFilteredOffsets(
						currentElement, firstToken.getOffset(),
						lastToken.getEndOffset()));
	}
}