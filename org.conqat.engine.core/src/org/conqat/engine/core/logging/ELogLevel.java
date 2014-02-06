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

import java.util.EnumSet;

import org.apache.log4j.Level;

/**
 * Log levels used by the ConQAT logging facility.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A76E14ED338ED507635BC920CB152AD2
 */
public enum ELogLevel {
	/** Level all. */
	ALL(Level.ALL),

	/** Level off. */
	OFF(Level.OFF),

	/** Level debug. */
	DEBUG(Level.DEBUG),

	/** Level info. */
	INFO(Level.INFO),

	/** Level warn. */
	WARN(Level.WARN),

	/** Level error. */
	ERROR(Level.ERROR),

	/** Level fatal. */
	FATAL(Level.FATAL);

	/** The corresponding Log4J log level. */
	private final Level log4Jlevel;

	/** Create new level. */
	private ELogLevel(Level log4Level) {
		log4Jlevel = log4Level;
	}

	/** Get the corresponding Log4J log level. */
	/* package */Level getLog4JLevel() {
		return log4Jlevel;
	}

	/**
	 * Convert Log4J log level to {@link ELogLevel}.
	 * 
	 * @throws IllegalArgumentException
	 *             if an unknown Log4J log level was provided.
	 */
	@SuppressWarnings("static-access")
	/* package */static ELogLevel forLog4jLevel(Level level) {
		switch (level.toInt()) {
		case Level.ALL_INT:
			return ALL;
		case Level.OFF_INT:
			return OFF;
		case Level.DEBUG_INT:
			return DEBUG;
		case Level.WARN_INT:
			return WARN;
		case Level.ERROR_INT:
			return ERROR;
		case Level.INFO_INT:
			return INFO;
		case Level.FATAL_INT:
			return FATAL;
		default:
			throw new IllegalArgumentException("Unknown log level.");
		}

	}

	/** Return all log levels but {@link #OFF}. */
	public static EnumSet<ELogLevel> getProperLogLevels() {
		EnumSet<ELogLevel> result = EnumSet.allOf(ELogLevel.class);
		result.remove(OFF);
		return result;
	}

}