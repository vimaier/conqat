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
package org.conqat.engine.dotnet.coverage;

import static org.conqat.engine.dotnet.coverage.ECoverageXmlElement.BlocksCovered;
import static org.conqat.engine.dotnet.coverage.ECoverageXmlElement.BlocksNotCovered;
import static org.conqat.engine.dotnet.coverage.ECoverageXmlElement.LinesCovered;
import static org.conqat.engine.dotnet.coverage.ECoverageXmlElement.LinesNotCovered;
import static org.conqat.engine.dotnet.coverage.ECoverageXmlElement.LinesPartiallyCovered;

import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.filesystem.CanonicalFile;

/**
 * Test case for {@link CoverageXmlReader}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43133 $
 * @ConQAT.Rating GREEN Hash: AE2AFB557902F864B667C2ED33D14460
 */
public class CoverageXmlReaderTest extends ResourceProcessorTestCaseBase {

	/** Tests that coverage file is read correctly */
	public void testReadCoverageFile() throws ConQATException {
		ListNode result = parseCoverageFile(
				useCanonicalTestFile("CoverageReport.xml"), false);

		ListNode[] modules = result.getChildren();
		assertEquals("Expecting 3 modules", 3, modules.length);

		// these values are taken from the xml report file
		assertCoverageNode(modules[0], "TestAppLibrary1.dll", 3, 0, 3, 3, 3);
		assertCoverageNode(modules[1], "CodeCoverageTestApp.exe", 14, 0, 4, 11,
				4);
		assertCoverageNode(modules[2], "TestAppLibrary2.dll", 3, 0, 6, 3, 6);
	}

	/** Tests that coverage file for a CPP program is read correctly */
	public void testReadCPPCoverageFile() throws ConQATException {
		ListNode result = parseCoverageFile(
				useCanonicalTestFile("CPPCoverageReport.xml"), true);

		ListNode[] modules = result.getChildren();
		assertEquals("Expecting 1 module", 1, modules.length);

		ListNode module = modules[0];
		assertCoverageNode(module, "Win32Test1.exe", 92, 13, 156, 154, 257);
		ListNode[] classes = module.getChildren();
		assertEquals(10, classes.length);
		assertEquals("<empty>", classes[1].getId());
		ListNode[] methods = classes[1].getChildren();
		assertEquals(15, methods.length);
		assertCoverageNode(methods[3], "doStuff", 6, 0, 0, 16, 0);

	}

	/** Parse coverage file into list node */
	public static ListNode parseCoverageFile(CanonicalFile report,
			boolean methodLevel) throws ConQATException {
		ITextElement reportElement = useTextElement(report);

		ListNode result = new ListNode();
		CoverageXmlReader reader = new CoverageXmlReader(reportElement, result,
				methodLevel);

		reader.parse();

		return result;
	}

	/** Assert that content of the given {@link ListNode} is as expected */
	private void assertCoverageNode(ListNode node, String name,
			int linesCovered, int linesPartiallyCovered, int linesNotCovered,
			int blocksCovered, int blocksNotCovered) {
		assertEquals(name, node.getId());

		assertEquals(linesCovered, node.getValue(LinesCovered.name()));
		assertEquals(linesPartiallyCovered,
				node.getValue(LinesPartiallyCovered.name()));
		assertEquals(linesNotCovered, node.getValue(LinesNotCovered.name()));
		assertEquals(blocksCovered, node.getValue(BlocksCovered.name()));
		assertEquals(blocksNotCovered, node.getValue(BlocksNotCovered.name()));

	}

}