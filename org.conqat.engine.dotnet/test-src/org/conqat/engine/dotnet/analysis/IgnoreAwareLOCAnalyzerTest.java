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
package org.conqat.engine.dotnet.analysis;

import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextContainer;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.pattern.PatternListDef;
import org.conqat.engine.resource.regions.RegexRegionMarker;

/**
 * Test case for {@link IgnoreAwareLOCAnalyzer}
 * 
 * @author ladmin
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: F977D9C3057E784CAF3D3C3209B7BFE5
 */
public class IgnoreAwareLOCAnalyzerTest extends ResourceProcessorTestCaseBase {

	/** Test correct computation of LOC in file without ignored regions */
	public void testLocNoIgnore() throws Exception {
		assertLoc("numbers.txt", 10);
	}

	/** Test correct computation of LOC in empty file */
	public void testLocEmpty() throws Exception {
		assertLoc("empty.txt", 0);
	}

	/** Test correct computation of LOC in file with 1 char */
	public void testLoc1char() throws Exception {
		assertLoc("1char.txt", 1);
	}

	/** Test correct computation of LOC in file with generated method */
	public void testLocIgnoreMethod() throws Exception {
		assertLoc("NUnitForm.cs", 1673);
	}

	/** Assert LOC as expected */
	private void assertLoc(String filename, int expectedLoc) throws Exception {
		int loc = computeLoc(filename);
		assertEquals("Unexpected LOC", expectedLoc, loc);
	}

	/** Use {@link IgnoreAwareLOCAnalyzer} to compute loc for a file */
	private int computeLoc(String filename) throws Exception {

		ITextResource root = createTextScope(useTestFile(filename)
				.getParentFile(), new String[] { filename }, null);

		PatternList patternList = (PatternList) executeProcessor(
				PatternListDef.class,
				"(pattern=(regex='(?i)(?m)(?s)private\\s+void\\s+InitializeComponent\\s*\\(\\s*\\)\\s*\\{.*?\\}'),"
						+ "pattern=(regex='(?m)^using.*;'))");

		executeProcessor(
				RegexRegionMarker.class,
				"(mark=(patterns=",
				patternList,
				", origin=initComponent, 'start-at-file-begin'='false'), regions=(name=ignore), input=(ref=",
				root, "))");

		String locM = "LoCM";
		TextContainer rootWithResults = (TextContainer) executeProcessor(
				IgnoreAwareLOCAnalyzer.class,
				"(filter=(key=ignore), input=(ref=", root,
				"), 'loc-valid'=(key=", locM, "))");

		// obtain result
		ITextResource childWithResult = ((TextContainer) rootWithResults
				.getChildren()[0]).getNamedChild(filename);
		int loc = NodeUtils.getValue(childWithResult, locM, Integer.class, 0);
		return loc;
	}
}