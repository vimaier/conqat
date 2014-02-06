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

import java.io.IOException;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.base.ContentAccessorBase;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Content accessor for plain files.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 43306 $
 * @ConQAT.Rating GREEN Hash: 9F2D7542CEA48CE25FFECA10A2EBFBDA
 */
public class FileContentAccessor extends ContentAccessorBase {

	/** The underlying file. */
	private final CanonicalFile file;

	/** Constructor. projectName may be <code>null</code> or empty. */
	public FileContentAccessor(CanonicalFile file, CanonicalFile root,
			String projectName) {
		super(UniformPathUtils.concatenate(projectName,
				obtainRelativePath(file, root)));
		this.file = file;
	}

	/** Constructor. */
	public FileContentAccessor(CanonicalFile file, String uniformPath) {
		super(uniformPath);
		this.file = file;
	}

	/** Return relative part of the path. */
	protected static String obtainRelativePath(CanonicalFile file,
			CanonicalFile root) {
		String localPath = StringUtils.stripPrefix(root.getCanonicalPath(),
				file.getCanonicalPath());
		return StringUtils.stripPrefix(UniformPathUtils.SEPARATOR,
				FileSystemUtils.normalizeSeparators(localPath));
	}

	/** {@inheritDoc} */
	@Override
	protected byte[] readContent() throws IOException {
		return FileSystemUtils.readFileBinary(file);
	}

	/** {@inheritDoc} */
	@Override
	public String getLocation() {
		return file.getCanonicalPath();
	}

	/** {@inheritDoc} */
	@Override
	public IContentAccessor createRelative(String relativePath)
			throws ConQATException {

		CanonicalFile newFile = resolveRelativePath(relativePath);

		if (!newFile.isReadableFile()) {
			throw new ConQATException("Could not resolve relative path! File "
					+ newFile + " can not be read.");
		}

		return new FileContentAccessor(newFile,
				determineNewUniformPath(newFile));
	}

	/** Resolves a relative path w.r.t. this accessor */
	private CanonicalFile resolveRelativePath(String relativePath)
			throws ConQATException {
		relativePath = UniformPathUtils.normalizeAllSeparators(relativePath);

		try {
			return new CanonicalFile(file.getParentFile(), relativePath);
		} catch (IOException e) {
			throw new ConQATException("Could not canonize: " + e.getMessage(),
					e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String createRelativeUniformPath(String relativePath)
			throws ConQATException {
		CanonicalFile targetFile = resolveRelativePath(relativePath);
		return determineNewUniformPath(targetFile);
	}

	/**
	 * Determines the new uniform path.
	 * 
	 * @throws ConQATException
	 *             if the newFile is not located under the root of the uniform
	 *             path of this file
	 */
	/* package */String determineNewUniformPath(CanonicalFile newFile)
			throws ConQATException {
		// we keep everything normalized
		String filePath = UniformPathUtils.normalizeAllSeparators(file
				.getCanonicalPath());
		String newFilePath = UniformPathUtils.normalizeAllSeparators(newFile
				.getCanonicalPath());

		String pathPrefix = StringUtils.longestCommonPrefix(filePath,
				newFilePath);
		pathPrefix = UniformPathUtils.getParentPath(pathPrefix);

		String fileSuffix = StringUtils.stripPrefix(pathPrefix, filePath);
		String uniformPrefix = StringUtils.stripSuffix(fileSuffix,
				getUniformPath());

		// make sure that the files have a uniform prefix (This is for CR#3690)
		if (getUniformPath().equals(uniformPrefix)) {
			throw new ConQATException("The file " + newFile
					+ " is not located under the root of the uniform path.");
		}

		String newUniformPath = uniformPrefix
				+ StringUtils.stripPrefix(pathPrefix, newFilePath);
		return newUniformPath;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getUniformPath() + " (" + getLocation() + ")";
	}
}