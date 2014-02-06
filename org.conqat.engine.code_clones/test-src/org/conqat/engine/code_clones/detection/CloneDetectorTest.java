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
package org.conqat.engine.code_clones.detection;

import org.conqat.engine.code_clones.normalization.provider.UnitProviderMock;
import org.conqat.engine.commons.exceptions.EmptyInputException;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;

/**
 * Test case for {@link CloneDetector}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37139 $
 * @ConQAT.Rating GREEN Hash: E6767288BBBDC032098A2764D1E44307
 */
public class CloneDetectorTest extends ResourceProcessorTestCaseBase {

	/**
	 * This test case has been created for CR#1553 to ensure that the
	 * CloneDetector issues a sensible error message if it receives empty input.
	 */
	public void testErrorMessageForEmptyInput() throws ConQATException {
		CloneDetector cloneDetector = new CloneDetector();
		cloneDetector.init(new ProcessorInfoMock());

		cloneDetector.setInput(dummyTextElement());
		cloneDetector.setNormalization(new UnitProviderMock());
		cloneDetector.setMinLength(5);

		try {
			cloneDetector.process();
			fail("Expected to fail due to empty input");
		} catch (EmptyInputException e) {
			// expected
			assertTrue(e.getMessage().startsWith("Empty input"));
		}
	}
}