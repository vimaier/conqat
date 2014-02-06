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

/**
 * This class is needed to annotate log messages with the name of the generating
 * processor.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D648DB22F4ED061A66A81C15D4B12D77
 */
/* package */class LogMessage {
	/** The original message. */
	private final Object message;

	/** Name of the generating processor. */
	private final String processorName;

	/**
	 * Create new log message
	 * 
	 * @param processorName
	 *            The original message.
	 * @param message
	 *            Name of the generating processor.
	 */
	/* package */LogMessage(String processorName, Object message) {
		this.message = message;
		this.processorName = processorName;

	}

	/** Get the original message. */
	public Object getMessage() {
		return message;
	}

	/** Get the name of the generating processor. */
	public String getProcessorName() {
		return processorName;
	}

	/**
	 * Returns &lt;message.toString()&gt; (&lt;processorName&gt;)
	 */
	@Override
	public String toString() {
		return message + " (" + processorName + ")";
	}
}