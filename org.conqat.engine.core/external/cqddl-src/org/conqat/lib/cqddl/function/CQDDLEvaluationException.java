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
package org.conqat.lib.cqddl.function;

/**
 * Exception used to indicate problems during evaluation of a
 * {@link ICQDDLFunction}.
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 36296 $
 * @ConQAT.Rating YELLOW Hash: 5844009DEDB95B095CB57B9810C2D8D6
 */
public class CQDDLEvaluationException extends Exception {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Constructor. */
	public CQDDLEvaluationException(String message) {
		super(message);
	}

	/** Constructor. */
	public CQDDLEvaluationException(String message, Throwable cause) {
		super(message, cause);
	}

	/** Constructor. */
	public CQDDLEvaluationException(Throwable cause) {
		super(cause);
	}
}