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
package org.conqat.engine.core.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.conqat.engine.core.driver.instance.ProcessorInstance;

/**
 * Default implementation of {@link IConQATLogger}. Each logger object is
 * associated with one processor instance and uses and underlying Log4J logger
 * to perform the actual logging.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7335B589C3D1058A662A84BF3D4197A2
 */
public class ConQATLogger extends ConQATLoggerBase {

	/** Underlying Log4J logger. */
	private final Logger logger;

	/** Name of the processor this logger belongs to. */
	private final String processorName;

	/**
	 * Create new logger for a processor.
	 * 
	 * @param processor
	 *            The processor the logger belongs to. Processor class defines
	 *            Log4J-logger.
	 */
	public ConQATLogger(ProcessorInstance processor) {
		this(processor, processor.getDeclaration().getSpecification()
				.getProcessorClass());
	}

	/**
	 * Create new logger for a processor but use another class to define the
	 * underlying Log4J logger. This constructor is used by the driver to create
	 * a logger whose messages are associated with a specific processor but the
	 * underlying Log4J-logger is defined by a driver class. This ensures that
	 * logging cannot be turned off by configuring the log level for the
	 * processor.
	 * 
	 * @param processor
	 *            The processor the logger belongs to.
	 * @param clazz
	 *            The class that defines the underlying Log4j-Logger.
	 */
	public ConQATLogger(ProcessorInstance processor, Class<?> clazz) {
		this(processor.getName(), clazz);
	}

	/** Create new logger with explicit processor name and class. */
	public ConQATLogger(String processorName, Class<?> clazz) {
		this.processorName = processorName;
		logger = Logger.getLogger(clazz);
	}

	/** {@inheritDoc} */
	@Override
	public void log(ELogLevel level, Object message) {
		if (level == ELogLevel.OFF) {
			return;
		}
		log(level.getLog4JLevel(), message);

	}

	/** {@inheritDoc} */
	@Override
	public void log(ELogLevel level, Object message, Throwable throwable) {
		if (level == ELogLevel.OFF) {
			return;
		}
		log(level.getLog4JLevel(), message, throwable);
	}

	/**
	 * Log a message. This creates a new {@link LogMessage} object and logs it
	 * with the Log4J logger.
	 * 
	 * @param level
	 *            Log4J log level.
	 * @param message
	 *            log message.
	 */
	private synchronized void log(Level level, Object message) {
		logger.log(level, new LogMessage(processorName, message));
	}

	/**
	 * Log a message. This creates a new {@link LogMessage} object and logs it
	 * with the Log4J logger.
	 * 
	 * @param level
	 *            Log4J log level.
	 * @param message
	 *            log message.
	 */
	private synchronized void log(Level level, Object message,
			Throwable throwable) {
		logger.log(level, new LogMessage(processorName, message), throwable);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized ELogLevel getMinLogLevel() {
		return ELogLevel.forLog4jLevel(logger.getEffectiveLevel());
	}
}