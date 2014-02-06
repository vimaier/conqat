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
package org.conqat.engine.core.bundle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Test for {@link BundleResourceManager}.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: AC358AEBC61BB506CCF7F6CA7F9CA99B
 */
public class BundleResourceManagerTest extends CCSMTestCaseBase {

	/** Test path 1. */
	private final String path1 = "test01.txt";

	/** Test path 2. */
	private final String path2 = "directory01/test02.txt";

	/** Manager under test. */
	private BundleResourceManager resourceManager;

	/** Create manager. */
	@Override
	public void setUp() throws BundleException {
		File location = useTestFile("resourceManager01");
		BundleInfo bundleInfo = new BundleInfo(location);
		resourceManager = new BundleResourceManager(bundleInfo);
	}

	/** Test for {@link BundleResourceManager#getAbsoluteResourcePath(String)}. */
	public void testGetAbsoluteResourcePath() {
		assertTrue(new File(resourceManager.getAbsoluteResourcePath(path1))
				.exists());
		assertTrue(new File(resourceManager.getAbsoluteResourcePath(path2))
				.exists());
	}

	/** Test for {@link BundleResourceManager#getResourceAsFile(String)}. */
	public void testGetResourceAsFile() {
		assertTrue(resourceManager.getResourceAsFile(path1).exists());
		assertTrue(resourceManager.getResourceAsFile(path2).exists());
	}

	/** Test for {@link BundleResourceManager#getResourceAsStream(String)}. */
	public void testGetResourceAsStream() throws IOException {
		assertContentEquals("This is a test.", resourceManager
				.getResourceAsStream(path1));
		assertContentEquals("This is a test.", resourceManager
				.getResourceAsStream(path2));

	}

	/** Test for {@link BundleResourceManager#getResourceAsURL(String)}. */
	public void testGetResourceAsURL() throws MalformedURLException,
			URISyntaxException {
		assertTrue(new File(resourceManager.getResourceAsURL(path1).toURI())
				.exists());
		assertTrue(new File(resourceManager.getResourceAsURL(path2).toURI())
				.exists());
	}

	/** Read from a stream and assert that content equals a string. */
	private void assertContentEquals(String expectedContent, InputStream stream)
			throws IOException {
		StringBuilder content = new StringBuilder();

		int c;
		while ((c = stream.read()) != -1) {
			content.append((char) c);
		}
		stream.close();

		assertEquals(expectedContent, content.toString());
	}

}