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
package org.conqat.engine.io;

import java.io.File;
import java.io.IOException;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.logging.ConQATLoggingEvent;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.date.DateUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This processor writes the current ConQAT log to a file.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35195 $
 * @ConQAT.Rating GREEN Hash: 94ECB8569D445CC73FA49366030CA2EE
 */
@AConQATProcessor(description = "This processor writes the current ConQAT log to a file.")
public class LogFileWriter extends FileWriterBase {

	/** Set output file. */
	@AConQATParameter(name = "wait-for", description = ""
			+ "This parameter does not provide any functionality, but it ensures that this "
			+ "processor is not run before all referenced processors and blocks have been "
			+ "executed and had the chance to write their log files.")
	public void waitFor(
			@SuppressWarnings("unused") @AConQATAttribute(name = "ref", description = "Reference to the processor which should be run before the log is written.") Object o) {
		// does nothing
	}

	/** {@inheritDoc} */
	@Override
	protected void writeFile(File file) throws IOException {
		StringBuilder content = new StringBuilder();
		appendHeader(content);
		appendLoggingEvents(content);
		FileSystemUtils.writeFile(file, content.toString());
	}

	/** Append the log messages to the log file. */
	private void appendLoggingEvents(StringBuilder content) {
		content.append("Logging events:");
		content.append(StringUtils.CR);

		UnmodifiableList<ConQATLoggingEvent> events = getProcessorInfo()
				.getLogManager().getLoggingEvents();

		for (ConQATLoggingEvent event : events) {
			content.append(StringUtils.TWO_SPACES);
			content.append(formatLevel(event.getLevel()));
			content.append(StringUtils.TWO_SPACES);
			content.append(StringUtils.replaceLineBreaks(event.getMessage()
					.toString()));
			content.append(StringUtils.CR);
		}
	}

	/** Format log level constant. */
	private String formatLevel(ELogLevel level) {
		return StringUtils.flushLeft(level.toString(), 6, ' ');
	}

	/** Add file header to the log file. */
	private void appendHeader(StringBuilder content) {
		content.append(getProcessorInfo().getConQATCoreVersion());
		content.append(" (");
		content.append(DateUtils.getNow());
		content.append(")");
		content.append(StringUtils.CR);
		content.append(StringUtils.CR);
	}
}