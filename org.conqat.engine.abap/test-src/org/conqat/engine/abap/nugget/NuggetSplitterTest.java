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
package org.conqat.engine.abap.nugget;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.lib.commons.collections.Pair;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Test case for the {@link NuggetSplitter}.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 43128 $
 * @ConQAT.Rating GREEN Hash: 6E3F472AFB0472C8AFE03F2185DD7388
 */
public class NuggetSplitterTest extends ResourceProcessorTestCaseBase {

	/**
	 * Test exception-free execution of the {@link NuggetSplitter} and assert
	 * expected number of files.
	 */
	public void testNuggetSplitter() throws Exception {
		File resultDir = executeNuggetSplitter("./std", null, null, -1, null);
		assertNumberOfAbapFiles(resultDir, 232);

		// check that no package directories are created
		assertFileExists(resultDir, "dir/ZAKE/PROG/Z_ZAKE_SVN_TEST.abap");

	}

	/**
	 * Test for dealing with nuggets holding corrupt XML. Test for throwing
	 * {@link ConQATException} if wrong encoding is not corrected.
	 */
	public void testNuggetSplitterFailsWithCorruptXml() throws Exception {
		try {
			executeNuggetSplitter("./corrupted", null, null, -1, null);
		} catch (ConQATException e) {
			Assert.assertEquals("Content is not allowed in prolog.", e
					.getCause().getMessage());
		}
	}

	/**
	 * Test for dealing wit nuggets holding corrupt XML (ascii0 and wrong
	 * encoding).
	 */
	public void testNuggetSplitterEncodingCorrection() throws Exception {
		File resultDir = executeNuggetSplitter("./corrupted", "iso-8859-1",
				null, -1, null);
		assertNumberOfAbapFiles(resultDir, 3);
	}

	/**
	 * Tests NuggetSplitter package creation.
	 * 
	 * @throws Exception
	 */
	public void testNuggetSplitterPackages() throws Exception {
		File resultDir = executeNuggetSplitter("./std", null, "_", -1, null);
		assertNumberOfAbapFiles(resultDir, 232);
		assertFileExists(resultDir,
				"dir/ZAKE/PROG/Z/ZAKE/SVN/Z_ZAKE_SVN_TEST.abap");

		resultDir = executeNuggetSplitter("./std/dir", null, "_", 3, null);
		assertFileExists(resultDir, "ZAKE/PROG/Z/ZAKE/Z_ZAKE_SVN_TEST.abap");
		assertFileExists(resultDir, "ZAKE/PROG/ZSVNTESTPROG.abap");

		resultDir = executeNuggetSplitter("./std/dir", null, "_", 3,
				new Pair<Integer, Integer>(1, 1));
		assertFileExists(resultDir, "ZAKE/PROG/Z/Z/AKE/Z_ZAKE_SVN_TEST.abap");
		assertFileExists(resultDir, "ZAKE/PROG/ZSVNTESTPROG.abap");
	}

	/**
	 * Helper method to execute NuggetSplitter.
	 */
	private File executeNuggetSplitter(String testInputDirectory,
			String encoding, String packageSeparator, int packageDepth,
			Pair<Integer, Integer> packageNaming) throws ConQATException {

		File tmpDirectory = getTmpDirectory();
		if (tmpDirectory.exists()) {
			FileSystemUtils.deleteRecursively(getTmpDirectory());
		}

		ITextResource root = createTextScope(useTestFile(testInputDirectory),
				new String[] { "**/*.nugg" }, null);

		Object[] args = buildArguments(root, tmpDirectory, encoding,
				packageSeparator, packageDepth, packageNaming);

		executeProcessor(NuggetSplitter.class, args);

		return tmpDirectory;
	}

	/**
	 * Builds the argument array for NuggetSplitter, depending on given method
	 * parameters. For unused parameters use <code>null</code> or -1.
	 */
	private Object[] buildArguments(ITextResource root, File output,
			String encodingFix, String packageSeparator, int packageDepth,
			Pair<Integer, Integer> packageNaming) {
		List<Object> argsList = new ArrayList<Object>(Arrays.asList(
				"(input=(ref=", root, "), target=(dir='",
				output.getAbsolutePath(), "')"));

		if (!StringUtils.isEmpty(encodingFix)) {
			argsList.addAll(Arrays.asList(", 'override-encoding'=(name='",
					encodingFix, "')"));
		}
		if (!StringUtils.isEmpty(packageSeparator)) {
			argsList.addAll(Arrays.asList(", 'packages-by'=(seperator='",
					packageSeparator, "')"));
		}
		if (packageDepth > -1) {
			argsList.addAll(Arrays.asList(", 'package-depth'=('max-depth'=",
					Integer.toString(packageDepth), ")"));
		}
		if (packageNaming != null) {
			argsList.addAll(Arrays.asList(
					", 'package-seperation-by-prefix'=(level=", packageNaming
							.getFirst().toString(), ", maxLength=",
					packageNaming.getSecond().toString(), ")"));
		}

		argsList.add(")");

		return argsList.toArray();
	}

	/**
	 * Helper method to assert number of extracted ABAP files.
	 */
	private void assertNumberOfAbapFiles(File directory, int expectedAbapFiles) {
		int actualABAPfiles = FileSystemUtils.listFilesRecursively(directory,
				new FileExtensionFilter("abap")).size();
		Assert.assertEquals(
				"NuggetSplitter did not create the expected number of files",
				expectedAbapFiles, actualABAPfiles);

	}

	/**
	 * Asserts that the given expected file exists.
	 */
	private void assertFileExists(File baseDir, String expectedFile)
			throws IOException {
		Assert.assertTrue(expectedFile
				+ " expected in output, but does not exist.",
				new CanonicalFile(baseDir, expectedFile).exists());
	}

}