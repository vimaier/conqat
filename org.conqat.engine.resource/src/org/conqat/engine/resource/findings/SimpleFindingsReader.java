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
package org.conqat.engine.resource.findings;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.base.ReportReaderBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: DF7D269CA3A8545C7D58EE3A571DBE19
 */
@AConQATProcessor(description = "This processor reads finding reports with a very simple format. "
		+ "Each line is a finding format as <location string> <#sep#> <rule id> <#sep#> <message> where "
		+ "each of the parts is a string and the message may contain further separator symbols. "
		+ ReportReaderBase.DOC)
public class SimpleFindingsReader extends ReportReaderBase {

	/** Default separator */
	private final static String DEFAULT_SEPARATOR = "#";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "separator", attribute = "value", description = "Separator between "
			+ "the parts within a line in the report file. This is a Java regular expression. "
			+ "[Default is " + DEFAULT_SEPARATOR + "]", optional = true)
	public String separator = DEFAULT_SEPARATOR;

	/** {@inheritDoc} */
	@Override
	protected void loadReport(ITextElement report) throws ConQATException {
		String[] lines = StringUtils.splitLines(report.getTextContent());
		for (int i = 0; i < lines.length; i++) {
			processLine(lines[i], i);
		}
	}

	/**
	 * Process a single line of the report.
	 */
	private void processLine(String line, int lineNumber)
			throws ConQATException {
		String[] parts = line.split(separator, 3);

		if (parts.length != 3) {
			handleError("Finding in line " + (lineNumber + 1)
					+ " does not match expected format: "
					+ "<location string> " + separator + " <rule id> "
					+ separator + " <message>. Line: " + line);
			return;
		}

		String filename = parts[0].trim();
		String ruleId = parts[1].trim();
		String message = parts[2].trim();

		createFindingForFileLocation(ruleId, message, filename);
	}

	/** Returns null. */
	@Override
	protected String obtainRuleDescription(String ruleId) {
		return null;
	}
}
