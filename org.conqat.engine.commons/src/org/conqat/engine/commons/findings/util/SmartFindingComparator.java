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
package org.conqat.engine.commons.findings.util;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.findings.Finding;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Compares findings by location, unless they seem to mention a threshold. Then
 * they are compared by the corresponding value.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: C9EBCBF994B52A0034FFAB270D71EFE7
 */
public class SmartFindingComparator implements Comparator<Finding> {

	/** Singleton instance of this comparator */
	public static final SmartFindingComparator INSTANCE = new SmartFindingComparator();

	/** Pattern used to extract the threshold value. */
	private static final Pattern THRESHOLD_PATTERN = Pattern.compile(
			"threshold.*:\\s*(\\d+([.]\\d+))$", Pattern.CASE_INSENSITIVE);

	/** {@inheritDoc} */
	@Override
	public int compare(Finding finding1, Finding finding2) {
		double threshold1 = getThreshold(finding1);
		double threshold2 = getThreshold(finding2);

		if (threshold1 != threshold2) {
			// sort descending
			return Double.compare(threshold2, threshold1);
		}

		int result = finding1.getLocationString().compareTo(
				finding2.getLocationString());
		if (result != 0) {
			return result;
		}

		// Compare by dependency message.
		result = StringUtils.compare(finding1.getMessage(),
				finding2.getMessage());
		if (result != 0) {
			return result;
		}

		// Compare by dependency source.
		result = StringUtils.compare(finding1.getDependencySource(),
				finding2.getDependencySource());
		if (result != 0) {
			return result;
		}

		// Compare by dependency target.
		result = StringUtils.compare(finding1.getDependencyTarget(),
				finding2.getDependencyTarget());
		if (result != 0) {
			return result;
		}

		// So far, not all characteristics of the findings have been checked.
		// However, it is very unlikely that non-identical findings will reach
		// this point. Still, when really stable sorting is needed, the other
		// properties of the findings have to be checked here as well.
		return 0;
	}

	/**
	 * Checks whether this denotes a threshold finding and returns the threshold
	 * value in this case.
	 */
	private double getThreshold(Finding finding) {
		Matcher matcher = THRESHOLD_PATTERN.matcher(finding.getMessage());
		if (!matcher.find()) {
			// sort towards the end
			return -1;
		}

		// can not fail as we checked format in the regex
		return Double.parseDouble(matcher.group(1));
	}

}
