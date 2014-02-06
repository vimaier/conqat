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
package org.conqat.engine.dotnet.ila.scenario;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Integration test suite for the Intermediate Language Analyser (ILA). Executes
 * the {@link IlaTestlet} for every .dll file found in this test's test data
 * directory.
 * <p>
 * So in order to add an integration test, simply add a new .dll file!
 * 
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 4516C78BEFD46B36C9C3395BEF418735
 */
public class IlaTest extends CCSMTestCaseBase {

	/** Create a test suite. */
	public static Test suite() {
		File[] inputFiles = findIntgrationTestInputFiles();

		TestSuite suite = new TestSuite("IlaTest");
		suite
				.setName("Integration Test [" + inputFiles.length
						+ " test files]");
		for (File testFile : inputFiles) {
			suite.addTest(new IlaTestlet(testFile));
		}

		return suite;
	}

	/** Determines the files that serve as input for the integration tests */
	private static File[] findIntgrationTestInputFiles() {
		// we have to create an instance of IlaTest here, since useTestFile is
		// an instance name. This name must be static, since JUnit expects
		// suite methods to be static.
		File directory = new IlaTest().useTestFile("");
		return directory.listFiles(new FileExtensionFilter("dll"));
	}
}