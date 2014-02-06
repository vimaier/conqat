/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2012 the ConQAT Project                                   |
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
package org.conqat.engine.core.driver.runner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.lib.commons.filesystem.ClassPathUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Base class for runners that analyze all classes contained in libraries of
 * ConQAT.
 * 
 * @author $Author: goede $
 * @version $Rev: 46361 $
 * @ConQAT.Rating GREEN Hash: DBAE4458C7D8D337E71C1668562EB6DB
 */
public abstract class LibraryClassAnalyzingRunnerBase extends
		ConQATRunnableBase {

	/** {@inheritDoc} */
	@Override
	protected final void doRun() {
		Set<BundleInfo> bundles = bundleConfig.getBundles();
		for (BundleInfo bundle : bundles) {
			for (File library : bundle.getLibraries()) {
				try {
					processClasses(library);
				} catch (IOException e) {
					System.err.println("Failed to process library " + library);
					e.printStackTrace();
					System.exit(2);
				}
			}
		}

		System.exit(reportResults());
	}

	/**
	 * Template method called after all classes have been processed. This can be
	 * used to report results to the user. The return value should be the
	 * intended exit code, i.e. 0 for success and all other values to signal
	 * problems.
	 */
	protected abstract int reportResults();

	/** Processes all classes from the given library. */
	private void processClasses(File library) throws IOException {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(library);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				processEntry(entries.nextElement(), jarFile);
			}
		} finally {
			FileSystemUtils.close(jarFile);
		}
	}

	/** Processes a single JAR entry. */
	private void processEntry(JarEntry entry, JarFile jarFile)
			throws IOException {
		if (entry.isDirectory()
				|| !entry.getName().endsWith(ClassPathUtils.CLASS_FILE_SUFFIX)) {
			return;
		}

		InputStream input = null;
		try {
			input = jarFile.getInputStream(entry);
			byte[] content = FileSystemUtils.readStreamBinary(input);
			processClass(entry.getName(), jarFile.getName(), content);
		} finally {
			FileSystemUtils.close(input);
		}
	}

	/** Template method for processing a single class. */
	protected abstract void processClass(String classFileName,
			String libraryName, byte[] byteCodeContent);

}
