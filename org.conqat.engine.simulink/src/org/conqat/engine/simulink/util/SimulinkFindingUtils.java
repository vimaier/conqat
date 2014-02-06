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
package org.conqat.engine.simulink.util;

import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.location.QualifiedNameLocation;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.simulink.scope.ISimulinkElement;

/**
 * Utility method for the Simulink bundle.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 15DDA9918B9947B9F13E563A5701B69A
 */
public class SimulinkFindingUtils {

	/**
	 * Create a finding and attach it to a model element.
	 * 
	 * @param group
	 *            the finding group the finding belongs to
	 * @param originTool
	 *            the class that best describes the creating tool.
	 * @param message
	 *            the message
	 * @param element
	 *            the affected model element
	 * @param id
	 *            the qualified name of the affected element, e.g. a Simulink
	 *            block
	 * @param key
	 *            the key used to store the finding
	 * @return the created finding
	 */
	public static Finding createAndAttachFinding(FindingGroup group,
			Class<?> originTool, String message, ISimulinkElement element,
			String id, String key) {
		Finding finding = group.createFinding(new QualifiedNameLocation(id,
				element.getLocation(), element.getUniformPath()));
		finding.setValue(EFindingKeys.MESSAGE.toString(), message);
		NodeUtils.getOrCreateFindingsList(element, key).add(finding);
		return finding;
	}
}