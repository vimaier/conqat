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
package org.conqat.engine.resource.scope.zip;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.binary.BinaryElementFactory;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.resource.scope.ScopeProcessorTestCaseBase;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Tests the {@link ZipFileScope}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41018 $
 * @ConQAT.Rating GREEN Hash: 0A03175A997F0B50B1FA053ED95AC9A4
 */
public class ZipFileScopeTest extends ScopeProcessorTestCaseBase {

	/** Name of the default ZIP file used for testing. */
	private static final String ZIP_FILE = "files.zip";

	/** Tests scanning of all files. */
	public void testScanAll() throws ConQATException {
		String[] expected = new String[] { "'TEST/a.txt'=a",
				"'TEST/b/b.txt'='b*2'", "'TEST/b/c/a.txt'=a",
				"'TEST/b/c/b.txt'='b*2'", "'TEST/long_name/a.txt'=a",
				"'TEST/long_name/b.txt'='b*2'" };
		assertScopesAreEqual(expected,
				executeZipFileScopeWithSingleZIP(null, ZIP_FILE));
	}

	/** Tests scanning with include pattern. */
	public void testScanInclude() throws ConQATException {
		String[] expected = new String[] { "'TEST/a.txt'=a",
				"'TEST/b/c/a.txt'=a", "'TEST/long_name/a.txt'=a" };
		assertScopesAreEqual(
				expected,
				executeZipFileScopeWithSingleZIP(
						"include=(pattern='**/a.txt')", ZIP_FILE));
	}

	/** Tests scanning with exclude pattern. */
	public void testScanExclude() throws ConQATException {
		String[] expected = new String[] { "'TEST/b/b.txt'='b*2'",
				"'TEST/b/c/b.txt'='b*2'", "'TEST/long_name/b.txt'='b*2'" };
		assertScopesAreEqual(
				expected,
				executeZipFileScopeWithSingleZIP(
						"exclude=(pattern='**/a.txt')", ZIP_FILE));
	}

	/** Tests scanning with entry prefix and pattern. */
	public void testScanPrefix() throws ConQATException {
		String[] expected = new String[] { "'TEST/b.txt'='b*2'",
				"'TEST/c/b.txt'='b*2'" };
		assertScopesAreEqual(
				expected,
				executeZipFileScopeWithSingleZIP(
						"entry=(prefix=b), exclude=(pattern='*/a.txt')",
						ZIP_FILE));

		// should also work if entry prefix ends in slash
		assertScopesAreEqual(
				expected,
				executeZipFileScopeWithSingleZIP(
						"entry=(prefix=\"b/\"), exclude=(pattern='*/a.txt')",
						ZIP_FILE));
	}

	/** Tests whether the correct location is used. */
	public void testFileLocation() throws ConQATException {
		IContentAccessor[] result = executeZipFileScopeWithSingleZIP(
				"include=(pattern='a.txt')", ZIP_FILE);
		assertEquals(1, result.length);
		assertEquals(useCanonicalTestFile("files.zip").getCanonicalPath()
				+ "!a.txt", result[0].getLocation());
	}

	/** Executes the {@link ZipFileScope} on a single test ZIP. */
	private IContentAccessor[] executeZipFileScopeWithSingleZIP(
			String additionalParameters, String zipName) throws ConQATException {
		if (StringUtils.isEmpty(additionalParameters)) {
			additionalParameters = StringUtils.EMPTY_STRING;
		} else {
			additionalParameters = ", " + additionalParameters;
		}

		IResource scope = createScope(useCanonicalTestFile("."),
				new String[] { zipName }, null, new BinaryElementFactory());
		return (IContentAccessor[]) executeProcessor(ZipFileScope.class,
				"(project=(name=TEST), 'zip-resource'=(ref=", scope, ")"
						+ additionalParameters + ")");
	}

	/** Tests reading a zip contained in another zip. */
	public void testZipInZip() throws ConQATException {
		IContentAccessor[] zipAccessors = executeZipFileScopeWithSingleZIP(
				null, "zipinzip.zip");
		IResource zipResource = (IResource) executeProcessor(
				ResourceBuilder.class, "(scope=(ref=", zipAccessors,
				"), factory=(pattern='**', ref=binFactory()))");

		IContentAccessor[] accessors = (IContentAccessor[]) executeProcessor(
				ZipFileScope.class,
				"(project=(name=TEST), 'zip-resource'=(ref=", zipResource,
				"),  include=(pattern='a.txt'))");
		assertEquals(1, accessors.length);
		assertEquals("TEST/a.txt", accessors[0].getUniformPath());
		assertEquals("a", new String(accessors[0].getContent()));
	}
}