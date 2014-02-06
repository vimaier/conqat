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
package org.conqat.engine.code_clones.core;

import org.conqat.engine.core.core.ConQATException;

/**
 * Multi-purpose exception class used for exceptions that crop up during clone
 * detection.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43153 $
 * @ConQAT.Rating GREEN Hash: 95A655C6A50FBA1E20C02E7F29D4F1A4
 */
public class CloneDetectionException extends ConQATException {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Create new exception. */
	public CloneDetectionException(String message) {
		super(message);
	}

	/** Create new exception. */
	public CloneDetectionException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
