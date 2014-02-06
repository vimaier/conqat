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

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Tests the {@link CloneReportReader}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37613 $
 * @ConQAT.Rating GREEN Hash: B22BA76331164B0C090AFE20BE54938A
 */
public class CloneReportReaderTest extends CCSMTestCaseBase {

	/** Tests a basic read operation. */
	public void testRead() throws ConQATException {
		CloneReportReader reportReader = new CloneReportReader(
				useTestFile("Clones.xml"));

		assertTrue(reportReader.getRootValues().containsValue("foo"));
		assertEquals(42, reportReader.getRootValues().getInt("foo"));

		assertEquals(5, reportReader.getSourceElementDescriptors().size());

		SourceElementDescriptor descriptor = reportReader
				.getSourceElementDescriptors().get(4);
		assertEquals(212, descriptor.getLength());
		assertEquals("F127", descriptor.getFingerprint());
		assertEquals("UNRATED", descriptor.getString("rating"));
		assertEquals(42, descriptor.getInt("foo"));

		assertEquals(2, reportReader.getCloneClasses().size());

		CloneClass cloneClass = reportReader.getCloneClasses().get(1);
		assertEquals(4, cloneClass.size());
		assertEquals("UNRATED", cloneClass.getString("rating"));
	}

}
