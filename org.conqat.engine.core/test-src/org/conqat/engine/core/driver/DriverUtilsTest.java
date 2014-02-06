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
package org.conqat.engine.core.driver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Test the {@link DriverUtils}.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: AC43EB7A45E1DCA5FCF0751BAA79E274
 */
public class DriverUtilsTest extends CCSMTestCaseBase {

	/** Tests {@link DriverUtils#parseBlockExpression(String)}. */
	public void testParseBlockIdentifier() throws IOException {
		String name = "my.test.block";
		assertEquals(name, DriverUtils
				.parseBlockExpression(DriverUtils.BLOCK_IDENTIFIER_PREFIX
						+ name));
	}

	/** Simple case of run config loading. */
	public void testLoadProcessedRunConfigSimple() throws IOException {
		File file = useTestFile("runconfig1.cqr");
		// when no keyword is in the file, loadRunConfig should just return the
		// content
		assertEquals(FileSystemUtils.readLinesUTF8(file), DriverUtils
				.loadProcessedRunConfig(file));
	}

	/** Run config loading with multiple delegation. */
	public void testLoadProcessedRunConfigRedirect() throws IOException {
		File file = useTestFile("runconfig2.cqr");
		List<String> config = DriverUtils.loadProcessedRunConfig(file);

		assertEquals("rc3.block", DriverUtils.parseBlockExpression(config
				.get(0)));

		// prepare expected values (corresponding to test data
		Queue<String> expectQueue = new LinkedList<String>();
		for (int i : Arrays.asList(4, 3, 4, 4, 3, 2)) {
			expectQueue.add("rc" + i + ".a=A");
			expectQueue.add("rc" + i + ".b=B");
		}

		for (String line : config.subList(1, config.size())) {
			if (StringUtils.isEmpty(line)) {
				continue;
			}

			assertTrue(!expectQueue.isEmpty());
			assertEquals(expectQueue.poll(), line);
		}
		assertTrue(expectQueue.isEmpty());
	}

	/** Test loading in the presence of cycles. */
	public void testLoadProcessedRunConfigCycle() {
		File file = useTestFile("runconfig_cycle1.cqr");

		try {
			DriverUtils.loadProcessedRunConfig(file);
			fail("Expected exception because of cycle!");
		} catch (IOException e) {
			// expected
		}
	}
}