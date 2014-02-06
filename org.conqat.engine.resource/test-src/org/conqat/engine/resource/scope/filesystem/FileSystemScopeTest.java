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
package org.conqat.engine.resource.scope.filesystem;

import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.scope.ScopeProcessorTestCaseBase;

import org.conqat.lib.commons.string.StringUtils;

/**
 * Tests the {@link FileSystemScope}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36715 $
 * @ConQAT.Rating GREEN Hash: 1C6F6C029D85D7C69FBDD3A8223784C6
 */
public class FileSystemScopeTest extends ScopeProcessorTestCaseBase {

	/** Tests scanning of all files. */
	public void testScanAll() throws Exception {
		String[] expected = new String[] { "'TEST/a.txt'=a",
				"'TEST/a.txt.suffix'=a", "'TEST/b/b.txt'='b*2'",
				"'TEST/b/c/a.txt'=a", "'TEST/b/c/b.txt'='b*2'",
				"'TEST/long_name/a.txt'=a", "'TEST/long_name/b.txt'='b*2'" };
		assertScopesAreEqual(expected, executeFilesystemScope(null));
	}

	/** Tests scanning with include pattern. */
	public void testScanInclude() throws Exception {
		String[] expected = new String[] { "'TEST/a.txt'=a",
				"'TEST/b/c/a.txt'=a", "'TEST/long_name/a.txt'=a" };
		assertScopesAreEqual(expected,
				executeFilesystemScope("include=(pattern='**/a.txt')"));
	}

	/** Tests scanning with exclude pattern. */
	public void testScanExclude() throws Exception {
		String[] expected = new String[] { "'TEST/b/b.txt'='b*2'",
				"'TEST/b/c/b.txt'='b*2'", "'TEST/long_name/b.txt'='b*2'" };
		assertScopesAreEqual(
				expected,
				executeFilesystemScope("exclude=(pattern='**/a.txt'), exclude=(pattern='**/*.suffix')"));
	}

	/** Tests whether the correct location is used. */
	public void testFileLocation() throws Exception {
		IContentAccessor[] result = executeFilesystemScope("include=(pattern='a.txt')");
		assertEquals(1, result.length);
		assertEquals(useCanonicalTestFile("a.txt").getCanonicalPath(),
				result[0].getLocation());
	}

	/** Executes the filesystem scope. */
	private IContentAccessor[] executeFilesystemScope(
			String additionalParameters) throws Exception {
		if (StringUtils.isEmpty(additionalParameters)) {
			additionalParameters = StringUtils.EMPTY_STRING;
		} else {
			additionalParameters = ", " + additionalParameters;
		}

		return (IContentAccessor[]) executeProcessor(FileSystemScope.class,
				"(project=(name=TEST), root=(dir='", useCanonicalTestFile(".")
						.getCanonicalPath(),
				"'), exclude=(pattern='**/.svn/**')" + additionalParameters
						+ ")");
	}
}