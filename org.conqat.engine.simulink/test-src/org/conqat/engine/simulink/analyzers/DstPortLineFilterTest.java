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

import org.conqat.engine.simulink.filters.DstPortLineFilter;
import org.conqat.engine.simulink.scope.ISimulinkElement;
import org.conqat.engine.simulink.scope.ISimulinkResource;
import org.conqat.engine.simulink.util.SimulinkTestCaseBase;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * Test for {@link DstPortLineFilter}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 90BF2E3DFC3688D73638D42C7AFB701C
 */
public class DstPortLineFilterTest extends SimulinkTestCaseBase {

	/** Test for {@link DstPortLineFilter}. */
	public void test() throws Exception {
		ISimulinkResource resource = readSimulinkElement();

		ISimulinkElement modelElement = getModelElement(resource);
		assertEquals(3, SimulinkUtils.countLines(modelElement.getModel()));

		executeProcessor(DstPortLineFilter.class, "(input=(ref=", resource,
				"), pattern=(list=patList(enable)))");

		assertEquals(2, SimulinkUtils.countLines(modelElement.getModel()));
	}
}