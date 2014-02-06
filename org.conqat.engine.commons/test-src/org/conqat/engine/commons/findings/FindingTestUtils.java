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
package org.conqat.engine.commons.findings;

import org.conqat.engine.commons.findings.location.ElementLocation;

/**
 * Utilities for tests involving findings.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 3DCAEC71C8315AD98DABC1FC516D0DE0
 */
public class FindingTestUtils {

	/** Count the number of findings in a report. */
	public static int countFindings(FindingReport report) {
		int count = 0;
		for (FindingCategory category : report.getChildren()) {
			for (FindingGroup group : category.getChildren()) {
				count += group.getChildrenSize();
			}
		}
		return count;
	}

	/** Count the number of locations of a certain type in a report. */
	public static <L extends ElementLocation> int countLocations(
			FindingReport report, Class<L> locationType) {
		int count = 0;
		for (FindingCategory category : report.getChildren()) {
			for (FindingGroup group : category.getChildren()) {
				for (Finding finding : group.getChildren()) {
					if (finding.getLocation().getClass() == locationType) {
						count++;
					}
				}
			}
		}
		return count;
	}
}