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
package org.conqat.engine.commons.statistics;

import org.conqat.engine.commons.test.ConQATCommonsProcessorTestCaseBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.math.Range;

/**
 * Tests the {@link NumberRangeFrequencyProcessor}.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37529 $
 * @ConQAT.Rating GREEN Hash: C1F54EBCFF2EC9C72FB113B488DF6879
 */
public class NumberRangeFrequencyProcessorTest extends
		ConQATCommonsProcessorTestCaseBase {

	/** Tests creation of auto ranges. */
	public void testAutoRange() throws ConQATException {
		NumberRangeFrequencyProcessor processor = new NumberRangeFrequencyProcessor();
		processor.addAutoRanges(10, 3, 20, true, true, true);
		processor.sortAndCheckRanges();

		assertEquals(6, processor.ranges.size());
		assertEquals(new Range(Double.NEGATIVE_INFINITY, false, 10, true),
				processor.ranges.get(0));
		for (int i = 1; i < 4; ++i) {
			assertEquals(new Range(7 + 3 * i, false, 10 + 3 * i, true),
					processor.ranges.get(i));
		}
		assertEquals(new Range(22, false, Double.POSITIVE_INFINITY, false),
				processor.ranges.get(5));
	}

	/** Tests the processor's core functionality. */
	public void testFrequency() throws ConQATException {
		Object input = parseCQDDL("listNode(root, (), "
				+ "listNode(leaf1, (key=42)), "
				+ "listNode(leaf2, (key=3.3333)), "
				+ "listNode(leaf3, (key=25)) )");

		Object result = executeProcessor(NumberRangeFrequencyProcessor.class,
				"(input=(ref=", input,
				"), key=(key=key), range=(lower=3.2, upper=33.))");

		assertTrue(result instanceof KeyedData<?>);

		KeyedData<?> keyedData = (KeyedData<?>) result;
		assertEquals(2, keyedData.getValues().size());
		assertEquals(2.,
				keyedData.getValues().get(new Range(3.2, false, 33, true)));
		assertEquals(1., keyedData.getValues().get(new Range(42, 42)));
	}
}
