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
package org.conqat.lib.simulink.builder;

/**
 * Exception thrown by the MDL parser. Exceptions may wrap other exceptions. *
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 773B72B17804C69C13E7656BF2BF75B2
 */
@SuppressWarnings("serial")
public class MDLParserException extends Exception {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** number of line where exception occurred. */
	private int lineNumber;

	/** number of column where exception occurred. */
	private int columnNumber;

	/**
	 * Create new parser exception.
	 */
	public MDLParserException(String message) {
		super(message);
	}

	/**
	 * Create new parser exception.
	 * 
	 * @param message
	 *            exception description
	 * @param lineNumber
	 *            line in the input file where problem occurred
	 * @param columnNumber
	 *            column in the input file where problem occurred
	 */
	public MDLParserException(String message, int lineNumber, int columnNumber) {
		super(message + " [line: " + lineNumber + ", col: " + columnNumber
				+ "]");
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	/**
	 * Create new parser exception wrapping another exception.
	 */
	public MDLParserException(Exception exception) {
		super("Unknown Exception caused by: " + exception.getMessage(),
				exception);
	}

	/**
	 * Get line in the input file where problem occurred.
	 */
	public int getColumnNumber() {
		return columnNumber;
	}

	/**
	 * Get column in the input file where problem occurred.
	 */
	public int getLineNumber() {
		return lineNumber;
	}

}