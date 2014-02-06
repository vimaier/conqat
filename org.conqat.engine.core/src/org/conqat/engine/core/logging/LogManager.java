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

import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.conqat.engine.core.driver.ConQATInstrumentation;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.collections.UnmodifiableSet;

/**
 * A Log4J appender that collects log messages and stores them. This allows
 * processors like the HTML presentation to display log messages.
 * 
 * @author Benjamin Hummel
 * @author Lukas Kuhn
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3D81D46E1403791F91C5EB0437266136
 */
public class LogManager extends AppenderSkeleton {

	/** Maps from processor names to list of logging events. */
	private final ListMap<String, ConQATLoggingEvent> conqatLogMessages = new ListMap<String, ConQATLoggingEvent>();

	/** The instrumentation used. */
	private final ConQATInstrumentation instrumentation;

	/** Constructor. */
	public LogManager(ConQATInstrumentation instrumentation) {
		this.instrumentation = instrumentation;
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		// nothing to do
	}

	/**
	 * Get the names of all processor that created log events.
	 */
	public UnmodifiableSet<String> getLoggingProcessors() {
		return CollectionUtils.asUnmodifiable(conqatLogMessages.getKeys());
	}

	/**
	 * Get all logging events.
	 * 
	 * @return the events, or an empty list if no events occurred.
	 */
	public UnmodifiableList<ConQATLoggingEvent> getLoggingEvents() {
		List<ConQATLoggingEvent> events = conqatLogMessages.getValues();

		if (events == null) {
			return CollectionUtils.emptyList();
		}

		return CollectionUtils.asUnmodifiable(events);
	}

	/**
	 * Get all logging events created by a specific processor.
	 * 
	 * @param processorName
	 *            name of the processor
	 * @return the events, or an empty list if no events occurred.
	 */
	public UnmodifiableList<ConQATLoggingEvent> getLogMessages(
			String processorName) {
		List<ConQATLoggingEvent> logMessages = conqatLogMessages
				.getCollection(processorName);

		if (logMessages == null) {
			return CollectionUtils.emptyList();
		}

		return CollectionUtils.asUnmodifiable(logMessages);
	}

	/** {@inheritDoc} */
	@Override
	public boolean requiresLayout() {
		// we want layout
		return true;
	}

	/** {@inheritDoc} */
	@Override
	protected void append(LoggingEvent event) {
		ConQATLoggingEvent conqatLoggingEvent = new ConQATLoggingEvent(event);

		conqatLogMessages.add(conqatLoggingEvent.getProcessorName(),
				conqatLoggingEvent);

		instrumentation.eventLogged(conqatLoggingEvent);
	}
}