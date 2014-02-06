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

import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * This processor performs regex-based replacements on a file. Input and output
 * file may be the same or different files.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8387A91B4FDAE86E9425EDD557DF0DFC
 */
@AConQATProcessor(description = "This processor performs regex-based replacements "
		+ "on a file. Input and output file may be the same or different files.")
public class FileRegexReplacer extends InputFileWriterBase<File> {

	/** List of replacements. */
	private PatternTransformationList regexes;

	/** Set regex replacements. */
	@AConQATParameter(name = "regex", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "List of regex replacements.")
	public void setRegexes(
			@AConQATAttribute(name = "list", description = "Regex list") PatternTransformationList regexes) {

		this.regexes = regexes;
	}

	/**
	 * Reads file, performs replacements and writes output file.
	 */
	@Override
	protected void writeToFile(File input, File output) throws IOException {
		String content = FileSystemUtils.readFile(input);
		String newContent = regexes.applyTransformation(content);
		FileSystemUtils.writeFile(output, newContent);
	}

}