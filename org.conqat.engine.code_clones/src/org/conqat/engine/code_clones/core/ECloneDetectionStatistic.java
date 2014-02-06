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

/**
 * The statistics defined in this enumeration can be used to store statistic
 * values in the {@link CloneDetectionStatistics} component.
 * <p>
 * The main use of this enumeration is to provide strong typing for statistic
 * names, so that static code analysis can be used to find out which statistic
 * is used where.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: DED4B4AE86D372033443D3CC52EEDFD6
 */
public enum ECloneDetectionStatistic {

	/** Number of files processed during clone detection */
	FILE_COUNT("Processed files"),

	/** Number of units processed during clone detection */
	PROCESSED_UNIT_COUNT("Processed units"),

	/** Number of redundant units */
	REDUNDANT_UNIT_COUNT("Redundant units"),

	/** Number of clones */
	CLONE_COUNT("Clones"),

	/** Number of clone classes */
	CLONE_CLASSES_COUNT("Clone classes");

	/**
	 * Label used for printing the {@link ECloneDetectionStatistic} in the
	 * {@link #toString()} method.
	 */
	private final String label;

	/** Default constructor */
	private ECloneDetectionStatistic(String label) {
		this.label = label;
	}

	/** Gets name of the statistic in a human-friendly form */
	@Override
	public String toString() {
		return label;
	}
}