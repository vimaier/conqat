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

import java.util.Comparator;

import org.apache.log4j.spi.LoggingEvent;

/**
 * This class defines ConQAT logging events and thereby builds a shallow
 * abstraction layer on top of Log4J.
 * <p>
 * This class also provides static methods that return Comparators that can be
 * used to sort {@link ConQATLoggingEvent}s.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D0D03FF8FDD97117989C775FCE7FD342
 */
public class ConQATLoggingEvent {

	/**
	 * Processor name used for logging events that were not created by an
	 * {@link IConQATLogger}.
	 */
	public static final String UNSPECIFIED_PROCESSOR = "UNSPECIFIED";

	/**
	 * Get comparator that compares by level first, then by time.
	 */
	public static Comparator<ConQATLoggingEvent> getByLevelComparator() {
		return new LevelComparator();
	}

	/**
	 * Get comparator that compares by message first, then by time.
	 */
	public static Comparator<ConQATLoggingEvent> getByMessageComparator() {
		return new MessageComparator();
	}

	/**
	 * Get comparator that compares by processor name first, then by time.
	 */
	public static Comparator<ConQATLoggingEvent> getByProcessorComparator() {
		return new ProcessorComparator();
	}

	/**
	 * Get comparator that compares by time.
	 */
	public static Comparator<ConQATLoggingEvent> getByTimeComparator() {
		return new TimeComparator();
	}

	/** The log level. */
	private final ELogLevel level;

	/** The message itself. */
	private final Object message;

	/** Name of the processor that created the message. */
	private final String processorName;

	/** The time the message was logged. */
	private final long time;

	/**
	 * Create new logging event. If the provided Log4J logging event contains a
	 * {@link LogMessage} the processor name is extracted otherwise it's set to
	 * {@link #UNSPECIFIED_PROCESSOR}.
	 * 
	 * @param event
	 *            the underlying Log4J logging event
	 */
	/* package */ConQATLoggingEvent(LoggingEvent event) {
		if (event.getMessage() instanceof LogMessage) {
			LogMessage logMessage = (LogMessage) event.getMessage();
			processorName = logMessage.getProcessorName();
			message = checkNullMessage(logMessage.getMessage());
		} else {
			processorName = ConQATLoggingEvent.UNSPECIFIED_PROCESSOR;
			message = checkNullMessage(event.getMessage());
		}

		level = ELogLevel.forLog4jLevel(event.getLevel());
		time = event.timeStamp;
	}

	/** Checks for a null message and replaces it by a constant string. */
	private static Object checkNullMessage(Object message) {
		if (message == null) {
			return "no message";
		}
		return message;
	}

	/** Get log level. */
	public ELogLevel getLevel() {
		return level;
	}

	/** Get log message. */
	public Object getMessage() {
		return message;
	}

	/**
	 * Get processor name. If message was not logged by a {@link IConQATLogger}
	 * this returns {@value #UNSPECIFIED_PROCESSOR}.
	 */
	public String getProcessorName() {
		return processorName;
	}

	/** Get time the message was logged in milliseconds. */
	public long getTime() {
		return time;
	}

	/**
	 * Base class for comparators, that defines a template method for first
	 * level compare.
	 */
	private static abstract class ComparatorBase implements
			Comparator<ConQATLoggingEvent> {

		/** Compare by time. */
		protected static int compareByTime(ConQATLoggingEvent event1,
				ConQATLoggingEvent event2) {
			return (int) (event1.time - event2.time);
		}

		/** Compare using template method. If this returns 0, compare by time. */
		@Override
		public int compare(ConQATLoggingEvent event1, ConQATLoggingEvent event2) {
			int result = firstLevelCompare(event1, event2);
			if (result != 0) {
				return result;
			}
			return compareByTime(event1, event2);
		}

		/** Template method for compare. */
		protected abstract int firstLevelCompare(ConQATLoggingEvent event1,
				ConQATLoggingEvent event2);
	}

	/** Compares by level. */
	private static class LevelComparator extends ComparatorBase {
		/** {@inheritDoc} */
		@Override
		public int firstLevelCompare(ConQATLoggingEvent event1,
				ConQATLoggingEvent event2) {
			return -1 * event1.level.compareTo(event2.level);
		}
	}

	/** Compares by message string. */
	private static class MessageComparator extends ComparatorBase {
		/** {@inheritDoc} */
		@Override
		public int firstLevelCompare(ConQATLoggingEvent event1,
				ConQATLoggingEvent event2) {
			return event1.message.toString().compareToIgnoreCase(
					event2.message.toString());
		}
	}

	/** Compares by processor name. */
	private static class ProcessorComparator extends ComparatorBase {
		/** {@inheritDoc} */
		@Override
		public int firstLevelCompare(ConQATLoggingEvent event1,
				ConQATLoggingEvent event2) {
			return event1.processorName
					.compareToIgnoreCase(event2.processorName);
		}
	}

	/** Compares by time. */
	private static class TimeComparator implements
			Comparator<ConQATLoggingEvent> {
		/** {@inheritDoc} */
		@Override
		public int compare(ConQATLoggingEvent event1, ConQATLoggingEvent event2) {
			return ComparatorBase.compareByTime(event1, event2);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getMessage().toString() + " (processor: " + getProcessorName()
				+ ")";
	}

}