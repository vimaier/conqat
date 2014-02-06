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

/**
 * Base class for implementing an {@link IConQATLogger}. This implementation
 * forward calls to the individual logging methods to the
 * {@link #log(ELogLevel, Object)} and
 * {@link #log(ELogLevel, Object, Throwable)} methods.
 * 
 * @author Florian Deissenboeck
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 010F60E30D5401756A2E306BD0ECF4D7
 */
public abstract class ConQATLoggerBase implements IConQATLogger {

	/** Log message with level {@link ELogLevel#DEBUG}. */
	@Override
	public void debug(Object message) {
		log(ELogLevel.DEBUG, message);
	}

	/** Log message with level {@link ELogLevel#DEBUG}. */
	@Override
	public void debug(Object message, Throwable throwable) {
		log(ELogLevel.DEBUG, message, throwable);
	}

	/** Log message with level {@link ELogLevel#ERROR}. */
	@Override
	public void error(Object message) {
		log(ELogLevel.ERROR, message);
	}

	/** Log message with level {@link ELogLevel#ERROR}. */
	@Override
	public void error(Object message, Throwable throwable) {
		log(ELogLevel.ERROR, message, throwable);
	}

	/** Log message with level {@link ELogLevel#INFO}. */
	@Override
	public void info(Object message) {
		log(ELogLevel.INFO, message);
	}

	/** Log message with level {@link ELogLevel#INFO}. */
	@Override
	public void info(Object message, Throwable throwable) {
		log(ELogLevel.INFO, message, throwable);
	}

	/** Log message with level {@link ELogLevel#WARN}. */
	@Override
	public void warn(Object message) {
		log(ELogLevel.WARN, message);
	}

	/** Log message with level {@link ELogLevel#WARN}. */
	@Override
	public void warn(Object message, Throwable throwable) {
		log(ELogLevel.WARN, message, throwable);
	}
}