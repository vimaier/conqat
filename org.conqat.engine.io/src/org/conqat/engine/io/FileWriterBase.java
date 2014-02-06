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
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Base class for file writers.
 * 
 * @author Florian Deissenboeck
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: BA0BEE0310876EA039AA5CD66769401D
 */
public abstract class FileWriterBase extends ConQATProcessorBase {

	/** Output file name. */
	private String filename;

	/** Set output file. */
	@AConQATParameter(name = "file", minOccurrences = 1, maxOccurrences = 1, description = "Output file parameter.")
	public void setFilename(
			@AConQATAttribute(name = "name", description = "Name of the output file.") String filename) {
		this.filename = filename;
	}

	/** {@inheritDoc} */
	@Override
	public final File process() throws ConQATException {

		File file = new File(filename);

		try {
			FileSystemUtils.ensureParentDirectoryExists(file);
		} catch (IOException e) {
			throw new ConQATException("Could not create directory for " + file
					+ " (" + e.getMessage() + ").");
		}

		try {
			writeFile(file);
		} catch (IOException e) {
			throw new ConQATException("Could not write to file " + file + " ("
					+ e.getMessage() + ").");
		}

		return file;
	}

	/** Write to the file. */
	protected abstract void writeFile(File file) throws ConQATException,
			IOException;

}