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
package org.conqat.engine.dotnet.fxcop;

import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.FindingTestUtils;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.dotnet.test.NUnitReportReaderTestCaseBase;

/**
 * Test for the {@link FxCopReportReader}.
 * 
 * TODO (FD): This does not test the new type-based mapping as the input is a
 * text not a token scope. Maybe the FxCop reader should only work for token
 * scopes.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating RED Hash: B7A6A56125A8F1359C8E34A776DDB1A3
 */
public class FxCopReportReaderTest extends NUnitReportReaderTestCaseBase {

	/** Test the analyzer with the NUint test data. */
	public void testWithNUnit() throws Exception {

		FindingReport report = obtainFindingReport("nunit-fxcop-report.xml",
				FxCopReportReader.class);

		assertEquals(963, FindingTestUtils.countFindings(report));
		assertEquals(963, FindingTestUtils.countLocations(report,
				TextRegionLocation.class));
		assertEquals(0,
				FindingTestUtils.countLocations(report, ElementLocation.class));
	}

}