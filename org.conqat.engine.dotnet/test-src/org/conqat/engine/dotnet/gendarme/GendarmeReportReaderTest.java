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
package org.conqat.engine.dotnet.gendarme;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.sorting.NameSorter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.dotnet.test.NUnitReportReaderTestCaseBase;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * Test class for {@link GendarmeReportReader}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 058DAC061A637176932CBCF45EB2C716
 */
public class GendarmeReportReaderTest extends NUnitReportReaderTestCaseBase {

	/**
	 * Reads 'gendarme.xml' and tests basic functionality.
	 */
	public void test() throws ConQATException {
		FindingReport report = obtainFindingReport("gendarme.xml",
				GendarmeReportReader.class);
		assertEquals(1, report.getChildren().length);
		FindingCategory category = report.getChildren()[0];
		assertEquals(2, category.getChildren().length);

		HashMap<String, FindingGroup> groups = new HashMap<String, FindingGroup>();
		for (FindingGroup group : category.getChildren()) {
			groups.put(NodeUtils.getStringValue(group,
					NodeConstants.RULE_IDENTIFIER_KEY), group);
		}

		checkGroup1(groups);
		checkGroup2(groups);
	}

	/**
	 * Check group 'AvoidCallingProblematicMethodsRule'.
	 */
	private void checkGroup1(HashMap<String, FindingGroup> groups)
			throws ConQATException {
		FindingGroup group = groups.get("AvoidCallingProblematicMethodsRule");
		assertEquals(1, group.getChildren().length);
		Finding finding = group.getChildren()[0];

		assertFindingMessage(
				finding,
				"You are calling to System.Reflection.Assembly System.Reflection.Assembly::LoadFrom(System.String), which is a potentially problematic method");

		assertTextRegionLocation(finding,
				"TEST/NUnitCore/core/AssemblyResolver.cs", 60);
	}

	/**
	 * Check group 'WriteStaticFieldFromInstanceMethodRule'.
	 */
	private void checkGroup2(HashMap<String, FindingGroup> groups)
			throws ConQATException {
		FindingGroup group = groups
				.get("WriteStaticFieldFromInstanceMethodRule");
		Finding[] children = group.getChildren();

		List<Finding> sortedChildren = CollectionUtils.sort(
				Arrays.asList(children),
				NameSorter.NameComparator.getInstance());

		assertEquals(2, children.length);

		Finding finding = sortedChildren.get(0);

		assertFindingMessage(
				finding,
				"The static field '$$method0x6000037-1', of type 'System.Collections.Generic.Dictionary`2<System.String,System.Int32>'. is being set in an instance method.");

		assertTextRegionLocation(finding,
				"TEST/NUnitCore/core/PlatformHelper.cs", 136);

		finding = sortedChildren.get(1);

		assertFindingMessage(
				finding,
				"The static field 'nextID', of type 'System.Int32'. is being set in an instance method.");

		assertTextRegionLocation(finding,
				"TEST/NUnitCore/interfaces/TestID.cs", 43);
	}

	/**
	 * Assert that a finding message is correct.
	 */
	private void assertFindingMessage(Finding finding, String message)
			throws ConQATException {
		assertEquals(
				message,
				NodeUtils.getStringValue(finding,
						EFindingKeys.MESSAGE.toString()));
	}

	/**
	 * Assert that that location of the finding is a {@link TextRegionLocation}
	 * with the provided file name and line number.
	 */
	private void assertTextRegionLocation(Finding finding, String uniformPath,
			int lineNumber) {
		TextRegionLocation regionLocation = (TextRegionLocation) finding
				.getLocation();

		assertEquals(uniformPath, regionLocation.getUniformPath());
		assertEquals(lineNumber, regionLocation.getRawStartLine());
	}

	/**
	 * Simple smoke test.
	 * 
	 * @throws Exception
	 */
	public void testSmoke() throws Exception {
		obtainFindingReport("gendarme_full.xml", GendarmeReportReader.class);
	}

}