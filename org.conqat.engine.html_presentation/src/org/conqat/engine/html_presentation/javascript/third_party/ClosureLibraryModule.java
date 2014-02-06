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
package org.conqat.engine.html_presentation.javascript.third_party;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.javascript.JavaScriptFile.EType;
import org.conqat.engine.html_presentation.javascript.JavaScriptModuleBase;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * This module provides the closure library
 * (http://code.google.com/intl/de-DE/closure/library/) as a module.
 * <p>
 * As the uncompressed size of the closure library is about 12 MB and we use it
 * as a third-party library only (no changes expected), we distribute the
 * library as a single ZIP file, that can be found in the class-resources
 * folder. The ZIP file was created from the SVN trunk
 * (http://closure-library.googlecode.com/svn/trunk) revision 1494 by copying
 * all "*.js" files.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 38040 $
 * @ConQAT.Rating GREEN Hash: 0680DF42FBAC0787B3A940A106CC6610
 */
public class ClosureLibraryModule extends JavaScriptModuleBase {

	/** {@inheritDoc} */
	@Override
	protected void createJavaScriptFiles() throws ConQATException {
		loadFromClosureZip();

		// we also load the Soy (closure templates) utils here, although
		// technically they are not part of the closure library.
		String soyutilsName = "soyutils_usegoog.js";
		addJavaScriptFile(createJavaScriptFileForClosure(EType.CODE_LIBRARY,
				soyutilsName, loadScript(soyutilsName),
				CollectionUtils.<String> emptyList(),
				CollectionUtils.<String> emptyList()));
	}

	/** Loads the library files from the closure ZIP file. */
	private void loadFromClosureZip() throws ConQATException {
		ZipInputStream zipStream = new ZipInputStream(getClass()
				.getResourceAsStream("closure-library.zip"));
		try {
			ZipEntry entry;
			while ((entry = zipStream.getNextEntry()) != null) {
				processEntry(entry, zipStream);
				zipStream.closeEntry();
			}
		} catch (IOException e) {
			throw new ConQATException(
					"Had problems while reading closure ZIP file!", e);
		} finally {
			FileSystemUtils.close(zipStream);
		}
	}

	/** Processes a single entry. */
	private void processEntry(ZipEntry entry, ZipInputStream zipStream)
			throws IOException {
		String name = entry.getName();

		// skip non-JavaScript and the dependency files
		if (entry.isDirectory() || !name.endsWith(".js")
				|| name.endsWith("deps.js")) {
			return;
		}

		addClosureJavaScript("closure-library:" + name, EType.CODE_LIBRARY,
				FileSystemUtils.readStreamUTF8(zipStream),
				CollectionUtils.<String> emptyList(),
				CollectionUtils.<String> emptyList());
	}
}
