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
package org.conqat.engine.architecture.assessment.scenario;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.conqat.lib.commons.filesystem.DirectoryOnlyFilter;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Executes data-driven tests for the architecture assessment.
 * 
 * @author heineman
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 89B388CEC8706D2C2FD476D14F9AD35D
 */
public class ArchitectureAssessmentScenarioTest extends CCSMTestCaseBase {

	/** Create test suite. */
	public static Test suite() {
		// find files
		File[] inputFolders = findTestFolders();

		// create suite
		TestSuite suite = new TestSuite(
				ArchitectureAssessmentScenarioTest.class.getName());
		for (File inputFolder : inputFolders) {
			if (!inputFolder.getName().equals(".svn")) {
				suite.addTest(new ArchitectureAssessmentScenarioTestlet(
						inputFolder));
			}
		}

		// return suite
		return suite;
	}

	/**
	 * @return all test folders.
	 */
	private static File[] findTestFolders() {
		File directory = new ArchitectureAssessmentScenarioTest()
				.useTestFile("testcases");
		return directory.listFiles(new DirectoryOnlyFilter());
	}

}