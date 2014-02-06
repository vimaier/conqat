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
package org.conqat.engine.commons.date;

import java.util.Date;

import org.conqat.engine.commons.filter.KeyBasedFilterBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: C6E80A66095836BCD9B6C78A121FF9A7
 */
@AConQATProcessor(description = "This filter filters nodes with a "
		+ "date in the specified range. The given dates are excluded.")
public class DateRangeFilter extends
		KeyBasedFilterBase<Date, IRemovableConQATNode> {

	/** Start of the range. */
	private Date startDate;

	/** End of the range. */
	private Date endDate;

	/** Target nodes this filter operates on. */
	private ETargetNodes targetNodes;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "start-date", minOccurrences = 1, maxOccurrences = 1, description = "Start of the date range.")
	public void setStartDate(
			@AConQATAttribute(name = "value", description = "Date") Date date) {
		startDate = date;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "end-date", minOccurrences = 1, maxOccurrences = 1, description = "End of the date range.")
	public void setEndDate(
			@AConQATAttribute(name = "value", description = "Date") Date date) {
		endDate = date;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "target", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The target nodes to operate on.")
	public void setTargets(
			@AConQATAttribute(name = "nodes", description = "the nodes this operation targets") ETargetNodes targets) {
		targetNodes = targets;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		if (targetNodes == null) {
			return super.getTargetNodes();
		}

		return targetNodes;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFilteredForValue(Date value) {
		return value.after(startDate) && value.before(endDate);
	}

}