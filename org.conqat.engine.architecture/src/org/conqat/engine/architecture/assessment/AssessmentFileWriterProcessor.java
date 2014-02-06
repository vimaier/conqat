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
package org.conqat.engine.architecture.assessment;

import java.io.File;
import java.io.IOException;

import org.conqat.engine.architecture.scope.ArchitectureDefinition;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41274 $
 * @ConQAT.Rating GREEN Hash: A0336531E0368CB609247E6B2AD6B3B4
 */
@AConQATProcessor(description = "Writes an assessed architecture to a file.")
public class AssessmentFileWriterProcessor extends ConQATProcessorBase {

	/** File that gets written */
	private File targetFile;

	/** Assessed {@link ArchitectureDefinition} that gets written to the file */
	private ArchitectureDefinition input;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "output", minOccurrences = 0, maxOccurrences = 1, description = "Set target file by passing directory and local filename")
	public void setOutputDirectory(
			@AConQATAttribute(name = "dir", description = "Name of the output directory") String outputDir,
			@AConQATAttribute(name = "filename", description = "Name of the file that gets written") String reportName)
			throws ConQATException {
		assertTargetFileNull();
		targetFile = new File(outputDir, reportName);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "target", minOccurrences = 0, maxOccurrences = 1, description = "Set target file by passing a file")
	public void setTargetFile(
			@AConQATAttribute(name = "file", description = "File into which assessment gets written") File targetFile)
			throws ConQATException {
		assertTargetFileNull();
		this.targetFile = targetFile;
	}

	/** Make sure that targetFile has not already been set */
	private void assertTargetFileNull() throws ConQATException {
		if (targetFile != null) {
			throw new ConQATException(
					"Can only set target file once. Target file has already been set to: "
							+ targetFile);
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ArchitectureDefinition input) {
		this.input = input;
	}

	/** {@inheritDoc} */
	@Override
	public File process() throws ConQATException {
		if (targetFile == null) {
			throw new ConQATException(
					"No target file provided. Please set target file.");
		}

		try {
			FileSystemUtils.ensureParentDirectoryExists(targetFile);
			AssessmentFileWriter writer = new AssessmentFileWriter(targetFile,
					input, getLogger());
			writer.writeArchitecture();
			writer.close();
		} catch (IOException e) {
			throw new ConQATException("Could not write file", e);
		}

		return targetFile;
	}
}