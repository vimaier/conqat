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
package org.conqat.engine.commons.util;

import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.logging.ELogLevel;

/**
 * {@ConQAT.Doc}
 * 
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: AC4459BB3169E3AB7DB3D1FD6C3FFD17
 */
@AConQATProcessor(description = "This processor logs the "
		+ "toString representation of the input with the specified "
		+ "log level and simply forwards the input.")
public class LoggingProcessor extends ConQATPipelineProcessorBase<Object> {

	/** The log level to use. */
	private ELogLevel logLevel = ELogLevel.INFO;

	/** Logging prefix */
	private String prefix = StringUtils.EMPTY_STRING;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "log-level", minOccurrences = 0, maxOccurrences = 1, description = "Log level to use [INFO]")
	public void setLogLevel(
			@AConQATAttribute(name = "value", description = "The level") ELogLevel logLevel) {
		this.logLevel = logLevel;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "prefix", minOccurrences = 0, maxOccurrences = 1, description = "Prefix for log message [<none>]")
	public void setPrefix(
			@AConQATAttribute(name = "value", description = "The prefix") String prefix) {
		this.prefix = prefix;
	}

	/** Log toString representation of input. */
	@Override
	protected void processInput(Object input) {
		getLogger().log(logLevel, prefix + input);
	}

}