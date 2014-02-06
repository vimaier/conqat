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
package org.conqat.engine.code_clones.result;

import java.io.File;
import java.util.List;

import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.core.logging.testutils.ConQATProcessorTestCaseBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.util.ResourceTraversalUtils;

/**
 * Test case for {@link CloneReportReaderProcessor}
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 4C32C233E9B55D9AB733C5140D585E5B
 */
public class CloneReportReaderProcessorTest extends ConQATProcessorTestCaseBase {

	/** Tests reading of a report */
	public void testReadReport() throws Exception {
		File reportFile = useTestFile("Clones.xml");

		CloneDetectionResultElement result = (CloneDetectionResultElement) executeProcessor(
				CloneReportReaderProcessor.class, "(report=(filename='"
						+ reportFile.getAbsolutePath()
						+ "'), 'artificial-elements'=(value=true))");

		assertEquals(2, result.getList().size());
		List<ITextElement> elements = ResourceTraversalUtils
				.listTextElements(result.getRoot());
		assertEquals(5, elements.size());
	}
}