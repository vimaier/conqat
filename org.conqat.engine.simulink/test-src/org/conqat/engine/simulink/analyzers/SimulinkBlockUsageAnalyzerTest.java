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

import org.conqat.engine.simulink.util.SimulinkTestCaseBase;
import org.conqat.lib.commons.collections.CounterSet;

/**
 * Test for {@link SimulinkBlockUsageAnalyzer}.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 959D55B2DB80992FD743C26D4EEB7CD9
 */
public class SimulinkBlockUsageAnalyzerTest extends SimulinkTestCaseBase {

	/** Test {@link SimulinkBlockUsageAnalyzer}. */
	@SuppressWarnings("unchecked")
	public void test() throws Exception {
		CounterSet<String> result = (CounterSet<String>) executeProcessor(
				SimulinkBlockUsageAnalyzer.class, "(input=(ref=",
				readSimulinkElement(), "))");

		assertEquals(187, result.getKeys().size());
		assertEquals(714, result.getTotal());
		assertEquals(266, result.getValue("Inport"));
	}
}