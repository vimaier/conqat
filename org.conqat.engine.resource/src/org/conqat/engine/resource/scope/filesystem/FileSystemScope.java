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
package org.conqat.engine.resource.scope.filesystem;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.logging.IncludeExcludeListLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.scope.PatternSupportingScopeBase;
import org.conqat.engine.resource.util.ConQATDirectoryScanner;
import org.conqat.engine.resource.util.ConQATFileUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 9727D0A84D8B654D9C5DA24A2C8CE84D
 */
@AConQATProcessor(description = "A scope working on the file system and reading files.")
public class FileSystemScope extends PatternSupportingScopeBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "root", attribute = "dir", description = "Path to the root directory.")
	public String rootDirectoryName;

	/** {@inheritDoc} */
	@Override
	protected IContentAccessor[] createAccessors() throws ConQATException {
		// create better error message for this case
		if (!new File(rootDirectoryName).isDirectory()) {
			throw new ConQATException("The root directory '"
					+ rootDirectoryName
					+ "' does not exist or is not a directory!");
		}

		String[] filenames = ConQATDirectoryScanner.scan(rootDirectoryName,
				caseSensitive, includePatterns.toArray(new String[0]),
				excludePatterns.toArray(new String[0]));

		IContentAccessor[] result = new IContentAccessor[filenames.length];
		CanonicalFile root = ConQATFileUtils
				.createCanonicalFile(rootDirectoryName);

		Set<String> uniformPaths = new HashSet<String>();
		for (int i = 0; i < filenames.length; ++i) {
			FileContentAccessor accessor = new FileContentAccessor(
					ConQATFileUtils.createCanonicalFile(new File(root,
							filenames[i])), root, projectName);
			result[i] = accessor;
			uniformPaths.add(accessor.getUniformPath());
		}

		getLogger().info(
				new IncludeExcludeListLogMessage("files", true, uniformPaths,
						StructuredLogTags.SCOPE, StructuredLogTags.FILES));

		return result;
	}
}