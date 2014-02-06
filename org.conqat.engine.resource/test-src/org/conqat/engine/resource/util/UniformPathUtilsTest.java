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
package org.conqat.engine.resource.util;

import java.util.Arrays;

import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Tests for the {@link UniformPathUtils}.
 * 
 * @author hummelb
 * @author $Author: poehlman $
 * @version $Rev: 40906 $
 * @ConQAT.Rating YELLOW Hash: 66053E9D5BBE55751553B0B9001924A9
 */
public class UniformPathUtilsTest extends CCSMTestCaseBase {

	/** Tests project extraction. */
	public void testExtractProject() {
		assertEquals("foo", UniformPathUtils.extractProject("foo"));
		assertEquals("foo", UniformPathUtils.extractProject("foo/bar"));
		assertEquals("foo", UniformPathUtils.extractProject("foo/bar/baz"));
	}

	/** Tests extraction of simple name. */
	public void testGetElementName() {
		assertEquals("", UniformPathUtils.getElementName("/"));
		assertEquals("", UniformPathUtils.getElementName("foo/"));
		assertEquals("foo", UniformPathUtils.getElementName("foo"));
		assertEquals("foo", UniformPathUtils.getElementName("bar/foo"));
		assertEquals("foo", UniformPathUtils.getElementName("baz/bar/foo"));
	}

	/** Tests extraction of parent path. */
	public void testGetParentPath() {
		assertEquals("", UniformPathUtils.getParentPath("/"));
		assertEquals("", UniformPathUtils.getParentPath("/foo"));
		assertEquals("", UniformPathUtils.getParentPath("foo"));
		assertEquals("bar", UniformPathUtils.getParentPath("bar/foo"));
		assertEquals("baz/bar", UniformPathUtils.getParentPath("baz/bar/foo"));
	}

	/** Tests path splitting. */
	public void testSplitPath() {
		assertTrue(Arrays.equals(UniformPathUtils.splitPath(""),
				new String[] { "" }));
		assertTrue(Arrays.equals(UniformPathUtils.splitPath("foo"),
				new String[] { "foo" }));
		assertTrue(Arrays.equals(UniformPathUtils.splitPath("foo/bar/baz"),
				new String[] { "foo", "bar", "baz" }));
		assertTrue(Arrays.equals(UniformPathUtils.splitPath("/foo/bar/baz"),
				new String[] { "", "foo", "bar", "baz" }));
	}

	/** Tests project stripping. */
	public void testStripProject() {
		assertEquals("foo/bar", UniformPathUtils.stripProject("TEST/foo/bar"));
		assertEquals("foo/bar", UniformPathUtils.stripProject("/foo/bar"));
		assertEquals("", UniformPathUtils.stripProject("TEST/"));
		assertEquals("noseparator",
				UniformPathUtils.stripProject("noseparator"));
	}

	/** Tests extension extraction. */
	public void testGetExtension() {
		assertEquals("java", UniformPathUtils.getExtension("test.java"));
		assertEquals("java", UniformPathUtils.getExtension("foo/bar/test.java"));
		assertEquals("", UniformPathUtils.getExtension("foo/bar/test."));
		assertEquals(null, UniformPathUtils.getExtension("foo/bar/test"));

		assertEquals("java",
				UniformPathUtils.getExtension("foo/bar.baz/test.java"));
		assertEquals(null, UniformPathUtils.getExtension("foo/bar.baz/test"));
	}

	/** Tests path cleaning. */
	public void testCleanPath() {
		assertEquals("test", UniformPathUtils.cleanPath("test"));
		assertEquals("a/b/c", UniformPathUtils.cleanPath("a/b/c"));
		assertEquals("a / b /c", UniformPathUtils.cleanPath("a / b /c"));
		assertEquals("a/b/c", UniformPathUtils.cleanPath("/a/b/c"));
		assertEquals("a/b/c", UniformPathUtils.cleanPath("///a////b///c"));
		assertEquals("a/b/c", UniformPathUtils.cleanPath("/a/./b/././c"));
		assertEquals("a/b/c", UniformPathUtils.cleanPath("/a/./b/././c"));

		assertEquals("a/b/c", UniformPathUtils.cleanPath("a/x/../b/c"));
		assertEquals("a/b/c",
				UniformPathUtils.cleanPath("a/x/y/z/../../../b/c"));
		assertEquals("a/b/c",
				UniformPathUtils.cleanPath("a/x/../y/z/../../b/c"));
		assertEquals("a/b/c",
				UniformPathUtils
						.cleanPath("a/x/././y/z/./../././../././.././b/./c"));

		assertEquals("../b/c", UniformPathUtils.cleanPath("a/../../b/c"));
		assertEquals("../../../../c",
				UniformPathUtils.cleanPath("a/../../../../b/../../c"));
	}

	/** Test resolving of relative paths. */
	public void testResolveRelativePath() {
		assertEquals("foo/bar/test.txt", UniformPathUtils.resolveRelativePath(
				"foo/bar/baz.txt", "test.txt"));
		assertEquals("foo/bar/a/b/c/test.txt",
				UniformPathUtils.resolveRelativePath("foo/bar/baz.txt",
						"a/b/c/test.txt"));
		assertEquals("foo/test.txt", UniformPathUtils.resolveRelativePath(
				"foo/bar/baz.txt", "../test.txt"));
		assertEquals("test.txt", UniformPathUtils.resolveRelativePath(
				"foo/bar/baz.txt", "../../test.txt"));
		assertEquals("../test.txt", UniformPathUtils.resolveRelativePath(
				"foo/bar/baz.txt", "../../../test.txt"));
	}

	/** Tests concatenation of paths. */
	public void testConcatenate() {
		assertEquals(StringUtils.EMPTY_STRING,
				UniformPathUtils.concatenate(null, ""));
		assertEquals("foo/bar", UniformPathUtils.concatenate("foo/bar"));
		assertEquals("foo/bar", UniformPathUtils.concatenate(null, "foo/bar"));
		assertEquals("foo/bar", UniformPathUtils.concatenate("foo", "bar"));
		assertEquals("foo/bar",
				UniformPathUtils.concatenate("foo", null, "bar", ""));
	}
}