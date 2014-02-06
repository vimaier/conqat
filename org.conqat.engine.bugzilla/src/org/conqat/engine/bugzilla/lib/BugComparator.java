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
package org.conqat.engine.bugzilla.lib;

import java.util.Comparator;

/**
 * Compares bugs based on the value stored in a field.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46826 $
 * @ConQAT.Rating GREEN Hash: 2AAF95C3B97AE8DCE325DEE9A36E5166
 */
public class BugComparator implements Comparator<Bug> {

	/** Field used for comparison. */
	private final EBugzillaField field;

	/** Create comparator for field. */
	public BugComparator(EBugzillaField field) {
		this.field = field;
	}

	/**
	 * Compare by comparing the strings stored at the field.
	 */
	@Override
	public int compare(Bug bug1, Bug bug2) {
		String value1 = bug1.getValue(field);
		String value2 = bug2.getValue(field);

		if (value1 == null && value2 == null) {
			return 0;
		}

		if (value1 == null) {
			return 1;
		}

		if (value2 == null) {
			return -1;
		}

		return value1.compareTo(value2);
	}
}