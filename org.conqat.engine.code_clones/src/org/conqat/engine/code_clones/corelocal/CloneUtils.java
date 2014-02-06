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
package org.conqat.engine.code_clones.corelocal;

import java.io.IOException;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * This class contains clone related utility methods that depend on ConQAT.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 7E82449153E601ECB2E252B08254E79A
 */
public class CloneUtils {

	/**
	 * Read cloned code directly from the file. This only works if the clones
	 * refer to files in the file system.
	 */
	public static String getCloneContentFromLocalFileSystem(Clone clone)
			throws ConQATException {
		CanonicalFile file = ResourceUtils.getFileFromLocation(clone
				.getLocation().getLocation());
		if (file == null) {
			throw new ConQATException(
					"Could not determine file for clone location "
							+ clone.getLocation()
							+ "! This only works for files in the file system.");
		}

		try {
			return FileSystemUtils.readFile(file).substring(
					clone.getLocation().getRawStartOffset(),
					clone.getLocation().getRawEndOffset() + 1);
		} catch (IOException e) {
			throw new ConQATException("Filed to read file " + file + ": "
					+ e.getMessage(), e);
		}
	}
}