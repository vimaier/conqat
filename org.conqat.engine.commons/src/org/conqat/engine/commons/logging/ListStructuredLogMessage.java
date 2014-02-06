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

import java.util.Collection;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * A structured log message with an additional details list.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 998BC956C7EBFDF3DE0EAFFBA672FBF2
 */
public class ListStructuredLogMessage extends StructuredLogMessage {

	/** The details list for this message. */
	private final List<String> details;

	/** Constructor. */
	public ListStructuredLogMessage(String message, Collection<String> details,
			String... tags) {
		super(message, tags);
		this.details = CollectionUtils.sort(details);
	}

	/** Returns the details list. */
	public UnmodifiableList<String> getDetails() {
		return CollectionUtils.asUnmodifiable(details);
	}
}