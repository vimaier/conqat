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

import org.conqat.lib.commons.version.Version;

/**
 * This class describes dependencies from one bundle to another as expressed in
 * the bundle descriptor.
 * <p>
 * This class is immutable.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E64879AB72E925659BCE37E02772F76C
 */
public class BundleDependency {

	/** The id of the required bundle. */
	private final String bundleId;

	/** The required version. */
	private final Version version;

	/**
	 * Create new dependency.
	 * 
	 * @param bundleId
	 *            id of the required bundle.
	 * @param version
	 *            required version.
	 * 
	 */
	public BundleDependency(String bundleId, Version version) {
		this.bundleId = bundleId;
		this.version = version;
	}

	/** Get id of required bundle. */
	public String getId() {
		return bundleId;
	}

	/** Get version of required bundle. */
	public Version getVersion() {
		return version;
	}

	/**
	 * Returns required id and version.
	 */
	@Override
	public String toString() {
		return bundleId + " [" + version + "]";
	}

}