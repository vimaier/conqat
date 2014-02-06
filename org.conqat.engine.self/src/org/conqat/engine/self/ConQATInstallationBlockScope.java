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
package org.conqat.engine.self;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.ConQATInfo;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.scope.filesystem.FileContentAccessor;
import org.conqat.engine.resource.util.ConQATFileUtils;
import org.conqat.engine.self.scope.ConQATBundleNode;
import org.conqat.engine.self.scope.ConQATInstallationRoot;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: BAC9591E9D727B1AF5E0CA5AE324AA72
 */
@AConQATProcessor(description = "This processor creates a ITextResource tree from the "
		+ "given ConQATInstallationRoot including all block files in the bundles.")
public class ConQATInstallationBlockScope extends
		ConQATInputProcessorBase<ConQATInstallationRoot> {

	/** The content accessors created. */
	private final List<IContentAccessor> accessors = new ArrayList<IContentAccessor>();

	/** {@inheritDoc} */
	@Override
	public IContentAccessor[] process() throws ConQATException {
		for (ConQATBundleNode bundle : input.getChildren()) {
			scanForBlocks(bundle.getBundleInfo());
		}
		return CollectionUtils.toArray(accessors, IContentAccessor.class);
	}

	/** Scans for blocks in the given directory. */
	private void scanForBlocks(BundleInfo bundleInfo) throws ConQATException {
		File blocksDirectory = bundleInfo.getBlocksDirectory();
		if (blocksDirectory == null || !blocksDirectory.isDirectory()) {
			return;
		}

		CanonicalFile root = ConQATFileUtils.createCanonicalFile(bundleInfo
				.getLocation());

		for (File blockFile : FileSystemUtils.listFilesRecursively(
				blocksDirectory, new FileExtensionFilter(
						ConQATInfo.BLOCK_FILE_EXTENSION))) {
			accessors.add(new FileContentAccessor(ConQATFileUtils
					.createCanonicalFile(blockFile), root, bundleInfo.getId()));
		}
	}
}
