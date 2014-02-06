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
package org.conqat.engine.commons.filter;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author Elmar Juergens
 * @author juergens
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @levd.rating GREEN Hash: 3A6513EF0DEE5B0F6FC3C1B0429218DD
 */
@AConQATProcessor(description = "This filter allows to set an upper limit and filters "
		+ "all nodes above the limit. This can e.g. be used to trim a filesystem scope to"
		+ "a certain size w.r.t. to LOC, bytes or some other measure. The values of each "
		+ "node are summated. The filter works on leave nodes only.")
public class ThresholdFilter extends FilterBase<IRemovableConQATNode> {

	/** The filter threshold. */
	private double threshold;

	/** The key to read. */
	private String key;

	/** The current value. */
	private double currentValue = 0;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "threshold", description = "Upper limit for filtering.", minOccurrences = 1, maxOccurrences = 1)
	public void setThreshold(
			@AConQATAttribute(name = "value", description = "Upper limit.") double threshold) {
		this.threshold = threshold;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The key to read.")
	public void setReadKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		this.key = key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isFiltered(IRemovableConQATNode node) {

		if (node.hasChildren()) {
			return false;
		}

		if (currentValue > threshold) {
			return true;
		}

		try {
			currentValue += NodeUtils.getDoubleValue(node, key);
		} catch (ConQATException e) {
			getLogger().warn(e.getMessage(), e);
		}

		return false;
	}

}