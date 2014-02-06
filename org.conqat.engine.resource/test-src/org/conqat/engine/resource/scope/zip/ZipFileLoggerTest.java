/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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

import java.util.HashSet;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Tests {@link ZipFileLogger}. These are not a lot more than a test for
 * {@link HashSet} though.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41869 $
 * @ConQAT.Rating GREEN Hash: 6D43001A061DDF47006802CE3215BF37
 */
public class ZipFileLoggerTest extends CCSMTestCaseBase {
	/** The content accessors. */
	IContentAccessor a;
	/** The content accessors. */
	IContentAccessor b;
	/** The content accessors. */
	IContentAccessor c;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		c = new ZipEntryContentAccessor(useCanonicalTestFile("files.zip"),
				"a.txt", StringUtils.EMPTY_STRING, "TEST", true, null);
	}

	/** Tests principal logging functionality. */
	public void testLogging() throws ConQATException {
		ZipFileLogger logger = new ZipFileLogger();
		HashSet<String> expectedSet = new HashSet<String>();
		createContentAccesors(logger);
		assertEquals(true, logger.getFiles().isEmpty());

		expectedSet = new HashSet<String>();
		expectedSet.add(a.getLocation());
		a.getContent();
		assertEquals(expectedSet, logger.getFiles());
	}

	/** Tests whether one file accessed many times leads to only one log entry */
	public void testMultipleLogging() throws ConQATException {
		ZipFileLogger logger = new ZipFileLogger();
		HashSet<String> expectedSet = new HashSet<String>();
		createContentAccesors(logger);

		a.getContent();
		a.getContent();
		a.getContent();
		// even though we access a thrice, the set should only contain one
		// entry.
		expectedSet.add(a.getLocation());
		assertEquals(expectedSet, logger.getFiles());
	}

	/**
	 * Tests whether the logging object is psased on to zip files created from
	 * one base {@link ZipEntryContentAccessor}
	 */
	public void testRelativeLogging() throws ConQATException {
		ZipFileLogger logger = new ZipFileLogger();
		HashSet<String> expectedSet = new HashSet<String>();
		createContentAccesors(logger);

		a.getContent();
		b.getContent();
		expectedSet.add(a.getLocation());
		expectedSet.add(b.getLocation());
		assertEquals(expectedSet, logger.getFiles());
	}

	/**
	 * Tests that constructor without logging object exists and logs nothing
	 * (and doesn't segfault).
	 */
	public void testNoLogging() throws ConQATException {
		ZipFileLogger logger = new ZipFileLogger();
		HashSet<String> expectedSet = new HashSet<String>();
		createContentAccesors(logger);

		c.getContent();
		assertEquals(expectedSet, logger.getFiles());
	}

	/**
	 * Helper method creating the {@link ZipEntryContentAccessor}s for the tests
	 * with the passed logger.
	 */
	private void createContentAccesors(ZipFileLogger logger)
			throws ConQATException {
		a = new ZipEntryContentAccessor(useCanonicalTestFile("files.zip"),
				"a.txt", StringUtils.EMPTY_STRING, "TEST", true, logger);
		b = a.createRelative("b/b.txt");

	}
}
