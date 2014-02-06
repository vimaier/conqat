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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test class for {@link BundlesClassLoaderInitializer}.
 * 
 * @author Florian Deissenboeck
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 3E8582B184E9BAB72B0EC89CC1FA1F4B
 */
public class BundlesClassLoaderInitializerTest extends BundleTestBase {

	/**
	 * Tests if class loader is prepared properly for a bundle with classes and
	 * libraries.
	 */
	public void testClassesAndLibraries() throws BundleException {
		BundlesClassLoader classLoader = initClassLoader("classLoaderInitializer04");

		assertEquals(3, classLoader.getURLs().length);

		HashSet<URL> urls = new HashSet<URL>(Arrays.asList(classLoader
				.getURLs()));

		assertTrue(urls.contains(obtainURL("classLoaderInitializer04",
				"classes")));
		assertTrue(urls.contains(obtainURL("classLoaderInitializer04",
				"lib/testLib01.jar")));
		assertTrue(urls.contains(obtainURL("classLoaderInitializer04",
				"lib/testLib02.jar")));
	}

	/** Tests if class loader is prepared properly for multiple bundles. */
	public void testMultipleBundles() throws BundleException {
		BundlesClassLoader classLoader = initClassLoader(
				"classLoaderInitializer02", "classLoaderInitializer03",
				"classLoaderInitializer04");

		assertEquals(6, classLoader.getURLs().length);

		HashSet<URL> urls = new HashSet<URL>(Arrays.asList(classLoader
				.getURLs()));

		assertTrue(urls.contains(obtainURL("classLoaderInitializer04",
				"classes")));
		assertTrue(urls.contains(obtainURL("classLoaderInitializer04",
				"lib/testLib01.jar")));
		assertTrue(urls.contains(obtainURL("classLoaderInitializer04",
				"lib/testLib02.jar")));
		assertTrue(urls.contains(obtainURL("classLoaderInitializer02",
				"classes")));
		assertTrue(urls.contains(obtainURL("classLoaderInitializer03",
				"lib/testLib01.jar")));
		assertTrue(urls.contains(obtainURL("classLoaderInitializer03",
				"lib/testLib02.jar")));
	}

	/**
	 * Tests if class loader is prepared properly for a bundle with classes but
	 * no libraries.
	 */
	public void testClassesNoLibraries() throws BundleException {
		BundlesClassLoader classLoader = initClassLoader("classLoaderInitializer02");

		assertEquals(1, classLoader.getURLs().length);

		assertEquals(obtainURL("classLoaderInitializer02", "classes"),
				classLoader.getURLs()[0]);
	}

	/**
	 * Tests if class loader is prepared properly for a bundle with no classes
	 * but libraries.
	 */
	public void testNoClassesLibraries() throws BundleException {
		BundlesClassLoader classLoader = initClassLoader("classLoaderInitializer03");

		assertEquals(2, classLoader.getURLs().length);

		HashSet<URL> urls = new HashSet<URL>(Arrays.asList(classLoader
				.getURLs()));

		assertTrue(urls.contains(obtainURL("classLoaderInitializer03",
				"lib/testLib01.jar")));
		assertTrue(urls.contains(obtainURL("classLoaderInitializer03",
				"lib/testLib02.jar")));
	}

	/**
	 * Test is class loader is prepared properly for a bundle without classes
	 * and libraries.
	 */
	public void testNoClassesNoLibraries() throws BundleException {
		BundlesClassLoader classLoader = initClassLoader("classLoaderInitializer01");

		assertEquals(0, classLoader.getURLs().length);
	}

	/**
	 * Init class loader.
	 * 
	 * @param directoryNames
	 *            bundle locations
	 */
	private BundlesClassLoader initClassLoader(String... directoryNames)
			throws BundleException {

		loadConfiguration(directoryNames);
		return (BundlesClassLoader) Thread.currentThread()
				.getContextClassLoader();
	}

	/**
	 * Obtain URL.
	 * 
	 * @param directoryName
	 *            bundle location
	 * @param subdirectory
	 *            sub directory or file
	 */
	private URL obtainURL(String directoryName, String subdirectory) {
		File directory = useTestFile(directoryName);

		File file = new File(directory, subdirectory);

		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Could not parse URL");
		}
	}

	/**
	 * Enables the {@link BundlesLoader} and the BundlesClassLoaderInitializer
	 * buildlet, since all other buildlets are not required by this test case.
	 */
	@Override
	protected Set<Class<?>> getEnabledBuildlets() {
		return createSet(BundlesLoader.class,
				BundlesClassLoaderInitializer.class);
	}
}