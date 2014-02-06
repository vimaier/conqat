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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This processor is used to produce a ZIP file from a set of files. This could
 * be useful to collect the results of multiple other writers into a single
 * file.
 * 
 * @author Florian Deissenboeck
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DE7812F5DBBE121D6E58CF2D9793199D
 */
@AConQATProcessor(description = ""
		+ "This processor is used to produce a ZIP file from a set of files. This could be useful to "
		+ "collect the results of multiple other writers into a single file.")
public class ZipFileCreator extends FileWriterBase {

	/** Files to include. */
	private final ArrayList<File> files = new ArrayList<File>();

	/** Add a result file. */
	@AConQATParameter(name = "append", description = "Append a file to the ZIP file.")
	public void addResult(
			@AConQATAttribute(name = "file", description = "The file to append.") File file) {
		files.add(file);
	}

	/** {@inheritDoc} */
	@Override
	protected void writeFile(File zipFile) throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

		byte[] buf = new byte[1024];
		for (File file : files) {
			FileInputStream in = new FileInputStream(file);

			// Add ZIP entry to output stream.
			out.putNextEntry(new ZipEntry(file.getPath()));

			// Transfer bytes from the file to the ZIP file
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			// Complete the entry
			out.closeEntry();
			in.close();
		}
		out.close();
	}
}