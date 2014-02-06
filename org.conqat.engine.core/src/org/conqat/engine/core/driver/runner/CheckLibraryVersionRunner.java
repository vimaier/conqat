/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2012 the ConQAT Project                                   |
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
package org.conqat.engine.core.driver.runner;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;

/**
 * ConQAT utility runner mainly used for automated builds. This runner checks
 * all libraries if they contain class files that require a Java version newer
 * than 1.6, which is the current minimal requirement for ConQAT. This exits
 * with 1 in case problems are found, 0 if not. In case of I/O problems
 * (unlikely), the exit code is 2.
 * 
 * @author $Author: goede $
 * @version $Rev: 46361 $
 * @ConQAT.Rating GREEN Hash: E37DB5B22ACC4850230FFC693CABC0BF
 */
public class CheckLibraryVersionRunner extends LibraryClassAnalyzingRunnerBase {

	/**
	 * The major version for 1.6 in the class file. Also see
	 * http://en.wikipedia.org/wiki/Java_class_file for a list of these version
	 * numbers.
	 */
	private static final int JAVA16_CLASS_VERSION = 50;
	/**
	 * Lists libraries (keys) and their classes (values) that have a too new
	 * class file version.
	 */
	private final ListMap<String, String> invalidLibrariesAndClasses = new ListMap<String, String>();

	/** {@inheritDoc} */
	@Override
	protected int reportResults() {
		int invalidLibraryCount = invalidLibrariesAndClasses.getKeys().size();
		if (invalidLibraryCount == 0) {
			System.out.println("No violations found.");
			return 0;
		}

		System.err
				.println("Had "
						+ invalidLibraryCount
						+ " libraries with classes that require a Java higher than 1.6!");
		for (String libraryName : CollectionUtils
				.sort(invalidLibrariesAndClasses.getKeys())) {
			System.err.println(libraryName + ":");
			for (String classFile : CollectionUtils
					.sort(invalidLibrariesAndClasses.getCollection(libraryName))) {
				System.err.println("    " + classFile);
			}
		}
		return 1;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * See http://en.wikipedia.org/wiki/Java_class_file for a description of the
	 * byte code format.
	 */
	@Override
	protected void processClass(String classFileName, String libraryName,
			byte[] byteCodeContent) {
		if (byteCodeContent.length < 8) {
			System.err.println("Found broken class file (too short) in "
					+ libraryName + ": " + classFileName);
			return;
		}

		int majorVersion = (byteCodeContent[6] << 8) + byteCodeContent[7];
		if (majorVersion > JAVA16_CLASS_VERSION) {
			invalidLibrariesAndClasses.add(libraryName, classFileName);
		}
	}
}
