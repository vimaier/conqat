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
import java.io.IOException;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.scope.zip.ZipFileLibrary;
import org.conqat.engine.resource.scope.zip.ZipPath;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41883 $
 * @ConQAT.Rating YELLOW Hash: B253DC6D95FCE51C4A64434BD9AB464F
 */
@AConQATProcessor(description = "Checks whether a file or directory exists on the filesystem or in a zip file. Can issue an error message, if it does not.")
public class FileExistsProcessor extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "file", attribute = "name", optional = false, description = "Name of file or directory whose existence is checked.")
	public String fileName;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "error", attribute = "message", optional = true, description = "If set, this error message is issued, if the file does not exist")
	public String errorMessage;

	/** {@inheritDoc} */
	@Override
	public Boolean process() {
		File file = new File(fileName);
		boolean exists = file.exists();

		ZipPath zipPath = new ZipPath(fileName);
		if (zipPath.isValid()) {
			try {
				// Try to open zip file and get relative entry
				ZipFileLibrary zipLibraray = ZipFileLibrary.getInstance();
				CanonicalFile zipFile = new CanonicalFile(
						zipPath.getZipFilename());
				exists = zipLibraray.hasEntry(zipFile, zipPath.getEntryName());
			} catch (IOException e) {
				exists = false;
			}
		}
		if (!exists && !StringUtils.isEmpty(errorMessage)) {
			getLogger()
					.error(errorMessage + "(" + file.getAbsolutePath() + ")");
		}
		return exists;
	}

}
