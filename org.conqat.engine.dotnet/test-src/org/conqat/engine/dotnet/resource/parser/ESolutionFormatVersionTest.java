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
package org.conqat.engine.dotnet.resource.parser;

import java.io.File;
import java.util.List;

import org.conqat.engine.dotnet.resource.ProjectContentExtractorBase;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;

/**
 * Test case for {@link ProjectContentExtractorBase}
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45825 $
 * @ConQAT.Rating GREEN Hash: 099C9B0B1D0FE21C90546B130EA392DA
 */
public class ESolutionFormatVersionTest extends ResourceProcessorTestCaseBase {

	/** Test for determining the format. */
	public void testDetermineFormat() throws Exception {
		assertProjectVersions(new String[] { "**/*.csproj" },
				new String[] { "**/*_*" }, ESolutionFormatVersion.VERSION_8);

		assertProjectVersions(new String[] { "**/*_VS200*.csproj" },
				new String[0], ESolutionFormatVersion.VERSION_9);
	}

	/** Assert that all project files adhere to a specified version */
	private void assertProjectVersions(String[] includePatterns,
			String[] excludePatterns, ESolutionFormatVersion expectedVersion)
			throws Exception {
		List<ITextElement> projectElements = getProjectElements(
				includePatterns, excludePatterns);
		for (ITextElement projectElement : projectElements) {
			ESolutionFormatVersion actualVersion = ESolutionFormatVersion
					.determineProjectFormat(projectElement);
			assertEquals(
					"Unexpected version in :" + projectElement.getLocation(),
					expectedVersion, actualVersion);
		}
	}

	/** Get list of project elements */
	private List<ITextElement> getProjectElements(String[] includePatterns,
			String[] excludePatterns) throws Exception {
		File root = useTestFile("../org.conqat.engine.dotnet.scope/NUnit_Folder");
		root = root.getCanonicalFile();
		ITextResource scope = createTextScope(root, includePatterns,
				excludePatterns);
		List<ITextElement> projectElements = ResourceTraversalUtils
				.listTextElements(scope);
		assertTrue(projectElements.size() > 0);
		return projectElements;
	}

}