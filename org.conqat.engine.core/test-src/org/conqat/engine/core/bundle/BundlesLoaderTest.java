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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.core.driver.error.EDriverExceptionType;

/**
 * Test class for {@link BundlesLoader}. This class checks if the libraries are
 * resolved correctly and does some simple checks to ensure that the descriptor
 * was read. Full testing for descriptor reading is implemented by
 * {@link BundleDescriptorReaderTest}.
 * 
 * @author Florian Deissenboeck
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 39D3E634FB981DF0010B6677B4BF9985
 */
public class BundlesLoaderTest extends BundleTestBase {

	/** Test if an exception is thrown if the location was not found. */
	public void testBundleLocationNotFound() {
		checkException(EDriverExceptionType.BUNDLE_LOCATION_NOT_FOUND, "x");
	}

	/** Test if an exception is thrown for empty library directories. */
	public void testEmptyLibraryDirectory() {
		checkException(EDriverExceptionType.EMPTY_LIBRARY_DIRECTORY,
				"bundleLoader03");
	}

	/** Test if libraries are resolved correctly. */
	public void testLibraries() throws BundleException {
		BundleInfo info = loadInfo("bundleLoader04");
		assertDescriptorLoaded(info);
		assertEquals(2, info.getLibraries().size());

		ArrayList<File> libraryList = new ArrayList<File>(info.getLibraries());
		Collections.sort(libraryList);
		assertEquals("testLib01.jar", libraryList.get(0).getName());
		assertEquals("testLib02.jar", libraryList.get(1).getName());
	}

	/** Test if an exception is thrown if the descriptor is missing. */
	public void testMissingDescriptor() {
		checkException(EDriverExceptionType.MISSING_BUNDLE_DESCRIPTOR,
				"bundleLoader01");
	}

	/** Test if multiple bundles are loaded correctly. */
	public void testMultipleBundles() throws BundleException {
		BundlesConfiguration config = loadConfiguration("bundleLoader02",
				"bundleLoader04", "bundleLoader05");

		assertEquals(3, config.getBundles().size());
		assertDescriptorLoaded(config.getBundle("bundleLoader02"));
		assertDescriptorLoaded(config.getBundle("bundleLoader04"));
		assertDescriptorLoaded(config.getBundle("bundleLoader05"));

	}

	/** Test if library resolution works for bundles without libraries. */
	public void testNoLibraries() throws BundleException {
		BundleInfo info = loadInfo("bundleLoader02");
		assertDescriptorLoaded(info);
		assertTrue(info.getLibraries().isEmpty());
	}

	/** Test that only jar files are read from the library directory. */
	public void testOnlyJarsAreRead() throws BundleException {
		BundleInfo info = loadInfo("bundleLoader05");
		assertDescriptorLoaded(info);
		assertEquals(2, info.getLibraries().size());

		ArrayList<File> libraryList = new ArrayList<File>(info.getLibraries());
		Collections.sort(libraryList);
		assertEquals("testLib01.jar", libraryList.get(0).getName());
		assertEquals("testLib02.jar", libraryList.get(1).getName());
	}

	/**
	 * Assert that the descriptor was loaded. Full testing for descriptor
	 * reading is implemented by {@link BundleDescriptorReaderTest}.
	 */
	private void assertDescriptorLoaded(BundleInfo info) {
		assertFalse(StringUtils.isEmpty(info.getName()));
		assertFalse(StringUtils.isEmpty(info.getProvider()));
		assertFalse(StringUtils.isEmpty(info.getDescription()));
	}

	/**
	 * Enables the {@link BundlesLoader}. All other buildlets are deactivated,
	 * since they are not required for the tests.
	 */
	@Override
	protected Set<Class<?>> getEnabledBuildlets() {
		return createSet(BundlesLoader.class);
	}

}