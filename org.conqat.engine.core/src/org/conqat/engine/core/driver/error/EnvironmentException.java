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
 * Exceptions of this class signal configuration problems the execution
 * environment (as opposed to problems within processor definition or problems
 * within the config file).
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5288C8F41C7D540A9D281BABFDED49E9
 */
public class EnvironmentException extends DriverException {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Create new exception instance. */
	public EnvironmentException(EDriverExceptionType type, String message,
			Throwable cause, ErrorLocation... locations) {
		super(type, message, cause, locations);
	}

	/** Create new exception instance. */
	public EnvironmentException(EDriverExceptionType type, String message,
			IErrorLocatable locatable) {
		super(type, message, locatable);
	}

	/** Create new exception instance. */
	public EnvironmentException(EDriverExceptionType type, String message,
			ErrorLocation location) {
		super(type, message, location);
	}

}