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
package org.conqat.engine.java.library;

import java.io.FileReader;

import junit.framework.TestCase;

/**
 * This class tests the <code>PackageDeclarationExtractor</code>-class.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 94055FEFDE9E3C2A5D27BF4FD07EF953
 */
public class PackageDeclarationExtractorTest extends TestCase {

	/** The extractor under test. */
	private final PackageDeclarationExtractor extractor = new PackageDeclarationExtractor();

	/** Test qualified name. */
	public void testValid01() {
		checkValid("edu.tum.cs.conqat.aggregation",
				"test-data/edu.tum.cs.conqat.libraries.java/TestValid01.java");
	}

	/** Test simple name. */
	public void testValid02() {
		checkValid("edu",
				"test-data/edu.tum.cs.conqat.libraries.java/TestValid02.java");
	}

	/** Test enum keyword in package name (was valid in pre Java 5). */
	public void testValidJava4() {
		checkValid("edu.enum.test",
				"test-data/edu.tum.cs.conqat.libraries.java/TestValidJava4.java");
	}

	/** Test illegal token in package name. */
	public void testInvalid01() {
		checkInvalid("test-data/edu.tum.cs.conqat.libraries.java/TestInvalid01.java");
	}

	/** Test EOF in package name. */
	public void testInvalid02() {
		checkInvalid("test-data/edu.tum.cs.conqat.libraries.java/TestInvalid02.java");
	}

	/**
	 * Test if extractor correctly returns null if no package declaration is
	 * present.
	 */
	public void testNoPackageStatement() {
		checkValid(null,
				"test-data/edu.tum.cs.conqat.libraries.java/TestNoPackageStatement.java");
	}

	/** Test if subsequent package declarations are ignored. */
	public void testMultiplePackageStatements() {
		checkValid("edu.tum.cs.conqat.aggregation",
				"test-data/edu.tum.cs.conqat.libraries.java/TestMultiplePackageStatements.java");
	}

	/**
	 * Compare expected package name to the extracted package name.
	 * 
	 * @param expectedPackageName
	 * @param path
	 */
	private void checkValid(String expectedPackageName, String path) {
		try {
			String packageName = extractor.getPackageNameFromReader(path,
					new FileReader(path));
			assertEquals(expectedPackageName, packageName);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/** Make sure an exception is raised. */
	private void checkInvalid(String path) {

		try {
			extractor.getPackageNameFromReader(path, new FileReader(path));
		} catch (Exception ex) {
			// desired result
			return;
		}
		fail("Accepted invalid package statement");
	}

}