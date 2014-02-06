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
package org.conqat.engine.resource.condition;

import java.io.IOException;
import java.util.zip.ZipFile;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.scope.zip.ZipPath;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41883 $
 * @ConQAT.Rating YELLOW Hash: 2A4578E1AFA53A4247F1A6EAA5412094
 */
@AConQATProcessor(description = "This condition checks whether a path points to a valid ZIP file. "
		+ "The processor returns true if the path is valid and the file can be opened as a ZIP file.")
public class ZipFileCondition extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "file", attribute = "path", description = "Path to file to check.")
	public String path;

	/** {@inheritDoc} */
	@Override
	public Boolean process() {
		ZipPath zipPath = new ZipPath(path);
		try {
			ZipFile zipFile = new ZipFile(zipPath.getZipFilename());
			zipFile.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}