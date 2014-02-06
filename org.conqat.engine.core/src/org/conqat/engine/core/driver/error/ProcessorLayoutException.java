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
package org.conqat.engine.core.driver.error;

/**
 * Exceptions of this class signal configuration problems <i>within</i> the
 * processor definition (as opposed to problems within the configuration file or
 * problems regarding the execution environment).
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E17F8737F4CCEE6F4609E099036B9264
 */
public class ProcessorLayoutException extends DriverException {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Create new exception instance. */
	public ProcessorLayoutException(EDriverExceptionType type, String message,
			IErrorLocatable locatable) {
		super(type, message, locatable);
	}

	/** Create new exception instance. */
	public ProcessorLayoutException(EDriverExceptionType type, String message,
			Throwable cause, IErrorLocatable locatable) {
		super(type, message, cause, locatable);
	}
}