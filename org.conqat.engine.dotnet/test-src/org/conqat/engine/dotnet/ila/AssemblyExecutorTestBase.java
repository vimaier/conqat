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
package org.conqat.engine.dotnet.ila;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.dotnet.BundleContext;
import org.conqat.engine.dotnet.DotnetExecutorBase;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.library.FileLibrary;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Base class for test cases that test classes that extend
 * {@link DotnetExecutorBase}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 46322 $
 * @ConQAT.Rating YELLOW Hash: E42A5F902C504407BD39702E3EA5A77D
 */
public abstract class AssemblyExecutorTestBase extends
		ResourceProcessorTestCaseBase {

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		initBundleContext();
		ensureEmptyTmpDir();
	}

	/**
	 * The {@link BundleContext} is required by executors to find their
	 * assembly. The ConQAT Driver is responsible for its initialization. If we
	 * use processors without using the Driver, we need to initialize it
	 * ourself.
	 */
	protected abstract void initBundleContext() throws BundleException,
			IOException;

	/** Retrieve a sorted list of files from the tmp folder */
	protected List<CanonicalFile> sortedTmpFiles() throws ConQATException {
		Set<CanonicalFile> tmpFiles = FileLibrary.canonize(FileSystemUtils
				.listFilesRecursively(getTmpDirectory()));
		return CollectionUtils.sort(tmpFiles);
	}

	/** Retrieve sorted list of files under input */
	protected List<File> sortedFiles(IResource input) {
		List<File> files = new ArrayList<File>();
		for (IElement fileElement : ResourceTraversalUtils.listElements(input)) {
			File file = ResourceUtils.getFile(fileElement);
			assertTrue("File must exist", file.exists());
			files.add(file);
		}
		Collections.sort(files);
		return files;
	}

	/** Returns true if the current operating system is microsoft windows. */
	protected static boolean isWindowsVM() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains("win");
	}
}