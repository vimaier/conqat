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

import org.conqat.engine.core.logging.ConQATLoggerBase;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * This logger collects all message at a specified level.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 79C56210E7EAFCC9F6C9E697FEF1EC3B
 */
public class CollectingLogger extends ConQATLoggerBase {

	/** The specified log level. */
	private final ELogLevel logLevel;

	/** The messages. */
	private final List<String> messages = new ArrayList<String>();

	/**
	 * Create new logger.
	 * 
	 * @param logLevel
	 *            the level the logger collects messages for.
	 */
	public CollectingLogger(ELogLevel logLevel) {
		this.logLevel = logLevel;
	}

	/** Get all messages. */
	public UnmodifiableList<String> getMessages() {
		return CollectionUtils.asUnmodifiable(messages);
	}

	/** Returns {@link ELogLevel#ALL} */
	@Override
	public ELogLevel getMinLogLevel() {
		return logLevel;
	}

	/** {@inheritDoc} */
	@Override
	public void log(ELogLevel level, Object message) {
		if (level == logLevel) {
			messages.add(message.toString());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void log(ELogLevel level, Object message, Throwable throwable) {
		log(level, message);
	}
}