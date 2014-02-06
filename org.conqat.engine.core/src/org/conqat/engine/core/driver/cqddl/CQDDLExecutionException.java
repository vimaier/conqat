/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.core.driver.cqddl;

/**
 * Exception used for all problems that are specific to the CQDDL-based
 * execution of processors.
 * 
 * @author $Author: heineman $
 * @version $Rev: 37939 $
 * @ConQAT.Rating GREEN Hash: 1A96353392F68F826411C0999B001CAF
 */
public class CQDDLExecutionException extends Exception {

	/** Serial version UID. */
	private static final long serialVersionUID = 1;

	/** Constructor. */
	/* package */CQDDLExecutionException(String message) {
		super(message);
	}

	/** Constructor. */
	/* package */CQDDLExecutionException(Throwable cause) {
		super(cause);
	}
}
