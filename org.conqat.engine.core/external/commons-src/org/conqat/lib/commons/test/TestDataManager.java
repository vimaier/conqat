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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import junit.framework.TestCase;

import org.conqat.lib.commons.collections.TwoDimHashMap;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Support class for identifying unused test data files. This class provides a
 * method to access test data files and logs which test cases access which test
 * files. On every access to a file, access statistic for used and unused files
 * are written to {@value #REPORT_DIRECTORY_NAME}.
 * <p>
 * This class is best used via inheriting from {@link CCSMTestCaseBase}.
 * 
 * @author Florian Deissenboeck
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5234A93C36869FFF4A3710F248F4C9DF
 */
public class TestDataManager {

	/** Name of the directory to write reports to. */
	public static final String REPORT_DIRECTORY_NAME = "test-tmp";

	/** Map of all instances (which is indexed by managed directory). */
	private static Map<File, TestDataManager> instances = new HashMap<File, TestDataManager>();

	/** Returns the instance of the test data manager for the given directory. */
	public static TestDataManager getInstance(File directory) {
		if (!instances.containsKey(directory)) {
			instances.put(directory, new TestDataManager(directory));
		}
		return instances.get(directory);
	}

	/** The set of unused files. */
	private final HashSet<String> unusedFiles = new HashSet<String>();

	/** Storage for all test files used so far. */
	private final TwoDimHashMap<Class<?>, TestCase, HashSet<String>> usedFiles = new TwoDimHashMap<Class<?>, TestCase, HashSet<String>>();

	/** The directory this manager works in. */
	private final File directory;

	/** Private constructor. */
	private TestDataManager(File directory) {
		this.directory = directory;

		if (!directory.exists() || !directory.isDirectory()) {
			return;
		}

		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				unusedFiles.add(file.getName());
			}
		}
	}

	/**
	 * Marks the given file as used and returns the complete file (with
	 * directory).
	 */
	public File getTestFile(String filename, TestCase testCase) {

		HashSet<String> set = usedFiles.getValue(testCase.getClass(), testCase);
		if (set == null) {
			set = new HashSet<String>();
			usedFiles.putValue(testCase.getClass(), testCase, set);
		}

		set.add(filename);
		unusedFiles.remove(filename);
		updateUsageReports();

		return new File(directory, filename);
	}

	/**
	 * Print a summary on used and unused test data files into a directory
	 * specific log file.
	 */
	private void updateUsageReports() {
		try {
			File baseDir = new File(REPORT_DIRECTORY_NAME);
			FileSystemUtils.ensureDirectoryExists(baseDir);
			String fname = directory.toString().replaceAll("[\\\\/]", "_");

			PrintWriter pw = new PrintWriter(new FileWriter(new File(baseDir,
					fname + "_usage.txt")));
			printUsedFiles(pw);
			pw.close();

			pw = new PrintWriter(new FileWriter(new File(baseDir, fname
					+ "_unusage.txt")));
			printUnusedFiles(pw);
			pw.close();
		} catch (IOException e) {
			// This is the best we can do (as we are in testing)
			e.printStackTrace();
		}
	}

	/** Print a report on all files not used. */
	public void printUnusedFiles(PrintWriter pw) {
		pw.println("Unused files for directory " + directory + ": "
				+ unusedFiles.size());
		ArrayList<String> fileList = new ArrayList<String>(unusedFiles);
		Collections.sort(fileList);
		for (String filename : fileList) {
			pw.print("  ");
			pw.println(filename);
		}

		pw.flush();
	}

	/** Print a report on all files used. */
	public void printUsedFiles(PrintWriter pw) {
		pw.println("Used files for directory " + directory);

		for (Class<?> clazz : usedFiles.getFirstKeys()) {
			pw.print("  ");
			pw.println(clazz.getName());
			for (TestCase testCase : usedFiles.getSecondKeys(clazz)) {
				pw.print("    ");
				pw.println(testCase.getName());
				for (String filename : usedFiles.getValue(clazz, testCase)) {
					pw.print("      ");
					pw.println(filename);
				}
			}
			pw.println();
		}

		pw.flush();
	}

}