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
import java.net.URLClassLoader;

import org.conqat.engine.core.driver.error.EDriverExceptionType;

/**
 * A classloader for bundles. Adding a bundle to the classloader provides access
 * to the classes defined by the bundle itself and the classes contained in the
 * bundle's libraries.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2713E48A3E4366621A371834A3FB761F
 */
public class BundlesClassLoader extends URLClassLoader {
	/**
	 * Create new class loader with no URLs specified.
	 */
	/* package */BundlesClassLoader() {
		super(new URL[0], Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Add bundle to class loader. This adds the classes directory and all
	 * libraries.
	 * 
	 * @param bundleInfo
	 *            the bundle to add
	 * @throws BundleException
	 *             if a classpath location could not be converted to an URL
	 */
	/* package */void addBundle(BundleInfo bundleInfo) throws BundleException {
		if (bundleInfo.hasClasses()) {
			addLocation(bundleInfo.getClassesDirectory(), bundleInfo);
		}

		for (File lib : bundleInfo.getLibraries()) {
			addLocation(lib, bundleInfo);
		}
	}

	/**
	 * Add location.
	 * 
	 * @param location
	 *            this can be either a file or a directory
	 * @param bundleInfo
	 *            bundle this location belong to. This is used for proper error
	 *            messages.
	 * @throws BundleException
	 *             if the location could not be converted to an URL
	 */
	private void addLocation(File location, BundleInfo bundleInfo)
			throws BundleException {
		try {
			URL url = location.toURI().toURL();
			addURL(url);
		} catch (MalformedURLException e) {
			throw new BundleException(
					EDriverExceptionType.ILLEGAL_URL_FROM_PATH,
					"Could not parse location " + location + " as URL in"
							+ bundleInfo, bundleInfo.getLocation());
		}
	}
}