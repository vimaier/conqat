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
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 67C7D6DEA71F58EC4288D978DE7F524D
 */
@AConQATProcessor(description = "This processors reads warnings from the output of the GNU Compiler Collection."
		+ ReportReaderBase.DOC)
public class GnuCompilerWarningsReader extends CompilerWarningsReaderBase {

	/** Pattern used to match lines of GCC output. */
	private static final Pattern GCC_WARNING_PATTERN = Pattern
			.compile("(.+):(\\d+):\\d+: (warning|note): (.*)");

	/** Pattern for intro text (i.e. context info). */
	private static final Pattern GCC_INTRO_PATTERN = Pattern
			.compile("(.+):(.+:)");

	/** Stores warning context. */
	private String warningContext;

	/** Stores the previous warning. */
	private CompilerWarning previousWarning;

	/** {@inheritDoc} */
	@Override
	protected CompilerWarning parseWarning(String line) {
		Matcher matcher = GCC_WARNING_PATTERN.matcher(line);
		if (!matcher.matches()) {
			matcher = GCC_INTRO_PATTERN.matcher(line);
			if (matcher.matches()) {
				warningContext = matcher.group(2);
			}
			return null;
		}

		String location = matcher.group(1);
		// can not throw as checked pattern before
		int lineNumber = Integer.parseInt(matcher.group(2));
		String type = matcher.group(3);
		String message = matcher.group(4);

		if ((!type.equals("warning") || StringUtils.startsWithOneOf(message,
				"(near ", "conflicts with previous"))
				&& previousWarning != null) {
			previousWarning.addMessageSuffix(message + " (" + location + ":"
					+ lineNumber + ")");
			return null;
		}

		// clustering of GCC messages is hard as there is no unique ID
		// instead we try to mask all variable parts of the message
		String quotes = "\"\'\u2018\u2019";
		String clusterString = message
				.replaceAll(
						"[" + quotes + "][^" + quotes + "]+[" + quotes + "]",
						"...").replaceAll("[(][^)]+[)]", "...")
				.replaceAll("\\d+", "...");

		previousWarning = new CompilerWarning(location, lineNumber, message,
				clusterString);
		if (warningContext != null) {
			previousWarning.addMessagePrefix(warningContext);
			warningContext = null;
		}
		return previousWarning;
	}
}
