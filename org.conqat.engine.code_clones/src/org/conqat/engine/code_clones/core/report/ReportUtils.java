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
package org.conqat.engine.code_clones.core.report;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility functions for reading and writing clone reports.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 0C9DC91F3DE51F4CC4A723779DB64A07
 */
public class ReportUtils {

	/** Creates a gap string for a gapped clone. */
	public static String createGapOffsetString(Clone clone) {
		StringBuilder builder = new StringBuilder();
		for (Region gap : clone.getGapPositions()) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(gap.getStart() + "-" + gap.getEnd());
		}
		return builder.toString();
	}

	/**
	 * Reads gap information from a gap offset string and stores it in a
	 * {@link Clone}
	 */
	public static void parseGapOffsetString(Clone clone, String gapOffsetsString) {
		if (StringUtils.isEmpty(gapOffsetsString)) {
			return;
		}

		for (String gapDigest : gapOffsetsString.split(",")) {
			String[] gapDigestParts = gapDigest.split("-");
			CCSMAssert.isTrue(gapDigestParts.length == 2,
					"Unexpected gap region format: " + gapDigest);
			int startOffset = Integer.parseInt(gapDigestParts[0]);
			int endOffset = Integer.parseInt(gapDigestParts[1]);
			clone.addGap(new Region(startOffset, endOffset));
		}
	}

}