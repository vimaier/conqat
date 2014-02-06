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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.build.JarPruner;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.digest.Digester;
import org.conqat.lib.commons.string.StringUtils;

/**
 * ConQAT utility runner mainly used for automated builds. This runner checks
 * all libraries if they contain the same classes and exits with 1 in case
 * problems are found, 0 if not. In case of I/O problems (unlikely), the exit
 * code is 2.
 * 
 * A utility to resolve library issues in cases where "too many" classes are
 * contained in the JARs is the {@link JarPruner}.
 * 
 * @author $Author: goede $
 * @version $Rev: 46361 $
 * @ConQAT.Rating GREEN Hash: 9D05EAAC45EA71AF8A2BDB6D28889085
 */
public class CheckLibraryClashesRunner extends LibraryClassAnalyzingRunnerBase {

	/**
	 * Maps class paths (i.e., the file name corresponding to a class) to class
	 * info.
	 */
	private final ListMap<String, ClassInfo> classesByPath = new ListMap<String, ClassInfo>();

	/** {@inheritDoc} */
	@Override
	protected int reportResults() {
		if (reportClashes()) {
			return 1;
		}

		System.out.println("SUCCESS: Done.");
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	protected void processClass(String classFileName, String libraryName,
			byte[] byteCodeContent) {
		classesByPath.add(classFileName, new ClassInfo(libraryName,
				byteCodeContent));
	}

	/**
	 * Reports any class clashes, i.e. classes that are provided by more than
	 * one library. Returns whether any clashes were found.
	 */
	private boolean reportClashes() {
		int clashCount = 0;
		int inconsistentClashCount = 0;

		Set<String> clashLibraries = new HashSet<String>();
		for (String classPath : classesByPath.getKeys()) {
			List<ClassInfo> infos = classesByPath.getCollection(classPath);
			if (infos.size() <= 1) {
				continue;
			}

			Set<String> hashCodes = new HashSet<String>();
			for (ClassInfo info : infos) {
				hashCodes.add(info.contentHash);
				clashLibraries.add(info.libraryName);
			}

			String suffix = StringUtils.EMPTY_STRING;
			if (hashCodes.size() > 1) {
				suffix = "; code content differs!";
				inconsistentClashCount += 1;
			}

			clashCount += 1;
			System.err.println("Class " + classPath
					+ " was found in multiple libraries: "
					+ StringUtils.concat(infos) + suffix);
		}

		if (clashCount > 0) {
			System.err.println("Found " + clashCount + " class clashes. "
					+ inconsistentClashCount + " of them were inconsistent!");
			System.err.println(clashLibraries.size() + " libraries affected: ");
			for (String library : CollectionUtils.sort(clashLibraries)) {
				System.err.println("  " + library);
			}
		}

		return clashCount > 0;
	}

	/** Stores information on a single class. */
	private static class ClassInfo {

		/** The name of the library. */
		private final String libraryName;

		/** Content hash of the class's byte code. */
		private final String contentHash;

		/** Constructor. */
		public ClassInfo(String libName, byte[] content) {
			this.libraryName = libName;
			this.contentHash = Digester.createMD5Digest(content);
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return libraryName;
		}
	}
}
