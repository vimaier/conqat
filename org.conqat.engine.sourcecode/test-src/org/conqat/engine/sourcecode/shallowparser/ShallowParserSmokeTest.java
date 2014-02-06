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
package org.conqat.engine.sourcecode.shallowparser;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.lib.scanner.ELanguage;

/**
 * This test reads all files in the test-data directory. The files are organized
 * such that the top-level directory indicates the language.
 * 
 * @author $Author: goede $
 * @version $Revision: 40359 $
 * @ConQAT.Rating GREEN Hash: 8C9F2CB8B64CC89B3CCE368978BE8C6D
 */
public class ShallowParserSmokeTest extends CCSMTestCaseBase {

	/** Extension of the reference files. */
	private static final String REF_EXTENSION = "parsed";

	/** Creates smoke test suite */
	public static Test suite() {
		// Switch to a non-static context, so we can use useTestFile() later on
		return new ShallowParserSmokeTest().createSuite();
	}

	/** Creates smoke test suite */
	private TestSuite createSuite() {
		PairList<File, ELanguage> files = findFiles();
		TestSuite suite = new TestSuite("Shallow Parser Smoke Test ["
				+ files.size() + " test files]");

		for (int i = 0; i < files.size(); ++i) {
			File refFile = files.getFirst(i);
			File codeFile = new File(StringUtils.stripSuffix("."
					+ REF_EXTENSION, refFile.getPath()));

			suite.addTest(new ShallowParserSmokeTestlet(codeFile, refFile,
					files.getSecond(i)));
		}

		return suite;
	}

	/** Returns all files together with their language. */
	private PairList<File, ELanguage> findFiles() {
		PairList<File, ELanguage> result = new PairList<File, ELanguage>();

		for (File file : FileSystemUtils.listFilesRecursively(useTestFile("."),
				new FileExtensionFilter(REF_EXTENSION))) {
			ELanguage language = EnumUtils.valueOfIgnoreCase(ELanguage.class,
					file.getParentFile().getName());
			CCSMAssert.isTrue(language != null, "No language found for file "
					+ file);
			result.add(file, language);
		}

		return result;
	}
}