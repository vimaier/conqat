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

import java.io.File;
import java.io.IOException;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.filesystem.CanonicalFile;

/**
 * Utility methods for dealing with files.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: AAAAF387C3A61C886E6FB7A033C80060
 */
public class ConQATFileUtils {

	/**
	 * Create {@link CanonicalFile} and convert the thrown {@link IOException}
	 * to a {@link ConQATException}.
	 */
	public static CanonicalFile createCanonicalFile(String path)
			throws ConQATException {
		try {
			return new CanonicalFile(path);
		} catch (IOException e) {
			throw new ConQATException("Could not canonize file: "
					+ e.getMessage(), e);
		}
	}

	/**
	 * Create {@link CanonicalFile} and convert the thrown {@link IOException}
	 * to a {@link ConQATException}.
	 */
	public static CanonicalFile createCanonicalFile(File file)
			throws ConQATException {
		return createCanonicalFile(file.getPath());
	}
}