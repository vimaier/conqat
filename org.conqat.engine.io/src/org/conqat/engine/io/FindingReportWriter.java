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
package org.conqat.engine.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.xml.FindingReportIO;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35195 $
 * @ConQAT.Rating GREEN Hash: 2D3933C95D41C73CF3215833D73DC0C3
 */
@AConQATProcessor(description = "This processor writes a finding report to an XML file.")
public class FindingReportWriter extends FileWriterBase {

	/** The report being serialized. */
	private FindingReport report;

	/** Whether the report should be compressed. */
	private boolean compress = true;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "report", minOccurrences = 1, maxOccurrences = 1, description = "The report being written.")
	public void setReport(
			@AConQATAttribute(name = "ref", description = "The report being written.") FindingReport report) {
		this.report = report;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "compress", minOccurrences = 0, maxOccurrences = 1, description = "Sets whether the report should be compressed (default is to compress).")
	public void setCompress(
			@AConQATAttribute(name = "value", description = "If this is true, the report will be compressed (GZIP).") boolean value) {
		compress = value;
	}

	/** {@inheritDoc} */
	@Override
	protected void writeFile(File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		if (compress) {
			out = new GZIPOutputStream(out);
		}
		FindingReportIO.writeReport(report, out);
	}

}