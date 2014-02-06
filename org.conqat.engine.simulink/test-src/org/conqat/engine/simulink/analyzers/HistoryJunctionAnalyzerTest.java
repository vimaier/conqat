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
package org.conqat.engine.simulink.analyzers;

import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.simulink.scope.ISimulinkElement;
import org.conqat.engine.simulink.scope.ISimulinkResource;
import org.conqat.engine.simulink.util.SimulinkTestCaseBase;

/**
 * Test for {@link HistoryJunctionAnalyzer}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CDE00CEC858986938CDEA33EF4E84A11
 */
public class HistoryJunctionAnalyzerTest extends SimulinkTestCaseBase {

	/** Test for {@link HistoryJunctionAnalyzer}. */
	public void test() throws Exception {
		ISimulinkResource resource = readSimulinkElement();

		executeProcessor(HistoryJunctionAnalyzer.class, "(input=(ref=",
				resource, "))");

		ISimulinkElement modelElement = getModelElement(resource);
		FindingsList findings = NodeUtils.getFindingsList(modelElement,
				HistoryJunctionAnalyzer.KEY);

		assertNotNull(findings);
		assertEquals(1, findings.size());
	}
}