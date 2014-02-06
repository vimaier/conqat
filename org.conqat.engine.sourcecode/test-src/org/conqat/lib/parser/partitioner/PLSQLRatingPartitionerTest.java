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
package org.conqat.lib.parser.partitioner;

import java.io.File;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Tests the new {@link PLSQLRatingPartitioner}. This test is meant to check
 * compatibility to the old rating partitioner, so it is basically a regression
 * test.
 * 
 * @author $Author: goede $
 * @version $Rev: 40495 $
 * @ConQAT.Rating GREEN Hash: 2B974CD931585469F6818616AEABDDD5
 */
public class PLSQLRatingPartitionerTest extends CCSMTestCaseBase {

	/** Extension of the partition result files. */
	private static final String REF_EXTENSION = "partitions";

	/** Creates smoke test suite */
	public static Test suite() {
		// Switch to a non-static context, so we can use useTestFile() later on
		return new PLSQLRatingPartitionerTest().createSuite();
	}

	/** Creates smoke test suite */
	private TestSuite createSuite() {
		List<File> files = FileSystemUtils.listFilesRecursively(
				useTestFile("."), new FileExtensionFilter(REF_EXTENSION));
		TestSuite suite = new TestSuite("PLSQL Partitioner Smoke Test ["
				+ files.size() + " test files]");

		for (File refFile : files) {
			File codeFile = new File(StringUtils.stripSuffix("."
					+ REF_EXTENSION, refFile.getPath()));
			suite.addTest(new PLSQLRatingPartitionerTestlet(codeFile, refFile));
		}
		return suite;
	}
}