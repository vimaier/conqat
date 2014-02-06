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
package org.conqat.engine.core.logging.testutils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * Log4J appender used for smoke tests. This appender intercepts all logging
 * messages. It can be queried for warn or error messages. This is made a
 * singleton to avoid multiple instances to be appended.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 4AD61A54F40B39A058B2DD114EE2E97E
 */
public class TestAppender extends AppenderSkeleton {

	/** Congleton instance. */
	private static TestAppender instance = null;

	/** Stores events in a singleton manner */
	private final List<LoggingEvent> events = new ArrayList<LoggingEvent>();

	/** Constructor. */
	private TestAppender() {
		// avoid construction
	}

	/** Returns the singleton instance. */
	public static synchronized TestAppender getInstance() {
		if (instance == null) {
			instance = new TestAppender();
		}
		return instance;
	}

	/** Receives a logging event */
	@Override
	protected void append(LoggingEvent event) {
		events.add(event);
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		// Do nothing
	}

	/** {@inheritDoc} */
	@Override
	public boolean requiresLayout() {
		return false;
	}

	/** Clears all events */
	public void clearEvents() {
		events.clear();
	}

	/** Gets the events received by the {@link TestAppender} */
	public List<LoggingEvent> getEvents() {
		return CollectionUtils.asUnmodifiable(events);
	}

	/**
	 * Gets the events received by the {@link TestAppender} for a certain level
	 */
	public List<LoggingEvent> getEvents(Level level) {
		List<LoggingEvent> levelEvents = new ArrayList<LoggingEvent>();
		for (LoggingEvent event : events) {
			if (event.getLevel().equals(level)) {
				levelEvents.add(event);
			}
		}
		return levelEvents;
	}

	/** Checks whether the appender contains warn or error level events */
	public boolean containsErrorEvents() {
		for (LoggingEvent event : events) {
			if (event.getLevel().equals(Level.ERROR)) {
				return true;
			}
		}
		return false;
	}

}