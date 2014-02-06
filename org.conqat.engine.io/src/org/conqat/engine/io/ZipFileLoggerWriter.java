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
package org.conqat.engine.io;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.SetNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IConQATProcessorInfo;
import org.conqat.engine.core.core.IShutdownHook;
import org.conqat.engine.resource.scope.zip.ZipFileLogger;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41868 $
 * @ConQAT.Rating GREEN Hash: 84FF1BD1D9B9861A1F168429830E3D55
 */
@AConQATProcessor(description = "A Processor that writes a file of all files in a zip which have been accessed. The output file"
		+ " is a CSV: The first line is a header with the column name, every succeeding line a path to"
		+ " a zip file used. The file locations are stored under the column id in the CSV file.")
public class ZipFileLoggerWriter extends ConQATProcessorBase {

	/** The logger. */
	private ZipFileLogger logger = new ZipFileLogger();

	/** Set output file. */
	@AConQATParameter(name = "file", minOccurrences = 0, maxOccurrences = 1, description = "Output file parameter.")
	public void setFilename(
			@AConQATAttribute(name = "name", description = "Name of the output file.") String filename) {
		this.filename = filename;
	}

	/** The output filename */
	private String filename;

	/** {@inheritDoc} */
	@Override
	public void init(IConQATProcessorInfo processorInfo) {
		super.init(processorInfo);
		processorInfo.registerShutdownHook(new IShutdownHook() {
			// We cannot make a ConQAT block out of this (and thus reuse
			// CSVWriter) since we are in a shutdown hook.
			CSVWriter writer = new CSVWriter();

			@Override
			public void performShutdown() throws ConQATException {
				// In the writer, the id is written. This is exactly the String
				// we pass to SetNode's constructor
				SetNode<String> input = new SetNode<String>("");
				for (String fileName : logger.getFiles()) {
					input.addChild(new SetNode<String>(fileName));
				}
				writer.setInput(input);
				writer.setFilename(filename);
				writer.process();
			}
		}, false);
	}

	/** {@inheritDoc} */
	@Override
	public ZipFileLogger process() {
		return logger;
	}

}
