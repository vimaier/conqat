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

import java.util.EnumSet;

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenElementUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2CA04FA47AA1514FA3F060BAA1AD6D29
 */
@AConQATProcessor(description = "This analyzer counts all lines of code except "
		+ "empty lines and lines that contain only comments.")
public class SLOCAnalyzer extends TokenMetricAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "SLOC", type = "java.lang.Integer")
	public static final String KEY = "SLOC";

	/** All token classes but comments. */
	public static final EnumSet<ETokenClass> NON_COMMENT_TOKEN_CLASSES = EnumSet
			.complementOf(EnumSet.of(ETokenClass.COMMENT));

	/** {@inheritDoc} */
	@Override
	protected String getKey() {
		return KEY;
	}

	/** {@inheritDoc} */
	@Override
	protected void calculateMetrics(ITokenElement element)
			throws ConQATException {
		String content = TokenElementUtils.getFilteredTokenContent(element,
				NON_COMMENT_TOKEN_CLASSES, getLogger());
		reportMetricValue(StringUtils.splitLines(content).length);
	}
}