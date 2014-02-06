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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.conqat.engine.core.logging.testutils.TestDriver;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.TestletBase;
import org.junit.Ignore;

/**
 * Testlet for the data-driven tests regarding the architecture assessment.
 * 
 * @author heineman
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E9F7E2A35F9EDA209C2258F1A45146A3
 */
// Ignore tells JUnit runner not to execute testlet
@Ignore
public class ArchitectureAssessmentScenarioTestlet extends TestletBase {

	/** The folder containing the test data. */
	private final File inputFolder;

	/** Constructor */
	public ArchitectureAssessmentScenarioTestlet(File inputFolder) {
		this.inputFolder = inputFolder;
	}

	/** {@inheritDoc} */
	@Override
	public void setName(String name) {
		File targetDirectory = targetDirectory();
		if (targetDirectory.exists()) {
			FileSystemUtils.deleteRecursively(targetDirectory);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void test() throws Exception {
		TestDriver driver = new TestDriver();
		File targetDirectory = targetDirectory();

		driver.drive(useTestFile("architecture_assessment.cqb")
				.getAbsolutePath(), "input.dir=" + useTestFile("java_project"),
				"output.dir=" + targetDirectory, "architecture.file="
						+ inputFolder.getAbsolutePath()
						+ "/architecture.architecture");
		File actualAssessmentFile = new File(targetDirectory,
				"architecture-assessment.xml");
		File exptectedAssessmentFile = new File(inputFolder,
				"expected_assessment.xml");

		// since assessment file content is not stable, we sort lines before
		// comparison
		assertLinesEqualIgnoringSequence(exptectedAssessmentFile,
				actualAssessmentFile);
	}

	/** Construct target directory */
	private File targetDirectory() {
		return new File(getTmpDirectory(), inputFolder.getName());
	}

	/** Compare lines in strings independent of order */
	private void assertLinesEqualIgnoringSequence(File expected, File actual)
			throws IOException {
		String expectedText = FileSystemUtils.readFile(expected);
		String actualText = FileSystemUtils.readFile(actual);
		String expectedSorted = sortLines(expectedText);
		String actualSorted = sortLines(actualText);
		if (!actualSorted.equals(expectedSorted)) {
			Assert.failNotEquals(null, expectedText, actualText);
		}
	}

	/** Sort lines in a string */
	private String sortLines(String text) {
		String[] lines = StringUtils.splitLines(text);
		List<String> sortedLines = CollectionUtils.sort(Arrays.asList(lines));
		return StringUtils.concat(sortedLines, StringUtils.CR);
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return inputFolder.getName();
	}

}