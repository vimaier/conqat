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
package org.conqat.engine.dotnet.test;

import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.util.FindingsReportExtractor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.ITextResource;
import org.junit.Ignore;

/**
 * Base class for test cases testing the report readers on the NUnit test data.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 38115 $
 * @ConQAT.Rating YELLOW Hash: 70218EFCFD0B60465085377395F97898
 */
@Ignore
public class NUnitReportReaderTestCaseBase extends
		ResourceProcessorTestCaseBase {

	/** Create a finding report for the given report. */
	public FindingReport obtainFindingReport(String filename,
			Class<? extends IConQATProcessor> reportReaderProcessor)
			throws ConQATException {
		ITextResource report = createTextScope(useCanonicalTestFile(""),
				new String[] { filename }, new String[0]);
		ITextResource input = createTextScope(
				useCanonicalTestFile("../org.conqat.engine.dotnet.scope/NUnit_Folder"),
				new String[] { "**/*.cs" }, new String[0]);
		ITextResource element = (ITextResource) executeProcessor(
				reportReaderProcessor,
				"(input=(ref=",
				input,
				"),'report-files'=(ref=",
				report,
				"),map=(prefix='z:\\location_unlikely_to_exist\\nunit-2.4.1\\src',project='TEST'), lenient=(mode=true))");
		FindingReport result = (FindingReport) executeProcessor(
				FindingsReportExtractor.class, "(input=(ref=", element, "))");

		return result;
	}

}