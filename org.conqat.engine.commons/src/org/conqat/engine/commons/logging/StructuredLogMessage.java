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
package org.conqat.engine.commons.logging;

import java.util.Arrays;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * The common base class used by all structured log messages. This class (as
 * well as all subclasses) should be immutable.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: E2862FB988FD1E84922234760D3277BE
 */
public class StructuredLogMessage {

	/** The message. */
	private final String message;

	/** The tags used. */
	private final List<String> tags;

	/** Constructor. */
	public StructuredLogMessage(String message, String... tags) {
		this.message = message;

		if (tags.length == 0) {
			// use reference to empty list to avoid creation of object
			this.tags = CollectionUtils.emptyList();
		} else {
			this.tags = Arrays.asList(tags);
		}
	}

	/**
	 * Returns a short message that summarizes the content of the log message
	 * for the user. May not return null. The default implementation returns
	 * {@link #getLoggedMessage()}, but subclasses may override.
	 */
	public String getUserSummaryMessage() {
		return getLoggedMessage();
	}

	/** Returns the tags. */
	public UnmodifiableList<String> getTags() {
		return CollectionUtils.asUnmodifiable(tags);
	}

	/**
	 * Returns the message that is written into the log. This may not return
	 * null.
	 */
	public String getLoggedMessage() {
		return message;
	}

	/** Forcing {@link #toString()} to return the {@link #getLoggedMessage()}. */
	@Override
	public final String toString() {
		return getLoggedMessage();
	}
}