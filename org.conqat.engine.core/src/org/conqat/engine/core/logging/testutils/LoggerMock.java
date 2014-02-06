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

import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.lib.commons.logging.SimpleLogger;

/**
 * Simple ConQAT logger for testing purposes that writes all messages to
 * standard out.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7144652DB4ED76239B14AD42F2D23A4C
 */
public class LoggerMock extends SimpleLogger implements IConQATLogger {

	/** Logs as info. */
	@Override
	public void log(ELogLevel level, Object message) {
		info(level + ": " + message);
	}

	/** Logs as info. */
	@Override
	public void log(ELogLevel level, Object message, Throwable throwable) {
		info(level + ": " + message, throwable);
	}

	/** {@inheritDoc} */
	@Override
	public ELogLevel getMinLogLevel() {
		return ELogLevel.DEBUG;
	}

}