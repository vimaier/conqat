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
package org.conqat.engine.resource.analysis;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.conqat.engine.resource.scope.filesystem.FileSystemScope;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.ITextResource;

import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;

/**
 * Test the {@link LOCAnalyzer} class.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: D1AD1A1F2F1AEB78EA3943CD7A1E746D
 * 
 */
public class LOCAnalyzerTest extends ResourceProcessorTestCaseBase {

	/** Test if it works on empty files. */
	public void testEmptyFile() throws Exception {
		createRandomFileWithFixedLoc("testEmpty.txt", 0);
		int loc = obtainLOCValue("testEmpty.txt");
		assertEquals(0, loc);
	}

	/** Test if it works on a file with only one line. */
	public void testOneLine() throws Exception {
		createRandomFileWithFixedLoc("testOneLine.txt", 1);
		int loc = obtainLOCValue("testOneLine.txt");
		assertEquals(1, loc);
	}

	/** Test if it works on a file with a random number of lines. */
	public void testRandomLength() throws Exception {
		int length = new Random().nextInt(1000);
		createRandomFileWithFixedLoc("testRandom.txt", length);
		int loc = obtainLOCValue("testRandom.txt");
		assertEquals(length, loc);
	}

	/**
	 * Create a {@link FileSystemScope}, add test files to it and obtain the loc
	 * for the files.
	 * 
	 * @param filename
	 *            The name of the child to get the loc number for.
	 * @return The scope with the results added to the leaf's result list.
	 */
	private int obtainLOCValue(String filename) throws Exception {
		ITextResource textRoot = createTextScope(getTmpDirectory(),
				new String[] { filename }, null);

		LOCAnalyzer currentCounter = new LOCAnalyzer();
		currentCounter.setRoot(textRoot);
		currentCounter.init(new ProcessorInfoMock());
		ITextResource rootWithResults = (ITextResource) executeProcessor(
				LOCAnalyzer.class, "(input=(ref=", textRoot, "))");

		Map<String, ITextResource> map = TraversalUtils
				.createIdToNodeMap(rootWithResults);
		ITextResource childWithResult = map.get("TEST/" + filename);

		return NodeUtils.getValue(childWithResult, LOCAnalyzer.KEY,
				Number.class).intValue();
	}

	/**
	 * Create random file.
	 * <p>
	 * Random file content is generated deterministically by always using the
	 * same seed value, in order to make test reproducible.
	 */
	public void createRandomFileWithFixedLoc(String filename, int length)
			throws IOException {
		String[] lines = StringUtils.generateStringArray(length, 80, 0);
		String content = StringUtils.concat(lines, StringUtils.CR);
		createTmpFile(filename, content);
	}
}