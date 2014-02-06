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
package org.conqat.engine.html_presentation.util;

import java.io.File;
import java.io.IOException;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.BundleContext;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class handles the copying of resources files to the output directory.
 * 
 * @author $Author: goede $
 * @version $Rev: 42594 $
 * @ConQAT.Rating YELLOW Hash: E07E028D228C174BCC3FB140DC71C078
 */
public class ResourcesManager {

	/** Directory for images. */
	public static final String IMAGES_DIRECTORY_NAME = "images";

	/** Prefixes of files being ignored during the initial copy. */
	private static final String[] IGNORED_FILE_PREFIXES = { ".", "font",
			"js-cache" };

	/** Output directory. */
	private final File outputDirectory;

	/**
	 * Create a new resource manager.
	 * 
	 * @param outputDirectory
	 *            Output directory.
	 */
	public ResourcesManager(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	/**
	 * Creates output directory and copies resources to the output directory.
	 */
	public void prepare() throws ConQATException {
		try {
			FileSystemUtils.ensureDirectoryExists(outputDirectory);
		} catch (IOException e) {
			throw new ConQATException("Couldn't create output directory: "
					+ outputDirectory);
		}

		for (File resourcePath : BundleContext.getInstance()
				.getHtmlPresentationManager().getResourcePaths()) {
			try {
				copyFilesRecursively(resourcePath, outputDirectory);
			} catch (IOException e) {
				throw new ConQATException(e);
			}
		}
	}

	/**
	 * Copies the contents of the given source directory to the destination
	 * directory. Files starting with one of {@link #IGNORED_FILE_PREFIXES} are
	 * ignored.
	 */
	private void copyFilesRecursively(File source, File destination)
			throws IOException {
		for (String name : source.list()) {
			File sourceFile = new File(source, name);
			File destFile = new File(destination, name);

            if (!StringUtils.startsWithOneOf(name, IGNORED_FILE_PREFIXES)) {
                if (sourceFile.isDirectory()) {
                    FileSystemUtils.ensureDirectoryExists(destFile);
                    copyFilesRecursively(sourceFile, destFile);
                } else {
                    FileSystemUtils.copyFile(sourceFile, destFile);
                }
            }
		}
	}

}