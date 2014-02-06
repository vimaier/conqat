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

import java.io.IOException;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Tests the {@link FileContentAccessor}.
 * 
 * @author $Author: juergens $
 * @version $Rev: 36771 $
 * @ConQAT.Rating GREEN Hash: 1F3EAB4004D655A0F8CD5E9C6DEA4895
 */
public class FileContentAccessorTest extends CCSMTestCaseBase {

	/** Tests the creation of relative paths. */
	public void testCreateRelative() throws ConQATException, IOException {
		CanonicalFile testFile = useCanonicalTestFile("a.txt");
		FileContentAccessor a = new FileContentAccessor(testFile,
				testFile.getParentFile(), "TEST");
		assertEquals("TEST/a.txt", a.getUniformPath());

		IContentAccessor b = a.createRelative("b/b.txt");
		assertEquals("TEST/b/b.txt", b.getUniformPath());
		assertTrue(new CanonicalFile(b.getLocation()).isReadableFile());

		assertEquals(a.createRelativeUniformPath("b/b.txt"), b.getUniformPath());

		// this should also work with the backslash (even on linux)
		b = a.createRelative("b\\b.txt");
		assertEquals("TEST/b/b.txt", b.getUniformPath());
		assertTrue(new CanonicalFile(b.getLocation()).isReadableFile());

		assertEquals(a.createRelativeUniformPath("b\\b.txt"),
				b.getUniformPath());

		// file must exist in order to be accessible
		try {
			a.createRelative("does/not/exist");
			fail("expected exception!");
		} catch (ConQATException e) {
			// expected
		}

		// file needs not exist in order to create uniform path
		assertEquals("TEST/does/not/exist",
				a.createRelativeUniformPath("does/not/exist"));

		// we can even resolve .. to some extent
		IContentAccessor b2 = b.createRelative("../long_name/b.txt");
		assertEquals("TEST/long_name/b.txt", b2.getUniformPath());

		assertEquals(b.createRelativeUniformPath("../long_name/b.txt"),
				b2.getUniformPath());
	}

	/** Test case for CR#3935 */
	public void testCreateRelativeOnRemoveWindowsShare()
			throws ConQATException, IOException {
		String filename = "\\\\server\\root\\file.java";
		CanonicalFile testFile = new CanonicalFile(filename);

		// make valid uniform path
		String uniformPath = UniformPathUtils.normalizeAllSeparators(filename);
		FileContentAccessor a = new FileContentAccessor(testFile, uniformPath);
		assertEquals(uniformPath, a.getUniformPath());

		String uniformPathOther = a.determineNewUniformPath(new CanonicalFile(
				"\\\\server\\root\\otherfile.java"));
		assertEquals("/server/root/otherfile.java", uniformPathOther);
	}

	/** Test case for CR#4006. */
	public void testRelativeCR4006() throws ConQATException {
		CanonicalFile testFile = useCanonicalTestFile("a.txt");
		FileContentAccessor a = new FileContentAccessor(testFile,
				testFile.getParentFile(), "TEST");

		// this call caused an exception in the old code
		IContentAccessor relative = a.createRelative("a.txt.suffix");

		assertEquals("TEST/a.txt.suffix", relative.getUniformPath());
	}

}