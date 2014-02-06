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
package org.conqat.engine.sourcecode.util;

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.diff.CodeChurnMetrics;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.sourcecode.analysis.SLOCAnalyzer;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenElementUtils;

/**
 * Calculates code churn metrics in SLOC.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 18F22BCBE134687D41FBB82B6B1A1DB3
 */
public class SLocCodeChurnMetrics extends CodeChurnMetrics<ITokenElement> {

	/** Constructor */
	public SLocCodeChurnMetrics(ITokenElement mainElement,
			ITokenElement compareeElement, IConQATLogger logger)
			throws ConQATException {
		calculateMetrics(mainElement, compareeElement, logger);
	}

	/** {@inheritDoc} */
	@Override
	protected List<String> normalize(ITokenElement element, IConQATLogger logger)
			throws ConQATException {
		String content = TokenElementUtils.getFilteredTokenContent(element,
				SLOCAnalyzer.NON_COMMENT_TOKEN_CLASSES, logger);
		return TextElementUtils.getNormalizedContent(content);
	}
}
