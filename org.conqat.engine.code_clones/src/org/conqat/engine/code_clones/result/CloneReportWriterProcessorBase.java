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
package org.conqat.engine.code_clones.result;

import java.io.File;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for processors that write clone reports.
 * 
 * @author Elmar Juergens
 * @author $Author: juergens $
 * @version $Revision: 41971 $
 * @ConQAT.Rating GREEN Hash: 19CF7587B40232CCDACC6F91FD249D63
 */
public abstract class CloneReportWriterProcessorBase extends
		ConQATProcessorBase {

	/** File into which report gets written */
	protected File targetFile;

	/** Creates the target file from directory and filename parameters */
	@AConQATParameter(name = "output", minOccurrences = 1, maxOccurrences = 1, description = "Name of the output directory")
	public void setOutputDirectory(
			@AConQATAttribute(name = "dir", description = "Name of the output directory") String outputDir,
			@AConQATAttribute(name = "report-name", description = "Name of the report file that gets written") String reportName) {

		targetFile = new File(outputDir, reportName);
	}

	/** {@inheritDoc} */
	@Override
	public File process() throws ConQATException {
		doWriteReport();

		getLogger().debug("Report written to: " + targetFile);

		return targetFile;
	}

	/** Hook method that deriving classes override to write actual report */
	protected abstract void doWriteReport() throws ConQATException;

}