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
package org.conqat.engine.resource.analysis;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41772 $
 * @ConQAT.Rating GREEN Hash: 056FACC25FA6939FCD4CB483CD790542
 */
@AConQATProcessor(description = "Reports length of lines. Optionally can mark overly long lines with findings.")
public class LineLengthAnalyzer extends TextMetricAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Line length", type = "java.lang.Number")
	public static final String KEY = "Line length";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "trailing-space", attribute = "ignore", optional = true, description = ""
			+ "Whether to ignore trailing space when counting line length (default is false).")
	public boolean ignoreTrailingSpace = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "ignored-lines", attribute = "patterns", optional = true, description = ""
			+ "All lines in which one of the given patterns are found are ignored for this analysis.")
	public PatternList ignoredLinesPatterns = new PatternList();

	/** {@inheritDoc} */
	@Override
	protected void calculateMetrics(ITextElement element)
			throws ConQATException {

		String[] lines = TextElementUtils.getLines(element);
		for (int currentLine = 0; currentLine < lines.length; currentLine++) {
			String line = lines[currentLine];
			if (ignoredLinesPatterns.findsAnyIn(line)) {
				continue;
			}
			if (ignoreTrailingSpace) {
				line = line.replaceAll("\\s+$", StringUtils.EMPTY_STRING);
			}
			reportMetricValueForFilteredLine(line.length(), currentLine + 1);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getKey() {
		return KEY;
	}
}
