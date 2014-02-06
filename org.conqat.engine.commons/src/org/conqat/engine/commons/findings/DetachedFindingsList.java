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
package org.conqat.engine.commons.findings;

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * List of {@link DetachedFinding}s.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A3680DB66B63993A68BCBEA7FB78BDB4
 */
public class DetachedFindingsList extends ArrayList<DetachedFinding> implements
		IDeepCloneable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** {@inheritDoc} */
	@Override
	public DetachedFindingsList deepClone() {
		DetachedFindingsList result = new DetachedFindingsList();
		result.addAll(this);
		return result;
	}

	/** Add all findings as {@link DetachedFinding} to the list. */
	public void addAllFindings(List<Finding> findings) {
		for (Finding finding : findings) {
			add(new DetachedFinding(finding));
		}
	}
}
