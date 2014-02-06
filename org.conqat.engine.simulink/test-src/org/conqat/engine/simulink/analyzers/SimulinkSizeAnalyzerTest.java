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

import org.conqat.engine.simulink.scope.ISimulinkElement;
import org.conqat.engine.simulink.scope.ISimulinkResource;
import org.conqat.engine.simulink.util.SimulinkTestCaseBase;

/**
 * Test for {@link SimulinkSizeAnalyzer}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 504AEE6178B40F871EB7C0ACC4289965
 */
public class SimulinkSizeAnalyzerTest extends SimulinkTestCaseBase {

	/** Test data file names. */
	private final String[] filenames = { getClass().getSimpleName() + "01",
			getClass().getSimpleName() + "02" };

	/** Test for {@link SimulinkSizeAnalyzer}. */
	public void test() throws Exception {
		ISimulinkResource resource = readSimulinkElement(filenames);

		executeProcessor(SimulinkSizeAnalyzer.class, "(input=(ref=", resource,
				"))");

		ISimulinkElement modelElement = getModelElement(resource, filenames[0]);
		assertEquals(34, modelElement.getValue(SimulinkSizeAnalyzer.BLOCKS_KEY));
		assertEquals(2, modelElement.getValue(SimulinkSizeAnalyzer.CHARTS_KEY));
		assertEquals(6, modelElement.getValue(SimulinkSizeAnalyzer.STATES_KEY));

		modelElement = getModelElement(resource, filenames[1]);
		assertEquals(34, modelElement.getValue(SimulinkSizeAnalyzer.BLOCKS_KEY));
		assertEquals(2, modelElement.getValue(SimulinkSizeAnalyzer.CHARTS_KEY));
		assertEquals(6, modelElement.getValue(SimulinkSizeAnalyzer.STATES_KEY));
	}
}