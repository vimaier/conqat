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

import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.html_presentation.BundleContext;
import org.conqat.engine.html_presentation.HTMLPresentation;

/**
 * Base class for layouters that create image files.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A012A3522AAD4FA985B0860191AFFFE9
 */
public abstract class ImageCreatingLayouterBase extends LayouterBase {

	/** The output directory. */
	protected File outputDirectory;

	/**
	 * Set output directory. Must be the same as specified for
	 * {@link HTMLPresentation}.
	 */
	@AConQATParameter(name = "output", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Output directory; must be the same as specified for HtmlPresentation.")
	public void setOutputDirectory(
			@AConQATAttribute(name = "dir", description = "Name of the output directory") String outputDirectoryName) {

		outputDirectory = new File(outputDirectoryName);
	}

	/**
	 * Get link target for file with suffix.
	 * 
	 * @param suffix
	 *            file suffix (without dot).
	 */
	protected String getHRef(String suffix) {
		return getHRef(null, suffix);
	}

	/**
	 * Get link target for file with name and suffix.
	 * 
	 * @param suffix
	 *            file suffix (without dot).
	 */
	protected String getHRef(String name, String suffix) {
		return ResourcesManager.IMAGES_DIRECTORY_NAME + "/"
				+ getFilename(name, suffix);
	}

	/**
	 * Get file with suffix.
	 * 
	 * @param suffix
	 *            file suffix (without dot).
	 */
	protected File getFile(String suffix) {
		return getFile(null, suffix);
	}

	/**
	 * Get file with name and suffix.
	 * 
	 * @param suffix
	 *            file suffix (without dot).
	 */
	protected File getFile(String name, String suffix) {
		return FileSystemUtils.newFile(outputDirectory,
				ResourcesManager.IMAGES_DIRECTORY_NAME, getFilename(name,
						suffix));
	}

	/**
	 * Create a unique file name based on the name of the processor, the
	 * provided name and the suffix. If the name is <code>null</code> or empty,
	 * the resulting file name is based only on the processor name and the
	 * suffix. The resulting name may be abbreviated.
	 */
	private String getFilename(String name, String suffix) {
		String namePart;
		if (StringUtils.isEmpty(name)) {
			namePart = StringUtils.EMPTY_STRING;
		} else {
			namePart = "_" + name;
		}
		return BundleContext.getInstance().getHtmlPresentationManager()
				.getAbbreviation(getProcessorInfo().getName())
				+ namePart + "." + suffix;
	}
}