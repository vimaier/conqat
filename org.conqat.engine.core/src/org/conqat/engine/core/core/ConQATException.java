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
package org.conqat.engine.core.core;

/**
 * Multi-purpose exception class that is thrown from a
 * {@link org.conqat.engine.core.core.IConQATProcessor}. Using the
 * ConQATException means that the processor at least handled the error in a
 * clean way, while other uncaught (runtime) exceptions usually indicate
 * implementation problems.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F66D2BF0AA3BB68ED26297B1A197F9B6
 */
public class ConQATException extends Exception {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Create a new exception. */
	public ConQATException(String message) {
		super(message);
	}

	/** Constructor. */
	public ConQATException(String message, Throwable cause) {
		super(message + "[" + cause.getMessage() + "]", cause);
	}

	/** Constructor. */
	public ConQATException(Throwable cause) {
		super(cause);
	}
}