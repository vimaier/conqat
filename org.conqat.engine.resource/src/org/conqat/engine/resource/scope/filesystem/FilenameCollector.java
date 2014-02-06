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
package org.conqat.engine.resource.scope.filesystem;

import java.io.File;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.scope.PatternProcessorBase;
import org.conqat.engine.resource.util.ConQATDirectoryScanner;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40883 $
 * @ConQAT.Rating GREEN Hash: 6DACF0146F49B384E2C6E592A6D897FE
 */
@AConQATProcessor(description = "Scans a directory for files matching patterns. The result is returned as "
		+ "an array of absolute paths.")
public class FilenameCollector extends PatternProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "root", attribute = "dir", optional = false, description = "The root directory for scanning")
	public String rootDir;

	/** {@inheritDoc} */
	@Override
	public String[] process() throws ConQATException {
		String[] paths = ConQATDirectoryScanner.scan(rootDir, caseSensitive,
				includePatterns.toArray(new String[0]),
				excludePatterns.toArray(new String[0]));
		for (int i = 0; i < paths.length; i++) {
			paths[i] = new File(rootDir, paths[i]).getAbsolutePath();
		}
		return paths;
	}
}
