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
package org.conqat.engine.dotnet.test;

import java.io.File;
import java.util.List;

import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.ITextResource;

import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;

/**
 * Test for the {@link TrxFileReader}.
 * 
 * @author Martin Feilkas
 * @author feilkas
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 06345468F6F5DC157CE25F13C7B37491
 */
// @org.junit.Ignore
public class TrxFileReaderTest extends ResourceProcessorTestCaseBase {

	/** Test the TrxFileReader with all trx-files in the test-data directory. */
	public void testTrxFileReader() throws Exception {
		File testFile = useTestFile("example-trx-report.trx");

		// execute processor
		TrxFileReader trxReader = new TrxFileReader();
		trxReader.init(new ProcessorInfoMock());

		ITextResource root = createTextScope(testFile.getParentFile(),
				new String[] { "**/*.trx" }, new String[0]);
		trxReader.setReportFilesRoot(root);
		ListNode rootNode = trxReader.process();

		// count passed / failed
		List<ListNode> testNodes = TraversalUtils
				.listLeavesDepthFirst(rootNode);
		int passed = 0;
		int failed = 0;
		for (ListNode testNode : testNodes) {
			String outcome = testNode.getValue(TrxFileReader.OUTCOME_KEY)
					.toString();
			if (outcome.equals("Passed")) {
				passed++;
			} else if (outcome.equals("Failed")) {
				failed++;
			}
		}

		// compare results
		assertEquals(22, testNodes.size());
		assertEquals(21, passed);
		assertEquals(1, failed);
	}
}