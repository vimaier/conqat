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
package org.conqat.engine.resource.analysis;

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46044 $
 * @ConQAT.Rating GREEN Hash: 81E2EB11008784EF46784442CA8BD643
 */
@AConQATProcessor(description = "Counts lines of code. Optionally can mark overly long files with findings.")
public class LOCAnalyzer extends TextMetricAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Lines of code", type = "java.lang.Number")
	public static final String KEY = "LoC";

	/** {@inheritDoc} */
	@Override
	protected String getKey() {
		return KEY;
	}

	/** {@inheritDoc} */
	@Override
	protected void calculateMetrics(ITextElement element)
			throws ConQATException {
		reportMetricValue(TextElementUtils.countLOC(element));
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingDescription() {
		return "<p>Long files can complicate both locating a specific feature "
				+ "within a file and understanding the consequences of a "
				+ "change. Ideally, a file should contain a clearly defined set of "
				+ "features. Very long files often indicate that too many features "
				+ "are intermixed in the code.</p> "
				+ "<p>The reduction of file length should not be performed only "
				+ "syntactically (e.g. by folding all lines into a single line or "
				+ "using a preprocessor for splitting the files), but rather by "
				+ "separating the different features or concerns of a class and "
				+ "placing them into separate files or classes.</p> ";
	}
}