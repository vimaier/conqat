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
import java.util.Set;

import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * Logger for storing which files in a ZIP have been accessed.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41665 $
 * @ConQAT.Rating GREEN Hash: 34157176271EE2AE2447C3D542C21FB6
 */
public class ZipFileLogger implements IDeepCloneable {
	/** The set of file locations this logger encountered. */
	private Set<String> fileLocations = new HashSet<String>();

	/** Adds a file to the list of file locations. */
	public void logFile(ZipEntryContentAccessor zipEntry) {
		fileLocations.add(zipEntry.getLocation());
	}

	/** Returns all files logged so far. */
	public Set<String> getFiles() {
		return fileLocations;
	}

	/** {@inheritDoc} */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}
}
