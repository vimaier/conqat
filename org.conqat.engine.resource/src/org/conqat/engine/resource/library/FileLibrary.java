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
package org.conqat.engine.resource.library;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Library for various IO purposes.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8450FEC9AACDF8EB261BDCCD94720C99
 */
public class FileLibrary {

	/**
	 * Checks if a directory exists. If not it creates the directory and all
	 * necessary parent directories.
	 * <p>
	 * In contrast to {@link FileSystemUtils#ensureDirectoryExists(File)}, this
	 * method throws a {@link ConQATException} instead of an {@link IOException}.
	 */
	public static void ensureDirectoryExists(String path)
			throws ConQATException {
		try {
			FileSystemUtils.ensureDirectoryExists(new File(path));
		} catch (IOException e) {
			throw new ConQATException("Error creating output directory: "
					+ e.getMessage());
		}
	}

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

	/**
	 * Assembles the full file name from a relative file name and the file name
	 * of the base file, in which the relative file name was found.
	 * 
	 * Returned absolute path fragment is platform independent
	 * ("\"s are replaced with "/").
	 */
	public static String absoluteFilenameFrom(String baseFilename,
			String relativeFilename) {

		String absoluteFilename = new File(
				new File(baseFilename).getParentFile(), relativeFilename)
				.getPath();
		absoluteFilename = absoluteFilename.replaceAll("\\\\", "/");
		return absoluteFilename;
	}

	/**
	 * Create set of canonical files from collection of paths.
	 * 
	 * @throws ConQATException
	 *             if canonization fails.
	 */
	public static Set<CanonicalFile> canonize(Collection<String> paths)
			throws ConQATException {
		HashSet<CanonicalFile> result = new HashSet<CanonicalFile>();
		for (String path : paths) {
			result.add(FileLibrary.createCanonicalFile(path));
		}
		return result;
	}

	/** Creates set of canonical files from a collection of files */
	public static Set<CanonicalFile> canonize(List<File> files)
			throws ConQATException {
		Set<CanonicalFile> canonicalFiles = new HashSet<CanonicalFile>();
		for (File file : files) {
			canonicalFiles.add(createCanonicalFile(file));
		}
		return canonicalFiles;
	}
}