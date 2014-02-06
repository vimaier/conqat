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
package org.conqat.engine.code_clones.core.report;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * This test ensures that the {@link CloneReportWriter} produces output that can
 * be read by the {@link CloneReportReader}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37613 $
 * @ConQAT.Rating GREEN Hash: 1BE2CA713F1A744F1F3A52A1A50ECD68
 */
public class CloneReportReaderWriterCompatibilityTest extends CCSMTestCaseBase {

	/** Tests compatibility. */
	public void testCompatibility() throws ConQATException, IOException {
		CloneReportReader reader1 = new CloneReportReader(
				useTestFile("Clones.xml"));

		File outputFile1 = new File(getTmpDirectory(), "report1.xml");
		writeReport(reader1, outputFile1);

		// first test: we expect this to read without errors
		CloneReportReader reader2 = new CloneReportReader(outputFile1);

		File outputFile2 = new File(getTmpDirectory(), "report2.xml");
		writeReport(reader2, outputFile2);

		// second test: we expect both file to be identical
		assertEquals(FileSystemUtils.readFileUTF8(outputFile1),
				FileSystemUtils.readFileUTF8(outputFile2));
	}

	/** Writes a report directly from a reader. */
	private void writeReport(CloneReportReader reader, File outputFile)
			throws ConQATException {
		Map<String, SourceElementDescriptor> descriptorMap = new HashMap<String, SourceElementDescriptor>();
		for (SourceElementDescriptor descriptor : reader
				.getSourceElementDescriptors()) {
			descriptorMap.put(descriptor.getUniformPath(), descriptor);
		}
		CloneReportWriter.writeReport(reader.getCloneClasses(), descriptorMap,
				reader.getRootValues(), reader.getSystemDate(), outputFile);
	}

}
