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
package org.conqat.engine.cpp.clang;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Tests the {@link ClangReportReader}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46846 $
 * @ConQAT.Rating GREEN Hash: 114F090093E425A6E0AC2793D712873C
 */
public class ClangReportReaderTest extends TokenTestCaseBase {

	/** Tests report loading. */
	public void test() throws ConQATException {
		File systemDirectory = useTestFile("system");
		ITextResource system = createTextScope(systemDirectory,
				new String[] { "**.cc" }, null);
		ITextResource reports = createTextScope(useTestFile("reports"),
				new String[] { "**.plist" }, null);

		executeProcessor(ClangReportReader.class, "(input=(ref=", system,
				"), 'report-files'=(ref=", reports,
				"), 'category-name'=(value=clang), map=(prefix='', project=TEST))");

		FindingReport findingReport = NodeUtils.getFindingReport(system);

		List<String> findings = new ArrayList<String>();
		for (Finding finding : FindingUtils.getAllFindings(findingReport)) {
			findings.add(finding.getMessage() + "@"
					+ finding.getLocationString());
		}
		String actualFindings = StringUtils.concat(
				CollectionUtils.sort(findings), "\n");

		assertEquals(
				"Dereference of null pointer (loaded from variable 'c')@TEST/test1.cc:26-26\n"
						+ "Potential leak of memory pointed to by 'buffer'@TEST/subdir/test2.cc:9-9\n"
						+ "Undefined or garbage value returned to caller@TEST/test1.cc:19-19",
				actualFindings);
	}
}
