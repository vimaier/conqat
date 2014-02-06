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
package org.conqat.lib.commons.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.TestCase;

import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Base class for test cases that access test data files. This class provides a
 * simple mechanism for accessing test data files in a specified directory and
 * provides statistics on test file usage and non-usage.
 * <p>
 * The test files a test case accesses must reside in the following location:
 * 
 * <pre>
 *        test-data/&lt;Name of the package the test case resides in&gt;
 * </pre>
 * 
 * For example if a test case is defined in package <code>demo.test</code> the
 * test files it accesses must be located in directory
 * <code>test-data/demo.test</code>.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8B0FE0996D65D7655B3253ABD9D3DD51
 */
public abstract class CCSMTestCaseBase extends TestCase {

	/** Test data directory. */
	private final static File TEST_DATA_ROOT_DIRECTORY = new File("test-data");

	/** Tmp directory. */
	private final static File TEST_TMP_ROOT_DIRECTORY = new File("test-tmp");

	/** Test data manager for this test case. */
	private final TestDataManager testDataManager = TestDataManager
			.getInstance(new File(TEST_DATA_ROOT_DIRECTORY, getClass()
					.getPackage().getName()));

	/** Tmp directory. */
	private final File tmpDirectory = new File(TEST_TMP_ROOT_DIRECTORY,
			getClass().getPackage().getName());

	/** Default constructor */
	public CCSMTestCaseBase() {
		super();
	}

	/**
	 * Constructs a test case with the given name.
	 * 
	 * @param name
	 *            Name of the test method that gets called
	 */
	public CCSMTestCaseBase(String name) {
		super(name);
	}

	/**
	 * Use test file. This method does not actually access the file, so no IO
	 * exception can be raised. This method uses a {@link TestDataManager} to
	 * log access to test data files.
	 * 
	 * @param filename
	 *            Name of the file
	 * @return the file.
	 */
	protected File useTestFile(String filename) {
		return testDataManager.getTestFile(filename, this);
	}

	/**
	 * Create a temporary file in a subdirectory of the test temp directory.
	 * Directories are created as needed.
	 * 
	 * @param filename
	 *            name of the file
	 * @param content
	 *            content
	 * @return the file
	 * @throws IOException
	 *             if an IO exception occurrs
	 */
	protected File createTmpFile(String filename, String content)
			throws IOException {
		File file = new File(tmpDirectory, filename);
		FileSystemUtils.writeFile(file, content);
		return file;
	}

	/** Get temporary directory. */
	protected File getTmpDirectory() {
		return tmpDirectory;
	}

	/** Delete temporary directory. */
	protected void deleteTmpDirectory() {
		if (tmpDirectory.isDirectory()) {
			FileSystemUtils.deleteRecursively(tmpDirectory);
		}
	}

	/**
	 * Print report about used test files.
	 */
	protected void printUsedFiles() {
		testDataManager.printUsedFiles(new PrintWriter(System.out));
	}

	/**
	 * Print report about unused test files.
	 */
	protected void printUnusedFiles() {
		testDataManager.printUnusedFiles(new PrintWriter(System.out));
	}

	/**
	 * Print report about used and unused test files.
	 */
	protected void printStatistics() {
		printUsedFiles();
		printUnusedFiles();
	}

	/**
	 * Same as {@link #useTestFile(String)} but returns a {@link CanonicalFile}.
	 * If canonization fails, this makes the current test fail.
	 */
	protected CanonicalFile useCanonicalTestFile(String filename) {
		try {
			return new CanonicalFile(useTestFile(filename));
		} catch (IOException e) {
			fail("Problem canonizing file: " + filename + ": " + e.getMessage());
			return null;
		}
	}

	/**
	 * Same as {@link #createTmpFile(String, String)} but returns a
	 * {@link CanonicalFile}. If canonization fails, this makes the current test
	 * fail.
	 */
	protected CanonicalFile createCanonicalTmpFile(String filename,
			String content) throws IOException {
		return canonize(createTmpFile(filename, content));
	}

	/**
	 * Canonize file. If canonization fails, this makes the current test fail.
	 */
	protected CanonicalFile canonize(File file) {
		try {
			return new CanonicalFile(file);
		} catch (IOException e) {
			fail("Problem canonizing file: " + file + ": " + e.getMessage());
			return null;
		}
	}

	/** Checks if we run on a 64 bit VM */
	public static boolean is64BitVM() {
		return System.getProperty("os.arch").contains("64");
	}
}