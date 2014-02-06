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
package org.conqat.engine.cpp.compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.base.ReportReaderBase;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: E21B5572672626A8CBF11320E0193A69
 */
@AConQATProcessor(description = "This processors reads warnings from the output of the Windriver Diab Compiler."
		+ ReportReaderBase.DOC)
public class DiabCompilerWarningsReader extends CompilerWarningsReaderBase {

	/** Pattern used to match lines of diab output. */
	private static final Pattern DIAB_PATTERN = Pattern
			.compile("\"(.+)\", line (\\d+): (info|warning) (.*)");

	/** Stores the last reported warning to allow later additions. */
	private CompilerWarning lastWarning = null;

	/** {@inheritDoc} */
	@Override
	protected CompilerWarning parseWarning(String line) {
		Matcher matcher = DIAB_PATTERN.matcher(line);
		if (!matcher.matches()) {
			return null;
		}

		String location = matcher.group(1);
		// can not throw as checked pattern before
		int lineNumber = Integer.parseInt(matcher.group(2));
		String type = matcher.group(3);
		String message = matcher.group(4);
		String clusterString = message.substring(0, message.indexOf(' '));

		if ("warning".equals(type)) {
			lastWarning = new CompilerWarning(location, lineNumber, message,
					clusterString);
			return lastWarning;
		} else if (lastWarning != null) {
			lastWarning.addMessageSuffix(line);
		}
		return null;
	}
}
