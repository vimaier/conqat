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
package org.conqat.engine.commons.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.conqat.engine.core.logging.ConQATLoggerBase;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.engine.core.logging.IConQATLogger;

/**
 * This logger can be used to reduce the number of generated log messages.
 * Messages are only logged if they haven't been logged before (or for a longer
 * time).
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 40563 $
 * @ConQAT.Rating GREEN Hash: EE995077985FA54EACBE2AB6B51791D6
 */
public class SlimmingLogger extends ConQATLoggerBase {

	/**
	 * This maps from message text to the message itself. While a set would be
	 * the appropriate data structure, we use a map here as it provides
	 * functionality to implement LRU cache. We use Strings as values (as
	 * opposed to <code>null</code>) so we can make use of the return value of
	 * {@link Map#put(Object, Object)}. We use Strings as keys to not create
	 * reference to the objects used as messages.
	 */
	private final LinkedHashMap<String, String> messages;

	/** The underlying logger. */
	private final IConQATLogger logger;

	/** Creates new slimming logger that logs each message only once. */
	public SlimmingLogger(IConQATLogger logger) {
		messages = new LinkedHashMap<String, String>();
		this.logger = logger;
	}

	/**
	 * Creates new slimming logger that has a limited memory of
	 * <code>capacity</code> elements. Log messages are only logged if they
	 * haven't been logged as one of the last <code>capacity</code> messages.
	 */
	public SlimmingLogger(IConQATLogger logger, final int capacity) {
		messages = new LinkedHashMap<String, String>() {
			/** Version for serialization */
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(
					java.util.Map.Entry<String, String> eldest) {
				return size() > capacity;
			}
		};
		this.logger = logger;
	}

	/** {@inheritDoc} */
	@Override
	public ELogLevel getMinLogLevel() {
		return logger.getMinLogLevel();
	}

	/** {@inheritDoc} */
	@Override
	public void log(ELogLevel level, Object message) {
		if (add(level, message)) {
			logger.log(level, message);
		}

	}

	/** {@inheritDoc} */
	@Override
	public void log(ELogLevel level, Object message, Throwable throwable) {
		if (add(level, message)) {
			logger.log(level, message, throwable);
		}
	}

	/**
	 * Adds a log messages to the cache. Returns <code>true</code> if this is a
	 * new log message, <code>false</code> otherwise.
	 */
	private boolean add(ELogLevel level, Object message) {
		String entry = level.name() + ":" + message;
		return messages.put(entry, entry) == null;
	}

}