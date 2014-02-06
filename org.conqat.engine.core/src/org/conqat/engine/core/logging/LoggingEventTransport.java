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

import java.io.Serializable;

/**
 * This class represents a logging event which can be serialized.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 06115BC04932AFD641428F0A936DDB12
 */
public class LoggingEventTransport implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Name of the processor. */
	private final String processorName;

	/** The log message. */
	private final String message;

	/** The log level of the message. */
	private final ELogLevel level;

	/** The time. */
	private final long time;

	/** Constructor. */
	public LoggingEventTransport(ConQATLoggingEvent event) {
		this(event.getProcessorName(), event.getMessage(), event.getLevel(),
				event.getTime());
	}

	/** Constructor */
	public LoggingEventTransport(String processorName, Object message,
			ELogLevel level, long time) {
		this.processorName = processorName;

		if (message == null) {
			this.message = null;
		} else {
			this.message = message.toString();
		}
		this.level = level;
		this.time = time;
	}

	/** Returns name of the processor. */
	public String getProcessorName() {
		return processorName;
	}

	/** Returns the log message. */
	public String getMessage() {
		return message;
	}

	/** Returns the log level of the message. */
	public ELogLevel getLevel() {
		return level;
	}

	/** Returns the time. */
	public long getTime() {
		return time;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return message;
	}
}