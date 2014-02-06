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
package org.conqat.engine.dotnet.coverage;

import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.core.core.ConQATException;

/**
 * Class for storing coverage information.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44377 $
 * @ConQAT.Rating GREEN Hash: DF53D71E18A034356AEF20AA6387E903
 */
public class CoverageInfo {

	/** The number of lines covered. */
	private final int linesCovered;

	/** The number of lines that are partially covered. */
	private final int linesPartiallyCovered;

	/** The number of uncovered lines. */
	private final int linesUncovered;

	/** Constructor. */
	public CoverageInfo(ListNode node) throws ConQATException {
		this(node, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param node
	 *            the node containing the coverage information.
	 * @param mergeNode
	 *            if this is non-null, the coverage information is merged by
	 *            adding up the values for each field.
	 */
	public CoverageInfo(ListNode node, CoverageInfo mergeNode)
			throws ConQATException {
		int linesCovered = extractInt(node, CoverageReportReader.LINES_COVERED);
		int linesPartiallyCovered = extractInt(node,
				CoverageReportReader.LINES_PARTIALLY_COVERED);
		int linesUncovered = extractInt(node,
				CoverageReportReader.LINES_NOT_COVERED);

		if (mergeNode != null) {
			linesCovered += mergeNode.linesCovered;
			linesPartiallyCovered += mergeNode.linesPartiallyCovered;
			linesUncovered += mergeNode.linesUncovered;
		}

		this.linesCovered = linesCovered;
		this.linesPartiallyCovered = linesPartiallyCovered;
		this.linesUncovered = linesUncovered;
	}

	/**
	 * Extracts the int value stored at the given key or throws an exception.
	 */
	private int extractInt(ListNode node, String key) throws ConQATException {
		Object value = node.getValue(key);
		if (value instanceof Integer) {
			return (Integer) value;
		}

		throw new ConQATException("No coverage information found in "
				+ node.getId() + ". Missing key " + key);
	}

	/**
	 * Returns the coverage as a value between 0 and 1. Partially covered lines
	 * are counted as 50% covered.
	 */
	public double getCoverage() {
		int size = getSizeInLines();
		if (size == 0) {
			return 0;
		}

		return (linesCovered + .5 * linesPartiallyCovered) / size;
	}

	/** Returns the sum of covered, partially covered, and uncovered lines. */
	public int getSizeInLines() {
		return linesCovered + linesPartiallyCovered + linesUncovered;
	}
}