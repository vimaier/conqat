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
import java.io.IOException;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.xml.FindingReportIO;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35195 $
 * @ConQAT.Rating GREEN Hash: 8DC82A03E689947354040DCD7A165484
 */
@AConQATProcessor(description = "This processor reads a finding report file "
		+ "and returns its content.")
public class FindingReportReader extends ConQATProcessorBase {

	/** File to read. */
	private File file;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "file", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The file to read.")
	public void setFile(
			@AConQATAttribute(name = "path", description = "The file path") File file) {
		this.file = file;
	}

	/** {@inheritDoc} */
	@Override
	public FindingReport process() throws ConQATException {
		try {
			return FindingReportIO.readReport(file);
		} catch (IOException e) {
			throw new ConQATException("Error reading file: " + e.getMessage(),
					e);
		}
	}

}