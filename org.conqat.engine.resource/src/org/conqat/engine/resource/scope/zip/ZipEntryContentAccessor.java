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
package org.conqat.engine.resource.scope.zip;

import java.io.IOException;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.base.ContentAccessorBase;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Content accessor for ZIP entries.
 * 
 * @author $Author: juergens $
 * @version $Rev: 41875 $
 * @ConQAT.Rating GREEN Hash: E01FE6089E700D83E24348E5279A5C95
 */
public class ZipEntryContentAccessor extends ContentAccessorBase {

	/** The file corresponding to the ZIP file. */
	private final CanonicalFile zipFile;

	/** The name of the ZIP entry. */
	private final String entryName;

	/**
	 * The prefix, which is stripped form the uniform path. We need this one in
	 * order to create relative content accessors.
	 */
	private String entryPrefix = null;

	/** The Zip File logger. */
	private final ZipFileLogger zipFileLogger;

	/**
	 * Flag if the files in the ZIP are accessed in a case sensitive or case
	 * insensitive manner.
	 */
	private final boolean caseSensitive;

	/** Constructor. */
	public ZipEntryContentAccessor(CanonicalFile zipFile, String entryName,
			String entryPrefix, String projectName, boolean caseSensitive,
			ZipFileLogger zipFileLogger) throws ConQATException {
		this(zipFile, canonizeEntryName(zipFile, entryName, caseSensitive),
				projectName
						+ UniformPathUtils.SEPARATOR
						+ stripEntryPrefix(zipFile, entryName, entryPrefix,
								caseSensitive), caseSensitive, zipFileLogger);
		this.entryPrefix = entryPrefix;
	}

	/**
	 * Constructor. Explicitly sets the canonical path to a given string. As the
	 * canonical path should match an entry in the zip file, the zip file is
	 * accessed in a case-sensitive manner.
	 */
	public ZipEntryContentAccessor(CanonicalFile zipFile, String entryName,
			String canonicalPath, ZipFileLogger zipFileLogger) {
		this(zipFile, entryName, canonicalPath, true, zipFileLogger);
	}

	/** Private Constructor. */
	private ZipEntryContentAccessor(CanonicalFile zipFile, String entryName,
			String uniformPath, boolean caseSensitive,
			ZipFileLogger zipFileLogger) {
		super(uniformPath);
		this.zipFile = zipFile;
		this.entryName = entryName;
		this.caseSensitive = caseSensitive;
		this.zipFileLogger = zipFileLogger;
	}

	/** Return relative part of the path. */
	private static String stripEntryPrefix(CanonicalFile zipFile, String entry,
			String entryPrefix, boolean caseSensitive) throws ConQATException {
		String canonizedEntryName = canonizeEntryName(zipFile, entry,
				caseSensitive);

		// convert entry and prefix to lowercase if accessing zip file in an
		// case-insensitive manner, so we can strip the prefix correctly.
		if (!caseSensitive) {
			entry = entry.toLowerCase();
			entryPrefix = entryPrefix.toLowerCase();
		}

		// strip the prefix from realEntryName if found in the
		// case-insensitive representation.
		if (entry.startsWith(entryPrefix)) {
			canonizedEntryName = canonizedEntryName.substring(entryPrefix
					.length());
		}

		return StringUtils.stripPrefix(UniformPathUtils.SEPARATOR,
				FileSystemUtils.normalizeSeparators(canonizedEntryName));
	}

	/**
	 * returns the real (case-sensitive) name of a zip file entry. If the entry
	 * is not found, the given entry name is returned as we do not throw
	 * exceptions for this case, similar to FileContentAccessors.
	 */
	private static String canonizeEntryName(CanonicalFile zipFile,
			String entry, boolean caseSensitive) throws ConQATException {
		try {
			entry = UniformPathUtils.normalizeAllSeparators(entry);
			String canonizedEntryName = ZipFileLibrary.getInstance()
					.getEntryName(zipFile, entry, caseSensitive);

			if (canonizedEntryName == null) {
				canonizedEntryName = entry;
			}

			return canonizedEntryName;
		} catch (IOException e) {
			throw new ConQATException(e.getMessage(), e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected byte[] readContent() throws IOException {
		if (zipFileLogger != null) {
			zipFileLogger.logFile(this);
		}
		return ZipFileLibrary.getInstance().readContent(zipFile, entryName,
				caseSensitive);
	}

	/** {@inheritDoc} */
	@Override
	public IContentAccessor createRelative(String relativePath)
			throws ConQATException {
		// we can reuse the UniformPathUtils, as entries follow uniform path
		// conventions
		String newEntry = createRelativeUniformPath(relativePath);
		try {
			if (!ZipFileLibrary.getInstance().hasEntry(zipFile, newEntry,
					caseSensitive)) {
				throw new ConQATException("The ZIP file " + zipFile
						+ " has no entry named " + newEntry);
			}
		} catch (IOException e) {
			throw new ConQATException(e.getMessage(), e);
		}

		String projectName = UniformPathUtils.extractProject(getUniformPath());

		return new ZipEntryContentAccessor(zipFile,
				UniformPathUtils.resolveRelativePath(entryName,
						UniformPathUtils.normalizeAllSeparators(relativePath)),
				entryPrefix, projectName, caseSensitive, zipFileLogger);
	}

	/** {@inheritDoc} */
	@Override
	public String createRelativeUniformPath(String relativePath) {
		// we can reuse the UniformPathUtils, as entries follow uniform path
		// conventions
		return UniformPathUtils.resolveRelativePath(entryName,
				UniformPathUtils.normalizeAllSeparators(relativePath));
	}

	/**
	 * {@inheritDoc}.
	 * <p>
	 * The format follows the JAR URLs used in Java and consists of the path to
	 * the ZIP file followed by the name of the entry with a '!' as separator.
	 */
	@Override
	public String getLocation() {
		return zipFile.getCanonicalPath() + ZipPath.ZIPFILE_SEPARATOR
				+ entryName;
	}
}