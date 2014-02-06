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
package org.conqat.engine.commons.mark;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35819 $
 * @ConQAT.Rating GREEN Hash: 1A070F0781F9D7418025D05019ADFA44
 */
@AConQATProcessor(description = "Marks all nodes whose id matches one of the given regular expressions.")
public class IdMarker extends MarkerBase<IConQATNode> {

	/** {@inheritDoc} */
	@Override
	protected String defaultLogCaption() {
		return "Node id";
	}

	/** {@inheritDoc} */
	@Override
	protected String getNodeStringToMatch(IConQATNode node) {
		return node.getId();
	}
}
