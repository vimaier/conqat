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
package org.conqat.engine.html_presentation.javascript;

import java.util.Properties;

import org.conqat.engine.commons.logging.ListStructuredLogMessage;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.driver.runner.ConQATRunnableBase;
import org.conqat.engine.core.logging.ConQATLoggerBase;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.lib.commons.options.AOption;

/**
 * Utility program for compiling ConQAT's JavaScript code without actually
 * executing the driver or even the HTML presentation. This is mostly used
 * during development.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 42761 $
 * @ConQAT.Rating GREEN Hash: BC7660393598588CD9A737E8B070B7B5
 */
public class JavaScriptStandaloneCompiler extends ConQATRunnableBase {

	/**
	 * Additional properties that override the ones specified in the
	 * compile.properties file.
	 */
	private final Properties overrideProperties = new Properties();

	/**
	 * Allows to set properties that override those in the compile.properties
	 * file.
	 */
	@AOption(shortName = 'p', longName = "properties", description = "Allows to set "
			+ "properties that override those in the compile.properties file. The format is key=value")
	public void setProperties(String propertiesString) {
		String[] parts = propertiesString.split("=", 2);
		if (parts.length != 2) {
			throw new IllegalArgumentException("Expected = sign in property.");
		}
		overrideProperties.put(parts[0], parts[1]);
	}

	/** {@inheritDoc} */
	@Override
	protected void doRun() {
		try {
			System.out.println("Starting JavaScript compilation.");
			Logger logger = new Logger();

			JavaScriptManager.getInstance().configureFromProperties(
					overrideProperties);
			String script = JavaScriptManager.getInstance()
					.obtainScript(logger);
			System.out.println("Compiled script has " + script.length() / 1000.
					+ " kB");
			if (logger.hadErrorsOrWarnings) {
				System.exit(1);
			}
		} catch (ConQATException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/** Logger used. */
	private final class Logger extends ConQATLoggerBase {

		/** Used to indicate whether messages where logged on a non-info level. */
		public boolean hadErrorsOrWarnings = false;

		/** {@inheritDoc} */
		@Override
		public void log(ELogLevel level, Object message) {
			System.out.println(level + ": " + message);
			expandStructuredLog(message);

			switch (level) {
			case FATAL:
			case ERROR:
			case WARN:
				hadErrorsOrWarnings = true;
			}
		}

		/** {@inheritDoc} */
		@Override
		public void log(ELogLevel level, Object message, Throwable throwable) {
			throwable.printStackTrace(System.out);
			log(level, message);
		}

		/** Expands a structured log message. */
		private void expandStructuredLog(Object message) {
			if (message instanceof ListStructuredLogMessage) {
				for (String detail : ((ListStructuredLogMessage) message)
						.getDetails()) {
					System.out.println("    -> " + detail);
				}
			}
		}

		/** {@inheritDoc} */
		@Override
		public ELogLevel getMinLogLevel() {
			return ELogLevel.INFO;
		}
	}
}
