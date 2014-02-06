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
package org.conqat.engine.svn;

import java.io.File;
import java.util.List;

import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.tmatesoft.svn.core.SVNProperty;

import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Test for {@link SVNPropertiesExtractor}. This test works on test-data stored
 * in a ZIP file. The reason for this is that the tests should work both in an
 * (exported) release version without .svn directories, but also in the
 * development context, where .svn directory are everywhere. AS we can not
 * checking .svn directories with meta-data, the ZIP file is used.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 6068DA2C9AD918C8DE60D3F205BE0073
 */
public class SVNPropertiesExtractorTest extends ResourceProcessorTestCaseBase {

	/**
	 * {@inheritDoc}
	 * <p>
	 * Unzips the test-data.
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		File targetDir = getTmpDirectory();
		if (targetDir.isDirectory()) {
			FileSystemUtils.deleteRecursively(getTmpDirectory());
		}
		FileSystemUtils.ensureDirectoryExists(targetDir);
		FileSystemUtils.unjar(useTestFile("src.zip"), getTmpDirectory());
	}

	/**
	 * Tests if all Java files in the src folder have the svn:keywords property.
	 */
	public void test() throws Exception {
		IResource root = createBinaryScope(getTmpDirectory(),
				new String[] { "**/*.java" }, null);

		executeProcessor(SVNPropertiesExtractor.class, "(input=(ref=", root,
				"), property=(name='", SVNProperty.KEYWORDS, "'))");

		List<IElement> elements = ResourceTraversalUtils.listElements(root,
				IElement.class);
		assertTrue(elements.size() == 5);

		for (IElement element : elements) {
			Object property = element.getValue(SVNProperty.KEYWORDS);
			assertNotNull(property);
			assertTrue(property.toString().toLowerCase().contains("id"));
		}
	}
}