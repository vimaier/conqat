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
package org.conqat.engine.core.bundle.library;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.driver.runner.ConQATRunnableBase;
import org.conqat.lib.commons.options.AOption;

/**
 * Base class for actions regarding analysis of bundle libraries and
 * descriptors.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 46536 $
 * @ConQAT.Rating YELLOW Hash: BD740BC8E21711B3B6CC0DD306E9D789
 */
public abstract class LibraryDescriptorRunnerBase extends ConQATRunnableBase {

	/** The output stream to print results to. */
	private PrintStream outputStream = System.out;

	/**
	 * Changes the output stream to the given file.
	 */
	@AOption(shortName = 'o', longName = "out", description = "Prints the output into the given file instead of standard output.")
	public void setOutputFile(String fileName) throws IOException {
		File file = new File(fileName);

		outputStream = new PrintStream(file);
	}

	/** {@inheritDoc} */
	@Override
	protected final void doRun() {
		doRun(outputStream);

		if (outputStream != System.out) {
			outputStream.close();
		}

	}

	/** Performs the actual execution and prints results to the given stream. */
	abstract void doRun(PrintStream out);

	/** @return The a list of all loaded java libraries. */
	protected Set<File> getLibraryFiles() {
		Set<File> libraries = new HashSet<File>();
		for (BundleInfo bundle : bundleConfig.getBundles()) {
			libraries.addAll(bundle.getLibraries());
		}
		return libraries;
	}

	/**
	 * @return A list of library descriptors for all loaded bundles.
	 */
	protected Set<LibraryDescriptor> getLibraryDescriptors() {

		Set<LibraryDescriptor> descriptors = new HashSet<LibraryDescriptor>();
		for (BundleInfo bundle : bundleConfig.getBundles()) {
			descriptors.addAll(bundle.getLibraryDescriptors());
		}

		return descriptors;
	}
}
