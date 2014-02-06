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
package org.conqat.engine.html_presentation;

import java.io.File;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.javascript.JavaScriptManager;
import org.conqat.engine.html_presentation.util.ResourcesManager;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1B8006BCEB029ABDBC436EFC7380724C
 */
@AConQATProcessor(description = "Writes a single HTML page from a page descriptor. "
		+ "Usually the HTMLPresentation is more appropriate.")
public class HTMLFileWriter extends ConQATProcessorBase {

	/** The input to work on. */
	private IPageDescriptor input;

	/** Output file name. */
	private String filename;

	/** Set input. */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IPageDescriptor input) {

		this.input = input;
	}

	/** Set output file. */
	@AConQATParameter(name = "file", minOccurrences = 1, maxOccurrences = 1, description = "Output file parameter.")
	public void setFilename(
			@AConQATAttribute(name = "name", description = "Name of the output file.") String filename) {
		this.filename = filename;
	}

	/** {@inheritDoc} */
	@Override
	public Object process() throws ConQATException {
		// This is performed each time, even if multiple writes go to the same
		// directory
		File outputDirectory = new File(filename).getParentFile();
		new ResourcesManager(outputDirectory).prepare();

		// this has to be rewritten each time, as further entries might have
		// been added.
		CSSMananger.getInstance().write(outputDirectory);
		JavaScriptManager.getInstance()
				.copyScript(outputDirectory, getLogger());
		new PageWriter(filename, input).write();

		return null;
	}

}