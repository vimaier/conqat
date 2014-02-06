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

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.scope.ScopeBase;
import org.conqat.engine.resource.scope.zip.ZipEntryContentAccessor;
import org.conqat.engine.resource.scope.zip.ZipFileLogger;
import org.conqat.engine.resource.scope.zip.ZipPath;
import org.conqat.engine.resource.util.ConQATFileUtils;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41883 $
 * @ConQAT.Rating YELLOW Hash: 4B240A2C35E3E7731F886536E91E493E
 */
@AConQATProcessor(description = "Scope for a single file.")
public class SingleFileScope extends ScopeBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "file", attribute = "path", description = "Path to file")
	public String path;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(attribute = "ref", parameter = "zip-file-logger", optional = true, description = ""
			+ "If this parameter is set, all the files that are read in the zip are logged.")
	public ZipFileLogger zipFileLogger = null;

	/** {@inheritDoc} */
	@Override
	protected IContentAccessor[] createAccessors() throws ConQATException {
		IContentAccessor accessor;

		ZipPath zipPath = new ZipPath(path);
		if (zipPath.isValid()) {
			CanonicalFile zipFile = ConQATFileUtils.createCanonicalFile(zipPath
					.getZipFilename());
			checkFileIsReadable(zipFile);
			accessor = new ZipEntryContentAccessor(zipFile,
					zipPath.getEntryName(), projectName
							+ UniformPathUtils.SEPARATOR
							+ zipPath.getEntryName(), zipFileLogger);
		} else {
			CanonicalFile file = ConQATFileUtils.createCanonicalFile(path);
			checkFileIsReadable(file);
			accessor = new FileContentAccessor(file, projectName
					+ UniformPathUtils.SEPARATOR + file.getName());
		}

		getLogger().debug("Using file at location: " + accessor.getLocation());

		return new IContentAccessor[] { accessor };
	}

	/** Throws an ConQAT Exception if the file does not exist. */
	private void checkFileIsReadable(CanonicalFile file) throws ConQATException {
		if (!file.isReadableFile()) {
			throw new ConQATException("Cannot read file at path " + file);
		}
	}
}