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
package org.conqat.engine.io;

import java.util.Map;

import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;

/**
 * Test case for {@link CSVReader}
 * 
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 8A2DFC572F070BE64EB401E028F182D5
 */
public class CSVReaderTest extends CCSMTestCaseBase {

	/** ID for node corresponding with line 2 in the test file */
	private static final String ID_LINE_2 = "/org.eclipse.jdt.junit/src/org/eclipse/jdt/internal/junit/launcher/JUnitBaseLaunchConfiguration.java";

	/** ID for node corresponding with line 32 in the test file */
	private static final String ID_LINE_32 = "/org.eclipse.jdt.junit/src/org/eclipse/jdt/internal/junit/ui/TestRunnerViewPart.java";

	/** Read a CSV file */
	public void testReadCSVFile() throws Exception {

		String filename = useTestFile("eclipse-metrics-fragment.csv")
				.getAbsolutePath();

		// set up reader
		CSVReader reader = new CSVReader();
		reader.init(new ProcessorInfoMock());
		reader.setFile(filename, "filename");
		reader.setSeparator(";");
		reader.addType("pre", "int");
		reader.addType("post", "int");

		// read csv file
		IConQATNode listRoot = reader.process();

		// assert correct number of nodes
		Map<String, IConQATNode> nodes = TraversalUtils
				.createIdToNodeMap(listRoot);
		assertEquals(101, nodes.size()); // 1 root + 100 children

		// assert correct retrieval and values for line 2
		assertEquals(nodes.get(ID_LINE_2).getValue("pre"), 1);
		assertEquals(nodes.get(ID_LINE_2).getValue("post"), 0);

		// assert correct retrieval and values for line 32
		assertEquals(nodes.get(ID_LINE_32).getValue("pre"), 4);
		assertEquals(nodes.get(ID_LINE_32).getValue("post"), 0);
	}

}