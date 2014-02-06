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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.conqat.lib.commons.string.StringUtils;

/**
 * A bundle resource manager is provided to each bundle via the bundle context
 * class and allows bundles to access resources in a clearly defined and
 * convenient way.
 * <p>
 * Resources always reside in the directory {@value #RESOURCES_LOCATION} which
 * must be a direct subdirectory of the bundle root directory. Path expression
 * to describe resources in this directory must use <em>forward</em> slashes
 * only, to allow for consistent translation to OS- specific separators.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 554A3BAC5100EF9B38DD02900B06A801
 */
public class BundleResourceManager {

	/** Location of the folder containing the bundle resources */
	public static final String RESOURCES_LOCATION = "resources";

	/** The bundle this resource manager belongs to. */
	private final BundleInfo bundleInfo;

	/**
	 * Create new resource manager for a bundle.
	 */
	/* package */BundleResourceManager(BundleInfo bundleInfo) {
		this.bundleInfo = bundleInfo;
	}

	/**
	 * Get absolute path to a resource.
	 * 
	 * @param path
	 *            path within the resource directory using forward slashes as
	 *            separators.
	 */
	public String getAbsoluteResourcePath(String path) {

		StringBuilder absolutePath = new StringBuilder();

		absolutePath.append(bundleInfo.getLocation().getAbsolutePath());
		absolutePath.append(File.separator);
		absolutePath.append(RESOURCES_LOCATION);

		if (!StringUtils.isEmpty(path)) {
			absolutePath.append(File.separator);
		}

		// remove leading slash
		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		path = path.replace('/', File.separatorChar);

		absolutePath.append(path);

		return absolutePath.toString();
	}

	/**
	 * Get resource as file.
	 * 
	 * @param path
	 *            path within the resource directory using forward slashes as
	 *            separators.
	 */
	public File getResourceAsFile(String path) {
		return new File(getAbsoluteResourcePath(path));
	}

	/**
	 * Get resource as stream.
	 * 
	 * @param path
	 *            path within the resource directory using forward slashes as
	 *            separators.
	 * @throws FileNotFoundException
	 *             if the resource could not be located.
	 */
	public InputStream getResourceAsStream(String path)
			throws FileNotFoundException {
		return new BufferedInputStream(new FileInputStream(
				getAbsoluteResourcePath(path)));
	}

	/**
	 * Get resource as URL.
	 * 
	 * @param path
	 *            path within the resource directory using forward slashes as
	 *            separators.
	 * @throws MalformedURLException
	 *             if the path could not be converted to an URL
	 */
	public URL getResourceAsURL(String path) throws MalformedURLException {
		return getResourceAsFile(path).toURI().toURL();
	}

}