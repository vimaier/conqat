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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.base.ReportReaderBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for processors reading compiler warnings.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46815 $
 * @ConQAT.Rating GREEN Hash: 682A5338B18048C03CD4FE553EE4FF5E
 */
public abstract class CompilerWarningsReaderBase extends ReportReaderBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "filter-duplicates", attribute = "value", optional = true, description = ""
			+ "If this is true, duplicate warnings are suppressed. Default is false.")
	public boolean filterDuplicates = false;

	/** {@inheritDoc} */
	@Override
	protected void loadReport(ITextElement report) throws ConQATException {

		int ignoredLines = 0;
		int filteredDuplicates = 0;

		ListMap<String, CompilerWarning> clusteredWarnings = new ListMap<String, CompilerWarning>();
		Set<String> seenWarningLines = new HashSet<String>();

		for (String line : TextElementUtils.getLines(report)) {
			CompilerWarning warning = parseWarning(line);

			if (warning == null) {
				ignoredLines += 1;
			} else if (filterDuplicates && !seenWarningLines.add(line)) {
				filteredDuplicates += 1;
			} else {
				clusteredWarnings.add(warning.clusterString, warning);
			}
		}

		for (String key : clusteredWarnings.getKeys()) {
			List<CompilerWarning> warnings = clusteredWarnings
					.getCollection(key);
			String commonMessage = determineCommonParts(warnings);
			for (CompilerWarning warning : warnings) {
				warning.report(commonMessage);
			}
		}

		getLogger().info("Ignored " + ignoredLines + " lines of the input");
		if (filterDuplicates) {
			getLogger().info(
					"Ignored " + filteredDuplicates + " duplicate warnings");
		}
	}

	/**
	 * Template method for parsing a warning from a single line. Returns null,
	 * if the line contains no warning.
	 */
	protected abstract CompilerWarning parseWarning(String line)
			throws ConQATException;

	/**
	 * Extracts the common parts from the messages, i.e. creates a astring from
	 * the common leading and trailing words (concatenated by "...").
	 */
	protected String determineCommonParts(List<CompilerWarning> warnings) {
		String referenceMessage = warnings.get(0).message;
		String[] reference = referenceMessage.split("\\s+");
		int prefixEnd = reference.length;
		int suffixStart = 0;

		for (CompilerWarning warning : warnings) {
			String[] parts = warning.message.split("\\s+");
			for (int i = 0; i < prefixEnd; ++i) {
				if (!parts[i].equals(reference[i])) {
					prefixEnd = i;
					break;
				}
			}

			for (int i = reference.length - 1; i >= suffixStart; --i) {
				if (!parts[parts.length - (reference.length - i)]
						.equals(reference[i])) {
					suffixStart = i + 1;
					break;
				}
			}
		}

		if (prefixEnd >= suffixStart) {
			return referenceMessage;
		}

		StringBuilder commonMessage = new StringBuilder();
		for (int i = 0; i < prefixEnd; ++i) {
			commonMessage.append(reference[i]).append(" ");
		}
		commonMessage.append("...");
		for (int i = suffixStart; i < reference.length; ++i) {
			commonMessage.append(" ").append(reference[i]);
		}
		return commonMessage.toString();
	}

	/** {@inheritDoc} */
	@Override
	protected String obtainRuleDescription(String ruleId) {
		return null;
	}

	/** Representation of a single warning. */
	protected class CompilerWarning {

		/** Location of the file. */
		private final String location;

		/** The line number. */
		private final int lineNumber;

		/** The message. */
		private final String message;

		/** String used for clustering. */
		private final String clusterString;

		/** Stores the message prefix. */
		private StringBuilder messagePrefix = new StringBuilder();

		/** Stores the message suffix. */
		private StringBuilder messageSuffix = new StringBuilder();

		/** Constructor. */
		public CompilerWarning(String location, int lineNumber, String message,
				String clusterString) {
			this.location = location;
			this.lineNumber = lineNumber;
			this.message = message;
			this.clusterString = clusterString;
		}

		/** Reports the message as a finding. */
		public void report(String commonMessage) throws ConQATException {
			createLineFinding(commonMessage, messagePrefix.toString() + message
					+ messageSuffix.toString(), location, lineNumber);
		}

		/** Adds further info as a prefix to this message. */
		public void addMessagePrefix(String line) {
			messagePrefix.append(line).append(StringUtils.CR);
		}

		/** Adds further info as a suffix to this message. */
		public void addMessageSuffix(String line) {
			messageSuffix.append(StringUtils.CR).append(line);
		}
	}
}
