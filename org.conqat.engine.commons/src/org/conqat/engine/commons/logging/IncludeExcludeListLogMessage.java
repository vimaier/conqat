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
package org.conqat.engine.commons.logging;

import java.util.Collection;

/**
 * A log message for logging include/exclude information.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 6C4ED68CDFEC3555AB96DA2F6758D2BD
 */
public class IncludeExcludeListLogMessage extends ListStructuredLogMessage {

	/**
	 * Constructor.
	 * 
	 * @param type
	 *            the type of list (e.g. files, patterns, etc.); will be used in
	 *            the message.
	 * @param included
	 *            determines whether the string "included" or "excluded" appears
	 *            in the message.
	 * @param details
	 *            the list of objects included or excluded.
	 */
	public IncludeExcludeListLogMessage(String type, boolean included,
			Collection<String> details, String... tags) {
		super(determineMessage(type, determineLabel(included), details.size()),
				details, tags);
	}

	/** Determine included/excluded action label */
	private static String determineLabel(boolean included) {
		if (included) {
			return "Included ";
		}
		return "Excluded ";
	}

	/**
	 * Constructor.
	 * 
	 * @param type
	 *            the type of list (e.g. files, patterns, etc.); will be used in
	 *            the message.
	 * @param actionLabel
	 *            Label on what happened to the objects.
	 * @param details
	 *            the list of objects included or excluded.
	 */
	public IncludeExcludeListLogMessage(String type, String actionLabel,
			Collection<String> details, String... tags) {
		super(determineMessage(type, actionLabel, details.size()), details,
				tags);
	}

	/** Constructs the log message. */
	private static String determineMessage(String type, String actionLabel,
			int size) {
		StringBuilder sb = new StringBuilder();
		sb.append(actionLabel);
		if (!actionLabel.endsWith(" ")) {
			sb.append(" ");
		}
		sb.append(type);
		sb.append(": ");
		sb.append(size);
		return sb.toString();
	}

}