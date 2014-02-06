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

import org.conqat.lib.commons.logging.ILogger;

/**
 * This interface describes ConQAT loggers that are used to log information in
 * ConQAT processors. An instance of this interface is provided to every
 * processor via
 * {@link org.conqat.engine.core.core.IConQATProcessorInfo#getLogger()} .
 * 
 * 
 * @author Florian Deissenboeck
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 94F1EC0370CE684FE133DA3323F11EC8
 */
public interface IConQATLogger extends ILogger {

	/** Log message with the specified level. */
	public void log(ELogLevel level, Object message);

	/** Log message with the specified level. */
	public void log(ELogLevel level, Object message, Throwable throwable);

	/**
	 * Returns the minimal log level of this logger. For example a minimal log
	 * level of <code>warn</code> indicates that <code>warn</code>,
	 * <code>error</code>, and <code>fatal</code> messages will be logged, while
	 * <code>info</code> and <code>debug</code> will not.
	 */
	public ELogLevel getMinLogLevel();
}